package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerHandshakePacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class HandshakeC2SPacket implements Packet {
   private int version;
   private String address;
   private int port;
   private NetworkProtocol protocol;

   public HandshakeC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public HandshakeC2SPacket(int version, String address, int port, NetworkProtocol protocol) {
      this.version = version;
      this.address = address;
      this.port = port;
      this.protocol = protocol;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.version = buffer.readVarInt();
      this.address = buffer.readString(255);
      this.port = buffer.readUnsignedShort();
      this.protocol = NetworkProtocol.byId(buffer.readVarInt());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.version);
      buffer.writeString(this.address);
      buffer.writeShort(this.port);
      buffer.writeVarInt(this.protocol.getId());
   }

   public void handle(ServerHandshakePacketHandler c_63ltmeiqk) {
      c_63ltmeiqk.handleHandshake(this);
   }

   public NetworkProtocol getProtocol() {
      return this.protocol;
   }

   public int getVersion() {
      return this.version;
   }
}
