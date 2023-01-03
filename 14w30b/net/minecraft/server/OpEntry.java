package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class OpEntry extends StoredUserEntry {
   private final int permissionLevel;

   public OpEntry(GameProfile profile, int permissionLevel) {
      super(profile);
      this.permissionLevel = permissionLevel;
   }

   public OpEntry(JsonObject json) {
      super(deserialize(json), json);
      this.permissionLevel = json.has("level") ? json.get("level").getAsInt() : 0;
   }

   public int getPermissionLevel() {
      return this.permissionLevel;
   }

   @Override
   protected void serialize(JsonObject json) {
      if (this.getUser() != null) {
         json.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
         json.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(json);
         json.addProperty("level", this.permissionLevel);
      }
   }

   private static GameProfile deserialize(JsonObject json) {
      if (json.has("uuid") && json.has("name")) {
         String var1 = json.get("uuid").getAsString();

         UUID var2;
         try {
            var2 = UUID.fromString(var1);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(var2, json.get("name").getAsString());
      } else {
         return null;
      }
   }
}
