package net.minecraft.util.registry;

import org.apache.commons.lang3.Validate;

public class DefaultedIdRegistry extends IdRegistry {
   private final Object defaultKey;
   private Object defaultValue;

   public DefaultedIdRegistry(Object defaultKey) {
      this.defaultKey = defaultKey;
   }

   @Override
   public void register(int id, Object key, Object value) {
      if (this.defaultKey.equals(key)) {
         this.defaultValue = value;
      }

      super.register(id, key, value);
   }

   public void validate() {
      Validate.notNull(this.defaultKey);
   }

   @Override
   public Object get(Object key) {
      Object var2 = super.get(key);
      return var2 == null ? this.defaultValue : var2;
   }

   @Override
   public Object get(int id) {
      Object var2 = super.get(id);
      return var2 == null ? this.defaultValue : var2;
   }
}
