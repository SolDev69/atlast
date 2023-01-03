package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ArrowRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/arrow.png");

   public ArrowRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   public void render(ArrowEntity c_09yfwkwex, double d, double e, double f, float g, float h) {
      this.bindTexture(c_09yfwkwex);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e, (float)f);
      GlStateManager.rotatef(c_09yfwkwex.prevYaw + (c_09yfwkwex.yaw - c_09yfwkwex.prevYaw) * h - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(c_09yfwkwex.prevPitch + (c_09yfwkwex.pitch - c_09yfwkwex.prevPitch) * h, 0.0F, 0.0F, 1.0F);
      Tessellator var10 = Tessellator.getInstance();
      BufferBuilder var11 = var10.getBufferBuilder();
      byte var12 = 0;
      float var13 = 0.0F;
      float var14 = 0.5F;
      float var15 = (float)(0 + var12 * 10) / 32.0F;
      float var16 = (float)(5 + var12 * 10) / 32.0F;
      float var17 = 0.0F;
      float var18 = 0.15625F;
      float var19 = (float)(5 + var12 * 10) / 32.0F;
      float var20 = (float)(10 + var12 * 10) / 32.0F;
      float var21 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float var22 = (float)c_09yfwkwex.shake - h;
      if (var22 > 0.0F) {
         float var23 = -MathHelper.sin(var22 * 3.0F) * var22;
         GlStateManager.rotatef(var23, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(var21, var21, var21);
      GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(var21, 0.0F, 0.0F);
      var11.start();
      var11.vertex(-7.0, -2.0, -2.0, (double)var17, (double)var19);
      var11.vertex(-7.0, -2.0, 2.0, (double)var18, (double)var19);
      var11.vertex(-7.0, 2.0, 2.0, (double)var18, (double)var20);
      var11.vertex(-7.0, 2.0, -2.0, (double)var17, (double)var20);
      var10.end();
      GL11.glNormal3f(-var21, 0.0F, 0.0F);
      var11.start();
      var11.vertex(-7.0, 2.0, -2.0, (double)var17, (double)var19);
      var11.vertex(-7.0, 2.0, 2.0, (double)var18, (double)var19);
      var11.vertex(-7.0, -2.0, 2.0, (double)var18, (double)var20);
      var11.vertex(-7.0, -2.0, -2.0, (double)var17, (double)var20);
      var10.end();

      for(int var24 = 0; var24 < 4; ++var24) {
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, var21);
         var11.start();
         var11.vertex(-8.0, -2.0, 0.0, (double)var13, (double)var15);
         var11.vertex(8.0, -2.0, 0.0, (double)var14, (double)var15);
         var11.vertex(8.0, 2.0, 0.0, (double)var14, (double)var16);
         var11.vertex(-8.0, 2.0, 0.0, (double)var13, (double)var16);
         var10.end();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(c_09yfwkwex, d, e, f, g, h);
   }

   protected Identifier getTexture(ArrowEntity c_09yfwkwex) {
      return TEXTURE;
   }
}
