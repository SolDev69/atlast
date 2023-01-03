package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class PowerEnchantment extends Enchantment {
   public PowerEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.BOW);
      this.setName("arrowDamage");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 1 + (level - 1) * 10;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }
}
