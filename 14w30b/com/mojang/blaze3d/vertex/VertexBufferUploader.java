package com.mojang.blaze3d.vertex;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexBufferUploader extends BufferUploader {
   private VertexBuffer buffer = null;

   @Override
   public int end(BufferBuilder builder, int vertexCount) {
      builder.clear();
      this.buffer.upload(builder.getBuffer(), builder.getLimit());
      return vertexCount;
   }

   public void setBuffer(VertexBuffer buffer) {
      this.buffer = buffer;
   }
}
