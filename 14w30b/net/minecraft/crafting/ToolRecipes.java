package net.minecraft.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ToolRecipes {
   private String[][] patterns = new String[][]{{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}};
   private Object[][] recipes = new Object[][]{
      {Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT},
      {Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE},
      {Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.GOLDEN_SHOVEL},
      {Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.GOLDEN_AXE},
      {Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.GOLDEN_HOE}
   };

   public void register(CraftingManager manager) {
      for(int var2 = 0; var2 < this.recipes[0].length; ++var2) {
         Object var3 = this.recipes[0][var2];

         for(int var4 = 0; var4 < this.recipes.length - 1; ++var4) {
            Item var5 = (Item)this.recipes[var4 + 1][var2];
            manager.registerShaped(new ItemStack(var5), this.patterns[var4], '#', Items.STICK, 'X', var3);
         }
      }

      manager.registerShaped(new ItemStack(Items.SHEARS), " #", "# ", '#', Items.IRON_INGOT);
   }
}
