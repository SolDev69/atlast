package net.minecraft.stat;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.util.JsonIntSerializable;
import net.minecraft.util.JsonSet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StatHandler {
   protected final Map stats = Maps.newConcurrentMap();

   public boolean hasAchievement(AchievementStat achievement) {
      return this.getValue((Stat)achievement) > 0;
   }

   public boolean hasParentAchievement(AchievementStat achievement) {
      return achievement.parent == null || this.hasAchievement(achievement.parent);
   }

   @Environment(EnvType.CLIENT)
   public int getValue(AchievementStat achievement) {
      if (this.hasAchievement(achievement)) {
         return 0;
      } else {
         int var2 = 0;

         for(AchievementStat var3 = achievement.parent; var3 != null && !this.hasAchievement(var3); ++var2) {
            var3 = var3.parent;
         }

         return var2;
      }
   }

   public void trySetValue(PlayerEntity player, Stat stat, int value) {
      if (!stat.isAchievement() || this.hasParentAchievement((AchievementStat)stat)) {
         this.setValue(player, stat, this.getValue(stat) + value);
      }
   }

   public void setValue(PlayerEntity player, Stat stat, int value) {
      JsonIntSerializable var4 = (JsonIntSerializable)this.stats.get(stat);
      if (var4 == null) {
         var4 = new JsonIntSerializable();
         this.stats.put(stat, var4);
      }

      var4.setValue(value);
   }

   public int getValue(Stat stat) {
      JsonIntSerializable var2 = (JsonIntSerializable)this.stats.get(stat);
      return var2 == null ? 0 : var2.getValue();
   }

   public JsonSet getJsonSet(Stat stat) {
      JsonIntSerializable var2 = (JsonIntSerializable)this.stats.get(stat);
      return var2 != null ? var2.getJsonSet() : null;
   }

   public JsonSet setJsonSet(Stat stat, JsonSet set) {
      JsonIntSerializable var3 = (JsonIntSerializable)this.stats.get(stat);
      if (var3 == null) {
         var3 = new JsonIntSerializable();
         this.stats.put(stat, var3);
      }

      var3.setJsonSet(set);
      return set;
   }
}
