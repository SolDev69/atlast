package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.entity.MooshroomRenderer;
import net.minecraft.client.render.model.entity.QuadrupedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.living.mob.passive.animal.MooshroomEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MushroomLayer implements EntityRenderLayer {
   private final MooshroomRenderer parent;

   public MushroomLayer(MooshroomRenderer parent) {
      this.parent = parent;
   }

   public void render(MooshroomEntity c_74tbbodgs, float f, float g, float h, float i, float j, float k, float l) {
      if (!c_74tbbodgs.isBaby() && !c_74tbbodgs.isInvisible()) {
         BlockRenderDispatcher var9 = MinecraftClient.getInstance().getBlockRenderDispatcher();
         this.parent.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         GlStateManager.enableCull();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.2F, 0.35F, 0.5F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var9.renderDynamic(Blocks.RED_MUSHROOM, 0, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, 0.0F, -0.6F);
         GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var9.renderDynamic(Blocks.RED_MUSHROOM, 0, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         ((QuadrupedModel)this.parent.getModel()).head.translate(0.0625F);
         GlStateManager.scalef(1.0F, -1.0F, 1.0F);
         GlStateManager.translatef(0.0F, 0.7F, -0.2F);
         GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
         var9.renderDynamic(Blocks.RED_MUSHROOM, 0, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableCull();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
