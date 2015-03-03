package org.logica.cns.mail;

/**
     Each instance of this subclass gets mimetype text/rtf.

     @author Eduard Drenth
     @version 1.0
*/

public class RtfAttachment extends Attachment {
   public RtfAttachment() {
      content = "";
      name = "noname";
      mimeType = "text/rtf";
   }
}
