package net.minecraft.client.twitch;

import net.minecraft.stat.achievement.AchievementStat;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class AchievementMetadata extends StreamMetadata {
   public AchievementMetadata(AchievementStat stat) {
      super("achievement");
      this.put("achievement_id", stat.id);
      this.put("achievement_name", stat.getDecoratedName().buildString());
      this.put("achievement_description", stat.getDescription());
      this.setMessage("Achievement '" + stat.getDecoratedName().buildString() + "' obtained!");
   }
}
