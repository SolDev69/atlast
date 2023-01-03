package net.minecraft.server;

import com.google.gson.JsonObject;

public class StoredUserEntry {
   private final Object player;

   public StoredUserEntry(Object player) {
      this.player = player;
   }

   protected StoredUserEntry(Object user, JsonObject json) {
      this.player = user;
   }

   Object getUser() {
      return this.player;
   }

   boolean hasExpired() {
      return false;
   }

   protected void serialize(JsonObject json) {
   }
}
