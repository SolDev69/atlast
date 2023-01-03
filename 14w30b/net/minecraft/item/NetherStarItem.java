package net.minecraft.item;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class NetherStarItem extends Item {
   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasEnchantmentGlint(ItemStack stack) {
      return true;
   }
}
