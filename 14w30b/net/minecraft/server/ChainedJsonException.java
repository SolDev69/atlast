package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class ChainedJsonException extends IOException {
   private final List entries = Lists.newArrayList();
   private final String message;

   public ChainedJsonException(String message) {
      this.entries.add(new ChainedJsonException.Entry());
      this.message = message;
   }

   public ChainedJsonException(String message, Throwable exception) {
      super(exception);
      this.entries.add(new ChainedJsonException.Entry());
      this.message = message;
   }

   public void prependJsonKey(String key) {
      ((ChainedJsonException.Entry)this.entries.get(0)).addJsonKey(key);
   }

   public void setFileNameAndFlush(String name) {
      ((ChainedJsonException.Entry)this.entries.get(0)).fileName = name;
      this.entries.add(0, new ChainedJsonException.Entry());
   }

   @Override
   public String getMessage() {
      return "Invalid " + ((ChainedJsonException.Entry)this.entries.get(this.entries.size() - 1)).toString() + ": " + this.message;
   }

   public static ChainedJsonException forException(Exception exception) {
      if (exception instanceof ChainedJsonException) {
         return (ChainedJsonException)exception;
      } else {
         String var1 = exception.getMessage();
         if (exception instanceof FileNotFoundException) {
            var1 = "File not found";
         }

         return new ChainedJsonException(var1, exception);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Entry {
      private String fileName = null;
      private final List jsonKeys = Lists.newArrayList();

      private Entry() {
      }

      private void addJsonKey(String key) {
         this.jsonKeys.add(0, key);
      }

      public String getJsonKeys() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      @Override
      public String toString() {
         if (this.fileName != null) {
            return !this.jsonKeys.isEmpty() ? this.fileName + " " + this.getJsonKeys() : this.fileName;
         } else {
            return !this.jsonKeys.isEmpty() ? "(Unknown file) " + this.getJsonKeys() : "(Unknown file)";
         }
      }
   }
}
