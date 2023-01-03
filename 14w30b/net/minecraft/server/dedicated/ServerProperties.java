package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class ServerProperties {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties properties = new Properties();
   private final File file;

   public ServerProperties(File file) {
      this.file = file;
      if (file.exists()) {
         FileInputStream var2 = null;

         try {
            var2 = new FileInputStream(file);
            this.properties.load(var2);
         } catch (Exception var12) {
            LOGGER.warn("Failed to load " + file, var12);
            this.generate();
         } finally {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (IOException var11) {
               }
            }
         }
      } else {
         LOGGER.warn(file + " does not exist");
         this.generate();
      }
   }

   public void generate() {
      LOGGER.info("Generating new properties file");
      this.save();
   }

   public void save() {
      FileOutputStream var1 = null;

      try {
         var1 = new FileOutputStream(this.file);
         this.properties.store(var1, "Minecraft server properties");
      } catch (Exception var11) {
         LOGGER.warn("Failed to save " + this.file, var11);
         this.generate();
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var10) {
            }
         }
      }
   }

   public File getFile() {
      return this.file;
   }

   public String getOrDefault(String key, String defaultValue) {
      if (!this.properties.containsKey(key)) {
         this.properties.setProperty(key, defaultValue);
         this.save();
         this.save();
      }

      return this.properties.getProperty(key, defaultValue);
   }

   public int getOrDefault(String key, int defaultValue) {
      try {
         return Integer.parseInt(this.getOrDefault(key, "" + defaultValue));
      } catch (Exception var4) {
         this.properties.setProperty(key, "" + defaultValue);
         this.save();
         return defaultValue;
      }
   }

   public boolean getOrDefault(String key, boolean defaultValue) {
      try {
         return Boolean.parseBoolean(this.getOrDefault(key, "" + defaultValue));
      } catch (Exception var4) {
         this.properties.setProperty(key, "" + defaultValue);
         this.save();
         return defaultValue;
      }
   }

   public void set(String key, Object value) {
      this.properties.setProperty(key, "" + value);
   }
}
