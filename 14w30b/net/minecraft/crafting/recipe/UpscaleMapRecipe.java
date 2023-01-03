package net.minecraft.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.map.SavedMapData;

public class UpscaleMapRecipe extends ShapedRecipe {
   public UpscaleMapRecipe() {
      super(
         3,
         3,
         new ItemStack[]{
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.FILLED_MAP, 0, 32767),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER),
            new ItemStack(Items.PAPER)
         },
         new ItemStack(Items.MAP, 0, 0)
      );
   }

   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      if (!super.matches(inventory, world)) {
         return false;
      } else {
         ItemStack var3 = null;

         for(int var4 = 0; var4 < inventory.getSize() && var3 == null; ++var4) {
            ItemStack var5 = inventory.getStack(var4);
            if (var5 != null && var5.getItem() == Items.FILLED_MAP) {
               var3 = var5;
            }
         }

         if (var3 == null) {
            return false;
         } else {
            SavedMapData var6 = Items.FILLED_MAP.getSavedMapData(var3, world);
            if (var6 == null) {
               return false;
            } else {
               return var6.scale < 4;
            }
         }
      }
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      ItemStack var2 = null;

      for(int var3 = 0; var3 < inventory.getSize() && var2 == null; ++var3) {
         ItemStack var4 = inventory.getStack(var3);
         if (var4 != null && var4.getItem() == Items.FILLED_MAP) {
            var2 = var4;
         }
      }

      var2 = var2.copy();
      var2.size = 1;
      if (var2.getNbt() == null) {
         var2.setNbt(new NbtCompound());
      }

      var2.getNbt().putBoolean("map_is_scaling", true);
      return var2;
   }
}
