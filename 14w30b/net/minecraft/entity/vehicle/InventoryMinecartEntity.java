package net.minecraft.entity.vehicle;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.InventoryLock;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.LockableMenuProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public abstract class InventoryMinecartEntity extends MinecartEntity implements LockableMenuProvider {
   private ItemStack[] stacks = new ItemStack[36];
   private boolean canBeRemoved = true;

   public InventoryMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public InventoryMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public void dropItems(DamageSource damageSource) {
      super.dropItems(damageSource);
      InventoryUtils.dropContents(this.world, this, this);
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.stacks[slot];
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (this.stacks[slot] != null) {
         if (this.stacks[slot].size <= amount) {
            ItemStack var4 = this.stacks[slot];
            this.stacks[slot] = null;
            return var4;
         } else {
            ItemStack var3 = this.stacks[slot].split(amount);
            if (this.stacks[slot].size == 0) {
               this.stacks[slot] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (this.stacks[slot] != null) {
         ItemStack var2 = this.stacks[slot];
         this.stacks[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.stacks[slot] = stack;
      if (stack != null && stack.size > this.getMaxStackSize()) {
         stack.size = this.getMaxStackSize();
      }
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.removed) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo(this) > 64.0);
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
   public String getName() {
      return this.hasCustomName() ? this.getCustomName() : "container.minecart";
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public void teleportToDimension(int dimensionId) {
      this.canBeRemoved = false;
      super.teleportToDimension(dimensionId);
   }

   @Override
   public void remove() {
      if (this.canBeRemoved) {
         InventoryUtils.dropContents(this.world, this, this);
      }

      super.remove();
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.stacks.length; ++var3) {
         if (this.stacks[var3] != null) {
            NbtCompound var4 = new NbtCompound();
            var4.putByte("Slot", (byte)var3);
            this.stacks[var3].writeNbt(var4);
            var2.add(var4);
         }
      }

      nbt.put("Items", var2);
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      NbtList var2 = nbt.getList("Items", 10);
      this.stacks = new ItemStack[this.getSize()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         int var5 = var4.getByte("Slot") & 255;
         if (var5 >= 0 && var5 < this.stacks.length) {
            this.stacks[var5] = ItemStack.fromNbt(var4);
         }
      }
   }

   @Override
   public boolean interact(PlayerEntity player) {
      if (!this.world.isClient) {
         player.openInventoryMenu(this);
      }

      return true;
   }

   @Override
   protected void applySlowdown() {
      int var1 = 15 - InventoryMenu.getAnalogOutput(this);
      float var2 = 0.98F + (float)var1 * 0.001F;
      this.velocityX *= (double)var2;
      this.velocityY *= 0.0;
      this.velocityZ *= (double)var2;
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
   public boolean isLocked() {
      return false;
   }

   @Override
   public void setLock(InventoryLock lock) {
   }

   @Override
   public InventoryLock getLock() {
      return InventoryLock.NONE;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.stacks.length; ++var1) {
         this.stacks[var1] = null;
      }
   }
}
