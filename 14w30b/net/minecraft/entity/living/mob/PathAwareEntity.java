package net.minecraft.entity.living.mob;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class PathAwareEntity extends MobEntity {
   public static final UUID FLEEING_SPEED_BONUS_UUID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier FLEEING_SPEED_BONUS = new AttributeModifier(FLEEING_SPEED_BONUS_UUID, "Fleeing speed bonus", 2.0, 2)
      .setSerialized(false);
   private BlockPos pos = BlockPos.ORIGIN;
   private float villageRadius = -1.0F;
   private Goal goToWalkTargetGoal = new GoToWalkTargetGoal(this, 1.0);
   private boolean leechedNavigation;

   public PathAwareEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public float getPathfindingFavor(BlockPos x) {
      return 0.0F;
   }

   @Override
   public boolean canSpawn() {
      return super.canSpawn() && this.getPathfindingFavor(new BlockPos(this.x, this.getBoundingBox().minY, this.z)) >= 0.0F;
   }

   public boolean m_59adgmjkb() {
      return !this.entityNavigation.isIdle();
   }

   public boolean isInVillage() {
      return this.isPosInVillage(new BlockPos(this));
   }

   public boolean isPosInVillage(BlockPos x) {
      if (this.villageRadius == -1.0F) {
         return true;
      } else {
         return this.pos.squaredDistanceTo(x) < (double)(this.villageRadius * this.villageRadius);
      }
   }

   public void setVillagePosAndRadius(BlockPos x, int y) {
      this.pos = x;
      this.villageRadius = (float)y;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public float getVillageRadius() {
      return this.villageRadius;
   }

   public void resetVillageRadius() {
      this.villageRadius = -1.0F;
   }

   public boolean inVillage() {
      return this.villageRadius != -1.0F;
   }

   @Override
   protected void updateLeashStatus() {
      super.updateLeashStatus();
      if (this.isLeashed() && this.getHoldingEntity() != null && this.getHoldingEntity().world == this.world) {
         Entity var1 = this.getHoldingEntity();
         this.setVillagePosAndRadius(new BlockPos((int)var1.x, (int)var1.y, (int)var1.z), 5);
         float var2 = this.getDistanceTo(var1);
         if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
            if (var2 > 10.0F) {
               this.detachLeash(true, true);
            }

            return;
         }

         if (!this.leechedNavigation) {
            this.goalSelector.addGoal(2, this.goToWalkTargetGoal);
            if (this.getNavigation() instanceof MobEntityNavigation) {
               ((MobEntityNavigation)this.getNavigation()).m_61diarbat(false);
            }

            this.leechedNavigation = true;
         }

         this.updateForLeashLength(var2);
         if (var2 > 4.0F) {
            this.getNavigation().startMovingTo(var1, 1.0);
         }

         if (var2 > 6.0F) {
            double var3 = (var1.x - this.x) / (double)var2;
            double var5 = (var1.y - this.y) / (double)var2;
            double var7 = (var1.z - this.z) / (double)var2;
            this.velocityX += var3 * Math.abs(var3) * 0.4;
            this.velocityY += var5 * Math.abs(var5) * 0.4;
            this.velocityZ += var7 * Math.abs(var7) * 0.4;
         }

         if (var2 > 10.0F) {
            this.detachLeash(true, true);
         }
      } else if (!this.isLeashed() && this.leechedNavigation) {
         this.leechedNavigation = false;
         this.goalSelector.removeGoal(this.goToWalkTargetGoal);
         if (this.getNavigation() instanceof MobEntityNavigation) {
            ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
         }

         this.resetVillageRadius();
      }
   }

   protected void updateForLeashLength(float leashLength) {
   }
}
