package com.mojang.blaze3d.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class MemoryTracker {
   private static final Map LIST_RANGES = Maps.newHashMap();
   private static final List LISTS = Lists.newArrayList();

   public static synchronized int getLists(int s) {
      int var1 = GL11.glGenLists(s);
      LIST_RANGES.put(var1, s);
      return var1;
   }

   public static synchronized void releaseList(int list) {
      GL11.glDeleteLists(list, LIST_RANGES.remove(list));
   }

   public static synchronized void releaseLists() {
      for(Entry var1 : LIST_RANGES.entrySet()) {
         GL11.glDeleteLists(var1.getKey(), var1.getValue());
      }

      LIST_RANGES.clear();
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
