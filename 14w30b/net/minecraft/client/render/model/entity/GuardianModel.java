package net.minecraft.client.render.model.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuardianModel extends Model {
   private ModelPart head;
   private ModelPart eye;
   private ModelPart[] spikes;
   private ModelPart[] tail;

   public GuardianModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.spikes = new ModelPart[12];
      this.head = new ModelPart(this);
      this.head.setTextureCoords(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.head.setTextureCoords(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.head.setTextureCoords(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.head.setTextureCoords(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.head.setTextureCoords(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

      for(int var1 = 0; var1 < this.spikes.length; ++var1) {
         this.spikes[var1] = new ModelPart(this, 0, 0);
         this.spikes[var1].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
         this.head.addChild(this.spikes[var1]);
      }

      this.eye = new ModelPart(this, 8, 0);
      this.eye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.head.addChild(this.eye);
      this.tail = new ModelPart[3];
      this.tail[0] = new ModelPart(this, 40, 0);
      this.tail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
      this.tail[1] = new ModelPart(this, 0, 54);
      this.tail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
      this.tail[2] = new ModelPart(this);
      this.tail[2].setTextureCoords(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
      this.tail[2].setTextureCoords(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
      this.head.addChild(this.tail[0]);
      this.tail[0].addChild(this.tail[1]);
      this.tail[1].addChild(this.tail[2]);
   }

   public int m_60xlazxax() {
      return 54;
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      GuardianEntity var8 = (GuardianEntity)entity;
      float var9 = age - (float)var8.time;
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      float[] var10 = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
      float[] var11 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
      float[] var12 = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
      float[] var13 = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
      float[] var14 = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
      float[] var15 = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
      float var16 = (1.0F - var8.m_51aqfgnbj(var9)) * 0.55F;

      for(int var17 = 0; var17 < 12; ++var17) {
         this.spikes[var17].rotationX = (float) Math.PI * var10[var17];
         this.spikes[var17].rotationY = (float) Math.PI * var11[var17];
         this.spikes[var17].rotationZ = (float) Math.PI * var12[var17];
         this.spikes[var17].pivotX = var13[var17] * (1.0F + MathHelper.cos(age * 1.5F + (float)var17) * 0.01F - var16);
         this.spikes[var17].pivotY = 16.0F + var14[var17] * (1.0F + MathHelper.cos(age * 1.5F + (float)var17) * 0.01F - var16);
         this.spikes[var17].pivotZ = var15[var17] * (1.0F + MathHelper.cos(age * 1.5F + (float)var17) * 0.01F - var16);
      }

      this.eye.pivotZ = -8.25F;
      Object var26 = MinecraftClient.getInstance().getCamera();
      if (var8.m_16dqbnsqq()) {
         var26 = var8.m_74mbxcnur();
      }

      if (var26 != null) {
         Vec3d var18 = ((Entity)var26).m_24itdohjr(0.0F);
         Vec3d var19 = entity.m_24itdohjr(0.0F);
         double var20 = var18.y - var19.y;
         if (var20 > 0.0) {
            this.eye.pivotY = 0.0F;
         } else {
            this.eye.pivotY = 1.0F;
         }

         Vec3d var22 = entity.m_01qqqsfds(0.0F);
         var22 = new Vec3d(var22.x, 0.0, var22.z);
         Vec3d var23 = new Vec3d(var19.x - var18.x, 0.0, var19.z - var18.z).normalize().rotateY((float) (Math.PI / 2));
         double var24 = var22.dot(var23);
         this.eye.pivotX = MathHelper.sqrt((float)Math.abs(var24)) * 2.0F * (float)Math.signum(var24);
      }

      this.eye.visible = true;
      float var27 = var8.m_34qaovacf(var9);
      this.tail[0].rotationY = MathHelper.sin(var27) * (float) Math.PI * 0.05F;
      this.tail[1].rotationY = MathHelper.sin(var27) * (float) Math.PI * 0.1F;
      this.tail[1].pivotX = -1.5F;
      this.tail[1].pivotY = 0.5F;
      this.tail[1].pivotZ = 14.0F;
      this.tail[2].rotationY = MathHelper.sin(var27) * (float) Math.PI * 0.15F;
      this.tail[2].pivotX = 0.5F;
      this.tail[2].pivotY = 0.5F;
      this.tail[2].pivotZ = 6.0F;
   }
}
