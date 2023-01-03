package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FishingBobberRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/particle/particles.png");

   public FishingBobberRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   public void render(FishingBobberEntity c_44sglgmne, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e, (float)f);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      this.bindTexture(c_44sglgmne);
      Tessellator var10 = Tessellator.getInstance();
      BufferBuilder var11 = var10.getBufferBuilder();
      byte var12 = 1;
      byte var13 = 2;
      float var14 = (float)(var12 * 8 + 0) / 128.0F;
      float var15 = (float)(var12 * 8 + 8) / 128.0F;
      float var16 = (float)(var13 * 8 + 0) / 128.0F;
      float var17 = (float)(var13 * 8 + 8) / 128.0F;
      float var18 = 1.0F;
      float var19 = 0.5F;
      float var20 = 0.5F;
      GlStateManager.rotatef(180.0F - this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
      var11.start();
      var11.normal(0.0F, 1.0F, 0.0F);
      var11.vertex((double)(0.0F - var19), (double)(0.0F - var20), 0.0, (double)var14, (double)var17);
      var11.vertex((double)(var18 - var19), (double)(0.0F - var20), 0.0, (double)var15, (double)var17);
      var11.vertex((double)(var18 - var19), (double)(1.0F - var20), 0.0, (double)var15, (double)var16);
      var11.vertex((double)(0.0F - var19), (double)(1.0F - var20), 0.0, (double)var14, (double)var16);
      var10.end();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      if (c_44sglgmne.player != null) {
         float var21 = c_44sglgmne.player.getHandSwingProcess(h);
         float var22 = MathHelper.sin(MathHelper.sqrt(var21) * (float) Math.PI);
         double var23 = -0.5;
         Vec3d var25 = new Vec3d(var23, 0.03, 0.8);
         var25 = var25.rotateX(-(c_44sglgmne.player.prevPitch + (c_44sglgmne.player.pitch - c_44sglgmne.player.prevPitch) * h) * (float) Math.PI / 180.0F);
         var25 = var25.rotateY(-(c_44sglgmne.player.prevYaw + (c_44sglgmne.player.yaw - c_44sglgmne.player.prevYaw) * h) * (float) Math.PI / 180.0F);
         var25 = var25.rotateY(var22 * 0.5F);
         var25 = var25.rotateX(-var22 * 0.7F);
         double var26 = c_44sglgmne.player.prevX + (c_44sglgmne.player.x - c_44sglgmne.player.prevX) * (double)h + var25.x;
         double var28 = c_44sglgmne.player.prevY + (c_44sglgmne.player.y - c_44sglgmne.player.prevY) * (double)h + var25.y;
         double var30 = c_44sglgmne.player.prevZ + (c_44sglgmne.player.z - c_44sglgmne.player.prevZ) * (double)h + var25.z;
         double var32 = (double)c_44sglgmne.player.getEyeHeight();
         if (this.dispatcher.options.perspective > 0 || c_44sglgmne.player != MinecraftClient.getInstance().player) {
            float var34 = (c_44sglgmne.player.prevBodyYaw + (c_44sglgmne.player.bodyYaw - c_44sglgmne.player.prevBodyYaw) * h) * (float) Math.PI / 180.0F;
            double var35 = (double)MathHelper.sin(var34);
            double var37 = (double)MathHelper.cos(var34);
            var26 = c_44sglgmne.player.prevX + (c_44sglgmne.player.x - c_44sglgmne.player.prevX) * (double)h - var37 * 0.35 - var35 * 0.85;
            var28 = c_44sglgmne.player.prevY + var32 + (c_44sglgmne.player.y - c_44sglgmne.player.prevY) * (double)h - 0.45;
            var30 = c_44sglgmne.player.prevZ + (c_44sglgmne.player.z - c_44sglgmne.player.prevZ) * (double)h - var35 * 0.35 + var37 * 0.85;
         }

         double var53 = c_44sglgmne.prevX + (c_44sglgmne.x - c_44sglgmne.prevX) * (double)h;
         double var36 = c_44sglgmne.prevY + (c_44sglgmne.y - c_44sglgmne.prevY) * (double)h + 0.25;
         double var38 = c_44sglgmne.prevZ + (c_44sglgmne.z - c_44sglgmne.prevZ) * (double)h;
         double var40 = (double)((float)(var26 - var53));
         double var42 = (double)((float)(var28 - var36)) + var32;
         double var44 = (double)((float)(var30 - var38));
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         var11.start(3);
         var11.color(0);
         byte var46 = 16;

         for(int var47 = 0; var47 <= var46; ++var47) {
            float var48 = (float)var47 / (float)var46;
            var11.vertex(d + var40 * (double)var48, e + var42 * (double)(var48 * var48 + var48) * 0.5 + 0.25, f + var44 * (double)var48);
         }

         var10.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         super.render(c_44sglgmne, d, e, f, g, h);
      }
   }

   protected Identifier getTexture(FishingBobberEntity c_44sglgmne) {
      return TEXTURE;
   }
}
