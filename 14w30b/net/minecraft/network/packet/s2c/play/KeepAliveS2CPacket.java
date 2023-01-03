package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class KeepAliveS2CPacket implements Packet {
   private int timeMillis;

   public KeepAliveS2CPacket() {
   }

   public KeepAliveS2CPacket(int timeMillis) {
      this.timeMillis = timeMillis;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleKeepAlive(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.timeMillis = buffer.readInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.timeMillis);
   }

   @Environment(EnvType.CLIENT)
   public int getTimeMillis() {
      return this.timeMillis;
   }
}
