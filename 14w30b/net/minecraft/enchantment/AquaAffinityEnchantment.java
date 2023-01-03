package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class AquaAffinityEnchantment extends Enchantment {
   public AquaAffinityEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.ARMOR_HEAD);
      this.setName("waterWorker");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 1;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + 40;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
