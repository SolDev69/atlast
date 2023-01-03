package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StatsCommand extends Command {
   @Override
   public String getName() {
      return "stats";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.stats.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.stats.usage");
      } else {
         boolean var3;
         if (args[0].equals("entity")) {
            var3 = false;
         } else {
            if (!args[0].equals("block")) {
               throw new IncorrectUsageException("commands.stats.usage");
            }

            var3 = true;
         }

         int var4;
         if (var3) {
            if (args.length < 5) {
               throw new IncorrectUsageException("commands.stats.block.usage");
            }

            var4 = 4;
         } else {
            if (args.length < 3) {
               throw new IncorrectUsageException("commands.stats.entity.usage");
            }

            var4 = 2;
         }

         String var5 = args[var4++];
         if ("set".equals(var5)) {
            if (args.length < var4 + 3) {
               if (var4 == 5) {
                  throw new IncorrectUsageException("commands.stats.block.set.usage");
               }

               throw new IncorrectUsageException("commands.stats.entity.set.usage");
            }
         } else {
            if (!"clear".equals(var5)) {
               throw new IncorrectUsageException("commands.stats.usage");
            }

            if (args.length < var4 + 1) {
               if (var4 == 5) {
                  throw new IncorrectUsageException("commands.stats.block.clear.usage");
               }

               throw new IncorrectUsageException("commands.stats.entity.clear.usage");
            }
         }

         CommandResults.Type var6 = CommandResults.Type.byName(args[var4++]);
         if (var6 == null) {
            throw new CommandException("commands.stats.failed");
         } else {
            World var7 = source.getSourceWorld();
            CommandResults var8;
            if (var3) {
               BlockPos var9 = parseBlockPos(source, args, 1, false);
               BlockEntity var10 = var7.getBlockEntity(var9);
               if (var10 == null) {
                  throw new CommandException("commands.stats.noCompatibleBlock", var9.getX(), var9.getY(), var9.getZ());
               }

               if (var10 instanceof CommandBlockBlockEntity) {
                  var8 = ((CommandBlockBlockEntity)var10).getCommandResults();
               } else {
                  if (!(var10 instanceof SignBlockEntity)) {
                     throw new CommandException("commands.stats.noCompatibleBlock", var9.getX(), var9.getY(), var9.getZ());
                  }

                  var8 = ((SignBlockEntity)var10).getCommandResults();
               }
            } else {
               Entity var14 = parseEntity(source, args[1]);
               var8 = var14.getCommandResults();
            }

            if ("set".equals(var5)) {
               String var15 = args[var4++];
               String var17 = args[var4];
               if (var15.length() == 0 || var17.length() == 0) {
                  throw new CommandException("commands.stats.failed");
               }

               CommandResults.updateSourceAndObjective(var8, var6, var15, var17);
               sendSuccess(source, this, "commands.stats.success", new Object[]{var6.getName(), var17, var15});
            } else if ("clear".equals(var5)) {
               CommandResults.updateSourceAndObjective(var8, var6, null, null);
               sendSuccess(source, this, "commands.stats.cleared", new Object[]{var6.getName()});
            }

            if (var3) {
               BlockPos var16 = parseBlockPos(source, args, 1, false);
               BlockEntity var18 = var7.getBlockEntity(var16);
               var18.markDirty();
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"entity", "block"});
      } else if (args.length == 2 && args[0].equals("entity")) {
         return suggestMatching(args, this.getPlayerNames());
      } else if ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))) {
         if ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block"))) {
            return (args.length != 6 || !args[0].equals("entity")) && (args.length != 8 || !args[0].equals("block"))
               ? null
               : suggestMatching(args, this.getObjectives());
         } else {
            return suggestMatching(args, CommandResults.Type.getNames());
         }
      } else {
         return suggestMatching(args, new String[]{"set", "clear"});
      }
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getInstance().getPlayerNames();
   }

   protected List getObjectives() {
      Collection var1 = MinecraftServer.getInstance().getWorld(0).getScoreboard().getObjectives();
      ArrayList var2 = Lists.newArrayList();

      for(ScoreboardObjective var4 : var1) {
         if (!var4.getCriterion().isReadOnly()) {
            var2.add(var4.getName());
         }
      }

      return var2;
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }
}
