package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.MinecartModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MinecartRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/minecart.png");
   protected Model model = new MinecartModel();

   public MinecartRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.5F;
   }

   public void render(MinecartEntity c_04pegpidi, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      this.bindTexture(c_04pegpidi);
      long var10 = (long)c_04pegpidi.getNetworkId() * 493286711L;
      var10 = var10 * var10 * 4392167121L + var10 * 98761L;
      float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translatef(var12, var13, var14);
      double var15 = c_04pegpidi.prevTickX + (c_04pegpidi.x - c_04pegpidi.prevTickX) * (double)h;
      double var17 = c_04pegpidi.prevTickY + (c_04pegpidi.y - c_04pegpidi.prevTickY) * (double)h;
      double var19 = c_04pegpidi.prevTickZ + (c_04pegpidi.z - c_04pegpidi.prevTickZ) * (double)h;
      double var21 = 0.3F;
      Vec3d var23 = c_04pegpidi.snapPositionToRail(var15, var17, var19);
      float var24 = c_04pegpidi.prevPitch + (c_04pegpidi.pitch - c_04pegpidi.prevPitch) * h;
      if (var23 != null) {
         Vec3d var25 = c_04pegpidi.snapPositionToRailWithOffset(var15, var17, var19, var21);
         Vec3d var26 = c_04pegpidi.snapPositionToRailWithOffset(var15, var17, var19, -var21);
         if (var25 == null) {
            var25 = var23;
         }

         if (var26 == null) {
            var26 = var23;
         }

         d += var23.x - var15;
         e += (var25.y + var26.y) / 2.0 - var17;
         f += var23.z - var19;
         Vec3d var27 = var26.add(-var25.x, -var25.y, -var25.z);
         if (var27.length() != 0.0) {
            var27 = var27.normalize();
            g = (float)(Math.atan2(var27.z, var27.x) * 180.0 / Math.PI);
            var24 = (float)(Math.atan(var27.y) * 73.0);
         }
      }

      GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
      GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var24, 0.0F, 0.0F, 1.0F);
      float var32 = (float)c_04pegpidi.getDamageWobbleTicks() - h;
      float var33 = c_04pegpidi.getDamageWobbleStrength() - h;
      if (var33 < 0.0F) {
         var33 = 0.0F;
      }

      if (var32 > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(var32) * var32 * var33 / 10.0F * (float)c_04pegpidi.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
      }

      int var35 = c_04pegpidi.getDisplayBlockOffset();
      Block var28 = c_04pegpidi.getDisplayBlock();
      int var29 = c_04pegpidi.getDisplayBlockMetadata();
      if (var28.getRenderType() != -1) {
         GlStateManager.pushMatrix();
         this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         float var30 = 0.75F;
         GlStateManager.scalef(var30, var30, var30);
         GlStateManager.translatef(-0.5F, (float)(var35 - 8) / 16.0F, 0.5F);
         this.renderSpawnerMinecart(c_04pegpidi, h, var28, var29);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(c_04pegpidi);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.model.render(c_04pegpidi, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.render(c_04pegpidi, d, e, f, g, h);
   }

   protected Identifier getTexture(MinecartEntity c_04pegpidi) {
      return TEXTURE;
   }

   protected void renderSpawnerMinecart(MinecartEntity spawnerMinecartEntity, float tickDelta, Block block, int metadata) {
      GlStateManager.pushMatrix();
      MinecraftClient.getInstance().getBlockRenderDispatcher().renderDynamic(block, metadata, spawnerMinecartEntity.getBrightness(tickDelta));
      GlStateManager.popMatrix();
   }
}
