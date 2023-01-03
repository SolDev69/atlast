package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.entity.IronGolemRenderer;
import net.minecraft.client.render.model.entity.IronGolemModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class IronGolemHeldFlowerLayer implements EntityRenderLayer {
   private final IronGolemRenderer parent;

   public IronGolemHeldFlowerLayer(IronGolemRenderer parent) {
      this.parent = parent;
   }

   public void render(IronGolemEntity c_91ginzfwf, float f, float g, float h, float i, float j, float k, float l) {
      if (c_91ginzfwf.getLookingAtVillagerTicks() != 0) {
         BlockRenderDispatcher var9 = MinecraftClient.getInstance().getBlockRenderDispatcher();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(5.0F + 180.0F * ((IronGolemModel)this.parent.getModel()).rightArm.rotationX / (float) Math.PI, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(-0.9375F, -0.25F, -0.9375F);
         float var10 = 0.5F;
         GlStateManager.scalef(var10, -var10, var10);
         int var11 = c_91ginzfwf.getLightLevel(h);
         int var12 = var11 % 65536;
         int var13 = var11 / 65536;
         GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var12 / 1.0F, (float)var13 / 1.0F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.parent.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         var9.renderDynamic(Blocks.RED_FLOWER, FlowerBlock.Type.POPPY.getIndex(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
