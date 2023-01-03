package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.handler.ClientQueryPacketHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.PingC2SPacket;
import net.minecraft.network.packet.c2s.query.ServerStatusC2SPacket;
import net.minecraft.network.packet.s2c.query.PingS2CPacket;
import net.minecraft.network.packet.s2c.query.ServerStatusS2CPacket;
import net.minecraft.server.ServerStatus;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerStatusPinger {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(String string, ServerPing serverPing) {
      if (string != null && !string.startsWith("0.0.0.0") && !string.isEmpty()) {
         RealmsServerAddress var3 = RealmsServerAddress.parseString(string);
         final Connection var4 = Connection.connect(InetAddress.getByName(var3.getHost()), var3.getPort());
         this.connections.add(var4);
         var4.setListener(new ClientQueryPacketHandler() {
            private boolean f_83vbrvyxw = false;

            @Override
            public void handleServerStatus(ServerStatusS2CPacket packet) {
               ServerStatus var2 = packet.getServerStatus();
               if (var2.getPlayers() != null) {
                  serverPing.nrOfPlayers = String.valueOf(var2.getPlayers().getOnline());
               }

               var4.send(new PingC2SPacket(Realms.currentTimeMillis()));
               this.f_83vbrvyxw = true;
            }

            @Override
            public void handlePing(PingS2CPacket packet) {
               var4.disconnect(new LiteralText("Finished"));
            }

            @Override
            public void onDisconnect(Text reason) {
               if (!this.f_83vbrvyxw) {
                  RealmsServerStatusPinger.LOGGER.error("Can't ping " + string + ": " + reason.buildString());
               }
            }
         });

         try {
            var4.send(new HandshakeC2SPacket(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, var3.getHost(), var3.getPort(), NetworkProtocol.STATUS));
            var4.send(new ServerStatusC2SPacket());
         } catch (Throwable var6) {
            LOGGER.error(var6);
         }
      }
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isOpen()) {
               var3.tick();
            } else {
               var2.remove();
               var3.handleDisconnection();
            }
         }
      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if (var3.isOpen()) {
               var2.remove();
               var3.disconnect(new LiteralText("Cancelled"));
            }
         }
      }
   }
}
