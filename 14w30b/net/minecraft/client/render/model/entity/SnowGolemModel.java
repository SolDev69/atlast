package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnowGolemModel extends Model {
   public ModelPart top;
   public ModelPart bottom;
   public ModelPart head;
   public ModelPart rightArm;
   public ModelPart leftArm;

   public SnowGolemModel() {
      float var1 = 4.0F;
      float var2 = 0.0F;
      this.head = new ModelPart(this, 0, 0).setTextureSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var2 - 0.5F);
      this.head.setPivot(0.0F, 0.0F + var1, 0.0F);
      this.rightArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
      this.rightArm.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
      this.rightArm.setPivot(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
      this.leftArm = new ModelPart(this, 32, 0).setTextureSize(64, 64);
      this.leftArm.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, var2 - 0.5F);
      this.leftArm.setPivot(0.0F, 0.0F + var1 + 9.0F - 7.0F, 0.0F);
      this.top = new ModelPart(this, 0, 16).setTextureSize(64, 64);
      this.top.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, var2 - 0.5F);
      this.top.setPivot(0.0F, 0.0F + var1 + 9.0F, 0.0F);
      this.bottom = new ModelPart(this, 0, 36).setTextureSize(64, 64);
      this.bottom.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, var2 - 0.5F);
      this.bottom.setPivot(0.0F, 0.0F + var1 + 20.0F, 0.0F);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.top.rotationY = yaw / (180.0F / (float)Math.PI) * 0.25F;
      float var8 = MathHelper.sin(this.top.rotationY);
      float var9 = MathHelper.cos(this.top.rotationY);
      this.rightArm.rotationZ = 1.0F;
      this.leftArm.rotationZ = -1.0F;
      this.rightArm.rotationY = 0.0F + this.top.rotationY;
      this.leftArm.rotationY = (float) Math.PI + this.top.rotationY;
      this.rightArm.pivotX = var9 * 5.0F;
      this.rightArm.pivotZ = -var8 * 5.0F;
      this.leftArm.pivotX = -var9 * 5.0F;
      this.leftArm.pivotZ = var8 * 5.0F;
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.top.render(scale);
      this.bottom.render(scale);
      this.head.render(scale);
      this.rightArm.render(scale);
      this.leftArm.render(scale);
   }
}
