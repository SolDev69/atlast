package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List locations = Lists.newArrayList();
   private final List times = Lists.newArrayList();
   public boolean isProfiling;
   private String currentLocation = "";
   private final Map locationToTime = Maps.newHashMap();

   public void reset() {
      this.locationToTime.clear();
      this.currentLocation = "";
      this.locations.clear();
   }

   public void push(String location) {
      if (this.isProfiling) {
         if (this.currentLocation.length() > 0) {
            this.currentLocation = this.currentLocation + ".";
         }

         this.currentLocation = this.currentLocation + location;
         this.locations.add(this.currentLocation);
         this.times.add(System.nanoTime());
      }
   }

   public void pop() {
      if (this.isProfiling) {
         long var1 = System.nanoTime();
         long var3 = this.times.remove(this.times.size() - 1);
         this.locations.remove(this.locations.size() - 1);
         long var5 = var1 - var3;
         if (this.locationToTime.containsKey(this.currentLocation)) {
            this.locationToTime.put(this.currentLocation, this.locationToTime.get(this.currentLocation) + var5);
         } else {
            this.locationToTime.put(this.currentLocation, var5);
         }

         if (var5 > 100000000L) {
            LOGGER.warn("Something's taking too long! '" + this.currentLocation + "' took aprox " + (double)var5 / 1000000.0 + " ms");
         }

         this.currentLocation = !this.locations.isEmpty() ? (String)this.locations.get(this.locations.size() - 1) : "";
      }
   }

   public List getResults(String location) {
      if (!this.isProfiling) {
         return null;
      } else {
         String var2 = location;
         long var3 = this.locationToTime.containsKey("root") ? this.locationToTime.get("root") : 0L;
         long var5 = this.locationToTime.containsKey(location) ? this.locationToTime.get(location) : -1L;
         ArrayList var7 = Lists.newArrayList();
         if (location.length() > 0) {
            location = location + ".";
         }

         long var8 = 0L;

         for(String var11 : this.locationToTime.keySet()) {
            if (var11.length() > location.length() && var11.startsWith(location) && var11.indexOf(".", location.length() + 1) < 0) {
               var8 += this.locationToTime.get(var11);
            }
         }

         float var20 = (float)var8;
         if (var8 < var5) {
            var8 = var5;
         }

         if (var3 < var8) {
            var3 = var8;
         }

         for(String var12 : this.locationToTime.keySet()) {
            if (var12.length() > location.length() && var12.startsWith(location) && var12.indexOf(".", location.length() + 1) < 0) {
               long var13 = this.locationToTime.get(var12);
               double var15 = (double)var13 * 100.0 / (double)var8;
               double var17 = (double)var13 * 100.0 / (double)var3;
               String var19 = var12.substring(location.length());
               var7.add(new Profiler.Result(var19, var15, var17));
            }
         }

         for(String var23 : this.locationToTime.keySet()) {
            this.locationToTime.put(var23, this.locationToTime.get(var23) * 999L / 1000L);
         }

         if ((float)var8 > var20) {
            var7.add(
               new Profiler.Result("unspecified", (double)((float)var8 - var20) * 100.0 / (double)var8, (double)((float)var8 - var20) * 100.0 / (double)var3)
            );
         }

         Collections.sort(var7);
         var7.add(0, new Profiler.Result(var2, 100.0, (double)var8 * 100.0 / (double)var3));
         return var7;
      }
   }

   public void swap(String location) {
      this.pop();
      this.push(location);
   }

   public String getCurrentLocation() {
      return this.locations.size() == 0 ? "[UNKNOWN]" : (String)this.locations.get(this.locations.size() - 1);
   }

   public static final class Result implements Comparable {
      public double percentageOfParent;
      public double percentageOfTotal;
      public String location;

      public Result(String location, double percentageOfParent, double percentageOfTotal) {
         this.location = location;
         this.percentageOfParent = percentageOfParent;
         this.percentageOfTotal = percentageOfTotal;
      }

      public int compareTo(Profiler.Result c_69kmnkjjo) {
         if (c_69kmnkjjo.percentageOfParent < this.percentageOfParent) {
            return -1;
         } else {
            return c_69kmnkjjo.percentageOfParent > this.percentageOfParent ? 1 : c_69kmnkjjo.location.compareTo(this.location);
         }
      }

      @Environment(EnvType.CLIENT)
      @Override
      public int hashCode() {
         return (this.location.hashCode() & 11184810) + 4473924;
      }
   }
}
