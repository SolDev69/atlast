package net.minecraft.server.integrated;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LanServerPinger extends Thread {
   private static final AtomicInteger threadId = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private final String motd;
   private final DatagramSocket socket;
   private boolean isRunning = true;
   private final String port;

   public LanServerPinger(String motd, String port) {
      super("LanServerPinger #" + threadId.incrementAndGet());
      this.motd = motd;
      this.port = port;
      this.setDaemon(true);
      this.socket = new DatagramSocket();
   }

   @Override
   public void run() {
      String var1 = getMetadata(this.motd, this.port);
      byte[] var2 = var1.getBytes();

      while(!this.isInterrupted() && this.isRunning) {
         try {
            InetAddress var3 = InetAddress.getByName("224.0.2.60");
            DatagramPacket var4 = new DatagramPacket(var2, var2.length, var3, 4445);
            this.socket.send(var4);
         } catch (IOException var6) {
            LOGGER.warn("LanServerPinger: " + var6.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException var5) {
         }
      }
   }

   @Override
   public void interrupt() {
      super.interrupt();
      this.isRunning = false;
   }

   public static String getMetadata(String motd, String port) {
      return "[MOTD]" + motd + "[/MOTD][AD]" + port + "[/AD]";
   }

   public static String parseMotd(String metadata) {
      int var1 = metadata.indexOf("[MOTD]");
      if (var1 < 0) {
         return "missing no";
      } else {
         int var2 = metadata.indexOf("[/MOTD]", var1 + "[MOTD]".length());
         return var2 < var1 ? "missing no" : metadata.substring(var1 + "[MOTD]".length(), var2);
      }
   }

   public static String parsePort(String metadata) {
      int var1 = metadata.indexOf("[/MOTD]");
      if (var1 < 0) {
         return null;
      } else {
         int var2 = metadata.indexOf("[/MOTD]", var1 + "[/MOTD]".length());
         if (var2 >= 0) {
            return null;
         } else {
            int var3 = metadata.indexOf("[AD]", var1 + "[/MOTD]".length());
            if (var3 < 0) {
               return null;
            } else {
               int var4 = metadata.indexOf("[/AD]", var3 + "[AD]".length());
               return var4 < var3 ? null : metadata.substring(var3 + "[AD]".length(), var4);
            }
         }
      }
   }
}
