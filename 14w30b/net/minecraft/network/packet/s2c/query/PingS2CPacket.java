package net.minecraft.network.packet.s2c.query;

import net.minecraft.client.network.handler.ClientQueryPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PingS2CPacket implements Packet {
   private long time;

   public PingS2CPacket() {
   }

   public PingS2CPacket(long time) {
      this.time = time;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.time = buffer.readLong();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeLong(this.time);
   }

   public void handle(ClientQueryPacketHandler c_74cpriuuv) {
      c_74cpriuuv.handlePing(this);
   }

   @Environment(EnvType.CLIENT)
   public long getTime() {
      return this.time;
   }
}
