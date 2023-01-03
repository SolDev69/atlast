package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerQueryPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PingC2SPacket implements Packet {
   private long time;

   public PingC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PingC2SPacket(long time) {
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

   public void handle(ServerQueryPacketHandler c_57rlldbju) {
      c_57rlldbju.handlePing(this);
   }

   public long getTime() {
      return this.time;
   }
}
