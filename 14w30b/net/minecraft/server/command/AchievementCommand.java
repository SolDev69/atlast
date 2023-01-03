package net.minecraft.server.command;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.stat.achievement.Achievements;

public class AchievementCommand extends Command {
   @Override
   public String getName() {
      return "achievement";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.achievement.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.achievement.usage");
      } else {
         final Stat var3 = Stats.get(args[1]);
         if (var3 == null && !args[1].equals("*")) {
            throw new CommandException("commands.achievement.unknownAchievement", args[1]);
         } else {
            final ServerPlayerEntity var4 = args.length >= 3 ? parsePlayer(source, args[2]) : asPlayer(source);
            boolean var5 = args[0].equalsIgnoreCase("give");
            boolean var6 = args[0].equalsIgnoreCase("take");
            if (var5 || var6) {
               if (var3 == null) {
                  if (var5) {
                     for(AchievementStat var14 : Achievements.ALL) {
                        var4.incrementStat(var14);
                     }

                     sendSuccess(source, this, "commands.achievement.give.success.all", new Object[]{var4.getName()});
                  } else if (var6) {
                     for(AchievementStat var15 : Lists.reverse(Achievements.ALL)) {
                        var4.clearStat(var15);
                     }

                     sendSuccess(source, this, "commands.achievement.take.success.all", new Object[]{var4.getName()});
                  }
               } else {
                  if (var3 instanceof AchievementStat) {
                     AchievementStat var7 = (AchievementStat)var3;
                     if (var5) {
                        if (var4.getStatHandler().hasAchievement(var7)) {
                           throw new CommandException("commands.achievement.alreadyHave", var4.getName(), var3.getNameForChat());
                        }

                        ArrayList var8;
                        for(var8 = Lists.newArrayList(); var7.parent != null && !var4.getStatHandler().hasAchievement(var7.parent); var7 = var7.parent) {
                           var8.add(var7.parent);
                        }

                        for(AchievementStat var10 : Lists.reverse(var8)) {
                           var4.incrementStat(var10);
                        }
                     } else if (var6) {
                        if (!var4.getStatHandler().hasAchievement(var7)) {
                           throw new CommandException("commands.achievement.dontHave", var4.getName(), var3.getNameForChat());
                        }

                        ArrayList var13;
                        for(var13 = Lists.newArrayList(Iterators.filter(Achievements.ALL.iterator(), new Predicate() {
                           public boolean apply(AchievementStat c_14hbkuyvc) {
                              return var4.getStatHandler().hasAchievement(c_14hbkuyvc) && c_14hbkuyvc != var3;
                           }
                        })); var7.parent != null && var4.getStatHandler().hasAchievement(var7.parent); var7 = var7.parent) {
                           var13.remove(var7.parent);
                        }

                        for(AchievementStat var17 : var13) {
                           var4.clearStat(var17);
                        }
                     }
                  }

                  if (var5) {
                     var4.incrementStat(var3);
                     sendSuccess(source, this, "commands.achievement.give.success.one", new Object[]{var4.getName(), var3.getNameForChat()});
                  } else if (var6) {
                     var4.clearStat(var3);
                     sendSuccess(source, this, "commands.achievement.take.success.one", new Object[]{var3.getNameForChat(), var4.getName()});
                  }
               }
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"give", "take"});
      } else if (args.length != 2) {
         return args.length == 3 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
      } else {
         ArrayList var3 = Lists.newArrayList();

         for(Stat var5 : Stats.ALL) {
            var3.add(var5.id);
         }

         return suggestMatching(args, var3);
      }
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 2;
   }
}
