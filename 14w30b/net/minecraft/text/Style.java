package net.minecraft.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Style {
   private Style parent;
   private Formatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private String insertion;
   private static final Style ROOT = new Style() {
      @Override
      public Formatting getColor() {
         return null;
      }

      @Override
      public boolean isBold() {
         return false;
      }

      @Override
      public boolean isItalic() {
         return false;
      }

      @Override
      public boolean isStrikethrough() {
         return false;
      }

      @Override
      public boolean isUnderlined() {
         return false;
      }

      @Override
      public boolean isObfuscated() {
         return false;
      }

      @Override
      public ClickEvent getClickEvent() {
         return null;
      }

      @Override
      public HoverEvent getHoverEvent() {
         return null;
      }

      @Override
      public String getInsertion() {
         return null;
      }

      @Override
      public Style setColor(Formatting color) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setBold(Boolean bold) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setItalic(Boolean italic) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setStrikethrough(Boolean strikethrough) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setUnderlined(Boolean underlined) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setObfuscated(Boolean obfuscated) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setClickEvent(ClickEvent clickEvent) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setHoverEvent(HoverEvent clickEvent) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Style setParent(Style parent) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return "Style.ROOT";
      }

      @Override
      public Style deepCopy() {
         return this;
      }

      @Override
      public Style copy() {
         return this;
      }

      @Environment(EnvType.CLIENT)
      @Override
      public String asString() {
         return "";
      }
   };

   public Formatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public boolean isBold() {
      return this.bold == null ? this.getParent().isBold() : this.bold;
   }

   public boolean isItalic() {
      return this.italic == null ? this.getParent().isItalic() : this.italic;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == null ? this.getParent().isStrikethrough() : this.strikethrough;
   }

   public boolean isUnderlined() {
      return this.underlined == null ? this.getParent().isUnderlined() : this.underlined;
   }

   public boolean isObfuscated() {
      return this.obfuscated == null ? this.getParent().isObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null
         && this.italic == null
         && this.strikethrough == null
         && this.underlined == null
         && this.obfuscated == null
         && this.color == null
         && this.clickEvent == null
         && this.hoverEvent == null;
   }

   public ClickEvent getClickEvent() {
      return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
   }

   public HoverEvent getHoverEvent() {
      return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
   }

   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public Style setColor(Formatting color) {
      this.color = color;
      return this;
   }

   public Style setBold(Boolean bold) {
      this.bold = bold;
      return this;
   }

   public Style setItalic(Boolean italic) {
      this.italic = italic;
      return this;
   }

   public Style setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
      return this;
   }

   public Style setUnderlined(Boolean underlined) {
      this.underlined = underlined;
      return this;
   }

   public Style setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
      return this;
   }

   public Style setClickEvent(ClickEvent clickEvent) {
      this.clickEvent = clickEvent;
      return this;
   }

   public Style setHoverEvent(HoverEvent clickEvent) {
      this.hoverEvent = clickEvent;
      return this;
   }

   public Style setInsertion(String insertion) {
      this.insertion = insertion;
      return this;
   }

   public Style setParent(Style parent) {
      this.parent = parent;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public String asString() {
      if (this.isEmpty()) {
         return this.parent != null ? this.parent.asString() : "";
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.getColor() != null) {
            var1.append(this.getColor());
         }

         if (this.isBold()) {
            var1.append(Formatting.BOLD);
         }

         if (this.isItalic()) {
            var1.append(Formatting.ITALIC);
         }

         if (this.isUnderlined()) {
            var1.append(Formatting.UNDERLINE);
         }

         if (this.isObfuscated()) {
            var1.append(Formatting.OBFUSCATED);
         }

         if (this.isStrikethrough()) {
            var1.append(Formatting.STRIKETHROUGH);
         }

         return var1.toString();
      }
   }

   private Style getParent() {
      return this.parent == null ? ROOT : this.parent;
   }

   @Override
   public String toString() {
      return "Style{hasParent="
         + (this.parent != null)
         + ", color="
         + this.color
         + ", bold="
         + this.bold
         + ", italic="
         + this.italic
         + ", underlined="
         + this.underlined
         + ", obfuscated="
         + this.obfuscated
         + ", clickEvent="
         + this.getClickEvent()
         + ", hoverEvent="
         + this.getHoverEvent()
         + ", insertion="
         + this.getInsertion()
         + '}';
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Style)) {
         return false;
      } else {
         Style var2 = (Style)obj;
         return this.isBold() == var2.isBold()
            && this.getColor() == var2.getColor()
            && this.isItalic() == var2.isItalic()
            && this.isObfuscated() == var2.isObfuscated()
            && this.isStrikethrough() == var2.isStrikethrough()
            && this.isUnderlined() == var2.isUnderlined()
            && (this.getClickEvent() != null ? this.getClickEvent().equals(var2.getClickEvent()) : var2.getClickEvent() == null)
            && (this.getHoverEvent() != null ? this.getHoverEvent().equals(var2.getHoverEvent()) : var2.getHoverEvent() == null)
            && (this.getInsertion() != null ? this.getInsertion().equals(var2.getInsertion()) : var2.getInsertion() == null);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.color.hashCode();
      var1 = 31 * var1 + this.bold.hashCode();
      var1 = 31 * var1 + this.italic.hashCode();
      var1 = 31 * var1 + this.underlined.hashCode();
      var1 = 31 * var1 + this.strikethrough.hashCode();
      var1 = 31 * var1 + this.obfuscated.hashCode();
      var1 = 31 * var1 + this.clickEvent.hashCode();
      var1 = 31 * var1 + this.hoverEvent.hashCode();
      return 31 * var1 + this.insertion.hashCode();
   }

   public Style deepCopy() {
      Style var1 = new Style();
      var1.bold = this.bold;
      var1.italic = this.italic;
      var1.strikethrough = this.strikethrough;
      var1.underlined = this.underlined;
      var1.obfuscated = this.obfuscated;
      var1.color = this.color;
      var1.clickEvent = this.clickEvent;
      var1.hoverEvent = this.hoverEvent;
      var1.parent = this.parent;
      var1.insertion = this.insertion;
      return var1;
   }

   public Style copy() {
      Style var1 = new Style();
      var1.setBold(this.isBold());
      var1.setItalic(this.isItalic());
      var1.setStrikethrough(this.isStrikethrough());
      var1.setUnderlined(this.isUnderlined());
      var1.setObfuscated(this.isObfuscated());
      var1.setColor(this.getColor());
      var1.setClickEvent(this.getClickEvent());
      var1.setHoverEvent(this.getHoverEvent());
      var1.setInsertion(this.getInsertion());
      return var1;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         if (jsonElement.isJsonObject()) {
            Style var4 = new Style();
            JsonObject var5 = jsonElement.getAsJsonObject();
            if (var5 == null) {
               return null;
            } else {
               if (var5.has("bold")) {
                  var4.bold = var5.get("bold").getAsBoolean();
               }

               if (var5.has("italic")) {
                  var4.italic = var5.get("italic").getAsBoolean();
               }

               if (var5.has("underlined")) {
                  var4.underlined = var5.get("underlined").getAsBoolean();
               }

               if (var5.has("strikethrough")) {
                  var4.strikethrough = var5.get("strikethrough").getAsBoolean();
               }

               if (var5.has("obfuscated")) {
                  var4.obfuscated = var5.get("obfuscated").getAsBoolean();
               }

               if (var5.has("color")) {
                  var4.color = (Formatting)jsonDeserializationContext.deserialize(var5.get("color"), Formatting.class);
               }

               if (var5.has("insertion")) {
                  var4.insertion = var5.get("insertion").getAsString();
               }

               if (var5.has("clickEvent")) {
                  JsonObject var6 = var5.getAsJsonObject("clickEvent");
                  if (var6 != null) {
                     JsonPrimitive var7 = var6.getAsJsonPrimitive("action");
                     ClickEvent.Action var8 = var7 == null ? null : ClickEvent.Action.byId(var7.getAsString());
                     JsonPrimitive var9 = var6.getAsJsonPrimitive("value");
                     String var10 = var9 == null ? null : var9.getAsString();
                     if (var8 != null && var10 != null && var8.allowFromRemoteSource()) {
                        var4.clickEvent = new ClickEvent(var8, var10);
                     }
                  }
               }

               if (var5.has("hoverEvent")) {
                  JsonObject var11 = var5.getAsJsonObject("hoverEvent");
                  if (var11 != null) {
                     JsonPrimitive var12 = var11.getAsJsonPrimitive("action");
                     HoverEvent.Action var13 = var12 == null ? null : HoverEvent.Action.byId(var12.getAsString());
                     Text var14 = (Text)jsonDeserializationContext.deserialize(var11.get("value"), Text.class);
                     if (var13 != null && var14 != null && var13.allowFromRemoteSource()) {
                        var4.hoverEvent = new HoverEvent(var13, var14);
                     }
                  }
               }

               return var4;
            }
         } else {
            return null;
         }
      }

      public JsonElement serialize(Style c_79hwjdsnl, Type type, JsonSerializationContext jsonSerializationContext) {
         if (c_79hwjdsnl.isEmpty()) {
            return null;
         } else {
            JsonObject var4 = new JsonObject();
            if (c_79hwjdsnl.bold != null) {
               var4.addProperty("bold", c_79hwjdsnl.bold);
            }

            if (c_79hwjdsnl.italic != null) {
               var4.addProperty("italic", c_79hwjdsnl.italic);
            }

            if (c_79hwjdsnl.underlined != null) {
               var4.addProperty("underlined", c_79hwjdsnl.underlined);
            }

            if (c_79hwjdsnl.strikethrough != null) {
               var4.addProperty("strikethrough", c_79hwjdsnl.strikethrough);
            }

            if (c_79hwjdsnl.obfuscated != null) {
               var4.addProperty("obfuscated", c_79hwjdsnl.obfuscated);
            }

            if (c_79hwjdsnl.color != null) {
               var4.add("color", jsonSerializationContext.serialize(c_79hwjdsnl.color));
            }

            if (c_79hwjdsnl.insertion != null) {
               var4.add("insertion", jsonSerializationContext.serialize(c_79hwjdsnl.insertion));
            }

            if (c_79hwjdsnl.clickEvent != null) {
               JsonObject var5 = new JsonObject();
               var5.addProperty("action", c_79hwjdsnl.clickEvent.getAction().getId());
               var5.addProperty("value", c_79hwjdsnl.clickEvent.getValue());
               var4.add("clickEvent", var5);
            }

            if (c_79hwjdsnl.hoverEvent != null) {
               JsonObject var6 = new JsonObject();
               var6.addProperty("action", c_79hwjdsnl.hoverEvent.getAction().getId());
               var6.add("value", jsonSerializationContext.serialize(c_79hwjdsnl.hoverEvent.getValue()));
               var4.add("hoverEvent", var6);
            }

            return var4;
         }
      }
   }
}
