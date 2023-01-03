package net.minecraft.client.render.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.C_46wfxponx;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.block.entity.BannerModel;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BannerRenderer extends BlockEntityRenderer {
   private static final Map TEXTURE_CACHE = Maps.newHashMap();
   private static final Identifier BASE_TEXTURE = new Identifier("textures/entity/banner_base.png");
   private BannerModel model = new BannerModel();

   public void render(BannerBlockEntity c_15jjsjmaj, double d, double e, double f, float g, int i) {
      boolean var10 = c_15jjsjmaj.getWorld() != null;
      boolean var11 = !var10 || c_15jjsjmaj.getCachedBlock() == Blocks.STANDING_BANNER;
      int var12 = var10 ? c_15jjsjmaj.getCachedMetadata() : 0;
      long var13 = var10 ? c_15jjsjmaj.getWorld().getTime() : 0L;
      GlStateManager.pushMatrix();
      float var15 = 0.6666667F;
      if (var11) {
         GlStateManager.translatef((float)d + 0.5F, (float)e + 0.75F * var15, (float)f + 0.5F);
         float var16 = (float)(var12 * 360) / 16.0F;
         GlStateManager.rotatef(-var16, 0.0F, 1.0F, 0.0F);
         this.model.pole.visible = true;
      } else {
         float var17 = 0.0F;
         if (var12 == 2) {
            var17 = 180.0F;
         }

         if (var12 == 4) {
            var17 = 90.0F;
         }

         if (var12 == 5) {
            var17 = -90.0F;
         }

         GlStateManager.translatef((float)d + 0.5F, (float)e - 0.25F * var15, (float)f + 0.5F);
         GlStateManager.rotatef(-var17, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
         this.model.pole.visible = false;
      }

      BlockPos var19 = c_15jjsjmaj.getPos();
      float var20 = (float)(var19.getX() * 7 + var19.getY() * 9 + var19.getZ() * 13) + (float)var13 + g;
      this.model.flag.rotationX = (-0.0125F + 0.01F * MathHelper.cos(var20 * (float) Math.PI * 0.02F)) * (float) Math.PI;
      Identifier var18 = this.getTexture(c_15jjsjmaj);
      if (var18 != null) {
         this.bindTexture(var18);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(var15, -var15, -var15);
         this.model.render();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private Identifier getTexture(BannerBlockEntity banner) {
      String var2 = banner.getTexture();
      if (var2.isEmpty()) {
         return null;
      } else {
         BannerRenderer.CachedTexture var3 = (BannerRenderer.CachedTexture)TEXTURE_CACHE.get(var2);
         if (var3 == null) {
            if (TEXTURE_CACHE.size() >= 256) {
               long var4 = System.currentTimeMillis();
               Iterator var6 = TEXTURE_CACHE.keySet().iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  BannerRenderer.CachedTexture var8 = (BannerRenderer.CachedTexture)TEXTURE_CACHE.get(var7);
                  if (var4 - var8.cachedTime > 60000L) {
                     MinecraftClient.getInstance().getTextureManager().close(var8.texture);
                     var6.remove();
                  }
               }

               if (TEXTURE_CACHE.size() >= 256) {
                  return null;
               }
            }

            List var9 = banner.getPatterns();
            List var5 = banner.getColors();
            ArrayList var10 = Lists.newArrayList();

            for(BannerBlockEntity.Pattern var12 : var9) {
               var10.add("textures/entity/banner/" + var12.getName() + ".png");
            }

            var3 = new BannerRenderer.CachedTexture();
            var3.texture = new Identifier(var2);
            MinecraftClient.getInstance().getTextureManager().register(var3.texture, new C_46wfxponx(BASE_TEXTURE, var10, var5));
            TEXTURE_CACHE.put(var2, var3);
         }

         var3.cachedTime = System.currentTimeMillis();
         return var3.texture;
      }
   }

   @Environment(EnvType.CLIENT)
   static class CachedTexture {
      public long cachedTime;
      public Identifier texture;

      private CachedTexture() {
      }
   }
}
