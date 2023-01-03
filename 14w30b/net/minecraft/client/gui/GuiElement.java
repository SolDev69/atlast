package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuiElement {
   public static final Identifier OPTIONS_BACKGROUND = new Identifier("textures/gui/options_background.png");
   public static final Identifier STATS_ICONS = new Identifier("textures/gui/container/stats_icons.png");
   public static final Identifier ICONS = new Identifier("textures/gui/icons.png");
   protected float drawOffset;

   protected void drawHorizontalLine(int x1, int x2, int y, int color) {
      if (x2 < x1) {
         int var5 = x1;
         x1 = x2;
         x2 = var5;
      }

      fill(x1, y, x2 + 1, y + 1, color);
   }

   protected void drawVerticalLine(int x, int y1, int y2, int color) {
      if (y2 < y1) {
         int var5 = y1;
         y1 = y2;
         y2 = var5;
      }

      fill(x, y1 + 1, x + 1, y2, color);
   }

   public static void fill(int x1, int y1, int x2, int y2, int color) {
      if (x1 < x2) {
         int var5 = x1;
         x1 = x2;
         x2 = var5;
      }

      if (y1 < y2) {
         int var11 = y1;
         y1 = y2;
         y2 = var11;
      }

      float var12 = (float)(color >> 24 & 0xFF) / 255.0F;
      float var6 = (float)(color >> 16 & 0xFF) / 255.0F;
      float var7 = (float)(color >> 8 & 0xFF) / 255.0F;
      float var8 = (float)(color & 0xFF) / 255.0F;
      Tessellator var9 = Tessellator.getInstance();
      BufferBuilder var10 = var9.getBufferBuilder();
      GlStateManager.disableBlend();
      GlStateManager.disableTexture();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color4f(var6, var7, var8, var12);
      var10.start();
      var10.vertex((double)x1, (double)y2, 0.0);
      var10.vertex((double)x2, (double)y2, 0.0);
      var10.vertex((double)x2, (double)y1, 0.0);
      var10.vertex((double)x1, (double)y1, 0.0);
      var9.end();
      GlStateManager.enableTexture();
      GlStateManager.enableBlend();
   }

   protected void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
      float var7 = (float)(color1 >> 24 & 0xFF) / 255.0F;
      float var8 = (float)(color1 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(color1 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(color1 & 0xFF) / 255.0F;
      float var11 = (float)(color2 >> 24 & 0xFF) / 255.0F;
      float var12 = (float)(color2 >> 16 & 0xFF) / 255.0F;
      float var13 = (float)(color2 >> 8 & 0xFF) / 255.0F;
      float var14 = (float)(color2 & 0xFF) / 255.0F;
      GlStateManager.disableTexture();
      GlStateManager.disableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var15 = Tessellator.getInstance();
      BufferBuilder var16 = var15.getBufferBuilder();
      var16.start();
      var16.color(var8, var9, var10, var7);
      var16.vertex((double)x2, (double)y1, (double)this.drawOffset);
      var16.vertex((double)x1, (double)y1, (double)this.drawOffset);
      var16.color(var12, var13, var14, var11);
      var16.vertex((double)x1, (double)y2, (double)this.drawOffset);
      var16.vertex((double)x2, (double)y2, (double)this.drawOffset);
      var15.end();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableBlend();
      GlStateManager.enableAlphaTest();
      GlStateManager.enableTexture();
   }

   public void drawCenteredString(TextRenderer textRenderer, String text, int centerX, int y, int color) {
      textRenderer.drawWithShadow(text, (float)(centerX - textRenderer.getStringWidth(text) / 2), (float)y, color);
   }

   public void drawString(TextRenderer textRenderer, String text, int x, int y, int color) {
      textRenderer.drawWithShadow(text, (float)x, (float)y, color);
   }

   public void drawTexture(int x, int y, int u, int v, int width, int height) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.getInstance();
      BufferBuilder var10 = var9.getBufferBuilder();
      var10.start();
      var10.vertex((double)(x + 0), (double)(y + height), (double)this.drawOffset, (double)((float)(u + 0) * var7), (double)((float)(v + height) * var8));
      var10.vertex(
         (double)(x + width), (double)(y + height), (double)this.drawOffset, (double)((float)(u + width) * var7), (double)((float)(v + height) * var8)
      );
      var10.vertex((double)(x + width), (double)(y + 0), (double)this.drawOffset, (double)((float)(u + width) * var7), (double)((float)(v + 0) * var8));
      var10.vertex((double)(x + 0), (double)(y + 0), (double)this.drawOffset, (double)((float)(u + 0) * var7), (double)((float)(v + 0) * var8));
      var9.end();
   }

   public void drawTexture(float x, float y, int u, int v, int width, int height) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.getInstance();
      BufferBuilder var10 = var9.getBufferBuilder();
      var10.start();
      var10.vertex(
         (double)(x + 0.0F), (double)(y + (float)height), (double)this.drawOffset, (double)((float)(u + 0) * var7), (double)((float)(v + height) * var8)
      );
      var10.vertex(
         (double)(x + (float)width),
         (double)(y + (float)height),
         (double)this.drawOffset,
         (double)((float)(u + width) * var7),
         (double)((float)(v + height) * var8)
      );
      var10.vertex(
         (double)(x + (float)width), (double)(y + 0.0F), (double)this.drawOffset, (double)((float)(u + width) * var7), (double)((float)(v + 0) * var8)
      );
      var10.vertex((double)(x + 0.0F), (double)(y + 0.0F), (double)this.drawOffset, (double)((float)(u + 0) * var7), (double)((float)(v + 0) * var8));
      var9.end();
   }

   public void drawSprite(int x, int y, TextureAtlasSprite sprite, int width, int height) {
      Tessellator var6 = Tessellator.getInstance();
      BufferBuilder var7 = var6.getBufferBuilder();
      var7.start();
      var7.vertex((double)(x + 0), (double)(y + height), (double)this.drawOffset, (double)sprite.getUMin(), (double)sprite.getVMax());
      var7.vertex((double)(x + width), (double)(y + height), (double)this.drawOffset, (double)sprite.getUMax(), (double)sprite.getVMax());
      var7.vertex((double)(x + width), (double)(y + 0), (double)this.drawOffset, (double)sprite.getUMax(), (double)sprite.getVMin());
      var7.vertex((double)(x + 0), (double)(y + 0), (double)this.drawOffset, (double)sprite.getUMin(), (double)sprite.getVMin());
      var6.end();
   }

   public static void drawTexture(int x, int y, float u, float v, int width, int height, float scaleU, float scaleV) {
      float var8 = 1.0F / scaleU;
      float var9 = 1.0F / scaleV;
      Tessellator var10 = Tessellator.getInstance();
      BufferBuilder var11 = var10.getBufferBuilder();
      var11.start();
      var11.vertex((double)x, (double)(y + height), 0.0, (double)(u * var8), (double)((v + (float)height) * var9));
      var11.vertex((double)(x + width), (double)(y + height), 0.0, (double)((u + (float)width) * var8), (double)((v + (float)height) * var9));
      var11.vertex((double)(x + width), (double)y, 0.0, (double)((u + (float)width) * var8), (double)(v * var9));
      var11.vertex((double)x, (double)y, 0.0, (double)(u * var8), (double)(v * var9));
      var10.end();
   }

   public static void drawTexture(int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height, float scaleU, float scaleV) {
      float var10 = 1.0F / scaleU;
      float var11 = 1.0F / scaleV;
      Tessellator var12 = Tessellator.getInstance();
      BufferBuilder var13 = var12.getBufferBuilder();
      var13.start();
      var13.vertex((double)x, (double)(y + height), 0.0, (double)(u * var10), (double)((v + (float)textureHeight) * var11));
      var13.vertex((double)(x + width), (double)(y + height), 0.0, (double)((u + (float)textureWidth) * var10), (double)((v + (float)textureHeight) * var11));
      var13.vertex((double)(x + width), (double)y, 0.0, (double)((u + (float)textureWidth) * var10), (double)(v * var11));
      var13.vertex((double)x, (double)y, 0.0, (double)(u * var10), (double)(v * var11));
      var12.end();
   }
}
