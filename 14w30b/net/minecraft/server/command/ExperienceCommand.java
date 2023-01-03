package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class ExperienceCommand extends Command {
   @Override
   public String getName() {
      return "xp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.xp.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length <= 0) {
         throw new IncorrectUsageException("commands.xp.usage");
      } else {
         String var3 = args[0];
         boolean var4 = var3.endsWith("l") || var3.endsWith("L");
         if (var4 && var3.length() > 1) {
            var3 = var3.substring(0, var3.length() - 1);
         }

         int var5 = parseInt(var3);
         boolean var6 = var5 < 0;
         if (var6) {
            var5 *= -1;
         }

         ServerPlayerEntity var7 = args.length > 1 ? parsePlayer(source, args[1]) : asPlayer(source);
         if (var4) {
            source.addResult(CommandResults.Type.QUERY_RESULT, var7.xpLevel);
            if (var6) {
               var7.addXp(-var5);
               sendSuccess(source, this, "commands.xp.success.negative.levels", new Object[]{var5, var7.getName()});
            } else {
               var7.addXp(var5);
               sendSuccess(source, this, "commands.xp.success.levels", new Object[]{var5, var7.getName()});
            }
         } else {
            source.addResult(CommandResults.Type.QUERY_RESULT, var7.xp);
            if (var6) {
               throw new CommandException("commands.xp.failure.widthdrawXp");
            }

            var7.increaseXp(var5);
            sendSuccess(source, this, "commands.xp.success", new Object[]{var5, var7.getName()});
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 2 ? suggestMatching(args, this.getPlayerNames()) : null;
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getInstance().getPlayerNames();
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 1;
   }
}
