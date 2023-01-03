package net.minecraft.stat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonIntSerializable;
import net.minecraft.util.JsonSet;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler extends StatHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File file;
   private final Set pendingStats = Sets.newHashSet();
   private int lastStatsUpdate = -300;
   private boolean update = false;

   public ServerStatHandler(MinecraftServer server, File file) {
      this.server = server;
      this.file = file;
   }

   public void load() {
      if (this.file.isFile()) {
         try {
            this.stats.clear();
            this.stats.putAll(this.deserialize(FileUtils.readFileToString(this.file)));
         } catch (IOException var2) {
            LOGGER.error("Couldn't read statistics file " + this.file, var2);
         } catch (JsonParseException var3) {
            LOGGER.error("Couldn't parse statistics file " + this.file, var3);
         }
      }
   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, serialize(this.stats));
      } catch (IOException var2) {
         LOGGER.error("Couldn't save stats", var2);
      }
   }

   @Override
   public void setValue(PlayerEntity player, Stat stat, int value) {
      int var4 = stat.isAchievement() ? this.getValue(stat) : 0;
      super.setValue(player, stat, value);
      this.pendingStats.add(stat);
      if (stat.isAchievement() && var4 == 0 && value > 0) {
         this.update = true;
         if (this.server.shouldAnnouncePlayerAchievements()) {
            this.server.getPlayerManager().sendSystemMessage(new TranslatableText("chat.type.achievement", player.getDisplayName(), stat.getNameForChat()));
         }
      }

      if (stat.isAchievement() && var4 > 0 && value == 0) {
         this.update = true;
         if (this.server.shouldAnnouncePlayerAchievements()) {
            this.server
               .getPlayerManager()
               .sendSystemMessage(new TranslatableText("chat.type.achievement.taken", player.getDisplayName(), stat.getNameForChat()));
         }
      }
   }

   public Set takePendingStats() {
      HashSet var1 = Sets.newHashSet(this.pendingStats);
      this.pendingStats.clear();
      this.update = false;
      return var1;
   }

   public Map deserialize(String name) {
      JsonElement var2 = new JsonParser().parse(name);
      if (!var2.isJsonObject()) {
         return Maps.newHashMap();
      } else {
         JsonObject var3 = var2.getAsJsonObject();
         HashMap var4 = Maps.newHashMap();

         for(Entry var6 : var3.entrySet()) {
            Stat var7 = Stats.get((String)var6.getKey());
            if (var7 != null) {
               JsonIntSerializable var8 = new JsonIntSerializable();
               if (((JsonElement)var6.getValue()).isJsonPrimitive() && ((JsonElement)var6.getValue()).getAsJsonPrimitive().isNumber()) {
                  var8.setValue(((JsonElement)var6.getValue()).getAsInt());
               } else if (((JsonElement)var6.getValue()).isJsonObject()) {
                  JsonObject var9 = ((JsonElement)var6.getValue()).getAsJsonObject();
                  if (var9.has("value") && var9.get("value").isJsonPrimitive() && var9.get("value").getAsJsonPrimitive().isNumber()) {
                     var8.setValue(var9.getAsJsonPrimitive("value").getAsInt());
                  }

                  if (var9.has("progress") && var7.getDataType() != null) {
                     try {
                        Constructor var10 = var7.getDataType().getConstructor();
                        JsonSet var11 = (JsonSet)var10.newInstance();
                        var11.add(var9.get("progress"));
                        var8.setJsonSet(var11);
                     } catch (Throwable var12) {
                        LOGGER.warn("Invalid statistic progress in " + this.file, var12);
                     }
                  }
               }

               var4.put(var7, var8);
            } else {
               LOGGER.warn("Invalid statistic in " + this.file + ": Don't know what " + (String)var6.getKey() + " is");
            }
         }

         return var4;
      }
   }

   public static String serialize(Map stats) {
      JsonObject var1 = new JsonObject();

      for(Entry var3 : stats.entrySet()) {
         if (((JsonIntSerializable)var3.getValue()).getJsonSet() != null) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("value", ((JsonIntSerializable)var3.getValue()).getValue());

            try {
               var4.add("progress", ((JsonIntSerializable)var3.getValue()).getJsonSet().toJson());
            } catch (Throwable var6) {
               LOGGER.warn("Couldn't save statistic " + ((Stat)var3.getKey()).getDecoratedName() + ": error serializing progress", var6);
            }

            var1.add(((Stat)var3.getKey()).id, var4);
         } else {
            var1.addProperty(((Stat)var3.getKey()).id, ((JsonIntSerializable)var3.getValue()).getValue());
         }
      }

      return var1.toString();
   }

   public void updateStatSet() {
      for(Stat var2 : this.stats.keySet()) {
         this.pendingStats.add(var2);
      }
   }

   public void sendStats(ServerPlayerEntity player) {
      int var2 = this.server.getTicks();
      HashMap var3 = Maps.newHashMap();
      if (this.update || var2 - this.lastStatsUpdate > 300) {
         this.lastStatsUpdate = var2;

         for(Stat var5 : this.takePendingStats()) {
            var3.put(var5, this.getValue(var5));
         }
      }

      player.networkHandler.sendPacket(new StatisticsS2CPacket(var3));
   }

   public void sendAchievements(ServerPlayerEntity player) {
      HashMap var2 = Maps.newHashMap();

      for(AchievementStat var4 : Achievements.ALL) {
         if (this.hasAchievement(var4)) {
            var2.put(var4, this.getValue((Stat)var4));
            this.pendingStats.remove(var4);
         }
      }

      player.networkHandler.sendPacket(new StatisticsS2CPacket(var2));
   }

   public boolean shouldUpdate() {
      return this.update;
   }
}
