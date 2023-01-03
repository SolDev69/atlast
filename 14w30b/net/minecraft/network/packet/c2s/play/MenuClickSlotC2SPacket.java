package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MenuClickSlotC2SPacket implements Packet {
   private int menuId;
   private int slotId;
   private int clickData;
   private short actionId;
   private ItemStack stack;
   private int action;

   public MenuClickSlotC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public MenuClickSlotC2SPacket(int menuId, int slotId, int clickData, int action, ItemStack stack, short actionId) {
      this.menuId = menuId;
      this.slotId = slotId;
      this.clickData = clickData;
      this.stack = stack != null ? stack.copy() : null;
      this.actionId = actionId;
      this.action = action;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleMenuClickSlot(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readByte();
      this.slotId = buffer.readShort();
      this.clickData = buffer.readByte();
      this.actionId = buffer.readShort();
      this.action = buffer.readByte();
      this.stack = buffer.readItemStack();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.slotId);
      buffer.writeByte(this.clickData);
      buffer.writeShort(this.actionId);
      buffer.writeByte(this.action);
      buffer.writeItemStack(this.stack);
   }

   public int getMenuId() {
      return this.menuId;
   }

   public int getSlotId() {
      return this.slotId;
   }

   public int getClickData() {
      return this.clickData;
   }

   public short getActionId() {
      return this.actionId;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public int getAction() {
      return this.action;
   }
}
