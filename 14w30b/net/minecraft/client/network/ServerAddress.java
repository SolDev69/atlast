package net.minecraft.client.network;

import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerAddress {
   private final String address;
   private final int port;

   private ServerAddress(String address, int port) {
      this.address = address;
      this.port = port;
   }

   public String getAddress() {
      return this.address;
   }

   public int getPort() {
      return this.port;
   }

   public static ServerAddress parse(String address) {
      if (address == null) {
         return null;
      } else {
         String[] var1 = address.split(":");
         if (address.startsWith("[")) {
            int var2 = address.indexOf("]");
            if (var2 > 0) {
               String var3 = address.substring(1, var2);
               String var4 = address.substring(var2 + 1).trim();
               if (var4.startsWith(":") && var4.length() > 0) {
                  var4 = var4.substring(1);
                  var1 = new String[]{var3, var4};
               } else {
                  var1 = new String[]{var3};
               }
            }
         }

         if (var1.length > 2) {
            var1 = new String[]{address};
         }

         String var5 = var1[0];
         int var6 = var1.length > 1 ? getPortOrDefault(var1[1], 25565) : 25565;
         if (var6 == 25565) {
            String[] var8 = parseAddress(var5);
            var5 = var8[0];
            var6 = getPortOrDefault(var8[1], 25565);
         }

         return new ServerAddress(var5, var6);
      }
   }

   private static String[] parseAddress(String address) {
      try {
         String var1 = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable var2 = new Hashtable();
         var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         var2.put("java.naming.provider.url", "dns:");
         var2.put("com.sun.jndi.dns.timeout.retries", "1");
         InitialDirContext var3 = new InitialDirContext(var2);
         Attributes var4 = var3.getAttributes("_minecraft._tcp." + address, new String[]{"SRV"});
         String[] var5 = var4.get("srv").get().toString().split(" ", 4);
         return new String[]{var5[3], var5[2]};
      } catch (Throwable var6) {
         return new String[]{address, Integer.toString(25565)};
      }
   }

   private static int getPortOrDefault(String port, int def) {
      try {
         return Integer.parseInt(port.trim());
      } catch (Exception var3) {
         return def;
      }
   }
}
