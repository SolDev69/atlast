package net.minecraft.client.options;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftClient client;
   private final List entries = Lists.newArrayList();

   public ServerList(MinecraftClient client) {
      this.client = client;
      this.load();
   }

   public void load() {
      try {
         this.entries.clear();
         NbtCompound var1 = NbtIo.read(new File(this.client.runDir, "servers.dat"));
         if (var1 == null) {
            return;
         }

         NbtList var2 = var1.getList("servers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.entries.add(ServerListEntry.fromNbt(var2.getCompound(var3)));
         }
      } catch (Exception var4) {
         LOGGER.error("Couldn't load server list", var4);
      }
   }

   public void save() {
      try {
         NbtList var1 = new NbtList();

         for(ServerListEntry var3 : this.entries) {
            var1.add(var3.toNbt());
         }

         NbtCompound var5 = new NbtCompound();
         var5.put("servers", var1);
         NbtIo.writeSafe(var5, new File(this.client.runDir, "servers.dat"));
      } catch (Exception var4) {
         LOGGER.error("Couldn't save server list", var4);
      }
   }

   public ServerListEntry get(int index) {
      return (ServerListEntry)this.entries.get(index);
   }

   public void remove(int index) {
      this.entries.remove(index);
   }

   public void add(ServerListEntry entry) {
      this.entries.add(entry);
   }

   public int size() {
      return this.entries.size();
   }

   public void swap(int index1, int index2) {
      ServerListEntry var3 = this.get(index1);
      this.entries.set(index1, this.get(index2));
      this.entries.set(index2, var3);
      this.save();
   }

   public void set(int index, ServerListEntry entry) {
      this.entries.set(index, entry);
   }

   public static void update(ServerListEntry entry) {
      ServerList var1 = new ServerList(MinecraftClient.getInstance());
      var1.load();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ServerListEntry var3 = var1.get(var2);
         if (var3.name.equals(entry.name) && var3.address.equals(entry.address)) {
            var1.set(var2, entry);
            break;
         }
      }

      var1.save();
   }
}
