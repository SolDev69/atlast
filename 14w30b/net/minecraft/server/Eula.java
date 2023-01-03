package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class Eula {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File file;
   private final boolean accepted;

   public Eula(File file) {
      this.file = file;
      this.accepted = this.load(file);
   }

   private boolean load(File file) {
      FileInputStream var2 = null;
      boolean var3 = false;

      try {
         Properties var4 = new Properties();
         var2 = new FileInputStream(file);
         var4.load(var2);
         var3 = Boolean.parseBoolean(var4.getProperty("eula", "false"));
      } catch (Exception var8) {
         LOGGER.warn("Failed to load " + file);
         this.write();
      } finally {
         IOUtils.closeQuietly(var2);
      }

      return var3;
   }

   public boolean isAccepted() {
      return this.accepted;
   }

   public void write() {
      FileOutputStream var1 = null;

      try {
         Properties var2 = new Properties();
         var1 = new FileOutputStream(this.file);
         var2.setProperty("eula", "false");
         var2.store(
            var1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
         );
      } catch (Exception var6) {
         LOGGER.warn("Failed to save " + this.file, var6);
      } finally {
         IOUtils.closeQuietly(var1);
      }
   }
}
