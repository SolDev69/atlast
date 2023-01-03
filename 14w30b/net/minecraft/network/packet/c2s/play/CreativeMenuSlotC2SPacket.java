package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CreativeMenuSlotC2SPacket implements Packet {
   private int slotId;
   private ItemStack stack;

   public CreativeMenuSlotC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public CreativeMenuSlotC2SPacket(int slotId, ItemStack stack) {
      this.slotId = slotId;
      this.stack = stack != null ? stack.copy() : null;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleCreativeMenuSlot(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.slotId = buffer.readShort();
      this.stack = buffer.readItemStack();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeShort(this.slotId);
      buffer.writeItemStack(this.stack);
   }

   public int getSlotId() {
      return this.slotId;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
