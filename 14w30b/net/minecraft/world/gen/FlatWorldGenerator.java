package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class FlatWorldGenerator {
   private final List layers = Lists.newArrayList();
   private final Map features = Maps.newHashMap();
   private int biomeId;

   public int getBiomeId() {
      return this.biomeId;
   }

   public void setBiomeId(int biomeId) {
      this.biomeId = biomeId;
   }

   public Map getFeatures() {
      return this.features;
   }

   public List getLayers() {
      return this.layers;
   }

   public void processLayers() {
      int var1 = 0;

      for(FlatWorldLayer var3 : this.layers) {
         var3.setY(var1);
         var1 += var3.getSize();
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(3);
      var1.append(";");

      for(int var2 = 0; var2 < this.layers.size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(((FlatWorldLayer)this.layers.get(var2)).toString());
      }

      var1.append(";");
      var1.append(this.biomeId);
      if (!this.features.isEmpty()) {
         var1.append(";");
         int var9 = 0;

         for(Entry var4 : this.features.entrySet()) {
            if (var9++ > 0) {
               var1.append(",");
            }

            var1.append(((String)var4.getKey()).toLowerCase());
            Map var5 = (Map)var4.getValue();
            if (!var5.isEmpty()) {
               var1.append("(");
               int var6 = 0;

               for(Entry var8 : var5.entrySet()) {
                  if (var6++ > 0) {
                     var1.append(" ");
                  }

                  var1.append((String)var8.getKey());
                  var1.append("=");
                  var1.append((String)var8.getValue());
               }

               var1.append(")");
            }
         }
      } else {
         var1.append(";");
      }

      return var1.toString();
   }

   private static FlatWorldLayer parseLayer(int biomeId, String preset, int y) {
      String[] var3 = biomeId >= 3 ? preset.split("\\*", 2) : preset.split("x", 2);
      int var4 = 1;
      int var5 = 0;
      if (var3.length == 2) {
         try {
            var4 = Integer.parseInt(var3[0]);
            if (y + var4 >= 256) {
               var4 = 256 - y;
            }

            if (var4 < 0) {
               var4 = 0;
            }
         } catch (Throwable var8) {
            return null;
         }
      }

      Object var6 = null;

      try {
         String var7 = var3[var3.length - 1];
         if (biomeId < 3) {
            var3 = var7.split(":", 2);
            if (var3.length > 1) {
               var5 = Integer.parseInt(var3[1]);
            }

            var12 = Block.byRawId(Integer.parseInt(var3[0]));
         } else {
            var3 = var7.split(":", 3);
            var12 = var3.length > 1 ? Block.byId(var3[0] + ":" + var3[1]) : null;
            if (var12 != null) {
               var5 = var3.length > 2 ? Integer.parseInt(var3[2]) : 0;
            } else {
               var12 = Block.byId(var3[0]);
               if (var12 != null) {
                  var5 = var3.length > 1 ? Integer.parseInt(var3[1]) : 0;
               }
            }

            if (var12 == null) {
               return null;
            }
         }

         if (var12 == Blocks.AIR) {
            var5 = 0;
         }

         if (var5 < 0 || var5 > 15) {
            var5 = 0;
         }
      } catch (Throwable var9) {
         return null;
      }

      FlatWorldLayer var13 = new FlatWorldLayer(biomeId, var4, var12, var5);
      var13.setY(y);
      return var13;
   }

   private static List getLayers(int biomeId, String preset) {
      if (preset != null && preset.length() >= 1) {
         ArrayList var2 = Lists.newArrayList();
         String[] var3 = preset.split(",");
         int var4 = 0;

         for(String var8 : var3) {
            FlatWorldLayer var9 = parseLayer(biomeId, var8, var4);
            if (var9 == null) {
               return null;
            }

            var2.add(var9);
            var4 += var9.getSize();
         }

         return var2;
      } else {
         return null;
      }
   }

   public static FlatWorldGenerator of(String preset) {
      if (preset == null) {
         return ofDefault();
      } else {
         String[] var1 = preset.split(";", -1);
         int var2 = var1.length == 1 ? 0 : MathHelper.parseInt(var1[0], 0);
         if (var2 >= 0 && var2 <= 3) {
            FlatWorldGenerator var3 = new FlatWorldGenerator();
            int var4 = var1.length == 1 ? 0 : 1;
            List var5 = getLayers(var2, var1[var4++]);
            if (var5 != null && !var5.isEmpty()) {
               var3.getLayers().addAll(var5);
               var3.processLayers();
               int var6 = Biome.PLAINS.id;
               if (var2 > 0 && var1.length > var4) {
                  var6 = MathHelper.parseInt(var1[var4++], var6);
               }

               var3.setBiomeId(var6);
               if (var2 > 0 && var1.length > var4) {
                  String[] var7 = var1[var4++].toLowerCase().split(",");

                  for(String var11 : var7) {
                     String[] var12 = var11.split("\\(", 2);
                     HashMap var13 = Maps.newHashMap();
                     if (var12[0].length() > 0) {
                        var3.getFeatures().put(var12[0], var13);
                        if (var12.length > 1 && var12[1].endsWith(")") && var12[1].length() > 1) {
                           String[] var14 = var12[1].substring(0, var12[1].length() - 1).split(" ");

                           for(int var15 = 0; var15 < var14.length; ++var15) {
                              String[] var16 = var14[var15].split("=", 2);
                              if (var16.length == 2) {
                                 var13.put(var16[0], var16[1]);
                              }
                           }
                        }
                     }
                  }
               } else {
                  var3.getFeatures().put("village", Maps.newHashMap());
               }

               return var3;
            } else {
               return ofDefault();
            }
         } else {
            return ofDefault();
         }
      }
   }

   public static FlatWorldGenerator ofDefault() {
      FlatWorldGenerator var0 = new FlatWorldGenerator();
      var0.setBiomeId(Biome.PLAINS.id);
      var0.getLayers().add(new FlatWorldLayer(1, Blocks.BEDROCK));
      var0.getLayers().add(new FlatWorldLayer(2, Blocks.DIRT));
      var0.getLayers().add(new FlatWorldLayer(1, Blocks.GRASS));
      var0.processLayers();
      var0.getFeatures().put("village", Maps.newHashMap());
      return var0;
   }
}
