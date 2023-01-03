package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.property.Property;

public abstract class AbstractBlockState implements BlockState {
   private static final Joiner ENTRY_JOINER = Joiner.on(',');
   private static final Function ENTRY_TO_STRING = new Function() {
      public String apply(Entry entry) {
         if (entry == null) {
            return "<NULL>";
         } else {
            Property var2 = (Property)entry.getKey();
            return var2.getName() + "=" + var2.getName((Comparable)entry.getValue());
         }
      }
   };

   @Override
   public BlockState next(Property property) {
      return this.set(property, (Comparable)findNext(property.values(), this.get(property)));
   }

   protected static Object findNext(Collection values, Object current) {
      Iterator var2 = values.iterator();

      while(var2.hasNext()) {
         if (var2.next().equals(current)) {
            if (var2.hasNext()) {
               return var2.next();
            }

            return values.iterator().next();
         }
      }

      return var2.next();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(Block.REGISTRY.getKey(this.getBlock()));
      if (!this.values().isEmpty()) {
         var1.append("[");
         ENTRY_JOINER.appendTo(var1, Iterables.transform(this.values().entrySet(), ENTRY_TO_STRING));
         var1.append("]");
      }

      return var1.toString();
   }
}
