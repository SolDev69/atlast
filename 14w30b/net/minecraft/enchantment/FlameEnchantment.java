package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class FlameEnchantment extends Enchantment {
   public FlameEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.BOW);
      this.setName("arrowFire");
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
