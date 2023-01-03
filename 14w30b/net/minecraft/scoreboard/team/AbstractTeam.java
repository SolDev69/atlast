package net.minecraft.scoreboard.team;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class AbstractTeam {
   public boolean isEqual(AbstractTeam team) {
      if (team == null) {
         return false;
      } else {
         return this == team;
      }
   }

   public abstract String getName();

   public abstract String getMemberDisplayName(String member);

   @Environment(EnvType.CLIENT)
   public abstract boolean showFriendlyInvisibles();

   public abstract boolean allowFriendlyFire();

   @Environment(EnvType.CLIENT)
   public abstract AbstractTeam.Visibility getNameTagVisibility();

   public abstract Collection getMembers();

   public abstract AbstractTeam.Visibility getDeathMessageVisibility();

   public static enum Visibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static Map BY_NAME = Maps.newHashMap();
      public final String name;
      public final int index;

      public static String[] getNames() {
         return BY_NAME.keySet().toArray(new String[BY_NAME.size()]);
      }

      public static AbstractTeam.Visibility byName(String name) {
         return (AbstractTeam.Visibility)BY_NAME.get(name);
      }

      private Visibility(String name, int index) {
         this.name = name;
         this.index = index;
      }

      static {
         for(AbstractTeam.Visibility var3 : values()) {
            BY_NAME.put(var3.name, var3);
         }
      }
   }
}
