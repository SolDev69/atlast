package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.handler.ClientLoginNetworkHandler;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.HelloC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ConnectScreen extends Screen {
   private static final AtomicInteger connectorThreadsCount = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private Connection connection;
   private boolean connectingCancelled;
   private final Screen parent;

   public ConnectScreen(Screen parent, MinecraftClient client, ServerListEntry server) {
      this.client = client;
      this.parent = parent;
      ServerAddress var4 = ServerAddress.parse(server.address);
      client.setWorld(null);
      client.setCurrentServerEntry(server);
      this.connect(var4.getAddress(), var4.getPort());
   }

   public ConnectScreen(Screen parent, MinecraftClient client, String address, int port) {
      this.client = client;
      this.parent = parent;
      client.setWorld(null);
      this.connect(address, port);
   }

   private void connect(String address, int port) {
      LOGGER.info("Connecting to " + address + ", " + port);
      (new Thread("Server Connector #" + connectorThreadsCount.incrementAndGet()) {
            @Override
            public void run() {
               InetAddress var1 = null;
   
               try {
                  if (ConnectScreen.this.connectingCancelled) {
                     return;
                  }
   
                  var1 = InetAddress.getByName(address);
                  ConnectScreen.this.connection = Connection.connect(var1, port);
                  ConnectScreen.this.connection
                     .setListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, ConnectScreen.this.client, ConnectScreen.this.parent));
                  ConnectScreen.this.connection.send(new HandshakeC2SPacket(30, address, port, NetworkProtocol.LOGIN));
                  ConnectScreen.this.connection.send(new HelloC2SPacket(ConnectScreen.this.client.getSession().getProfile()));
               } catch (UnknownHostException var5) {
                  if (ConnectScreen.this.connectingCancelled) {
                     return;
                  }
   
                  ConnectScreen.LOGGER.error("Couldn't connect to server", var5);
                  ConnectScreen.this.client
                     .openScreen(
                        new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host"))
                     );
               } catch (Exception var6) {
                  if (ConnectScreen.this.connectingCancelled) {
                     return;
                  }
   
                  ConnectScreen.LOGGER.error("Couldn't connect to server", var6);
                  String var3 = var6.toString();
                  if (var1 != null) {
                     String var4 = var1.toString() + ":" + port;
                     var3 = var3.replaceAll(var4, "");
                  }
   
                  ConnectScreen.this.client
                     .openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", var3)));
               }
            }
         })
         .start();
   }

   @Override
   public void tick() {
      if (this.connection != null) {
         if (this.connection.isOpen()) {
            this.connection.tick();
         } else if (this.connection.getDisconnectReason() != null) {
            this.connection.getListener().onDisconnect(this.connection.getDisconnectReason());
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   public void init() {
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0) {
         this.connectingCancelled = true;
         if (this.connection != null) {
            this.connection.disconnect(new LiteralText("Aborted"));
         }

         this.client.openScreen(this.parent);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      if (this.connection == null) {
         this.drawCenteredString(this.textRenderer, I18n.translate("connect.connecting"), this.titleWidth / 2, this.height / 2 - 50, 16777215);
      } else {
         this.drawCenteredString(this.textRenderer, I18n.translate("connect.authorizing"), this.titleWidth / 2, this.height / 2 - 50, 16777215);
      }

      super.render(mouseX, mouseY, tickDelta);
   }
}
