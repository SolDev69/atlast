package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.PriorityQueue;
import net.minecraft.client.render.BufferElementComparator;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;

@Environment(EnvType.CLIENT)
public class BufferBuilder {
   private ByteBuffer byteBuffer;
   private IntBuffer intBuffer;
   private FloatBuffer floatBuffer;
   private int vertexCount;
   private double u;
   private double v;
   private int brightness;
   private int color;
   private int index;
   private boolean uncolored;
   private int drawMode;
   private double offsetX;
   private double offsetY;
   private double offsetZ;
   private int normals;
   private int limit;
   private VertexFormat format;
   private boolean building;
   private int size;

   public BufferBuilder(int size) {
      this.size = size;
      this.byteBuffer = MemoryTracker.createByteBuffer(size * 4);
      this.intBuffer = this.byteBuffer.asIntBuffer();
      this.floatBuffer = this.byteBuffer.asFloatBuffer();
      this.format = new VertexFormat();
      this.format.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
   }

   private void grow(int amount) {
      LogManager.getLogger()
         .warn("Needed to grow BufferBuilder buffer: Old size " + this.size * 4 + " bytes, new size " + (this.size * 4 + amount) + " bytes.");
      this.size += amount / 4;
      ByteBuffer var2 = MemoryTracker.createByteBuffer(this.size * 4);
      ((Buffer)this.intBuffer).position(0);
      var2.asIntBuffer().put(this.intBuffer);
      this.byteBuffer = var2;
      this.intBuffer = this.byteBuffer.asIntBuffer();
      this.floatBuffer = this.byteBuffer.asFloatBuffer();
   }

   public BufferBuilder.State buildState(float x, float y, float z) {
      int[] var4 = new int[this.index];
      PriorityQueue var5 = new PriorityQueue(
         this.index,
         new BufferElementComparator(
            this.floatBuffer,
            (float)((double)x + this.offsetX),
            (float)((double)y + this.offsetY),
            (float)((double)z + this.offsetZ),
            this.format.getVertexSize() / 4
         )
      );
      int var6 = this.format.getVertexSize();

      for(int var7 = 0; var7 < this.index; var7 += var6) {
         var5.add(var7);
      }

      for(int var10 = 0; !var5.isEmpty(); var10 += var6) {
         int var8 = var5.remove();

         for(int var9 = 0; var9 < var6; ++var9) {
            var4[var10 + var9] = this.intBuffer.get(var8 + var9);
         }
      }

      ((Buffer)this.intBuffer).clear();
      this.intBuffer.put(var4);
      return new BufferBuilder.State(var4, this.index, this.vertexCount, new VertexFormat(this.format));
   }

   public void setState(BufferBuilder.State state) {
      ((Buffer)this.intBuffer).clear();
      this.intBuffer.put(state.getBuffer());
      this.index = state.getIndex();
      this.vertexCount = state.getVertexCount();
      this.format = new VertexFormat(state.getFormat());
   }

   public void clear() {
      this.vertexCount = 0;
      this.index = 0;
      this.format.clear();
      this.format.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
   }

   public void start() {
      this.start(7);
   }

   public void start(int drawMode) {
      if (this.building) {
         throw new IllegalStateException("Already building!");
      } else {
         this.building = true;
         this.clear();
         this.drawMode = drawMode;
         this.uncolored = false;
      }
   }

   public void texture(double u, double v) {
      if (!this.format.hasUv(0) && !this.format.hasUv(1)) {
         VertexFormatElement var5 = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
         this.format.add(var5);
      }

      this.u = u;
      this.v = v;
   }

   public void brightness(int brightness) {
      if (!this.format.hasUv(1)) {
         if (!this.format.hasUv(0)) {
            this.format.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2));
         }

