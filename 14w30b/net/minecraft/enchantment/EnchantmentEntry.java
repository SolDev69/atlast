package net.minecraft.enchantment;

import net.minecraft.util.WeightedPicker;

public class EnchantmentEntry extends WeightedPicker.Entry {
   public final Enchantment enchantment;
   public final int level;

   public EnchantmentEntry(Enchantment id, int level) {
      super(id.getType());
      this.enchantment = id;
      this.level = level;
   }
}
