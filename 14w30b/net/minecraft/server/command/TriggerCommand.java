package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class TriggerCommand extends Command {
   @Override
   public String getName() {
      return "trigger";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.trigger.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 3) {
         throw new IncorrectUsageException("commands.trigger.usage");
      } else {
         ServerPlayerEntity var3;
         if (source instanceof ServerPlayerEntity) {
            var3 = (ServerPlayerEntity)source;
         } else {
            Entity var4 = source.asEntity();
            if (!(var4 instanceof ServerPlayerEntity)) {
               throw new CommandException("commands.trigger.invalidPlayer");
            }

            var3 = (ServerPlayerEntity)var4;
         }

         Scoreboard var8 = MinecraftServer.getInstance().getWorld(0).getScoreboard();
         ScoreboardObjective var5 = var8.getObjective(args[0]);
         if (var5 != null && var5.getCriterion() == ScoreboardCriterion.TRIGGER) {
            int var6 = parseInt(args[2]);
            if (!var8.hasScore(var3.getName(), var5)) {
               throw new CommandException("commands.trigger.invalidObjective", args[0]);
            } else {
               ScoreboardScore var7 = var8.getScore(var3.getName(), var5);
               if (var7.isLocked()) {
                  throw new CommandException("commands.trigger.disabled", args[0]);
               } else {
                  if ("set".equals(args[1])) {
                     var7.set(var6);
                  } else {
                     if (!"add".equals(args[1])) {
                        throw new CommandException("commands.trigger.invalidMode", args[1]);
                     }

                     var7.increase(var6);
                  }

                  var7.setLocked(true);
                  if (var3.interactionManager.isCreative()) {
                     sendSuccess(source, this, "commands.trigger.success", new Object[]{args[0], args[1], args[2]});
                  }
               }
            }
         } else {
            throw new CommandException("commands.trigger.invalidObjective", args[0]);
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         Scoreboard var3 = MinecraftServer.getInstance().getWorld(0).getScoreboard();
         ArrayList var4 = Lists.newArrayList();

         for(ScoreboardObjective var6 : var3.getObjectives()) {
            if (var6.getCriterion() == ScoreboardCriterion.TRIGGER) {
               var4.add(var6.getName());
            }
         }

         return suggestMatching(args, var4.toArray(new String[var4.size()]));
      } else {
         return args.length == 2 ? suggestMatching(args, new String[]{"add", "set"}) : null;
      }
   }
}
