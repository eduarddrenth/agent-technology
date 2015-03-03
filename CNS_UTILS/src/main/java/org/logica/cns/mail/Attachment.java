package org.logica.cns.mail;

import java.io.File;

/**
     This class represents a mailattachment.
     Each instance has a name, a mimeType and a content which are never null.

     @author Eduard Drenth
     @version 1.0
*/

public class Attachment {
   protected Object content;
   protected String name;
   protected String mimeType;

   public Attachment() {
      content = "";
      name = "noname";
      mimeType = "application/unknown";
   }

   public void set (File file) {
      if (file != null) {
         name = file.getName();
         content = file;
      }
   }

   public void set (String content, String name) {
      if (name != null) this.name = name;
      if (content != null) this.content = content;
   }

   public void set (byte[] content, String name) {
      if (name != null) this.name = name;
      if (content != null && content.length > 0) this.content = content;
   }
   
   public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
   }

}
