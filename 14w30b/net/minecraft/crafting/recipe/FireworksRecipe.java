package net.minecraft.crafting.recipe;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public class FireworksRecipe implements CraftingRecipe {
   private ItemStack result;

   @Override
   public boolean matches(CraftingInventory inventory, World world) {
      this.result = null;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      for(int var9 = 0; var9 < inventory.getSize(); ++var9) {
         ItemStack var10 = inventory.getStack(var9);
         if (var10 != null) {
            if (var10.getItem() == Items.GUNPOWDER) {
               ++var4;
            } else if (var10.getItem() == Items.FIREWORKS_CHARGE) {
               ++var6;
            } else if (var10.getItem() == Items.DYE) {
               ++var5;
            } else if (var10.getItem() == Items.PAPER) {
               ++var3;
            } else if (var10.getItem() == Items.GLOWSTONE_DUST) {
               ++var7;
            } else if (var10.getItem() == Items.DIAMOND) {
               ++var7;
            } else if (var10.getItem() == Items.FIRE_CHARGE) {
               ++var8;
            } else if (var10.getItem() == Items.FEATHER) {
               ++var8;
            } else if (var10.getItem() == Items.GOLD_NUGGET) {
               ++var8;
            } else {
               if (var10.getItem() != Items.SKULL) {
                  return false;
               }

               ++var8;
            }
         }
      }

      var7 += var5 + var8;
      if (var4 > 3 || var3 > 1) {
         return false;
      } else if (var4 >= 1 && var3 == 1 && var7 == 0) {
         this.result = new ItemStack(Items.FIREWORKS);
         if (var6 > 0) {
            NbtCompound var18 = new NbtCompound();
            NbtCompound var22 = new NbtCompound();
            NbtList var26 = new NbtList();

            for(int var27 = 0; var27 < inventory.getSize(); ++var27) {
               ItemStack var29 = inventory.getStack(var27);
               if (var29 != null && var29.getItem() == Items.FIREWORKS_CHARGE && var29.hasNbt() && var29.getNbt().isType("Explosion", 10)) {
                  var26.add(var29.getNbt().getCompound("Explosion"));
               }
            }

            var22.put("Explosions", var26);
            var22.putByte("Flight", (byte)var4);
            var18.put("Fireworks", var22);
            this.result.setNbt(var18);
         }

         return true;
      } else if (var4 == 1 && var3 == 0 && var6 == 0 && var5 > 0 && var8 <= 1) {
         this.result = new ItemStack(Items.FIREWORKS_CHARGE);
         NbtCompound var17 = new NbtCompound();
         NbtCompound var21 = new NbtCompound();
         byte var25 = 0;
         ArrayList var12 = Lists.newArrayList();

         for(int var13 = 0; var13 < inventory.getSize(); ++var13) {
            ItemStack var14 = inventory.getStack(var13);
            if (var14 != null) {
               if (var14.getItem() == Items.DYE) {
                  var12.add(DyeItem.COLORS[var14.getMetadata()]);
               } else if (var14.getItem() == Items.GLOWSTONE_DUST) {
                  var21.putBoolean("Flicker", true);
               } else if (var14.getItem() == Items.DIAMOND) {
                  var21.putBoolean("Trail", true);
               } else if (var14.getItem() == Items.FIRE_CHARGE) {
                  var25 = 1;
               } else if (var14.getItem() == Items.FEATHER) {
                  var25 = 4;
               } else if (var14.getItem() == Items.GOLD_NUGGET) {
                  var25 = 2;
               } else if (var14.getItem() == Items.SKULL) {
                  var25 = 3;
               }
            }
         }

         int[] var28 = new int[var12.size()];

         for(int var30 = 0; var30 < var28.length; ++var30) {
            var28[var30] = var12.get(var30);
         }

         var21.putIntArray("Colors", var28);
         var21.putByte("Type", var25);
         var17.put("Explosion", var21);
         this.result.setNbt(var17);
         return true;
      } else if (var4 == 0 && var3 == 0 && var6 == 1 && var5 > 0 && var5 == var7) {
         ArrayList var16 = Lists.newArrayList();

         for(int var19 = 0; var19 < inventory.getSize(); ++var19) {
            ItemStack var11 = inventory.getStack(var19);
            if (var11 != null) {
               if (var11.getItem() == Items.DYE) {
                  var16.add(DyeItem.COLORS[var11.getMetadata()]);
               } else if (var11.getItem() == Items.FIREWORKS_CHARGE) {
                  this.result = var11.copy();
                  this.result.size = 1;
               }
            }
         }

         int[] var20 = new int[var16.size()];

         for(int var23 = 0; var23 < var20.length; ++var23) {
            var20[var23] = var16.get(var23);
         }

         if (this.result != null && this.result.hasNbt()) {
            NbtCompound var24 = this.result.getNbt().getCompound("Explosion");
            if (var24 == null) {
               return false;
            } else {
               var24.putIntArray("FadeColors", var20);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack getResult(CraftingInventory inventory) {
      return this.result.copy();
   }

   @Override
   public int getInputCount() {
      return 10;
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
}
