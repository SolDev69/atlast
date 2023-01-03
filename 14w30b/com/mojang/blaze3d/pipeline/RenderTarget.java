package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.nio.ByteBuffer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class RenderTarget {
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   public boolean useDepth;
   public int frameBufferId;
   public int colorTextureId;
   public int depthBufferId;
   public float[] clearChannels;
   public int filterMode;

   public RenderTarget(int width, int height, boolean useDepth) {
      this.useDepth = useDepth;
      this.frameBufferId = -1;
      this.colorTextureId = -1;
      this.depthBufferId = -1;
      this.clearChannels = new float[4];
      this.clearChannels[0] = 1.0F;
      this.clearChannels[1] = 1.0F;
      this.clearChannels[2] = 1.0F;
      this.clearChannels[3] = 0.0F;
      this.resize(width, height);
   }

   public void resize(int width, int height) {
      if (!GLX.useFbo()) {
         this.viewWidth = width;
         this.viewHeight = height;
      } else {
         GlStateManager.disableDepth();
         if (this.frameBufferId >= 0) {
            this.destroyBuffers();
         }

         this.createBuffers(width, height);
         this.checkStatus();
         GLX.bindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
      }
   }

   public void destroyBuffers() {
      if (GLX.useFbo()) {
         this.unbindRead();
         this.unbindWrite();
         if (this.depthBufferId > -1) {
            GLX.deleteRenderbuffers(this.depthBufferId);
            this.depthBufferId = -1;
         }

         if (this.colorTextureId > -1) {
            TextureUtil.deleteTexture(this.colorTextureId);
            this.colorTextureId = -1;
         }

         if (this.frameBufferId > -1) {
            GLX.bindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
            GLX.deleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
         }
      }
   }

   public void createBuffers(int width, int height) {
      this.viewWidth = width;
      this.viewHeight = height;
      this.width = width;
      this.height = height;
      if (!GLX.useFbo()) {
         this.clear();
      } else {
         this.frameBufferId = GLX.genFramebuffers();
         this.colorTextureId = TextureUtil.genTextures();
         if (this.useDepth) {
            this.depthBufferId = GLX.genRenderbuffers();
         }

         this.setFilterMode(9728);
         GlStateManager.bindTexture(this.colorTextureId);
         GL11.glTexImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (ByteBuffer)null);
         GLX.bindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
         GLX.framebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
         if (this.useDepth) {
            GLX.bindRenderbuffer(GLX.GL_RENDERBUFFER, this.depthBufferId);
            GLX.renderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.width, this.height);
            GLX.framebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.depthBufferId);
         }

         this.clear();
         this.unbindRead();
      }
   }

   public void setFilterMode(int filterMode) {
      if (GLX.useFbo()) {
         this.filterMode = filterMode;
         GlStateManager.bindTexture(this.colorTextureId);
         GL11.glTexParameterf(3553, 10241, (float)filterMode);
         GL11.glTexParameterf(3553, 10240, (float)filterMode);
         GL11.glTexParameterf(3553, 10242, 10496.0F);
         GL11.glTexParameterf(3553, 10243, 10496.0F);
         GlStateManager.bindTexture(0);
      }
   }

   public void checkStatus() {
      int var1 = GLX.checkFramebufferStatus(GLX.GL_FRAMEBUFFER);
      if (var1 != GLX.GL_FRAMEBUFFER_COMPLETE) {
         if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + var1);
         }
      }
   }

   public void bindRead() {
      if (GLX.useFbo()) {
         GlStateManager.bindTexture(this.colorTextureId);
      }
   }

   public void unbindRead() {
      if (GLX.useFbo()) {
         GlStateManager.bindTexture(0);
      }
   }

   public void bindWrite(boolean viewport) {
      if (GLX.useFbo()) {
         GLX.bindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
         if (viewport) {
            GlStateManager.viewport(0, 0, this.viewWidth, this.viewHeight);
         }
      }
   }

   public void unbindWrite() {
      if (GLX.useFbo()) {
         GLX.bindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
      }
   }

   public void setClearColor(float r, float g, float b, float a) {
      this.clearChannels[0] = r;
      this.clearChannels[1] = g;
      this.clearChannels[2] = b;
      this.clearChannels[3] = a;
   }

   public void draw(int width, int height) {
      this.draw(width, height, true);
   }

   public void draw(int width, int height, boolean glBlend) {
      if (GLX.useFbo()) {
         GlStateManager.colorMask(true, true, true, false);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, (double)width, (double)height, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.viewport(0, 0, width, height);
         GlStateManager.enableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableAlphaTest();
         if (glBlend) {
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindRead();
         float var4 = (float)width;
         float var5 = (float)height;
         float var6 = (float)this.viewWidth / (float)this.width;
         float var7 = (float)this.viewHeight / (float)this.height;
         Tessellator var8 = Tessellator.getInstance();
         BufferBuilder var9 = var8.getBufferBuilder();
         var9.start();
         var9.color(-1);
         var9.vertex(0.0, (double)var5, 0.0, 0.0, 0.0);
         var9.vertex((double)var4, (double)var5, 0.0, (double)var6, 0.0);
         var9.vertex((double)var4, 0.0, 0.0, (double)var6, (double)var7);
         var9.vertex(0.0, 0.0, 0.0, 0.0, (double)var7);
         var8.end();
         this.unbindRead();
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
      }
   }

   public void clear() {
      this.bindWrite(true);
      GlStateManager.clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
      int var1 = 16384;
      if (this.useDepth) {
         GlStateManager.clearDepth(1.0);
         var1 |= 256;
      }

      GlStateManager.clear(var1);
      this.unbindWrite();
   }
}
