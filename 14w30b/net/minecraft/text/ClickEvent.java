package net.minecraft.text;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent(ClickEvent.Action action, String value) {
      this.action = action;
      this.value = value;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         ClickEvent var2 = (ClickEvent)object;
         if (this.action != var2.action) {
            return false;
         } else {
            return this.value != null ? this.value.equals(var2.value) : var2.value == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.action.hashCode();
      return 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
   }

   public static enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      TWITCH_USER_INFO("twitch_user_info", false),
      SUGGEST_COMMAND("suggest_command", true);

      private static final Map BY_ID = Maps.newHashMap();
      private final boolean allowFromRemoteSource;
      private final String id;

      private Action(String id, boolean allowFromRemoteSource) {
         this.id = id;
         this.allowFromRemoteSource = allowFromRemoteSource;
      }

      public boolean allowFromRemoteSource() {
         return this.allowFromRemoteSource;
      }

      public String getId() {
         return this.id;
      }

      public static ClickEvent.Action byId(String id) {
         return (ClickEvent.Action)BY_ID.get(id);
      }

      static {
         for(ClickEvent.Action var3 : values()) {
            BY_ID.put(var3.getId(), var3);
         }
      }
   }
}
