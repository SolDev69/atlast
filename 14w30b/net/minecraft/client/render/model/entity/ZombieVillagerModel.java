package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZombieVillagerModel extends HumanoidModel {
   public ZombieVillagerModel() {
      this(0.0F, 0.0F, false);
   }

   public ZombieVillagerModel(float reduction, float rotationAngle, boolean texture) {
      super(reduction, 0.0F, 64, texture ? 32 : 64);
      if (texture) {
         this.head = new ModelPart(this, 0, 0);
         this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, reduction);
         this.head.setPivot(0.0F, 0.0F + rotationAngle, 0.0F);
      } else {
         this.head = new ModelPart(this);
         this.head.setPivot(0.0F, 0.0F + rotationAngle, 0.0F);
         this.head.setTextureCoords(0, 32).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, reduction);
         this.head.setTextureCoords(24, 32).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, reduction);
      }
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
