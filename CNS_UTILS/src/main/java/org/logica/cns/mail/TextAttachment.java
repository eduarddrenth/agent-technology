package org.logica.cns.mail;

/**
     Each instance of this subclass gets mimetype text/plain.

     @author Eduard Drenth
     @version 1.0
*/

public class TextAttachment extends Attachment {
   public TextAttachment() {
      content = "";
      name = "noname";
      mimeType = "text/plain";
   }
}
