package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class HeldItemRenderer {
   private static final Identifier MAP_TEXTURE = new Identifier("textures/map/map_background.png");
   private static final Identifier UNDERWATER_TEXTURE = new Identifier("textures/misc/underwater.png");
   private final MinecraftClient client;
   private ItemStack item;
   private float swapAnimationTicks;
   private float lastHoldAnimationTicks;
   private final EntityRenderDispatcher entity;
   private final ItemRenderer renderer;
   private int selectedSlot = -1;

   public HeldItemRenderer(MinecraftClient client) {
      this.client = client;
      this.entity = client.getEntityRenderDispatcher();
      this.renderer = client.getItemRenderer();
   }

   public void render(LivingEntity entity, ItemStack item, ModelTransformations.Type transform) {
      if (item != null) {
         Item var4 = item.getItem();
         Block var5 = Block.byItem(var4);
         GlStateManager.pushMatrix();
         if (this.renderer.isGui3d(item)) {
            GlStateManager.scalef(2.0F, 2.0F, 2.0F);
            if (this.isTranslucent(var5)) {
               GlStateManager.depthMask(false);
            }
         }

         this.renderer.renderHeldItem(item, entity, transform);
         if (this.isTranslucent(var5)) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   private boolean isTranslucent(Block block) {
      return block != null && block.getRenderLayer() == BlockLayer.TRANSLUCENT;
   }

   private void rotate(float x, float y) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(x, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(y, 0.0F, 1.0F, 0.0F);
      Lighting.turnOn();
      GlStateManager.popMatrix();
   }

   private void m_11iigoupi(ClientPlayerEntity c_95zrfkavi) {
      int var2 = this.client.world.getLightColor(new BlockPos(c_95zrfkavi), 0);
      float var3 = (float)(var2 & 65535);
      float var4 = (float)(var2 >> 16);
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, var3, var4);
   }

   private void m_99vcluupu(LocalClientPlayerEntity c_08urhdhqb, float f) {
      float var3 = c_08urhdhqb.f_45fovxhju + (c_08urhdhqb.f_40xrfnzqh - c_08urhdhqb.f_45fovxhju) * f;
      float var4 = c_08urhdhqb.f_68idpvjry + (c_08urhdhqb.f_49gybzpgn - c_08urhdhqb.f_68idpvjry) * f;
      GlStateManager.rotatef((c_08urhdhqb.pitch - var3) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((c_08urhdhqb.yaw - var4) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float m_99vutcbrm(float f) {
      float var2 = 1.0F - f / 45.0F + 0.1F;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      return -MathHelper.cos(var2 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderRightArm(PlayerRenderer player) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(0.25F, -0.85F, 0.75F);
      player.renderPlayerHandModel(this.client.player);
      GlStateManager.popMatrix();
   }

   private void renderLeftArm(PlayerRenderer player) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(-0.3F, -1.1F, 0.45F);
      player.m_23uzhiwco(this.client.player);
      GlStateManager.popMatrix();
   }

   private void renderArms(ClientPlayerEntity player) {
      this.client.getTextureManager().bind(player.getSkinTexture());
      EntityRenderer var2 = this.entity.getRenderer(this.client.player);
      PlayerRenderer var3 = (PlayerRenderer)var2;
      if (!player.isInvisible()) {
         this.renderRightArm(var3);
         this.renderLeftArm(var3);
      }
   }

   private void renderMap(ClientPlayerEntity c_95zrfkavi, float f, float g, float h) {
      float var5 = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
      float var6 = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI * 2.0F);
      float var7 = -0.2F * MathHelper.sin(h * (float) Math.PI);
      GlStateManager.translatef(var5, var6, var7);
      float var8 = this.m_99vutcbrm(f);
      GlStateManager.translatef(0.0F, 0.04F, -0.72F);
      GlStateManager.translatef(0.0F, g * -1.2F, 0.0F);
      GlStateManager.translatef(0.0F, var8 * -0.5F, 0.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var8 * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderArms(c_95zrfkavi);
      float var9 = MathHelper.sin(h * h * (float) Math.PI);
      float var10 = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
      GlStateManager.rotatef(var9 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var10 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(var10 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(0.38F, 0.38F, 0.38F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
      GlStateManager.scalef(0.015625F, 0.015625F, 0.015625F);
      this.client.getTextureManager().bind(MAP_TEXTURE);
      Tessellator var11 = Tessellator.getInstance();
      BufferBuilder var12 = var11.getBufferBuilder();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      var12.start();
      var12.vertex(-7.0, 135.0, 0.0, 0.0, 1.0);
      var12.vertex(135.0, 135.0, 0.0, 1.0, 1.0);
      var12.vertex(135.0, -7.0, 0.0, 1.0, 0.0);
      var12.vertex(-7.0, -7.0, 0.0, 0.0, 0.0);
      var11.end();
      SavedMapData var13 = Items.FILLED_MAP.getSavedMapData(this.item, this.client.world);
      if (var13 != null) {
         this.client.gameRenderer.getMapRenderer().draw(var13, false);
      }
   }

   private void renderHand(ClientPlayerEntity c_95zrfkavi, float f, float g) {
      float var4 = -0.3F * MathHelper.sin(MathHelper.sqrt(g) * (float) Math.PI);
      float var5 = 0.4F * MathHelper.sin(MathHelper.sqrt(g) * (float) Math.PI * 2.0F);
      float var6 = -0.4F * MathHelper.sin(g * (float) Math.PI);
      GlStateManager.translatef(var4, var5, var6);
      GlStateManager.translatef(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translatef(0.0F, f * -0.6F, 0.0F);
      GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
      float var7 = MathHelper.sin(g * g * (float) Math.PI);
      float var8 = MathHelper.sin(MathHelper.sqrt(g) * (float) Math.PI);
      GlStateManager.rotatef(var8 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var7 * -20.0F, 0.0F, 0.0F, 1.0F);
      this.client.getTextureManager().bind(c_95zrfkavi.getSkinTexture());
      GlStateManager.translatef(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotatef(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
      GlStateManager.translatef(5.6F, 0.0F, 0.0F);
      EntityRenderer var9 = this.entity.getRenderer(this.client.player);
      PlayerRenderer var10 = (PlayerRenderer)var9;
      var10.renderPlayerHandModel(this.client.player);
   }

   private void applyHandSwing(float f) {
      float var2 = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
      float var3 = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI * 2.0F);
      float var4 = -0.2F * MathHelper.sin(f * (float) Math.PI);
      GlStateManager.translatef(var2, var3, var4);
   }

   private void applyConsuming(ClientPlayerEntity c_95zrfkavi, float f) {
      float var3 = (float)c_95zrfkavi.getItemUseTimer() - f + 1.0F;
      float var4 = var3 / (float)this.item.getUseDuration();
      float var5 = MathHelper.abs(MathHelper.cos(var3 / 4.0F * (float) Math.PI) * 0.1F);
      if (var4 >= 0.8F) {
         var5 = 0.0F;
      }

      GlStateManager.translatef(0.0F, var5, 0.0F);
      float var6 = 1.0F - (float)Math.pow((double)var4, 27.0);
      GlStateManager.translatef(var6 * 0.6F, var6 * -0.5F, var6 * 0.0F);
      GlStateManager.rotatef(var6 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var6 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void appyFirstPersonTransform(float xU, float yU) {
      GlStateManager.translatef(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translatef(0.0F, xU * -0.6F, 0.0F);
      GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
      float var3 = MathHelper.sin(yU * yU * (float) Math.PI);
      float var4 = MathHelper.sin(MathHelper.sqrt(yU) * (float) Math.PI);
      GlStateManager.rotatef(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(0.4F, 0.4F, 0.4F);
   }

   private void applyBowNocking(float f, ClientPlayerEntity c_95zrfkavi) {
      GlStateManager.rotatef(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(-0.9F, 0.2F, 0.0F);
      float var3 = (float)this.item.getUseDuration() - ((float)c_95zrfkavi.getItemUseTimer() - f + 1.0F);
      float var4 = var3 / 20.0F;
      var4 = (var4 * var4 + var4 * 2.0F) / 3.0F;
      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      if (var4 > 0.1F) {
         float var5 = MathHelper.sin((var3 - 0.1F) * 1.3F);
         float var6 = var4 - 0.1F;
         float var7 = var5 * var6;
         GlStateManager.translatef(var7 * 0.0F, var7 * 0.01F, var7 * 0.0F);
      }

      GlStateManager.translatef(var4 * 0.0F, var4 * 0.0F, var4 * 0.1F);
      GlStateManager.scalef(1.0F, 1.0F, 1.0F + var4 * 0.2F);
   }

   private void applySwordBlocking() {
      GlStateManager.translatef(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotatef(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(60.0F, 0.0F, 1.0F, 0.0F);
   }

   public void renderInFirstPerson(float tickDelta) {
      float var2 = 1.0F - (this.lastHoldAnimationTicks + (this.swapAnimationTicks - this.lastHoldAnimationTicks) * tickDelta);
      LocalClientPlayerEntity var3 = this.client.player;
      float var4 = var3.getHandSwingProcess(tickDelta);
      float var5 = var3.prevPitch + (var3.pitch - var3.prevPitch) * tickDelta;
      float var6 = var3.prevYaw + (var3.yaw - var3.prevYaw) * tickDelta;
      this.rotate(var5, var6);
      this.m_11iigoupi(var3);
      this.m_99vcluupu(var3, tickDelta);
      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      if (this.item != null) {
         if (this.item.getItem() == Items.FILLED_MAP) {
            this.renderMap(var3, var5, var2, var4);
         } else if (var3.getItemUseTimer() > 0) {
            UseAction var7 = this.item.getUseAction();
            switch(var7) {
               case NONE:
                  this.appyFirstPersonTransform(var2, 0.0F);
                  break;
               case EAT:
               case DRINK:
                  this.applyConsuming(var3, tickDelta);
                  this.appyFirstPersonTransform(var2, 0.0F);
                  break;
               case BLOCK:
                  this.appyFirstPersonTransform(var2, 0.0F);
                  this.applySwordBlocking();
                  break;
               case BOW:
                  this.appyFirstPersonTransform(var2, 0.0F);
                  this.applyBowNocking(tickDelta, var3);
            }
         } else {
            this.applyHandSwing(var4);
            this.appyFirstPersonTransform(var2, var4);
         }

         this.render(var3, this.item, ModelTransformations.Type.FIRST_PERSON);
      } else if (!var3.isInvisible()) {
         this.renderHand(var3, var2, var4);
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
   }

   public void renderScreenSpaceEffects(float tickDelta) {
      GlStateManager.disableAlphaTest();
      if (this.client.player.isInWall()) {
         BlockState var2 = this.client.world.getBlockState(new BlockPos(this.client.player));
         LocalClientPlayerEntity var3 = this.client.player;

         for(int var4 = 0; var4 < 8; ++var4) {
            double var5 = var3.x + (double)(((float)((var4 >> 0) % 2) - 0.5F) * var3.width * 0.8F);
            double var7 = var3.y + (double)(((float)((var4 >> 1) % 2) - 0.5F) * 0.1F);
            double var9 = var3.z + (double)(((float)((var4 >> 2) % 2) - 0.5F) * var3.width * 0.8F);
            BlockPos var11 = new BlockPos(var5, var7 + (double)var3.getEyeHeight(), var9);
            BlockState var12 = this.client.world.getBlockState(var11);
            if (var12.getBlock().isViewBlocking()) {
               var2 = var12;
            }
         }

         if (var2.getBlock().getRenderType() != -1) {
            this.renderSuffocatingEffect(tickDelta, this.client.getBlockRenderDispatcher().getModelShaper().getParticleIcon(var2));
         }
      }

      if (!this.client.player.isSpectator()) {
         if (this.client.player.isSubmergedIn(Material.WATER)) {
            this.renderInWaterEffect(tickDelta);
         }

         if (this.client.player.isOnFire()) {
            this.renderOnFireEffect(tickDelta);
         }
      }

      GlStateManager.enableAlphaTest();
   }

   private void renderSuffocatingEffect(float tickDelta, TextureAtlasSprite suffocatingBlockTexture) {
      this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      Tessellator var3 = Tessellator.getInstance();
      BufferBuilder var4 = var3.getBufferBuilder();
      float var5 = 0.1F;
      GlStateManager.color4f(var5, var5, var5, 0.5F);
      GlStateManager.pushMatrix();
      float var6 = -1.0F;
      float var7 = 1.0F;
      float var8 = -1.0F;
      float var9 = 1.0F;
      float var10 = -0.5F;
      float var11 = suffocatingBlockTexture.getUMin();
      float var12 = suffocatingBlockTexture.getUMax();
      float var13 = suffocatingBlockTexture.getVMin();
      float var14 = suffocatingBlockTexture.getVMax();
      var4.start();
      var4.vertex((double)var6, (double)var8, (double)var10, (double)var12, (double)var14);
      var4.vertex((double)var7, (double)var8, (double)var10, (double)var11, (double)var14);
      var4.vertex((double)var7, (double)var9, (double)var10, (double)var11, (double)var13);
      var4.vertex((double)var6, (double)var9, (double)var10, (double)var12, (double)var13);
      var3.end();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderInWaterEffect(float tickDelta) {
      this.client.getTextureManager().bind(UNDERWATER_TEXTURE);
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      float var4 = this.client.player.getBrightness(tickDelta);
      GlStateManager.color4f(var4, var4, var4, 0.5F);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      float var5 = 4.0F;
      float var6 = -1.0F;
      float var7 = 1.0F;
      float var8 = -1.0F;
      float var9 = 1.0F;
      float var10 = -0.5F;
      float var11 = -this.client.player.yaw / 64.0F;
      float var12 = this.client.player.pitch / 64.0F;
      var3.start();
      var3.vertex((double)var6, (double)var8, (double)var10, (double)(var5 + var11), (double)(var5 + var12));
      var3.vertex((double)var7, (double)var8, (double)var10, (double)(0.0F + var11), (double)(var5 + var12));
      var3.vertex((double)var7, (double)var9, (double)var10, (double)(0.0F + var11), (double)(0.0F + var12));
      var3.vertex((double)var6, (double)var9, (double)var10, (double)(var5 + var11), (double)(0.0F + var12));
      var2.end();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
   }

   private void renderOnFireEffect(float tickDelta) {
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      float var4 = 1.0F;

      for(int var5 = 0; var5 < 2; ++var5) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite var6 = this.client.getSpriteAtlasTexture().getSprite("minecraft:blocks/fire_layer_1");
         this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         float var7 = var6.getUMin();
         float var8 = var6.getUMax();
         float var9 = var6.getVMin();
         float var10 = var6.getVMax();
         float var11 = (0.0F - var4) / 2.0F;
         float var12 = var11 + var4;
         float var13 = 0.0F - var4 / 2.0F;
         float var14 = var13 + var4;
         float var15 = -0.5F;
         GlStateManager.translatef((float)(-(var5 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotatef((float)(var5 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         var3.start();
         var3.vertex((double)var11, (double)var13, (double)var15, (double)var8, (double)var10);
         var3.vertex((double)var12, (double)var13, (double)var15, (double)var7, (double)var10);
         var3.vertex((double)var12, (double)var14, (double)var15, (double)var7, (double)var9);
         var3.vertex((double)var11, (double)var14, (double)var15, (double)var8, (double)var9);
         var2.end();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void updateHeldItem() {
      this.lastHoldAnimationTicks = this.swapAnimationTicks;
      LocalClientPlayerEntity var1 = this.client.player;
      ItemStack var2 = var1.inventory.getMainHandStack();
      boolean var3 = false;
      if (this.item != null && var2 != null) {
         if (!this.item.isEqualForHoldAnimation(var2)) {
            var3 = true;
         }
      } else if (this.item == null && var2 == null) {
         var3 = false;
      } else {
         var3 = true;
      }

      float var4 = 0.4F;
      float var5 = var3 ? 0.0F : 1.0F;
      float var6 = MathHelper.clamp(var5 - this.swapAnimationTicks, -var4, var4);
      this.swapAnimationTicks += var6;
      if (this.swapAnimationTicks < 0.1F) {
         this.item = var2;
         this.selectedSlot = var1.inventory.selectedSlot;
      }
   }

   public void resetSwapAnimation() {
      this.swapAnimationTicks = 0.0F;
   }

   public void resetSwapAnimation2() {
      this.swapAnimationTicks = 0.0F;
   }
}
