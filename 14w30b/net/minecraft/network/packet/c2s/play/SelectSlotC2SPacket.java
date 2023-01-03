package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SelectSlotC2SPacket implements Packet {
   private int slot;

   public SelectSlotC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public SelectSlotC2SPacket(int slot) {
      this.slot = slot;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.slot = buffer.readShort();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeShort(this.slot);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleSelectSlot(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
