package net.minecraft.crafting.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RepairRecipe implements CraftingRecipe {
   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < inventory.getSize(); ++var4) {
         ItemStack var5 = inventory.getStack(var4);
         if (var5 != null) {
            var3.add(var5);
            if (var3.size() > 1) {
               ItemStack var6 = (ItemStack)var3.get(0);
               if (var5.getItem() != var6.getItem() || var6.size != 1 || var5.size != 1 || !var6.getItem().isDamageable()) {
                  return false;
               }
            }
         }
      }

      return var3.size() == 2;
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < inventory.getSize(); ++var3) {
         ItemStack var4 = inventory.getStack(var3);
         if (var4 != null) {
            var2.add(var4);
            if (var2.size() > 1) {
               ItemStack var5 = (ItemStack)var2.get(0);
               if (var4.getItem() != var5.getItem() || var5.size != 1 || var4.size != 1 || !var5.getItem().isDamageable()) {
                  return null;
               }
            }
         }
      }

      if (var2.size() == 2) {
         ItemStack var10 = (ItemStack)var2.get(0);
         ItemStack var11 = (ItemStack)var2.get(1);
         if (var10.getItem() == var11.getItem() && var10.size == 1 && var11.size == 1 && var10.getItem().isDamageable()) {
            Item var12 = var10.getItem();
            int var6 = var12.getMaxDamage() - var10.getDamage();
            int var7 = var12.getMaxDamage() - var11.getDamage();
            int var8 = var6 + var7 + var12.getMaxDamage() * 5 / 100;
            int var9 = var12.getMaxDamage() - var8;
            if (var9 < 0) {
               var9 = 0;
            }

            return new ItemStack(var10.getItem(), 1, var9);
         }
      }

      return null;
   }

   @Override
   public int getInputCount() {
      return 4;
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
         if (var4 != null && var4.getItem().hasRecipeRemainder()) {
            var2[var3] = new ItemStack(var4.getItem().getRecipeRemainder());
         }
      }

      return var2;
   }
}
