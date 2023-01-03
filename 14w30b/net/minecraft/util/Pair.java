package net.minecraft.util;

public class Pair {
   private Object left;
   private Object right;

   public Pair(Object left, Object right) {
      this.left = left;
      this.right = right;
   }

   public Object getLeft() {
      return this.left;
   }

   public Object getRight() {
      return this.right;
   }
}
