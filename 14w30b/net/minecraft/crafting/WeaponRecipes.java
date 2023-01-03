package net.minecraft.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WeaponRecipes {
   private String[][] patterns = new String[][]{{"X", "X", "#"}};
   private Object[][] recipes = new Object[][]{
      {Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT},
      {Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD}
   };

   public void register(CraftingManager manager) {
      for(int var2 = 0; var2 < this.recipes[0].length; ++var2) {
         Object var3 = this.recipes[0][var2];

         for(int var4 = 0; var4 < this.recipes.length - 1; ++var4) {
            Item var5 = (Item)this.recipes[var4 + 1][var2];
            manager.registerShaped(new ItemStack(var5), this.patterns[var4], '#', Items.STICK, 'X', var3);
         }
      }

      manager.registerShaped(new ItemStack(Items.BOW, 1), " #X", "# X", " #X", 'X', Items.STRING, '#', Items.STICK);
      manager.registerShaped(new ItemStack(Items.ARROW, 4), "X", "#", "Y", 'Y', Items.FEATHER, 'X', Items.FLINT, '#', Items.STICK);
   }
}
