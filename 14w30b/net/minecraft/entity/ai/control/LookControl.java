package net.minecraft.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class LookControl {
   private MobEntity mob;
   private float yaw;
   private float pitch;
   private boolean active;
   private double lookX;
   private double lookY;
   private double lookZ;

   public LookControl(MobEntity mob) {
      this.mob = mob;
   }

   public void setLookatValues(Entity entity, float yaw, float pitch) {
      this.lookX = entity.x;
      if (entity instanceof LivingEntity) {
         this.lookY = entity.y + (double)entity.getEyeHeight();
      } else {
         this.lookY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
      }

      this.lookZ = entity.z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.active = true;
   }

   public void lookAt(double x, double y, double z, float yaw, float pitch) {
      this.lookX = x;
      this.lookY = y;
      this.lookZ = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.active = true;
   }

   public void tick() {
      this.mob.pitch = 0.0F;
      if (this.active) {
         this.active = false;
         double var1 = this.lookX - this.mob.x;
         double var3 = this.lookY - (this.mob.y + (double)this.mob.getEyeHeight());
         double var5 = this.lookZ - this.mob.z;
         double var7 = (double)MathHelper.sqrt(var1 * var1 + var5 * var5);
         float var9 = (float)(Math.atan2(var5, var1) * 180.0 / (float) Math.PI) - 90.0F;
         float var10 = (float)(-(Math.atan2(var3, var7) * 180.0 / (float) Math.PI));
         this.mob.pitch = this.clampAndWrapAngle(this.mob.pitch, var10, this.pitch);
         this.mob.headYaw = this.clampAndWrapAngle(this.mob.headYaw, var9, this.yaw);
      } else {
         this.mob.headYaw = this.clampAndWrapAngle(this.mob.headYaw, this.mob.bodyYaw, 10.0F);
      }

      float var11 = MathHelper.wrapDegrees(this.mob.headYaw - this.mob.bodyYaw);
      if (!this.mob.getNavigation().isIdle()) {
         if (var11 < -75.0F) {
            this.mob.headYaw = this.mob.bodyYaw - 75.0F;
         }

         if (var11 > 75.0F) {
            this.mob.headYaw = this.mob.bodyYaw + 75.0F;
         }
      }
   }

   private float clampAndWrapAngle(float yaw1, float yaw2, float maxOffset) {
      float var4 = MathHelper.wrapDegrees(yaw2 - yaw1);
      if (var4 > maxOffset) {
         var4 = maxOffset;
      }

      if (var4 < -maxOffset) {
         var4 = -maxOffset;
      }

      return yaw1 + var4;
   }

   public boolean m_96bcvxfgw() {
      return this.active;
   }

   public double m_97ssyvlpp() {
      return this.lookX;
   }

   public double m_41adiagvz() {
      return this.lookY;
   }

   public double m_59fqsnxye() {
      return this.lookZ;
   }
}
