package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ClassUtils.Interfaces;

public class TypeInstanceMultiMap extends AbstractSet {
   private final Map f_23ajuddng = Maps.newHashMap();
   private final Multimap byType = HashMultimap.create();
   private final TIntObjectHashMap f_16vozspdw = new TIntObjectHashMap();
   private final Set types = Sets.newIdentityHashSet();

   public TypeInstanceMultiMap() {
      this.addType(Entity.class);
   }

   public void addType(Class type) {
      this.types.add(type);

      for(Entity var3 : this) {
         if (type.isAssignableFrom(var3.getClass())) {
            this.byType.put(type, var3);
         }
      }
   }

   protected Class findTop(Class class_) {
      for(Class var3 : ClassUtils.hierarchy(class_, Interfaces.INCLUDE)) {
         if (this.types.contains(var3)) {
            if (var3 == Entity.class) {
               this.addType(class_);
            }

            return var3;
         }
      }

      throw new IllegalArgumentException("Don't know how to search for " + class_);
   }

   public boolean add(Entity c_47ldwddrb) {
      this.f_23ajuddng.put(c_47ldwddrb.getUuid(), c_47ldwddrb);
      this.f_16vozspdw.put(c_47ldwddrb.getNetworkId(), c_47ldwddrb);

      for(Class var3 : this.types) {
         if (var3.isAssignableFrom(c_47ldwddrb.getClass())) {
            this.byType.put(var3, c_47ldwddrb);
         }
      }

      return true;
   }

   @Override
   public boolean remove(Object value) {
      if (value instanceof Entity) {
         Entity var2 = (Entity)value;
         boolean var3 = this.f_23ajuddng.remove(var2.getUuid()) != null;
         if (var3) {
            this.f_16vozspdw.remove(var2.getNetworkId());

            for(Class var5 : this.types) {
               if (var5.isAssignableFrom(var2.getClass())) {
                  this.byType.remove(var5, var2);
               }
            }
         }

         return var3;
      } else {
         throw new IllegalArgumentException("Object " + value + " is not an Entity");
      }
   }

   public Iterable find(Class type) {
      return new Iterable() {
         @Override
         public Iterator iterator() {
            Iterator var1 = TypeInstanceMultiMap.this.byType.get(TypeInstanceMultiMap.this.findTop(type)).iterator();
            return Iterators.filter(var1, type);
         }
      };
   }

   @Override
   public Iterator iterator() {
      final Iterator var1 = this.byType.get(Entity.class).iterator();
      return new AbstractIterator() {
         protected Entity computeNext() {
            return !var1.hasNext() ? (Entity)this.endOfData() : (Entity)var1.next();
         }
      };
   }

   @Override
   public int size() {
      return this.byType.get(Entity.class).size();
   }
}
