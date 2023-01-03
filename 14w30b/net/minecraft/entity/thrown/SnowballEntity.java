package net.minecraft.entity.thrown;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.BlazeEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;

public class SnowballEntity extends ThrownEntity {
   public SnowballEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public SnowballEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng) {
      super(c_54ruxjwzt, c_97zulxhng);
   }

   public SnowballEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   protected void onCollision(HitResult result) {
      if (result.entity != null) {
         byte var2 = 0;
         if (result.entity instanceof BlazeEntity) {
            var2 = 3;
         }

         result.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)var2);
      }

      for(int var3 = 0; var3 < 8; ++var3) {
         this.world.addParticle(ParticleType.SNOWBALL, this.x, this.y, this.z, 0.0, 0.0, 0.0);
      }

      if (!this.world.isClient) {
         this.remove();
      }
   }
}
