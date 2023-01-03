package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.Id2ObjectBiMap;
import net.minecraft.util.IdObjectIterable;

public class IdRegistry extends MappedRegistry implements IdObjectIterable {
   protected final Id2ObjectBiMap ids = new Id2ObjectBiMap();
   protected final Map keys = ((BiMap)this.entries).inverse();

   public void register(int id, Object key, Object value) {
      this.ids.put(value, id);
      this.put(key, value);
   }

   @Override
   protected Map createMap() {
      return HashBiMap.create();
   }

   @Override
   public Object get(Object key) {
      return super.get(key);
   }

   public Object getKey(Object value) {
      return this.keys.get(value);
   }

   @Override
   public boolean containsKey(Object key) {
      return super.containsKey(key);
   }

   public int getId(Object value) {
      return this.ids.getId(value);
   }

   public Object get(int id) {
      return this.ids.get(id);
   }

   @Override
   public Iterator iterator() {
      return this.ids.iterator();
   }
}
