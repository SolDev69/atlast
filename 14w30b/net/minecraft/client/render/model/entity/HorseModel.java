package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HorseModel extends Model {
   private ModelPart head;
   private ModelPart topJaw;
   private ModelPart bottomJaw;
   private ModelPart donkeyLeftEar;
   private ModelPart donkeyRightEar;
   private ModelPart muleEarLeft;
   private ModelPart muleEarRight;
   private ModelPart neck;
   private ModelPart halster;
   private ModelPart mane;
   private ModelPart mainBody;
   private ModelPart dock;
   private ModelPart tail;
   private ModelPart skirt;
   private ModelPart leftThigh;
   private ModelPart backLeftCannon;
   private ModelPart backLeftHoof;
   private ModelPart rightThigh;
   private ModelPart backRightCannon;
   private ModelPart backRightHoof;
   private ModelPart leftForearm;
   private ModelPart frontLeftCannon;
   private ModelPart frontLeftHoof;
   private ModelPart rightForearm;
   private ModelPart frontRightCannon;
   private ModelPart frontRightHoof;
   private ModelPart leftDonkeyChest;
   private ModelPart rightDonkeyChest;
   private ModelPart saddleSeat;
   private ModelPart gullet;
   private ModelPart cantle;
   private ModelPart leftFender;
   private ModelPart leftStirrup;
   private ModelPart rightFender;
   private ModelPart rightStirrup;
   private ModelPart leftSnaffleBit;
   private ModelPart rightSnaffleBit;
   private ModelPart leftReign;
   private ModelPart rightReign;

   public HorseModel() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.mainBody = new ModelPart(this, 0, 34);
      this.mainBody.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
      this.mainBody.setPivot(0.0F, 11.0F, 9.0F);
      this.dock = new ModelPart(this, 44, 0);
      this.dock.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
      this.dock.setPivot(0.0F, 3.0F, 14.0F);
      this.setModelRotation(this.dock, -1.134464F, 0.0F, 0.0F);
      this.tail = new ModelPart(this, 38, 7);
      this.tail.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
      this.tail.setPivot(0.0F, 3.0F, 14.0F);
      this.setModelRotation(this.tail, -1.134464F, 0.0F, 0.0F);
      this.skirt = new ModelPart(this, 24, 3);
      this.skirt.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
      this.skirt.setPivot(0.0F, 3.0F, 14.0F);
      this.setModelRotation(this.skirt, -1.40215F, 0.0F, 0.0F);
      this.leftThigh = new ModelPart(this, 78, 29);
      this.leftThigh.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
      this.leftThigh.setPivot(4.0F, 9.0F, 11.0F);
      this.backLeftCannon = new ModelPart(this, 78, 43);
      this.backLeftCannon.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backLeftCannon.setPivot(4.0F, 16.0F, 11.0F);
      this.backLeftHoof = new ModelPart(this, 78, 51);
      this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backLeftHoof.setPivot(4.0F, 16.0F, 11.0F);
      this.rightThigh = new ModelPart(this, 96, 29);
      this.rightThigh.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
      this.rightThigh.setPivot(-4.0F, 9.0F, 11.0F);
      this.backRightCannon = new ModelPart(this, 96, 43);
      this.backRightCannon.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backRightCannon.setPivot(-4.0F, 16.0F, 11.0F);
      this.backRightHoof = new ModelPart(this, 96, 51);
      this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backRightHoof.setPivot(-4.0F, 16.0F, 11.0F);
      this.leftForearm = new ModelPart(this, 44, 29);
      this.leftForearm.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
      this.leftForearm.setPivot(4.0F, 9.0F, -8.0F);
      this.frontLeftCannon = new ModelPart(this, 44, 41);
      this.frontLeftCannon.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
      this.frontLeftCannon.setPivot(4.0F, 16.0F, -8.0F);
      this.frontLeftHoof = new ModelPart(this, 44, 51);
      this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
      this.frontLeftHoof.setPivot(4.0F, 16.0F, -8.0F);
      this.rightForearm = new ModelPart(this, 60, 29);
      this.rightForearm.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
      this.rightForearm.setPivot(-4.0F, 9.0F, -8.0F);
      this.frontRightCannon = new ModelPart(this, 60, 41);
      this.frontRightCannon.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
      this.frontRightCannon.setPivot(-4.0F, 16.0F, -8.0F);
      this.frontRightHoof = new ModelPart(this, 60, 51);
      this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
      this.frontRightHoof.setPivot(-4.0F, 16.0F, -8.0F);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
      this.head.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.head, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.topJaw = new ModelPart(this, 24, 18);
      this.topJaw.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
      this.topJaw.setPivot(0.0F, 3.95F, -10.0F);
      this.setModelRotation(this.topJaw, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.bottomJaw = new ModelPart(this, 24, 27);
      this.bottomJaw.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
      this.bottomJaw.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.bottomJaw, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.head.addChild(this.topJaw);
      this.head.addChild(this.bottomJaw);
      this.donkeyLeftEar = new ModelPart(this, 0, 0);
      this.donkeyLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
      this.donkeyLeftEar.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.donkeyLeftEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.donkeyRightEar = new ModelPart(this, 0, 0);
      this.donkeyRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
      this.donkeyRightEar.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.donkeyRightEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.muleEarLeft = new ModelPart(this, 0, 12);
      this.muleEarLeft.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleEarLeft.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.muleEarLeft, (float) (Math.PI / 6), 0.0F, (float) (Math.PI / 12));
      this.muleEarRight = new ModelPart(this, 0, 12);
      this.muleEarRight.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleEarRight.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.muleEarRight, (float) (Math.PI / 6), 0.0F, (float) (-Math.PI / 12));
      this.neck = new ModelPart(this, 0, 12);
      this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
      this.neck.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.neck, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.leftDonkeyChest = new ModelPart(this, 0, 34);
      this.leftDonkeyChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.leftDonkeyChest.setPivot(-7.5F, 3.0F, 10.0F);
      this.setModelRotation(this.leftDonkeyChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.rightDonkeyChest = new ModelPart(this, 0, 47);
      this.rightDonkeyChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.rightDonkeyChest.setPivot(4.5F, 3.0F, 10.0F);
      this.setModelRotation(this.rightDonkeyChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.saddleSeat = new ModelPart(this, 80, 0);
      this.saddleSeat.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
      this.saddleSeat.setPivot(0.0F, 2.0F, 2.0F);
      this.gullet = new ModelPart(this, 106, 9);
      this.gullet.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
      this.gullet.setPivot(0.0F, 2.0F, 2.0F);
      this.cantle = new ModelPart(this, 80, 9);
      this.cantle.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
      this.cantle.setPivot(0.0F, 2.0F, 2.0F);
      this.leftStirrup = new ModelPart(this, 74, 0);
      this.leftStirrup.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.leftStirrup.setPivot(5.0F, 3.0F, 2.0F);
      this.leftFender = new ModelPart(this, 70, 0);
      this.leftFender.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.leftFender.setPivot(5.0F, 3.0F, 2.0F);
      this.rightStirrup = new ModelPart(this, 74, 4);
      this.rightStirrup.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.rightStirrup.setPivot(-5.0F, 3.0F, 2.0F);
      this.rightFender = new ModelPart(this, 80, 0);
      this.rightFender.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.rightFender.setPivot(-5.0F, 3.0F, 2.0F);
      this.leftSnaffleBit = new ModelPart(this, 74, 13);
      this.leftSnaffleBit.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
      this.leftSnaffleBit.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.leftSnaffleBit, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.rightSnaffleBit = new ModelPart(this, 74, 13);
      this.rightSnaffleBit.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
      this.rightSnaffleBit.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.rightSnaffleBit, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.leftReign = new ModelPart(this, 44, 10);
      this.leftReign.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.leftReign.setPivot(0.0F, 4.0F, -10.0F);
      this.rightReign = new ModelPart(this, 44, 5);
      this.rightReign.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.rightReign.setPivot(0.0F, 4.0F, -10.0F);
      this.mane = new ModelPart(this, 58, 0);
      this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
      this.mane.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.mane, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.halster = new ModelPart(this, 80, 12);
      this.halster.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
      this.halster.setPivot(0.0F, 4.0F, -10.0F);
      this.setModelRotation(this.halster, (float) (Math.PI / 6), 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      HorseBaseEntity var8 = (HorseBaseEntity)entity;
      int var9 = var8.getType();
      float var10 = var8.getGrassAnimationProgress(0.0F);
      boolean var11 = var8.isNotBaby();
      boolean var12 = var11 && var8.isSaddled();
      boolean var13 = var11 && var8.hasChest();
      boolean var14 = var9 == 1 || var9 == 2;
      float var15 = var8.getSize();
      boolean var16 = var8.rider != null;
      if (var12) {
         this.halster.render(scale);
         this.saddleSeat.render(scale);
         this.gullet.render(scale);
         this.cantle.render(scale);
         this.leftFender.render(scale);
         this.leftStirrup.render(scale);
         this.rightFender.render(scale);
         this.rightStirrup.render(scale);
         this.leftSnaffleBit.render(scale);
         this.rightSnaffleBit.render(scale);
         if (var16) {
            this.leftReign.render(scale);
            this.rightReign.render(scale);
         }
      }

      if (!var11) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(var15, 0.5F + var15 * 0.5F, var15);
         GlStateManager.translatef(0.0F, 0.95F * (1.0F - var15), 0.0F);
      }

      this.leftThigh.render(scale);
      this.backLeftCannon.render(scale);
      this.backLeftHoof.render(scale);
      this.rightThigh.render(scale);
      this.backRightCannon.render(scale);
      this.backRightHoof.render(scale);
      this.leftForearm.render(scale);
      this.frontLeftCannon.render(scale);
      this.frontLeftHoof.render(scale);
      this.rightForearm.render(scale);
      this.frontRightCannon.render(scale);
      this.frontRightHoof.render(scale);
      if (!var11) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(var15, var15, var15);
         GlStateManager.translatef(0.0F, 1.35F * (1.0F - var15), 0.0F);
      }

      this.mainBody.render(scale);
      this.dock.render(scale);
      this.tail.render(scale);
      this.skirt.render(scale);
      this.neck.render(scale);
      this.mane.render(scale);
      if (!var11) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var17 = 0.5F + var15 * var15 * 0.5F;
         GlStateManager.scalef(var17, var17, var17);
         if (var10 <= 0.0F) {
            GlStateManager.translatef(0.0F, 1.35F * (1.0F - var15), 0.0F);
         } else {
            GlStateManager.translatef(0.0F, 0.9F * (1.0F - var15) * var10 + 1.35F * (1.0F - var15) * (1.0F - var10), 0.15F * (1.0F - var15) * var10);
         }
      }

      if (var14) {
         this.muleEarLeft.render(scale);
         this.muleEarRight.render(scale);
      } else {
         this.donkeyLeftEar.render(scale);
         this.donkeyRightEar.render(scale);
      }

      this.head.render(scale);
      if (!var11) {
         GlStateManager.popMatrix();
      }

      if (var13) {
         this.leftDonkeyChest.render(scale);
         this.rightDonkeyChest.render(scale);
      }
   }

   private void setModelRotation(ModelPart model, float rotationX, float rotationY, float rotationZ) {
      model.rotationX = rotationX;
      model.rotationY = rotationY;
      model.rotationZ = rotationZ;
   }

   private float tickRotation(float prevBodyYaw, float bodyYaw, float tickdelta) {
      float var4 = bodyYaw - prevBodyYaw;

      while(var4 < -180.0F) {
         var4 += 360.0F;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return prevBodyYaw + tickdelta * var4;
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      super.renderMobAnimation(entity, handSwing, handSwingAmount, tickDelta);
      float var5 = this.tickRotation(entity.prevBodyYaw, entity.bodyYaw, tickDelta);
      float var6 = this.tickRotation(entity.prevHeadYaw, entity.headYaw, tickDelta);
      float var7 = entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta;
      float var8 = var6 - var5;
      float var9 = var7 / (180.0F / (float)Math.PI);
      if (var8 > 20.0F) {
         var8 = 20.0F;
      }

      if (var8 < -20.0F) {
         var8 = -20.0F;
      }

      if (handSwingAmount > 0.2F) {
         var9 += MathHelper.cos(handSwing * 0.4F) * 0.15F * handSwingAmount;
      }

      HorseBaseEntity var10 = (HorseBaseEntity)entity;
      float var11 = var10.getGrassAnimationProgress(tickDelta);
      float var12 = var10.getAngryAnimationProgress(tickDelta);
      float var13 = 1.0F - var12;
      float var14 = var10.getEatingAnimationProgress(tickDelta);
      boolean var15 = var10.type != 0;
      boolean var16 = var10.isSaddled();
      boolean var17 = var10.rider != null;
      float var18 = (float)entity.time + tickDelta;
      float var19 = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI);
      float var20 = var19 * 0.8F * handSwingAmount;
      this.head.pivotY = 4.0F;
      this.head.pivotZ = -10.0F;
      this.dock.pivotY = 3.0F;
      this.tail.pivotZ = 14.0F;
      this.rightDonkeyChest.pivotY = 3.0F;
      this.rightDonkeyChest.pivotZ = 10.0F;
      this.mainBody.rotationX = 0.0F;
      this.head.rotationX = (float) (Math.PI / 6) + var9;
      this.head.rotationY = var8 / (180.0F / (float)Math.PI);
      this.head.rotationX = var12 * ((float) (Math.PI / 12) + var9) + var11 * 2.18166F + (1.0F - Math.max(var12, var11)) * this.head.rotationX;
      this.head.rotationY = var12 * var8 / (180.0F / (float)Math.PI) + (1.0F - Math.max(var12, var11)) * this.head.rotationY;
      this.head.pivotY = var12 * -6.0F + var11 * 11.0F + (1.0F - Math.max(var12, var11)) * this.head.pivotY;
      this.head.pivotZ = var12 * -1.0F + var11 * -10.0F + (1.0F - Math.max(var12, var11)) * this.head.pivotZ;
      this.dock.pivotY = var12 * 9.0F + var13 * this.dock.pivotY;
      this.tail.pivotZ = var12 * 18.0F + var13 * this.tail.pivotZ;
      this.rightDonkeyChest.pivotY = var12 * 5.5F + var13 * this.rightDonkeyChest.pivotY;
      this.rightDonkeyChest.pivotZ = var12 * 15.0F + var13 * this.rightDonkeyChest.pivotZ;
      this.mainBody.rotationX = var12 * -45.0F / (180.0F / (float)Math.PI) + var13 * this.mainBody.rotationX;
      this.donkeyLeftEar.pivotY = this.head.pivotY;
      this.donkeyRightEar.pivotY = this.head.pivotY;
      this.muleEarLeft.pivotY = this.head.pivotY;
      this.muleEarRight.pivotY = this.head.pivotY;
      this.neck.pivotY = this.head.pivotY;
      this.topJaw.pivotY = 0.02F;
      this.bottomJaw.pivotY = 0.0F;
      this.mane.pivotY = this.head.pivotY;
      this.donkeyLeftEar.pivotZ = this.head.pivotZ;
      this.donkeyRightEar.pivotZ = this.head.pivotZ;
      this.muleEarLeft.pivotZ = this.head.pivotZ;
      this.muleEarRight.pivotZ = this.head.pivotZ;
      this.neck.pivotZ = this.head.pivotZ;
      this.topJaw.pivotZ = 0.02F - var14 * 1.0F;
      this.bottomJaw.pivotZ = 0.0F + var14 * 1.0F;
      this.mane.pivotZ = this.head.pivotZ;
      this.donkeyLeftEar.rotationX = this.head.rotationX;
      this.donkeyRightEar.rotationX = this.head.rotationX;
      this.muleEarLeft.rotationX = this.head.rotationX;
      this.muleEarRight.rotationX = this.head.rotationX;
      this.neck.rotationX = this.head.rotationX;
      this.topJaw.rotationX = 0.0F - 0.09424778F * var14;
      this.bottomJaw.rotationX = 0.0F + ((float) (Math.PI / 20)) * var14;
      this.mane.rotationX = this.head.rotationX;
      this.donkeyLeftEar.rotationY = this.head.rotationY;
      this.donkeyRightEar.rotationY = this.head.rotationY;
      this.muleEarLeft.rotationY = this.head.rotationY;
      this.muleEarRight.rotationY = this.head.rotationY;
      this.neck.rotationY = this.head.rotationY;
      this.topJaw.rotationY = 0.0F;
      this.bottomJaw.rotationY = 0.0F;
      this.mane.rotationY = this.head.rotationY;
      this.leftDonkeyChest.rotationX = var20 / 5.0F;
      this.rightDonkeyChest.rotationX = -var20 / 5.0F;
      float var21 = (float) (Math.PI / 2);
      float var22 = (float) (Math.PI * 3.0 / 2.0);
      float var23 = (float) (-Math.PI / 3);
      float var24 = (float) (Math.PI / 12) * var12;
      float var25 = MathHelper.cos(var18 * 0.6F + (float) Math.PI);
      this.leftForearm.pivotY = -2.0F * var12 + 9.0F * var13;
      this.leftForearm.pivotZ = -2.0F * var12 + -8.0F * var13;
      this.rightForearm.pivotY = this.leftForearm.pivotY;
      this.rightForearm.pivotZ = this.leftForearm.pivotZ;
      this.backLeftCannon.pivotY = this.leftThigh.pivotY + MathHelper.sin((float) (Math.PI / 2) + var24 + var13 * -var19 * 0.5F * handSwingAmount) * 7.0F;
      this.backLeftCannon.pivotZ = this.leftThigh.pivotZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var24 + var13 * -var19 * 0.5F * handSwingAmount) * 7.0F;
      this.backRightCannon.pivotY = this.rightThigh.pivotY + MathHelper.sin((float) (Math.PI / 2) + var24 + var13 * var19 * 0.5F * handSwingAmount) * 7.0F;
      this.backRightCannon.pivotZ = this.rightThigh.pivotZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var24 + var13 * var19 * 0.5F * handSwingAmount) * 7.0F;
      float var26 = ((float) (-Math.PI / 3) + var25) * var12 + var20 * var13;
      float var27 = ((float) (-Math.PI / 3) + -var25) * var12 + -var20 * var13;
      this.frontLeftCannon.pivotY = this.leftForearm.pivotY + MathHelper.sin((float) (Math.PI / 2) + var26) * 7.0F;
      this.frontLeftCannon.pivotZ = this.leftForearm.pivotZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var26) * 7.0F;
      this.frontRightCannon.pivotY = this.rightForearm.pivotY + MathHelper.sin((float) (Math.PI / 2) + var27) * 7.0F;
      this.frontRightCannon.pivotZ = this.rightForearm.pivotZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + var27) * 7.0F;
      this.leftThigh.rotationX = var24 + -var19 * 0.5F * handSwingAmount * var13;
      this.backLeftCannon.rotationX = -0.08726646F * var12 + (-var19 * 0.5F * handSwingAmount - Math.max(0.0F, var19 * 0.5F * handSwingAmount)) * var13;
      this.backLeftHoof.rotationX = this.backLeftCannon.rotationX;
      this.rightThigh.rotationX = var24 + var19 * 0.5F * handSwingAmount * var13;
      this.backRightCannon.rotationX = -0.08726646F * var12 + (var19 * 0.5F * handSwingAmount - Math.max(0.0F, -var19 * 0.5F * handSwingAmount)) * var13;
      this.backRightHoof.rotationX = this.backRightCannon.rotationX;
      this.leftForearm.rotationX = var26;
      this.frontLeftCannon.rotationX = (this.leftForearm.rotationX + (float) Math.PI * Math.max(0.0F, 0.2F + var25 * 0.2F)) * var12
         + (var20 + Math.max(0.0F, var19 * 0.5F * handSwingAmount)) * var13;
      this.frontLeftHoof.rotationX = this.frontLeftCannon.rotationX;
      this.rightForearm.rotationX = var27;
      this.frontRightCannon.rotationX = (this.rightForearm.rotationX + (float) Math.PI * Math.max(0.0F, 0.2F - var25 * 0.2F)) * var12
         + (-var20 + Math.max(0.0F, -var19 * 0.5F * handSwingAmount)) * var13;
      this.frontRightHoof.rotationX = this.frontRightCannon.rotationX;
      this.backLeftHoof.pivotY = this.backLeftCannon.pivotY;
      this.backLeftHoof.pivotZ = this.backLeftCannon.pivotZ;
      this.backRightHoof.pivotY = this.backRightCannon.pivotY;
      this.backRightHoof.pivotZ = this.backRightCannon.pivotZ;
      this.frontLeftHoof.pivotY = this.frontLeftCannon.pivotY;
      this.frontLeftHoof.pivotZ = this.frontLeftCannon.pivotZ;
      this.frontRightHoof.pivotY = this.frontRightCannon.pivotY;
      this.frontRightHoof.pivotZ = this.frontRightCannon.pivotZ;
      if (var16) {
         this.saddleSeat.pivotY = var12 * 0.5F + var13 * 2.0F;
         this.saddleSeat.pivotZ = var12 * 11.0F + var13 * 2.0F;
         this.gullet.pivotY = this.saddleSeat.pivotY;
         this.cantle.pivotY = this.saddleSeat.pivotY;
         this.leftFender.pivotY = this.saddleSeat.pivotY;
         this.rightFender.pivotY = this.saddleSeat.pivotY;
         this.leftStirrup.pivotY = this.saddleSeat.pivotY;
         this.rightStirrup.pivotY = this.saddleSeat.pivotY;
         this.leftDonkeyChest.pivotY = this.rightDonkeyChest.pivotY;
         this.gullet.pivotZ = this.saddleSeat.pivotZ;
         this.cantle.pivotZ = this.saddleSeat.pivotZ;
         this.leftFender.pivotZ = this.saddleSeat.pivotZ;
         this.rightFender.pivotZ = this.saddleSeat.pivotZ;
         this.leftStirrup.pivotZ = this.saddleSeat.pivotZ;
         this.rightStirrup.pivotZ = this.saddleSeat.pivotZ;
         this.leftDonkeyChest.pivotZ = this.rightDonkeyChest.pivotZ;
         this.saddleSeat.rotationX = this.mainBody.rotationX;
         this.gullet.rotationX = this.mainBody.rotationX;
         this.cantle.rotationX = this.mainBody.rotationX;
         this.leftReign.pivotY = this.head.pivotY;
         this.rightReign.pivotY = this.head.pivotY;
         this.halster.pivotY = this.head.pivotY;
         this.leftSnaffleBit.pivotY = this.head.pivotY;
         this.rightSnaffleBit.pivotY = this.head.pivotY;
         this.leftReign.pivotZ = this.head.pivotZ;
         this.rightReign.pivotZ = this.head.pivotZ;
         this.halster.pivotZ = this.head.pivotZ;
         this.leftSnaffleBit.pivotZ = this.head.pivotZ;
         this.rightSnaffleBit.pivotZ = this.head.pivotZ;
         this.leftReign.rotationX = var9;
         this.rightReign.rotationX = var9;
         this.halster.rotationX = this.head.rotationX;
         this.leftSnaffleBit.rotationX = this.head.rotationX;
         this.rightSnaffleBit.rotationX = this.head.rotationX;
         this.halster.rotationY = this.head.rotationY;
         this.leftSnaffleBit.rotationY = this.head.rotationY;
         this.leftReign.rotationY = this.head.rotationY;
         this.rightSnaffleBit.rotationY = this.head.rotationY;
         this.rightReign.rotationY = this.head.rotationY;
         if (var17) {
            this.leftFender.rotationX = (float) (-Math.PI / 3);
            this.leftStirrup.rotationX = (float) (-Math.PI / 3);
            this.rightFender.rotationX = (float) (-Math.PI / 3);
            this.rightStirrup.rotationX = (float) (-Math.PI / 3);
            this.leftFender.rotationZ = 0.0F;
            this.leftStirrup.rotationZ = 0.0F;
            this.rightFender.rotationZ = 0.0F;
            this.rightStirrup.rotationZ = 0.0F;
         } else {
            this.leftFender.rotationX = var20 / 3.0F;
            this.leftStirrup.rotationX = var20 / 3.0F;
            this.rightFender.rotationX = var20 / 3.0F;
            this.rightStirrup.rotationX = var20 / 3.0F;
            this.leftFender.rotationZ = var20 / 5.0F;
            this.leftStirrup.rotationZ = var20 / 5.0F;
            this.rightFender.rotationZ = -var20 / 5.0F;
            this.rightStirrup.rotationZ = -var20 / 5.0F;
         }
      }

      var21 = -1.3089F + handSwingAmount * 1.5F;
      if (var21 > 0.0F) {
         var21 = 0.0F;
      }

      if (var15) {
         this.dock.rotationY = MathHelper.cos(var18 * 0.7F);
         var21 = 0.0F;
      } else {
         this.dock.rotationY = 0.0F;
      }

      this.tail.rotationY = this.dock.rotationY;
      this.skirt.rotationY = this.dock.rotationY;
      this.tail.pivotY = this.dock.pivotY;
      this.skirt.pivotY = this.dock.pivotY;
      this.tail.pivotZ = this.dock.pivotZ;
      this.skirt.pivotZ = this.dock.pivotZ;
      this.dock.rotationX = var21;
      this.tail.rotationX = var21;
      this.skirt.rotationX = -0.2618F + var21;
   }
}
