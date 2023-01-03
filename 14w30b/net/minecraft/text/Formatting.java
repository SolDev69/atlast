package net.minecraft.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public enum Formatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   private static final Map BY_NAME = Maps.newHashMap();
   private static final Pattern CODE_PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final String name;
   private final char code;
   private final boolean modifier;
   private final String stringRepresentation;
   private final int index;

   private static String condenseName(String name) {
      return name.toLowerCase().replaceAll("[^a-z]", "");
   }

   private Formatting(String name, char code, int index) {
      this(name, code, false, index);
   }

   private Formatting(String name, char code, boolean modifier) {
      this(name, code, modifier, -1);
   }

   private Formatting(String name, char code, boolean modifier, int index) {
      this.name = name;
      this.code = code;
      this.modifier = modifier;
      this.index = index;
      this.stringRepresentation = "§" + code;
   }

   public int getIndex() {
      return this.index;
   }

   public boolean isModifier() {
      return this.modifier;
   }

   public boolean isColor() {
      return !this.modifier && this != RESET;
   }

   public String getName() {
      return this.name().toLowerCase();
   }

   @Override
   public String toString() {
      return this.stringRepresentation;
   }

   @Environment(EnvType.CLIENT)
   public static String strip(String string) {
      return string == null ? null : CODE_PATTERN.matcher(string).replaceAll("");
   }

   public static Formatting byName(String name) {
      return name == null ? null : (Formatting)BY_NAME.get(condenseName(name));
   }

   public static Formatting byIndex(int index) {
      if (index < 0) {
         return RESET;
      } else {
         for(Formatting var4 : values()) {
            if (var4.getIndex() == index) {
               return var4;
            }
         }

         return null;
      }
   }

   public static Collection getNames(boolean colors, boolean modifiers) {
      ArrayList var2 = Lists.newArrayList();

      for(Formatting var6 : values()) {
         if ((!var6.isColor() || colors) && (!var6.isModifier() || modifiers)) {
            var2.add(var6.getName());
         }
      }

      return var2;
   }

   static {
      for(Formatting var3 : values()) {
         BY_NAME.put(condenseName(var3.name), var3);
      }
   }
}
