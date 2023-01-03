package net.minecraft.entity.living.mob.water;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class WaterMobEntity extends MobEntity implements EntityCategoryProvider {
   public WaterMobEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   public boolean isWaterCreature() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      return true;
   }

   @Override
   public boolean m_52qkzdxky() {
      return this.world.canBuildIn(this.getBoundingBox(), this);
   }

   @Override
   public int getMinAmbientSoundDelay() {
      return 120;
   }

   @Override
   protected boolean canDespawn() {
      return true;
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      return 1 + this.world.random.nextInt(3);
   }

   @Override
   public void baseTick() {
      int var1 = this.getBreath();
      super.baseTick();
      if (this.isAlive() && !this.isInWater()) {
         this.setBreath(--var1);
         if (this.getBreath() == -20) {
            this.setBreath(0);
            this.damage(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setBreath(300);
      }
   }

   @Override
   public boolean hasLiquidCollision() {
      return false;
   }

   @Override
   public MobEntity.Environment m_84jincljh() {
      return MobEntity.Environment.IN_WATER;
   }
}
