package net.minecraft.network.packet.s2c.login;

import net.minecraft.client.network.handler.ClientLoginPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CompressionThresholdS2CPacket implements Packet {
   private int compressionThreshold;

   public CompressionThresholdS2CPacket() {
   }

   public CompressionThresholdS2CPacket(int compressionThreshold) {
      this.compressionThreshold = compressionThreshold;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.compressionThreshold = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientLoginPacketHandler c_67vhogdbn) {
      c_67vhogdbn.handleCompressionThreshold(this);
   }

   @Environment(EnvType.CLIENT)
   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
