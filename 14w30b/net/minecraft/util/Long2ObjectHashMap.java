package net.minecraft.util;

public class Long2ObjectHashMap {
   private transient Long2ObjectHashMap.Node[] nodes;
   private transient int size;
   private int cap;
   private int threshold;
   private final float loadFactor = 0.75F;
   private transient volatile int modCount;

   public Long2ObjectHashMap() {
      this.threshold = 3072;
      this.nodes = new Long2ObjectHashMap.Node[4096];
      this.cap = this.nodes.length - 1;
   }

   private static int hash(long key) {
      return hash((int)(key ^ key >>> 32));
   }

   private static int hash(int key) {
      key ^= key >>> 20 ^ key >>> 12;
      return key ^ key >>> 7 ^ key >>> 4;
   }

   private static int index(int hash, int cap) {
      return hash & cap;
   }

   public int getSize() {
      return this.size;
   }

   public Object get(long key) {
      int var3 = hash(key);

      for(Long2ObjectHashMap.Node var4 = this.nodes[index(var3, this.cap)]; var4 != null; var4 = var4.next) {
         if (var4.key == key) {
            return var4.value;
         }
      }

      return null;
   }

   public boolean contains(long key) {
      return this.getNode(key) != null;
   }

   final Long2ObjectHashMap.Node getNode(long key) {
      int var3 = hash(key);

      for(Long2ObjectHashMap.Node var4 = this.nodes[index(var3, this.cap)]; var4 != null; var4 = var4.next) {
         if (var4.key == key) {
            return var4;
         }
      }

      return null;
   }

   public void put(long key, Object value) {
      int var4 = hash(key);
      int var5 = index(var4, this.cap);

      for(Long2ObjectHashMap.Node var6 = this.nodes[var5]; var6 != null; var6 = var6.next) {
         if (var6.key == key) {
            var6.value = value;
            return;
         }
      }

      ++this.modCount;
      this.insertNode(var4, key, value, var5);
   }

   private void resize(int cap) {
      Long2ObjectHashMap.Node[] var2 = this.nodes;
      int var3 = var2.length;
      if (var3 == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         Long2ObjectHashMap.Node[] var4 = new Long2ObjectHashMap.Node[cap];
         this.addAll(var4);
         this.nodes = var4;
         this.cap = this.nodes.length - 1;
         this.threshold = (int)((float)cap * this.loadFactor);
      }
   }

   private void addAll(Long2ObjectHashMap.Node[] nodes) {
      Long2ObjectHashMap.Node[] var2 = this.nodes;
      int var3 = nodes.length;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         Long2ObjectHashMap.Node var5 = var2[var4];
         if (var5 != null) {
            var2[var4] = null;

            while(true) {
               Long2ObjectHashMap.Node var6 = var5.next;
               int var7 = index(var5.hash, var3 - 1);
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

   public Object remove(long key) {
      Long2ObjectHashMap.Node var3 = this.removeNode(key);
      return var3 == null ? null : var3.value;
   }

   final Long2ObjectHashMap.Node removeNode(long key) {
      int var3 = hash(key);
      int var4 = index(var3, this.cap);
      Long2ObjectHashMap.Node var5 = this.nodes[var4];

      Long2ObjectHashMap.Node var6;
      Long2ObjectHashMap.Node var7;
      for(var6 = var5; var6 != null; var6 = var7) {
         var7 = var6.next;
         if (var6.key == key) {
            ++this.modCount;
            --this.size;
            if (var5 == var6) {
               this.nodes[var4] = var7;
            } else {
               var5.next = var7;
            }

            return var6;
         }

         var5 = var6;
      }

      return var6;
   }

   private void insertNode(int hash, long key, Object value, int index) {
      Long2ObjectHashMap.Node var6 = this.nodes[index];
      this.nodes[index] = new Long2ObjectHashMap.Node(hash, key, value, var6);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.nodes.length);
      }
   }

   static class Node {
      final long key;
      Object value;
      Long2ObjectHashMap.Node next;
      final int hash;

      Node(int hash, long key, Object value, Long2ObjectHashMap.Node next) {
         this.value = value;
         this.next = next;
         this.key = key;
         this.hash = hash;
      }

      public final long getKey() {
         return this.key;
      }

      public final Object getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object obj) {
         if (!(obj instanceof Long2ObjectHashMap.Node)) {
            return false;
         } else {
            Long2ObjectHashMap.Node var2 = (Long2ObjectHashMap.Node)obj;
            Long var3 = this.getKey();
            Long var4 = var2.getKey();
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
         return Long2ObjectHashMap.hash(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
