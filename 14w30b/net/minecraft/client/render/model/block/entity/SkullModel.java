package net.minecraft.client.render.model.block.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkullModel extends Model {
   public ModelPart model;

   public SkullModel() {
      this(0, 35, 64, 64);
   }

   public SkullModel(int textureOffsetU, int textureOffsetV, int textureWidth, int textureHeight) {
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.model = new ModelPart(this, textureOffsetU, textureOffsetV);
      this.model.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.model.setPivot(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.model.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.model.rotationY = yaw / (180.0F / (float)Math.PI);
      this.model.rotationX = pitch / (180.0F / (float)Math.PI);
   }
}
