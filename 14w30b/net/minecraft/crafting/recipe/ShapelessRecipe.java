package net.minecraft.crafting.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipe implements CraftingRecipe {
   private final ItemStack result;
   private final List inputs;

   public ShapelessRecipe(ItemStack result, List inputs) {
      this.result = result;
      this.inputs = inputs;
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
      ArrayList var3 = Lists.newArrayList(this.inputs);

      for(int var4 = 0; var4 < inventory.getHeight(); ++var4) {
         for(int var5 = 0; var5 < inventory.getWidth(); ++var5) {
            ItemStack var6 = inventory.getStack(var5, var4);
            if (var6 != null) {
               boolean var7 = false;

               for(ItemStack var9 : var3) {
                  if (var6.getItem() == var9.getItem() && (var9.getMetadata() == 32767 || var6.getMetadata() == var9.getMetadata())) {
                     var7 = true;
                     var3.remove(var9);
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }
         }
      }

      return var3.isEmpty();
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      return this.result.copy();
   }

   @Override
   public int getInputCount() {
      return this.inputs.size();
   }
}
