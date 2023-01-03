package com.mojang.blaze3d.vertex;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tessellator {
   private BufferBuilder bufferBuilder;
   private BufferUploader bufferUploader = new BufferUploader();
   private static final Tessellator INSTANCE = new Tessellator(2097152);

   public static Tessellator getInstance() {
      return INSTANCE;
   }

   public Tessellator(int size) {
      this.bufferBuilder = new BufferBuilder(size);
   }

   public int end() {
      return this.bufferUploader.end(this.bufferBuilder, this.bufferBuilder.end());
   }

   public BufferBuilder getBufferBuilder() {
      return this.bufferBuilder;
   }
}
