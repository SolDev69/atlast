package net.minecraft.client.render.model.block.entity;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HumanoidSkullModel extends SkullModel {
   private final ModelPart hat = new ModelPart(this, 32, 0);

   public HumanoidSkullModel() {
      super(0, 0, 64, 64);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
      this.hat.setPivot(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      super.render(entity, handSwing, handSwingAmount, age, yaw, pitch, scale);
      this.hat.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      super.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.hat.rotationY = this.model.rotationY;
      this.hat.rotationX = this.model.rotationX;
   }
}
