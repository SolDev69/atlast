package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherModel extends Model {
   private ModelPart[] body;
   private ModelPart[] skulls;

   public WitherModel(float f) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.body = new ModelPart[3];
      this.body[0] = new ModelPart(this, 0, 16);
      this.body[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
      this.body[1] = new ModelPart(this).setTextureSize(this.textureWidth, this.textureHeight);
      this.body[1].setPivot(-2.0F, 6.9F, -0.5F);
      this.body[1].setTextureCoords(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
      this.body[1].setTextureCoords(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
      this.body[1].setTextureCoords(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
      this.body[1].setTextureCoords(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
      this.body[2] = new ModelPart(this, 12, 22);
      this.body[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
      this.skulls = new ModelPart[3];
      this.skulls[0] = new ModelPart(this, 0, 0);
      this.skulls[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
      this.skulls[1] = new ModelPart(this, 32, 0);
      this.skulls[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.skulls[1].pivotX = -8.0F;
      this.skulls[1].pivotY = 4.0F;
      this.skulls[2] = new ModelPart(this, 32, 0);
      this.skulls[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.skulls[2].pivotX = 10.0F;
      this.skulls[2].pivotY = 4.0F;
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);

      for(ModelPart var11 : this.skulls) {
         var11.render(scale);
      }

      for(ModelPart var15 : this.body) {
         var15.render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      float var8 = MathHelper.cos(age * 0.1F);
      this.body[1].rotationX = (0.065F + 0.05F * var8) * (float) Math.PI;
      this.body[2].setPivot(-2.0F, 6.9F + MathHelper.cos(this.body[1].rotationX) * 10.0F, -0.5F + MathHelper.sin(this.body[1].rotationX) * 10.0F);
      this.body[2].rotationX = (0.265F + 0.1F * var8) * (float) Math.PI;
      this.skulls[0].rotationY = yaw / (180.0F / (float)Math.PI);
      this.skulls[0].rotationX = pitch / (180.0F / (float)Math.PI);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      WitherEntity var5 = (WitherEntity)entity;

      for(int var6 = 1; var6 < 3; ++var6) {
         this.skulls[var6].rotationY = (var5.getHeadYaw(var6 - 1) - entity.bodyYaw) / (180.0F / (float)Math.PI);
         this.skulls[var6].rotationX = var5.getHeadPitch(var6 - 1) / (180.0F / (float)Math.PI);
      }
   }
}
