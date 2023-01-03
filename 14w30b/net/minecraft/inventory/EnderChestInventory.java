package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class EnderChestInventory extends SimpleInventory {
   private EnderChestBlockEntity enderChest;

   public EnderChestInventory() {
      super("container.enderchest", false, 27);
   }

   public void setCurrentBlockEntity(EnderChestBlockEntity enderChest) {
      this.enderChest = enderChest;
   }

   public void readNbt(NbtList list) {
      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         this.setStack(var2, null);
      }

      for(int var5 = 0; var5 < list.size(); ++var5) {
         NbtCompound var3 = list.getCompound(var5);
         int var4 = var3.getByte("Slot") & 255;
         if (var4 >= 0 && var4 < this.getSize()) {
            this.setStack(var4, ItemStack.fromNbt(var3));
         }
      }
   }

   public NbtList toNbt() {
      NbtList var1 = new NbtList();

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         ItemStack var3 = this.getStack(var2);
         if (var3 != null) {
            NbtCompound var4 = new NbtCompound();
            var4.putByte("Slot", (byte)var2);
            var3.writeNbt(var4);
            var1.add(var4);
         }
      }

      return var1;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.enderChest != null && !this.enderChest.isValid(player) ? false : super.isValid(player);
   }

   @Override
   public void onOpen(PlayerEntity player) {
      if (this.enderChest != null) {
         this.enderChest.onOpen();
      }

      super.onOpen(player);
   }

   @Override
   public void onClose(PlayerEntity player) {
      if (this.enderChest != null) {
         this.enderChest.onClose();
      }

      super.onClose(player);
      this.enderChest = null;
   }
}
