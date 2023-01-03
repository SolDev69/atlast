package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class ForwardingJsonSet extends ForwardingSet implements JsonSet {
   private final Set delegate = Sets.newHashSet();

   @Override
   public void add(JsonElement element) {
      if (element.isJsonArray()) {
         for(JsonElement var3 : element.getAsJsonArray()) {
            this.add(var3.getAsString());
         }
      }
   }

   @Override
   public JsonElement toJson() {
      JsonArray var1 = new JsonArray();

      for(String var3 : this) {
         var1.add(new JsonPrimitive(var3));
      }

      return var1;
   }

   protected Set delegate() {
      return this.delegate;
   }
}
