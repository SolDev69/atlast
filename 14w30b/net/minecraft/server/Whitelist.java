package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Whitelist extends StoredUserList {
   public Whitelist(File file) {
      super(file);
   }

   @Override
   protected StoredUserEntry deserialize(JsonObject json) {
      return new WhitelistEntry(json);
   }

   @Environment(EnvType.SERVER)
   public boolean isWhitelisted(GameProfile profile) {
      return this.contains(profile);
   }

   @Override
   public String[] getNames() {
      String[] var1 = new String[this.getEntries().size()];
      int var2 = 0;

      for(WhitelistEntry var4 : this.getEntries().values()) {
         var1[var2++] = ((GameProfile)var4.getUser()).getName();
      }

      return var1;
   }

   protected String getKey(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   public GameProfile getPlayer(String name) {
      for(WhitelistEntry var3 : this.getEntries().values()) {
         if (name.equalsIgnoreCase(((GameProfile)var3.getUser()).getName())) {
            return (GameProfile)var3.getUser();
         }
      }

      return null;
   }
}
