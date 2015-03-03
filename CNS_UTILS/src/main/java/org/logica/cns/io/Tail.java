package org.logica.cns.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class to tail a file. Basic usage:
 * <pre>
- Tail t = new Tail(file);
- t.addObserver(<your observer>)
- t.start();
in your public void update(Observable o, Object arg) {} method cast the arg parameter to a {@link Result}, this
contains the newly read lines and the path of the file read from. When the arg parameter is null this indicates an error during the tail.
 * </pre>
 * @author Eduard Drenth: Logica, 16-nov-2009
 * 
 */
public class Tail extends Observable implements Closeable {

    private int maxLinesInMemory = MAXLINESINMEMORY;
    public static final int MAXLINESINMEMORY = 500;

    public int getMaxLinesInMemory() {
        return maxLinesInMemory;
    }

    public void setMaxLinesInMemory(int maxLinesInMemory) {
        if (maxLinesInMemory > 0) {
            this.maxLinesInMemory = maxLinesInMemory;
        }
    }

    public static class Result {

        private String path;
        private Collection<String> c;

        private Result(String path, Collection<String> c) {
            this.c = c;
            this.path = path;
        }

        public Collection<String> getLines() {
            return c;
        }

        public String getPath() {
            return path;
        }
    }

    private static class MyRunner implements Runnable {

        private Tail t = null;

        MyRunner(Tail t) {
            this.t = t;
        }

        @Override
        public void run() {
            t.doIt();
        }
    }
    private File f = null;
    private Reader fileReader = null;
    private BufferedReader input = null;
    private boolean reading = false;
    private boolean pauze = false;
    private Thread myThread = null;
    private List<String> queue = new LinkedList<String>();
    private int linesRead = 0;
    private int loopsBeforeNotify = 5;

    public String getFilePath() {
        return f.getPath();
    }

    /**
     * Observers will be notified after this number of read attempts, defaults to 5.
     * @return
     */
    public int getLoopsBeforeNotify() {
        return loopsBeforeNotify;
    }

    /**
     * Change the number of read attempts after which {@link Observer}s will be notified
     * @param loopsBeforeNotify
     */
    public void setLoopsBeforeNotify(int loopsBeforeNotify) {
        if (loopsBeforeNotify > 0) {
            this.loopsBeforeNotify = loopsBeforeNotify;
        }
    }
    private static Log log = LogFactory.getLog(Tail.class);

    /**
     * Construct a new Tail Object that is able to tail a file and notify Observers.
     * @param f
     * @throws IOException when the file cannot be tailed
     */
    public Tail(File f) throws IOException {
        if (null != f && f.isFile() && f.canRead()) {
            this.f = f;
        } else {
            throw new IOException("file " + ((f==null)?"unknown":f.getPath()) + " cannot be tailed");
        }
    }

    /**
     * 
     * @see Tail(File)
     */
    public Tail(String path) throws IOException {
        this(new File(path));
    }

    /**
     * Start tailing the file, only the first call that generates no FileNotFoundException has effect.
     * @throws FileNotFoundException when the file to be tailed is not found
     */
    public void start() throws FileNotFoundException {
        if (null == myThread) {
            fileReader = new FileReader(f);
            input = new BufferedReader(fileReader);
            myThread = new Thread(new MyRunner(this));
            linesRead = 0;
            reading = true;
            pauze = false;
            myThread.start();
        }
    }

    /**
     * stops reading and closes the stream
     * @throws IOException
     */
    public void stop() throws IOException, InterruptedException {
        if (isReading()) {
            reading = false;
            myThread.interrupt();
            myThread.join();
            close();
        }
    }

    public void pauze() {
        pauze = true;
    }

    public void resume() {
        pauze = false;
    }

    /**
     * method used internally to publish lines read to the Observers registerd
     * @return A Collection with lines read from the source sofar
     * @throws InterruptedException
     */
    private Collection<String> getReadLines() throws InterruptedException {
        if (queue.size() > 0) {
            List<String> c = new LinkedList<String>(queue);
            queue.clear();
            return c;
        }
        return null;
    }

    /**
     * continuously try to read from the file, throws IOException if the file becomes unavailable during the tail.
     */
    private void doIt() {
        String line = null;
        int loop = 0;

        try {
            while (reading) {
                if (!pauze) {
                    loop++;
                    if (loop > loopsBeforeNotify && hasChanged() && countObservers() > 0) {
                        notifyObservers(new Result(f.getPath(), getReadLines()));
                        loop = 0;
                    }
                    if (queue.size() < maxLinesInMemory) {
                        if ((line = input.readLine()) != null) {
                            queue.add(line);
                            setChanged();
                            linesRead++;
                            continue;
                        } else if (!f.canRead()) {
                            throw new IOException("cannot read " + f.getPath());
                        } else if (log.isDebugEnabled()) {
                            log.debug("continue tailing (possibly never appended) " + f.getPath());
                        }
                    } else {
                        log.warn("more than " + maxLinesInMemory + " in buffer (" + queue.size() + ")");
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException x) {
                }
            }
        } catch (InterruptedException ex) {
            notifyObservers();
            log.error("error reading " + f.getPath(), ex);
        } catch (IOException iOException) {
            notifyObservers();
            log.error("error reading " + f.getPath(), iOException);
        }
    }

    /**
     * can be used for testing
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Tail t = new Tail(new File(args[0]));
        t.start();

        t.addObserver(new Handle());
    }

    /**
     * how many lines were read from the file
     * @return
     */
    public int getLinesRead() {
        return linesRead;
    }

    /**
     * is the tail still active
     * @return
     */
    public boolean isReading() {
        return reading && !pauze && null != myThread && myThread.isAlive();
    }

    /**
     * closes the underlying Redaer
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
/**
 * for
 * @author eduard
 */
class Handle implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        System.out.println((Collection<String>) arg);
    }
}
