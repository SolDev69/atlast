package net.minecraft.client.gui.screen.inventory.menu;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.InventoryMenuListener;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreativeInventoryListener implements InventoryMenuListener {
   private final MinecraftClient client;

   public CreativeInventoryListener(MinecraftClient client) {
      this.client = client;
   }

   @Override
   public void updateMenu(InventoryMenu menu, List stacks) {
   }

   @Override
   public void onSlotChanged(InventoryMenu menu, int id, ItemStack stack) {
      this.client.interactionManager.addStackToCreativeMenu(stack, id);
   }

   @Override
   public void onDataChanged(InventoryMenu menu, int id, int value) {
   }

   @Override
   public void updateData(InventoryMenu menu, Inventory inventory) {
   }
}
