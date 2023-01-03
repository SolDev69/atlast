package net.minecraft.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MineralRecipes {
   private Object[][] recipes = new Object[][]{
      {Blocks.GOLD_BLOCK, new ItemStack(Items.GOLD_INGOT, 9)},
      {Blocks.IRON_BLOCK, new ItemStack(Items.IRON_INGOT, 9)},
      {Blocks.DIAMOND_BLOCK, new ItemStack(Items.DIAMOND, 9)},
      {Blocks.EMERALD_BLOCK, new ItemStack(Items.EMERALD, 9)},
      {Blocks.LAPIS_BLOCK, new ItemStack(Items.DYE, 9, DyeColor.BLUE.getMetadata())},
      {Blocks.REDSTONE_BLOCK, new ItemStack(Items.REDSTONE, 9)},
      {Blocks.COAL_BLOCK, new ItemStack(Items.COAL, 9, 0)},
      {Blocks.HAY, new ItemStack(Items.WHEAT, 9)},
      {Blocks.SLIME, new ItemStack(Items.SLIME_BALL, 9)}
   };

   public void register(CraftingManager manager) {
      for(int var2 = 0; var2 < this.recipes.length; ++var2) {
         Block var3 = (Block)this.recipes[var2][0];
         ItemStack var4 = (ItemStack)this.recipes[var2][1];
         manager.registerShaped(new ItemStack(var3), "###", "###", "###", '#', var4);
         manager.registerShaped(var4, "#", '#', var3);
      }

      manager.registerShaped(new ItemStack(Items.GOLD_INGOT), "###", "###", "###", '#', Items.GOLD_NUGGET);
      manager.registerShaped(new ItemStack(Items.GOLD_NUGGET, 9), "#", '#', Items.GOLD_INGOT);
   }
}
