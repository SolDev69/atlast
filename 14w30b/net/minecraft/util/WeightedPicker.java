package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedPicker {
   public static int getTotalWeight(Collection entries) {
      int var1 = 0;

      for(WeightedPicker.Entry var3 : entries) {
         var1 += var3.weight;
      }

      return var1;
   }

   public static WeightedPicker.Entry pick(Random random, Collection entries, int totalWeight) {
      if (totalWeight <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = random.nextInt(totalWeight);
         return pick(entries, var3);
      }
   }

   public static WeightedPicker.Entry pick(Collection entries, int weight) {
      for(WeightedPicker.Entry var3 : entries) {
         weight -= var3.weight;
         if (weight < 0) {
            return var3;
         }
      }

      return null;
   }

   public static WeightedPicker.Entry pick(Random random, Collection entries) {
      return pick(random, entries, getTotalWeight(entries));
   }

   public static class Entry {
      protected int weight;

      public Entry(int weight) {
         this.weight = weight;
      }
   }
}
