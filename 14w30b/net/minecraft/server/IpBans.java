package net.minecraft.server;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class IpBans extends StoredUserList {
   public IpBans(File file) {
      super(file);
   }

   @Override
   protected StoredUserEntry deserialize(JsonObject json) {
      return new IpBanEntry(json);
   }

   public boolean isBanned(SocketAddress adress) {
      String var2 = this.getIp(adress);
      return this.contains(var2);
   }

   public IpBanEntry get(SocketAddress address) {
      String var2 = this.getIp(address);
      return (IpBanEntry)this.get(var2);
   }

   private String getIp(SocketAddress address) {
      String var2 = address.toString();
      if (var2.contains("/")) {
         var2 = var2.substring(var2.indexOf(47) + 1);
      }

      if (var2.contains(":")) {
         var2 = var2.substring(0, var2.indexOf(58));
      }

      return var2;
   }
}
