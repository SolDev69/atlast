package net.minecraft.inventory.menu;

import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class HorseMenu extends InventoryMenu {
   private Inventory horseInventory;
   private HorseBaseEntity horse;

   public HorseMenu(Inventory playerInventory, Inventory horseInventory, HorseBaseEntity horse, PlayerEntity player) {
      this.horseInventory = horseInventory;
      this.horse = horse;
      byte var5 = 3;
      horseInventory.onOpen(player);
      int var6 = (var5 - 4) * 18;
      this.addSlot(new InventorySlot(horseInventory, 0, 8, 18) {
         @Override
         public boolean canSetStack(ItemStack stack) {
            return super.canSetStack(stack) && stack.getItem() == Items.SADDLE && !this.hasStack();
         }
      });
      this.addSlot(new InventorySlot(horseInventory, 1, 8, 36) {
         @Override
         public boolean canSetStack(ItemStack stack) {
            return super.canSetStack(stack) && horse.drawHoverEffect() && HorseBaseEntity.isHorseArmor(stack.getItem());
         }

         @Environment(EnvType.CLIENT)
         @Override
         public boolean isActive() {
            return horse.drawHoverEffect();
         }
      });
      if (horse.hasChest()) {
         for(int var7 = 0; var7 < var5; ++var7) {
            for(int var8 = 0; var8 < 5; ++var8) {
               this.addSlot(new InventorySlot(horseInventory, 2 + var8 + var7 * 5, 80 + var8 * 18, 18 + var7 * 18));
            }
         }
      }

      for(int var9 = 0; var9 < 3; ++var9) {
         for(int var11 = 0; var11 < 9; ++var11) {
            this.addSlot(new InventorySlot(playerInventory, var11 + var9 * 9 + 9, 8 + var11 * 18, 102 + var9 * 18 + var6));
         }
      }

      for(int var10 = 0; var10 < 9; ++var10) {
         this.addSlot(new InventorySlot(playerInventory, var10, 8 + var10 * 18, 160 + var6));
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.horseInventory.isValid(player) && this.horse.isAlive() && this.horse.getDistanceTo(player) < 8.0F;
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id < this.horseInventory.getSize()) {
            if (!this.moveStack(var5, this.horseInventory.getSize(), this.slots.size(), true)) {
               return null;
            }
         } else if (this.getSlot(1).canSetStack(var5) && !this.getSlot(1).hasStack()) {
            if (!this.moveStack(var5, 1, 2, false)) {
               return null;
            }
         } else if (this.getSlot(0).canSetStack(var5)) {
            if (!this.moveStack(var5, 0, 1, false)) {
               return null;
            }
         } else if (this.horseInventory.getSize() <= 2 || !this.moveStack(var5, 2, this.horseInventory.getSize(), false)) {
            return null;
         }

         if (var5.size == 0) {
            var4.setStack(null);
         } else {
            var4.markDirty();
         }
      }

      return var3;
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      this.horseInventory.onClose(player);
   }
}
