package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Set;

public class Int2ObjectHashMap {
   private transient Int2ObjectHashMap.Node[] nodes;
   private transient int size;
   private int threshold;
   private final float loadFactor;
   private transient volatile int modCount;
   private Set ids = Sets.newHashSet();

   public Int2ObjectHashMap() {
      this.loadFactor = 0.75F;
      this.threshold = 12;
      this.nodes = new Int2ObjectHashMap.Node[16];
   }

   private static int hash(int key) {
      key ^= key >>> 20 ^ key >>> 12;
      return key ^ key >>> 7 ^ key >>> 4;
   }

   private static int index(int hash, int cap) {
      return hash & cap - 1;
   }

   public Object get(int key) {
      int var2 = hash(key);

      for(Int2ObjectHashMap.Node var3 = this.nodes[index(var2, this.nodes.length)]; var3 != null; var3 = var3.next) {
         if (var3.key == key) {
            return var3.value;
         }
      }

      return null;
   }

   public boolean contains(int key) {
      return this.getNode(key) != null;
   }

   final Int2ObjectHashMap.Node getNode(int key) {
      int var2 = hash(key);

      for(Int2ObjectHashMap.Node var3 = this.nodes[index(var2, this.nodes.length)]; var3 != null; var3 = var3.next) {
         if (var3.key == key) {
            return var3;
         }
      }

      return null;
   }

   public void put(int key, Object value) {
      this.ids.add(key);
      int var3 = hash(key);
      int var4 = index(var3, this.nodes.length);

      for(Int2ObjectHashMap.Node var5 = this.nodes[var4]; var5 != null; var5 = var5.next) {
         if (var5.key == key) {
            var5.value = value;
            return;
         }
      }

      ++this.modCount;
      this.insertNode(var3, key, value, var4);
   }

   private void resize(int size) {
      Int2ObjectHashMap.Node[] var2 = this.nodes;
      int var3 = var2.length;
      if (var3 == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         Int2ObjectHashMap.Node[] var4 = new Int2ObjectHashMap.Node[size];
         this.addAll(var4);
         this.nodes = var4;
         this.threshold = (int)((float)size * this.loadFactor);
      }
   }

   private void addAll(Int2ObjectHashMap.Node[] nodes) {
      Int2ObjectHashMap.Node[] var2 = this.nodes;
      int var3 = nodes.length;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         Int2ObjectHashMap.Node var5 = var2[var4];
         if (var5 != null) {
            var2[var4] = null;

            while(true) {
               Int2ObjectHashMap.Node var6 = var5.next;
               int var7 = index(var5.hash, var3);
               var5.next = nodes[var7];
               nodes[var7] = var5;
               var5 = var6;
               if (var6 == null) {
                  break;
               }
            }
         }
      }
   }

   public Object remove(int key) {
      this.ids.remove(key);
      Int2ObjectHashMap.Node var2 = this.removeNode(key);
      return var2 == null ? null : var2.value;
   }

   final Int2ObjectHashMap.Node removeNode(int key) {
      int var2 = hash(key);
      int var3 = index(var2, this.nodes.length);
      Int2ObjectHashMap.Node var4 = this.nodes[var3];

      Int2ObjectHashMap.Node var5;
      Int2ObjectHashMap.Node var6;
      for(var5 = var4; var5 != null; var5 = var6) {
         var6 = var5.next;
         if (var5.key == key) {
            ++this.modCount;
            --this.size;
            if (var4 == var5) {
               this.nodes[var3] = var6;
            } else {
               var4.next = var6;
            }

            return var5;
         }

         var4 = var5;
      }

      return var5;
   }

   public void clear() {
      ++this.modCount;
      Int2ObjectHashMap.Node[] var1 = this.nodes;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = null;
      }

      this.size = 0;
   }

   private void insertNode(int hash, int key, Object value, int index) {
      Int2ObjectHashMap.Node var5 = this.nodes[index];
      this.nodes[index] = new Int2ObjectHashMap.Node(hash, key, value, var5);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.nodes.length);
      }
   }

   static class Node {
      final int key;
      Object value;
      Int2ObjectHashMap.Node next;
      final int hash;

      Node(int hash, int key, Object value, Int2ObjectHashMap.Node next) {
         this.value = value;
         this.next = next;
         this.key = key;
         this.hash = hash;
      }

      public final int getKey() {
         return this.key;
      }

      public final Object getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object obj) {
         if (!(obj instanceof Int2ObjectHashMap.Node)) {
            return false;
         } else {
            Int2ObjectHashMap.Node var2 = (Int2ObjectHashMap.Node)obj;
            Integer var3 = this.getKey();
            Integer var4 = var2.getKey();
            if (var3 == var4 || var3 != null && var3.equals(var4)) {
               Object var5 = this.getValue();
               Object var6 = var2.getValue();
               if (var5 == var6 || var5 != null && var5.equals(var6)) {
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public final int hashCode() {
         return Int2ObjectHashMap.hash(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