         VertexFormatElement var2 = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
         this.format.add(var2);
      }

      this.brightness = brightness;
   }

   public void color(float r, float g, float b) {
      this.color((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
   }

   public void color(float r, float g, float b, float a) {
      this.color((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
   }

   public void color(int r, int g, int b) {
      this.color(r, g, b, 255);
   }

   public void position(int r, int g, int b, int a) {
      int var5 = (this.vertexCount - 4) * (this.format.getVertexSize() / 4) + this.format.getUvOffset(1) / 4;
      int var6 = this.format.getVertexSize() >> 2;
      this.intBuffer.put(var5, r);
      this.intBuffer.put(var5 + var6, g);
      this.intBuffer.put(var5 + var6 * 2, b);
      this.intBuffer.put(var5 + var6 * 3, a);
   }

   public void postPosition(double x, double y, double z) {
      if (this.index >= this.size - this.format.getVertexSize()) {
         this.grow(2097152);
      }

      int var7 = this.format.getVertexSize() / 4;
      int var8 = (this.vertexCount - 4) * var7;

      for(int var9 = 0; var9 < 4; ++var9) {
         int var10 = var8 + var9 * var7;
         int var11 = var10 + 1;
         int var12 = var11 + 1;
         this.intBuffer.put(var10, Float.floatToRawIntBits((float)(x + this.offsetX) + Float.intBitsToFloat(this.intBuffer.get(var10))));
         this.intBuffer.put(var11, Float.floatToRawIntBits((float)(y + this.offsetY) + Float.intBitsToFloat(this.intBuffer.get(var11))));
         this.intBuffer.put(var12, Float.floatToRawIntBits((float)(z + this.offsetZ) + Float.intBitsToFloat(this.intBuffer.get(var12))));
      }
   }

   public void multiplyColor(float r, float g, float b, int index) {
      int var5 = ((this.vertexCount - index) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
      int var6 = this.intBuffer.get(var5);
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         int var7 = (int)((float)(var6 & 0xFF) * r);
         int var8 = (int)((float)(var6 >> 8 & 0xFF) * g);
         int var9 = (int)((float)(var6 >> 16 & 0xFF) * b);
         var6 &= -16777216;
         var6 |= var9 << 16 | var8 << 8 | var7;
      } else {
         int var13 = (int)((float)(this.color >> 24 & 0xFF) * r);
         int var14 = (int)((float)(this.color >> 16 & 0xFF) * g);
         int var15 = (int)((float)(this.color >> 8 & 0xFF) * b);
         var6 &= 255;
         var6 |= var13 << 24 | var14 << 16 | var15 << 8;
      }

      if (this.uncolored) {
         var6 = -1;
      }

      this.intBuffer.put(var5, var6);
   }

   public void setColor(float f, float g, float h, float i, int j) {
      int var6 = ((this.vertexCount - j) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
      int var7 = MathHelper.clamp((int)(f * 255.0F), 0, 255);
      int var8 = MathHelper.clamp((int)(g * 255.0F), 0, 255);
      int var9 = MathHelper.clamp((int)(h * 255.0F), 0, 255);
      int var10 = MathHelper.clamp((int)(i * 255.0F), 0, 255);
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.intBuffer.put(var6, var10 << 24 | var9 << 16 | var8 << 8 | var7);
      } else {
         this.intBuffer.put(var6, var7 << 24 | var8 << 16 | var9 << 8 | var10);
      }
   }

   public void color(int r, int g, int b, int a) {
      if (!this.uncolored) {
         if (r > 255) {
            r = 255;
         }

         if (g > 255) {
            g = 255;
         }

         if (b > 255) {
            b = 255;
         }

         if (a > 255) {
            a = 255;
         }

         if (r < 0) {
            r = 0;
         }

         if (g < 0) {
            g = 0;
         }

         if (b < 0) {
            b = 0;
         }

         if (a < 0) {
            a = 0;
         }

         if (!this.format.hasColor()) {
            VertexFormatElement var5 = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
            this.format.add(var5);
         }

         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.color = a << 24 | b << 16 | g << 8 | r;
         } else {
            this.color = r << 24 | g << 16 | b << 8 | a;
         }
      }
   }

   public void color(byte r, byte g, byte b) {
      this.color(r & 255, g & 255, b & 255);
   }

   public void vertex(double x, double y, double z, double u, double v) {
      this.texture(u, v);
      this.vertex(x, y, z);
   }

   public void format(VertexFormat format) {
      this.format = new VertexFormat(format);
   }

   public void vertices(int[] vertices) {
      int var2 = this.format.getVertexSize() / 4;
      this.vertexCount += vertices.length / var2;
      ((Buffer)this.intBuffer).position(this.index);
      this.intBuffer.put(vertices);
      this.index += vertices.length;
   }

   public void vertex(double x, double y, double z) {
      if (this.index >= this.size - this.format.getVertexSize()) {
         this.grow(2097152);
      }

      for(VertexFormatElement var9 : this.format.getElements()) {
         int var10 = var9.getFormatSize() >> 2;
         int var11 = this.index + var10;
         switch(var9.getUsage()) {
            case POSITION:
               this.intBuffer.put(var11, Float.floatToRawIntBits((float)(x + this.offsetX)));
               this.intBuffer.put(var11 + 1, Float.floatToRawIntBits((float)(y + this.offsetY)));
               this.intBuffer.put(var11 + 2, Float.floatToRawIntBits((float)(z + this.offsetZ)));
               break;
            case COLOR:
               this.intBuffer.put(var11, this.color);
               break;
            case UV:
               if (var9.getIndex() == 0) {
                  this.intBuffer.put(var11, Float.floatToRawIntBits((float)this.u));
                  this.intBuffer.put(var11 + 1, Float.floatToRawIntBits((float)this.v));
               } else {
                  this.intBuffer.put(var11, this.brightness);
               }
               break;
            case NORMAL:
               this.intBuffer.put(var11, this.normals);
         }
      }

      this.index += this.format.getVertexSize() >> 2;
      ++this.vertexCount;
   }

   public void color(int rgb) {
      int var2 = rgb >> 16 & 0xFF;
      int var3 = rgb >> 8 & 0xFF;
      int var4 = rgb & 0xFF;
      this.color(var2, var3, var4);
   }

   public void color(int rgb, int a) {
      int var3 = rgb >> 16 & 0xFF;
      int var4 = rgb >> 8 & 0xFF;
      int var5 = rgb & 0xFF;
      this.color(var3, var4, var5, a);
   }

   public void uncolored() {
      this.uncolored = true;
   }

   public void normal(float x, float y, float z) {
      if (!this.format.hasNormal()) {
         VertexFormatElement var4 = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
         this.format.add(var4);
         this.format.add(new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.PADDING, 1));
      }

      byte var7 = (byte)((int)(x * 127.0F));
      byte var5 = (byte)((int)(y * 127.0F));
      byte var6 = (byte)((int)(z * 127.0F));
      this.normals = var7 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
   }

   public void postNormal(float x, float y, float z) {
      byte var4 = (byte)((int)(x * 127.0F));
      byte var5 = (byte)((int)(y * 127.0F));
      byte var6 = (byte)((int)(z * 127.0F));
      int var7 = this.format.getVertexSize() >> 2;
      int var8 = (this.vertexCount - 4) * var7 + this.format.getNormalOffset() / 4;
      this.normals = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
      this.intBuffer.put(var8, this.normals);
      this.intBuffer.put(var8 + var7, this.normals);
      this.intBuffer.put(var8 + var7 * 2, this.normals);
      this.intBuffer.put(var8 + var7 * 3, this.normals);
   }

   public void offset(double offsetX, double offsetY, double offsetZ) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
   }

   public int end() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      } else {
         this.building = false;
         if (this.vertexCount > 0) {
            ((Buffer)this.byteBuffer).position(0);
            ((Buffer)this.byteBuffer).limit(this.index * 4);
         }

         this.limit = this.index * 4;
         return this.limit;
      }
   }

   public int getLimit() {
      return this.limit;
   }

   public ByteBuffer getBuffer() {
      return this.byteBuffer;
   }

   public VertexFormat getFormat() {
      return this.format;
   }

   public int getVertexCount() {
      return this.vertexCount;
   }

   public int getDrawMode() {
      return this.drawMode;
   }

   @Environment(EnvType.CLIENT)
   public class State {
      private final int[] buffer;
      private final int index;
      private final int vertexCount;
      private final VertexFormat format;

      public State(int[] buffer, int index, int vertexCount, VertexFormat format) {
         this.buffer = buffer;
         this.index = index;
         this.vertexCount = vertexCount;
         this.format = format;
      }

      public int[] getBuffer() {
         return this.buffer;
      }

      public int getIndex() {
         return this.index;
      }

      public int getVertexCount() {
         return this.vertexCount;
      }

      public VertexFormat getFormat() {
         return this.format;
      }
   }
}
