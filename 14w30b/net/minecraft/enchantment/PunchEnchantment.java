package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class PunchEnchantment extends Enchantment {
   public PunchEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.BOW);
      this.setName("arrowKnockback");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 12 + (level - 1) * 20;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 25;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
