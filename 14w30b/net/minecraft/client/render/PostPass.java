package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.manager.IResourceManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PostPass {
   private final Effect effect;
   public final RenderTarget inTarget;
   public final RenderTarget outTarget;
   private final List auxAssets = Lists.newArrayList();
   private final List auxNames = Lists.newArrayList();
   private final List auxWidths = Lists.newArrayList();
   private final List auxHeights = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;

   public PostPass(IResourceManager resourceManager, String name, RenderTarget inTarget, RenderTarget outTarget) {
      this.effect = new Effect(resourceManager, name);
      this.inTarget = inTarget;
      this.outTarget = outTarget;
   }

   public void close() {
      this.effect.close();
   }

   public void addAuxAsset(String name, Object asset, int width, int height) {
      this.auxNames.add(this.auxNames.size(), name);
      this.auxAssets.add(this.auxAssets.size(), asset);
      this.auxWidths.add(this.auxWidths.size(), width);
      this.auxHeights.add(this.auxHeights.size(), height);
   }

   private void prepareState() {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.enableDepth();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture();
      GlStateManager.bindTexture(0);
   }

   public void setOrthoMatrix(Matrix4f matrix) {
      this.shaderOrthoMatrix = matrix;
   }

   public void process(float tickDelta) {
      this.prepareState();
      this.inTarget.unbindWrite();
      float var2 = (float)this.outTarget.width;
      float var3 = (float)this.outTarget.height;
      GlStateManager.viewport(0, 0, (int)var2, (int)var3);
      this.effect.setSampler("DiffuseSampler", this.inTarget);

      for(int var4 = 0; var4 < this.auxAssets.size(); ++var4) {
         this.effect.setSampler((String)this.auxNames.get(var4), this.auxAssets.get(var4));
         this.effect
            .safeGetUniform("AuxSize" + var4)
            .set((float)((Integer)this.auxWidths.get(var4)).intValue(), (float)((Integer)this.auxHeights.get(var4)).intValue());
      }

      this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
      this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
      this.effect.safeGetUniform("OutSize").set(var2, var3);
      this.effect.safeGetUniform("Time").set(tickDelta);
      MinecraftClient var9 = MinecraftClient.getInstance();
      this.effect.safeGetUniform("ScreenSize").set((float)var9.width, (float)var9.height);
      this.effect.apply();
      this.outTarget.clear();
      this.outTarget.bindWrite(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tessellator var5 = Tessellator.getInstance();
      BufferBuilder var6 = var5.getBufferBuilder();
      var6.start();
      var6.color(-1);
      var6.vertex(0.0, (double)var3, 500.0);
      var6.vertex((double)var2, (double)var3, 500.0);
      var6.vertex((double)var2, 0.0, 500.0);
      var6.vertex(0.0, 0.0, 500.0);
      var5.end();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.effect.clear();
      this.outTarget.unbindWrite();
      this.inTarget.unbindRead();

      for(Object var8 : this.auxAssets) {
         if (var8 instanceof RenderTarget) {
            ((RenderTarget)var8).unbindRead();
         }
      }
   }

   public Effect getEffect() {
      return this.effect;
   }
}
