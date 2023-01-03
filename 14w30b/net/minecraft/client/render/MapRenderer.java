package net.minecraft.client.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Map;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.minecraft.world.map.MapDecoration;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MapRenderer {
   private static final Identifier MAP_ICONS_TEXTURE = new Identifier("textures/map/map_icons.png");
   private final TextureManager textureManager;
   private final Map mapTextures = Maps.newHashMap();

   public MapRenderer(TextureManager textureManager) {
      this.textureManager = textureManager;
   }

   public void updateTexture(SavedMapData mapData) {
      this.getMapTexture(mapData).updateTexture();
   }

   public void draw(SavedMapData mapData, boolean inItemFrame) {
      this.getMapTexture(mapData).draw(inItemFrame);
   }

   private MapRenderer.Texture getMapTexture(SavedMapData mapData) {
      MapRenderer.Texture var2 = (MapRenderer.Texture)this.mapTextures.get(mapData.id);
      if (var2 == null) {
         var2 = new MapRenderer.Texture(mapData);
         this.mapTextures.put(mapData.id, var2);
      }

      return var2;
   }

   public void clearStateTextures() {
      for(MapRenderer.Texture var2 : this.mapTextures.values()) {
         this.textureManager.close(var2.currentTexture);
      }

      this.mapTextures.clear();
   }

   @Environment(EnvType.CLIENT)
   class Texture {
      private final SavedMapData mapData;
      private final NativeImageBackedTexture texture;
      private final Identifier currentTexture;
      private final int[] colors;

      private Texture(SavedMapData mapData) {
         this.mapData = mapData;
         this.texture = new NativeImageBackedTexture(128, 128);
         this.colors = this.texture.getRgbArray();
         this.currentTexture = MapRenderer.this.textureManager.register("map/" + mapData.id, this.texture);

         for(int var3 = 0; var3 < this.colors.length; ++var3) {
            this.colors[var3] = 0;
         }
      }

      private void updateTexture() {
         for(int var1 = 0; var1 < 16384; ++var1) {
            int var2 = this.mapData.colors[var1] & 255;
            if (var2 / 4 == 0) {
               this.colors[var1] = (var1 + var1 / 128 & 1) * 8 + 16 << 24;
            } else {
               this.colors[var1] = MaterialColor.COLORS[var2 / 4].getRenderColor(var2 & 3);
            }
         }

         this.texture.upload();
      }

      private void draw(boolean inItemFrame) {
         byte var2 = 0;
         byte var3 = 0;
         Tessellator var4 = Tessellator.getInstance();
         BufferBuilder var5 = var4.getBufferBuilder();
         float var6 = 0.0F;
         MapRenderer.this.textureManager.bind(this.currentTexture);
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(1, 771, 0, 1);
         GlStateManager.disableAlphaTest();
         var5.start();
         var5.vertex((double)((float)(var2 + 0) + var6), (double)((float)(var3 + 128) - var6), -0.01F, 0.0, 1.0);
         var5.vertex((double)((float)(var2 + 128) - var6), (double)((float)(var3 + 128) - var6), -0.01F, 1.0, 1.0);
         var5.vertex((double)((float)(var2 + 128) - var6), (double)((float)(var3 + 0) + var6), -0.01F, 1.0, 0.0);
         var5.vertex((double)((float)(var2 + 0) + var6), (double)((float)(var3 + 0) + var6), -0.01F, 0.0, 0.0);
         var4.end();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableBlend();
         MapRenderer.this.textureManager.bind(MapRenderer.MAP_ICONS_TEXTURE);
         int var7 = 0;

         for(MapDecoration var9 : this.mapData.decorations.values()) {
            if (!inItemFrame || var9.getType() == 1) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)var2 + (float)var9.getX() / 2.0F + 64.0F, (float)var3 + (float)var9.getY() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotatef((float)(var9.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scalef(4.0F, 4.0F, 3.0F);
               GlStateManager.translatef(-0.125F, 0.125F, 0.0F);
               byte var10 = var9.getType();
               float var11 = (float)(var10 % 4 + 0) / 4.0F;
               float var12 = (float)(var10 / 4 + 0) / 4.0F;
               float var13 = (float)(var10 % 4 + 1) / 4.0F;
               float var14 = (float)(var10 / 4 + 1) / 4.0F;
               var5.start();
               var5.vertex(-1.0, 1.0, (double)((float)var7 * 0.001F), (double)var11, (double)var12);
               var5.vertex(1.0, 1.0, (double)((float)var7 * 0.001F), (double)var13, (double)var12);
               var5.vertex(1.0, -1.0, (double)((float)var7 * 0.001F), (double)var13, (double)var14);
               var5.vertex(-1.0, -1.0, (double)((float)var7 * 0.001F), (double)var11, (double)var14);
               var4.end();
               GlStateManager.popMatrix();
               ++var7;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, -0.04F);
         GlStateManager.scalef(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }
}
