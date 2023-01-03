package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.client.texture.CompassSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ItemFrameRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
   private final MinecraftClient client = MinecraftClient.getInstance();
   private final ModelIdentifier f_26kpxkeil = new ModelIdentifier("item_frame", "normal");
   private final ModelIdentifier f_00axjqdwt = new ModelIdentifier("item_frame", "map");
   private ItemRenderer f_24llfjomg;

   public ItemFrameRenderer(EntityRenderDispatcher c_28wsgstbh, ItemRenderer c_60kdcdvri) {
      super(c_28wsgstbh);
      this.f_24llfjomg = c_60kdcdvri;
   }

   public void render(ItemFrameEntity c_67odgdwyd, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      BlockPos var10 = c_67odgdwyd.getBlockPos();
      double var11 = (double)var10.getX() - c_67odgdwyd.x + d;
      double var13 = (double)var10.getY() - c_67odgdwyd.y + e;
      double var15 = (double)var10.getZ() - c_67odgdwyd.z + f;
      GlStateManager.translated(var11 + 0.5, var13 + 0.5, var15 + 0.5);
      GlStateManager.rotatef(180.0F - c_67odgdwyd.yaw, 0.0F, 1.0F, 0.0F);
      this.dispatcher.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      BlockRenderDispatcher var17 = this.client.getBlockRenderDispatcher();
      ModelManager var18 = var17.getModelShaper().getManager();
      BakedModel var19;
      if (c_67odgdwyd.getItemStackInItemFrame() != null && c_67odgdwyd.getItemStackInItemFrame().getItem() == Items.FILLED_MAP) {
         var19 = var18.getModel(this.f_00axjqdwt);
      } else {
         var19 = var18.getModel(this.f_26kpxkeil);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      var17.getModelRenderer().render(var19, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
      this.renderItem(c_67odgdwyd);
      GlStateManager.popMatrix();
      this.renderNameTag(
         c_67odgdwyd, d + (double)((float)c_67odgdwyd.getFacing.getOffsetX() * 0.3F), e - 0.25, f + (double)((float)c_67odgdwyd.getFacing.getOffsetZ() * 0.3F)
      );
   }

   protected Identifier getTexture(ItemFrameEntity c_67odgdwyd) {
      return null;
   }

   private void renderItem(ItemFrameEntity itemFrame) {
      ItemStack var2 = itemFrame.getItemStackInItemFrame();
      if (var2 != null) {
         ItemEntity var3 = new ItemEntity(itemFrame.world, 0.0, 0.0, 0.0, var2);
         Item var4 = var3.getItemStack().getItem();
         var3.getItemStack().size = 1;
         var3.hoverHeight = 0.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         int var5 = itemFrame.rotation();
         if (var4 == Items.FILLED_MAP) {
            var5 = var5 % 4 * 2;
         }

         GlStateManager.rotatef((float)var5 * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (var4 == Items.FILLED_MAP) {
            this.dispatcher.textureManager.bind(TEXTURE);
            Tessellator var6 = Tessellator.getInstance();
            BufferBuilder var7 = var6.getBufferBuilder();
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float var8 = 0.0078125F;
            GlStateManager.scalef(var8, var8, var8);
            GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
            SavedMapData var9 = Items.FILLED_MAP.getSavedMapData(var3.getItemStack(), itemFrame.world);
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);
            if (var9 != null) {
               this.client.gameRenderer.getMapRenderer().draw(var9, true);
            }
         } else {
            TextureAtlasSprite var12 = null;
            if (var4 == Items.COMPASS) {
               var12 = this.client.getSpriteAtlasTexture().getSprite(CompassSprite.f_11nxdzvjg);
               this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
               if (var12 instanceof CompassSprite) {
                  CompassSprite var13 = (CompassSprite)var12;
                  double var14 = var13.f_19uughxjv;
                  double var10 = var13.f_29hcqvgoc;
                  var13.f_19uughxjv = 0.0;
                  var13.f_29hcqvgoc = 0.0;
                  var13.m_12wsbzays(
                     itemFrame.world,
                     itemFrame.x,
                     itemFrame.z,
                     (double)MathHelper.wrapDegrees((float)(180 + itemFrame.getFacing.getIdHorizontal() * 90)),
                     false,
                     true
                  );
                  var13.f_19uughxjv = var14;
                  var13.f_29hcqvgoc = var10;
               } else {
                  var12 = null;
               }
            }

            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            if (!this.f_24llfjomg.isGui3d(var3.getItemStack())) {
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            this.f_24llfjomg.renderHeldItem(var3.getItemStack());
            if (var12 != null && var12.getFrameSize() > 0) {
               var12.update();
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   protected void renderNameTag(ItemFrameEntity c_67odgdwyd, double d, double e, double f) {
      if (MinecraftClient.isHudDisabled()
         && c_67odgdwyd.getItemStackInItemFrame() != null
         && c_67odgdwyd.getItemStackInItemFrame().hasCustomHoverName()
         && this.dispatcher.targetEntity == c_67odgdwyd) {
         float var8 = 1.6F;
         float var9 = 0.016666668F * var8;
         double var10 = c_67odgdwyd.getSquaredDistanceTo(this.dispatcher.camera);
         float var12 = c_67odgdwyd.isSneaking() ? 32.0F : 64.0F;
         if (var10 < (double)(var12 * var12)) {
            String var13 = c_67odgdwyd.getItemStackInItemFrame().getHoverName();
            if (c_67odgdwyd.isSneaking()) {
               TextRenderer var14 = this.getFontRenderer();
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)d + 0.0F, (float)e + c_67odgdwyd.height + 0.5F, (float)f);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef(-this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef(this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
               GlStateManager.scalef(-var9, -var9, var9);
               GlStateManager.disableLighting();
               GlStateManager.translatef(0.0F, 0.25F / var9, 0.0F);
               GlStateManager.depthMask(false);
               GlStateManager.disableBlend();
               GlStateManager.blendFunc(770, 771);
               Tessellator var15 = Tessellator.getInstance();
               BufferBuilder var16 = var15.getBufferBuilder();
               GlStateManager.disableTexture();
               var16.start();
               int var17 = var14.getStringWidth(var13) / 2;
               var16.color(0.0F, 0.0F, 0.0F, 0.25F);
               var16.vertex((double)(-var17 - 1), -1.0, 0.0);
               var16.vertex((double)(-var17 - 1), 8.0, 0.0);
               var16.vertex((double)(var17 + 1), 8.0, 0.0);
               var16.vertex((double)(var17 + 1), -1.0, 0.0);
               var15.end();
               GlStateManager.enableTexture();
               GlStateManager.depthMask(true);
               var14.drawWithoutShadow(var13, -var14.getStringWidth(var13) / 2, 0, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.enableBlend();
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderNameTag(c_67odgdwyd, var13, d, e, f, 64);
            }
         }
      }
   }
}
