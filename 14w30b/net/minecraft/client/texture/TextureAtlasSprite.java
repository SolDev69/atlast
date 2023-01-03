package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resource.metadata.AnimationFrame;
import net.minecraft.client.resource.metadata.AnimationMetadata;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureAtlasSprite {
   private final String name;
   protected List frames = Lists.newArrayList();
   protected int[][] f_13fljufio;
   private AnimationMetadata meta;
   protected boolean rotation;
   protected int x;
   protected int y;
   protected int width;
   protected int height;
   private float uMin;
   private float uMax;
   private float vMin;
   private float vMax;
   protected int frameIndex;
   protected int frameTicks;
   private static String f_80nlvazcq = "builtin/clock";
   private static String f_98uskzetq = "builtin/compass";

   protected TextureAtlasSprite(String name) {
      this.name = name;
   }

   protected static TextureAtlasSprite m_53dhrcjlm(Identifier c_07ipdbewr) {
      String var1 = c_07ipdbewr.toString();
      if (f_80nlvazcq.equals(var1)) {
         return new ClockSprite(var1);
      } else {
         return (TextureAtlasSprite)(f_98uskzetq.equals(var1) ? new CompassSprite(var1) : new TextureAtlasSprite(var1));
      }
   }

   public static void m_41txmgmed(String string) {
      f_80nlvazcq = string;
   }

   public static void m_41puehtak(String string) {
      f_98uskzetq = string;
   }

   public void reInitialize(int u, int v, int x, int y, boolean rotation) {
      this.x = x;
      this.y = y;
      this.rotation = rotation;
      float var6 = (float)(0.01F / (double)u);
      float var7 = (float)(0.01F / (double)v);
      this.uMin = (float)x / (float)((double)u) + var6;
      this.uMax = (float)(x + this.width) / (float)((double)u) - var6;
      this.vMin = (float)y / (float)v + var7;
      this.vMax = (float)(y + this.height) / (float)v - var7;
   }

   public void copyData(TextureAtlasSprite sprite) {
      this.x = sprite.x;
      this.y = sprite.y;
      this.width = sprite.width;
      this.height = sprite.height;
      this.rotation = sprite.rotation;
      this.uMin = sprite.uMin;
      this.uMax = sprite.uMax;
      this.vMin = sprite.vMin;
      this.vMax = sprite.vMax;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getUMin() {
      return this.uMin;
   }

   public float getUMax() {
      return this.uMax;
   }

   public float getU(double d) {
      float var3 = this.uMax - this.uMin;
      return this.uMin + var3 * (float)d / 16.0F;
   }

   public float getVMin() {
      return this.vMin;
   }

   public float getVMax() {
      return this.vMax;
   }

   public float getV(double d) {
      float var3 = this.vMax - this.vMin;
      return this.vMin + var3 * ((float)d / 16.0F);
   }

   public String getName() {
      return this.name;
   }

   public void update() {
      ++this.frameTicks;
      if (this.frameTicks >= this.meta.getTime(this.frameIndex)) {
         int var1 = this.meta.getIndex(this.frameIndex);
         int var2 = this.meta.getFrameCount() == 0 ? this.frames.size() : this.meta.getFrameCount();
         this.frameIndex = (this.frameIndex + 1) % var2;
         this.frameTicks = 0;
         int var3 = this.meta.getIndex(this.frameIndex);
         if (var1 != var3 && var3 >= 0 && var3 < this.frames.size()) {
            TextureUtil.upload((int[][])this.frames.get(var3), this.width, this.height, this.x, this.y, false, false);
         }
      } else if (this.meta.isInterpolated()) {
         this.m_23vrxibxo();
      }
   }

   private void m_23vrxibxo() {
      double var1 = 1.0 - (double)this.frameTicks / (double)this.meta.getTime(this.frameIndex);
      int var3 = this.meta.getIndex(this.frameIndex);
      int var4 = this.meta.getFrameCount() == 0 ? this.frames.size() : this.meta.getFrameCount();
      int var5 = this.meta.getIndex((this.frameIndex + 1) % var4);
      if (var3 != var5 && var5 >= 0 && var5 < this.frames.size()) {
         int[][] var6 = (int[][])this.frames.get(var3);
         int[][] var7 = (int[][])this.frames.get(var5);
         if (this.f_13fljufio == null || this.f_13fljufio.length != var6.length) {
            this.f_13fljufio = new int[var6.length][];
         }

         for(int var8 = 0; var8 < var6.length; ++var8) {
            if (this.f_13fljufio[var8] == null) {
               this.f_13fljufio[var8] = new int[var6[var8].length];
            }

            if (var8 < var7.length && var7[var8].length == var6[var8].length) {
               for(int var9 = 0; var9 < var6[var8].length; ++var9) {
                  int var10 = var6[var8][var9];
                  int var11 = var7[var8][var9];
                  int var12 = (int)((double)((var10 & 0xFF0000) >> 16) * var1 + (double)((var11 & 0xFF0000) >> 16) * (1.0 - var1));
                  int var13 = (int)((double)((var10 & 0xFF00) >> 8) * var1 + (double)((var11 & 0xFF00) >> 8) * (1.0 - var1));
                  int var14 = (int)((double)(var10 & 0xFF) * var1 + (double)(var11 & 0xFF) * (1.0 - var1));
                  this.f_13fljufio[var8][var9] = var10 & 0xFF000000 | var12 << 16 | var13 << 8 | var14;
               }
            }
         }

         TextureUtil.upload(this.f_13fljufio, this.width, this.height, this.x, this.y, false, false);
      }
   }

   public int[][] getFrame(int frame) {
      return (int[][])this.frames.get(frame);
   }

   public int getFrameSize() {
      return this.frames.size();
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public void loadTextues(BufferedImage[] textures, AnimationMetadata animationMetadata) {
      this.nullify();
      int var3 = textures[0].getWidth();
      int var4 = textures[0].getHeight();
      this.width = var3;
      this.height = var4;
      int[][] var5 = new int[textures.length][];

      for(int var6 = 0; var6 < textures.length; ++var6) {
         BufferedImage var7 = textures[var6];
         if (var7 != null) {
            if (var6 > 0 && (var7.getWidth() != var3 >> var6 || var7.getHeight() != var4 >> var6)) {
               throw new RuntimeException(
                  String.format(
                     "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", var6, var7.getWidth(), var7.getHeight(), var3 >> var6, var4 >> var6
                  )
               );
            }

            var5[var6] = new int[var7.getWidth() * var7.getHeight()];
            var7.getRGB(0, 0, var7.getWidth(), var7.getHeight(), var5[var6], 0, var7.getWidth());
         }
      }

      if (animationMetadata == null) {
         if (var4 != var3) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.frames.add(var5);
      } else {
         int var11 = var4 / var3;
         int var12 = var3;
         int var8 = var3;
         this.height = this.width;
         if (animationMetadata.getFrameCount() > 0) {
            for(int var10 : animationMetadata.getIndices()) {
               if (var10 >= var11) {
                  throw new RuntimeException("invalid frameindex " + var10);
               }

               this.m_29haulacs(var10);
               this.frames.set(var10, m_01lgtcpca(var5, var12, var8, var10));
            }

            this.meta = animationMetadata;
         } else {
            ArrayList var13 = Lists.newArrayList();

            for(int var14 = 0; var14 < var11; ++var14) {
               this.frames.add(m_01lgtcpca(var5, var12, var8, var14));
               var13.add(new AnimationFrame(var14, -1));
            }

            this.meta = new AnimationMetadata(var13, this.width, this.height, animationMetadata.getTime(), animationMetadata.isInterpolated());
         }
      }
   }

   public void applyMipmaps(int mipmaps) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < this.frames.size(); ++var3) {
         final int[][] var4 = (int[][])this.frames.get(var3);
         if (var4 != null) {
            try {
               var2.add(TextureUtil.generateMipmaps(mipmaps, this.width, var4));
            } catch (Throwable var8) {
               CrashReport var6 = CrashReport.of(var8, "Generating mipmaps for frame");
               CashReportCategory var7 = var6.addCategory("Frame being iterated");
               var7.add("Frame index", var3);
               var7.add("Frame sizes", new Callable() {
                  public String call() {
                     StringBuilder var1 = new StringBuilder();

                     for(int[] var5 : var4) {
                        if (var1.length() > 0) {
                           var1.append(", ");
                        }

                        var1.append(var5 == null ? "null" : var5.length);
                     }

                     return var1.toString();
                  }
               });
               throw new CrashException(var6);
            }
         }
      }

      this.setFrames(var2);
   }

   private void m_29haulacs(int i) {
      if (this.frames.size() <= i) {
         for(int var2 = this.frames.size(); var2 <= i; ++var2) {
            this.frames.add(null);
         }
      }
   }

   private static int[][] m_01lgtcpca(int[][] is, int i, int j, int k) {
      int[][] var4 = new int[is.length][];

      for(int var5 = 0; var5 < is.length; ++var5) {
         int[] var6 = is[var5];
         if (var6 != null) {
            var4[var5] = new int[(i >> var5) * (j >> var5)];
            System.arraycopy(var6, k * var4[var5].length, var4[var5], 0, var4[var5].length);
         }
      }

      return var4;
   }

   public void clearFrames() {
      this.frames.clear();
   }

   public boolean hasMeta() {
      return this.meta != null;
   }

   public void setFrames(List frames) {
      this.frames = frames;
   }

   private void nullify() {
      this.meta = null;
      this.setFrames(Lists.newArrayList());
      this.frameIndex = 0;
      this.frameTicks = 0;
   }

   @Override
   public String toString() {
      return "TextureAtlasSprite{name='"
         + this.name
         + '\''
         + ", frameCount="
         + this.frames.size()
         + ", rotated="
         + this.rotation
         + ", x="
         + this.x
         + ", y="
         + this.y
         + ", height="
         + this.height
         + ", width="
         + this.width
         + ", u0="
         + this.uMin
         + ", u1="
         + this.uMax
         + ", v0="
         + this.vMin
         + ", v1="
         + this.vMax
         + '}';
   }
}
