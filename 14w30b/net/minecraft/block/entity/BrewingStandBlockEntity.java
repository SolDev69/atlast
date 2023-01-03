package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.menu.BrewingStandMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class BrewingStandBlockEntity extends InventoryBlockEntity implements Tickable, SidedInventory {
   private static final int[] INGREDIENT_SLOTS = new int[]{3};
   private static final int[] POTION_SLOTS = new int[]{0, 1, 2};
   private ItemStack[] inputs = new ItemStack[4];
   private int timer;
   private boolean[] hasInput;
   private Item ingredient;
   private String customName;

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.brewing";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public int getSize() {
      return this.inputs.length;
   }

   @Override
   public void tick() {
      if (this.timer > 0) {
         --this.timer;
         if (this.timer == 0) {
            this.finishBrewing();
            this.markDirty();
         } else if (!this.canFinishBrewing()) {
            this.timer = 0;
            this.markDirty();
         } else if (this.ingredient != this.inputs[3].getItem()) {
            this.timer = 0;
            this.markDirty();
         }
      } else if (this.canFinishBrewing()) {
         this.timer = 400;
         this.ingredient = this.inputs[3].getItem();
      }

      if (!this.world.isClient) {
         boolean[] var1 = this.findFilledInputs();
         if (!Arrays.equals(var1, this.hasInput)) {
            this.hasInput = var1;
            BlockState var2 = this.world.getBlockState(this.getPos());

            for(int var3 = 0; var3 < BrewingStandBlock.HAS_BOTTLE.length; ++var3) {
               var2 = var2.set(BrewingStandBlock.HAS_BOTTLE[var3], var1[var3]);
            }

            this.world.setBlockState(this.pos, var2, 2);
         }
      }
   }

   private boolean canFinishBrewing() {
      if (this.inputs[3] != null && this.inputs[3].size > 0) {
         ItemStack var1 = this.inputs[3];
         if (!var1.getItem().hasBrewingRecipe(var1)) {
            return false;
         } else {
            boolean var2 = false;

            for(int var3 = 0; var3 < 3; ++var3) {
               if (this.inputs[var3] != null && this.inputs[var3].getItem() == Items.POTION) {
                  int var4 = this.inputs[var3].getMetadata();
                  int var5 = this.updatePotion(var4, var1);
                  if (!PotionItem.isSplashPotion(var4) && PotionItem.isSplashPotion(var5)) {
                     var2 = true;
                     break;
                  }

                  List var6 = Items.POTION.getEffectsFromMetadata(var4);
                  List var7 = Items.POTION.getEffectsFromMetadata(var5);
                  if ((var4 <= 0 || var6 != var7) && (var6 == null || !var6.equals(var7) && var7 != null) && var4 != var5) {
                     var2 = true;
                     break;
                  }
               }
            }

            return var2;
         }
      } else {
         return false;
      }
   }

   private void finishBrewing() {
      if (this.canFinishBrewing()) {
         ItemStack var1 = this.inputs[3];

         for(int var2 = 0; var2 < 3; ++var2) {
            if (this.inputs[var2] != null && this.inputs[var2].getItem() == Items.POTION) {
               int var3 = this.inputs[var2].getMetadata();
               int var4 = this.updatePotion(var3, var1);
               List var5 = Items.POTION.getEffectsFromMetadata(var3);
               List var6 = Items.POTION.getEffectsFromMetadata(var4);
               if (var3 > 0 && var5 == var6 || var5 != null && (var5.equals(var6) || var6 == null)) {
                  if (!PotionItem.isSplashPotion(var3) && PotionItem.isSplashPotion(var4)) {
                     this.inputs[var2].setDamage(var4);
                  }
               } else if (var3 != var4) {
                  this.inputs[var2].setDamage(var4);
               }
            }
         }

         if (var1.getItem().hasRecipeRemainder()) {
            this.inputs[3] = new ItemStack(var1.getItem().getRecipeRemainder());
         } else {
            --this.inputs[3].size;
            if (this.inputs[3].size <= 0) {
               this.inputs[3] = null;
            }
         }
      }
   }

   private int updatePotion(int metadata, ItemStack potion) {
      if (potion == null) {
         return metadata;
      } else {
         return potion.getItem().hasBrewingRecipe(potion) ? PotionHelper.updateMetadata(metadata, potion.getItem().getBrewingRecipe(potion)) : metadata;
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      NbtList var2 = nbt.getList("Items", 10);
      this.inputs = new ItemStack[this.getSize()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         byte var5 = var4.getByte("Slot");
         if (var5 >= 0 && var5 < this.inputs.length) {
            this.inputs[var5] = ItemStack.fromNbt(var4);
         }
      }

      this.timer = nbt.getShort("BrewTime");
      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putShort("BrewTime", (short)this.timer);
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.inputs.length; ++var3) {
         if (this.inputs[var3] != null) {
            NbtCompound var4 = new NbtCompound();
            var4.putByte("Slot", (byte)var3);
            this.inputs[var3].writeNbt(var4);
            var2.add(var4);
         }
      }

      nbt.put("Items", var2);
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public ItemStack getStack(int slot) {
      return slot >= 0 && slot < this.inputs.length ? this.inputs[slot] : null;
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (slot >= 0 && slot < this.inputs.length) {
         ItemStack var3 = this.inputs[slot];
         this.inputs[slot] = null;
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (slot >= 0 && slot < this.inputs.length) {
         ItemStack var2 = this.inputs[slot];
         this.inputs[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      if (slot >= 0 && slot < this.inputs.length) {
         this.inputs[slot] = stack;
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
      if (slot == 3) {
         return stack.getItem().hasBrewingRecipe(stack);
      } else {
         return stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE;
      }
   }

   public boolean[] findFilledInputs() {
      boolean[] var1 = new boolean[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         if (this.inputs[var2] != null) {
            var1[var2] = true;
         }
      }

      return var1;
   }

   @Override
   public int[] getSlots(Direction side) {
      return side == Direction.UP ? INGREDIENT_SLOTS : POTION_SLOTS;
   }

   @Override
   public boolean canHopperAddStack(int slot, ItemStack stack, Direction side) {
      return this.canSetStack(slot, stack);
   }

   @Override
   public boolean canHopperRemoveStack(int slot, ItemStack stack, Direction side) {
      return true;
   }

   @Override
   public String getMenuType() {
      return "minecraft:brewing_stand";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new BrewingStandMenu(playerInventory, this);
   }

   @Override
   public int getData(int id) {
      switch(id) {
         case 0:
            return this.timer;
         default:
            return 0;
      }
   }

   @Override
   public void setData(int id, int value) {
      switch(id) {
         case 0:
            this.timer = value;
      }
   }

   @Override
   public int getDataCount() {
      return 1;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.inputs.length; ++var1) {
         this.inputs[var1] = null;
      }
   }
}
