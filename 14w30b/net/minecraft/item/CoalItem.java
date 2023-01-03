package net.minecraft.item;

import java.util.List;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CoalItem extends Item {
   public CoalItem() {
      this.setStackable(true);
      this.setMaxDamage(0);
      this.setItemGroup(ItemGroup.MATERIALS);
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      return stack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      list.add(new ItemStack(item, 1, 0));
      list.add(new ItemStack(item, 1, 1));
   }
}
