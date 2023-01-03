package net.minecraft.block.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class IntegerProperty extends AbstractProperty {
   private final ImmutableSet values;

   protected IntegerProperty(String name, int min, int max) {
      super(name, Integer.class);
      if (min < 0) {
         throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
      } else if (max <= min) {
         throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
      } else {
         HashSet var4 = Sets.newHashSet();

         for(int var5 = min; var5 <= max; ++var5) {
            var4.add(var5);
         }

         this.values = ImmutableSet.copyOf(var4);
      }
   }

   @Override
   public Collection values() {
      return this.values;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object == null || this.getClass() != object.getClass()) {
         return false;
      } else if (!super.equals(object)) {
         return false;
      } else {
         IntegerProperty var2 = (IntegerProperty)object;
         return this.values.equals(var2.values);
      }
   }

   @Override
   public int hashCode() {
      int var1 = super.hashCode();
      return 31 * var1 + this.values.hashCode();
   }

   public static IntegerProperty of(String name, int min, int max) {
      return new IntegerProperty(name, min, max);
   }

   public String getName(Integer integer) {
      return integer.toString();
   }
}
