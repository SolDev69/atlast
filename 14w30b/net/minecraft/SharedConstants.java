package net.minecraft;

import net.minecraft.util.math.Direction;

public class SharedConstants {
   public static final int f_35cytruuz = Direction.WEST.getId();
   public static final int f_65hjhkibg = Direction.DOWN.getId();
   public static final int f_80ionvmpr = Direction.NORTH.getId();
   public static final int f_12bhivnfu = Direction.EAST.getId();
   public static final int f_45rwuiagn = Direction.UP.getId();
   public static final int f_11horuacr = Direction.SOUTH.getId();
   public static final char[] INVALID_FILE_CHARS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   public static boolean isValidChatChar(char chr) {
      return chr != 167 && chr >= ' ' && chr != 127;
   }

   public static String stripInvalidChars(String s) {
      StringBuilder var1 = new StringBuilder();

      for(char var5 : s.toCharArray()) {
         if (isValidChatChar(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }
}
