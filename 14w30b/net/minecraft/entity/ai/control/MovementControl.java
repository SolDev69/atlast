package net.minecraft.entity.ai.control;

import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class MovementControl {
   protected MobEntity mob;
   protected double x;
   protected double y;
   protected double z;
   protected double speed;
   protected boolean updated;

   public MovementControl(MobEntity mob) {
      this.mob = mob;
      this.x = mob.x;
      this.y = mob.y;
      this.z = mob.z;
   }

   public boolean isUpdated() {
      return this.updated;
   }

   public double getSpeed() {
      return this.speed;
   }

   public void update(double x, double y, double z, double speed) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.speed = speed;
      this.updated = true;
   }

   public void tickUpdateMovement() {
      this.mob.setForwardVelocity(0.0F);
      if (this.updated) {
         this.updated = false;
         int var1 = MathHelper.floor(this.mob.getBoundingBox().minY + 0.5);
         double var2 = this.x - this.mob.x;
         double var4 = this.z - this.mob.z;
         double var6 = this.y - (double)var1;
         double var8 = var2 * var2 + var6 * var6 + var4 * var4;
         if (!(var8 < 2.5000003E-7F)) {
            float var10 = (float)(Math.atan2(var4, var2) * 180.0 / (float) Math.PI) - 90.0F;
            this.mob.yaw = this.clampAndWrapAngle(this.mob.yaw, var10, 30.0F);
            this.mob.setMovementSpeed((float)(this.speed * this.mob.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get()));
            if (var6 > 0.0 && var2 * var2 + var4 * var4 < 1.0) {
               this.mob.getJumpControl().setActive();
            }
         }
      }
   }

   protected float clampAndWrapAngle(float yaw1, float yaw2, float maxOffset) {
      float var4 = MathHelper.wrapDegrees(yaw2 - yaw1);
      if (var4 > maxOffset) {
         var4 = maxOffset;
      }

      if (var4 < -maxOffset) {
         var4 = -maxOffset;
      }

      float var5 = yaw1 + var4;
      if (var5 < 0.0F) {
         var5 += 360.0F;
      } else if (var5 > 360.0F) {
         var5 -= 360.0F;
      }

      return var5;
   }

   public double m_53wsenqsm() {
      return this.x;
   }

   public double m_90fcnurdd() {
      return this.y;
   }

   public double m_37abemtzt() {
      return this.z;
   }
}
