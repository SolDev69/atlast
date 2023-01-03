package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlazeModel extends Model {
   private ModelPart[] rods = new ModelPart[12];
   private ModelPart head;

   public BlazeModel() {
      for(int var1 = 0; var1 < this.rods.length; ++var1) {
         this.rods[var1] = new ModelPart(this, 0, 16);
         this.rods[var1].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      }

      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.render(scale);

      for(int var8 = 0; var8 < this.rods.length; ++var8) {
         this.rods[var8].render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      float var8 = age * (float) Math.PI * -0.1F;

      for(int var9 = 0; var9 < 4; ++var9) {
         this.rods[var9].pivotY = -2.0F + MathHelper.cos(((float)(var9 * 2) + age) * 0.25F);
         this.rods[var9].pivotX = MathHelper.cos(var8) * 9.0F;
         this.rods[var9].pivotZ = MathHelper.sin(var8) * 9.0F;
         ++var8;
      }

      var8 = (float) (Math.PI / 4) + age * (float) Math.PI * 0.03F;

      for(int var12 = 4; var12 < 8; ++var12) {
         this.rods[var12].pivotY = 2.0F + MathHelper.cos(((float)(var12 * 2) + age) * 0.25F);
         this.rods[var12].pivotX = MathHelper.cos(var8) * 7.0F;
         this.rods[var12].pivotZ = MathHelper.sin(var8) * 7.0F;
         ++var8;
      }

      var8 = 0.47123894F + age * (float) Math.PI * -0.05F;

      for(int var13 = 8; var13 < 12; ++var13) {
         this.rods[var13].pivotY = 11.0F + MathHelper.cos(((float)var13 * 1.5F + age) * 0.5F);
         this.rods[var13].pivotX = MathHelper.cos(var8) * 5.0F;
         this.rods[var13].pivotZ = MathHelper.sin(var8) * 5.0F;
         ++var8;
      }

      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
   }
}
