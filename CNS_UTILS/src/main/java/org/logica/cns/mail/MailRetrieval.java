package org.logica.cns.mail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Enumeration;
import javax.mail.Header;
import java.io.FileNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
This class is for retrieving mail through the pop3 or imap protocol.
It wil initialize a Folder from which the messages can be read.


Example use:
<pre>
<i>

try {
MailRetrieval m = new MailRetrieval("pop3.comp","user","password");

// MailRetrieval m = new MailRetrieval("pop3.comp", "imap", 345, "MIJNFOLDER" ,"user","password");

Message message[] = m.getMessages(Folder.READ_WRITE);
for (int i = 0; i &lt; message.length; i++) {
System.out.println(message[i].getFrom()[0]+":"+message[i].getSubject());
if (message[i].getSubject().equals("test")) {
Message[] mes = { message[i] };
m.remove(mes); // messages count from 1 not from 0!!
}
}
m.close();

} catch (Exception ex) {System.err.println(ex); System.exit(1);}

</i></pre>

@author Eduard Drenth
@version 1.0
 */
public class MailRetrieval {

    private static final Log log = LogFactory.getLog(MailRetrieval.class);
    private Folder folder;
    private Store store;
    private String user;
    private String password;

    /**
     *  constructor will try to connect to a mailserver and get the requested folder. When the connection is successfull, you
     *  can use {@link #getMessages getMessages(Folder.READ_WRITE)} or {@link #getMessages(int) getMessages(Folder.READ_ONLY)}
     *  to get a handle to the messages in th folder. <b>Always call {@link #close() close()}, it will close and commit
     *  changes {@link #remove(int, int) see remove()}</b>
     *
     * @param  host                         the name or ipaddress of the mailserver.
     * @param  proto                        imap or pop3.
     * @param  port                         the portnumber to connect to.
     * @param  folder                       the name of the folder to get (INBOX for pop3).
     * @param  user                         the name of the account.
     * @param  password                     the password for the account.
     * @exception  MessagingException
     * @exception  NoSuchProviderException
     */
    public MailRetrieval(String host, String proto, int port, String folder, String user, String password)
            throws MessagingException, NoSuchProviderException {
        Properties props = new Properties();

        props.put("mail." + proto + ".host", host);
        props.put("mail." + proto + ".port", String.valueOf(port));
        this.user = user;
        this.password = password;

        Authenticator auth = new MailAuthenticator();
        Session session = Session.getInstance(props, auth);

        store = session.getStore(proto);
        store.connect();
        this.folder = store.getFolder(folder);
    }

    /**
     * @param  host
     * @param  user
     * @param  password
     * @exception  MessagingException
     * @exception  NoSuchProviderException
     */
    public MailRetrieval(String host, String user, String password)
            throws MessagingException, NoSuchProviderException {
        this(host, "pop3", 110, "INBOX", user, password);
    }

    class MailAuthenticator extends Authenticator {

