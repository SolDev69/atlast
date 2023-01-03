package net.minecraft.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class ShapedRecipe implements CraftingRecipe {
   private final int width;
   private final int height;
   private final ItemStack[] inputs;
   private final ItemStack result;
   private boolean copyNbt;

   public ShapedRecipe(int width, int height, ItemStack[] inputs, ItemStack result) {
      this.width = width;
      this.height = height;
      this.inputs = inputs;
      this.result = result;
   }

   @Override
   public ItemStack getOutput() {
      return this.result;
   }

   @Override
   public ItemStack[] getRemainder(CraftingInventory inventory) {
      ItemStack[] var2 = new ItemStack[inventory.getSize()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = inventory.getStack(var3);
         if (var4 != null && var4.getItem().hasRecipeRemainder()) {
            var2[var3] = new ItemStack(var4.getItem().getRecipeRemainder());
         }
      }

      return var2;
   }

   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      for(int var3 = 0; var3 <= 3 - this.width; ++var3) {
         for(int var4 = 0; var4 <= 3 - this.height; ++var4) {
            if (this.matches(inventory, var3, var4, true)) {
               return true;
            }

            if (this.matches(inventory, var3, var4, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matches(CraftingInventory inventory, int horizontalOffset, int verticalOffset, boolean horizontal) {
      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 3; ++var6) {
            int var7 = var5 - horizontalOffset;
            int var8 = var6 - verticalOffset;
            ItemStack var9 = null;
            if (var7 >= 0 && var8 >= 0 && var7 < this.width && var8 < this.height) {
               if (horizontal) {
                  var9 = this.inputs[this.width - var7 - 1 + var8 * this.width];
               } else {
                  var9 = this.inputs[var7 + var8 * this.width];
               }
            }

            ItemStack var10 = inventory.getStack(var5, var6);
            if (var10 != null || var9 != null) {
               if (var10 == null && var9 != null || var10 != null && var9 == null) {
                  return false;
               }

               if (var9.getItem() != var10.getItem()) {
                  return false;
               }

               if (var9.getMetadata() != 32767 && var9.getMetadata() != var10.getMetadata()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      ItemStack var2 = this.getOutput().copy();
      if (this.copyNbt) {
         for(int var3 = 0; var3 < inventory.getSize(); ++var3) {
            ItemStack var4 = inventory.getStack(var3);
            if (var4 != null && var4.hasNbt()) {
               var2.setNbt((NbtCompound)var4.getNbt().copy());
            }
         }
      }

      return var2;
   }

   @Override
   public int getInputCount() {
      return this.width * this.height;
   }

   public ShapedRecipe setCopyNbt() {
      this.copyNbt = true;
      return this;
   }
}
