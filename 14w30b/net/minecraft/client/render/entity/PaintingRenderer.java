package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PaintingRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/painting/paintings_kristoffer_zetterstrand.png");

   public PaintingRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   public void render(PaintingEntity c_39jfhoywn, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.translated(d, e, f);
      GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindTexture(c_39jfhoywn);
      PaintingEntity.Motive var10 = c_39jfhoywn.motive;
      float var11 = 0.0625F;
      GlStateManager.scalef(var11, var11, var11);
      this.renderPainting(c_39jfhoywn, var10.width, var10.height, var10.widthOffset, var10.heightOffset);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(c_39jfhoywn, d, e, f, g, h);
   }

   protected Identifier getTexture(PaintingEntity c_39jfhoywn) {
      return TEXTURE;
   }

   private void renderPainting(PaintingEntity painting, int width, int height, int widthOffset, int heightOffset) {
      float var6 = (float)(-width) / 2.0F;
      float var7 = (float)(-height) / 2.0F;
      float var8 = 0.5F;
      float var9 = 0.75F;
      float var10 = 0.8125F;
      float var11 = 0.0F;
      float var12 = 0.0625F;
      float var13 = 0.75F;
      float var14 = 0.8125F;
      float var15 = 0.001953125F;
      float var16 = 0.001953125F;
      float var17 = 0.7519531F;
      float var18 = 0.7519531F;
      float var19 = 0.0F;
      float var20 = 0.0625F;

      for(int var21 = 0; var21 < width / 16; ++var21) {
         for(int var22 = 0; var22 < height / 16; ++var22) {
            float var23 = var6 + (float)((var21 + 1) * 16);
            float var24 = var6 + (float)(var21 * 16);
            float var25 = var7 + (float)((var22 + 1) * 16);
            float var26 = var7 + (float)(var22 * 16);
            this.transformRenderPosition(painting, (var23 + var24) / 2.0F, (var25 + var26) / 2.0F);
            float var27 = (float)(widthOffset + width - var21 * 16) / 256.0F;
            float var28 = (float)(widthOffset + width - (var21 + 1) * 16) / 256.0F;
            float var29 = (float)(heightOffset + height - var22 * 16) / 256.0F;
            float var30 = (float)(heightOffset + height - (var22 + 1) * 16) / 256.0F;
            Tessellator var31 = Tessellator.getInstance();
            BufferBuilder var32 = var31.getBufferBuilder();
            var32.start();
            var32.normal(0.0F, 0.0F, -1.0F);
            var32.vertex((double)var23, (double)var26, (double)(-var8), (double)var28, (double)var29);
            var32.vertex((double)var24, (double)var26, (double)(-var8), (double)var27, (double)var29);
            var32.vertex((double)var24, (double)var25, (double)(-var8), (double)var27, (double)var30);
            var32.vertex((double)var23, (double)var25, (double)(-var8), (double)var28, (double)var30);
            var32.normal(0.0F, 0.0F, 1.0F);
            var32.vertex((double)var23, (double)var25, (double)var8, (double)var9, (double)var11);
            var32.vertex((double)var24, (double)var25, (double)var8, (double)var10, (double)var11);
            var32.vertex((double)var24, (double)var26, (double)var8, (double)var10, (double)var12);
            var32.vertex((double)var23, (double)var26, (double)var8, (double)var9, (double)var12);
            var32.normal(0.0F, 1.0F, 0.0F);
            var32.vertex((double)var23, (double)var25, (double)(-var8), (double)var13, (double)var15);
            var32.vertex((double)var24, (double)var25, (double)(-var8), (double)var14, (double)var15);
            var32.vertex((double)var24, (double)var25, (double)var8, (double)var14, (double)var16);
            var32.vertex((double)var23, (double)var25, (double)var8, (double)var13, (double)var16);
            var32.normal(0.0F, -1.0F, 0.0F);
            var32.vertex((double)var23, (double)var26, (double)var8, (double)var13, (double)var15);
            var32.vertex((double)var24, (double)var26, (double)var8, (double)var14, (double)var15);
            var32.vertex((double)var24, (double)var26, (double)(-var8), (double)var14, (double)var16);
            var32.vertex((double)var23, (double)var26, (double)(-var8), (double)var13, (double)var16);
            var32.normal(-1.0F, 0.0F, 0.0F);
            var32.vertex((double)var23, (double)var25, (double)var8, (double)var18, (double)var19);
            var32.vertex((double)var23, (double)var26, (double)var8, (double)var18, (double)var20);
            var32.vertex((double)var23, (double)var26, (double)(-var8), (double)var17, (double)var20);
            var32.vertex((double)var23, (double)var25, (double)(-var8), (double)var17, (double)var19);
            var32.normal(1.0F, 0.0F, 0.0F);
            var32.vertex((double)var24, (double)var25, (double)(-var8), (double)var18, (double)var19);
            var32.vertex((double)var24, (double)var26, (double)(-var8), (double)var18, (double)var20);
            var32.vertex((double)var24, (double)var26, (double)var8, (double)var17, (double)var20);
            var32.vertex((double)var24, (double)var25, (double)var8, (double)var17, (double)var19);
            var31.end();
         }
      }
   }

   private void transformRenderPosition(PaintingEntity painting, float horizontalOffset, float hightOffset) {
      int var4 = MathHelper.floor(painting.x);
      int var5 = MathHelper.floor(painting.y + (double)(hightOffset / 16.0F));
      int var6 = MathHelper.floor(painting.z);
      Direction var7 = painting.getFacing;
      if (var7 == Direction.NORTH) {
         var4 = MathHelper.floor(painting.x + (double)(horizontalOffset / 16.0F));
      }

      if (var7 == Direction.WEST) {
         var6 = MathHelper.floor(painting.z - (double)(horizontalOffset / 16.0F));
      }

      if (var7 == Direction.SOUTH) {
         var4 = MathHelper.floor(painting.x - (double)(horizontalOffset / 16.0F));
      }

      if (var7 == Direction.EAST) {
         var6 = MathHelper.floor(painting.z + (double)(horizontalOffset / 16.0F));
      }

      int var8 = this.dispatcher.world.getLightColor(new BlockPos(var4, var5, var6), 0);
      int var9 = var8 % 65536;
      int var10 = var8 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var9, (float)var10);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
   }
}
