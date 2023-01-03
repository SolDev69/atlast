package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.handler.ClientLoginNetworkHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.HelloC2SPacket;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted = false;
   private Connection connection;

   public RealmsConnect(RealmsScreen realmsScreen) {
      this.onlineScreen = realmsScreen;
   }

   public void connect(String string, int i) {
      (new Thread("Realms-connect-task") {
            @Override
            public void run() {
               InetAddress var1 = null;
   
               try {
                  var1 = InetAddress.getByName(string);
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.this.connection = Connection.connect(var1, i);
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.this.connection
                     .setListener(
                        new ClientLoginNetworkHandler(RealmsConnect.this.connection, MinecraftClient.getInstance(), RealmsConnect.this.onlineScreen.getProxy())
                     );
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.this.connection.send(new HandshakeC2SPacket(31, string, i, NetworkProtocol.LOGIN));
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.this.connection.send(new HelloC2SPacket(MinecraftClient.getInstance().getSession().getProfile()));
               } catch (UnknownHostException var5) {
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.LOGGER.error("Couldn't connect to world", var5);
                  Realms.setScreen(
                     new DisconnectedOnlineScreen(
                        RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host '" + string + "'")
                     )
                  );
               } catch (Exception var6) {
                  if (RealmsConnect.this.aborted) {
                     return;
                  }
   
                  RealmsConnect.LOGGER.error("Couldn't connect to world", var6);
                  String var3 = var6.toString();
                  if (var1 != null) {
                     String var4 = var1.toString() + ":" + i;
                     var3 = var3.replaceAll(var4, "");
                  }
   
                  Realms.setScreen(
                     new DisconnectedOnlineScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", var3))
                  );
               }
            }
         })
         .start();
   }

   public void abort() {
      this.aborted = true;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isOpen()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }
   }
}
