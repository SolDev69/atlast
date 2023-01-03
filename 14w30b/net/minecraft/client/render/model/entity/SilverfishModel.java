package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SilverfishModel extends Model {
   private ModelPart[] body;
   private ModelPart[] scales;
   private float[] radii = new float[7];
   private static final int[][] SEGMENT_LOCATIONS = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] SEGMENT_SIZES = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      this.body = new ModelPart[7];
      float var1 = -3.5F;

      for(int var2 = 0; var2 < this.body.length; ++var2) {
         this.body[var2] = new ModelPart(this, SEGMENT_SIZES[var2][0], SEGMENT_SIZES[var2][1]);
         this.body[var2]
            .addBox(
               (float)SEGMENT_LOCATIONS[var2][0] * -0.5F,
               0.0F,
               (float)SEGMENT_LOCATIONS[var2][2] * -0.5F,
               SEGMENT_LOCATIONS[var2][0],
               SEGMENT_LOCATIONS[var2][1],
               SEGMENT_LOCATIONS[var2][2]
            );
         this.body[var2].setPivot(0.0F, (float)(24 - SEGMENT_LOCATIONS[var2][1]), var1);
         this.radii[var2] = var1;
         if (var2 < this.body.length - 1) {
            var1 += (float)(SEGMENT_LOCATIONS[var2][2] + SEGMENT_LOCATIONS[var2 + 1][2]) * 0.5F;
         }
      }

      this.scales = new ModelPart[3];
      this.scales[0] = new ModelPart(this, 20, 0);
      this.scales[0].addBox(-5.0F, 0.0F, (float)SEGMENT_LOCATIONS[2][2] * -0.5F, 10, 8, SEGMENT_LOCATIONS[2][2]);
      this.scales[0].setPivot(0.0F, 16.0F, this.radii[2]);
      this.scales[1] = new ModelPart(this, 20, 11);
      this.scales[1].addBox(-3.0F, 0.0F, (float)SEGMENT_LOCATIONS[4][2] * -0.5F, 6, 4, SEGMENT_LOCATIONS[4][2]);
      this.scales[1].setPivot(0.0F, 20.0F, this.radii[4]);
      this.scales[2] = new ModelPart(this, 20, 18);
      this.scales[2].addBox(-3.0F, 0.0F, (float)SEGMENT_LOCATIONS[4][2] * -0.5F, 6, 5, SEGMENT_LOCATIONS[1][2]);
      this.scales[2].setPivot(0.0F, 19.0F, this.radii[1]);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);

      for(int var8 = 0; var8 < this.body.length; ++var8) {
         this.body[var8].render(scale);
      }

      for(int var9 = 0; var9 < this.scales.length; ++var9) {
         this.scales[var9].render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      for(int var8 = 0; var8 < this.body.length; ++var8) {
         this.body[var8].rotationY = MathHelper.cos(age * 0.9F + (float)var8 * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.05F
            * (float)(1 + Math.abs(var8 - 2));
         this.body[var8].pivotX = MathHelper.sin(age * 0.9F + (float)var8 * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(var8 - 2);
      }

      this.scales[0].rotationY = this.body[2].rotationY;
      this.scales[1].rotationY = this.body[4].rotationY;
      this.scales[1].pivotX = this.body[4].pivotX;
      this.scales[2].rotationY = this.body[1].rotationY;
      this.scales[2].pivotX = this.body[1].pivotX;
   }
}
