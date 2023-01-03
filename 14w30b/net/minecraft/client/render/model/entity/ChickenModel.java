package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChickenModel extends Model {
   public ModelPart head;
   public ModelPart body;
   public ModelPart rightLeg;
   public ModelPart leftLeg;
   public ModelPart rightWing;
   public ModelPart leftWing;
   public ModelPart beak;
   public ModelPart waddle;

   public ChickenModel() {
      byte var1 = 16;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
      this.head.setPivot(0.0F, (float)(-1 + var1), -4.0F);
      this.beak = new ModelPart(this, 14, 0);
      this.beak.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
      this.beak.setPivot(0.0F, (float)(-1 + var1), -4.0F);
      this.waddle = new ModelPart(this, 14, 4);
      this.waddle.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
      this.waddle.setPivot(0.0F, (float)(-1 + var1), -4.0F);
      this.body = new ModelPart(this, 0, 9);
      this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
      this.body.setPivot(0.0F, (float)var1, 0.0F);
      this.rightLeg = new ModelPart(this, 26, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.rightLeg.setPivot(-2.0F, (float)(3 + var1), 1.0F);
      this.leftLeg = new ModelPart(this, 26, 0);
      this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.leftLeg.setPivot(1.0F, (float)(3 + var1), 1.0F);
      this.rightWing = new ModelPart(this, 24, 13);
      this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
      this.rightWing.setPivot(-4.0F, (float)(-3 + var1), 0.0F);
      this.leftWing = new ModelPart(this, 24, 13);
      this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
      this.leftWing.setPivot(4.0F, (float)(-3 + var1), 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
         this.head.render(scale);
         this.beak.render(scale);
         this.waddle.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.rightLeg.render(scale);
         this.leftLeg.render(scale);
         this.rightWing.render(scale);
         this.leftWing.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.beak.render(scale);
         this.waddle.render(scale);
         this.body.render(scale);
         this.rightLeg.render(scale);
         this.leftLeg.render(scale);
         this.rightWing.render(scale);
         this.leftWing.render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.beak.rotationX = this.head.rotationX;
      this.beak.rotationY = this.head.rotationY;
      this.waddle.rotationX = this.head.rotationX;
      this.waddle.rotationY = this.head.rotationY;
      this.body.rotationX = (float) (Math.PI / 2);
      this.rightLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      this.leftLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.rightWing.rotationZ = age;
      this.leftWing.rotationZ = -age;
   }
}
