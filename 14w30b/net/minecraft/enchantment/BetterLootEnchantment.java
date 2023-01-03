package net.minecraft.enchantment;

import net.minecraft.resource.Identifier;

public class BetterLootEnchantment extends Enchantment {
   protected BetterLootEnchantment(int i, Identifier c_07ipdbewr, int j, EnchantmentTarget c_88lmatnkp) {
      super(i, c_07ipdbewr, j, c_88lmatnkp);
      if (c_88lmatnkp == EnchantmentTarget.DIGGER) {
         this.setName("lootBonusDigger");
      } else if (c_88lmatnkp == EnchantmentTarget.FISHING_ROD) {
         this.setName("lootBonusFishing");
      } else {
         this.setName("lootBonus");
      }
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

   @Override
   public boolean checkCompatibility(Enchantment other) {
      return super.checkCompatibility(other) && other.id != SILK_TOUCH.id;
   }
}
