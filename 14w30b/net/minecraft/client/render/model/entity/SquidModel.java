package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SquidModel extends Model {
   ModelPart body;
   ModelPart[] legs = new ModelPart[8];

   public SquidModel() {
      byte var1 = -16;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      this.body.pivotY += (float)(24 + var1);

      for(int var2 = 0; var2 < this.legs.length; ++var2) {
         this.legs[var2] = new ModelPart(this, 48, 0);
         double var3 = (double)var2 * Math.PI * 2.0 / (double)this.legs.length;
         float var5 = (float)Math.cos(var3) * 5.0F;
         float var6 = (float)Math.sin(var3) * 5.0F;
         this.legs[var2].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.legs[var2].pivotX = var5;
         this.legs[var2].pivotZ = var6;
         this.legs[var2].pivotY = (float)(31 + var1);
         var3 = (double)var2 * Math.PI * -2.0 / (double)this.legs.length + (Math.PI / 2);
         this.legs[var2].rotationY = (float)var3;
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      for(ModelPart var11 : this.legs) {
         var11.rotationX = age;
      }
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.body.render(scale);

      for(int var8 = 0; var8 < this.legs.length; ++var8) {
         this.legs[var8].render(scale);
      }
   }
}
