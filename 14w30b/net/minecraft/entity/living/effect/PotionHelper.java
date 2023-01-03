package net.minecraft.entity.living.effect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PotionHelper {
   public static final String UNCRAFTABLE = null;
   public static final String SUGAR = "-0+1-2-3&4-4+13";
   public static final String GHAST_TEAR = "+0-1-2-3&4-4+13";
   public static final String POISON = "-0-1+2-3&4-4+13";
   public static final String FERMENTED_SPIDER_EYE = "-0+3-4+13";
   public static final String GLISTERING_MELON = "+0-1+2-3&4-4+13";
   public static final String BLAZE_POWDER = "+0-1-2+3&4-4+13";
   public static final String MAGMA_CREAM = "+0+1-2-3&4-4+13";
   public static final String REDSTONE = "-5+6-7";
   public static final String GLOWSTONE = "+5-6-7";
   public static final String GUNPOWDER = "+14&13-13";
   public static final String GOLDEN_CARROT = "-0+1+2-3+13&4-4";
   public static final String WATER_BREATHING = "+0-1+2+3+13&4-4";
   public static final String f_78qmeymir = "+0+1-2+3&4-4+13";
   private static final Map RECIPES = Maps.newHashMap();
   private static final Map EFFECT_CACHE = Maps.newHashMap();
   private static final Map COLOR_CACHE = Maps.newHashMap();
   private static final String[] NAMES = new String[]{
      "potion.prefix.mundane",
      "potion.prefix.uninteresting",
      "potion.prefix.bland",
      "potion.prefix.clear",
      "potion.prefix.milky",
      "potion.prefix.diffuse",
      "potion.prefix.artless",
      "potion.prefix.thin",
      "potion.prefix.awkward",
      "potion.prefix.flat",
      "potion.prefix.bulky",
      "potion.prefix.bungling",
      "potion.prefix.buttered",
      "potion.prefix.smooth",
      "potion.prefix.suave",
      "potion.prefix.debonair",
      "potion.prefix.thick",
      "potion.prefix.elegant",
      "potion.prefix.fancy",
      "potion.prefix.charming",
      "potion.prefix.dashing",
      "potion.prefix.refined",
      "potion.prefix.cordial",
      "potion.prefix.sparkling",
      "potion.prefix.potent",
      "potion.prefix.foul",
      "potion.prefix.odorless",
      "potion.prefix.rank",
      "potion.prefix.harsh",
      "potion.prefix.acrid",
      "potion.prefix.gross",
      "potion.prefix.stinky"
   };

   public static boolean hasFlag(int metadata, int flag) {
      return (metadata & 1 << flag) != 0;
   }

   private static int getFlag(int metadata, int flag) {
      return hasFlag(metadata, flag) ? 1 : 0;
   }

   private static int getFlagInverse(int metadata, int flag) {
      return hasFlag(metadata, flag) ? 0 : 1;
   }

   public static int getId(int metadata) {
      return remap(metadata, 5, 4, 3, 2, 1);
   }

   public static int getColor(Collection effects) {
      int var1 = 3694022;
      if (effects != null && !effects.isEmpty()) {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         float var5 = 0.0F;

         for(StatusEffectInstance var7 : effects) {
            if (var7.hasParticles()) {
               int var8 = StatusEffect.BY_ID[var7.getId()].getPotionColor();

               for(int var9 = 0; var9 <= var7.getAmplifier(); ++var9) {
                  var2 += (float)(var8 >> 16 & 0xFF) / 255.0F;
                  var3 += (float)(var8 >> 8 & 0xFF) / 255.0F;
                  var4 += (float)(var8 >> 0 & 0xFF) / 255.0F;
                  ++var5;
               }
            }
         }

         if (var5 == 0.0F) {
            return 0;
         } else {
            var2 = var2 / var5 * 255.0F;
            var3 = var3 / var5 * 255.0F;
            var4 = var4 / var5 * 255.0F;
            return (int)var2 << 16 | (int)var3 << 8 | (int)var4;
         }
      } else {
         return var1;
      }
   }

   public static boolean isAllAmbient(Collection effects) {
      for(StatusEffectInstance var2 : effects) {
         if (!var2.isAmbient()) {
            return false;
         }
      }

      return true;
   }

   @Environment(EnvType.CLIENT)
   public static int getColor(int metadata, boolean ignoreCache) {
      if (!ignoreCache) {
         if (COLOR_CACHE.containsKey(metadata)) {
            return COLOR_CACHE.get(metadata);
         } else {
            int var2 = getColor(getStatusEffects(metadata, false));
            COLOR_CACHE.put(metadata, var2);
            return var2;
         }
      } else {
         return getColor(getStatusEffects(metadata, true));
      }
   }

   public static String getName(int metadata) {
      int var1 = getId(metadata);
      return NAMES[var1];
   }

   private static int updatePotionEffectMetadata(boolean bl, boolean bl2, boolean bl3, int i, int j, int k, int metadata) {
      int var7 = 0;
      if (bl) {
         var7 = getFlagInverse(metadata, j);
      } else if (i != -1) {
         if (i == 0 && getAmountOfOnes(metadata) == j) {
            var7 = 1;
         } else if (i == 1 && getAmountOfOnes(metadata) > j) {
            var7 = 1;
         } else if (i == 2 && getAmountOfOnes(metadata) < j) {
            var7 = 1;
         }
      } else {
         var7 = getFlag(metadata, j);
      }

      if (bl2) {
         var7 *= k;
      }

      if (bl3) {
         var7 *= -1;
      }

      return var7;
   }

   private static int getAmountOfOnes(int i) {
      int var1;
      for(var1 = 0; i > 0; ++var1) {
         i &= i - 1;
      }

      return var1;
   }

   private static int getEffectAmplifier(String recipe, int start, int end, int metadata) {
      if (start < recipe.length() && end >= 0 && start < end) {
         int var4 = recipe.indexOf(124, start);
         if (var4 >= 0 && var4 < end) {
            int var17 = getEffectAmplifier(recipe, start, var4 - 1, metadata);
            if (var17 > 0) {
               return var17;
            } else {
               int var19 = getEffectAmplifier(recipe, var4 + 1, end, metadata);
               return var19 > 0 ? var19 : 0;
            }
         } else {
            int var5 = recipe.indexOf(38, start);
            if (var5 >= 0 && var5 < end) {
               int var18 = getEffectAmplifier(recipe, start, var5 - 1, metadata);
               if (var18 <= 0) {
                  return 0;
               } else {
                  int var20 = getEffectAmplifier(recipe, var5 + 1, end, metadata);
                  if (var20 <= 0) {
                     return 0;
                  } else {
                     return var18 > var20 ? var18 : var20;
                  }
               }
            } else {
               boolean var6 = false;
               boolean var7 = false;
               boolean var8 = false;
               boolean var9 = false;
               boolean var10 = false;
               byte var11 = -1;
               int var12 = 0;
               int var13 = 0;
               int var14 = 0;

               for(int var15 = start; var15 < end; ++var15) {
                  char var16 = recipe.charAt(var15);
                  if (var16 >= '0' && var16 <= '9') {
                     if (var6) {
                        var13 = var16 - '0';
                        var7 = true;
                     } else {
                        var12 *= 10;
                        var12 += var16 - '0';
                        var8 = true;
                     }
                  } else if (var16 == '*') {
                     var6 = true;
                  } else if (var16 == '!') {
                     if (var8) {
                        var14 += updatePotionEffectMetadata(var9, var7, var10, var11, var12, var13, metadata);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     var9 = true;
                  } else if (var16 == '-') {
                     if (var8) {
                        var14 += updatePotionEffectMetadata(var9, var7, var10, var11, var12, var13, metadata);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     var10 = true;
                  } else if (var16 != '=' && var16 != '<' && var16 != '>') {
                     if (var16 == '+' && var8) {
                        var14 += updatePotionEffectMetadata(var9, var7, var10, var11, var12, var13, metadata);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }
                  } else {
                     if (var8) {
                        var14 += updatePotionEffectMetadata(var9, var7, var10, var11, var12, var13, metadata);
                        var9 = false;
                        var10 = false;
                        var6 = false;
                        var7 = false;
                        var8 = false;
                        var13 = 0;
                        var12 = 0;
                        var11 = -1;
                     }

                     if (var16 == '=') {
                        var11 = 0;
                     } else if (var16 == '<') {
                        var11 = 2;
                     } else if (var16 == '>') {
                        var11 = 1;
                     }
                  }
               }

               if (var8) {
                  var14 += updatePotionEffectMetadata(var9, var7, var10, var11, var12, var13, metadata);
               }

               return var14;
            }
         }
      } else {
         return 0;
      }
   }

   public static List getStatusEffects(int metadata, boolean ignoreUsability) {
      ArrayList var2 = null;

      for(StatusEffect var6 : StatusEffect.BY_ID) {
         if (var6 != null && (!var6.isUsable() || ignoreUsability)) {
            String var7 = (String)RECIPES.get(var6.getId());
            if (var7 != null) {
               int var8 = getEffectAmplifier(var7, 0, var7.length(), metadata);
               if (var8 > 0) {
                  int var9 = 0;
                  String var10 = (String)EFFECT_CACHE.get(var6.getId());
                  if (var10 != null) {
                     var9 = getEffectAmplifier(var10, 0, var10.length(), metadata);
                     if (var9 < 0) {
                        var9 = 0;
                     }
                  }

                  if (var6.isInstant()) {
                     var8 = 1;
                  } else {
                     var8 = 1200 * (var8 * 3 + (var8 - 1) * 2);
                     var8 >>= var9;
                     var8 = (int)Math.round((double)var8 * var6.getEffectiveness());
                     if ((metadata & 16384) != 0) {
                        var8 = (int)Math.round((double)var8 * 0.75 + 0.5);
                     }
                  }

                  if (var2 == null) {
                     var2 = Lists.newArrayList();
                  }

                  StatusEffectInstance var11 = new StatusEffectInstance(var6.getId(), var8, var9);
                  if ((metadata & 16384) != 0) {
                     var11.setSplash(true);
                  }

                  var2.add(var11);
               }
            }
         }
      }

      return var2;
   }

   private static int updateMetadata(int metadata, int flag, boolean remove, boolean toggle, boolean clear) {
      if (clear) {
         if (!hasFlag(metadata, flag)) {
            return 0;
         }
      } else if (remove) {
         metadata &= ~(1 << flag);
      } else if (toggle) {
         if ((metadata & 1 << flag) == 0) {
            metadata |= 1 << flag;
         } else {
            metadata &= ~(1 << flag);
         }
      } else {
         metadata |= 1 << flag;
      }

      return metadata;
   }

   public static int updateMetadata(int metadata, String recipe) {
      byte var2 = 0;
      int var3 = recipe.length();
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      int var8 = 0;

      for(int var9 = var2; var9 < var3; ++var9) {
         char var10 = recipe.charAt(var9);
         if (var10 >= '0' && var10 <= '9') {
            var8 *= 10;
            var8 += var10 - '0';
            var4 = true;
         } else if (var10 == '!') {
            if (var4) {
               metadata = updateMetadata(metadata, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var5 = true;
         } else if (var10 == '-') {
            if (var4) {
               metadata = updateMetadata(metadata, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var6 = true;
         } else if (var10 == '+') {
            if (var4) {
               metadata = updateMetadata(metadata, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }
         } else if (var10 == '&') {
            if (var4) {
               metadata = updateMetadata(metadata, var8, var6, var5, var7);
               var7 = false;
               var5 = false;
               var6 = false;
               var4 = false;
               var8 = 0;
            }

            var7 = true;
         }
      }

      if (var4) {
         metadata = updateMetadata(metadata, var8, var6, var5, var7);
      }

      return metadata & 32767;
   }

   public static int remap(int metadata, int flag1, int flag2, int flag3, int flag4, int flag5) {
      return (hasFlag(metadata, flag1) ? 16 : 0)
         | (hasFlag(metadata, flag2) ? 8 : 0)
         | (hasFlag(metadata, flag3) ? 4 : 0)
         | (hasFlag(metadata, flag4) ? 2 : 0)
         | (hasFlag(metadata, flag5) ? 1 : 0);
   }

   static {
      RECIPES.put(StatusEffect.REGENERATION.getId(), "0 & !1 & !2 & !3 & 0+6");
      RECIPES.put(StatusEffect.SPEED.getId(), "!0 & 1 & !2 & !3 & 1+6");
      RECIPES.put(StatusEffect.FIRE_RESISTANCE.getId(), "0 & 1 & !2 & !3 & 0+6");
      RECIPES.put(StatusEffect.INSTANT_HEALTH.getId(), "0 & !1 & 2 & !3");
      RECIPES.put(StatusEffect.POISON.getId(), "!0 & !1 & 2 & !3 & 2+6");
      RECIPES.put(StatusEffect.WEAKNESS.getId(), "!0 & !1 & !2 & 3 & 3+6");
      RECIPES.put(StatusEffect.INSTANT_DAMAGE.getId(), "!0 & !1 & 2 & 3");
      RECIPES.put(StatusEffect.SLOWNESS.getId(), "!0 & 1 & !2 & 3 & 3+6");
      RECIPES.put(StatusEffect.STRENGTH.getId(), "0 & !1 & !2 & 3 & 3+6");
      RECIPES.put(StatusEffect.NIGHTVISION.getId(), "!0 & 1 & 2 & !3 & 2+6");
      RECIPES.put(StatusEffect.INVISIBILITY.getId(), "!0 & 1 & 2 & 3 & 2+6");
      RECIPES.put(StatusEffect.WATER_BREATHING.getId(), "0 & !1 & 2 & 3 & 2+6");
      RECIPES.put(StatusEffect.JUMP_BOOST.getId(), "0 & 1 & !2 & 3");
      EFFECT_CACHE.put(StatusEffect.SPEED.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.HASTE.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.STRENGTH.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.REGENERATION.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.INSTANT_DAMAGE.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.INSTANT_HEALTH.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.RESISTANCE.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.POISON.getId(), "5");
      EFFECT_CACHE.put(StatusEffect.JUMP_BOOST.getId(), "5");
   }
}
