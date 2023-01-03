package net.minecraft.text;

import java.util.regex.Pattern;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StringUtils {
   private static final Pattern PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

   @Environment(EnvType.CLIENT)
   public static String getDurationString(int ticks) {
      int var1 = ticks / 20;
      int var2 = var1 / 60;
      var1 %= 60;
      return var1 < 10 ? var2 + ":0" + var1 : var2 + ":" + var1;
   }

   @Environment(EnvType.CLIENT)
   public static String stripFormatting(String text) {
      return PATTERN.matcher(text).replaceAll("");
   }

   public static boolean isStringEmpty(String string) {
      return org.apache.commons.lang3.StringUtils.isEmpty(string);
   }
}
