package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class InfinityEnchantment extends Enchantment {
   public InfinityEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.BOW);
      this.setName("arrowInfinite");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 20;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
