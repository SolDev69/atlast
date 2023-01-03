package net.minecraft.client;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Session {
   private final String username;
   private final String uuid;
   private final String accessToken;
   private final Session.Type type;

   public Session(String username, String uuid, String accessToken, String type) {
      this.username = username;
      this.uuid = uuid;
      this.accessToken = accessToken;
      this.type = Session.Type.byId(type);
   }

   public String getSessionId() {
      return "token:" + this.accessToken + ":" + this.uuid;
   }

   public String getUuid() {
      return this.uuid;
   }

   public String getUsername() {
      return this.username;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public GameProfile getProfile() {
      try {
         UUID var1 = UUIDTypeAdapter.fromString(this.getUuid());
         return new GameProfile(var1, this.getUsername());
      } catch (IllegalArgumentException var2) {
         return new GameProfile(null, this.getUsername());
      }
   }

   public Session.Type getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map BY_ID = Maps.newHashMap();
      private final String id;

      private Type(String id) {
         this.id = id;
      }

      public static Session.Type byId(String id) {
         return (Session.Type)BY_ID.get(id.toLowerCase());
      }

      static {
         for(Session.Type var3 : values()) {
            BY_ID.put(var3.id, var3);
         }
      }
   }
}
