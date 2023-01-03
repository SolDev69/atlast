package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class Ops extends StoredUserList {
   public Ops(File file) {
      super(file);
   }

   @Override
   protected StoredUserEntry deserialize(JsonObject json) {
      return new OpEntry(json);
   }

   @Override
   public String[] getNames() {
      String[] var1 = new String[this.getEntries().size()];
      int var2 = 0;

      for(OpEntry var4 : this.getEntries().values()) {
         var1[var2++] = ((GameProfile)var4.getUser()).getName();
      }

      return var1;
   }

   protected String getKey(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   public GameProfile getPlayer(String name) {
      for(OpEntry var3 : this.getEntries().values()) {
         if (name.equalsIgnoreCase(((GameProfile)var3.getUser()).getName())) {
            return (GameProfile)var3.getUser();
         }
      }

      return null;
   }
}
