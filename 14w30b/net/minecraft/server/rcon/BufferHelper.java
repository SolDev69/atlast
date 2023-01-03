package net.minecraft.server.rcon;

import com.google.common.base.Charsets;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class BufferHelper {
   public static final char[] HEX_CHARS_LOOKUP = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String getString(byte[] buf, int pos, int max) {
      int var3 = max - 1;
      int var4 = pos > var3 ? var3 : pos;

      while(0 != buf[var4] && var4 < var3) {
         ++var4;
      }

      return new String(buf, pos, var4 - pos, Charsets.UTF_8);
   }

   public static int getIntLE(byte[] buf, int start) {
      return getIntLE(buf, start, buf.length);
   }

   public static int getIntLE(byte[] buf, int start, int limit) {
      return 0 > limit - start - 4 ? 0 : buf[start + 3] << 24 | (buf[start + 2] & 0xFF) << 16 | (buf[start + 1] & 0xFF) << 8 | buf[start] & 0xFF;
   }

   public static int getIntBE(byte[] buf, int start, int limit) {
      return 0 > limit - start - 4 ? 0 : buf[start] << 24 | (buf[start + 1] & 0xFF) << 16 | (buf[start + 2] & 0xFF) << 8 | buf[start + 3] & 0xFF;
   }

   public static String toHex(byte b) {
      return "" + HEX_CHARS_LOOKUP[(b & 240) >>> 4] + HEX_CHARS_LOOKUP[b & 15];
   }
}