        /**
         * @return a PasswordAuthentication Object
         */
        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(user, password);
        }
    }

    public static void main(String[] args) {
        try {
            MailRetrieval mr = new MailRetrieval(args[0], args[1], args[2]);
            Message[] m = mr.getMessages(Folder.READ_WRITE);
            log.info(mr.digupPattern(m[0], Pattern.compile(args[3]), Integer.parseInt(args[4])));
            log.info(mr.getSenderAddressFromForwardedMessage(m[0]));
            mr.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * @return the String representation of the URL of the Message folder
     */
    public String getURL()
            throws MessagingException {
        if (folder == null) {
            return null;
        }
        return folder.getURLName().toString();
    }

    /**
     * @param  mode Folder.READ_ONLY, Folder.READ_WRITE
     * @return an array of Message Objects found or null
     */
    public Message[] getMessages(int mode)
            throws MessagingException {
        if (folder == null) {
            return null;
        }
        folder.open(mode);
        return folder.getMessages();
    }

    /** close the message folder and the store
     */
    public void close()
            throws MessagingException {
        if (folder != null && folder.isOpen()) {
            folder.close(true);
        }
        if (store != null) {
            store.close();
        }
    }

    protected void finalize() throws Throwable {
        close();
    }

    /**
     *  Be carefull using this one, since the numbers can change on the server, preferably use {@link #remove(Message[])
     *  remove(Message[])}.
     *
     * @param  i
     * @param  j
     */
    public void remove(int i, int j)
            throws MessagingException, IllegalStateException, IndexOutOfBoundsException {
        if (folder == null) {
            return;
        }
        folder.setFlags(i, j, new Flags(Flags.Flag.DELETED), true);
    }

    /**
     *  Be carefull using this one, since the numbers can change on the server, preferably use {@link #remove(Message[])
     *  remove(Message[])}.
     *
     * @param  i
     */
    public void remove(int i)
            throws MessagingException, IllegalStateException, IndexOutOfBoundsException {
        remove(i, i);
    }

    /**
     * @param  m the array of Message Objects to remove
     */
    public void remove(Message[] m)
            throws MessagingException, IllegalStateException {
        if (folder == null) {
            return;
        }
        folder.setFlags(m, new Flags(Flags.Flag.DELETED), true);
    }

    /**
     *  tries to store a Message in a directory, calls either {@link #store(Part, String, String) store(Part, String, String)}
     *  , where the filename will be the subject of the message, or {@link #storeMultipart(Multipart, String)
     *  storeMultipart(Multipart)}
     *
     * @param  m
     * @param  directory
     */
    public void storeMail(Message m, String directory)
            throws MessagingException, IOException, SecurityException {
        storeHeadersAndSubject(m, directory);
        if (m.isMimeType("multipart/*")) {
            storeMultipart((Multipart) m.getContent(), directory);
        } else {
            store(m, directory, m.getSubject());
        }
    }

    public void storeHeadersAndSubject(Message m, String directory)
            throws MessagingException, IOException, SecurityException {
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("cannot store in: " + directory);
        }

        int nr = -1;
        File mailInfo = new File(dir.getPath() + File.separator + "mailInfo.txt");

        while (mailInfo.exists()) {
            mailInfo = new File(mailInfo.getPath() + (++nr));
        }

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(mailInfo));

        Enumeration e = m.getAllHeaders();

        while (e.hasMoreElements()) {
            Header h = (Header) e.nextElement();
            out.write((h.getName() + ": " + h.getValue() + "\n").getBytes());
        }

        out.close();
    }

    /** Stores a Part using the filename contained in the message (if any..)
     * @param  p The Part to store
     * @param  directory The directory to store the part in
     */
    public void store(Part p, String directory)
            throws MessagingException, IOException {
        store(p, directory, p.getFileName());
    }

    /**Stores a Part using the filename supplied. No files are overwritten, a incrementing numerical suffix is used to garentee this ("messageBody0", "messageBody2", "messageBody3", ...)
     * @param  p The Part to store
     * @param  directory The directory to store the part in
     * @param  filename The file to store the Part in, if null "MessageBody" is used
     */
    public void store(Part p, String directory, String filename)
            throws MessagingException, IOException, SecurityException {

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("cannot store in: " + directory);
        }

        String disp = p.getDisposition();

        // many mailers don't include a Content-Disposition
        if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE)) {

            if (filename == null || "".equals(filename)) {
                filename = "MessageBody";
            }

            String sep = System.getProperty("file.separator");

            int nr = -1;
            File f = new File(directory + sep + filename);

            while (f.exists()) {
                f = new File(f.getPath() + (++nr));
            }

            OutputStream os = null;
            InputStream is = null;

            try {
                os = new BufferedOutputStream(new FileOutputStream(f));
            } catch (FileNotFoundException e) {
                f = new File(directory + sep + "MessageBody");
                os = null;
            }

            while (f.exists()) {
                f = new File(f.getPath() + (++nr));
            }

            try {

                if (os == null) {
                    os = new BufferedOutputStream(new FileOutputStream(f));
                }

                is = (p instanceof MimePart) ? p.getInputStream() : MimeUtility.decode(p.getInputStream(), ((MimePart) p).getEncoding());
                int c;

                while ((c = is.read()) != -1) {
                    os.write(c);
                }
            } finally {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            }
        }
    }

    /**
     *  Stores parts from a multipartmessage. Every part with a disposition null, attachment or inline will be stored in
     *  directory.
     *
     * @param  mp
     * @param  directory
     */
    public void storeMultipart(Multipart mp, String directory)
            throws MessagingException, IOException, SecurityException {

        int count = mp.getCount();

        for (int i = 0; i < count; i++) {
            Part p = mp.getBodyPart(i);

// als er een multipart in zit duiken we de recursie in

            if (p.isMimeType("multipart/*")) {
                storeMultipart((Multipart) p.getContent(), directory);
                continue;
            }

            store(p, directory);

        }
    }

    public Part findAttachment(String name, Multipart mp) throws MessagingException, IOException {
        int count = mp.getCount();
        Part p = null;

        for (int i = 0; i < count; i++) {
            p = mp.getBodyPart(i);

// als er een multipart in zit duiken we de recursie in

            if (p.isMimeType("multipart/*")) {
                p = findAttachment(name, (Multipart) p.getContent());
                if (p != null) {
                    return p;
                }
            } else {
                if (!name.equals(p.getFileName())) {
                    p = null;
                }
            }
        }
        return p;
    }

    public Part findAttachment(String name, Message m) throws MessagingException, IOException {
        if (m.isMimeType("multipart/*")) {
            return findAttachment(name, (Multipart) m.getContent());
        } else {
            return null;
        }
    }

    public byte[] getAttachment(String name, Message m) throws MessagingException, IOException {
        Part p = findAttachment(name, m);
        ByteArrayOutputStream os = null;
        InputStream is = null;
        if (p != null) {
            try {
                os = new ByteArrayOutputStream();

                is = (p instanceof MimePart) ? p.getInputStream() : MimeUtility.decode(p.getInputStream(), ((MimePart) p).getEncoding());
                int c;

                while ((c = is.read()) != -1) {
                    os.write(c);
                }
            } finally {
                os.close();
                is.close();
            }
        }
        return (os == null) ? null : os.toByteArray();
    }

    /**
     * @return a List of all Strings found by {@link #digupPattern( Message , Pattern, int) digupPattern()}
     */
    public synchronized List getFoundStrings() {
        while (foundStringsLocked) {
            try {
                wait(120000);
                return null;
            } catch (InterruptedException ex) {
            }
        }
        return stringsFound;
    }

    /**
     *
     */
    private synchronized void unLockFoundStrings() {
        foundStringsLocked = false;
        notifyAll();
    }
    private List stringsFound = new LinkedList();
    private boolean foundStringsLocked = false;

    /** This method returns the first String found in the Message. As a side effect a {@link #getFoundStrings() List}
     * is filled with all Strings matching the Pattern.
     * @param  m The message to search in
     * @param  pattern The Pattern to look for
     * @param  returnGroup The group in the Pattern to return (if 0 return the complete Pattern)
     * @return The first return group in the Pattern or an empty String
     */
    public synchronized String digupPattern(Message m, Pattern pattern, int returnGroup)
            throws MessagingException, IOException {
        foundStringsLocked = true;
        stringsFound.clear();
        String s = null;
        if (m.isMimeType("multipart/*")) {
            s = digupPattern((Multipart) m.getContent(), pattern, returnGroup);
        } else if (m.isMimeType("message/*")) {
            s = digupPattern((Message) m.getContent(), pattern, returnGroup);
        } else {
            if (m.getContent() instanceof Part) {
                s = digupPattern((Part) m.getContent(), pattern, returnGroup);
            } else {
                s = digupPattern(m.getContent().toString(), pattern, returnGroup);
            }
        }
        unLockFoundStrings();
        return (s == null) ? "" : s;
    }

    public String getSenderAddressFromForwardedMessage(Message m) throws IOException, MessagingException {
        String s = "";
        if (m.isMimeType("multipart/*")) {
            Multipart p = (Multipart) m.getContent();
            for (int i = 0; i < p.getCount(); i++) {
                if (p.getBodyPart(i).isMimeType("message/*")) {
                    s = ((Message) p.getBodyPart(i).getContent()).getFrom()[0].toString();
                    break;
                }
            }
        } else if (m.isMimeType("message/*")) {
            s = ((Message) m).getFrom()[0].toString();
        }
        return s;
    }

    /**
     * @param  p The Part to search in
     * @param  pattern The Pattern to look for
     * @param  returnGroup The group in the Pattern to return (if 0 return the complete Pattern)
     * @param  mimetype The Part must be of this mimetype, if null defaults to "text/*"
     * @return The return group in the Pattern or an empty String
     */
    public String digupPattern(Part p, Pattern pattern, int returnGroup, String mimetype)
            throws MessagingException, IOException {
        String type = mimetype;
        if (mimetype == null || mimetype.isEmpty()) {
            type = "text/*";
        }
        if (p.isMimeType(type)) {
            String content = p.getContent().toString();
            log.info("Searching " + pattern.pattern() + " in: " + content);
            return digupPattern(content, pattern, returnGroup);
        } else {
            log.info("Not searching for regex in: " + p.getContentType());
        }
        return "";
    }

    /**
     * @param  s The String to search in
     * @param  pattern The Pattern to look for
     * @param  returnGroup The group in the Pattern to return (if 0 return the complete Pattern)
     * @return The return group in the Pattern or an empty String
     */
    public String digupPattern(String s, Pattern pattern, int returnGroup)
            throws MessagingException, IOException {
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            stringsFound.add(m.group(returnGroup));
        }
        return (stringsFound.size() > 0) ? (String) stringsFound.get(0) : "";
    }

    /**
     * Override this method and let it call {@link #digupPattern( Part, Pattern, int, String) digupPattern()} with a
     * mimetype other than "text/*" to let all Pattern searches be performed on Parts of a different mimetype
     * @param  p The Part to search in
     * @param  pattern The Pattern to look for
     * @param  returnGroup The group in the Pattern to return (if 0 return the complete Pattern)
     * @return The return group in the Pattern or an empty String
     */
    public String digupPattern(Part p, Pattern pattern, int returnGroup)
            throws MessagingException, IOException {
        return digupPattern(p, pattern, returnGroup, "text/*");
    }

    /**
     *  Searches all bodyparts in a Multipart for a regex pattern.
     * @see #digupPattern(Part, Pattern, int) digupPattern()
     * @param  p
     * @param  pattern
     * @param  returnGroup
     * @return              group returnGroup in the pattern, or an empty string if nothing found.
     */
    public String digupPattern(Multipart p, Pattern pattern, int returnGroup)
            throws MessagingException, IOException {
        String s = "";
        String retval = "";
        for (int i = 0; i < p.getCount(); i++) {
            if (p.getBodyPart(i) == null) {
                continue;
            }
            if (p.getBodyPart(i).isMimeType("multipart/*")) {
                s = digupPattern((Multipart) p.getBodyPart(i).getContent(), pattern, returnGroup);
            } else if (p.getBodyPart(i).isMimeType("message/*")) {
                s = digupPattern((Message) p.getBodyPart(i).getContent(), pattern, returnGroup);
            } else {
                s = digupPattern((Part) p.getBodyPart(i), pattern, returnGroup);
            }
            if (retval.isEmpty()) {
                retval = s;
            }
        }
        return retval;
    }
}

