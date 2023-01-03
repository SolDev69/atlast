package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WolfModel extends Model {
   public ModelPart head;
   public ModelPart body;
   public ModelPart backRightLeg;
   public ModelPart backLeftLeg;
   public ModelPart frontRightLeg;
   public ModelPart frontLeftLeg;
   ModelPart tail;
   ModelPart neck;

   public WolfModel() {
      float var1 = 0.0F;
      float var2 = 13.5F;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, var1);
      this.head.setPivot(-1.0F, var2, -7.0F);
      this.body = new ModelPart(this, 18, 14);
      this.body.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, var1);
      this.body.setPivot(0.0F, 14.0F, 2.0F);
      this.neck = new ModelPart(this, 21, 0);
      this.neck.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, var1);
      this.neck.setPivot(-1.0F, 14.0F, 2.0F);
      this.backRightLeg = new ModelPart(this, 0, 18);
      this.backRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.backRightLeg.setPivot(-2.5F, 16.0F, 7.0F);
      this.backLeftLeg = new ModelPart(this, 0, 18);
      this.backLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.backLeftLeg.setPivot(0.5F, 16.0F, 7.0F);
      this.frontRightLeg = new ModelPart(this, 0, 18);
      this.frontRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.frontRightLeg.setPivot(-2.5F, 16.0F, -4.0F);
      this.frontLeftLeg = new ModelPart(this, 0, 18);
      this.frontLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.frontLeftLeg.setPivot(0.5F, 16.0F, -4.0F);
      this.tail = new ModelPart(this, 9, 18);
      this.tail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, var1);
      this.tail.setPivot(-1.0F, 12.0F, 8.0F);
      this.head.setTextureCoords(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, var1);
      this.head.setTextureCoords(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, var1);
      this.head.setTextureCoords(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, var1);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      super.render(entity, handSwing, handSwingAmount, age, yaw, pitch, scale);
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
         this.head.renderRotation(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.backRightLeg.render(scale);
         this.backLeftLeg.render(scale);
         this.frontRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
         this.tail.renderRotation(scale);
         this.neck.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.renderRotation(scale);
         this.body.render(scale);
         this.backRightLeg.render(scale);
         this.backLeftLeg.render(scale);
         this.frontRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
         this.tail.renderRotation(scale);
         this.neck.render(scale);
      }
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      WolfEntity var5 = (WolfEntity)entity;
      if (var5.isAngry()) {
         this.tail.rotationY = 0.0F;
      } else {
         this.tail.rotationY = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      }

      if (var5.isSitting()) {
         this.neck.setPivot(-1.0F, 16.0F, -3.0F);
         this.neck.rotationX = (float) (Math.PI * 2.0 / 5.0);
         this.neck.rotationY = 0.0F;
         this.body.setPivot(0.0F, 18.0F, 0.0F);
         this.body.rotationX = (float) (Math.PI / 4);
         this.tail.setPivot(-1.0F, 21.0F, 6.0F);
         this.backRightLeg.setPivot(-2.5F, 22.0F, 2.0F);
         this.backRightLeg.rotationX = (float) (Math.PI * 3.0 / 2.0);
         this.backLeftLeg.setPivot(0.5F, 22.0F, 2.0F);
         this.backLeftLeg.rotationX = (float) (Math.PI * 3.0 / 2.0);
         this.frontRightLeg.rotationX = 5.811947F;
         this.frontRightLeg.setPivot(-2.49F, 17.0F, -4.0F);
         this.frontLeftLeg.rotationX = 5.811947F;
         this.frontLeftLeg.setPivot(0.51F, 17.0F, -4.0F);
      } else {
         this.body.setPivot(0.0F, 14.0F, 2.0F);
         this.body.rotationX = (float) (Math.PI / 2);
         this.neck.setPivot(-1.0F, 14.0F, -3.0F);
         this.neck.rotationX = this.body.rotationX;
         this.tail.setPivot(-1.0F, 12.0F, 8.0F);
         this.backRightLeg.setPivot(-2.5F, 16.0F, 7.0F);
         this.backLeftLeg.setPivot(0.5F, 16.0F, 7.0F);
         this.frontRightLeg.setPivot(-2.5F, 16.0F, -4.0F);
         this.frontLeftLeg.setPivot(0.5F, 16.0F, -4.0F);
         this.backRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
         this.backLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
         this.frontRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
         this.frontLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      }

      this.head.rotationZ = var5.begLerp(tickDelta) + var5.getShakeAngle(tickDelta, 0.0F);
      this.neck.rotationZ = var5.getShakeAngle(tickDelta, -0.08F);
      this.body.rotationZ = var5.getShakeAngle(tickDelta, -0.16F);
      this.tail.rotationZ = var5.getShakeAngle(tickDelta, -0.2F);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.tail.rotationX = age;
   }
}
