package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.DispenserMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class DispenserBlockEntity extends InventoryBlockEntity implements Inventory {
   private static final Random RANDOM = new Random();
   private ItemStack[] inventory = new ItemStack[9];
   protected String customName;

   @Override
   public int getSize() {
      return 9;
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.inventory[slot];
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (this.inventory[slot] != null) {
         if (this.inventory[slot].size <= amount) {
            ItemStack var4 = this.inventory[slot];
            this.inventory[slot] = null;
            this.markDirty();
            return var4;
         } else {
            ItemStack var3 = this.inventory[slot].split(amount);
            if (this.inventory[slot].size == 0) {
               this.inventory[slot] = null;
            }

            this.markDirty();
            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (this.inventory[slot] != null) {
         ItemStack var2 = this.inventory[slot];
         this.inventory[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   public int pickNonEmptySlot() {
      int var1 = -1;
      int var2 = 1;

      for(int var3 = 0; var3 < this.inventory.length; ++var3) {
         if (this.inventory[var3] != null && RANDOM.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.inventory[slot] = stack;
      if (stack != null && stack.size > this.getMaxStackSize()) {
         stack.size = this.getMaxStackSize();
      }

      this.markDirty();
   }

   public int insertStack(ItemStack stack) {
      for(int var2 = 0; var2 < this.inventory.length; ++var2) {
         if (this.inventory[var2] == null || this.inventory[var2].getItem() == null) {
            this.setStack(var2, stack);
            return var2;
         }
      }

      return -1;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.dispenser";
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      NbtList var2 = nbt.getList("Items", 10);
      this.inventory = new ItemStack[this.getSize()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         int var5 = var4.getByte("Slot") & 255;
         if (var5 >= 0 && var5 < this.inventory.length) {
            this.inventory[var5] = ItemStack.fromNbt(var4);
         }
      }

      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.inventory.length; ++var3) {
         if (this.inventory[var3] != null) {
            NbtCompound var4 = new NbtCompound();
            var4.putByte("Slot", (byte)var3);
            this.inventory[var3].writeNbt(var4);
            var2.add(var4);
         }
      }

      nbt.put("Items", var2);
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }

   @Override
   public void onOpen(PlayerEntity player) {
   }

   @Override
   public void onClose(PlayerEntity player) {
   }

   @Override
   public boolean canSetStack(int slot, ItemStack stack) {
      return true;
   }

   @Override
   public String getMenuType() {
      return "minecraft:dispenser";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new DispenserMenu(playerInventory, this);
   }

   @Override
   public int getData(int id) {
      return 0;
   }

   @Override
   public void setData(int id, int value) {
   }

   @Override
   public int getDataCount() {
      return 0;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.inventory.length; ++var1) {
         this.inventory[var1] = null;
      }
   }
}
