package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ExperienceOrbRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/experience_orb.png");

   public ExperienceOrbRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.15F;
      this.shadowDarkness = 0.75F;
   }

   public void render(XpOrbEntity c_64zvdesqf, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e, (float)f);
      this.bindTexture(c_64zvdesqf);
      int var10 = c_64zvdesqf.getSize();
      float var11 = (float)(var10 % 4 * 16 + 0) / 64.0F;
      float var12 = (float)(var10 % 4 * 16 + 16) / 64.0F;
      float var13 = (float)(var10 / 4 * 16 + 0) / 64.0F;
      float var14 = (float)(var10 / 4 * 16 + 16) / 64.0F;
      float var15 = 1.0F;
      float var16 = 0.5F;
      float var17 = 0.25F;
      int var18 = c_64zvdesqf.getLightLevel(h);
      int var19 = var18 % 65536;
      int var20 = var18 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var19 / 1.0F, (float)var20 / 1.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var27 = 255.0F;
      float var28 = ((float)c_64zvdesqf.renderTicks + h) / 2.0F;
      var20 = (int)((MathHelper.sin(var28 + 0.0F) + 1.0F) * 0.5F * var27);
      int var21 = (int)var27;
      int var22 = (int)((MathHelper.sin(var28 + ((float) (Math.PI * 4.0 / 3.0))) + 1.0F) * 0.1F * var27);
      int var23 = var20 << 16 | var21 << 8 | var22;
      GlStateManager.rotatef(180.0F - this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
      float var24 = 0.3F;
      GlStateManager.scalef(var24, var24, var24);
      Tessellator var25 = Tessellator.getInstance();
      BufferBuilder var26 = var25.getBufferBuilder();
      var26.start();
      var26.color(var23, 128);
      var26.normal(0.0F, 1.0F, 0.0F);
      var26.vertex((double)(0.0F - var16), (double)(0.0F - var17), 0.0, (double)var11, (double)var14);
      var26.vertex((double)(var15 - var16), (double)(0.0F - var17), 0.0, (double)var12, (double)var14);
      var26.vertex((double)(var15 - var16), (double)(1.0F - var17), 0.0, (double)var12, (double)var13);
      var26.vertex((double)(0.0F - var16), (double)(1.0F - var17), 0.0, (double)var11, (double)var13);
      var25.end();
      GlStateManager.enableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(c_64zvdesqf, d, e, f, g, h);
   }

   protected Identifier getTexture(XpOrbEntity c_64zvdesqf) {
      return TEXTURE;
   }
}
