package org.logica.cns.mail;

/**
     Each instance of this subclass gets mimetype text/html.

     @author Eduard Drenth
     @version 1.0
*/


public class HtmlAttachment extends Attachment {
   public HtmlAttachment() {
      content = "";
      name = "noname";
      mimeType = "text/html";
   }
}
