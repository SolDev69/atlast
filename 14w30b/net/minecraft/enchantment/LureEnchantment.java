package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class LureEnchantment extends Enchantment {
   protected LureEnchantment(int i, Identifier c_07ipdbewr, int j, EnchantmentTarget c_88lmatnkp) {
      super(i, c_07ipdbewr, j, c_88lmatnkp);
      this.setName("fishingSpeed");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 15 + (level - 1) * 9;
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return super.getMinXpRequirement(level) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
