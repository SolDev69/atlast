package net.minecraft.entity.thrown;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.EndermiteEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnderPearlEntity extends ThrownEntity {
   public EnderPearlEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public EnderPearlEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng) {
      super(c_54ruxjwzt, c_97zulxhng);
   }

   @Environment(EnvType.CLIENT)
   public EnderPearlEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   protected void onCollision(HitResult result) {
      LivingEntity var2 = this.getOwner();
      if (result.entity != null) {
         result.entity.damage(DamageSource.thrownProjectile(this, var2), 0.0F);
      }

      for(int var3 = 0; var3 < 32; ++var3) {
         this.world
            .addParticle(
               ParticleType.PORTAL, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian()
            );
      }

      if (!this.world.isClient) {
         if (var2 instanceof ServerPlayerEntity) {
            ServerPlayerEntity var5 = (ServerPlayerEntity)var2;
            if (var5.networkHandler.getConnection().isOpen() && var5.world == this.world) {
               if (this.random.nextFloat() < 0.05F) {
                  EndermiteEntity var4 = new EndermiteEntity(this.world);
                  var4.m_64qjbbsko(true);
                  var4.refreshPositionAndAngles(var2.x, var2.y, var2.z, var2.yaw, var2.pitch);
                  this.world.addEntity(var4);
               }

               if (var2.hasVehicle()) {
                  var2.startRiding(null);
               }

               var2.refreshPosition(this.x, this.y, this.z);
               var2.fallDistance = 0.0F;
               var2.damage(DamageSource.FALL, 5.0F);
            }
         }

         this.remove();
      }
   }
}
