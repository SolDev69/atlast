package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OcelotModel extends Model {
   ModelPart backLeftLeg;
   ModelPart backRightLeg;
   ModelPart frontLeftLeg;
   ModelPart frontrightLeg;
   ModelPart upperTail;
   ModelPart lowerTail;
   ModelPart head;
   ModelPart body;
   int movementType = 1;

   public OcelotModel() {
      this.setTexturePos("head.main", 0, 0);
      this.setTexturePos("head.nose", 0, 24);
      this.setTexturePos("head.ear1", 0, 10);
      this.setTexturePos("head.ear2", 6, 10);
      this.head = new ModelPart(this, "head");
      this.head.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
      this.head.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
      this.head.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
      this.head.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
      this.head.setPivot(0.0F, 15.0F, -9.0F);
      this.body = new ModelPart(this, 20, 0);
      this.body.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
      this.body.setPivot(0.0F, 12.0F, -10.0F);
      this.upperTail = new ModelPart(this, 0, 15);
      this.upperTail.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
      this.upperTail.rotationX = 0.9F;
      this.upperTail.setPivot(0.0F, 15.0F, 8.0F);
      this.lowerTail = new ModelPart(this, 4, 15);
      this.lowerTail.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1);
      this.lowerTail.setPivot(0.0F, 20.0F, 14.0F);
      this.backLeftLeg = new ModelPart(this, 8, 13);
      this.backLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
      this.backLeftLeg.setPivot(1.1F, 18.0F, 5.0F);
      this.backRightLeg = new ModelPart(this, 8, 13);
      this.backRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2);
      this.backRightLeg.setPivot(-1.1F, 18.0F, 5.0F);
      this.frontLeftLeg = new ModelPart(this, 40, 0);
      this.frontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
      this.frontLeftLeg.setPivot(1.2F, 13.8F, -5.0F);
      this.frontrightLeg = new ModelPart(this, 40, 0);
      this.frontrightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2);
      this.frontrightLeg.setPivot(-1.2F, 13.8F, -5.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.5F / var8, 1.5F / var8, 1.5F / var8);
         GlStateManager.translatef(0.0F, 10.0F * scale, 4.0F * scale);
         this.head.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.backLeftLeg.render(scale);
         this.backRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
         this.frontrightLeg.render(scale);
         this.upperTail.render(scale);
         this.lowerTail.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.body.render(scale);
         this.upperTail.render(scale);
         this.lowerTail.render(scale);
         this.backLeftLeg.render(scale);
         this.backRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
         this.frontrightLeg.render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      if (this.movementType != 3) {
         this.body.rotationX = (float) (Math.PI / 2);
         if (this.movementType == 2) {
            this.backLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
            this.backRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + 0.3F) * 1.0F * handSwingAmount;
            this.frontLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI + 0.3F) * 1.0F * handSwingAmount;
            this.frontrightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
            this.lowerTail.rotationX = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(handSwing) * handSwingAmount;
         } else {
            this.backLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
            this.backRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
            this.frontLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
            this.frontrightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
            if (this.movementType == 1) {
               this.lowerTail.rotationX = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(handSwing) * handSwingAmount;
            } else {
               this.lowerTail.rotationX = 1.7278761F + 0.47123894F * MathHelper.cos(handSwing) * handSwingAmount;
            }
         }
      }
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      OcelotEntity var5 = (OcelotEntity)entity;
      this.body.pivotY = 12.0F;
      this.body.pivotZ = -10.0F;
      this.head.pivotY = 15.0F;
      this.head.pivotZ = -9.0F;
      this.upperTail.pivotY = 15.0F;
      this.upperTail.pivotZ = 8.0F;
      this.lowerTail.pivotY = 20.0F;
      this.lowerTail.pivotZ = 14.0F;
      this.frontLeftLeg.pivotY = this.frontrightLeg.pivotY = 13.8F;
      this.frontLeftLeg.pivotZ = this.frontrightLeg.pivotZ = -5.0F;
      this.backLeftLeg.pivotY = this.backRightLeg.pivotY = 18.0F;
      this.backLeftLeg.pivotZ = this.backRightLeg.pivotZ = 5.0F;
      this.upperTail.rotationX = 0.9F;
      if (var5.isSneaking()) {
         ++this.body.pivotY;
         this.head.pivotY += 2.0F;
         ++this.upperTail.pivotY;
         this.lowerTail.pivotY += -4.0F;
         this.lowerTail.pivotZ += 2.0F;
         this.upperTail.rotationX = (float) (Math.PI / 2);
         this.lowerTail.rotationX = (float) (Math.PI / 2);
         this.movementType = 0;
      } else if (var5.isSprinting()) {
         this.lowerTail.pivotY = this.upperTail.pivotY;
         this.lowerTail.pivotZ += 2.0F;
         this.upperTail.rotationX = (float) (Math.PI / 2);
         this.lowerTail.rotationX = (float) (Math.PI / 2);
         this.movementType = 2;
      } else if (var5.isSitting()) {
         this.body.rotationX = (float) (Math.PI / 4);
         this.body.pivotY += -4.0F;
         this.body.pivotZ += 5.0F;
         this.head.pivotY += -3.3F;
         ++this.head.pivotZ;
         this.upperTail.pivotY += 8.0F;
         this.upperTail.pivotZ += -2.0F;
         this.lowerTail.pivotY += 2.0F;
         this.lowerTail.pivotZ += -0.8F;
         this.upperTail.rotationX = 1.7278761F;
         this.lowerTail.rotationX = 2.670354F;
         this.frontLeftLeg.rotationX = this.frontrightLeg.rotationX = (float) (-Math.PI / 20);
         this.frontLeftLeg.pivotY = this.frontrightLeg.pivotY = 15.8F;
         this.frontLeftLeg.pivotZ = this.frontrightLeg.pivotZ = -7.0F;
         this.backLeftLeg.rotationX = this.backRightLeg.rotationX = (float) (-Math.PI / 2);
         this.backLeftLeg.pivotY = this.backRightLeg.pivotY = 21.0F;
         this.backLeftLeg.pivotZ = this.backRightLeg.pivotZ = 1.0F;
         this.movementType = 3;
      } else {
         this.movementType = 1;
      }
   }
}
