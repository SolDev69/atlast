package net.minecraft.client.render.model.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderCrystalModel extends Model {
   private ModelPart cube;
   private ModelPart glass = new ModelPart(this, "glass");
   private ModelPart base;

   public EnderCrystalModel(float f, boolean hasBase) {
      this.glass.setTextureCoords(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.cube = new ModelPart(this, "cube");
      this.cube.setTextureCoords(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (hasBase) {
         this.base = new ModelPart(this, "base");
         this.base.setTextureCoords(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      }
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      GlStateManager.translatef(0.0F, -0.5F, 0.0F);
      if (this.base != null) {
         this.base.render(scale);
      }

      GlStateManager.rotatef(handSwingAmount, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.8F + age, 0.0F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.glass.render(scale);
      float var8 = 0.875F;
      GlStateManager.scalef(var8, var8, var8);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(handSwingAmount, 0.0F, 1.0F, 0.0F);
      this.glass.render(scale);
      GlStateManager.scalef(var8, var8, var8);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(handSwingAmount, 0.0F, 1.0F, 0.0F);
      this.cube.render(scale);
      GlStateManager.popMatrix();
   }
}
