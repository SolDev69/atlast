package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LeashModel extends Model {
   public ModelPart leash;

   public LeashModel() {
      this(0, 0, 32, 32);
   }

   public LeashModel(int textureOffsetU, int textureOffsetV, int textureWidth, int textureHeight) {
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.leash = new ModelPart(this, textureOffsetU, textureOffsetV);
      this.leash.addBox(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
      this.leash.setPivot(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.leash.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.leash.rotationY = yaw / (180.0F / (float)Math.PI);
      this.leash.rotationX = pitch / (180.0F / (float)Math.PI);
   }
}
