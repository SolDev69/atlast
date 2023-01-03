package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class RespirationEnchantment extends Enchantment {
   public RespirationEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.ARMOR_HEAD);
      this.setName("oxygen");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 10 * level;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 30;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
