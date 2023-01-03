package net.minecraft.entity.ai.control;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
   private LivingEntity entity;
   private int activeTicks;
   private float lastHeadYaw;

   public BodyControl(LivingEntity entity) {
      this.entity = entity;
   }

   public void tick() {
      double var1 = this.entity.x - this.entity.prevX;
      double var3 = this.entity.z - this.entity.prevZ;
      if (var1 * var1 + var3 * var3 > 2.5000003E-7F) {
         this.entity.bodyYaw = this.entity.yaw;
         this.entity.headYaw = this.clampAndWrapAngle(this.entity.bodyYaw, this.entity.headYaw, 75.0F);
         this.lastHeadYaw = this.entity.headYaw;
         this.activeTicks = 0;
      } else {
         float var5 = 75.0F;
         if (Math.abs(this.entity.headYaw - this.lastHeadYaw) > 15.0F) {
            this.activeTicks = 0;
            this.lastHeadYaw = this.entity.headYaw;
         } else {
            ++this.activeTicks;
            boolean var6 = true;
            if (this.activeTicks > 10) {
               var5 = Math.max(1.0F - (float)(this.activeTicks - 10) / 10.0F, 0.0F) * 75.0F;
            }
         }

         this.entity.bodyYaw = this.clampAndWrapAngle(this.entity.headYaw, this.entity.bodyYaw, var5);
      }
   }

   private float clampAndWrapAngle(float yaw1, float yaw2, float maxOffset) {
      float var4 = MathHelper.wrapDegrees(yaw1 - yaw2);
      if (var4 < -maxOffset) {
         var4 = -maxOffset;
      }

      if (var4 >= maxOffset) {
         var4 = maxOffset;
      }

      return yaw1 - var4;
   }
}
