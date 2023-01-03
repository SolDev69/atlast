package net.minecraft.util;

public abstract class LazySupplier {
   private Object value;
   private boolean loaded = false;

   public Object get() {
      if (!this.loaded) {
         this.loaded = true;
         this.value = this.load();
      }

      return this.value;
   }

   protected abstract Object load();
}
