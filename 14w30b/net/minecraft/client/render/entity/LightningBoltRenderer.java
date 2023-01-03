package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Random;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LightningBoltRenderer extends EntityRenderer {
   public LightningBoltRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   public void render(LightningBoltEntity c_78xpuylot, double d, double e, double f, float g, float h) {
      Tessellator var10 = Tessellator.getInstance();
      BufferBuilder var11 = var10.getBufferBuilder();
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(770, 1);
      double[] var12 = new double[8];
      double[] var13 = new double[8];
      double var14 = 0.0;
      double var16 = 0.0;
      Random var18 = new Random(c_78xpuylot.seed);

      for(int var19 = 7; var19 >= 0; --var19) {
         var12[var19] = var14;
         var13[var19] = var16;
         var14 += (double)(var18.nextInt(11) - 5);
         var16 += (double)(var18.nextInt(11) - 5);
      }

      for(int var46 = 0; var46 < 4; ++var46) {
         Random var47 = new Random(c_78xpuylot.seed);

         for(int var20 = 0; var20 < 3; ++var20) {
            int var21 = 7;
            int var22 = 0;
            if (var20 > 0) {
               var21 = 7 - var20;
            }

            if (var20 > 0) {
               var22 = var21 - 2;
            }

            double var23 = var12[var21] - var14;
            double var25 = var13[var21] - var16;

            for(int var27 = var21; var27 >= var22; --var27) {
               double var28 = var23;
               double var30 = var25;
               if (var20 == 0) {
                  var23 += (double)(var47.nextInt(11) - 5);
                  var25 += (double)(var47.nextInt(11) - 5);
               } else {
                  var23 += (double)(var47.nextInt(31) - 15);
                  var25 += (double)(var47.nextInt(31) - 15);
               }

               var11.start(5);
               float var32 = 0.5F;
               var11.color(0.9F * var32, 0.9F * var32, 1.0F * var32, 0.3F);
               double var33 = 0.1 + (double)var46 * 0.2;
               if (var20 == 0) {
                  var33 *= (double)var27 * 0.1 + 1.0;
               }

               double var35 = 0.1 + (double)var46 * 0.2;
               if (var20 == 0) {
                  var35 *= (double)(var27 - 1) * 0.1 + 1.0;
               }

               for(int var37 = 0; var37 < 5; ++var37) {
                  double var38 = d + 0.5 - var33;
                  double var40 = f + 0.5 - var33;
                  if (var37 == 1 || var37 == 2) {
                     var38 += var33 * 2.0;
                  }

                  if (var37 == 2 || var37 == 3) {
                     var40 += var33 * 2.0;
                  }

                  double var42 = d + 0.5 - var35;
                  double var44 = f + 0.5 - var35;
                  if (var37 == 1 || var37 == 2) {
                     var42 += var35 * 2.0;
                  }

                  if (var37 == 2 || var37 == 3) {
                     var44 += var35 * 2.0;
                  }

                  var11.vertex(var42 + var23, e + (double)(var27 * 16), var44 + var25);
                  var11.vertex(var38 + var28, e + (double)((var27 + 1) * 16), var40 + var30);
               }

               var10.end();
            }
         }
      }

      GlStateManager.enableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture();
   }

   protected Identifier getTexture(LightningBoltEntity c_78xpuylot) {
      return null;
   }
}
