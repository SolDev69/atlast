package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerModel extends HumanoidModel {
   public ModelPart f_55ogitseo;
   public ModelPart f_17nzqhcqy;
   public ModelPart f_39rzcdjce;
   public ModelPart f_04tvtlvbb;
   public ModelPart f_80hdikubr;
   private ModelPart cape;
   private ModelPart ears;
   private boolean thinArms;

   public PlayerModel(float reduction, boolean thinArms) {
      super(reduction, 0.0F, 64, 64);
      this.thinArms = thinArms;
      this.ears = new ModelPart(this, 24, 0);
      this.ears.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, reduction);
      this.cape = new ModelPart(this, 0, 0);
      this.cape.setTextureSize(64, 32);
      this.cape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, reduction);
      if (thinArms) {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, reduction);
         this.leftArm.setPivot(5.0F, 2.5F, 0.0F);
         this.rightArm = new ModelPart(this, 40, 16);
         this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, reduction);
         this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);
         this.f_55ogitseo = new ModelPart(this, 48, 48);
         this.f_55ogitseo.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, reduction + 0.25F);
         this.f_55ogitseo.setPivot(5.0F, 2.5F, 0.0F);
         this.f_17nzqhcqy = new ModelPart(this, 40, 32);
         this.f_17nzqhcqy.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, reduction + 0.25F);
         this.f_17nzqhcqy.setPivot(-5.0F, 2.5F, 10.0F);
      } else {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, reduction);
         this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
         this.f_55ogitseo = new ModelPart(this, 48, 48);
         this.f_55ogitseo.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, reduction + 0.25F);
         this.f_55ogitseo.setPivot(5.0F, 2.0F, 0.0F);
         this.f_17nzqhcqy = new ModelPart(this, 40, 32);
         this.f_17nzqhcqy.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, reduction + 0.25F);
         this.f_17nzqhcqy.setPivot(-5.0F, 2.0F, 10.0F);
      }

      this.leftLeg = new ModelPart(this, 16, 48);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, reduction);
      this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
      this.f_39rzcdjce = new ModelPart(this, 0, 48);
      this.f_39rzcdjce.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, reduction + 0.25F);
      this.f_39rzcdjce.setPivot(1.9F, 12.0F, 0.0F);
      this.f_04tvtlvbb = new ModelPart(this, 0, 32);
      this.f_04tvtlvbb.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, reduction + 0.25F);
      this.f_04tvtlvbb.setPivot(-1.9F, 12.0F, 0.0F);
      this.f_80hdikubr = new ModelPart(this, 16, 32);
      this.f_80hdikubr.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, reduction + 0.25F);
      this.f_80hdikubr.setPivot(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      super.render(entity, handSwing, handSwingAmount, age, yaw, pitch, scale);
      GlStateManager.pushMatrix();
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.f_39rzcdjce.render(scale);
         this.f_04tvtlvbb.render(scale);
         this.f_55ogitseo.render(scale);
         this.f_17nzqhcqy.render(scale);
         this.f_80hdikubr.render(scale);
      } else {
         if (entity.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.f_39rzcdjce.render(scale);
         this.f_04tvtlvbb.render(scale);
         this.f_55ogitseo.render(scale);
         this.f_17nzqhcqy.render(scale);
         this.f_80hdikubr.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void renderEars(float tickDelta) {
      copyRotation(this.head, this.ears);
      this.ears.pivotX = 0.0F;
      this.ears.pivotY = 0.0F;
      this.ears.render(tickDelta);
   }

   public void renderCape(float tickDelta) {
      this.cape.render(tickDelta);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      copyRotation(this.leftLeg, this.f_39rzcdjce);
      copyRotation(this.rightLeg, this.f_04tvtlvbb);
      copyRotation(this.leftArm, this.f_55ogitseo);
      copyRotation(this.rightArm, this.f_17nzqhcqy);
      copyRotation(this.body, this.f_80hdikubr);
      if (entity.isSneaking()) {
         this.cape.pivotY = 2.0F;
      } else {
         this.cape.pivotY = 0.0F;
      }
   }

   public void m_37hhumyue() {
      this.rightArm.render(0.0625F);
      this.f_17nzqhcqy.render(0.0625F);
   }

   public void m_95hdkquge() {
      this.leftArm.render(0.0625F);
      this.f_55ogitseo.render(0.0625F);
   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      this.f_55ogitseo.visible = visible;
      this.f_17nzqhcqy.visible = visible;
      this.f_39rzcdjce.visible = visible;
      this.f_04tvtlvbb.visible = visible;
      this.f_80hdikubr.visible = visible;
      this.cape.visible = visible;
      this.ears.visible = visible;
   }

   @Override
   public void translateRightArm(float scale) {
      if (this.thinArms) {
         ++this.rightArm.pivotX;
         this.rightArm.translate(scale);
         --this.rightArm.pivotX;
      } else {
         this.rightArm.translate(scale);
      }
   }
}
