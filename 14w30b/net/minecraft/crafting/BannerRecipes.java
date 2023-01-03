package net.minecraft.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.crafting.recipe.CraftingRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public class BannerRecipes {
   void register(CraftingManager manager) {
      for(DyeColor var5 : DyeColor.values()) {
         manager.registerShaped(
            new ItemStack(Items.BANNER, 1, var5.getMetadata()), "###", "###", " | ", '#', new ItemStack(Blocks.WOOL, 1, var5.getIndex()), '|', Items.STICK
         );
      }

      manager.register(new BannerRecipes.DuplicatePatternRecipe());
      manager.register(new BannerRecipes.AddPatternRecipe());
   }

   static class AddPatternRecipe implements CraftingRecipe {
      private AddPatternRecipe() {
      }

      @Override
      public boolean matches(CraftingInventory inventory, World world) {
         boolean var3 = false;

         for(int var4 = 0; var4 < inventory.getSize(); ++var4) {
            ItemStack var5 = inventory.getStack(var4);
            if (var5 != null && var5.getItem() == Items.BANNER) {
               if (var3) {
                  return false;
               }

               if (BannerBlockEntity.getPatternCount(var5) >= 6) {
                  return false;
               }

               var3 = true;
            }
         }

         if (!var3) {
            return false;
         } else {
            return this.getPattern(inventory) != null;
         }
      }

      @Override
      public ItemStack getResult(CraftingInventory inventory) {
         ItemStack var2 = null;

         for(int var3 = 0; var3 < inventory.getSize(); ++var3) {
            ItemStack var4 = inventory.getStack(var3);
            if (var4 != null && var4.getItem() == Items.BANNER) {
               var2 = var4.copy();
               var2.size = 1;
               break;
            }
         }

         BannerBlockEntity.Pattern var8 = this.getPattern(inventory);
         if (var8 != null) {
            int var9 = 0;

            for(int var5 = 0; var5 < inventory.getSize(); ++var5) {
               ItemStack var6 = inventory.getStack(var5);
               if (var6 != null && var6.getItem() == Items.DYE) {
                  var9 = var6.getMetadata();
                  break;
               }
            }

            NbtCompound var10 = var2.getNbt("BlockEntityTag", true);
            NbtList var11 = null;
            if (var10.isType("Patterns", 9)) {
               var11 = var10.getList("Patterns", 10);
            } else {
               var11 = new NbtList();
               var10.put("Patterns", var11);
            }

            NbtCompound var7 = new NbtCompound();
            var7.putString("Pattern", var8.getId());
            var7.putInt("Color", var9);
            var11.add(var7);
         }

         return var2;
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

      private BannerBlockEntity.Pattern getPattern(CraftingInventory inventory) {
         for(BannerBlockEntity.Pattern var5 : BannerBlockEntity.Pattern.values()) {
            if (var5.hasPattern()) {
               boolean var6 = true;
               if (var5.hasItem()) {
                  boolean var7 = false;
                  boolean var8 = false;

                  for(int var9 = 0; var9 < inventory.getSize() && var6; ++var9) {
                     ItemStack var10 = inventory.getStack(var9);
                     if (var10 != null && var10.getItem() != Items.BANNER) {
                        if (var10.getItem() == Items.DYE) {
                           if (var8) {
                              var6 = false;
                              break;
                           }

                           var8 = true;
                        } else {
                           if (var7 || !var10.matchesItem(var5.getItem())) {
                              var6 = false;
                              break;
                           }

                           var7 = true;
                        }
                     }
                  }

                  if (!var7) {
                     var6 = false;
                  }
               } else {
                  int var12 = -1;

                  for(int var13 = 0; var13 < inventory.getSize() && var6; ++var13) {
                     int var14 = var13 / 3;
                     int var15 = var13 % 3;
                     ItemStack var11 = inventory.getStack(var13);
                     if (var11 != null && var11.getItem() != Items.BANNER) {
                        if (var11.getItem() != Items.DYE) {
                           var6 = false;
                           break;
                        }

                        if (var12 != -1 && var12 != var11.getMetadata()) {
                           var6 = false;
                           break;
                        }

                        if (var5.getPatterns()[var14].charAt(var15) == ' ') {
                           var6 = false;
                           break;
                        }

                        var12 = var11.getMetadata();
                     } else if (var5.getPatterns()[var14].charAt(var15) != ' ') {
                        var6 = false;
                        break;
                     }
                  }
               }

               if (var6) {
                  return var5;
               }
            }
         }

         return null;
      }
   }

   static class DuplicatePatternRecipe implements CraftingRecipe {
      private DuplicatePatternRecipe() {
      }

      @Override
      public boolean matches(CraftingInventory inventory, World world) {
         ItemStack var3 = null;
         ItemStack var4 = null;

         for(int var5 = 0; var5 < inventory.getSize(); ++var5) {
            ItemStack var6 = inventory.getStack(var5);
            if (var6 != null) {
               if (var6.getItem() != Items.BANNER) {
                  return false;
               }

               if (var3 != null && var4 != null) {
                  return false;
               }

               int var7 = BannerBlockEntity.getBaseColor(var6);
               boolean var8 = BannerBlockEntity.getPatternCount(var6) > 0;
               if (var3 != null) {
                  if (var8) {
                     return false;
                  }

                  if (var7 != BannerBlockEntity.getBaseColor(var3)) {
                     return false;
                  }

                  var4 = var6;
               } else if (var4 != null) {
                  if (!var8) {
                     return false;
                  }

                  if (var7 != BannerBlockEntity.getBaseColor(var4)) {
                     return false;
                  }

                  var3 = var6;
               } else if (var8) {
                  var3 = var6;
               } else {
                  var4 = var6;
               }
            }
         }

         return var3 != null && var4 != null;
      }

      @Override
      public ItemStack getResult(CraftingInventory inventory) {
         for(int var2 = 0; var2 < inventory.getSize(); ++var2) {
            ItemStack var3 = inventory.getStack(var2);
            if (var3 != null && BannerBlockEntity.getPatternCount(var3) > 0) {
               ItemStack var4 = var3.copy();
               var4.size = 1;
               return var4;
            }
         }

         return null;
      }

      @Override
      public int getInputCount() {
         return 2;
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
            if (var4 != null) {
               if (var4.getItem().hasRecipeRemainder()) {
                  var2[var3] = new ItemStack(var4.getItem().getRecipeRemainder());
               } else if (var4.hasNbt() && BannerBlockEntity.getPatternCount(var4) > 0) {
                  var2[var3] = var4.copy();
                  var2[var3].size = 1;
               }
            }
         }

         return var2;
      }
   }
}
