package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer {
   private static final Identifier SHADOW_TEXTURE = new Identifier("textures/misc/shadow.png");
   protected final EntityRenderDispatcher dispatcher;
   protected float shadowSize;
   protected float shadowDarkness = 1.0F;

   protected EntityRenderer(EntityRenderDispatcher dispatcher) {
      this.dispatcher = dispatcher;
   }

   public boolean shouldRender(Entity entity, Culler view, double cameraX, double cameraY, double cameraZ) {
      return entity.isWithinViewDistanceOf(cameraX, cameraY, cameraZ) && (entity.ignoreCameraFrustum || view.isVisible(entity.getBoundingBox()));
   }

   public void render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta) {
      this.renderNameTag(entity, dx, dy, dz);
   }

   protected void renderNameTag(Entity entity, double dx, double dy, double dz) {
      if (this.shouldRenderNameTag(entity)) {
         this.renderNameTag(entity, entity.getDisplayName().buildFormattedString(), dx, dy, dz, 64);
      }
   }

   protected boolean shouldRenderNameTag(Entity entity) {
      return entity.shouldShowNameTag() && entity.hasCustomName();
   }

   protected void renderNameTags(Entity entity, double dx, double dy, double dz, String name, float tickDelta, double squaredDistance) {
      this.renderNameTag(entity, name, dx, dy, dz, 64);
   }

   protected abstract Identifier getTexture(Entity entity);

   protected boolean bindTexture(Entity entity) {
      Identifier var2 = this.getTexture(entity);
      if (var2 == null) {
         return false;
      } else {
         this.bindTexture(var2);
         return true;
      }
   }

   public void bindTexture(Identifier texture) {
      this.dispatcher.textureManager.bind(texture);
   }

   private void renderOnFire(Entity entity, double dx, double dy, double dz, float tickDelta) {
      GlStateManager.disableLighting();
      SpriteAtlasTexture var9 = MinecraftClient.getInstance().getSpriteAtlasTexture();
      TextureAtlasSprite var10 = var9.getSprite("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite var11 = var9.getSprite("minecraft:blocks/fire_layer_1");
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)dx, (float)dy, (float)dz);
      float var12 = entity.width * 1.4F;
      GlStateManager.scalef(var12, var12, var12);
      Tessellator var13 = Tessellator.getInstance();
      BufferBuilder var14 = var13.getBufferBuilder();
      float var15 = 0.5F;
      float var16 = 0.0F;
      float var17 = entity.height / var12;
      float var18 = (float)(entity.y - entity.getBoundingBox().minY);
      GlStateManager.rotatef(-this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.3F + (float)((int)var17) * 0.02F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var19 = 0.0F;
      int var20 = 0;
      var14.start();

      while(var17 > 0.0F) {
         TextureAtlasSprite var21 = var20 % 2 == 0 ? var10 : var11;
         this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         float var22 = var21.getUMin();
         float var23 = var21.getVMin();
         float var24 = var21.getUMax();
         float var25 = var21.getVMax();
         if (var20 / 2 % 2 == 0) {
            float var26 = var24;
            var24 = var22;
            var22 = var26;
         }

         var14.vertex((double)(var15 - var16), (double)(0.0F - var18), (double)var19, (double)var24, (double)var25);
         var14.vertex((double)(-var15 - var16), (double)(0.0F - var18), (double)var19, (double)var22, (double)var25);
         var14.vertex((double)(-var15 - var16), (double)(1.4F - var18), (double)var19, (double)var22, (double)var23);
         var14.vertex((double)(var15 - var16), (double)(1.4F - var18), (double)var19, (double)var24, (double)var23);
         var17 -= 0.45F;
         var18 -= 0.45F;
         var15 *= 0.9F;
         var19 += 0.03F;
         ++var20;
      }

      var13.end();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   private void renderShadow(Entity entity, double dx, double dy, double dz, float g, float tickDelta) {
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(770, 771);
      this.dispatcher.textureManager.bind(SHADOW_TEXTURE);
      World var10 = this.getWorld();
      GlStateManager.depthMask(false);
      float var11 = this.shadowSize;
      if (entity instanceof MobEntity) {
         MobEntity var12 = (MobEntity)entity;
         var11 *= var12.getShadowScale();
         if (var12.isBaby()) {
            var11 *= 0.5F;
         }
      }

      double var35 = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
      double var14 = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
      double var16 = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
      int var18 = MathHelper.floor(var35 - (double)var11);
      int var19 = MathHelper.floor(var35 + (double)var11);
      int var20 = MathHelper.floor(var14 - (double)var11);
      int var21 = MathHelper.floor(var14);
      int var22 = MathHelper.floor(var16 - (double)var11);
      int var23 = MathHelper.floor(var16 + (double)var11);
      double var24 = dx - var35;
      double var26 = dy - var14;
      double var28 = dz - var16;
      Tessellator var30 = Tessellator.getInstance();
      BufferBuilder var31 = var30.getBufferBuilder();
      var31.start();

      for(BlockPos var33 : BlockPos.iterateRegion(new BlockPos(var18, var20, var22), new BlockPos(var19, var21, var23))) {
         Block var34 = var10.getBlockState(var33.down()).getBlock();
         if (var34.getRenderType() != -1 && var10.getRawBrightness(var33) > 3) {
            this.renderShadowOnBlock(var34, dx, dy, dz, var33, g, var11, var24, var26, var28);
         }
      }

      var30.end();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(true);
   }

   private World getWorld() {
      return this.dispatcher.world;
   }

   private void renderShadowOnBlock(Block block, double dx, double dy, double dz, BlockPos pos, float dyBlock, float dzBlock, double i, double j, double k) {
      if (block.isFullCube()) {
         Tessellator var17 = Tessellator.getInstance();
         BufferBuilder var18 = var17.getBufferBuilder();
         double var19 = ((double)dyBlock - (dy - ((double)pos.getY() + j)) / 2.0) * 0.5 * (double)this.getWorld().getBrightness(pos);
         if (!(var19 < 0.0)) {
            if (var19 > 1.0) {
               var19 = 1.0;
            }

            var18.color(1.0F, 1.0F, 1.0F, (float)var19);
            double var21 = (double)pos.getX() + block.getMinX() + i;
            double var23 = (double)pos.getX() + block.getMaxX() + i;
            double var25 = (double)pos.getY() + block.getMinY() + j + 0.015625;
            double var27 = (double)pos.getZ() + block.getMinZ() + k;
            double var29 = (double)pos.getZ() + block.getMaxZ() + k;
            float var31 = (float)((dx - var21) / 2.0 / (double)dzBlock + 0.5);
            float var32 = (float)((dx - var23) / 2.0 / (double)dzBlock + 0.5);
            float var33 = (float)((dz - var27) / 2.0 / (double)dzBlock + 0.5);
            float var34 = (float)((dz - var29) / 2.0 / (double)dzBlock + 0.5);
            var18.vertex(var21, var25, var27, (double)var31, (double)var33);
            var18.vertex(var21, var25, var29, (double)var31, (double)var34);
            var18.vertex(var23, var25, var29, (double)var32, (double)var34);
            var18.vertex(var23, var25, var27, (double)var32, (double)var33);
         }
      }
   }

   public static void renderAreaEffectCloud(Box box, double dx, double dy, double dz) {
      GlStateManager.disableTexture();
      Tessellator var7 = Tessellator.getInstance();
      BufferBuilder var8 = var7.getBufferBuilder();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      var8.start();
      var8.offset(dx, dy, dz);
      var8.normal(0.0F, 0.0F, -1.0F);
      var8.vertex(box.minX, box.maxY, box.minZ);
      var8.vertex(box.maxX, box.maxY, box.minZ);
      var8.vertex(box.maxX, box.minY, box.minZ);
      var8.vertex(box.minX, box.minY, box.minZ);
      var8.normal(0.0F, 0.0F, 1.0F);
      var8.vertex(box.minX, box.minY, box.maxZ);
      var8.vertex(box.maxX, box.minY, box.maxZ);
      var8.vertex(box.maxX, box.maxY, box.maxZ);
      var8.vertex(box.minX, box.maxY, box.maxZ);
      var8.normal(0.0F, -1.0F, 0.0F);
      var8.vertex(box.minX, box.minY, box.minZ);
      var8.vertex(box.maxX, box.minY, box.minZ);
      var8.vertex(box.maxX, box.minY, box.maxZ);
      var8.vertex(box.minX, box.minY, box.maxZ);
      var8.normal(0.0F, 1.0F, 0.0F);
      var8.vertex(box.minX, box.maxY, box.maxZ);
      var8.vertex(box.maxX, box.maxY, box.maxZ);
      var8.vertex(box.maxX, box.maxY, box.minZ);
      var8.vertex(box.minX, box.maxY, box.minZ);
      var8.normal(-1.0F, 0.0F, 0.0F);
      var8.vertex(box.minX, box.minY, box.maxZ);
      var8.vertex(box.minX, box.maxY, box.maxZ);
      var8.vertex(box.minX, box.maxY, box.minZ);
      var8.vertex(box.minX, box.minY, box.minZ);
      var8.normal(1.0F, 0.0F, 0.0F);
      var8.vertex(box.maxX, box.minY, box.minZ);
      var8.vertex(box.maxX, box.maxY, box.minZ);
      var8.vertex(box.maxX, box.maxY, box.maxZ);
      var8.vertex(box.maxX, box.minY, box.maxZ);
      var8.offset(0.0, 0.0, 0.0);
      var7.end();
      GlStateManager.enableTexture();
   }

   public void postRender(Entity entity, double dx, double dy, double dz, float g, float tickDelta) {
      if (this.dispatcher.options.fancyGraphics && this.shadowSize > 0.0F && !entity.isInvisible() && this.dispatcher.m_42egfdxeq()) {
         double var10 = this.dispatcher.getSquaredDistanceToCamera(entity.x, entity.y, entity.z);
         float var12 = (float)((1.0 - var10 / 256.0) * (double)this.shadowDarkness);
         if (var12 > 0.0F) {
            this.renderShadow(entity, dx, dy, dz, var12, tickDelta);
         }
      }

      if (entity.shouldRenderOnFire()) {
         this.renderOnFire(entity, dx, dy, dz, tickDelta);
      }
   }

   public TextRenderer getFontRenderer() {
      return this.dispatcher.getTextRenderer();
   }

   protected void renderNameTag(Entity entity, String name, double dx, double dy, double dz, int distance) {
      double var10 = entity.getSquaredDistanceTo(this.dispatcher.camera);
      if (!(var10 > (double)(distance * distance))) {
         TextRenderer var12 = this.getFontRenderer();
         float var13 = 1.6F;
         float var14 = 0.016666668F * var13;
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)dx + 0.0F, (float)dy + entity.height + 0.5F, (float)dz);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.scalef(-var14, -var14, var14);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.enableDepth();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         Tessellator var15 = Tessellator.getInstance();
         BufferBuilder var16 = var15.getBufferBuilder();
         byte var17 = 0;
         if (name.equals("deadmau5")) {
            var17 = -10;
         }

         GlStateManager.disableTexture();
         var16.start();
         int var18 = var12.getStringWidth(name) / 2;
         var16.color(0.0F, 0.0F, 0.0F, 0.25F);
         var16.vertex((double)(-var18 - 1), (double)(-1 + var17), 0.0);
         var16.vertex((double)(-var18 - 1), (double)(8 + var17), 0.0);
         var16.vertex((double)(var18 + 1), (double)(8 + var17), 0.0);
         var16.vertex((double)(var18 + 1), (double)(-1 + var17), 0.0);
         var15.end();
         GlStateManager.enableTexture();
         var12.drawWithoutShadow(name, -var12.getStringWidth(name) / 2, var17, 553648127);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(true);
         var12.drawWithoutShadow(name, -var12.getStringWidth(name) / 2, var17, -1);
         GlStateManager.enableLighting();
         GlStateManager.enableBlend();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public EntityRenderDispatcher getDispatcher() {
      return this.dispatcher;
   }
}
