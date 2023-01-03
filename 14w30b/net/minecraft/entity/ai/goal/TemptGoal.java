package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TemptGoal extends Goal {
   private PathAwareEntity entity;
   private double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double targetPitch;
   private double targetYaw;
   private PlayerEntity player;
   private int delay;
   private boolean goalActive;
   private Item temptingItem;
   private boolean scaredByMovement;
   private boolean inWater;

   public TemptGoal(PathAwareEntity entity, double speed, Item temptingItem, boolean bl) {
      this.entity = entity;
      this.speed = speed;
      this.temptingItem = temptingItem;
      this.scaredByMovement = bl;
      this.setControls(3);
      if (!(entity.getNavigation() instanceof MobEntityNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean canStart() {
      if (this.delay > 0) {
         --this.delay;
         return false;
      } else {
         this.player = this.entity.world.getClosestPlayer(this.entity, 10.0);
         if (this.player == null) {
            return false;
         } else {
            ItemStack var1 = this.player.getMainHandStack();
            if (var1 == null) {
               return false;
            } else {
               return var1.getItem() == this.temptingItem;
            }
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      if (this.scaredByMovement) {
         if (this.entity.getSquaredDistanceTo(this.player) < 36.0) {
            if (this.player.getSquaredDistanceTo(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs((double)this.player.pitch - this.targetPitch) > 5.0 || Math.abs((double)this.player.yaw - this.targetYaw) > 5.0) {
               return false;
            }
         } else {
            this.targetX = this.player.x;
            this.targetY = this.player.y;
            this.targetZ = this.player.z;
         }

         this.targetPitch = (double)this.player.pitch;
         this.targetYaw = (double)this.player.yaw;
      }

      return this.canStart();
   }

   @Override
   public void start() {
      this.targetX = this.player.x;
      this.targetY = this.player.y;
      this.targetZ = this.player.z;
      this.goalActive = true;
      this.inWater = ((MobEntityNavigation)this.entity.getNavigation()).m_10fpgovhb();
      ((MobEntityNavigation)this.entity.getNavigation()).m_61diarbat(false);
   }

   @Override
   public void stop() {
      this.player = null;
      this.entity.getNavigation().stopCurrentNavigation();
      this.delay = 100;
      this.goalActive = false;
      ((MobEntityNavigation)this.entity.getNavigation()).m_61diarbat(this.inWater);
   }

   @Override
   public void tick() {
      this.entity.getLookControl().setLookatValues(this.player, 30.0F, (float)this.entity.getLookPitchSpeed());
      if (this.entity.getSquaredDistanceTo(this.player) < 6.25) {
         this.entity.getNavigation().stopCurrentNavigation();
      } else {
         this.entity.getNavigation().startMovingTo(this.player, this.speed);
      }
   }

   public boolean isGoalActive() {
      return this.goalActive;
   }
}
