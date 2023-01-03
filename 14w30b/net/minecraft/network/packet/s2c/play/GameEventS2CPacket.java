package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class GameEventS2CPacket implements Packet {
   public static final String[] EVENT_MESSAGES = new String[]{"tile.bed.notValid"};
   private int event;
   private float data;

   public GameEventS2CPacket() {
   }

   public GameEventS2CPacket(int event, float data) {
      this.event = event;
      this.data = data;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.event = buffer.readUnsignedByte();
      this.data = buffer.readFloat();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.event);
      buffer.writeFloat(this.data);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleGameEvent(this);
   }

   @Environment(EnvType.CLIENT)
   public int getEvent() {
      return this.event;
   }

   @Environment(EnvType.CLIENT)
   public float getData() {
      return this.data;
   }
}
