package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.util.HashMap;
import java.util.Locale;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {
   public TypeAdapter create(Gson gson, TypeToken token) {
      Class var3 = token.getRawType();
      if (!var3.isEnum()) {
         return null;
      } else {
         final HashMap var4 = Maps.newHashMap();

         for(Object var8 : var3.getEnumConstants()) {
            var4.put(this.getKey(var8), var8);
         }

         return new TypeAdapter() {
            public void write(JsonWriter jw, Object obj) {
               if (obj == null) {
                  jw.nullValue();
               } else {
                  jw.value(LowercaseEnumTypeAdapterFactory.this.getKey(obj));
               }
            }

            public Object read(JsonReader jr) {
               if (jr.peek() == JsonToken.NULL) {
                  jr.nextNull();
                  return null;
               } else {
                  return var4.get(jr.nextString());
               }
            }
         };
      }
   }

   private String getKey(Object obj) {
      return obj instanceof Enum ? ((Enum)obj).name().toLowerCase(Locale.US) : obj.toString().toLowerCase(Locale.US);
   }
}
