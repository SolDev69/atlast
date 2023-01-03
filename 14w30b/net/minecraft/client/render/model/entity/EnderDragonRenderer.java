package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.layer.EnderDragonDeathLayer;
import net.minecraft.client.render.entity.layer.EnderDragonEyesLayer;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.boss.BossBar;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderDragonRenderer extends MobRenderer {
   private static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal_beam.png");
   private static final Identifier EXPLOSION_TEXTURE = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
   private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon.png");
   protected EnderDragonModel model = (EnderDragonModel)this.model;

   public EnderDragonRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new EnderDragonModel(0.0F), 0.5F);
      this.addLayer(new EnderDragonEyesLayer(this));
      this.addLayer(new EnderDragonDeathLayer());
   }

   protected void applyRotation(EnderDragonEntity c_36agmpvyu, float f, float g, float h) {
      float var5 = (float)c_36agmpvyu.getSegmentProperties(7, h)[0];
      float var6 = (float)(c_36agmpvyu.getSegmentProperties(5, h)[1] - c_36agmpvyu.getSegmentProperties(10, h)[1]);
      GlStateManager.rotatef(-var5, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, 1.0F);
      if (c_36agmpvyu.deathTicks > 0) {
         float var7 = ((float)c_36agmpvyu.deathTicks + h - 1.0F) / 20.0F * 1.6F;
         var7 = MathHelper.sqrt(var7);
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         GlStateManager.rotatef(var7 * this.getYawWhileDead(c_36agmpvyu), 0.0F, 0.0F, 1.0F);
      }
   }

   protected void renderHand(EnderDragonEntity c_36agmpvyu, float f, float g, float h, float i, float j, float k) {
      if (c_36agmpvyu.ticksSinceDeath > 0) {
         float var8 = (float)c_36agmpvyu.ticksSinceDeath / 200.0F;
         GlStateManager.depthFunc(515);
         GlStateManager.enableAlphaTest();
         GlStateManager.alphaFunc(516, var8);
         this.bindTexture(EXPLOSION_TEXTURE);
         this.model.render(c_36agmpvyu, f, g, h, i, j, k);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthFunc(514);
      }

      this.bindTexture(c_36agmpvyu);
      this.model.render(c_36agmpvyu, f, g, h, i, j, k);
      if (c_36agmpvyu.hurtTimer > 0) {
         GlStateManager.depthFunc(514);
         GlStateManager.disableTexture();
         GlStateManager.disableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color4f(1.0F, 0.0F, 0.0F, 0.5F);
         this.model.render(c_36agmpvyu, f, g, h, i, j, k);
         GlStateManager.enableTexture();
         GlStateManager.enableBlend();
         GlStateManager.depthFunc(515);
      }
   }

   public void render(EnderDragonEntity c_36agmpvyu, double d, double e, double f, float g, float h) {
      BossBar.update(c_36agmpvyu, false);
      super.render((MobEntity)c_36agmpvyu, d, e, f, g, h);
      if (c_36agmpvyu.connectedCrystal != null) {
         this.render(c_36agmpvyu, d, e, f, h);
      }
   }

   protected void render(EnderDragonEntity c_36agmpvyu, double d, double e, double f, float g) {
      float var9 = (float)c_36agmpvyu.connectedCrystal.age + g;
      float var10 = MathHelper.sin(var9 * 0.2F) / 2.0F + 0.5F;
      var10 = (var10 * var10 + var10) * 0.2F;
      float var11 = (float)(c_36agmpvyu.connectedCrystal.x - c_36agmpvyu.x - (c_36agmpvyu.prevX - c_36agmpvyu.x) * (double)(1.0F - g));
      float var12 = (float)((double)var10 + c_36agmpvyu.connectedCrystal.y - 1.0 - c_36agmpvyu.y - (c_36agmpvyu.prevY - c_36agmpvyu.y) * (double)(1.0F - g));
      float var13 = (float)(c_36agmpvyu.connectedCrystal.z - c_36agmpvyu.z - (c_36agmpvyu.prevZ - c_36agmpvyu.z) * (double)(1.0F - g));
      float var14 = MathHelper.sqrt(var11 * var11 + var13 * var13);
      float var15 = MathHelper.sqrt(var11 * var11 + var12 * var12 + var13 * var13);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e + 2.0F, (float)f);
      GlStateManager.rotatef((float)(-Math.atan2((double)var13, (double)var11)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(-Math.atan2((double)var14, (double)var12)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
      Tessellator var16 = Tessellator.getInstance();
      BufferBuilder var17 = var16.getBufferBuilder();
      Lighting.turnOff();
      GlStateManager.disableCull();
      this.bindTexture(BEAM_TEXTURE);
      GlStateManager.shadeModel(7425);
      float var18 = 0.0F - ((float)c_36agmpvyu.time + g) * 0.01F;
      float var19 = MathHelper.sqrt(var11 * var11 + var12 * var12 + var13 * var13) / 32.0F - ((float)c_36agmpvyu.time + g) * 0.01F;
      var17.start(5);
      byte var20 = 8;

      for(int var21 = 0; var21 <= var20; ++var21) {
         float var22 = MathHelper.sin((float)(var21 % var20) * (float) Math.PI * 2.0F / (float)var20) * 0.75F;
         float var23 = MathHelper.cos((float)(var21 % var20) * (float) Math.PI * 2.0F / (float)var20) * 0.75F;
         float var24 = (float)(var21 % var20) * 1.0F / (float)var20;
         var17.color(0);
         var17.vertex((double)(var22 * 0.2F), (double)(var23 * 0.2F), 0.0, (double)var24, (double)var19);
         var17.color(16777215);
         var17.vertex((double)var22, (double)var23, (double)var15, (double)var24, (double)var18);
      }

      var16.end();
      GlStateManager.enableCull();
      GlStateManager.shadeModel(7424);
      Lighting.turnOn();
      GlStateManager.popMatrix();
   }

   protected Identifier getTexture(EnderDragonEntity c_36agmpvyu) {
      return TEXTURE;
   }
}
