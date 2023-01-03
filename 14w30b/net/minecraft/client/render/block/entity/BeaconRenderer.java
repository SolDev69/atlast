package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class BeaconRenderer extends BlockEntityRenderer {
   private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

   public void render(BeaconBlockEntity c_61olupcva, double d, double e, double f, float g, int i) {
      float var10 = c_61olupcva.getBeamAngle();
      GlStateManager.alphaFunc(516, 0.1F);
      if (var10 > 0.0F) {
         Tessellator var11 = Tessellator.getInstance();
         BufferBuilder var12 = var11.getBufferBuilder();
         this.bindTexture(BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.depthMask(true);
         GlStateManager.blendFuncSeparate(770, 1, 1, 0);
         float var13 = (float)c_61olupcva.getWorld().getTime() + g;
         float var14 = -var13 * 0.2F - (float)MathHelper.floor(-var13 * 0.1F);
         byte var15 = 1;
         double var16 = (double)var13 * 0.025 * (1.0 - (double)(var15 & 1) * 2.5);
         var12.start();
         var12.color(255, 255, 255, 32);
         double var18 = (double)var15 * 0.2;
         double var20 = 0.5 + Math.cos(var16 + (Math.PI * 3.0 / 4.0)) * var18;
         double var22 = 0.5 + Math.sin(var16 + (Math.PI * 3.0 / 4.0)) * var18;
         double var24 = 0.5 + Math.cos(var16 + (Math.PI / 4)) * var18;
         double var26 = 0.5 + Math.sin(var16 + (Math.PI / 4)) * var18;
         double var28 = 0.5 + Math.cos(var16 + (Math.PI * 5.0 / 4.0)) * var18;
         double var30 = 0.5 + Math.sin(var16 + (Math.PI * 5.0 / 4.0)) * var18;
         double var32 = 0.5 + Math.cos(var16 + (Math.PI * 7.0 / 4.0)) * var18;
         double var34 = 0.5 + Math.sin(var16 + (Math.PI * 7.0 / 4.0)) * var18;
         double var36 = (double)(256.0F * var10);
         double var38 = 0.0;
         double var40 = 1.0;
         double var42 = (double)(-1.0F + var14);
         double var44 = (double)(256.0F * var10) * (0.5 / var18) + var42;
         var12.vertex(d + var20, e + var36, f + var22, var40, var44);
         var12.vertex(d + var20, e, f + var22, var40, var42);
         var12.vertex(d + var24, e, f + var26, var38, var42);
         var12.vertex(d + var24, e + var36, f + var26, var38, var44);
         var12.vertex(d + var32, e + var36, f + var34, var40, var44);
         var12.vertex(d + var32, e, f + var34, var40, var42);
         var12.vertex(d + var28, e, f + var30, var38, var42);
         var12.vertex(d + var28, e + var36, f + var30, var38, var44);
         var12.vertex(d + var24, e + var36, f + var26, var40, var44);
         var12.vertex(d + var24, e, f + var26, var40, var42);
         var12.vertex(d + var32, e, f + var34, var38, var42);
         var12.vertex(d + var32, e + var36, f + var34, var38, var44);
         var12.vertex(d + var28, e + var36, f + var30, var40, var44);
         var12.vertex(d + var28, e, f + var30, var40, var42);
         var12.vertex(d + var20, e, f + var22, var38, var42);
         var12.vertex(d + var20, e + var36, f + var22, var38, var44);
         var11.end();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         GlStateManager.depthMask(false);
         var12.start();
         var12.color(255, 255, 255, 32);
         double var46 = 0.2;
         double var17 = 0.2;
         double var19 = 0.8;
         double var21 = 0.2;
         double var23 = 0.2;
         double var25 = 0.8;
         double var27 = 0.8;
         double var29 = 0.8;
         double var31 = (double)(256.0F * var10);
         double var33 = 0.0;
         double var35 = 1.0;
         double var37 = (double)(-1.0F + var14);
         double var39 = (double)(256.0F * var10) + var37;
         var12.vertex(d + var46, e + var31, f + var17, var35, var39);
         var12.vertex(d + var46, e, f + var17, var35, var37);
         var12.vertex(d + var19, e, f + var21, var33, var37);
         var12.vertex(d + var19, e + var31, f + var21, var33, var39);
         var12.vertex(d + var27, e + var31, f + var29, var35, var39);
         var12.vertex(d + var27, e, f + var29, var35, var37);
         var12.vertex(d + var23, e, f + var25, var33, var37);
         var12.vertex(d + var23, e + var31, f + var25, var33, var39);
         var12.vertex(d + var19, e + var31, f + var21, var35, var39);
         var12.vertex(d + var19, e, f + var21, var35, var37);
         var12.vertex(d + var27, e, f + var29, var33, var37);
         var12.vertex(d + var27, e + var31, f + var29, var33, var39);
         var12.vertex(d + var23, e + var31, f + var25, var35, var39);
         var12.vertex(d + var23, e, f + var25, var35, var37);
         var12.vertex(d + var46, e, f + var17, var33, var37);
         var12.vertex(d + var46, e + var31, f + var17, var33, var39);
         var11.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         GlStateManager.depthMask(true);
      }
   }
}
