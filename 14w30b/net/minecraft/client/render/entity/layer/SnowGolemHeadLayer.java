package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.SnowGolemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.entity.living.mob.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnowGolemHeadLayer implements EntityRenderLayer {
   private final SnowGolemRenderer parent;

   public SnowGolemHeadLayer(SnowGolemRenderer parent) {
      this.parent = parent;
   }

   public void render(SnowGolemEntity c_64nrvqmxb, float f, float g, float h, float i, float j, float k, float l) {
      if (!c_64nrvqmxb.isInvisible()) {
         GlStateManager.pushMatrix();
         this.parent.getModel().head.translate(0.0625F);
         float var9 = 0.625F;
         GlStateManager.translatef(0.0F, -0.34375F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(var9, -var9, -var9);
         MinecraftClient.getInstance().getHeldItemRenderer().render(c_64nrvqmxb, new ItemStack(Blocks.PUMPKIN, 1), ModelTransformations.Type.HEAD);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
