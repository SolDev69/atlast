package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IterableBuilder {
   public static Iterable iterableIterableToArrayIterable(Class type, Iterable iterableIterable) {
      return new IterableBuilder.ArrayIterable(type, (Iterable[])iterableToArray(Iterable.class, iterableIterable));
   }

   public static Iterable iterableIterableToListIterable(Iterable iterableIterable) {
      return arrayIterableToListIterable(iterableIterableToArrayIterable(Object.class, iterableIterable));
   }

   private static Iterable arrayIterableToListIterable(Iterable arrayIterable) {
      return Iterables.transform(arrayIterable, new IterableBuilder.ArrayToListFunction());
   }

   private static Object[] iterableToArray(Class type, Iterable iterable) {
      ArrayList var2 = Lists.newArrayList();

      for(Object var4 : iterable) {
         var2.add(var4);
      }

      return var2.toArray(newArray(type, var2.size()));
   }

   private static Object[] newArray(Class type, int size) {
      return Array.newInstance(type, size);
   }

   static class ArrayIterable implements Iterable {
      private final Class type;
      private final Iterable[] iterables;

      private ArrayIterable(Class type, Iterable[] iterables) {
         this.type = type;
         this.iterables = iterables;
      }

      @Override
      public Iterator iterator() {
         return (Iterator)(this.iterables.length <= 0
            ? Collections.singletonList(IterableBuilder.newArray(this.type, 0)).iterator()
            : new IterableBuilder.ArrayIterable.ArrayIterator(this.type, this.iterables));
      }

      static class ArrayIterator extends UnmodifiableIterator {
         private int nextIterator = -2;
         private final Iterable[] iterables;
         private final Iterator[] iterators;
         private final Object[] nextArray;

         private ArrayIterator(Class type, Iterable[] iterables) {
            this.iterables = iterables;
            this.iterators = (Iterator[])IterableBuilder.newArray(Iterator.class, this.iterables.length);

            for(int var3 = 0; var3 < this.iterables.length; ++var3) {
               this.iterators[var3] = iterables[var3].iterator();
            }

            this.nextArray = IterableBuilder.newArray(type, this.iterators.length);
         }

         private void cleanUp() {
            this.nextIterator = -1;
            Arrays.fill(this.iterators, null);
            Arrays.fill(this.nextArray, null);
         }

         public boolean hasNext() {
            if (this.nextIterator == -2) {
               this.nextIterator = 0;

               for(Iterator var4 : this.iterators) {
                  if (!var4.hasNext()) {
                     this.cleanUp();
                     break;
                  }
               }

               return true;
            } else {
               if (this.nextIterator >= this.iterators.length) {
                  for(this.nextIterator = this.iterators.length - 1; this.nextIterator >= 0; --this.nextIterator) {
                     Iterator var1 = this.iterators[this.nextIterator];
                     if (var1.hasNext()) {
                        break;
                     }

                     if (this.nextIterator == 0) {
                        this.cleanUp();
                        break;
                     }

                     var1 = this.iterables[this.nextIterator].iterator();
                     this.iterators[this.nextIterator] = var1;
                     if (!var1.hasNext()) {
                        this.cleanUp();
                        break;
                     }
                  }
               }

               return this.nextIterator >= 0;
            }
         }

         public Object[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               while(this.nextIterator < this.iterators.length) {
                  this.nextArray[this.nextIterator] = this.iterators[this.nextIterator].next();
                  ++this.nextIterator;
               }

               return this.nextArray.clone();
            }
         }
      }
   }

   static class ArrayToListFunction implements Function {
      private ArrayToListFunction() {
      }

      public List apply(Object[] array) {
         return Arrays.asList(array);
      }
   }
}
