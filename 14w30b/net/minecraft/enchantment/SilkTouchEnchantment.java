package net.minecraft.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;

public class SilkTouchEnchantment extends Enchantment {
   protected SilkTouchEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.DIGGER);
      this.setName("untouching");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 15;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return super.getMinXpRequirement(level) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }

   @Override
   public boolean checkCompatibility(Enchantment other) {
      return super.checkCompatibility(other) && other.id != FORTUNE.id;
   }

   @Override
   public boolean isValidTarget(ItemStack stack) {
      return stack.getItem() == Items.SHEARS ? true : super.isValidTarget(stack);
   }
}
