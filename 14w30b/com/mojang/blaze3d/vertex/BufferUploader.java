package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class BufferUploader {
   public int end(BufferBuilder builder, int vertexCount) {
      if (vertexCount > 0) {
         VertexFormat var3 = builder.getFormat();
         int var4 = var3.getVertexSize();
         ByteBuffer var5 = builder.getBuffer();
         List var6 = var3.getElements();

         for(VertexFormatElement var8 : var6) {
            VertexFormatElement.Usage var9 = var8.getUsage();
            int var10 = var8.getType().getGlCode();
            int var11 = var8.getIndex();
            switch(var9) {
               case POSITION:
                  ((Buffer)var5).position(var8.getFormatSize());
                  GL11.glVertexPointer(var8.getCount(), var10, var4, var5);
                  GL11.glEnableClientState(32884);
                  break;
               case UV:
                  ((Buffer)var5).position(var8.getFormatSize());
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0 + var11);
                  GL11.glTexCoordPointer(var8.getCount(), var10, var4, var5);
                  GL11.glEnableClientState(32888);
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0);
                  break;
               case COLOR:
                  ((Buffer)var5).position(var8.getFormatSize());
                  GL11.glColorPointer(var8.getCount(), var10, var4, var5);
                  GL11.glEnableClientState(32886);
                  break;
               case NORMAL:
                  ((Buffer)var5).position(var8.getFormatSize());
                  GL11.glNormalPointer(var10, var4, var5);
                  GL11.glEnableClientState(32885);
            }
         }

         GL11.glDrawArrays(builder.getDrawMode(), 0, builder.getVertexCount());

         for(VertexFormatElement var13 : var6) {
            VertexFormatElement.Usage var14 = var13.getUsage();
            int var15 = var13.getIndex();
            switch(var14) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0 + var15);
                  GL11.glDisableClientState(32888);
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0);
                  break;
               case COLOR:
                  GL11.glDisableClientState(32886);
                  GlStateManager.clearColor();
                  break;
               case NORMAL:
                  GL11.glDisableClientState(32885);
            }
         }
      }

      builder.clear();
      return vertexCount;
   }
}
