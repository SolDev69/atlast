package net.minecraft.util.registry;

public class DefaultedRegistry extends MappedRegistry {
   private final Object defaultValue;

   public DefaultedRegistry(Object defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public Object get(Object key) {
      Object var2 = super.get(key);
      return var2 == null ? this.defaultValue : var2;
   }
}
