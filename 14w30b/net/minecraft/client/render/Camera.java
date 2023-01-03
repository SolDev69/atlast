package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@Environment(EnvType.CLIENT)
public class Camera {
   private static final IntBuffer glIntBuffer = MemoryTracker.createIntBuffer(16);
   private static final FloatBuffer glFloatBuffer1 = MemoryTracker.createFloatBuffer(16);
   private static final FloatBuffer glFloatBuffer2 = MemoryTracker.createFloatBuffer(16);
   private static final FloatBuffer POSITION_BUFFER = MemoryTracker.createFloatBuffer(3);
   private static Vec3d offset = new Vec3d(0.0, 0.0, 0.0);
   private static float dx;
   private static float dy;
   private static float dz;
   private static float forwards;
   private static float sideways;

   public static void setup(PlayerEntity camera, boolean thirdPerson) {
      GlStateManager.getFloat(2982, glFloatBuffer1);
      GlStateManager.getFloat(2983, glFloatBuffer2);
      GL11.glGetInteger(2978, glIntBuffer);
      float var2 = (float)((glIntBuffer.get(0) + glIntBuffer.get(2)) / 2);
      float var3 = (float)((glIntBuffer.get(1) + glIntBuffer.get(3)) / 2);
      GLU.gluUnProject(var2, var3, 0.0F, glFloatBuffer1, glFloatBuffer2, glIntBuffer, POSITION_BUFFER);
      offset = new Vec3d((double)POSITION_BUFFER.get(0), (double)POSITION_BUFFER.get(1), (double)POSITION_BUFFER.get(2));
      int var4 = thirdPerson ? 1 : 0;
      float var5 = camera.pitch;
      float var6 = camera.yaw;
      dx = MathHelper.cos(var6 * (float) Math.PI / 180.0F) * (float)(1 - var4 * 2);
      dz = MathHelper.sin(var6 * (float) Math.PI / 180.0F) * (float)(1 - var4 * 2);
      forwards = -dz * MathHelper.sin(var5 * (float) Math.PI / 180.0F) * (float)(1 - var4 * 2);
      sideways = dx * MathHelper.sin(var5 * (float) Math.PI / 180.0F) * (float)(1 - var4 * 2);
      dy = MathHelper.cos(var5 * (float) Math.PI / 180.0F);
   }

   public static Vec3d getPos(Entity camera, double tickDelta) {
      double var3 = camera.prevX + (camera.x - camera.prevX) * tickDelta;
      double var5 = camera.prevY + (camera.y - camera.prevY) * tickDelta;
      double var7 = camera.prevZ + (camera.z - camera.prevZ) * tickDelta;
      double var9 = var3 + offset.x;
      double var11 = var5 + offset.y;
      double var13 = var7 + offset.z;
      return new Vec3d(var9, var11, var13);
   }

   public static Block getLiquidInside(World world, Entity camera, float tickDelta) {
      Vec3d var3 = getPos(camera, (double)tickDelta);
      BlockPos var4 = new BlockPos(var3);
      BlockState var5 = world.getBlockState(var4);
      Block var6 = var5.getBlock();
      if (var6.getMaterial().isLiquid()) {
         float var7 = 0.0F;
         if (var5.getBlock() instanceof LiquidBlock) {
            var7 = LiquidBlock.getHeightLoss(var5.get(LiquidBlock.LEVEL)) - 0.11111111F;
         }

         float var8 = (float)(var4.getY() + 1) - var7;
         if (var3.y >= (double)var8) {
            var6 = world.getBlockState(var4.up()).getBlock();
         }
      }

      return var6;
   }

   public static Vec3d offset() {
      return offset;
   }

   public static float dx() {
      return dx;
   }

   public static float dy() {
      return dy;
   }

   public static float dz() {
      return dz;
   }

   public static float forwards() {
      return forwards;
   }

   public static float sideways() {
      return sideways;
   }
}
