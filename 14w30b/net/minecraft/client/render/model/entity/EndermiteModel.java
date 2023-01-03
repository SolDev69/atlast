package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermiteModel extends Model {
   private static final int[][] PART_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] PART_TEXTURE_COORDS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private static final int PART_COUNT = PART_SIZES.length;
   private final ModelPart[] parts = new ModelPart[PART_COUNT];

   public EndermiteModel() {
      float var1 = -3.5F;

      for(int var2 = 0; var2 < this.parts.length; ++var2) {
         this.parts[var2] = new ModelPart(this, PART_TEXTURE_COORDS[var2][0], PART_TEXTURE_COORDS[var2][1]);
         this.parts[var2]
            .addBox((float)PART_SIZES[var2][0] * -0.5F, 0.0F, (float)PART_SIZES[var2][2] * -0.5F, PART_SIZES[var2][0], PART_SIZES[var2][1], PART_SIZES[var2][2]);
         this.parts[var2].setPivot(0.0F, (float)(24 - PART_SIZES[var2][1]), var1);
         if (var2 < this.parts.length - 1) {
            var1 += (float)(PART_SIZES[var2][2] + PART_SIZES[var2 + 1][2]) * 0.5F;
         }
      }
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);

      for(int var8 = 0; var8 < this.parts.length; ++var8) {
         this.parts[var8].render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      for(int var8 = 0; var8 < this.parts.length; ++var8) {
         this.parts[var8].rotationY = MathHelper.cos(age * 0.9F + (float)var8 * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.01F
            * (float)(1 + Math.abs(var8 - 2));
         this.parts[var8].pivotX = MathHelper.sin(age * 0.9F + (float)var8 * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(var8 - 2);
      }
   }
}
