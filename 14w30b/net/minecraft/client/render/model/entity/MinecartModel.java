package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MinecartModel extends Model {
   public ModelPart[] parts = new ModelPart[7];

   public MinecartModel() {
      this.parts[0] = new ModelPart(this, 0, 10);
      this.parts[1] = new ModelPart(this, 0, 0);
      this.parts[2] = new ModelPart(this, 0, 0);
      this.parts[3] = new ModelPart(this, 0, 0);
      this.parts[4] = new ModelPart(this, 0, 0);
      this.parts[5] = new ModelPart(this, 44, 10);
      byte var1 = 20;
      byte var2 = 8;
      byte var3 = 16;
      byte var4 = 4;
      this.parts[0].addBox((float)(-var1 / 2), (float)(-var3 / 2), -1.0F, var1, var3, 2, 0.0F);
      this.parts[0].setPivot(0.0F, (float)var4, 0.0F);
      this.parts[5].addBox((float)(-var1 / 2 + 1), (float)(-var3 / 2 + 1), -1.0F, var1 - 2, var3 - 2, 1, 0.0F);
      this.parts[5].setPivot(0.0F, (float)var4, 0.0F);
      this.parts[1].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.parts[1].setPivot((float)(-var1 / 2 + 1), (float)var4, 0.0F);
      this.parts[2].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.parts[2].setPivot((float)(var1 / 2 - 1), (float)var4, 0.0F);
      this.parts[3].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.parts[3].setPivot(0.0F, (float)var4, (float)(-var3 / 2 + 1));
      this.parts[4].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.parts[4].setPivot(0.0F, (float)var4, (float)(var3 / 2 - 1));
      this.parts[0].rotationX = (float) (Math.PI / 2);
      this.parts[1].rotationY = (float) (Math.PI * 3.0 / 2.0);
      this.parts[2].rotationY = (float) (Math.PI / 2);
      this.parts[3].rotationY = (float) Math.PI;
      this.parts[5].rotationX = (float) (-Math.PI / 2);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.parts[5].pivotY = 4.0F - age;

      for(int var8 = 0; var8 < 6; ++var8) {
         this.parts[var8].render(scale);
      }
   }
}
