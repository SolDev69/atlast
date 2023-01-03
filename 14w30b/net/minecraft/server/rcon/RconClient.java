package net.minecraft.server.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import net.minecraft.server.dedicated.IDedicatedServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class RconClient extends RconBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean authenticated;
   private Socket socket;
   private byte[] packetBuffer = new byte[1460];
   private String password;

   RconClient(IDedicatedServer server, Socket socket) {
      super(server, "RCON Client");
      this.socket = socket;

      try {
         this.socket.setSoTimeout(0);
      } catch (Exception var4) {
         this.running = false;
      }

      this.password = server.getPropertyOrDefault("rcon.password", "");
      this.info("Rcon connection from: " + socket.getInetAddress());
   }

   @Override
   public void run() {
      try {
         try {
            while(this.running) {
               BufferedInputStream var1 = new BufferedInputStream(this.socket.getInputStream());
               int var2 = var1.read(this.packetBuffer, 0, 1460);
               if (10 > var2) {
                  return;
               }

               int var3 = 0;
               int var4 = BufferHelper.getIntLE(this.packetBuffer, 0, var2);
               if (var4 != var2 - 4) {
                  return;
               }

               var3 += 4;
               int var5 = BufferHelper.getIntLE(this.packetBuffer, var3, var2);
               var3 += 4;
               int var6 = BufferHelper.getIntLE(this.packetBuffer, var3);
               var3 += 4;
               switch(var6) {
                  case 2:
                     if (this.authenticated) {
                        String var8 = BufferHelper.getString(this.packetBuffer, var3, var2);

                        try {
                           this.execute(var5, this.server.executeRconCommand(var8));
                        } catch (Exception var16) {
                           this.execute(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                        }
                        break;
                     }

                     this.executeUnknown();
                     break;
                  case 3:
                     String var7 = BufferHelper.getString(this.packetBuffer, var3, var2);
                     var3 += var7.length();
                     if (0 != var7.length() && var7.equals(this.password)) {
                        this.authenticated = true;
                        this.execute(var5, 2, "");
                        break;
                     }

                     this.authenticated = false;
                     this.executeUnknown();
                     break;
                  default:
                     this.execute(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
               }
            }

            return;
         } catch (SocketTimeoutException var17) {
         } catch (IOException var18) {
         } catch (Exception var19) {
            LOGGER.error("Exception whilst parsing RCON input", var19);
         }
      } finally {
         this.close();
      }
   }

   private void execute(int stream1, int stream2, String text) {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] var6 = text.getBytes("UTF-8");
      var5.writeInt(Integer.reverseBytes(var6.length + 10));
      var5.writeInt(Integer.reverseBytes(stream1));
      var5.writeInt(Integer.reverseBytes(stream2));
      var5.write(var6);
      var5.write(0);
      var5.write(0);
      this.socket.getOutputStream().write(var4.toByteArray());
   }

   private void executeUnknown() {
      this.execute(-1, 2, "");
   }

   private void execute(int id, String name) {
      int var3 = name.length();

      do {
         int var4 = 4096 <= var3 ? 4096 : var3;
         this.execute(id, 0, name.substring(0, var4));
         name = name.substring(var4);
         var3 = name.length();
      } while(0 != var3);
   }

   private void close() {
      if (null != this.socket) {
         try {
            this.socket.close();
         } catch (IOException var2) {
            this.warn("IO: " + var2.getMessage());
         }

         this.socket = null;
      }
   }
}
