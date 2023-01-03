package net.minecraft.inventory.menu;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BeaconMenu extends InventoryMenu {
   private Inventory beacon;
   private final BeaconMenu.PaymentSlot paymentSlot;

   public BeaconMenu(Inventory player, Inventory beacon) {
      this.beacon = beacon;
      this.addSlot(this.paymentSlot = new BeaconMenu.PaymentSlot(beacon, 0, 136, 110));
      byte var3 = 36;
      short var4 = 137;

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new InventorySlot(player, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new InventorySlot(player, var7, var3 + var7 * 18, 58 + var4));
      }
   }

   @Override
   public void addListener(InventoryMenuListener listener) {
      super.addListener(listener);
      listener.updateData(this, this.beacon);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
      this.beacon.setData(id, value);
   }

   public Inventory getBeacon() {
      return this.beacon;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      return this.beacon.isValid(player);
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id == 0) {
            if (!this.moveStack(var5, 1, 37, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
         } else if (!this.paymentSlot.hasStack() && this.paymentSlot.canSetStack(var5) && var5.size == 1) {
            if (!this.moveStack(var5, 0, 1, false)) {
               return null;
            }
         } else if (id >= 1 && id < 28) {
            if (!this.moveStack(var5, 28, 37, false)) {
               return null;
            }
         } else if (id >= 28 && id < 37) {
            if (!this.moveStack(var5, 1, 28, false)) {
               return null;
            }
         } else if (!this.moveStack(var5, 1, 37, false)) {
            return null;
         }

         if (var5.size == 0) {
            var4.setStack(null);
         } else {
            var4.markDirty();
         }

         if (var5.size == var3.size) {
            return null;
         }

         var4.onStackRemovedByPlayer(player, var5);
      }

      return var3;
   }

   class PaymentSlot extends InventorySlot {
      public PaymentSlot(Inventory inventory, int slot, int x, int y) {
         super(inventory, slot, x, y);
      }

      @Override
      public boolean canSetStack(ItemStack stack) {
         if (stack == null) {
            return false;
         } else {
            return stack.getItem() == Items.EMERALD
               || stack.getItem() == Items.DIAMOND
               || stack.getItem() == Items.GOLD_INGOT
               || stack.getItem() == Items.IRON_INGOT;
         }
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }
   }
}
