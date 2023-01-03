package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DecorationEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class MobRenderer extends LivingEntityRenderer {
   public MobRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected boolean shouldRenderNameTag(MobEntity c_81psrrogw) {
      return super.shouldRenderNameTag((LivingEntity)c_81psrrogw)
         && (c_81psrrogw.shouldShowNameTag() || c_81psrrogw.hasCustomName() && c_81psrrogw == this.dispatcher.targetEntity);
   }

   public boolean shouldRender(MobEntity c_81psrrogw, Culler c_72tlvecqx, double d, double e, double f) {
      if (super.shouldRender(c_81psrrogw, c_72tlvecqx, d, e, f)) {
         return true;
      } else if (c_81psrrogw.isLeashed() && c_81psrrogw.getHoldingEntity() != null) {
         Entity var9 = c_81psrrogw.getHoldingEntity();
         return c_72tlvecqx.isVisible(var9.getBoundingBox());
      } else {
         return false;
      }
   }

   public void render(MobEntity c_81psrrogw, double d, double e, double f, float g, float h) {
      super.render((LivingEntity)c_81psrrogw, d, e, f, g, h);
      this.renderRiders(c_81psrrogw, d, e, f, g, h);
   }

   private double getDistanceMoved(double prevPos, double pos, double f) {
      return prevPos + (pos - prevPos) * f;
   }

   protected void renderRiders(MobEntity entity, double dX, double dY, double dZ, float g, float h) {
      Entity var10 = entity.getHoldingEntity();
      if (var10 != null) {
         dY -= (1.6 - (double)entity.height) * 0.5;
         Tessellator var11 = Tessellator.getInstance();
         BufferBuilder var12 = var11.getBufferBuilder();
         double var13 = this.getDistanceMoved((double)var10.prevYaw, (double)var10.yaw, (double)(h * 0.5F)) * (float) (Math.PI / 180.0);
         double var15 = this.getDistanceMoved((double)var10.prevPitch, (double)var10.pitch, (double)(h * 0.5F)) * (float) (Math.PI / 180.0);
         double var17 = Math.cos(var13);
         double var19 = Math.sin(var13);
         double var21 = Math.sin(var15);
         if (var10 instanceof DecorationEntity) {
            var17 = 0.0;
            var19 = 0.0;
            var21 = -1.0;
         }

         double var23 = Math.cos(var15);
         double var25 = this.getDistanceMoved(var10.prevX, var10.x, (double)h) - var17 * 0.7 - var19 * 0.5 * var23;
         double var27 = this.getDistanceMoved(var10.prevY + (double)var10.getEyeHeight() * 0.7, var10.y + (double)var10.getEyeHeight() * 0.7, (double)h)
            - var21 * 0.5
            - 0.25;
         double var29 = this.getDistanceMoved(var10.prevZ, var10.z, (double)h) - var19 * 0.7 + var17 * 0.5 * var23;
         double var31 = this.getDistanceMoved((double)entity.prevBodyYaw, (double)entity.bodyYaw, (double)h) * (float) (Math.PI / 180.0) + (Math.PI / 2);
         var17 = Math.cos(var31) * (double)entity.width * 0.4;
         var19 = Math.sin(var31) * (double)entity.width * 0.4;
         double var33 = this.getDistanceMoved(entity.prevX, entity.x, (double)h) + var17;
         double var35 = this.getDistanceMoved(entity.prevY, entity.y, (double)h);
         double var37 = this.getDistanceMoved(entity.prevZ, entity.z, (double)h) + var19;
         dX += var17;
         dZ += var19;
         double var39 = (double)((float)(var25 - var33));
         double var41 = (double)((float)(var27 - var35));
         double var43 = (double)((float)(var29 - var37));
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         boolean var45 = true;
         double var46 = 0.025;
         var12.start(5);

         for(int var48 = 0; var48 <= 24; ++var48) {
            if (var48 % 2 == 0) {
               var12.color(0.5F, 0.4F, 0.3F, 1.0F);
            } else {
               var12.color(0.35F, 0.28F, 0.21000001F, 1.0F);
            }

            float var49 = (float)var48 / 24.0F;
            var12.vertex(
               dX + var39 * (double)var49 + 0.0,
               dY + var41 * (double)(var49 * var49 + var49) * 0.5 + (double)((24.0F - (float)var48) / 18.0F + 0.125F),
               dZ + var43 * (double)var49
            );
            var12.vertex(
               dX + var39 * (double)var49 + 0.025,
               dY + var41 * (double)(var49 * var49 + var49) * 0.5 + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025,
               dZ + var43 * (double)var49
            );
         }

         var11.end();
         var12.start(5);

         for(int var55 = 0; var55 <= 24; ++var55) {
            if (var55 % 2 == 0) {
               var12.color(0.5F, 0.4F, 0.3F, 1.0F);
            } else {
               var12.color(0.35F, 0.28F, 0.21000001F, 1.0F);
            }

            float var56 = (float)var55 / 24.0F;
            var12.vertex(
               dX + var39 * (double)var56 + 0.0,
               dY + var41 * (double)(var56 * var56 + var56) * 0.5 + (double)((24.0F - (float)var55) / 18.0F + 0.125F) + 0.025,
               dZ + var43 * (double)var56
            );
            var12.vertex(
               dX + var39 * (double)var56 + 0.025,
               dY + var41 * (double)(var56 * var56 + var56) * 0.5 + (double)((24.0F - (float)var55) / 18.0F + 0.125F),
               dZ + var43 * (double)var56 + 0.025
            );
         }

         var11.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         GlStateManager.enableCull();
      }
   }
}
