package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;

public class IntArrays {
   private static int num = 256;
   private static List tcache = Lists.newArrayList();
   private static List tallocated = Lists.newArrayList();
   private static List cache = Lists.newArrayList();
   private static List allocated = Lists.newArrayList();

   public static synchronized int[] get(int size) {
      if (size <= 256) {
         if (tcache.isEmpty()) {
            int[] var5 = new int[256];
            tallocated.add(var5);
            return var5;
         } else {
            int[] var4 = (int[])tcache.remove(tcache.size() - 1);
            tallocated.add(var4);
            return var4;
         }
      } else if (size > num) {
         num = size;
         cache.clear();
         allocated.clear();
         int[] var3 = new int[num];
         allocated.add(var3);
         return var3;
      } else if (cache.isEmpty()) {
         int[] var2 = new int[num];
         allocated.add(var2);
         return var2;
      } else {
         int[] var1 = (int[])cache.remove(cache.size() - 1);
         allocated.add(var1);
         return var1;
      }
   }

   public static synchronized void next() {
      if (!cache.isEmpty()) {
         cache.remove(cache.size() - 1);
      }

      if (!tcache.isEmpty()) {
         tcache.remove(tcache.size() - 1);
      }

      cache.addAll(allocated);
      tcache.addAll(tallocated);
      allocated.clear();
      tallocated.clear();
   }

   public static synchronized String toString() {
      return "cache: " + cache.size() + ", tcache: " + tcache.size() + ", allocated: " + allocated.size() + ", tallocated: " + tallocated.size();
   }
}
