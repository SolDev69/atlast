package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BatModel extends Model {
   private ModelPart head;
   private ModelPart body;
   private ModelPart rightWing;
   private ModelPart leftWing;
   private ModelPart rightWingTip;
   private ModelPart leftWingTip;

   public BatModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      ModelPart var1 = new ModelPart(this, 24, 0);
      var1.addBox(-4.0F, -6.0F, -2.0F, 3, 4, 1);
      this.head.addChild(var1);
      ModelPart var2 = new ModelPart(this, 24, 0);
      var2.flipped = true;
      var2.addBox(1.0F, -6.0F, -2.0F, 3, 4, 1);
      this.head.addChild(var2);
      this.body = new ModelPart(this, 0, 16);
      this.body.addBox(-3.0F, 4.0F, -3.0F, 6, 12, 6);
      this.body.setTextureCoords(0, 34).addBox(-5.0F, 16.0F, 0.0F, 10, 6, 1);
      this.rightWing = new ModelPart(this, 42, 0);
      this.rightWing.addBox(-12.0F, 1.0F, 1.5F, 10, 16, 1);
      this.rightWingTip = new ModelPart(this, 24, 16);
      this.rightWingTip.setPivot(-12.0F, 1.0F, 1.5F);
      this.rightWingTip.addBox(-8.0F, 1.0F, 0.0F, 8, 12, 1);
      this.leftWing = new ModelPart(this, 42, 0);
      this.leftWing.flipped = true;
      this.leftWing.addBox(2.0F, 1.0F, 1.5F, 10, 16, 1);
      this.leftWingTip = new ModelPart(this, 24, 16);
      this.leftWingTip.flipped = true;
      this.leftWingTip.setPivot(12.0F, 1.0F, 1.5F);
      this.leftWingTip.addBox(0.0F, 1.0F, 0.0F, 8, 12, 1);
      this.body.addChild(this.rightWing);
      this.body.addChild(this.leftWing);
      this.rightWing.addChild(this.rightWingTip);
      this.leftWing.addChild(this.leftWingTip);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.render(scale);
      this.body.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      if (((BatEntity)entity).isRoosting()) {
         float var8 = 180.0F / (float)Math.PI;
         this.head.rotationX = pitch / (180.0F / (float)Math.PI);
         this.head.rotationY = (float) Math.PI - yaw / (180.0F / (float)Math.PI);
         this.head.rotationZ = (float) Math.PI;
         this.head.setPivot(0.0F, -2.0F, 0.0F);
         this.rightWing.setPivot(-3.0F, 0.0F, 3.0F);
         this.leftWing.setPivot(3.0F, 0.0F, 3.0F);
         this.body.rotationX = (float) Math.PI;
         this.rightWing.rotationX = (float) (-Math.PI / 20);
         this.rightWing.rotationY = (float) (-Math.PI * 2.0 / 5.0);
         this.rightWingTip.rotationY = -1.7278761F;
         this.leftWing.rotationX = this.rightWing.rotationX;
         this.leftWing.rotationY = -this.rightWing.rotationY;
         this.leftWingTip.rotationY = -this.rightWingTip.rotationY;
      } else {
         float var9 = 180.0F / (float)Math.PI;
         this.head.rotationX = pitch / (180.0F / (float)Math.PI);
         this.head.rotationY = yaw / (180.0F / (float)Math.PI);
         this.head.rotationZ = 0.0F;
         this.head.setPivot(0.0F, 0.0F, 0.0F);
         this.rightWing.setPivot(0.0F, 0.0F, 0.0F);
         this.leftWing.setPivot(0.0F, 0.0F, 0.0F);
         this.body.rotationX = (float) (Math.PI / 4) + MathHelper.cos(age * 0.1F) * 0.15F;
         this.body.rotationY = 0.0F;
         this.rightWing.rotationY = MathHelper.cos(age * 1.3F) * (float) Math.PI * 0.25F;
         this.leftWing.rotationY = -this.rightWing.rotationY;
         this.rightWingTip.rotationY = this.rightWing.rotationY * 0.5F;
         this.leftWingTip.rotationY = -this.rightWing.rotationY * 0.5F;
      }
   }
}
