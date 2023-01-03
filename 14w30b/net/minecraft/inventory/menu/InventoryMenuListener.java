package net.minecraft.inventory.menu;

import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface InventoryMenuListener {
   void updateMenu(InventoryMenu menu, List stacks);

   void onSlotChanged(InventoryMenu menu, int id, ItemStack stack);

   void onDataChanged(InventoryMenu menu, int id, int value);

   void updateData(InventoryMenu menu, Inventory inventory);
}
