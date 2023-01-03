package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderDragonModel extends Model {
   private ModelPart head;
   private ModelPart neck;
   private ModelPart jaw;
   private ModelPart body;
   private ModelPart rearLeg;
   private ModelPart frontLeg;
   private ModelPart rearLegTip;
   private ModelPart frontLegTip;
   private ModelPart rearFoot;
   private ModelPart frontFoot;
   private ModelPart wing;
   private ModelPart wingTip;
   private float tickDelta;

   public EnderDragonModel(float f) {
      this.textureWidth = 256;
      this.textureHeight = 256;
      this.setTexturePos("body.body", 0, 0);
      this.setTexturePos("wing.skin", -56, 88);
      this.setTexturePos("wingtip.skin", -56, 144);
      this.setTexturePos("rearleg.main", 0, 0);
      this.setTexturePos("rearfoot.main", 112, 0);
      this.setTexturePos("rearlegtip.main", 196, 0);
      this.setTexturePos("head.upperhead", 112, 30);
      this.setTexturePos("wing.bone", 112, 88);
      this.setTexturePos("head.upperlip", 176, 44);
      this.setTexturePos("jaw.jaw", 176, 65);
      this.setTexturePos("frontleg.main", 112, 104);
      this.setTexturePos("wingtip.bone", 112, 136);
      this.setTexturePos("frontfoot.main", 144, 104);
      this.setTexturePos("neck.box", 192, 104);
      this.setTexturePos("frontlegtip.main", 226, 138);
      this.setTexturePos("body.scale", 220, 53);
      this.setTexturePos("head.scale", 0, 0);
      this.setTexturePos("neck.scale", 48, 0);
      this.setTexturePos("head.nostril", 112, 0);
      float var2 = -16.0F;
      this.head = new ModelPart(this, "head");
      this.head.addBox("upperlip", -6.0F, -1.0F, -8.0F + var2, 12, 5, 16);
      this.head.addBox("upperhead", -8.0F, -8.0F, 6.0F + var2, 16, 16, 16);
      this.head.flipped = true;
      this.head.addBox("scale", -5.0F, -12.0F, 12.0F + var2, 2, 4, 6);
      this.head.addBox("nostril", -5.0F, -3.0F, -6.0F + var2, 2, 2, 4);
      this.head.flipped = false;
      this.head.addBox("scale", 3.0F, -12.0F, 12.0F + var2, 2, 4, 6);
      this.head.addBox("nostril", 3.0F, -3.0F, -6.0F + var2, 2, 2, 4);
      this.jaw = new ModelPart(this, "jaw");
      this.jaw.setPivot(0.0F, 4.0F, 8.0F + var2);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
      this.head.addChild(this.jaw);
      this.neck = new ModelPart(this, "neck");
      this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
      this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
      this.body = new ModelPart(this, "body");
      this.body.setPivot(0.0F, 4.0F, 8.0F);
      this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
      this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
      this.wing = new ModelPart(this, "wing");
      this.wing.setPivot(-12.0F, 5.0F, 2.0F);
      this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
      this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wingTip = new ModelPart(this, "wingtip");
      this.wingTip.setPivot(-56.0F, 0.0F, 0.0F);
      this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
      this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wing.addChild(this.wingTip);
      this.frontLeg = new ModelPart(this, "frontleg");
      this.frontLeg.setPivot(-12.0F, 20.0F, 2.0F);
      this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
      this.frontLegTip = new ModelPart(this, "frontlegtip");
      this.frontLegTip.setPivot(0.0F, 20.0F, -1.0F);
      this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
      this.frontLeg.addChild(this.frontLegTip);
      this.frontFoot = new ModelPart(this, "frontfoot");
      this.frontFoot.setPivot(0.0F, 23.0F, 0.0F);
      this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
      this.frontLegTip.addChild(this.frontFoot);
      this.rearLeg = new ModelPart(this, "rearleg");
      this.rearLeg.setPivot(-16.0F, 16.0F, 42.0F);
      this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
      this.rearLegTip = new ModelPart(this, "rearlegtip");
      this.rearLegTip.setPivot(0.0F, 32.0F, -4.0F);
      this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
      this.rearLeg.addChild(this.rearLegTip);
      this.rearFoot = new ModelPart(this, "rearfoot");
      this.rearFoot.setPivot(0.0F, 31.0F, 4.0F);
      this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
      this.rearLegTip.addChild(this.rearFoot);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      this.tickDelta = tickDelta;
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      GlStateManager.pushMatrix();
      EnderDragonEntity var8 = (EnderDragonEntity)entity;
      float var9 = var8.lastWingPosition + (var8.wingPosition - var8.lastWingPosition) * this.tickDelta;
      this.jaw.rotationX = (float)(Math.sin((double)(var9 * (float) Math.PI * 2.0F)) + 1.0) * 0.2F;
      float var10 = (float)(Math.sin((double)(var9 * (float) Math.PI * 2.0F - 1.0F)) + 1.0);
      var10 = (var10 * var10 * 1.0F + var10 * 2.0F) * 0.05F;
      GlStateManager.translatef(0.0F, var10 - 2.0F, -3.0F);
      GlStateManager.rotatef(var10 * 2.0F, 1.0F, 0.0F, 0.0F);
      float var11 = -30.0F;
      float var13 = 0.0F;
      float var14 = 1.5F;
      double[] var15 = var8.getSegmentProperties(6, this.tickDelta);
      float var16 = this.tickRotation(var8.getSegmentProperties(5, this.tickDelta)[0] - var8.getSegmentProperties(10, this.tickDelta)[0]);
      float var17 = this.tickRotation(var8.getSegmentProperties(5, this.tickDelta)[0] + (double)(var16 / 2.0F));
      var11 += 2.0F;
      float var18 = var9 * (float) Math.PI * 2.0F;
      var11 = 20.0F;
      float var12 = -12.0F;

      for(int var19 = 0; var19 < 5; ++var19) {
         double[] var20 = var8.getSegmentProperties(5 - var19, this.tickDelta);
         float var21 = (float)Math.cos((double)((float)var19 * 0.45F + var18)) * 0.15F;
         this.neck.rotationY = this.tickRotation(var20[0] - var15[0]) * (float) Math.PI / 180.0F * var14;
         this.neck.rotationX = var21 + (float)(var20[1] - var15[1]) * (float) Math.PI / 180.0F * var14 * 5.0F;
         this.neck.rotationZ = -this.tickRotation(var20[0] - (double)var17) * (float) Math.PI / 180.0F * var14;
         this.neck.pivotY = var11;
         this.neck.pivotZ = var12;
         this.neck.pivotX = var13;
         var11 = (float)((double)var11 + Math.sin((double)this.neck.rotationX) * 10.0);
         var12 = (float)((double)var12 - Math.cos((double)this.neck.rotationY) * Math.cos((double)this.neck.rotationX) * 10.0);
         var13 = (float)((double)var13 - Math.sin((double)this.neck.rotationY) * Math.cos((double)this.neck.rotationX) * 10.0);
         this.neck.render(scale);
      }

      this.head.pivotY = var11;
      this.head.pivotZ = var12;
      this.head.pivotX = var13;
      double[] var30 = var8.getSegmentProperties(0, this.tickDelta);
      this.head.rotationY = this.tickRotation(var30[0] - var15[0]) * (float) Math.PI / 180.0F * 1.0F;
      this.head.rotationZ = -this.tickRotation(var30[0] - (double)var17) * (float) Math.PI / 180.0F * 1.0F;
      this.head.render(scale);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var16 * var14 * 1.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      this.body.rotationZ = 0.0F;
      this.body.render(scale);

      for(int var32 = 0; var32 < 2; ++var32) {
         GlStateManager.enableCull();
         float var34 = var9 * (float) Math.PI * 2.0F;
         this.wing.rotationX = 0.125F - (float)Math.cos((double)var34) * 0.2F;
         this.wing.rotationY = 0.25F;
         this.wing.rotationZ = (float)(Math.sin((double)var34) + 0.125) * 0.8F;
         this.wingTip.rotationZ = -((float)(Math.sin((double)(var34 + 2.0F)) + 0.5)) * 0.75F;
         this.rearLeg.rotationX = 1.0F + var10 * 0.1F;
         this.rearLegTip.rotationX = 0.5F + var10 * 0.1F;
         this.rearFoot.rotationX = 0.75F + var10 * 0.1F;
         this.frontLeg.rotationX = 1.3F + var10 * 0.1F;
         this.frontLegTip.rotationX = -0.5F - var10 * 0.1F;
         this.frontFoot.rotationX = 0.75F + var10 * 0.1F;
         this.wing.render(scale);
         this.frontLeg.render(scale);
         this.rearLeg.render(scale);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         if (var32 == 0) {
            GlStateManager.cullFace(1028);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.cullFace(1029);
      GlStateManager.disableCull();
      float var33 = -((float)Math.sin((double)(var9 * (float) Math.PI * 2.0F))) * 0.0F;
      var18 = var9 * (float) Math.PI * 2.0F;
      var11 = 10.0F;
      var12 = 60.0F;
      var13 = 0.0F;
      var15 = var8.getSegmentProperties(11, this.tickDelta);

      for(int var35 = 0; var35 < 12; ++var35) {
         var30 = var8.getSegmentProperties(12 + var35, this.tickDelta);
         var33 = (float)((double)var33 + Math.sin((double)((float)var35 * 0.45F + var18)) * 0.05F);
         this.neck.rotationY = (this.tickRotation(var30[0] - var15[0]) * var14 + 180.0F) * (float) Math.PI / 180.0F;
         this.neck.rotationX = var33 + (float)(var30[1] - var15[1]) * (float) Math.PI / 180.0F * var14 * 5.0F;
         this.neck.rotationZ = this.tickRotation(var30[0] - (double)var17) * (float) Math.PI / 180.0F * var14;
         this.neck.pivotY = var11;
         this.neck.pivotZ = var12;
         this.neck.pivotX = var13;
         var11 = (float)((double)var11 + Math.sin((double)this.neck.rotationX) * 10.0);
         var12 = (float)((double)var12 - Math.cos((double)this.neck.rotationY) * Math.cos((double)this.neck.rotationX) * 10.0);
         var13 = (float)((double)var13 - Math.sin((double)this.neck.rotationY) * Math.cos((double)this.neck.rotationX) * 10.0);
         this.neck.render(scale);
      }

      GlStateManager.popMatrix();
   }

   private float tickRotation(double rotation) {
      while(rotation >= 180.0) {
         rotation -= 360.0;
      }

      while(rotation < -180.0) {
         rotation += 360.0;
      }

      return (float)rotation;
   }
}
