package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.entity.layer.EntityRenderLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer extends EntityRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final NativeImageBackedTexture f_84krvapht = new NativeImageBackedTexture(16, 16);
   protected Model model;
   protected FloatBuffer f_62hfmskxz = MemoryTracker.createFloatBuffer(4);
   protected List layers = Lists.newArrayList();
   protected boolean solidRender = false;

   public LivingEntityRenderer(EntityRenderDispatcher dispatcher, Model model, float shadowSize) {
      super(dispatcher);
      this.model = model;
      this.shadowSize = shadowSize;
   }

   protected boolean addLayer(EntityRenderLayer layer) {
      return this.layers.add(layer);
   }

   protected boolean removeLayer(EntityRenderLayer layer) {
      return this.layers.remove(layer);
   }

   public Model getModel() {
      return this.model;
   }

   protected float getRotatedAngle(float prevAng, float ang, float tickDelta) {
      float var4 = ang - prevAng;

      while(var4 < -180.0F) {
         var4 += 360.0F;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return prevAng + tickDelta * var4;
   }

   public void m_81npivqro() {
   }

   public void render(LivingEntity c_97zulxhng, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.model.handSwingProgress = this.getHandSwingProgress(c_97zulxhng, h);
      this.model.hasVehicle = c_97zulxhng.hasVehicle();
      this.model.isBaby = c_97zulxhng.isBaby();

      try {
         float var10 = this.getRotatedAngle(c_97zulxhng.prevBodyYaw, c_97zulxhng.bodyYaw, h);
         float var11 = this.getRotatedAngle(c_97zulxhng.prevHeadYaw, c_97zulxhng.headYaw, h);
         float var12 = var11 - var10;
         if (c_97zulxhng.hasVehicle() && c_97zulxhng.vehicle instanceof LivingEntity) {
            LivingEntity var13 = (LivingEntity)c_97zulxhng.vehicle;
            var10 = this.getRotatedAngle(var13.prevBodyYaw, var13.bodyYaw, h);
            var12 = var11 - var10;
            float var14 = MathHelper.wrapDegrees(var12);
            if (var14 < -85.0F) {
               var14 = -85.0F;
            }

            if (var14 >= 85.0F) {
               var14 = 85.0F;
            }

            var10 = var11 - var14;
            if (var14 * var14 > 2500.0F) {
               var10 += var14 * 0.2F;
            }
         }

         float var21 = c_97zulxhng.prevPitch + (c_97zulxhng.pitch - c_97zulxhng.prevPitch) * h;
         this.applyTranslation(c_97zulxhng, d, e, f);
         float var22 = this.getEntityAge(c_97zulxhng, h);
         this.applyRotation(c_97zulxhng, var22, var10, h);
         GlStateManager.enableRescaleNormal();
         GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
         this.scale(c_97zulxhng, h);
         float var15 = 0.0625F;
         GlStateManager.translatef(0.0F, -1.5078125F, 0.0F);
         float var16 = c_97zulxhng.prevHandSwingAmount + (c_97zulxhng.handSwingAmount - c_97zulxhng.prevHandSwingAmount) * h;
         float var17 = c_97zulxhng.handSwing - c_97zulxhng.handSwingAmount * (1.0F - h);
         if (c_97zulxhng.isBaby()) {
            var17 *= 3.0F;
         }

         if (var16 > 1.0F) {
            var16 = 1.0F;
         }

         GlStateManager.enableAlphaTest();
         this.model.renderMobAnimation(c_97zulxhng, var17, var16, h);
         this.model.setAngles(var17, var16, var22, var12, var21, 0.0625F, c_97zulxhng);
         if (this.solidRender) {
            boolean var23 = this.renderModel(c_97zulxhng);
            this.renderHand(c_97zulxhng, var17, var16, var22, var12, var21, 0.0625F);
            if (var23) {
               this.tearDownSolidState();
            }
         } else {
            boolean var18 = this.renderLayers(c_97zulxhng, h);
            this.renderHand(c_97zulxhng, var17, var16, var22, var12, var21, 0.0625F);
            if (var18) {
               this.tearDownOverlayColor();
            }

            GlStateManager.depthMask(true);
            if (!(c_97zulxhng instanceof PlayerEntity) || !((PlayerEntity)c_97zulxhng).isSpectator()) {
               this.renderLayers(c_97zulxhng, var17, var16, h, var22, var12, var21, 0.0625F);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
         LOGGER.error("Couldn't render entity", var19);
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      if (!this.solidRender) {
         super.render(c_97zulxhng, d, e, f, g, h);
      }
   }

   protected boolean renderModel(LivingEntity c_97zulxhng) {
      int var2 = 16777215;
      if (c_97zulxhng instanceof PlayerEntity) {
         Team var3 = (Team)c_97zulxhng.getScoreboardTeam();
         if (var3 != null) {
            String var4 = TextRenderer.isolateFormatting(var3.getPrefix());
            if (var4.length() >= 2) {
               var2 = this.getFontRenderer().getColor(var4.charAt(1));
            }
         }
      }

      float var6 = (float)(var2 >> 16 & 0xFF) / 255.0F;
      float var7 = (float)(var2 >> 8 & 0xFF) / 255.0F;
      float var5 = (float)(var2 & 0xFF) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.color4f(var6, var7, var5, 1.0F);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      return true;
   }

   protected void tearDownSolidState() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void renderHand(LivingEntity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      boolean var8 = !entity.isInvisible();
      boolean var9 = !var8 && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
      if (var8 || var9) {
         if (!this.bindTexture(entity)) {
            return;
         }

         if (var9) {
            GlStateManager.pushMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.model.render(entity, handSwing, handSwingAmount, age, yaw, pitch, scale);
         if (var9) {
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }
   }

   protected boolean renderLayers(LivingEntity entity, float tickDelta) {
      return this.renderLayers(entity, tickDelta, true);
   }

   protected boolean renderLayers(LivingEntity entity, float tickDelta, boolean bl) {
      float var4 = entity.getBrightness(tickDelta);
      int var5 = this.getCreeperFuseTime(entity, var4, tickDelta);
      boolean var6 = (var5 >> 24 & 0xFF) > 0;
      boolean var7 = entity.hurtTimer > 0 || entity.deathTicks > 0;
      if (!var6 && !var7) {
         return false;
      } else if (!var6 && !bl) {
         return false;
      } else {
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableTexture();
         GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, 8448);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableTexture();
         GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND2_RGB, 770);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
         ((Buffer)this.f_62hfmskxz).position(0);
         if (var7) {
            this.f_62hfmskxz.put(1.0F);
            this.f_62hfmskxz.put(0.0F);
            this.f_62hfmskxz.put(0.0F);
            this.f_62hfmskxz.put(0.3F);
         } else {
            float var8 = (float)(var5 >> 24 & 0xFF) / 255.0F;
            float var9 = (float)(var5 >> 16 & 0xFF) / 255.0F;
            float var10 = (float)(var5 >> 8 & 0xFF) / 255.0F;
            float var11 = (float)(var5 & 0xFF) / 255.0F;
            this.f_62hfmskxz.put(var9);
            this.f_62hfmskxz.put(var10);
            this.f_62hfmskxz.put(var11);
            this.f_62hfmskxz.put(1.0F - var8);
         }

         ((Buffer)this.f_62hfmskxz).flip();
         GL11.glTexEnv(8960, 8705, this.f_62hfmskxz);
         GlStateManager.activeTexture(GLX.GL_TEXTURE2);
         GlStateManager.enableTexture();
         GlStateManager.bindTexture(f_84krvapht.getGlId());
         GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, 8448);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
         GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         return true;
      }
   }

   protected void tearDownOverlayColor() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableTexture();
      GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND1_ALPHA, 770);
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.activeTexture(GLX.GL_TEXTURE2);
      GlStateManager.disableTexture();
      GlStateManager.bindTexture(0);
      GL11.glTexEnvi(8960, 8704, GLX.GL_COMBINE);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GL11.glTexEnvi(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void applyTranslation(LivingEntity entity, double dx, double dy, double f) {
      GlStateManager.translatef((float)dx, (float)dy, (float)f);
   }

   protected void applyRotation(LivingEntity entity, float yaw, float bodyYaw, float tickDelta) {
      GlStateManager.rotatef(180.0F - bodyYaw, 0.0F, 1.0F, 0.0F);
      if (entity.deathTicks > 0) {
         float var5 = ((float)entity.deathTicks + tickDelta - 1.0F) / 20.0F * 1.6F;
         var5 = MathHelper.sqrt(var5);
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         GlStateManager.rotatef(var5 * this.getYawWhileDead(entity), 0.0F, 0.0F, 1.0F);
      } else {
         String var7 = Formatting.strip(entity.getName());
         if (var7 != null
            && (var7.equals("Dinnerbone") || var7.equals("Grumm"))
            && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).hidesCape(PlayerModelPart.CAPE))) {
            GlStateManager.translatef(0.0F, entity.height + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   protected float getHandSwingProgress(LivingEntity entity, float tickDelta) {
      return entity.getHandSwingProcess(tickDelta);
   }

   protected float getEntityAge(LivingEntity entity, float tickDelta) {
      return (float)entity.time + tickDelta;
   }

   protected void renderLayers(
      LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale
   ) {
      for(EntityRenderLayer var10 : this.layers) {
         boolean var11 = this.renderLayers(entity, tickDelta, var10.colorsWhenDamaged());
         var10.render(entity, handSwingAmount, handSwing, tickDelta, age, headYaw, headPitch, scale);
         if (var11) {
            this.tearDownOverlayColor();
         }
      }
   }

   protected float getYawWhileDead(LivingEntity entity) {
      return 90.0F;
   }

   protected int getCreeperFuseTime(LivingEntity entity, float f, float timeDelta) {
      return 0;
   }

   protected void scale(LivingEntity entity, float scale) {
   }

   public void renderNameTag(LivingEntity c_97zulxhng, double d, double e, double f) {
      if (this.shouldRenderNameTag(c_97zulxhng)) {
         double var8 = c_97zulxhng.getSquaredDistanceTo(this.dispatcher.camera);
         float var10 = c_97zulxhng.isSneaking() ? 32.0F : 64.0F;
         if (!(var8 >= (double)(var10 * var10))) {
            String var11 = c_97zulxhng.getDisplayName().buildFormattedString();
            float var12 = 0.02666667F;
            GlStateManager.alphaFunc(516, 0.1F);
            if (c_97zulxhng.isSneaking()) {
               TextRenderer var13 = this.getFontRenderer();
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)d, (float)e + c_97zulxhng.height + 0.5F, (float)f);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef(-this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef(this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
               GlStateManager.scalef(-0.02666667F, -0.02666667F, 0.02666667F);
               GlStateManager.translatef(0.0F, 9.374999F, 0.0F);
               GlStateManager.disableLighting();
               GlStateManager.depthMask(false);
               GlStateManager.disableBlend();
               GlStateManager.disableTexture();
               GlStateManager.blendFuncSeparate(770, 771, 1, 0);
               Tessellator var14 = Tessellator.getInstance();
               BufferBuilder var15 = var14.getBufferBuilder();
               var15.start();
               int var16 = var13.getStringWidth(var11) / 2;
               var15.color(0.0F, 0.0F, 0.0F, 0.25F);
               var15.vertex((double)(-var16 - 1), -1.0, 0.0);
               var15.vertex((double)(-var16 - 1), 8.0, 0.0);
               var15.vertex((double)(var16 + 1), 8.0, 0.0);
               var15.vertex((double)(var16 + 1), -1.0, 0.0);
               var14.end();
               GlStateManager.enableTexture();
               GlStateManager.depthMask(true);
               var13.drawWithoutShadow(var11, -var13.getStringWidth(var11) / 2, 0, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.enableBlend();
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderNameTags(c_97zulxhng, d, e, f, var11, 0.02666667F, var8);
            }
         }
      }
   }

   protected boolean shouldRenderNameTag(LivingEntity c_97zulxhng) {
      LocalClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (c_97zulxhng instanceof PlayerEntity && c_97zulxhng != var2) {
         AbstractTeam var3 = c_97zulxhng.getScoreboardTeam();
         AbstractTeam var4 = var2.getScoreboardTeam();
         if (var3 != null) {
            AbstractTeam.Visibility var5 = var3.getNameTagVisibility();
            switch(var5) {
               case ALWAYS:
                  return true;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return var4 == null || var3.isEqual(var4);
               case HIDE_FOR_OWN_TEAM:
                  return var4 == null || !var3.isEqual(var4);
               default:
                  return true;
            }
         }
      }

      return MinecraftClient.isHudDisabled() && c_97zulxhng != this.dispatcher.camera && !c_97zulxhng.isInvisibleTo(var2) && c_97zulxhng.rider == null;
   }

   public void setSolidRender(boolean onlySolidLayers) {
      this.solidRender = onlySolidLayers;
   }

   static {
      int[] var0 = f_84krvapht.getRgbArray();

      for(int var1 = 0; var1 < 256; ++var1) {
         var0[var1] = -1;
      }

      f_84krvapht.upload();
   }
}
