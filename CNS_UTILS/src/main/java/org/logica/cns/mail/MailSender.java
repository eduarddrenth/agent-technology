package org.logica.cns.mail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
This class is for sending mail through the smtp protocol.
It can be used for sending with or without authentication,
for sending with or without attachments.


Example use:
<pre><i>

try {

MailSender m = new MailSender("localhost");

Attachment att = new Attachment();
att.set(new File("test.java"));
m.addAttachment(att);

att = new Attachment();
att.set("test.java","naam");
m.addAttachment(att);

m.send("toaddress","fromaddress","subject","messagebody");

// m.send(toaddresses[],ccaddresses[],bccaddresses[],"fromaddress","subject","messagebody","priority");

} catch (MessagingException ex) {System.err.println(ex.getMessage()); System.exit(1);}

</i></pre>

@author Eduard Drenth
@version 1.0
 */
public class MailSender {

    private static Log log = LogFactory.getLog(MailSender.class);
    private MimeMessage msg;
    private Collection<Attachment> attachments = new ArrayList(1);
    private String user;
    private String password;
    private Session session;
    private Properties props;

    /** Inner class used when authentication is required by the server
     */
    class MailAuthenticator extends Authenticator {

        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(user, password);
        }
    }

    /** Returns the message used by the MailSender
     */
    public MimeMessage getRawMessage() {
        return msg;
    }

    /** Constructs a MailSender which will try to send mail through host.
     */
    public MailSender(String host)
            throws MessagingException {
        this(host, 25, "", "");
    }

    /** Bottleneck constructor, constructs a MailSender which will try to send mail through host:port, when
    authentication is required by the server user and password are used. Initializes Properties (mail.smtp.host,
    mail.smtp.port, mail.transport.protocol and mail.mime.charset (ISO-8859-1)) and a mail session with an authenticator
     */
    public MailSender(String host, int port, String user, String password)
            throws MessagingException {
        // Setup the session, using the smtpHost name:
        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.mime.charset", "ISO-8859-1");

        this.user = user;
        this.password = password;

        if (user == null || user.isEmpty()) {
            session = Session.getInstance(props, null);
        } else {
            props.put("mail.smtp.auth", String.valueOf(true));
            Authenticator auth = new MailAuthenticator();
            session = Session.getInstance(props, auth);
        }
        // Create the message (MimeMessage) object:
        msg = new MimeMessage(session);

    }

    public static void main(String[] args) {
        try {

            final String USAGE = "Usage: MailSender -h <mailhost> -s <subject> -f <from> [ -t <to> ]... " +
                    "[ -c <cc> ].. [ -b <ccc> ].. [ -a <file-to-attach> <mime-type> ]..";

            if (args == null || args.length < 8) {
                throw new Exception(USAGE);
            }

            String host = "unknown", subject = "unknown", from = "unknown";
            List toList = new LinkedList();
            List ccList = new LinkedList();
            List bccList = new LinkedList();
            List attachmentList = new LinkedList();

            int nextArg = 666;
            final String SWITCHES[] = {"-h", "-s", "-f", "-t", "-c", "-b", "-a"};
            for (int argNum = 0; argNum < args.length; argNum = nextArg) {
                String switchName = args[argNum];
                String arg1 = null, arg2 = null;
                if ((argNum + 1) < args.length) {
                    arg1 = args[argNum + 1];
                } else {
                    throw new Exception(USAGE);
                }
                if ((argNum + 2) < args.length) {
                    arg2 = args[argNum + 2]; // arg2 possibly remains null
                }
                // Lookup the switch in the array:
                int sw;
                for (sw = 0; sw < SWITCHES.length; sw++) {
                    if (SWITCHES[sw].equals(switchName)) {
                        break;
                    }
                }
                switch (sw) {
                    case 0:
                        nextArg = argNum + 2; // -h
                        host = arg1;
                        break;
                    case 1:
                        nextArg = argNum + 2; // -s
                        subject = arg1;
                        break;
                    case 2:
                        nextArg = argNum + 2; // -f
                        from = arg1;
                        break;
                    case 3:
                        nextArg = argNum + 2; // -t
                        toList.add(arg1);
                        break;
                    case 4:
                        nextArg = argNum + 2; // -c
                        ccList.add(arg1);
                        break;
                    case 5:
                        nextArg = argNum + 2; // -b
                        bccList.add(arg1);
                        break;
                    case 6:
                        nextArg = argNum + 3; // -a
                        if (arg2 == null) {
                            throw new Exception(USAGE);
                        }
                        Attachment attachment = new Attachment();
                        attachment.set(new File(arg1));
                        attachment.setMimeType(arg2);
                        attachmentList.add(attachment);
                        break;
                    default:
                        throw new Exception(USAGE);
                }
            }

            // collect data

            int i = -1;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            while ((i = System.in.read()) != -1) {
                bo.write(i);
            }
            bo.close();

            MailSender m = new MailSender(host);
            Iterator atts = attachmentList.iterator();
            while (atts.hasNext()) {
                m.addAttachment((Attachment) atts.next());
            }
            String to[] = new String[toList.size()];
            for (int j = 0; j < toList.size(); j++) {
                to[j] = (String) toList.get(j);
            }
            String cc[] = new String[ccList.size()];
            for (int j = 0; j < ccList.size(); j++) {
                cc[j] = (String) ccList.get(j);
            }
            String bcc[] = new String[bccList.size()];
            for (int j = 0; j < bccList.size(); j++) {
                bcc[j] = (String) bccList.get(j);
            }

            m.send(to, cc, bcc, from, subject, new String(bo.toByteArray()), 3);

        } catch (Exception ex) {
            log.error("error sending mail", ex);
            System.exit(1);
        }
    }

    public void send(String to, String from, String subject, String message)
            throws MessagingException {
        String[] too = {to};
        String[] cc = {};
        String[] bcc = {};
        send(too, cc, bcc, from, subject, message, 3);
    }

    public void forward(String to, String from, String body, String subjectPrefix, Message message)
            throws MessagingException {
        String[] too = {to};
        String[] cc = {};
        String[] bcc = {};
        if (body == null || "".equals(body)) {
            forward(too, cc, bcc, from, subjectPrefix, message, 3);
        } else {
            forward(too, cc, bcc, from, body, subjectPrefix, message, 3);
        }
    }

    public void forward(String to, String from, String subjectPrefix, Message message)
            throws MessagingException {
        String[] too = {to};
        String[] cc = {};
        String[] bcc = {};
        forward(too, cc, bcc, from, subjectPrefix, message, 3);
    }

    /** bottleneck function, forwards a message by sending it as an attachment with a mail.
     */
    public void forward(String[] to, String[] cc, String[] bcc, String from, String body, String subjectPrefix,
            Message message, int priority)
            throws MessagingException {

        msg.setFrom(new InternetAddress(from));

        // Convert the to address strings to an array of InternetAddress objects:
        InternetAddress[] toAddresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            toAddresses[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);

        // Convert the cc address strings to an array of InternetAddress objects:
        InternetAddress[] ccAddresses = new InternetAddress[cc.length];
        for (int i = 0; i < cc.length; i++) {
            ccAddresses[i] = new InternetAddress(cc[i]);
        }
        msg.setRecipients(Message.RecipientType.CC, ccAddresses);

        // Convert the bcc address strings to an array of InternetAddress objects:
        InternetAddress[] bccAddresses = new InternetAddress[bcc.length];
        for (int i = 0; i < bcc.length; i++) {
            bccAddresses[i] = new InternetAddress(bcc[i]);
        }
        msg.setRecipients(Message.RecipientType.BCC, bccAddresses);

        // Set other headers:
        msg.setSubject(subjectPrefix + message.getSubject(), "ISO-8859-1");

        switch (priority) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                msg.setHeader("X-Priority", String.valueOf(priority));
                break;
            default:
                msg.setHeader("X-Priority", "3");
        }

        // Create your new message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body, "ISO-8859-1");

        // Create a multi-part to combine the parts
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Create and fill part for the forwarded content
        messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(message.getDataHandler());

        // Add part to multi part
        multipart.addBodyPart(messageBodyPart);

        // Associate multi-part with message
        msg.setContent(multipart);

        msg.setSentDate(new Date());

        // send the message
        Transport.send(msg);
    }

    /** bottleneck function, forwards a message by sending an exact copy of the original message.
     */
    public void forward(String[] to, String[] cc, String[] bcc, String from, String subjectPrefix,
            Message message, int priority)
            throws MessagingException {

        msg.setFrom(new InternetAddress(from));

        // Convert the to address strings to an array of InternetAddress objects:
        InternetAddress[] toAddresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            toAddresses[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);

        // Convert the cc address strings to an array of InternetAddress objects:
        InternetAddress[] ccAddresses = new InternetAddress[cc.length];
        for (int i = 0; i < cc.length; i++) {
            ccAddresses[i] = new InternetAddress(cc[i]);
        }
        msg.setRecipients(Message.RecipientType.CC, ccAddresses);

        // Convert the bcc address strings to an array of InternetAddress objects:
        InternetAddress[] bccAddresses = new InternetAddress[bcc.length];
        for (int i = 0; i < bcc.length; i++) {
            bccAddresses[i] = new InternetAddress(bcc[i]);
        }
        msg.setRecipients(Message.RecipientType.BCC, bccAddresses);

        // Set other headers:
        msg.setSubject(subjectPrefix + message.getSubject(), "ISO-8859-1");

        switch (priority) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                msg.setHeader("X-Priority", String.valueOf(priority));
                break;
            default:
                msg.setHeader("X-Priority", "3");
        }

        // set the content of the message to the content of the original message
        msg.setDataHandler(message.getDataHandler());

        msg.setSentDate(new Date());

        // send the message
        Transport.send(msg);
    }

    public void send(String[] to, String[] cc, String[] bcc, String from, String subject,
            String message, int priority)
            throws MessagingException {
        msg.setFrom(new InternetAddress(from));

        // Convert the to address strings to an array of InternetAddress objects:
        InternetAddress[] toAddresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            toAddresses[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, toAddresses);

        if (cc!=null&&cc.length>0) {
            // Convert the cc address strings to an array of InternetAddress objects:
            InternetAddress[] ccAddresses = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++) {
                ccAddresses[i] = new InternetAddress(cc[i]);
            }
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
        }

        if (bcc!=null&&bcc.length>0) {
            // Convert the bcc address strings to an array of InternetAddress objects:
            InternetAddress[] bccAddresses = new InternetAddress[bcc.length];
            for (int i = 0; i < bcc.length; i++) {
                bccAddresses[i] = new InternetAddress(bcc[i]);
            }
            msg.setRecipients(Message.RecipientType.BCC, bccAddresses);
        }

        // Set other headers:
        msg.setSubject(subject, "ISO-8859-1");

        switch (priority) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                msg.setHeader("X-Priority", String.valueOf(priority));
                break;
            default:
                msg.setHeader("X-Priority", "3");
        }

        // Create and fill the main message part
        MimeBodyPart mainPart = new MimeBodyPart();
        mainPart.setText(message, "ISO-8859-1");


        if (attachments != null && attachments.size() > 0) {

            // create a Multipart and add the main part:
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mainPart);

            Iterator<Attachment> it = attachments.iterator();
            while (it.hasNext()) {
                Attachment att = it.next();

                if (att.content != null) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();

                    // now we see what the content of the attachment is: File, String or byte[]
                    DataSource source = null;
                    if (att.content instanceof File) {
                        source = new FileDataSource((File) att.content);
                    }
                    if (att.content instanceof String) {
                        source = new ByteArrayDataSource(((String) att.content).getBytes(), att.mimeType);
                    }
                    if (att.content instanceof byte[]) {
                        source = new ByteArrayDataSource((byte[]) att.content, att.mimeType);
                    }

                    if (source == null) {
                        continue;
                    }

                    attachmentPart.setDataHandler(new DataHandler(source));
                    if (att instanceof HtmlAttachment || att instanceof TextAttachment) {
                        attachmentPart.setDisposition(Part.INLINE);
                    } else {
                        attachmentPart.setDisposition(Part.ATTACHMENT);
                    }
                    attachmentPart.setFileName(att.name);
                    multipart.addBodyPart(attachmentPart);
                }


            }

            msg.setContent(multipart);
        } else {
            // add the mainPart to the message
            try {
                msg.setText((String) mainPart.getContent(), "ISO-8859-1");
            } catch (IOException ex) {
                msg.setText("");
            }
        }

        msg.setSentDate(new Date());


        // send the message
        Transport.send(msg);

    }

    public void addAttachment(Attachment att) {
        attachments.add(att);
    }

    public void removeAttachment(Attachment att) {
        attachments.remove(att);
    }
}
