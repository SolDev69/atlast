package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class KeepAliveC2SPacket implements Packet {
   private int timeMillis;

   public KeepAliveC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public KeepAliveC2SPacket(int timeMillis) {
      this.timeMillis = timeMillis;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleKeepAlive(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.timeMillis = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.timeMillis);
   }

   public int getTimeMillis() {
      return this.timeMillis;
   }
}
