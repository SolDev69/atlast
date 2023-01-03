package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
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
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.living.player.PlayerEntity;
import org.apache.commons.io.IOUtils;

public class PlayerCache {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private final Map playersByName = Maps.newHashMap();
   private final Map playersByUuid = Maps.newHashMap();
   private final LinkedList saveQueue = Lists.newLinkedList();
   private final MinecraftServer server;
   protected final Gson gson;
   private final File file;
   private static final ParameterizedType ENTRY_TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerCache.Entry.class};
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

   public PlayerCache(MinecraftServer server, File file) {
      this.server = server;
      this.file = file;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(PlayerCache.Entry.class, new PlayerCache.Serializer());
      this.gson = var3.create();
      this.load();
   }

   private static GameProfile findProfile(MinecraftServer server, String name) {
      final GameProfile[] var2 = new GameProfile[1];
      ProfileLookupCallback var3 = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile profile) {
            var2[0] = profile;
         }

         public void onProfileLookupFailed(GameProfile profile, Exception exception) {
            var2[0] = null;
         }
      };
      server.getGameProfileRepository().findProfilesByNames(new String[]{name}, Agent.MINECRAFT, var3);
      if (!server.isOnlineMode() && var2[0] == null) {
         UUID var4 = PlayerEntity.getUuid(new GameProfile(null, name));
         GameProfile var5 = new GameProfile(var4, name);
         var3.onProfileLookupSucceeded(var5);
      }

      return var2[0];
   }

   public void add(GameProfile profile) {
      this.add(profile, null);
   }

   private void add(GameProfile profile, Date expirationDate) {
      UUID var3 = profile.getId();
      if (expirationDate == null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTime(new Date());
         var4.add(2, 1);
         expirationDate = var4.getTime();
      }

      String var7 = profile.getName().toLowerCase(Locale.ROOT);
      PlayerCache.Entry var5 = new PlayerCache.Entry(profile, expirationDate);
      if (this.playersByUuid.containsKey(var3)) {
         PlayerCache.Entry var6 = (PlayerCache.Entry)this.playersByUuid.get(var3);
         this.playersByName.remove(var6.getProfile().getName().toLowerCase(Locale.ROOT));
         this.playersByName.put(profile.getName().toLowerCase(Locale.ROOT), var5);
         this.saveQueue.remove(profile);
      } else {
         this.playersByUuid.put(var3, var5);
         this.playersByName.put(var7, var5);
      }

      this.saveQueue.addFirst(profile);
   }

   public GameProfile remove(String name) {
      String var2 = name.toLowerCase(Locale.ROOT);
      PlayerCache.Entry var3 = (PlayerCache.Entry)this.playersByName.get(var2);
      if (var3 != null && new Date().getTime() >= var3.expirationDate.getTime()) {
         this.playersByUuid.remove(var3.getProfile().getId());
         this.playersByName.remove(var3.getProfile().getName().toLowerCase(Locale.ROOT));
         this.saveQueue.remove(var3.getProfile());
         var3 = null;
      }

      if (var3 != null) {
         GameProfile var4 = var3.getProfile();
         this.saveQueue.remove(var4);
         this.saveQueue.addFirst(var4);
      } else {
         GameProfile var5 = findProfile(this.server, var2);
         if (var5 != null) {
            this.add(var5);
            var3 = (PlayerCache.Entry)this.playersByName.get(var2);
         }
      }

      this.save();
      return var3 == null ? null : var3.getProfile();
   }

   public String[] getNames() {
      ArrayList var1 = Lists.newArrayList(this.playersByName.keySet());
      return var1.toArray(new String[var1.size()]);
   }

   public GameProfile getProfile(UUID uuid) {
      PlayerCache.Entry var2 = (PlayerCache.Entry)this.playersByUuid.get(uuid);
      return var2 == null ? null : var2.getProfile();
   }

   private PlayerCache.Entry get(UUID uuid) {
      PlayerCache.Entry var2 = (PlayerCache.Entry)this.playersByUuid.get(uuid);
      if (var2 != null) {
         GameProfile var3 = var2.getProfile();
         this.saveQueue.remove(var3);
         this.saveQueue.addFirst(var3);
      }

      return var2;
   }

   public void load() {
      Object var1 = null;
      BufferedReader var2 = null;

      label53: {
         try {
            var2 = Files.newReader(this.file, Charsets.UTF_8);
            var9 = (List)this.gson.fromJson(var2, ENTRY_TYPE);
            break label53;
         } catch (FileNotFoundException var7) {
         } finally {
            IOUtils.closeQuietly(var2);
         }

         return;
      }

      if (var9 != null) {
         this.playersByName.clear();
         this.playersByUuid.clear();
         this.saveQueue.clear();

         for(PlayerCache.Entry var4 : Lists.reverse(var9)) {
            if (var4 != null) {
               this.add(var4.getProfile(), var4.getExpirationDate());
            }
         }
      }
   }

   public void save() {
      String var1 = this.gson.toJson(this.getEntries(1000));
      BufferedWriter var2 = null;

      try {
         var2 = Files.newWriter(this.file, Charsets.UTF_8);
         var2.write(var1);
         return;
      } catch (FileNotFoundException var8) {
         return;
      } catch (IOException var9) {
      } finally {
         IOUtils.closeQuietly(var2);
      }
   }

   private List getEntries(int amount) {
      ArrayList var2 = Lists.newArrayList();

      for(GameProfile var5 : Lists.newArrayList(Iterators.limit(this.saveQueue.iterator(), amount))) {
         PlayerCache.Entry var6 = this.get(var5.getId());
         if (var6 != null) {
            var2.add(var6);
         }
      }

      return var2;
   }

   class Entry {
      private final GameProfile profile;
      private final Date expirationDate;

      private Entry(GameProfile profile, Date expirationDate) {
         this.profile = profile;
         this.expirationDate = expirationDate;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }
   }

   class Serializer implements JsonDeserializer, JsonSerializer {
      private Serializer() {
      }

      public JsonElement serialize(PlayerCache.Entry c_53kehutrm, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("name", c_53kehutrm.getProfile().getName());
         UUID var5 = c_53kehutrm.getProfile().getId();
         var4.addProperty("uuid", var5 == null ? "" : var5.toString());
         var4.addProperty("expiresOn", PlayerCache.DATE_FORMAT.format(c_53kehutrm.getExpirationDate()));
         return var4;
      }

      public PlayerCache.Entry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         if (jsonElement.isJsonObject()) {
            JsonObject var4 = jsonElement.getAsJsonObject();
            JsonElement var5 = var4.get("name");
            JsonElement var6 = var4.get("uuid");
            JsonElement var7 = var4.get("expiresOn");
            if (var5 != null && var6 != null) {
               String var8 = var6.getAsString();
               String var9 = var5.getAsString();
               Date var10 = null;
               if (var7 != null) {
                  try {
                     var10 = PlayerCache.DATE_FORMAT.parse(var7.getAsString());
                  } catch (ParseException var14) {
                     var10 = null;
                  }
               }

               if (var9 != null && var8 != null) {
                  UUID var11;
                  try {
                     var11 = UUID.fromString(var8);
                  } catch (Throwable var13) {
                     return null;
                  }

                  return PlayerCache.this.new Entry(new GameProfile(var11, var9), var10);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
