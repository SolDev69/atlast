package net.minecraft.crafting.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class DyeArmorRecipe implements CraftingRecipe {
   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      ItemStack var3 = null;
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < inventory.getSize(); ++var5) {
         ItemStack var6 = inventory.getStack(var5);
         if (var6 != null) {
            if (var6.getItem() instanceof ArmorItem) {
               ArmorItem var7 = (ArmorItem)var6.getItem();
               if (var7.getMaterial() != ArmorItem.Material.CLOTH || var3 != null) {
                  return false;
               }

               var3 = var6;
            } else {
               if (var6.getItem() != Items.DYE) {
                  return false;
               }

               var4.add(var6);
            }
         }
      }

      return var3 != null && !var4.isEmpty();
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      ItemStack var2 = null;
      int[] var3 = new int[3];
      int var4 = 0;
      int var5 = 0;
      ArmorItem var6 = null;

      for(int var7 = 0; var7 < inventory.getSize(); ++var7) {
         ItemStack var8 = inventory.getStack(var7);
         if (var8 != null) {
            if (var8.getItem() instanceof ArmorItem) {
               var6 = (ArmorItem)var8.getItem();
               if (var6.getMaterial() != ArmorItem.Material.CLOTH || var2 != null) {
                  return null;
               }

               var2 = var8.copy();
               var2.size = 1;
               if (var6.hasColor(var8)) {
                  int var9 = var6.getColor(var2);
                  float var10 = (float)(var9 >> 16 & 0xFF) / 255.0F;
                  float var11 = (float)(var9 >> 8 & 0xFF) / 255.0F;
                  float var12 = (float)(var9 & 0xFF) / 255.0F;
                  var4 = (int)((float)var4 + Math.max(var10, Math.max(var11, var12)) * 255.0F);
                  var3[0] = (int)((float)var3[0] + var10 * 255.0F);
                  var3[1] = (int)((float)var3[1] + var11 * 255.0F);
                  var3[2] = (int)((float)var3[2] + var12 * 255.0F);
                  ++var5;
               }
            } else {
               if (var8.getItem() != Items.DYE) {
                  return null;
               }

               float[] var17 = SheepEntity.getColorRgb(DyeColor.byMetadata(var8.getMetadata()));
               int var20 = (int)(var17[0] * 255.0F);
               int var22 = (int)(var17[1] * 255.0F);
               int var24 = (int)(var17[2] * 255.0F);
               var4 += Math.max(var20, Math.max(var22, var24));
               var3[0] += var20;
               var3[1] += var22;
               var3[2] += var24;
               ++var5;
            }
         }
      }

      if (var6 == null) {
         return null;
      } else {
         int var13 = var3[0] / var5;
         int var15 = var3[1] / var5;
         int var18 = var3[2] / var5;
         float var21 = (float)var4 / (float)var5;
         float var23 = (float)Math.max(var13, Math.max(var15, var18));
         var13 = (int)((float)var13 * var21 / var23);
         var15 = (int)((float)var15 * var21 / var23);
         var18 = (int)((float)var18 * var21 / var23);
         int var25 = (var13 << 8) + var15;
         var25 = (var25 << 8) + var18;
         var6.setColor(var2, var25);
         return var2;
      }
   }

   @Override
   public int getInputCount() {
      return 10;
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
