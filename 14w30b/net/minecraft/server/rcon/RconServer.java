package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.IDedicatedServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class RconServer extends RconBase {
   private int port;
   private int serverPort;
   private String hostname;
   private ServerSocket listener;
   private String password;
   private Map clientBySocket;

   public RconServer(IDedicatedServer server) {
      super(server, "RCON Listener");
      this.port = server.getPropertyOrDefault("rcon.port", 0);
      this.password = server.getPropertyOrDefault("rcon.password", "");
      this.hostname = server.getIp();
      this.serverPort = server.getPort();
      if (0 == this.port) {
         this.port = this.serverPort + 10;
         this.info("Setting default rcon port to " + this.port);
         server.setProperty("rcon.port", this.port);
         if (0 == this.password.length()) {
            server.setProperty("rcon.password", "");
         }

         server.saveProperties();
      }

      if (0 == this.hostname.length()) {
         this.hostname = "0.0.0.0";
      }

      this.initMap();
      this.listener = null;
   }

   private void initMap() {
      this.clientBySocket = Maps.newHashMap();
   }

   private void removeStoppedClients() {
      Iterator var1 = this.clientBySocket.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!((RconClient)var2.getValue()).isRunning()) {
            var1.remove();
         }
      }
   }

   @Override
   public void run() {
      this.info("RCON running on " + this.hostname + ":" + this.port);

      try {
         while(this.running) {
            try {
               Socket var1 = this.listener.accept();
               var1.setSoTimeout(500);
               RconClient var2 = new RconClient(this.server, var1);
               var2.start();
               this.clientBySocket.put(var1.getRemoteSocketAddress(), var2);
               this.removeStoppedClients();
            } catch (SocketTimeoutException var7) {
               this.removeStoppedClients();
            } catch (IOException var8) {
               if (this.running) {
                  this.info("IO: " + var8.getMessage());
               }
            }
         }
      } finally {
         this.closeSocket(this.listener);
      }
   }

   @Override
   public void start() {
      if (0 == this.password.length()) {
         this.warn("No rcon password set in '" + this.server.getPropertiesFilePath() + "', rcon disabled!");
      } else if (0 >= this.port || 65535 < this.port) {
         this.warn("Invalid rcon port " + this.port + " found in '" + this.server.getPropertiesFilePath() + "', rcon disabled!");
      } else if (!this.running) {
         try {
            this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
            this.listener.setSoTimeout(500);
            super.start();
         } catch (IOException var2) {
            this.warn("Unable to initialise rcon on " + this.hostname + ":" + this.port + " : " + var2.getMessage());
         }
      }
   }
}
