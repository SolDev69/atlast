package net.minecraft.client.options;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerListEntry {
   public String name;
   public String address;
   public String onlinePlayers;
   public String description;
   public long ping;
   public int protocol = 31;
   public String version = "14w30c";
   public boolean isLoaded;
   public String playerListString;
   private ServerListEntry.ResourcePackStatus resourcePackStatus = ServerListEntry.ResourcePackStatus.PROMPT;
   private String icon;

   public ServerListEntry(String name, String address) {
      this.name = name;
      this.address = address;
   }

   public NbtCompound toNbt() {
      NbtCompound var1 = new NbtCompound();
      var1.putString("name", this.name);
      var1.putString("ip", this.address);
      if (this.icon != null) {
         var1.putString("icon", this.icon);
      }

      if (this.resourcePackStatus == ServerListEntry.ResourcePackStatus.ENABLED) {
         var1.putBoolean("acceptTextures", true);
      } else if (this.resourcePackStatus == ServerListEntry.ResourcePackStatus.DISABLED) {
         var1.putBoolean("acceptTextures", false);
      }

      return var1;
   }

   public ServerListEntry.ResourcePackStatus getResourcePackStatus() {
      return this.resourcePackStatus;
   }

   public void setResourcePackStatus(ServerListEntry.ResourcePackStatus status) {
      this.resourcePackStatus = status;
   }

   public static ServerListEntry fromNbt(NbtCompound nbt) {
      ServerListEntry var1 = new ServerListEntry(nbt.getString("name"), nbt.getString("ip"));
      if (nbt.isType("icon", 8)) {
         var1.setIcon(nbt.getString("icon"));
      }

      if (nbt.isType("acceptTextures", 1)) {
         if (nbt.getBoolean("acceptTextures")) {
            var1.setResourcePackStatus(ServerListEntry.ResourcePackStatus.ENABLED);
         } else {
            var1.setResourcePackStatus(ServerListEntry.ResourcePackStatus.DISABLED);
         }
      } else {
         var1.setResourcePackStatus(ServerListEntry.ResourcePackStatus.PROMPT);
      }

      return var1;
   }

   public String getIcon() {
      return this.icon;
   }

   public void setIcon(String icon) {
      this.icon = icon;
   }

   public void set(ServerListEntry server) {
      this.address = server.address;
      this.name = server.name;
      this.setResourcePackStatus(server.getResourcePackStatus());
      this.icon = server.icon;
   }

   @Environment(EnvType.CLIENT)
   public static enum ResourcePackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Text message;

      private ResourcePackStatus(String id) {
         this.message = new TranslatableText("addServer.resourcePack." + id);
      }

      public Text getMessage() {
         return this.message;
      }
   }
}
