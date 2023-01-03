package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SelectSlotS2CPacket implements Packet {
   private int slot;

   public SelectSlotS2CPacket() {
   }

   public SelectSlotS2CPacket(int slot) {
      this.slot = slot;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.slot = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.slot);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleSelectSlot(this);
   }

   @Environment(EnvType.CLIENT)
   public int getSlot() {
      return this.slot;
   }
}
