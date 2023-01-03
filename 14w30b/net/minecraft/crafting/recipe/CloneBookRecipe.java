package net.minecraft.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class CloneBookRecipe implements CraftingRecipe {
   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      int var3 = 0;
      ItemStack var4 = null;

      for(int var5 = 0; var5 < inventory.getSize(); ++var5) {
         ItemStack var6 = inventory.getStack(var5);
         if (var6 != null) {
            if (var6.getItem() == Items.WRITTEN_BOOK) {
               if (var4 != null) {
                  return false;
               }

               var4 = var6;
            } else {
               if (var6.getItem() != Items.WRITABLE_BOOK) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return var4 != null && var3 > 0;
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      int var2 = 0;
      ItemStack var3 = null;

      for(int var4 = 0; var4 < inventory.getSize(); ++var4) {
         ItemStack var5 = inventory.getStack(var4);
         if (var5 != null) {
            if (var5.getItem() == Items.WRITTEN_BOOK) {
               if (var3 != null) {
                  return null;
               }

               var3 = var5;
            } else {
               if (var5.getItem() != Items.WRITABLE_BOOK) {
                  return null;
               }

               ++var2;
            }
         }
      }

      if (var3 != null && var2 >= 1 && WrittenBookItem.getGeneration(var3) < 2) {
         ItemStack var6 = new ItemStack(Items.WRITTEN_BOOK, var2);
         var6.setNbt((NbtCompound)var3.getNbt().copy());
         var6.getNbt().putInt("generation", WrittenBookItem.getGeneration(var3) + 1);
         if (var3.hasCustomHoverName()) {
            var6.setHoverName(var3.getHoverName());
         }

         return var6;
      } else {
         return null;
      }
   }

   @Override
   public int getInputCount() {
      return 9;
   }

   @Override
   public ItemStack getOutput() {
      return null;
   }

   @Override
   public ItemStack[] getRemainder(CraftingInventory inventory) {
      ItemStack[] var2 = new ItemStack[inventory.getSize()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = inventory.getStack(var3);
         if (var4 != null && var4.getItem() instanceof WrittenBookItem) {
            var2[var3] = var4;
            break;
         }
      }

      return var2;
   }
}
