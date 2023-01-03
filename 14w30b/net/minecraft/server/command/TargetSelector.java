package net.minecraft.server.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class TargetSelector {
   private static final Pattern TARGET_SELECTOR_PATTERN = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern VALUE_PATTERN = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern ARG_PATTERN = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
   private static final Set POS_ARGS = Sets.newHashSet(new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

   public static ServerPlayerEntity selectFirstPlayer(CommandSource source, String s) {
      return (ServerPlayerEntity)selectFirst(source, s, ServerPlayerEntity.class);
   }

   public static Entity selectFirst(CommandSource source, String s, Class type) {
      List var3 = select(source, s, type);
      return var3.size() == 1 ? (Entity)var3.get(0) : null;
   }

   public static Text getSelectionAsText(CommandSource source, String s) {
      List var2 = select(source, s, Entity.class);
      if (var2.isEmpty()) {
         return null;
      } else {
         ArrayList var3 = Lists.newArrayList();

         for(Entity var5 : var2) {
            var3.add(var5.getDisplayName());
         }

         return Command.listText(var3);
      }
   }

   public static List select(CommandSource source, String s, Class type) {
      Matcher var3 = TARGET_SELECTOR_PATTERN.matcher(s);
      if (var3.matches() && source.canUseCommand(1, "@")) {
         Map var4 = parseArgs(var3.group(2));
         String var5 = var3.group(1);
         final BlockPos var6 = parsePos(var4, source.getSourceBlockPos());
         final int var7 = parseInt(var4, "rm", -1);
         final int var8 = parseInt(var4, "r", -1);
         int var9 = parseInt(var4, "dx", -1);
         int var10 = parseInt(var4, "dy", -1);
         int var11 = parseInt(var4, "dz", -1);
         int var12 = parseInt(var4, "c", !var5.equals("a") && !var5.equals("e") ? 1 : 0);
         final int var13 = parseInt(var4, "m", WorldSettings.GameMode.NOT_SET.getIndex());
         final int var14 = parseInt(var4, "lm", -1);
         final int var15 = parseInt(var4, "l", -1);
         final Map var16 = parseScoreboardScores(var4);
         String var17 = getArg(var4, "type");
         String var18 = getArg(var4, "name");
         String var19 = getArg(var4, "team");
         final boolean var20 = var18 != null && var18.startsWith("!");
         final boolean var21 = var19 != null && var19.startsWith("!");
         final boolean var22 = var17 != null && var17.startsWith("!");
         if (var20) {
            var18 = var18.substring(1);
         }

         if (var21) {
            var19 = var19.substring(1);
         }

         if (var22) {
            var17 = var17.substring(1);
         }

         final String var23 = var18;
         final String var24 = var19;
         final String var25 = var17;
         ArrayList var26 = Lists.newArrayList();
         if (hasPosArg(var4)) {
            var26.add(source.getSourceWorld());
         } else {
            Collections.addAll(var26, MinecraftServer.getInstance().worlds);
         }

         Object var27 = Lists.newArrayList();

         for(World var29 : var26) {
            if (var29 != null) {
               ArrayList var30 = Lists.newArrayList();
               if (var17 != null && var5.equals("e")) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        String var2 = Entities.getId(c_47ldwddrb);
                        if (var2 == null && c_47ldwddrb instanceof PlayerEntity) {
                           var2 = "Player";
                        }

                        return var25.equals(var2) != var22;
                     }
                  });
               } else if (!var5.equals("e")) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        return c_47ldwddrb instanceof PlayerEntity;
                     }
                  });
               }

               if (var14 > -1 || var15 > -1) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        if (!(c_47ldwddrb instanceof ServerPlayerEntity)) {
                           return false;
                        } else {
                           ServerPlayerEntity var2 = (ServerPlayerEntity)c_47ldwddrb;
                           return (var14 <= -1 || var2.xpLevel >= var14) && (var15 <= -1 || var2.xpLevel <= var15);
                        }
                     }
                  });
               }

               if (var13 != WorldSettings.GameMode.NOT_SET.getIndex()) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        if (!(c_47ldwddrb instanceof ServerPlayerEntity)) {
                           return false;
                        } else {
                           ServerPlayerEntity var2 = (ServerPlayerEntity)c_47ldwddrb;
                           return var2.interactionManager.getGameMode().getIndex() == var13;
                        }
                     }
                  });
               }

               if (var19 != null) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        if (!(c_47ldwddrb instanceof LivingEntity)) {
                           return false;
                        } else {
                           LivingEntity var2 = (LivingEntity)c_47ldwddrb;
                           AbstractTeam var3 = var2.getScoreboardTeam();
                           String var4 = var3 == null ? "" : var3.getName();
                           return var4.equals(var24) != var21;
                        }
                     }
                  });
               }

               if (var16 != null && var16.size() > 0) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        Scoreboard var2 = MinecraftServer.getInstance().getWorld(0).getScoreboard();

                        for(Entry var4 : var16.entrySet()) {
                           String var5 = (String)var4.getKey();
                           boolean var6 = false;
                           if (var5.endsWith("_min") && var5.length() > 4) {
                              var6 = true;
                              var5 = var5.substring(0, var5.length() - 4);
                           }

                           ScoreboardObjective var7 = var2.getObjective(var5);
                           if (var7 == null) {
                              return false;
                           }

                           String var8 = c_47ldwddrb instanceof ServerPlayerEntity ? c_47ldwddrb.getName() : c_47ldwddrb.getUuid().toString();
                           if (!var2.hasScore(var8, var7)) {
                              return false;
                           }

                           ScoreboardScore var9 = var2.getScore(var8, var7);
                           int var10 = var9.get();
                           if (var10 < var4.getValue() && var6) {
                              return false;
                           }

                           if (var10 > var4.getValue() && !var6) {
                              return false;
                           }
                        }

                        return true;
                     }
                  });
               }

               if (var18 != null) {
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        return c_47ldwddrb.getName().equals(var23) != var20;
                     }
                  });
               }

               if (var6 != null && (var7 >= 0 || var8 >= 0)) {
                  final int var31 = var7 * var7;
                  final int var32 = var8 * var8;
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        int var2 = (int)c_47ldwddrb.getSquaredDistanceToCenter(var6);
                        return (var7 < 0 || var2 >= var31) && (var8 < 0 || var2 <= var32);
                     }
                  });
               }

               if (var4.containsKey("rym") || var4.containsKey("ry")) {
                  final int var38 = wrapDegrees(parseInt(var4, "rym", 0));
                  final int var41 = wrapDegrees(parseInt(var4, "ry", 359));
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        int var2 = TargetSelector.wrapDegrees((int)Math.floor((double)c_47ldwddrb.yaw));
                        if (var38 > var41) {
                           return var2 >= var38 || var2 <= var41;
                        } else {
                           return var2 >= var38 && var2 <= var41;
                        }
                     }
                  });
               }

               if (var4.containsKey("rxm") || var4.containsKey("rx")) {
                  final int var39 = wrapDegrees(parseInt(var4, "rxm", 0));
                  final int var42 = wrapDegrees(parseInt(var4, "rx", 359));
                  var30.add(new Predicate() {
                     public boolean apply(Entity c_47ldwddrb) {
                        int var2 = TargetSelector.wrapDegrees((int)Math.floor((double)c_47ldwddrb.pitch));
                        if (var39 > var42) {
                           return var2 >= var39 || var2 <= var42;
                        } else {
                           return var2 >= var39 && var2 <= var42;
                        }
                     }
                  });
               }

               Predicate var40 = Predicates.and(var30);
               Predicate var43 = Predicates.and(EntityFilter.ALIVE, var40);
               if (var6 != null) {
                  int var33 = var29.players.size();
                  int var34 = var29.entities.size();
                  if (var9 >= 0 || var10 >= 0 || var11 >= 0) {
                     final Box var44 = new Box(
                        (double)var6.getX(),
                        (double)var6.getY(),
                        (double)var6.getZ(),
                        (double)(var6.getX() + var9 + 1),
                        (double)(var6.getY() + var10 + 1),
                        (double)(var6.getZ() + var11 + 1)
                     );
                     if (!var5.equals("e") && var33 < var34 / 16) {
                        Predicate var36 = new Predicate() {
                           public boolean apply(Entity c_47ldwddrb) {
                              if (c_47ldwddrb.x < var44.minX || c_47ldwddrb.y < var44.minY || c_47ldwddrb.z < var44.minZ) {
                                 return false;
                              } else {
                                 return !(c_47ldwddrb.x >= var44.maxX) && !(c_47ldwddrb.y >= var44.maxY) && !(c_47ldwddrb.z >= var44.maxZ);
                              }
                           }
                        };
                        var27.addAll(var29.getPlayers(type, Predicates.and(var43, var36)));
                     } else {
                        var27.addAll(var29.getEntities(type, var44, var43));
                     }
                  } else if (var8 >= 0) {
                     Box var35 = new Box(
                        (double)(var6.getX() - var8),
                        (double)(var6.getY() - var8),
                        (double)(var6.getZ() - var8),
                        (double)(var6.getX() + var8 + 1),
                        (double)(var6.getY() + var8 + 1),
                        (double)(var6.getZ() + var8 + 1)
                     );
                     if (!var5.equals("e") && var33 < var34 / 16) {
                        var27.addAll(var29.getPlayers(type, var43));
                     } else {
                        var27.addAll(var29.getEntities(type, var35, var43));
                     }
                  } else if (var5.equals("a")) {
                     var27.addAll(var29.getPlayers(type, var40));
                  } else if (!var5.equals("p") && !var5.equals("r")) {
                     var27.addAll(var29.getEntities(type, var43));
                  } else {
                     var27.addAll(var29.getPlayers(type, var43));
                  }
               } else if (var5.equals("a")) {
                  var27.addAll(var29.getPlayers(type, var40));
               } else if (!var5.equals("p") && !var5.equals("r")) {
                  var27.addAll(var29.getEntities(type, var43));
               } else {
                  var27.addAll(var29.getPlayers(type, var43));
               }
            }
         }

         if (!var5.equals("p") && !var5.equals("a") && !var5.equals("e")) {
            if (var5.equals("r")) {
               Collections.shuffle((List<?>)var27);
            }
         } else if (var6 != null) {
            Collections.sort((List)var27, new Comparator() {
               public int compare(Entity c_47ldwddrb, Entity c_47ldwddrb2) {
                  return ComparisonChain.start().compare(c_47ldwddrb.getSquaredDistanceTo(var6), c_47ldwddrb2.getSquaredDistanceTo(var6)).result();
               }
            });
         }

         Entity var37 = source.asEntity();
         if (var37 != null && type.isAssignableFrom(var37.getClass()) && var12 == 1 && var27.contains(var37) && !"r".equals(var17)) {
            var27 = Lists.newArrayList(new Entity[]{var37});
         }

         if (var12 != 0) {
            if (var12 < 0) {
               Collections.reverse((List<?>)var27);
            }

            var27 = var27.subList(0, Math.min(Math.abs(var12), var27.size()));
         }

         return (List)var27;
      } else {
         return Collections.emptyList();
      }
   }

   public static int wrapDegrees(int degrees) {
      degrees %= 360;
      if (degrees >= 160) {
         degrees -= 360;
      }

      if (degrees < 0) {
         degrees += 360;
      }

      return degrees;
   }

   private static BlockPos parsePos(Map args, BlockPos defaultValue) {
      return new BlockPos(parseInt(args, "x", defaultValue.getX()), parseInt(args, "y", defaultValue.getY()), parseInt(args, "z", defaultValue.getZ()));
   }

   private static boolean hasPosArg(Map args) {
      boolean var1 = false;

      for(String var3 : POS_ARGS) {
         if (args.containsKey(var3)) {
            var1 = true;
         }
      }

      return var1;
   }

   private static int parseInt(Map args, String key, int defaultValue) {
      return args.containsKey(key) ? MathHelper.parseInt((String)args.get(key), defaultValue) : defaultValue;
   }

   private static String getArg(Map args, String key) {
      return (String)args.get(key);
   }

   public static Map parseScoreboardScores(Map args) {
      HashMap var1 = Maps.newHashMap();

      for(String var3 : args.keySet()) {
         if (var3.startsWith("score_") && var3.length() > "score_".length()) {
            var1.put(var3.substring("score_".length()), MathHelper.parseInt((String)args.get(var3), 1));
         }
      }

      return var1;
   }

   public static boolean matchesMultiple(String s) {
      Matcher var1 = TARGET_SELECTOR_PATTERN.matcher(s);
      if (!var1.matches()) {
         return false;
      } else {
         Map var2 = parseArgs(var1.group(2));
         String var3 = var1.group(1);
         return parseInt(var2, "c", !var3.equals("a") && !var3.equals("e") ? 1 : 0) != 1;
      }
   }

   public static boolean matchesRawTarget(String s, String rawTarget) {
      Matcher var2 = TARGET_SELECTOR_PATTERN.matcher(s);
      return var2.matches() && (rawTarget == null || rawTarget.equals(var2.group(1)));
   }

   public static boolean isValid(String s) {
      return matchesRawTarget(s, null);
   }

   private static Map parseArgs(String s) {
      HashMap var1 = Maps.newHashMap();
      if (s == null) {
         return var1;
      } else {
         int var2 = 0;
         int var3 = -1;

         for(Matcher var4 = VALUE_PATTERN.matcher(s); var4.find(); var3 = var4.end()) {
            String var5 = null;
            switch(var2++) {
               case 0:
                  var5 = "x";
                  break;
               case 1:
                  var5 = "y";
                  break;
               case 2:
                  var5 = "z";
                  break;
               case 3:
                  var5 = "r";
            }

            if (var5 != null && var4.group(1).length() > 0) {
               var1.put(var5, var4.group(1));
            }
         }

         if (var3 < s.length()) {
            Matcher var6 = ARG_PATTERN.matcher(var3 == -1 ? s : s.substring(var3));

            while(var6.find()) {
               var1.put(var6.group(1), var6.group(2));
            }
         }

         return var1;
      }
   }
}
