package net.minecraft.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class StreamOverlay {
   private static final Identifier STREAM_INDICATOR_TEXTURE = new Identifier("textures/gui/stream_indicator.png");
   private final MinecraftClient client;
   private float f_06hpdbydk = 1.0F;
   private int f_24yajqydk = 1;

   public StreamOverlay(MinecraftClient client) {
      this.client = client;
   }

   public void render(int i, int j) {
      if (this.client.getTwitchStream().m_99rcqogzt()) {
         GlStateManager.disableBlend();
         int var3 = this.client.getTwitchStream().m_23mxopcac();
         if (var3 > 0) {
            String var4 = "" + var3;
            int var5 = this.client.textRenderer.getStringWidth(var4);
            boolean var6 = true;
            int var7 = i - var5 - 1;
            int var8 = j + 20 - 1;
            int var10 = j + 20 + this.client.textRenderer.fontHeight - 1;
            GlStateManager.disableTexture();
            Tessellator var11 = Tessellator.getInstance();
            BufferBuilder var12 = var11.getBufferBuilder();
            GlStateManager.color4f(0.0F, 0.0F, 0.0F, (0.65F + 0.35000002F * this.f_06hpdbydk) / 2.0F);
            var12.start();
            var12.vertex((double)var7, (double)var10, 0.0);
            var12.vertex((double)i, (double)var10, 0.0);
            var12.vertex((double)i, (double)var8, 0.0);
            var12.vertex((double)var7, (double)var8, 0.0);
            var11.end();
            GlStateManager.enableTexture();
            this.client.textRenderer.drawWithoutShadow(var4, i - var5, j + 20, 16777215);
         }

         this.m_93ceusibq(i, j, this.m_84huuzcjd(), 0);
         this.m_93ceusibq(i, j, this.m_15bzgnpzw(), 17);
      }
   }

   private void m_93ceusibq(int i, int j, int k, int l) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.65F + 0.35000002F * this.f_06hpdbydk);
      this.client.getTextureManager().bind(STREAM_INDICATOR_TEXTURE);
      float var5 = 150.0F;
      float var6 = 0.0F;
      float var7 = (float)k * 0.015625F;
      float var8 = 1.0F;
      float var9 = (float)(k + 16) * 0.015625F;
      Tessellator var10 = Tessellator.getInstance();
      BufferBuilder var11 = var10.getBufferBuilder();
      var11.start();
      var11.vertex((double)(i - 16 - l), (double)(j + 16), (double)var5, (double)var6, (double)var9);
      var11.vertex((double)(i - l), (double)(j + 16), (double)var5, (double)var8, (double)var9);
      var11.vertex((double)(i - l), (double)(j + 0), (double)var5, (double)var8, (double)var7);
      var11.vertex((double)(i - 16 - l), (double)(j + 0), (double)var5, (double)var6, (double)var7);
      var10.end();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private int m_84huuzcjd() {
      return this.client.getTwitchStream().m_59ybwvnxm() ? 16 : 0;
   }

   private int m_15bzgnpzw() {
      return this.client.getTwitchStream().m_81cvlsrmx() ? 48 : 32;
   }

   public void m_10hksspcd() {
      if (this.client.getTwitchStream().m_99rcqogzt()) {
         this.f_06hpdbydk += 0.025F * (float)this.f_24yajqydk;
         if (this.f_06hpdbydk < 0.0F) {
            this.f_24yajqydk *= -1;
            this.f_06hpdbydk = 0.0F;
         } else if (this.f_06hpdbydk > 1.0F) {
            this.f_24yajqydk *= -1;
            this.f_06hpdbydk = 1.0F;
         }
      } else {
         this.f_06hpdbydk = 1.0F;
         this.f_24yajqydk = 1;
      }
   }
}
