package com.mojang.blaze3d.platform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final IntBuffer BUFFER = MemoryTracker.createIntBuffer(4194304);
   public static final NativeImageBackedTexture MISSING_TEXTURE = new NativeImageBackedTexture(16, 16);
   public static final int[] MISSING_DATA = MISSING_TEXTURE.getRgbArray();
   private static final int[] MIPMAP_BUFFER;

   public static int genTextures() {
      return GlStateManager.genTextures();
   }

   public static void deleteTexture(int texture) {
      GlStateManager.deleteTexture(texture);
   }

   public static int uploadTexture(int texture, BufferedImage image) {
      return uploadTexture(texture, image, false, false);
   }

   public static void uploadTexture(int texture, int[] pixels, int width, int height) {
      bind(texture);
      upload(0, pixels, width, height, 0, 0, false, false, false);
   }

   public static int[][] generateMipmaps(int mipmaps, int width, int[][] pixels) {
      int[][] var3 = new int[mipmaps + 1][];
      var3[0] = pixels[0];
      if (mipmaps > 0) {
         boolean var4 = false;

         for(int var5 = 0; var5 < pixels.length; ++var5) {
            if (pixels[0][var5] >> 24 == 0) {
               var4 = true;
               break;
            }
         }

         for(int var14 = 1; var14 <= mipmaps; ++var14) {
            if (pixels[var14] != null) {
               var3[var14] = pixels[var14];
            } else {
               int[] var6 = var3[var14 - 1];
               int[] var7 = new int[var6.length >> 2];
               int var8 = width >> var14;
               int var9 = var7.length / var8;
               int var10 = var8 << 1;

               for(int var11 = 0; var11 < var8; ++var11) {
                  for(int var12 = 0; var12 < var9; ++var12) {
                     int var13 = 2 * (var11 + var12 * var10);
                     var7[var11 + var12 * var8] = blendPixels(var6[var13 + 0], var6[var13 + 1], var6[var13 + 0 + var10], var6[var13 + 1 + var10], var4);
                  }
               }

               var3[var14] = var7;
            }
         }
      }

      return var3;
   }

   private static int blendPixels(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, boolean transperant) {
      if (!transperant) {
         int var14 = blendPixelComponents(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 24);
         int var16 = blendPixelComponents(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 16);
         int var18 = blendPixelComponents(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 8);
         int var20 = blendPixelComponents(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 0);
         return var14 << 24 | var16 << 16 | var18 << 8 | var20;
      } else {
         MIPMAP_BUFFER[0] = colorTopLeft;
         MIPMAP_BUFFER[1] = colorTopRight;
         MIPMAP_BUFFER[2] = colorBottomLeft;
         MIPMAP_BUFFER[3] = colorBottomRight;
         float var5 = 0.0F;
         float var6 = 0.0F;
         float var7 = 0.0F;
         float var8 = 0.0F;

         for(int var9 = 0; var9 < 4; ++var9) {
            if (MIPMAP_BUFFER[var9] >> 24 != 0) {
               var5 += (float)Math.pow((double)((float)(MIPMAP_BUFFER[var9] >> 24 & 0xFF) / 255.0F), 2.2);
               var6 += (float)Math.pow((double)((float)(MIPMAP_BUFFER[var9] >> 16 & 0xFF) / 255.0F), 2.2);
               var7 += (float)Math.pow((double)((float)(MIPMAP_BUFFER[var9] >> 8 & 0xFF) / 255.0F), 2.2);
               var8 += (float)Math.pow((double)((float)(MIPMAP_BUFFER[var9] >> 0 & 0xFF) / 255.0F), 2.2);
            }
         }

         var5 /= 4.0F;
         var6 /= 4.0F;
         var7 /= 4.0F;
         var8 /= 4.0F;
         int var21 = (int)(Math.pow((double)var5, 0.45454545454545453) * 255.0);
         int var10 = (int)(Math.pow((double)var6, 0.45454545454545453) * 255.0);
         int var11 = (int)(Math.pow((double)var7, 0.45454545454545453) * 255.0);
         int var12 = (int)(Math.pow((double)var8, 0.45454545454545453) * 255.0);
         if (var21 < 96) {
            var21 = 0;
         }

         return var21 << 24 | var10 << 16 | var11 << 8 | var12;
      }
   }

   private static int blendPixelComponents(int colorTopLeft, int colorTopRight, int colorBottomLeft, int colorBottomRight, int componentShift) {
      float var5 = (float)Math.pow((double)((float)(colorTopLeft >> componentShift & 0xFF) / 255.0F), 2.2);
      float var6 = (float)Math.pow((double)((float)(colorTopRight >> componentShift & 0xFF) / 255.0F), 2.2);
      float var7 = (float)Math.pow((double)((float)(colorBottomLeft >> componentShift & 0xFF) / 255.0F), 2.2);
      float var8 = (float)Math.pow((double)((float)(colorBottomRight >> componentShift & 0xFF) / 255.0F), 2.2);
      float var9 = (float)Math.pow((double)(var5 + var6 + var7 + var8) * 0.25, 0.45454545454545453);
      return (int)((double)var9 * 255.0);
   }

   public static void upload(int[][] texture, int xOffset, int yOffset, int width, int height, boolean blur, boolean clamped) {
      for(int var7 = 0; var7 < texture.length; ++var7) {
         int[] var8 = texture[var7];
         upload(var7, var8, xOffset >> var7, yOffset >> var7, width >> var7, height >> var7, blur, clamped, texture.length > 1);
      }
   }

   private static void upload(int level, int[] texture, int xOffset, int yOffset, int width, int height, boolean blur, boolean clamped, boolean mipmap) {
      int var9 = 4194304 / xOffset;
      setTextureFilter(blur, mipmap);
      setTextureClamp(clamped);

      int var12;
      for(int var10 = 0; var10 < xOffset * yOffset; var10 += xOffset * var12) {
         int var11 = var10 / xOffset;
         var12 = Math.min(var9, yOffset - var11);
         int var13 = xOffset * var12;
         putInBufferAt(texture, var10, var13);
         GL11.glTexSubImage2D(3553, level, width, height + var11, xOffset, var12, 32993, 33639, BUFFER);
      }
   }

   public static int uploadTexture(int texture, BufferedImage image, boolean bl, boolean bl2) {
      prepareImage(texture, image.getWidth(), image.getHeight());
      return m_23lrazwja(texture, image, 0, 0, bl, bl2);
   }

   public static void prepareImage(int id, int width, int height) {
      prepareImage(id, 0, width, height);
   }

   public static void prepareImage(int id, int mipmapLevel, int width, int height) {
      deleteTexture(id);
      bind(id);
      if (mipmapLevel > 0) {
         GL11.glTexParameteri(3553, 33085, mipmapLevel);
         GL11.glTexParameterf(3553, 33082, 0.0F);
         GL11.glTexParameterf(3553, 33083, (float)mipmapLevel);
         GL11.glTexParameterf(3553, 34049, 0.0F);
      }

      for(int var4 = 0; var4 <= mipmapLevel; ++var4) {
         GL11.glTexImage2D(3553, var4, 6408, width >> var4, height >> var4, 0, 32993, 33639, (IntBuffer)null);
      }
   }

   public static int m_23lrazwja(int id, BufferedImage texture, int xOffset, int yOffset, boolean bl, boolean bl2) {
      bind(id);
      m_69msbrpgz(texture, xOffset, yOffset, bl, bl2);
      return id;
   }

   private static void m_69msbrpgz(BufferedImage texture, int xOffset, int yOffset, boolean blur, boolean mipmap) {
      int var5 = texture.getWidth();
      int var6 = texture.getHeight();
      int var7 = 4194304 / var5;
      int[] var8 = new int[var7 * var5];
      setFilterWithBlur(blur);
      setTextureClamp(mipmap);

      for(int var9 = 0; var9 < var5 * var6; var9 += var5 * var7) {
         int var10 = var9 / var5;
         int var11 = Math.min(var7, var6 - var10);
         int var12 = var5 * var11;
         texture.getRGB(0, var10, var5, var11, var8, 0, var5);
         putInBuffer(var8, var12);
         GL11.glTexSubImage2D(3553, 0, xOffset, yOffset + var10, var5, var11, 32993, 33639, BUFFER);
      }
   }

   private static void setTextureClamp(boolean clamp) {
      if (clamp) {
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }
   }

   private static void setFilterWithBlur(boolean blur) {
      setTextureFilter(blur, false);
   }

   private static void setTextureFilter(boolean blur, boolean mipmap) {
      if (blur) {
         GL11.glTexParameteri(3553, 10241, mipmap ? 9987 : 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         GL11.glTexParameteri(3553, 10241, mipmap ? 9986 : 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }
   }

   private static void putInBuffer(int[] texture, int size) {
      putInBufferAt(texture, 0, size);
   }

   private static void putInBufferAt(int[] texture, int offset, int size) {
      int[] var3 = texture;
      if (MinecraftClient.getInstance().options.anaglyph) {
         var3 = getAnaglyphColors(texture);
      }

      ((Buffer)BUFFER).clear();
      BUFFER.put(var3, offset, size);
      ((Buffer)BUFFER).position(0).limit(size);
   }

   static void bind(int id) {
      GlStateManager.bindTexture(id);
   }

   public static int[] getPixels(IResourceManager resourceManager, Identifier id) {
      BufferedImage var2 = readImage(resourceManager.getResource(id).asStream());
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      int[] var5 = new int[var3 * var4];
      var2.getRGB(0, 0, var3, var4, var5, 0, var3);
      return var5;
   }

   public static BufferedImage readImage(InputStream is) {
      BufferedImage var1;
      try {
         var1 = ImageIO.read(is);
      } finally {
         IOUtils.closeQuietly(is);
      }

      return var1;
   }

   public static int[] getAnaglyphColors(int[] colors) {
      int[] var1 = new int[colors.length];

      for(int var2 = 0; var2 < colors.length; ++var2) {
         var1[var2] = getAnaglyphColor(colors[var2]);
      }

      return var1;
   }

   public static int getAnaglyphColor(int color) {
      int var1 = color >> 24 & 0xFF;
      int var2 = color >> 16 & 0xFF;
      int var3 = color >> 8 & 0xFF;
      int var4 = color & 0xFF;
      int var5 = (var2 * 30 + var3 * 59 + var4 * 11) / 100;
      int var6 = (var2 * 30 + var3 * 70) / 100;
      int var7 = (var2 * 30 + var4 * 70) / 100;
      return var1 << 24 | var5 << 16 | var6 << 8 | var7;
   }

   public static void writeAsPNG(String file, int texture, int count, int width, int height) {
      bind(texture);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);

      for(int var5 = 0; var5 <= count; ++var5) {
         File var6 = new File(file + "_" + var5 + ".png");
         int var7 = width >> var5;
         int var8 = height >> var5;
         int var9 = var7 * var8;
         IntBuffer var10 = BufferUtils.createIntBuffer(var9);
         int[] var11 = new int[var9];
         GL11.glGetTexImage(3553, var5, 32993, 33639, var10);
         var10.get(var11);
         BufferedImage var12 = new BufferedImage(var7, var8, 2);
         var12.setRGB(0, 0, var7, var8, var11, 0, var7);

         try {
            ImageIO.write(var12, "png", var6);
            LOGGER.debug("Exported png to: {}", new Object[]{var6.getAbsolutePath()});
         } catch (IOException var14) {
            LOGGER.debug("Unable to write: ", var14);
         }
      }
   }

   public static void copyTextureValues(int[] texture, int lenth, int pos) {
      int[] var3 = new int[lenth];
      int var4 = pos / 2;

      for(int var5 = 0; var5 < var4; ++var5) {
         System.arraycopy(texture, var5 * lenth, var3, 0, lenth);
         System.arraycopy(texture, (pos - 1 - var5) * lenth, texture, var5 * lenth, lenth);
         System.arraycopy(var3, 0, texture, (pos - 1 - var5) * lenth, lenth);
      }
   }

   static {
      int var0 = -16777216;
      int var1 = -524040;
      int[] var2 = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
      int[] var3 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      int var4 = var2.length;

      for(int var5 = 0; var5 < 16; ++var5) {
         System.arraycopy(var5 < var4 ? var2 : var3, 0, MISSING_DATA, 16 * var5, var4);
         System.arraycopy(var5 < var4 ? var3 : var2, 0, MISSING_DATA, 16 * var5 + var4, var4);
      }

      MISSING_TEXTURE.upload();
      MIPMAP_BUFFER = new int[4];
   }
}
