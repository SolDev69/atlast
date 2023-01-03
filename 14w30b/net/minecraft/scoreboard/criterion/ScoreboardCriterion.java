package net.minecraft.scoreboard.criterion;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.text.Formatting;

public interface ScoreboardCriterion {
   Map BY_NAME = Maps.newHashMap();
   ScoreboardCriterion DUMMY = new GenericCriterion("dummy");
   ScoreboardCriterion TRIGGER = new GenericCriterion("trigger");
   ScoreboardCriterion DEATH_COUNT = new GenericCriterion("deathCount");
   ScoreboardCriterion PLAYER_KILL_COUNT = new GenericCriterion("playerKillCount");
   ScoreboardCriterion TOTAL_KILL_COUNT = new GenericCriterion("totalKillCount");
   ScoreboardCriterion HEALTH = new HealthCriterion("health");
   ScoreboardCriterion[] TEAM_KILL_BY_COLOR = new ScoreboardCriterion[]{
      new ColoredCriterion("teamkill.", Formatting.BLACK),
      new ColoredCriterion("teamkill.", Formatting.DARK_BLUE),
      new ColoredCriterion("teamkill.", Formatting.DARK_GREEN),
      new ColoredCriterion("teamkill.", Formatting.DARK_AQUA),
      new ColoredCriterion("teamkill.", Formatting.DARK_RED),
      new ColoredCriterion("teamkill.", Formatting.DARK_PURPLE),
      new ColoredCriterion("teamkill.", Formatting.GOLD),
      new ColoredCriterion("teamkill.", Formatting.GRAY),
      new ColoredCriterion("teamkill.", Formatting.DARK_GRAY),
      new ColoredCriterion("teamkill.", Formatting.BLUE),
      new ColoredCriterion("teamkill.", Formatting.GREEN),
      new ColoredCriterion("teamkill.", Formatting.AQUA),
      new ColoredCriterion("teamkill.", Formatting.RED),
      new ColoredCriterion("teamkill.", Formatting.LIGHT_PURPLE),
      new ColoredCriterion("teamkill.", Formatting.YELLOW),
      new ColoredCriterion("teamkill.", Formatting.WHITE)
   };
   ScoreboardCriterion[] KILLED_BY_TEAM_BY_COLOR = new ScoreboardCriterion[]{
      new ColoredCriterion("killedByTeam.", Formatting.BLACK),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_BLUE),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_GREEN),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_AQUA),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_RED),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_PURPLE),
      new ColoredCriterion("killedByTeam.", Formatting.GOLD),
      new ColoredCriterion("killedByTeam.", Formatting.GRAY),
      new ColoredCriterion("killedByTeam.", Formatting.DARK_GRAY),
      new ColoredCriterion("killedByTeam.", Formatting.BLUE),
      new ColoredCriterion("killedByTeam.", Formatting.GREEN),
      new ColoredCriterion("killedByTeam.", Formatting.AQUA),
      new ColoredCriterion("killedByTeam.", Formatting.RED),
      new ColoredCriterion("killedByTeam.", Formatting.LIGHT_PURPLE),
      new ColoredCriterion("killedByTeam.", Formatting.YELLOW),
      new ColoredCriterion("killedByTeam.", Formatting.WHITE)
   };

   String getName();

   int countScore(List owners);

   boolean isReadOnly();

   ScoreboardCriterion.RenderType getRenderType();

   public static enum RenderType {
      INTEGER("integer"),
      HEARTS("hearts");

      private static final Map BY_NAME = Maps.newHashMap();
      private final String name;

      private RenderType(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static ScoreboardCriterion.RenderType byName(String name) {
         ScoreboardCriterion.RenderType var1 = (ScoreboardCriterion.RenderType)BY_NAME.get(name);
         return var1 == null ? INTEGER : var1;
      }

      static {
         for(ScoreboardCriterion.RenderType var3 : values()) {
            BY_NAME.put(var3.getName(), var3);
         }
      }
   }
}
