package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GLX;
import java.nio.ByteBuffer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class VertexBuffer {
   private int id;
   private final VertexFormat format;
   private int vertexCount;

   public VertexBuffer(VertexFormat format) {
      this.format = format;
      this.id = GLX.genBuffers();
   }

   public void bind() {
      GLX.bindBuffer(GLX.GL_ARRAY_BUFFER, this.id);
   }

   public void upload(ByteBuffer buffer, int limit) {
      this.bind();
      GLX.bufferData(GLX.GL_ARRAY_BUFFER, buffer, 35044);
      this.unbind();
      this.vertexCount = limit / this.format.getVertexSize();
   }

   public void draw(int mode) {
      GL11.glDrawArrays(mode, 0, this.vertexCount);
   }

   public void unbind() {
      GLX.bindBuffer(GLX.GL_ARRAY_BUFFER, 0);
   }

   public void delete() {
      if (this.id >= 0) {
         GLX.deleteBuffers(this.id);
         this.id = -1;
      }
   }
}
