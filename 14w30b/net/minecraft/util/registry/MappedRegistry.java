package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedRegistry implements Registry {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final Map entries = this.createMap();

   protected Map createMap() {
      return Maps.newHashMap();
   }

   @Override
   public Object get(Object key) {
      return this.entries.get(key);
   }

   @Override
   public void put(Object key, Object value) {
      Validate.notNull(key);
      Validate.notNull(value);
      if (this.entries.containsKey(key)) {
         LOGGER.debug("Adding duplicate key '" + key + "' to registry");
      }

      this.entries.put(key, value);
   }

   public Set keySet() {
      return Collections.unmodifiableSet(this.entries.keySet());
   }

   public boolean containsKey(Object key) {
      return this.entries.containsKey(key);
   }

   @Override
   public Iterator iterator() {
      return this.entries.values().iterator();
   }
}
