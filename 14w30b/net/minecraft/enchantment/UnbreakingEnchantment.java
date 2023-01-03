package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;

public class UnbreakingEnchantment extends Enchantment {
   protected UnbreakingEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.BREAKABLE);
      this.setName("durability");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 5 + (level - 1) * 8;
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
   public boolean isValidTarget(ItemStack stack) {
      return stack.isDamageable() ? true : super.isValidTarget(stack);
   }

   public static boolean shouldReduceDamage(ItemStack item, int level, Random random) {
      if (item.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F) {
         return false;
      } else {
         return random.nextInt(level + 1) > 0;
      }
   }
}
