package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldTimeS2CPacket implements Packet {
   private long time;
   private long timeOfDay;

   public WorldTimeS2CPacket() {
   }

   public WorldTimeS2CPacket(long time, long timeOfDay, boolean doDaylightCycle) {
      this.time = time;
      this.timeOfDay = timeOfDay;
      if (!doDaylightCycle) {
         this.timeOfDay = -this.timeOfDay;
         if (this.timeOfDay == 0L) {
            this.timeOfDay = -1L;
         }
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.time = buffer.readLong();
      this.timeOfDay = buffer.readLong();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeLong(this.time);
      buffer.writeLong(this.timeOfDay);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleWorldTime(this);
   }

   @Environment(EnvType.CLIENT)
   public long getTime() {
      return this.time;
   }

   @Environment(EnvType.CLIENT)
   public long getTimeOfDay() {
      return this.timeOfDay;
   }
}
