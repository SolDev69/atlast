package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GhastModel extends Model {
   ModelPart body;
   ModelPart[] tentacles = new ModelPart[9];

   public GhastModel() {
      byte var1 = -16;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      this.body.pivotY += (float)(24 + var1);
      Random var2 = new Random(1660L);

      for(int var3 = 0; var3 < this.tentacles.length; ++var3) {
         this.tentacles[var3] = new ModelPart(this, 0, 0);
         float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var6 = var2.nextInt(7) + 8;
         this.tentacles[var3].addBox(-1.0F, 0.0F, -1.0F, 2, var6, 2);
         this.tentacles[var3].pivotX = var4;
         this.tentacles[var3].pivotZ = var5;
         this.tentacles[var3].pivotY = (float)(31 + var1);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      for(int var8 = 0; var8 < this.tentacles.length; ++var8) {
         this.tentacles[var8].rotationX = 0.2F * MathHelper.sin(age * 0.3F + (float)var8) + 0.4F;
      }
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 0.6F, 0.0F);
      this.body.render(scale);

      for(ModelPart var11 : this.tentacles) {
         var11.render(scale);
      }

      GlStateManager.popMatrix();
   }
}
