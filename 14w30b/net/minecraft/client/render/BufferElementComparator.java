package net.minecraft.client.render;

import java.nio.FloatBuffer;
import java.util.Comparator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BufferElementComparator implements Comparator {
   private float x;
   private float y;
   private float z;
   private FloatBuffer floatBuffer;
   private int vertexSize;

   public BufferElementComparator(FloatBuffer buffer, float x, float y, float z, int vertexSize) {
      this.floatBuffer = buffer;
      this.x = x;
      this.y = y;
      this.z = z;
      this.vertexSize = vertexSize;
   }

   public int compare(Integer integer, Integer integer2) {
      float var3 = this.floatBuffer.get(integer) - this.x;
      float var4 = this.floatBuffer.get(integer + 1) - this.y;
      float var5 = this.floatBuffer.get(integer + 2) - this.z;
      float var6 = this.floatBuffer.get(integer + this.vertexSize + 0) - this.x;
      float var7 = this.floatBuffer.get(integer + this.vertexSize + 1) - this.y;
      float var8 = this.floatBuffer.get(integer + this.vertexSize + 2) - this.z;
      float var9 = this.floatBuffer.get(integer + this.vertexSize * 2 + 0) - this.x;
      float var10 = this.floatBuffer.get(integer + this.vertexSize * 2 + 1) - this.y;
      float var11 = this.floatBuffer.get(integer + this.vertexSize * 2 + 2) - this.z;
      float var12 = this.floatBuffer.get(integer + this.vertexSize * 3 + 0) - this.x;
      float var13 = this.floatBuffer.get(integer + this.vertexSize * 3 + 1) - this.y;
      float var14 = this.floatBuffer.get(integer + this.vertexSize * 3 + 2) - this.z;
      float var15 = this.floatBuffer.get(integer2) - this.x;
      float var16 = this.floatBuffer.get(integer2 + 1) - this.y;
      float var17 = this.floatBuffer.get(integer2 + 2) - this.z;
      float var18 = this.floatBuffer.get(integer2 + this.vertexSize + 0) - this.x;
      float var19 = this.floatBuffer.get(integer2 + this.vertexSize + 1) - this.y;
      float var20 = this.floatBuffer.get(integer2 + this.vertexSize + 2) - this.z;
      float var21 = this.floatBuffer.get(integer2 + this.vertexSize * 2 + 0) - this.x;
      float var22 = this.floatBuffer.get(integer2 + this.vertexSize * 2 + 1) - this.y;
      float var23 = this.floatBuffer.get(integer2 + this.vertexSize * 2 + 2) - this.z;
      float var24 = this.floatBuffer.get(integer2 + this.vertexSize * 3 + 0) - this.x;
      float var25 = this.floatBuffer.get(integer2 + this.vertexSize * 3 + 1) - this.y;
      float var26 = this.floatBuffer.get(integer2 + this.vertexSize * 3 + 2) - this.z;
      float var27 = (var3 + var6 + var9 + var12) * 0.25F;
      float var28 = (var4 + var7 + var10 + var13) * 0.25F;
      float var29 = (var5 + var8 + var11 + var14) * 0.25F;
      float var30 = (var15 + var18 + var21 + var24) * 0.25F;
      float var31 = (var16 + var19 + var22 + var25) * 0.25F;
      float var32 = (var17 + var20 + var23 + var26) * 0.25F;
      float var33 = var27 * var27 + var28 * var28 + var29 * var29;
      float var34 = var30 * var30 + var31 * var31 + var32 * var32;
      return Float.compare(var34, var33);
   }
}
