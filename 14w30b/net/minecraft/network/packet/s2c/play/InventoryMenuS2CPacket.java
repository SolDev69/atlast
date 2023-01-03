package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class InventoryMenuS2CPacket implements Packet {
   private int menuId;
   private ItemStack[] stacks;

   public InventoryMenuS2CPacket() {
   }

   public InventoryMenuS2CPacket(int menuId, List stacks) {
      this.menuId = menuId;
      this.stacks = new ItemStack[stacks.size()];

      for(int var3 = 0; var3 < this.stacks.length; ++var3) {
         ItemStack var4 = (ItemStack)stacks.get(var3);
         this.stacks[var3] = var4 == null ? null : var4.copy();
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readUnsignedByte();
      short var2 = buffer.readShort();
      this.stacks = new ItemStack[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.stacks[var3] = buffer.readItemStack();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.stacks.length);

      for(ItemStack var5 : this.stacks) {
         buffer.writeItemStack(var5);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleInventoryMenu(this);
   }

   @Environment(EnvType.CLIENT)
   public int getMenuId() {
      return this.menuId;
   }

   @Environment(EnvType.CLIENT)
   public ItemStack[] getStacks() {
      return this.stacks;
   }
}
