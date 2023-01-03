package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;

public class ThornsEnchantment extends Enchantment {
   public ThornsEnchantment(int rawId, Identifier id, int type) {
      super(rawId, id, type, EnchantmentTarget.ARMOR_TORSO);
      this.setName("thorns");
   }

   @Override
   public int getMinXpRequirement(int level) {
      return 10 + 20 * (level - 1);
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
      return stack.getItem() instanceof ArmorItem ? true : super.isValidTarget(stack);
   }

   @Override
   public void applyProtectionWildcard(LivingEntity target, Entity attacker, int level) {
      Random var4 = target.getRandom();
      ItemStack var5 = EnchantmentHelper.getFirstArmorStackWithEnchantment(Enchantment.THORNS, target);
      if (shouldDamageAttacker(level, var4)) {
         attacker.damage(DamageSource.thorns(target), (float)getDamageAmount(level, var4));
         attacker.playSound("damage.thorns", 0.5F, 1.0F);
         if (var5 != null) {
            var5.damageAndBreak(3, target);
         }
      } else if (var5 != null) {
         var5.damageAndBreak(1, target);
      }
   }

   public static boolean shouldDamageAttacker(int level, Random random) {
      if (level <= 0) {
         return false;
      } else {
         return random.nextFloat() < 0.15F * (float)level;
      }
   }

   public static int getDamageAmount(int level, Random random) {
      return level > 10 ? level - 10 : 1 + random.nextInt(4);
   }
}
