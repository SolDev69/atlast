package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
   private static final String[] TYPE_NAMES = new String[]{"all", "fire", "fall", "explosion", "projectile"};
   private static final int[] MIN_XP = new int[]{1, 10, 5, 5, 3};
   private static final int[] XP_MODIFIER = new int[]{11, 8, 6, 8, 6};
   private static final int[] MAX_XP = new int[]{20, 12, 10, 12, 15};
   public final int protectionType;

   public ProtectionEnchantment(int rawId, Identifier id, int type, int protectionType) {
      super(rawId, id, type, EnchantmentTarget.ARMOR);
      this.protectionType = protectionType;
      if (protectionType == 2) {
         this.target = EnchantmentTarget.ARMOR_FEET;
      }
   }

   @Override
   public int getMinXpRequirement(int level) {
      return MIN_XP[this.protectionType] + (level - 1) * XP_MODIFIER[this.protectionType];
   }

   @Override
   public int getMaxXpRequirement(int level) {
      return this.getMinXpRequirement(level) + MAX_XP[this.protectionType];
   }

   @Override
   public int getMaxLevel() {
      return 4;
   }

   @Override
   public int getExtraProtection(int level, DamageSource source) {
      if (source.isOutOfWorld()) {
         return 0;
      } else {
         float var3 = (float)(6 + level * level) / 3.0F;
         if (this.protectionType == 0) {
            return MathHelper.floor(var3 * 0.75F);
         } else if (this.protectionType == 1 && source.isFire()) {
            return MathHelper.floor(var3 * 1.25F);
         } else if (this.protectionType == 2 && source == DamageSource.FALL) {
            return MathHelper.floor(var3 * 2.5F);
         } else if (this.protectionType == 3 && source.isExplosive()) {
            return MathHelper.floor(var3 * 1.5F);
         } else {
            return this.protectionType == 4 && source.isProjectile() ? MathHelper.floor(var3 * 1.5F) : 0;
         }
      }
   }

   @Override
   public String getName() {
      return "enchantment.protect." + TYPE_NAMES[this.protectionType];
   }

   @Override
   public boolean checkCompatibility(Enchantment other) {
      if (other instanceof ProtectionEnchantment) {
         ProtectionEnchantment var2 = (ProtectionEnchantment)other;
         if (var2.protectionType == this.protectionType) {
            return false;
         } else {
            return this.protectionType == 2 || var2.protectionType == 2;
         }
      } else {
         return super.checkCompatibility(other);
      }
   }

   public static int modifyOnFireTimer(Entity entity, int ticks) {
      int var2 = EnchantmentHelper.getHighestEnchantmentLevel(Enchantment.FIRE_PROTECTION.id, entity.getEquipmentStacks());
      if (var2 > 0) {
         ticks -= MathHelper.floor((float)ticks * (float)var2 * 0.15F);
      }

      return ticks;
   }

   public static double modifyExplosionDamage(Entity entity, double damage) {
      int var3 = EnchantmentHelper.getHighestEnchantmentLevel(Enchantment.BLAST_PROTECTION.id, entity.getEquipmentStacks());
      if (var3 > 0) {
         damage -= (double)MathHelper.floor(damage * (double)((float)var3 * 0.15F));
      }

      return damage;
   }
}
