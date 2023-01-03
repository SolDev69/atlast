package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StoredUserList {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final Gson gson;
   private final File file;
   private final Map users = Maps.newHashMap();
   private boolean enabled = true;
   private static final ParameterizedType ENTRY_TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{StoredUserEntry.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };

   public StoredUserList(File file) {
      this.file = file;
      GsonBuilder var2 = new GsonBuilder().setPrettyPrinting();
      var2.registerTypeHierarchyAdapter(StoredUserEntry.class, new StoredUserList.Serializer());
      this.gson = var2.create();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Environment(EnvType.SERVER)
   public File getFile() {
      return this.file;
   }

   public void add(StoredUserEntry entry) {
      this.users.put(this.getKey(entry.getUser()), entry);

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after adding a user.", var3);
      }
   }

   public StoredUserEntry get(Object user) {
      this.removeExpiredEntries();
      return (StoredUserEntry)this.users.get(this.getKey(user));
   }

   public void remove(Object user) {
      this.users.remove(this.getKey(user));

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after removing a user.", var3);
      }
   }

   public String[] getNames() {
      return this.users.keySet().toArray(new String[this.users.size()]);
   }

   @Environment(EnvType.SERVER)
   public boolean isEmpty() {
      return this.users.size() < 1;
   }

   protected String getKey(Object user) {
      return user.toString();
   }

   protected boolean contains(Object user) {
      return this.users.containsKey(this.getKey(user));
   }

   private void removeExpiredEntries() {
      ArrayList var1 = Lists.newArrayList();

      for(StoredUserEntry var3 : this.users.values()) {
         if (var3.hasExpired()) {
            var1.add(var3.getUser());
         }
      }

      for(Object var5 : var1) {
         this.users.remove(var5);
      }
   }

   protected StoredUserEntry deserialize(JsonObject json) {
      return new StoredUserEntry((Object)null, json);
   }

   protected Map getEntries() {
      return this.users;
   }

   public void save() {
      Collection var1 = this.users.values();
      String var2 = this.gson.toJson(var1);
      BufferedWriter var3 = null;

      try {
         var3 = Files.newWriter(this.file, Charsets.UTF_8);
         var3.write(var2);
      } finally {
         IOUtils.closeQuietly(var3);
      }
   }

   @Environment(EnvType.SERVER)
   public void load() {
      Object var1 = null;
      BufferedReader var2 = null;

      try {
         var2 = Files.newReader(this.file, Charsets.UTF_8);
         var7 = (Collection)this.gson.fromJson(var2, ENTRY_TYPE);
      } finally {
         IOUtils.closeQuietly(var2);
      }

      if (var7 != null) {
         this.users.clear();

         for(StoredUserEntry var4 : var7) {
            if (var4.getUser() != null) {
               this.users.put(this.getKey(var4.getUser()), var4);
            }
         }
      }
   }

   class Serializer implements JsonDeserializer, JsonSerializer {
      private Serializer() {
      }

      public JsonElement serialize(StoredUserEntry c_63qyayzni, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         c_63qyayzni.serialize(var4);
         return var4;
      }

      public StoredUserEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         if (jsonElement.isJsonObject()) {
            JsonObject var4 = jsonElement.getAsJsonObject();
            return StoredUserList.this.deserialize(var4);
         } else {
            return null;
         }
      }
   }
}
