package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MagmaCubeModel extends Model {
   ModelPart[] slices = new ModelPart[8];
   ModelPart body;

   public MagmaCubeModel() {
      for(int var1 = 0; var1 < this.slices.length; ++var1) {
         byte var2 = 0;
         int var3 = var1;
         if (var1 == 2) {
            var2 = 24;
            var3 = 10;
         } else if (var1 == 3) {
            var2 = 24;
            var3 = 19;
         }

         this.slices[var1] = new ModelPart(this, var2, var3);
         this.slices[var1].addBox(-4.0F, (float)(16 + var1), -4.0F, 8, 1, 8);
      }

      this.body = new ModelPart(this, 0, 16);
      this.body.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
      MagmaCubeEntity var5 = (MagmaCubeEntity)entity;
      float var6 = var5.lastStretch + (var5.stretch - var5.lastStretch) * tickDelta;
      if (var6 < 0.0F) {
         var6 = 0.0F;
      }

      for(int var7 = 0; var7 < this.slices.length; ++var7) {
         this.slices[var7].pivotY = (float)(-(4 - var7)) * var6 * 1.7F;
      }
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.body.render(scale);

      for(int var8 = 0; var8 < this.slices.length; ++var8) {
         this.slices[var8].render(scale);
      }
   }
}
