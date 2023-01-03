package com.mojang.blaze3d.platform;

import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@Environment(EnvType.CLIENT)
public class MemoryTracker {
   private static final List LISTS = Lists.newArrayList();

   public static synchronized int getLists(int s) {
      int var1 = GL11.glGenLists(s);
      if (var1 == 0) {
         int var2 = GL11.glGetError();
         String var3 = "No error code reported";
         if (var2 != 0) {
            var3 = GLU.gluErrorString(var2);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + s + ", GL error (" + var2 + "): " + var3);
      } else {
         return var1;
      }
   }

   public static synchronized void releaseLists(int list, int range) {
      GL11.glDeleteLists(list, range);
   }

   public static synchronized void releaseList(int list) {
      GL11.glDeleteLists(list, 1);
   }

   public static synchronized ByteBuffer createByteBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
   }

   public static IntBuffer createIntBuffer(int capacity) {
      return createByteBuffer(capacity << 2).asIntBuffer();
   }

   public static FloatBuffer createFloatBuffer(int capacity) {
      return createByteBuffer(capacity << 2).asFloatBuffer();
   }
}
