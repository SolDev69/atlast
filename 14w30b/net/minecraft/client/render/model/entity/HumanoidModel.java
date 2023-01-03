package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HumanoidModel extends Model {
   public ModelPart head;
   public ModelPart hat;
   public ModelPart body;
   public ModelPart rightArm;
   public ModelPart leftArm;
   public ModelPart rightLeg;
   public ModelPart leftLeg;
   public int leftHandItemId;
   public int rightHandItemId;
   public boolean sneaking;
   public boolean aimingBow;

   public HumanoidModel() {
      this(0.0F);
   }

   public HumanoidModel(float reduction) {
      this(reduction, 0.0F, 64, 32);
   }

   public HumanoidModel(float reduction, float pivotPoint, int textureWidth, int textureHeight) {
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, reduction);
      this.head.setPivot(0.0F, 0.0F + pivotPoint, 0.0F);
      this.hat = new ModelPart(this, 32, 0);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, reduction + 0.5F);
      this.hat.setPivot(0.0F, 0.0F + pivotPoint, 0.0F);
      this.body = new ModelPart(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, reduction);
      this.body.setPivot(0.0F, 0.0F + pivotPoint, 0.0F);
      this.rightArm = new ModelPart(this, 40, 16);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, reduction);
      this.rightArm.setPivot(-5.0F, 2.0F + pivotPoint, 0.0F);
      this.leftArm = new ModelPart(this, 40, 16);
      this.leftArm.flipped = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, reduction);
      this.leftArm.setPivot(5.0F, 2.0F + pivotPoint, 0.0F);
      this.rightLeg = new ModelPart(this, 0, 16);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, reduction);
      this.rightLeg.setPivot(-1.9F, 12.0F + pivotPoint, 0.0F);
      this.leftLeg = new ModelPart(this, 0, 16);
      this.leftLeg.flipped = true;
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, reduction);
      this.leftLeg.setPivot(1.9F, 12.0F + pivotPoint, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      GlStateManager.pushMatrix();
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.scalef(1.5F / var8, 1.5F / var8, 1.5F / var8);
         GlStateManager.translatef(0.0F, 16.0F * scale, 0.0F);
         this.head.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.rightArm.render(scale);
         this.leftArm.render(scale);
         this.rightLeg.render(scale);
         this.leftLeg.render(scale);
         this.hat.render(scale);
      } else {
         if (entity.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.head.render(scale);
         this.body.render(scale);
         this.rightArm.render(scale);
         this.leftArm.render(scale);
         this.rightLeg.render(scale);
         this.leftLeg.render(scale);
         this.hat.render(scale);
      }

      GlStateManager.popMatrix();
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.rightArm.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 2.0F * handSwingAmount * 0.5F;
      this.leftArm.rotationX = MathHelper.cos(handSwing * 0.6662F) * 2.0F * handSwingAmount * 0.5F;
      this.rightArm.rotationZ = 0.0F;
      this.leftArm.rotationZ = 0.0F;
      this.rightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      this.leftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.rightLeg.rotationY = 0.0F;
      this.leftLeg.rotationY = 0.0F;
      if (this.hasVehicle) {
         this.rightArm.rotationX += (float) (-Math.PI / 5);
         this.leftArm.rotationX += (float) (-Math.PI / 5);
         this.rightLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
         this.leftLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
         this.rightLeg.rotationY = (float) (Math.PI / 10);
         this.leftLeg.rotationY = (float) (-Math.PI / 10);
      }

      if (this.leftHandItemId != 0) {
         this.leftArm.rotationX = this.leftArm.rotationX * 0.5F - (float) (Math.PI / 10) * (float)this.leftHandItemId;
      }

      this.rightArm.rotationY = 0.0F;
      this.rightArm.rotationZ = 0.0F;
      switch(this.rightHandItemId) {
         case 0:
         case 2:
         default:
            break;
         case 1:
            this.rightArm.rotationX = this.rightArm.rotationX * 0.5F - (float) (Math.PI / 10) * (float)this.rightHandItemId;
            break;
         case 3:
            this.rightArm.rotationX = this.rightArm.rotationX * 0.5F - (float) (Math.PI / 10) * (float)this.rightHandItemId;
            this.rightArm.rotationY = (float) (-Math.PI / 6);
      }

      this.leftArm.rotationY = 0.0F;
      if (this.handSwingProgress > -9990.0F) {
         float var8 = this.handSwingProgress;
         this.body.rotationY = MathHelper.sin(MathHelper.sqrt(var8) * (float) Math.PI * 2.0F) * 0.2F;
         this.rightArm.pivotZ = MathHelper.sin(this.body.rotationY) * 5.0F;
         this.rightArm.pivotX = -MathHelper.cos(this.body.rotationY) * 5.0F;
         this.leftArm.pivotZ = -MathHelper.sin(this.body.rotationY) * 5.0F;
         this.leftArm.pivotX = MathHelper.cos(this.body.rotationY) * 5.0F;
         this.rightArm.rotationY += this.body.rotationY;
         this.leftArm.rotationY += this.body.rotationY;
         this.leftArm.rotationX += this.body.rotationY;
         var8 = 1.0F - this.handSwingProgress;
         var8 *= var8;
         var8 *= var8;
         var8 = 1.0F - var8;
         float var9 = MathHelper.sin(var8 * (float) Math.PI);
         float var10 = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.rotationX - 0.7F) * 0.75F;
         this.rightArm.rotationX = (float)((double)this.rightArm.rotationX - ((double)var9 * 1.2 + (double)var10));
         this.rightArm.rotationY += this.body.rotationY * 2.0F;
         this.rightArm.rotationZ += MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
      }

      if (this.sneaking) {
         this.body.rotationX = 0.5F;
         this.rightArm.rotationX += 0.4F;
         this.leftArm.rotationX += 0.4F;
         this.rightLeg.pivotZ = 4.0F;
         this.leftLeg.pivotZ = 4.0F;
         this.rightLeg.pivotY = 9.0F;
         this.leftLeg.pivotY = 9.0F;
         this.head.pivotY = 1.0F;
      } else {
         this.body.rotationX = 0.0F;
         this.rightLeg.pivotZ = 0.1F;
         this.leftLeg.pivotZ = 0.1F;
         this.rightLeg.pivotY = 12.0F;
         this.leftLeg.pivotY = 12.0F;
         this.head.pivotY = 0.0F;
      }

      this.rightArm.rotationZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
      this.leftArm.rotationZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
      this.rightArm.rotationX += MathHelper.sin(age * 0.067F) * 0.05F;
      this.leftArm.rotationX -= MathHelper.sin(age * 0.067F) * 0.05F;
      if (this.aimingBow) {
         float var15 = 0.0F;
         float var16 = 0.0F;
         this.rightArm.rotationZ = 0.0F;
         this.leftArm.rotationZ = 0.0F;
         this.rightArm.rotationY = -(0.1F - var15 * 0.6F) + this.head.rotationY;
         this.leftArm.rotationY = 0.1F - var15 * 0.6F + this.head.rotationY + 0.4F;
         this.rightArm.rotationX = ((float) (-Math.PI / 2)) + this.head.rotationX;
         this.leftArm.rotationX = (float) (-Math.PI / 2) + this.head.rotationX;
         this.rightArm.rotationX -= var15 * 1.2F - var16 * 0.4F;
         this.leftArm.rotationX -= var15 * 1.2F - var16 * 0.4F;
         this.rightArm.rotationZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
         this.leftArm.rotationZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
         this.rightArm.rotationX += MathHelper.sin(age * 0.067F) * 0.05F;
         this.leftArm.rotationX -= MathHelper.sin(age * 0.067F) * 0.05F;
      }

      copyRotation(this.head, this.hat);
   }

   @Override
   public void copyPropertiesFrom(Model model) {
      super.copyPropertiesFrom(model);
      if (model instanceof HumanoidModel) {
         HumanoidModel var2 = (HumanoidModel)model;
         this.leftHandItemId = var2.leftHandItemId;
         this.rightHandItemId = var2.rightHandItemId;
         this.sneaking = var2.sneaking;
         this.aimingBow = var2.aimingBow;
      }
   }

   public void setVisible(boolean visible) {
      this.head.visible = visible;
      this.hat.visible = visible;
      this.body.visible = visible;
      this.rightArm.visible = visible;
      this.leftArm.visible = visible;
      this.rightLeg.visible = visible;
      this.leftLeg.visible = visible;
   }

   public void translateRightArm(float scale) {
      this.rightArm.translate(scale);
   }
}
