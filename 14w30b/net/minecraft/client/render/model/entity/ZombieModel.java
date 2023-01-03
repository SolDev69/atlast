package net.minecraft.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZombieModel extends HumanoidModel {
   public ZombieModel() {
      this(0.0F, false);
   }

   protected ZombieModel(float f, float g, int i, int j) {
      super(f, g, i, j);
   }

   public ZombieModel(float reduction, boolean pivot) {
      super(reduction, 0.0F, 64, pivot ? 32 : 64);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      float var8 = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
      float var9 = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
      this.rightArm.rotationZ = 0.0F;
      this.leftArm.rotationZ = 0.0F;
      this.rightArm.rotationY = -(0.1F - var8 * 0.6F);
      this.leftArm.rotationY = 0.1F - var8 * 0.6F;
      this.rightArm.rotationX = (float) (-Math.PI / 2);
      this.leftArm.rotationX = (float) (-Math.PI / 2);
      this.rightArm.rotationX -= var8 * 1.2F - var9 * 0.4F;
      this.leftArm.rotationX -= var8 * 1.2F - var9 * 0.4F;
      this.rightArm.rotationZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
      this.leftArm.rotationZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
      this.rightArm.rotationX += MathHelper.sin(age * 0.067F) * 0.05F;
      this.leftArm.rotationX -= MathHelper.sin(age * 0.067F) * 0.05F;
   }
}
