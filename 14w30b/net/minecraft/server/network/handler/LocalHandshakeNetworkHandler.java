package net.minecraft.server.network.handler;

import net.minecraft.network.Connection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LocalHandshakeNetworkHandler implements ServerHandshakePacketHandler {
   private final MinecraftServer server;
   private final Connection connection;

   public LocalHandshakeNetworkHandler(MinecraftServer server, Connection connection) {
      this.server = server;
      this.connection = connection;
   }

   @Override
   public void handleHandshake(HandshakeC2SPacket packet) {
      this.connection.setProtocol(packet.getProtocol());
      this.connection.setListener(new ServerLoginNetworkHandler(this.server, this.connection));
   }

   @Override
   public void onDisconnect(Text reason) {
   }
}
