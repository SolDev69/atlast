package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;

public class DamageEnchantment extends Enchantment {
   private static final String[] TARGET_NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_XP = new int[]{1, 5, 5};
   private static final int[] XP_MODIFIER = new int[]{11, 8, 8};
   private static final int[] MAX_XP = new int[]{20, 20, 20};
   public final int target;

   public DamageEnchantment(int rawId, Identifier id, int type, int target) {
      super(rawId, id, type, EnchantmentTarget.WEAPON);
      this.target = target;
   }

   @Override
   public int getMinXpRequirement(int level) {
      return MIN_XP[this.target] + (level - 1) * XP_MODIFIER[this.target];
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + MAX_XP[this.target];
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public float getExtraDamage(int level, LivingEntityType entity) {
      if (this.target == 0) {
         return (float)level * 1.25F;
      } else if (this.target == 1 && entity == LivingEntityType.UNDEAD) {
         return (float)level * 2.5F;
      } else {
         return this.target == 2 && entity == LivingEntityType.ARTHROPOD ? (float)level * 2.5F : 0.0F;
      }
   }

   @Override
   public String getName() {
      return "enchantment.damage." + TARGET_NAMES[this.target];
   }

   @Override
   public boolean checkCompatibility(Enchantment other) {
      return !(other instanceof DamageEnchantment);
   }

   @Override
   public boolean isValidTarget(ItemStack stack) {
      return stack.getItem() instanceof AxeItem ? true : super.isValidTarget(stack);
   }

   @Override
   public void applyDamageWildcard(LivingEntity attacker, Entity target, int level) {
      if (target instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)target;
         if (this.target == 2 && var4.getMobType() == LivingEntityType.ARTHROPOD) {
            int var5 = 20 + attacker.getRandom().nextInt(10 * level);
            var4.addStatusEffect(new StatusEffectInstance(StatusEffect.SLOWNESS.id, var5, 3));
         }
      }
   }
}
