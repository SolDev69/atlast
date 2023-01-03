package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FollowOwnerGoal extends Goal {
   private TameableEntity pet;
   private LivingEntity owner;
   World world;
   private double speed;
   private EntityNavigation navigation;
   private int updateTimer;
   float maxDistance;
   float minDistance;
   private boolean isInWater;

   public FollowOwnerGoal(TameableEntity pet, double speed, float minDistance, float g) {
      this.pet = pet;
      this.world = pet.world;
      this.speed = speed;
      this.navigation = pet.getNavigation();
      this.minDistance = minDistance;
      this.maxDistance = g;
      this.setControls(3);
      if (!(pet.getNavigation() instanceof MobEntityNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean canStart() {
      LivingEntity var1 = this.pet.getOwner();
      if (var1 == null) {
         return false;
      } else if (this.pet.isSitting()) {
         return false;
      } else if (this.pet.getSquaredDistanceTo(var1) < (double)(this.minDistance * this.minDistance)) {
         return false;
      } else {
         this.owner = var1;
         return true;
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.navigation.isIdle() && this.pet.getSquaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance) && !this.pet.isSitting();
   }

   @Override
   public void start() {
      this.updateTimer = 0;
      this.isInWater = ((MobEntityNavigation)this.pet.getNavigation()).m_10fpgovhb();
      ((MobEntityNavigation)this.pet.getNavigation()).m_61diarbat(false);
   }

   @Override
   public void stop() {
      this.owner = null;
      this.navigation.stopCurrentNavigation();
      ((MobEntityNavigation)this.pet.getNavigation()).m_61diarbat(true);
   }

   @Override
   public void tick() {
      this.pet.getLookControl().setLookatValues(this.owner, 10.0F, (float)this.pet.getLookPitchSpeed());
      if (!this.pet.isSitting()) {
         if (--this.updateTimer <= 0) {
            this.updateTimer = 10;
            if (!this.navigation.startMovingTo(this.owner, this.speed)) {
               if (!this.pet.isLeashed()) {
                  if (!(this.pet.getSquaredDistanceTo(this.owner) < 144.0)) {
                     int var1 = MathHelper.floor(this.owner.x) - 2;
                     int var2 = MathHelper.floor(this.owner.z) - 2;
                     int var3 = MathHelper.floor(this.owner.getBoundingBox().minY);

                     for(int var4 = 0; var4 <= 4; ++var4) {
                        for(int var5 = 0; var5 <= 4; ++var5) {
                           if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3)
                              && World.hasSolidTop(this.world, new BlockPos(var1 + var4, var3 - 1, var2 + var5))
                              && !this.world.getBlockState(new BlockPos(var1 + var4, var3, var2 + var5)).getBlock().isFullCube()
                              && !this.world.getBlockState(new BlockPos(var1 + var4, var3 + 1, var2 + var5)).getBlock().isFullCube()) {
                              this.pet
                                 .refreshPositionAndAngles(
                                    (double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.pet.yaw, this.pet.pitch
                                 );
                              this.navigation.stopCurrentNavigation();
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
