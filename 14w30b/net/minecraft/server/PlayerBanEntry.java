package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class PlayerBanEntry extends BanEntry {
   public PlayerBanEntry(GameProfile profile) {
      this(profile, null, null, null, null);
   }

   public PlayerBanEntry(GameProfile profile, Date startDate, String source, Date expirationDate, String reason) {
      super(profile, expirationDate, source, expirationDate, reason);
   }

   public PlayerBanEntry(JsonObject json) {
      super(deserialize(json), json);
   }

   @Override
   protected void serialize(JsonObject json) {
      if (this.getUser() != null) {
         json.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
         json.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(json);
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
