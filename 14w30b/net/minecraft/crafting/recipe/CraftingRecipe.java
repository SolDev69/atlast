package net.minecraft.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CraftingRecipe {
   boolean matches(CraftingInventory inventory, World world);

   ItemStack getResult(CraftingInventory inventory);

   int getInputCount();

   ItemStack getOutput();

   ItemStack[] getRemainder(CraftingInventory inventory);
}
