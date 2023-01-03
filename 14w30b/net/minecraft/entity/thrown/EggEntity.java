package net.minecraft.entity.thrown;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;

public class EggEntity extends ThrownEntity {
   public EggEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public EggEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng) {
      super(c_54ruxjwzt, c_97zulxhng);
   }

   public EggEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   protected void onCollision(HitResult result) {
      if (result.entity != null) {
         result.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
      }

      if (!this.world.isClient && this.random.nextInt(8) == 0) {
         byte var2 = 1;
         if (this.random.nextInt(32) == 0) {
            var2 = 4;
         }

         for(int var3 = 0; var3 < var2; ++var3) {
            ChickenEntity var4 = new ChickenEntity(this.world);
            var4.setBreedingAge(-24000);
            var4.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
            this.world.addEntity(var4);
         }
      }

      for(int var5 = 0; var5 < 8; ++var5) {
         this.world.addParticle(ParticleType.SNOWBALL, this.x, this.y, this.z, 0.0, 0.0, 0.0);
      }

      if (!this.world.isClient) {
         this.remove();
      }
   }
}
