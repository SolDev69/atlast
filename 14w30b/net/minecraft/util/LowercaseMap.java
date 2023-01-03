package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowercaseMap implements Map {
   private final Map delegate = Maps.newLinkedHashMap();

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object key) {
      return this.delegate.containsKey(key);
   }

   @Override
   public Object get(Object key) {
      return this.delegate.get(key.toString().toLowerCase());
   }

   public Object put(String string, Object object) {
      return this.delegate.put(string.toLowerCase(), object);
   }

   @Override
   public Object remove(Object key) {
      return this.delegate.remove(key.toString().toLowerCase());
   }

   @Override
   public void putAll(Map map) {
      for(Entry var3 : map.entrySet()) {
         this.put((String)var3.getKey(), var3.getValue());
      }
   }

   @Override
   public void clear() {
      this.delegate.clear();
   }

   @Override
   public Set keySet() {
      return this.delegate.keySet();
   }

   @Override
   public Collection values() {
      return this.delegate.values();
   }

   @Override
   public Set entrySet() {
      return this.delegate.entrySet();
   }
}
