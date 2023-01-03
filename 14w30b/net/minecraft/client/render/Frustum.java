package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Frustum extends FrustumData {
   private static Frustum INSTANCE = new Frustum();
   private FloatBuffer projectionBuffer = MemoryTracker.createFloatBuffer(16);
   private FloatBuffer modelBuffer = MemoryTracker.createFloatBuffer(16);
   private FloatBuffer clipBuffer = MemoryTracker.createFloatBuffer(16);

   public static FrustumData getInstance() {
      INSTANCE.compute();
      return INSTANCE;
   }

   private void normalize(float[][] fs, int i) {
      float var3 = MathHelper.sqrt(fs[i][0] * fs[i][0] + fs[i][1] * fs[i][1] + fs[i][2] * fs[i][2]);
      fs[i][0] /= var3;
      fs[i][1] /= var3;
      fs[i][2] /= var3;
      fs[i][3] /= var3;
   }

   public void compute() {
      ((Buffer)this.projectionBuffer).clear();
      ((Buffer)this.modelBuffer).clear();
      ((Buffer)this.clipBuffer).clear();
      GlStateManager.getFloat(2983, this.projectionBuffer);
      GlStateManager.getFloat(2982, this.modelBuffer);
      ((Buffer)this.projectionBuffer).flip().limit(16);
      this.projectionBuffer.get(this.projectionMatrix);
      ((Buffer)this.modelBuffer).flip().limit(16);
      this.modelBuffer.get(this.modelMatrix);
      this.clipMatrix[0] = this.modelMatrix[0] * this.projectionMatrix[0]
         + this.modelMatrix[1] * this.projectionMatrix[4]
         + this.modelMatrix[2] * this.projectionMatrix[8]
         + this.modelMatrix[3] * this.projectionMatrix[12];
      this.clipMatrix[1] = this.modelMatrix[0] * this.projectionMatrix[1]
         + this.modelMatrix[1] * this.projectionMatrix[5]
         + this.modelMatrix[2] * this.projectionMatrix[9]
         + this.modelMatrix[3] * this.projectionMatrix[13];
      this.clipMatrix[2] = this.modelMatrix[0] * this.projectionMatrix[2]
         + this.modelMatrix[1] * this.projectionMatrix[6]
         + this.modelMatrix[2] * this.projectionMatrix[10]
         + this.modelMatrix[3] * this.projectionMatrix[14];
      this.clipMatrix[3] = this.modelMatrix[0] * this.projectionMatrix[3]
         + this.modelMatrix[1] * this.projectionMatrix[7]
         + this.modelMatrix[2] * this.projectionMatrix[11]
         + this.modelMatrix[3] * this.projectionMatrix[15];
      this.clipMatrix[4] = this.modelMatrix[4] * this.projectionMatrix[0]
         + this.modelMatrix[5] * this.projectionMatrix[4]
         + this.modelMatrix[6] * this.projectionMatrix[8]
         + this.modelMatrix[7] * this.projectionMatrix[12];
      this.clipMatrix[5] = this.modelMatrix[4] * this.projectionMatrix[1]
         + this.modelMatrix[5] * this.projectionMatrix[5]
         + this.modelMatrix[6] * this.projectionMatrix[9]
         + this.modelMatrix[7] * this.projectionMatrix[13];
      this.clipMatrix[6] = this.modelMatrix[4] * this.projectionMatrix[2]
         + this.modelMatrix[5] * this.projectionMatrix[6]
         + this.modelMatrix[6] * this.projectionMatrix[10]
         + this.modelMatrix[7] * this.projectionMatrix[14];
      this.clipMatrix[7] = this.modelMatrix[4] * this.projectionMatrix[3]
         + this.modelMatrix[5] * this.projectionMatrix[7]
         + this.modelMatrix[6] * this.projectionMatrix[11]
         + this.modelMatrix[7] * this.projectionMatrix[15];
      this.clipMatrix[8] = this.modelMatrix[8] * this.projectionMatrix[0]
         + this.modelMatrix[9] * this.projectionMatrix[4]
         + this.modelMatrix[10] * this.projectionMatrix[8]
         + this.modelMatrix[11] * this.projectionMatrix[12];
      this.clipMatrix[9] = this.modelMatrix[8] * this.projectionMatrix[1]
         + this.modelMatrix[9] * this.projectionMatrix[5]
         + this.modelMatrix[10] * this.projectionMatrix[9]
         + this.modelMatrix[11] * this.projectionMatrix[13];
      this.clipMatrix[10] = this.modelMatrix[8] * this.projectionMatrix[2]
         + this.modelMatrix[9] * this.projectionMatrix[6]
         + this.modelMatrix[10] * this.projectionMatrix[10]
         + this.modelMatrix[11] * this.projectionMatrix[14];
      this.clipMatrix[11] = this.modelMatrix[8] * this.projectionMatrix[3]
         + this.modelMatrix[9] * this.projectionMatrix[7]
         + this.modelMatrix[10] * this.projectionMatrix[11]
         + this.modelMatrix[11] * this.projectionMatrix[15];
      this.clipMatrix[12] = this.modelMatrix[12] * this.projectionMatrix[0]
         + this.modelMatrix[13] * this.projectionMatrix[4]
         + this.modelMatrix[14] * this.projectionMatrix[8]
         + this.modelMatrix[15] * this.projectionMatrix[12];
      this.clipMatrix[13] = this.modelMatrix[12] * this.projectionMatrix[1]
         + this.modelMatrix[13] * this.projectionMatrix[5]
         + this.modelMatrix[14] * this.projectionMatrix[9]
         + this.modelMatrix[15] * this.projectionMatrix[13];
      this.clipMatrix[14] = this.modelMatrix[12] * this.projectionMatrix[2]
         + this.modelMatrix[13] * this.projectionMatrix[6]
         + this.modelMatrix[14] * this.projectionMatrix[10]
         + this.modelMatrix[15] * this.projectionMatrix[14];
      this.clipMatrix[15] = this.modelMatrix[12] * this.projectionMatrix[3]
         + this.modelMatrix[13] * this.projectionMatrix[7]
         + this.modelMatrix[14] * this.projectionMatrix[11]
         + this.modelMatrix[15] * this.projectionMatrix[15];
      this.frustum[0][0] = this.clipMatrix[3] - this.clipMatrix[0];
      this.frustum[0][1] = this.clipMatrix[7] - this.clipMatrix[4];
      this.frustum[0][2] = this.clipMatrix[11] - this.clipMatrix[8];
      this.frustum[0][3] = this.clipMatrix[15] - this.clipMatrix[12];
      this.normalize(this.frustum, 0);
      this.frustum[1][0] = this.clipMatrix[3] + this.clipMatrix[0];
      this.frustum[1][1] = this.clipMatrix[7] + this.clipMatrix[4];
      this.frustum[1][2] = this.clipMatrix[11] + this.clipMatrix[8];
      this.frustum[1][3] = this.clipMatrix[15] + this.clipMatrix[12];
      this.normalize(this.frustum, 1);
      this.frustum[2][0] = this.clipMatrix[3] + this.clipMatrix[1];
      this.frustum[2][1] = this.clipMatrix[7] + this.clipMatrix[5];
      this.frustum[2][2] = this.clipMatrix[11] + this.clipMatrix[9];
      this.frustum[2][3] = this.clipMatrix[15] + this.clipMatrix[13];
      this.normalize(this.frustum, 2);
      this.frustum[3][0] = this.clipMatrix[3] - this.clipMatrix[1];
      this.frustum[3][1] = this.clipMatrix[7] - this.clipMatrix[5];
      this.frustum[3][2] = this.clipMatrix[11] - this.clipMatrix[9];
      this.frustum[3][3] = this.clipMatrix[15] - this.clipMatrix[13];
      this.normalize(this.frustum, 3);
      this.frustum[4][0] = this.clipMatrix[3] - this.clipMatrix[2];
      this.frustum[4][1] = this.clipMatrix[7] - this.clipMatrix[6];
      this.frustum[4][2] = this.clipMatrix[11] - this.clipMatrix[10];
      this.frustum[4][3] = this.clipMatrix[15] - this.clipMatrix[14];
      this.normalize(this.frustum, 4);
      this.frustum[5][0] = this.clipMatrix[3] + this.clipMatrix[2];
      this.frustum[5][1] = this.clipMatrix[7] + this.clipMatrix[6];
      this.frustum[5][2] = this.clipMatrix[11] + this.clipMatrix[10];
      this.frustum[5][3] = this.clipMatrix[15] + this.clipMatrix[14];
      this.normalize(this.frustum, 5);
   }
}
