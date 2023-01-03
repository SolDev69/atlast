package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class QuadrupedModel extends Model {
   public ModelPart head;
   public ModelPart body;
   public ModelPart backRightLeg;
   public ModelPart backLeftLeg;
   public ModelPart frontRightLeg;
   public ModelPart frontLeftLeg;
   protected float babyHeadHeightOffset = 8.0F;
   protected float babyHeadOffset = 4.0F;

   public QuadrupedModel(int pivotPoint, float reduction) {
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, reduction);
      this.head.setPivot(0.0F, (float)(18 - pivotPoint), -6.0F);
      this.body = new ModelPart(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, reduction);
      this.body.setPivot(0.0F, (float)(17 - pivotPoint), 2.0F);
      this.backRightLeg = new ModelPart(this, 0, 16);
      this.backRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, pivotPoint, 4, reduction);
      this.backRightLeg.setPivot(-3.0F, (float)(24 - pivotPoint), 7.0F);
      this.backLeftLeg = new ModelPart(this, 0, 16);
      this.backLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, pivotPoint, 4, reduction);
      this.backLeftLeg.setPivot(3.0F, (float)(24 - pivotPoint), 7.0F);
      this.frontRightLeg = new ModelPart(this, 0, 16);
      this.frontRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, pivotPoint, 4, reduction);
      this.frontRightLeg.setPivot(-3.0F, (float)(24 - pivotPoint), -5.0F);
      this.frontLeftLeg = new ModelPart(this, 0, 16);
      this.frontLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, pivotPoint, 4, reduction);
      this.frontLeftLeg.setPivot(3.0F, (float)(24 - pivotPoint), -5.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, this.babyHeadHeightOffset * scale, this.babyHeadOffset * scale);
         this.head.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.backRightLeg.render(scale);
         this.backLeftLeg.render(scale);
         this.frontRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.body.render(scale);
         this.backRightLeg.render(scale);
         this.backLeftLeg.render(scale);
         this.frontRightLeg.render(scale);
         this.frontLeftLeg.render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      float var8 = 180.0F / (float)Math.PI;
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.body.rotationX = (float) (Math.PI / 2);
      this.backRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      this.backLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.frontRightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.frontLeftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
   }
}
