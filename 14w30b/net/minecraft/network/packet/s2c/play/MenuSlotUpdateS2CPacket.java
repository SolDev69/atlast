package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MenuSlotUpdateS2CPacket implements Packet {
   private int menuId;
   private int slotId;
   private ItemStack stack;

   public MenuSlotUpdateS2CPacket() {
   }

   public MenuSlotUpdateS2CPacket(int menuId, int slotId, ItemStack stack) {
      this.menuId = menuId;
      this.slotId = slotId;
      this.stack = stack == null ? null : stack.copy();
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleMenuSlotUpdate(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readByte();
      this.slotId = buffer.readShort();
      this.stack = buffer.readItemStack();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.slotId);
      buffer.writeItemStack(this.stack);
   }

   @Environment(EnvType.CLIENT)
   public int getMenuId() {
      return this.menuId;
   }

   @Environment(EnvType.CLIENT)
   public int getSlotId() {
      return this.slotId;
   }

   @Environment(EnvType.CLIENT)
   public ItemStack getStack() {
      return this.stack;
   }
}
