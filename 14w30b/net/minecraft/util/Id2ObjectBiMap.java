package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class Id2ObjectBiMap implements IdObjectIterable {
   private final IdentityHashMap ids = new IdentityHashMap(512);
   private final List values = Lists.newArrayList();

   public void put(Object value, int id) {
      this.ids.put(value, id);

      while(this.values.size() <= id) {
         this.values.add(null);
      }

      this.values.set(id, value);
   }

   public int getId(Object value) {
      Integer var2 = (Integer)this.ids.get(value);
      return var2 == null ? -1 : var2;
   }

   public final Object get(int id) {
      return id >= 0 && id < this.values.size() ? this.values.get(id) : null;
   }

   @Override
   public Iterator iterator() {
      return Iterators.filter(this.values.iterator(), Predicates.notNull());
   }
}
