package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringNbtReader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern PRIMITIVE_PATTERN = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

   public static NbtCompound parse(String rawNbt) {
      rawNbt = rawNbt.trim();
      if (!rawNbt.startsWith("{")) {
         throw new NbtException("Invalid tag encountered, expected '{' as first char.");
      } else if (getTopElementCount(rawNbt) != 1) {
         throw new NbtException("Encountered multiple top tags, only one expected");
      } else {
         return (NbtCompound)parseNbtEntry("tag", rawNbt).getValue();
      }
   }

   static int getTopElementCount(String rawNbt) {
      int var1 = 0;
      boolean var2 = false;
      Stack var3 = new Stack();

      for(int var4 = 0; var4 < rawNbt.length(); ++var4) {
         char var5 = rawNbt.charAt(var4);
         if (var5 == '"') {
            if (isEscaped(rawNbt, var4)) {
               if (!var2) {
                  throw new NbtException("Illegal use of \\\": " + rawNbt);
               }
            } else {
               var2 = !var2;
            }
         } else if (!var2) {
            if (var5 != '{' && var5 != '[') {
               if (var5 == '}' && (var3.isEmpty() || var3.pop() != '{')) {
                  throw new NbtException("Unbalanced curly brackets {}: " + rawNbt);
               }

               if (var5 == ']' && (var3.isEmpty() || var3.pop() != '[')) {
                  throw new NbtException("Unbalanced square brackets []: " + rawNbt);
               }
            } else {
               if (var3.isEmpty()) {
                  ++var1;
               }

               var3.push(var5);
            }
         }
      }

      if (var2) {
         throw new NbtException("Unbalanced quotation: " + rawNbt);
      } else if (!var3.isEmpty()) {
         throw new NbtException("Unbalanced brackets: " + rawNbt);
      } else {
         if (var1 == 0 && !rawNbt.isEmpty()) {
            var1 = 1;
         }

         return var1;
      }
   }

   static StringNbtReader.Entry parseNbtEntry(String... rawEntry) {
      return parseNbtEntry(rawEntry[0], rawEntry[1]);
   }

   static StringNbtReader.Entry parseNbtEntry(String key, String rawElement) {
      rawElement = rawElement.trim();
      if (rawElement.startsWith("{")) {
         rawElement = rawElement.substring(1, rawElement.length() - 1);

         StringNbtReader.CompoundEntry var8;
         String var9;
         for(var8 = new StringNbtReader.CompoundEntry(key); rawElement.length() > 0; rawElement = rawElement.substring(var9.length() + 1)) {
            var9 = getNextEntry(rawElement, true);
            if (var9.length() > 0) {
               boolean var11 = false;
               var8.entries.add(parseNbtEntry(var9, var11));
            }

            if (rawElement.length() < var9.length() + 1) {
               break;
            }

            char var12 = rawElement.charAt(var9.length());
            if (var12 != ',' && var12 != '{' && var12 != '}' && var12 != '[' && var12 != ']') {
               throw new NbtException("Unexpected token '" + var12 + "' at: " + rawElement.substring(var9.length()));
            }
         }

         return var8;
      } else if (rawElement.startsWith("[") && !PRIMITIVE_PATTERN.matcher(rawElement).matches()) {
         rawElement = rawElement.substring(1, rawElement.length() - 1);

         StringNbtReader.ListEntry var2;
         String var3;
         for(var2 = new StringNbtReader.ListEntry(key); rawElement.length() > 0; rawElement = rawElement.substring(var3.length() + 1)) {
            var3 = getNextEntry(rawElement, false);
            if (var3.length() > 0) {
               boolean var4 = true;
               var2.entries.add(parseNbtEntry(var3, var4));
            }

            if (rawElement.length() < var3.length() + 1) {
               break;
            }

            char var10 = rawElement.charAt(var3.length());
            if (var10 != ',' && var10 != '{' && var10 != '}' && var10 != '[' && var10 != ']') {
               throw new NbtException("Unexpected token '" + var10 + "' at: " + rawElement.substring(var3.length()));
            }
         }

         return var2;
      } else {
         return new StringNbtReader.PrimitiveEntry(key, rawElement);
      }
   }

   private static StringNbtReader.Entry parseNbtEntry(String rawEntry, boolean allowNoSeparator) {
      String var2 = getKey(rawEntry, allowNoSeparator);
      String var3 = getValue(rawEntry, allowNoSeparator);
      return parseNbtEntry(var2, var3);
   }

   private static String getNextEntry(String rawNbt, boolean expectSeparator) {
      int var2 = findSeparator(rawNbt, ':');
      int var3 = findSeparator(rawNbt, ',');
      if (expectSeparator) {
         if (var2 == -1) {
            throw new NbtException("Unable to locate name/value separator for string: " + rawNbt);
         }

         if (var3 != -1 && var3 < var2) {
            throw new NbtException("Name error at: " + rawNbt);
         }
      } else if (var2 == -1 || var2 > var3) {
         var2 = -1;
      }

      return getNextEntry(rawNbt, var2);
   }

   private static String getNextEntry(String rawNbt, int separatorIndex) {
      Stack var2 = new Stack();
      int var3 = separatorIndex + 1;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;

      for(int var7 = 0; var3 < rawNbt.length(); ++var3) {
         char var8 = rawNbt.charAt(var3);
         if (var8 == '"') {
            if (isEscaped(rawNbt, var3)) {
               if (!var4) {
                  throw new NbtException("Illegal use of \\\": " + rawNbt);
               }
            } else {
               var4 = !var4;
               if (var4 && !var6) {
                  var5 = true;
               }

               if (!var4) {
                  var7 = var3;
               }
            }
         } else if (!var4) {
            if (var8 != '{' && var8 != '[') {
               if (var8 == '}' && (var2.isEmpty() || var2.pop() != '{')) {
                  throw new NbtException("Unbalanced curly brackets {}: " + rawNbt);
               }

               if (var8 == ']' && (var2.isEmpty() || var2.pop() != '[')) {
                  throw new NbtException("Unbalanced square brackets []: " + rawNbt);
               }

               if (var8 == ',' && var2.isEmpty()) {
                  return rawNbt.substring(0, var3);
               }
            } else {
               var2.push(var8);
            }
         }

         if (!Character.isWhitespace(var8)) {
            if (!var4 && var5 && var7 != var3) {
               return rawNbt.substring(0, var7 + 1);
            }

            var6 = true;
         }
      }

      return rawNbt.substring(0, var3);
   }

   private static String getKey(String rawEntry, boolean allowNoSeparator) {
      if (allowNoSeparator) {
         rawEntry = rawEntry.trim();
         if (rawEntry.startsWith("{") || rawEntry.startsWith("[")) {
            return "";
         }
      }

      int var2 = findSeparator(rawEntry, ':');
      if (var2 != -1) {
         return rawEntry.substring(0, var2).trim();
      } else if (allowNoSeparator) {
         return "";
      } else {
         throw new NbtException("Unable to locate name/value separator for string: " + rawEntry);
      }
   }

   private static String getValue(String rawEntry, boolean allowNoSeparator) {
      if (allowNoSeparator) {
         rawEntry = rawEntry.trim();
         if (rawEntry.startsWith("{") || rawEntry.startsWith("[")) {
            return rawEntry;
         }
      }

      int var2 = findSeparator(rawEntry, ':');
      if (var2 != -1) {
         return rawEntry.substring(var2 + 1).trim();
      } else if (allowNoSeparator) {
         return rawEntry;
      } else {
         throw new NbtException("Unable to locate name/value separator for string: " + rawEntry);
      }
   }

   private static int findSeparator(String rawNbt, char chr) {
      int var2 = 0;

      for(boolean var3 = true; var2 < rawNbt.length(); ++var2) {
         char var4 = rawNbt.charAt(var2);
         if (var4 == '"') {
            if (!isEscaped(rawNbt, var2)) {
               var3 = !var3;
            }
         } else if (var3) {
            if (var4 == chr) {
               return var2;
            }

            if (var4 == '{' || var4 == '[') {
               return -1;
            }
         }
      }

      return -1;
   }

   private static boolean isEscaped(String rawNbt, int index) {
      return index > 0 && rawNbt.charAt(index - 1) == '\\';
   }

   static class CompoundEntry extends StringNbtReader.Entry {
      protected List entries = Lists.newArrayList();

      public CompoundEntry(String key) {
         this.key = key;
      }

      @Override
      public NbtElement getValue() {
         NbtCompound var1 = new NbtCompound();

         for(StringNbtReader.Entry var3 : this.entries) {
            var1.put(var3.key, var3.getValue());
         }

         return var1;
      }
   }

   abstract static class Entry {
      protected String key;

      public abstract NbtElement getValue();
   }

   static class ListEntry extends StringNbtReader.Entry {
      protected List entries = Lists.newArrayList();

      public ListEntry(String key) {
         this.key = key;
      }

      @Override
      public NbtElement getValue() {
         NbtList var1 = new NbtList();

         for(StringNbtReader.Entry var3 : this.entries) {
            var1.add(var3.getValue());
         }

         return var1;
      }
   }

   static class PrimitiveEntry extends StringNbtReader.Entry {
      private static final Pattern DOUBLE_WITH_SUFFIX_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
      private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
      private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?[0-9]+[b|B]");
      private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?[0-9]+[l|L]");
      private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?[0-9]+[s|S]");
      private static final Pattern INT_PATTERN = Pattern.compile("[-+]?[0-9]+");
      private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
      private static final Splitter LIST_ELEMENT_SPLITTER = Splitter.on(',').omitEmptyStrings();
      protected String rawValue;

      public PrimitiveEntry(String key, String rawValue) {
         this.key = key;
         this.rawValue = rawValue;
      }

      @Override
      public NbtElement getValue() {
         try {
            if (DOUBLE_WITH_SUFFIX_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtDouble(Double.parseDouble(this.rawValue.substring(0, this.rawValue.length() - 1)));
            }

            if (FLOAT_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtFloat(Float.parseFloat(this.rawValue.substring(0, this.rawValue.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtByte(Byte.parseByte(this.rawValue.substring(0, this.rawValue.length() - 1)));
            }

            if (LONG_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtLong(Long.parseLong(this.rawValue.substring(0, this.rawValue.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtShort(Short.parseShort(this.rawValue.substring(0, this.rawValue.length() - 1)));
            }

            if (INT_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtInt(Integer.parseInt(this.rawValue));
            }

            if (DOUBLE_PATTERN.matcher(this.rawValue).matches()) {
               return new NbtDouble(Double.parseDouble(this.rawValue));
            }

            if (this.rawValue.equalsIgnoreCase("true") || this.rawValue.equalsIgnoreCase("false")) {
               return new NbtByte((byte)(Boolean.parseBoolean(this.rawValue) ? 1 : 0));
            }
         } catch (NumberFormatException var6) {
            this.rawValue = this.rawValue.replaceAll("\\\\\"", "\"");
            return new NbtString(this.rawValue);
         }

         if (this.rawValue.startsWith("[") && this.rawValue.endsWith("]")) {
            String var1 = this.rawValue.substring(1, this.rawValue.length() - 1);
            String[] var2 = (String[])Iterables.toArray(LIST_ELEMENT_SPLITTER.split(var1), String.class);

            try {
               int[] var3 = new int[var2.length];

               for(int var4 = 0; var4 < var2.length; ++var4) {
                  var3[var4] = Integer.parseInt(var2[var4].trim());
               }

               return new NbtIntArray(var3);
            } catch (NumberFormatException var5) {
               return new NbtString(this.rawValue);
            }
         } else {
            if (this.rawValue.startsWith("\"") && this.rawValue.endsWith("\"")) {
               this.rawValue = this.rawValue.substring(1, this.rawValue.length() - 1);
            }

            this.rawValue = this.rawValue.replaceAll("\\\\\"", "\"");
            return new NbtString(this.rawValue);
         }
      }
   }
}
