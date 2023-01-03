package net.minecraft.block.state.property;

import com.google.common.base.Objects;

public abstract class AbstractProperty implements Property {
   private final Class type;
   private final String name;

   protected AbstractProperty(String name, Class type) {
      this.type = type;
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Class getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.type).add("values", this.values()).toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         AbstractProperty var2 = (AbstractProperty)obj;
         return this.type.equals(var2.type) && this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.type.hashCode() + this.name.hashCode();
   }
}
