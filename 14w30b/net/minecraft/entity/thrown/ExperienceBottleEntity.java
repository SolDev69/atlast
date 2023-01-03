package net.minecraft.entity.thrown;

import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ThrownEntity {
   public ExperienceBottleEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public ExperienceBottleEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng) {
      super(c_54ruxjwzt, c_97zulxhng);
   }

   public ExperienceBottleEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   protected float getGravity() {
      return 0.07F;
   }

   @Override
   protected float getSpeed() {
      return 0.7F;
   }

   @Override
   protected float getStartPitchOffset() {
      return -20.0F;
   }

   @Override
   protected void onCollision(HitResult result) {
      if (!this.world.isClient) {
         this.world.doEvent(2002, new BlockPos(this), 0);
         int var2 = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

         while(var2 > 0) {
            int var3 = XpOrbEntity.roundSize(var2);
            var2 -= var3;
            this.world.addEntity(new XpOrbEntity(this.world, this.x, this.y, this.z, var3));
         }

         this.remove();
      }
   }
}
