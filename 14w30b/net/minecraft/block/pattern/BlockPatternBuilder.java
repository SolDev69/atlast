package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner CHAR_JOINER = Joiner.on(",");
   private final List pattern = Lists.newArrayList();
   private final Map chars = Maps.newHashMap();
   private int height;
   private int width;

   private BlockPatternBuilder() {
      this.chars.put(' ', Predicates.alwaysTrue());
   }

   public BlockPatternBuilder aisle(String... args) {
      if (!ArrayUtils.isEmpty(args) && !StringUtils.isEmpty(args[0])) {
         if (this.pattern.isEmpty()) {
            this.height = args.length;
            this.width = args[0].length();
         }

         if (args.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + args.length + ")");
         } else {
            for(String var5 : args) {
               if (var5.length() != this.width) {
                  throw new IllegalArgumentException(
                     "Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + var5.length() + ")"
                  );
               }

               for(char var9 : var5.toCharArray()) {
                  if (!this.chars.containsKey(var9)) {
                     this.chars.put(var9, null);
                  }
               }
            }

            this.pattern.add(args);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder with(char chr, Predicate predicate) {
      this.chars.put(chr, predicate);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.buildPattern());
   }

   private Predicate[][][] buildPattern() {
      this.validate();
      Predicate[][][] var1 = (Predicate[][][])Array.newInstance(Predicate.class, this.pattern.size(), this.height, this.width);

      for(int var2 = 0; var2 < this.pattern.size(); ++var2) {
         for(int var3 = 0; var3 < this.height; ++var3) {
            for(int var4 = 0; var4 < this.width; ++var4) {
               var1[var2][var3][var4] = (Predicate)this.chars.get(((String[])this.pattern.get(var2))[var3].charAt(var4));
            }
         }
      }

      return var1;
   }

   private void validate() {
      ArrayList var1 = Lists.newArrayList();

      for(Entry var3 : this.chars.entrySet()) {
         if (var3.getValue() == null) {
            var1.add(var3.getKey());
         }
      }

      if (!var1.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + CHAR_JOINER.join(var1) + " are missing");
      }
   }
}
