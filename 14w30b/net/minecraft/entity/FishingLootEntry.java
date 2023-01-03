package net.minecraft.entity;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedPicker;

public class FishingLootEntry extends WeightedPicker.Entry {
   private final ItemStack stack;
   private float damage;
   private boolean enchantable;

   public FishingLootEntry(ItemStack stack, int weight) {
      super(weight);
      this.stack = stack;
   }

   public ItemStack getItemStack(Random random) {
      ItemStack var2 = this.stack.copy();
      if (this.damage > 0.0F) {
         int var3 = (int)(this.damage * (float)this.stack.getMaxDamage());
         int var4 = var2.getMaxDamage() - random.nextInt(random.nextInt(var3) + 1);
         if (var4 > var3) {
            var4 = var3;
         }

         if (var4 < 1) {
            var4 = 1;
         }

         var2.setDamage(var4);
      }

      if (this.enchantable) {
         EnchantmentHelper.addRandomEnchantment(random, var2, 30);
      }

      return var2;
   }

   public FishingLootEntry setDamage(float damage) {
      this.damage = damage;
      return this;
   }

   public FishingLootEntry setEnchantable() {
      this.enchantable = true;
      return this;
   }
}
