package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapBuilder {
   public static Map linkedHashMap(Iterable keys, Iterable values) {
      return map(keys, values, Maps.newLinkedHashMap());
   }

   public static Map map(Iterable keys, Iterable values, Map map) {
      Iterator var3 = values.iterator();

      for(Object var5 : keys) {
         map.put(var5, var3.next());
      }

      if (var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return map;
      }
   }
}
