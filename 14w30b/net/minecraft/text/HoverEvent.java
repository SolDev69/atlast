package net.minecraft.text;

import com.google.common.collect.Maps;
import java.util.Map;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final Text value;

   public HoverEvent(HoverEvent.Action action, Text value) {
      this.action = action;
      this.value = value;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public Text getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         HoverEvent var2 = (HoverEvent)obj;
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
      return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.action.hashCode();
      return 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
   }

   public static enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ACHIEVEMENT("show_achievement", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

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

      public static HoverEvent.Action byId(String id) {
         return (HoverEvent.Action)BY_ID.get(id);
      }

      static {
         for(HoverEvent.Action var3 : values()) {
            BY_ID.put(var3.getId(), var3);
         }
      }
   }
}
