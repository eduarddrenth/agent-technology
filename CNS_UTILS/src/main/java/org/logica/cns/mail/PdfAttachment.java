package org.logica.cns.mail;

/**
     Each instance of this subclass gets mimetype appliaction/pdf.

     @author Eduard Drenth
     @version 1.0
*/

public class PdfAttachment extends Attachment {
   public PdfAttachment() {
      content = "";
      name = "noname";
      mimeType = "application/pdf";
   }
}
