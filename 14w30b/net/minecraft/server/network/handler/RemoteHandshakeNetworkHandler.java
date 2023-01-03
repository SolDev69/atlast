package net.minecraft.server.network.handler;

import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginFailS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class RemoteHandshakeNetworkHandler implements ServerHandshakePacketHandler {
   private final MinecraftServer server;
   private final Connection connection;

   public RemoteHandshakeNetworkHandler(MinecraftServer server, Connection connection) {
      this.server = server;
      this.connection = connection;
   }

   @Override
   public void handleHandshake(HandshakeC2SPacket packet) {
      switch(packet.getProtocol()) {
         case LOGIN:
            this.connection.setProtocol(NetworkProtocol.LOGIN);
            if (packet.getVersion() > 31) {
               LiteralText var2 = new LiteralText("Outdated server! I'm still on 14w30c");
               this.connection.send(new LoginFailS2CPacket(var2));
               this.connection.disconnect(var2);
            } else if (packet.getVersion() < 31) {
               LiteralText var3 = new LiteralText("Outdated client! Please use 14w30c");
               this.connection.send(new LoginFailS2CPacket(var3));
               this.connection.disconnect(var3);
            } else {
               this.connection.setListener(new ServerLoginNetworkHandler(this.server, this.connection));
            }
            break;
         case STATUS:
            this.connection.setProtocol(NetworkProtocol.STATUS);
            this.connection.setListener(new ServerQueryNetworkHandler(this.server, this.connection));
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + packet.getProtocol());
      }
   }

   @Override
   public void onDisconnect(Text reason) {
   }
}
