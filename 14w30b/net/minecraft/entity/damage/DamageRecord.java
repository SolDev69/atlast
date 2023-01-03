package net.minecraft.entity.damage;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.text.Text;

public class DamageRecord {
   private final DamageSource source;
   private final int entityAge;
   private final float damage;
   private final float entityHealth;
   private final String fallDeathSuffix;
   private final float fallDistance;

   public DamageRecord(DamageSource source, int entityAge, float entityHealth, float damage, String fallDeathSuffix, float fallDistance) {
      this.source = source;
      this.entityAge = entityAge;
      this.damage = damage;
      this.entityHealth = entityHealth;
      this.fallDeathSuffix = fallDeathSuffix;
      this.fallDistance = fallDistance;
   }

   public DamageSource getDamageSource() {
      return this.source;
   }

   public float getDamage() {
      return this.damage;
   }

   public boolean isAttackerLiving() {
      return this.source.getAttacker() instanceof LivingEntity;
   }

   public String getFallDeathSuffix() {
      return this.fallDeathSuffix;
   }

   public Text getAttackerName() {
      return this.getDamageSource().getAttacker() == null ? null : this.getDamageSource().getAttacker().getDisplayName();
   }

   public float getFallDistance() {
      return this.source == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
   }
}
