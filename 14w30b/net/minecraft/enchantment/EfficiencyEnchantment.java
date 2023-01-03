package net.minecraft.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;

public class EfficiencyEnchantment extends Enchantment {
   protected EfficiencyEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.DIGGER);
      this.setName("digging");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 1 + 10 * (level - 1);
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return super.getMinXpRequirement(level) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public boolean isValidTarget(ItemStack stack) {
      return stack.getItem() == Items.SHEARS ? true : super.isValidTarget(stack);
   }
}
