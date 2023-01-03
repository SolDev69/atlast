package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CompressionThresholdS2CPacket implements Packet {
   private int compressionThreshold;

   @Override
   public void write(PacketByteBuf buffer) {
      this.compressionThreshold = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleCompressionThreshold(this);
   }

   @Environment(EnvType.CLIENT)
   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
