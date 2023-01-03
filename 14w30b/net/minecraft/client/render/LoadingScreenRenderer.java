package net.minecraft.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.util.ProgressListener;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LoadingScreenRenderer implements ProgressListener {
   private String task = "";
   private MinecraftClient client;
   private String title = "";
   private long systemTime = MinecraftClient.getTime();
   private boolean updated;
   private Window window;
   private RenderTarget target;

   public LoadingScreenRenderer(MinecraftClient client) {
      this.client = client;
      this.window = new Window(client, client.width, client.height);
      this.target = new RenderTarget(client.width, client.height, false);
      this.target.setFilterMode(9728);
   }

   @Override
   public void updateTitle(String title) {
      this.updated = false;
      this.setTitle(title);
   }

   @Override
   public void updateProgress(String title) {
      this.updated = true;
      this.setTitle(title);
   }

   private void setTitle(String title) {
      this.title = title;
      if (!this.client.running) {
         if (!this.updated) {
            throw new LoadingScreenRenderError();
         }
      } else {
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         if (GLX.useFbo()) {
            int var2 = this.window.getScale();
            GlStateManager.ortho(0.0, (double)(this.window.getWidth() * var2), (double)(this.window.getHeight() * var2), 0.0, 100.0, 300.0);
         } else {
            Window var3 = new Window(this.client, this.client.width, this.client.height);
            GlStateManager.ortho(0.0, var3.getScaledWidth(), var3.getScaledHeight(), 0.0, 100.0, 300.0);
         }

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -200.0F);
      }
   }

   @Override
   public void setTask(String task) {
      if (!this.client.running) {
         if (!this.updated) {
            throw new LoadingScreenRenderError();
         }
      } else {
         this.systemTime = 0L;
         this.task = task;
         this.progressStagePercentage(-1);
         this.systemTime = 0L;
      }
   }

   @Override
   public void progressStagePercentage(int percentage) {
      if (!this.client.running) {
         if (!this.updated) {
            throw new LoadingScreenRenderError();
         }
      } else {
         long var2 = MinecraftClient.getTime();
         if (var2 - this.systemTime >= 100L) {
            this.systemTime = var2;
            Window var4 = new Window(this.client, this.client.width, this.client.height);
            int var5 = var4.getScale();
            int var6 = var4.getWidth();
            int var7 = var4.getHeight();
            if (GLX.useFbo()) {
               this.target.clear();
            } else {
               GlStateManager.clear(256);
            }

            this.target.bindWrite(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, var4.getScaledWidth(), var4.getScaledHeight(), 0.0, 100.0, 300.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0F, 0.0F, -200.0F);
            if (!GLX.useFbo()) {
               GlStateManager.clear(16640);
            }

            Tessellator var8 = Tessellator.getInstance();
            BufferBuilder var9 = var8.getBufferBuilder();
            this.client.getTextureManager().bind(GuiElement.OPTIONS_BACKGROUND);
            float var10 = 32.0F;
            var9.start();
            var9.color(4210752);
            var9.vertex(0.0, (double)var7, 0.0, 0.0, (double)((float)var7 / var10));
            var9.vertex((double)var6, (double)var7, 0.0, (double)((float)var6 / var10), (double)((float)var7 / var10));
            var9.vertex((double)var6, 0.0, 0.0, (double)((float)var6 / var10), 0.0);
            var9.vertex(0.0, 0.0, 0.0, 0.0, 0.0);
            var8.end();
            if (percentage >= 0) {
               byte var11 = 100;
               byte var12 = 2;
               int var13 = var6 / 2 - var11 / 2;
               int var14 = var7 / 2 + 16;
               GlStateManager.disableTexture();
               var9.start();
               var9.color(8421504);
               var9.vertex((double)var13, (double)var14, 0.0);
               var9.vertex((double)var13, (double)(var14 + var12), 0.0);
               var9.vertex((double)(var13 + var11), (double)(var14 + var12), 0.0);
               var9.vertex((double)(var13 + var11), (double)var14, 0.0);
               var9.color(8454016);
               var9.vertex((double)var13, (double)var14, 0.0);
               var9.vertex((double)var13, (double)(var14 + var12), 0.0);
               var9.vertex((double)(var13 + percentage), (double)(var14 + var12), 0.0);
               var9.vertex((double)(var13 + percentage), (double)var14, 0.0);
               var8.end();
               GlStateManager.enableTexture();
            }

            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            this.client
               .textRenderer
               .drawWithShadow(this.title, (float)((var6 - this.client.textRenderer.getStringWidth(this.title)) / 2), (float)(var7 / 2 - 4 - 16), 16777215);
            this.client
               .textRenderer
               .drawWithShadow(this.task, (float)((var6 - this.client.textRenderer.getStringWidth(this.task)) / 2), (float)(var7 / 2 - 4 + 8), 16777215);
            this.target.unbindWrite();
            if (GLX.useFbo()) {
               this.target.draw(var6 * var5, var7 * var5);
            }

            this.client.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception var15) {
            }
         }
      }
   }

   @Override
   public void setDone() {
   }
}
