package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpiderModel extends Model {
   public ModelPart head;
   public ModelPart neck;
   public ModelPart body;
   public ModelPart backRightLeg;
   public ModelPart backLeftLeg;
   public ModelPart backMiddleRightLeg;
   public ModelPart backMiddleLeftLeg;
   public ModelPart frontMiddleRightLeg;
   public ModelPart frontMiddleLeftLeg;
   public ModelPart frontRightLeg;
   public ModelPart frontLeftLeg;

   public SpiderModel() {
      float var1 = 0.0F;
      byte var2 = 15;
      this.head = new ModelPart(this, 32, 4);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, var1);
      this.head.setPivot(0.0F, (float)var2, -3.0F);
      this.neck = new ModelPart(this, 0, 0);
      this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
      this.neck.setPivot(0.0F, (float)var2, 0.0F);
      this.body = new ModelPart(this, 0, 12);
      this.body.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
      this.body.setPivot(0.0F, (float)var2, 9.0F);
      this.backRightLeg = new ModelPart(this, 18, 0);
      this.backRightLeg.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.backRightLeg.setPivot(-4.0F, (float)var2, 2.0F);
      this.backLeftLeg = new ModelPart(this, 18, 0);
      this.backLeftLeg.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.backLeftLeg.setPivot(4.0F, (float)var2, 2.0F);
      this.backMiddleRightLeg = new ModelPart(this, 18, 0);
      this.backMiddleRightLeg.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.backMiddleRightLeg.setPivot(-4.0F, (float)var2, 1.0F);
      this.backMiddleLeftLeg = new ModelPart(this, 18, 0);
      this.backMiddleLeftLeg.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.backMiddleLeftLeg.setPivot(4.0F, (float)var2, 1.0F);
      this.frontMiddleRightLeg = new ModelPart(this, 18, 0);
      this.frontMiddleRightLeg.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.frontMiddleRightLeg.setPivot(-4.0F, (float)var2, 0.0F);
      this.frontMiddleLeftLeg = new ModelPart(this, 18, 0);
      this.frontMiddleLeftLeg.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.frontMiddleLeftLeg.setPivot(4.0F, (float)var2, 0.0F);
      this.frontRightLeg = new ModelPart(this, 18, 0);
      this.frontRightLeg.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.frontRightLeg.setPivot(-4.0F, (float)var2, -1.0F);
      this.frontLeftLeg = new ModelPart(this, 18, 0);
      this.frontLeftLeg.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
      this.frontLeftLeg.setPivot(4.0F, (float)var2, -1.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.render(scale);
      this.neck.render(scale);
      this.body.render(scale);
      this.backRightLeg.render(scale);
      this.backLeftLeg.render(scale);
      this.backMiddleRightLeg.render(scale);
      this.backMiddleLeftLeg.render(scale);
      this.frontMiddleRightLeg.render(scale);
      this.frontMiddleLeftLeg.render(scale);
      this.frontRightLeg.render(scale);
      this.frontLeftLeg.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      float var8 = (float) (Math.PI / 4);
      this.backRightLeg.rotationZ = -var8;
      this.backLeftLeg.rotationZ = var8;
      this.backMiddleRightLeg.rotationZ = -var8 * 0.74F;
      this.backMiddleLeftLeg.rotationZ = var8 * 0.74F;
      this.frontMiddleRightLeg.rotationZ = -var8 * 0.74F;
      this.frontMiddleLeftLeg.rotationZ = var8 * 0.74F;
      this.frontRightLeg.rotationZ = -var8;
      this.frontLeftLeg.rotationZ = var8;
      float var9 = -0.0F;
      float var10 = (float) (Math.PI / 8);
      this.backRightLeg.rotationY = var10 * 2.0F + var9;
      this.backLeftLeg.rotationY = -var10 * 2.0F - var9;
      this.backMiddleRightLeg.rotationY = var10 * 1.0F + var9;
      this.backMiddleLeftLeg.rotationY = -var10 * 1.0F - var9;
      this.frontMiddleRightLeg.rotationY = -var10 * 1.0F + var9;
      this.frontMiddleLeftLeg.rotationY = var10 * 1.0F - var9;
      this.frontRightLeg.rotationY = -var10 * 2.0F + var9;
      this.frontLeftLeg.rotationY = var10 * 2.0F - var9;
      float var11 = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * handSwingAmount;
      float var12 = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * handSwingAmount;
      float var13 = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * handSwingAmount;
      float var14 = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * handSwingAmount;
      float var15 = Math.abs(MathHelper.sin(handSwing * 0.6662F + 0.0F) * 0.4F) * handSwingAmount;
      float var16 = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) Math.PI) * 0.4F) * handSwingAmount;
      float var17 = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * handSwingAmount;
      float var18 = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * handSwingAmount;
      this.backRightLeg.rotationY += var11;
      this.backLeftLeg.rotationY += -var11;
      this.backMiddleRightLeg.rotationY += var12;
      this.backMiddleLeftLeg.rotationY += -var12;
      this.frontMiddleRightLeg.rotationY += var13;
      this.frontMiddleLeftLeg.rotationY += -var13;
      this.frontRightLeg.rotationY += var14;
      this.frontLeftLeg.rotationY += -var14;
      this.backRightLeg.rotationZ += var15;
      this.backLeftLeg.rotationZ += -var15;
      this.backMiddleRightLeg.rotationZ += var16;
      this.backMiddleLeftLeg.rotationZ += -var16;
      this.frontMiddleRightLeg.rotationZ += var17;
      this.frontMiddleLeftLeg.rotationZ += -var17;
      this.frontRightLeg.rotationZ += var18;
      this.frontLeftLeg.rotationZ += -var18;
   }
}
