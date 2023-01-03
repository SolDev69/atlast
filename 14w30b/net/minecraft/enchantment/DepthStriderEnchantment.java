package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class DepthStriderEnchantment extends Enchantment {
   public DepthStriderEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.ARMOR_FEET);
      this.setName("waterWalker");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return level * 10;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
