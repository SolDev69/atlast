package net.minecraft.server.dedicated.command;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;

public class PardonIpCommand extends Command {
   @Override
   public String getName() {
      return "pardon-ip";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canUse(CommandSource source) {
      return MinecraftServer.getInstance().getPlayerManager().getIpBans().isEnabled() && super.canUse(source);
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.unbanip.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length == 1 && args[0].length() > 1) {
         Matcher var3 = BanIpCommand.REGEX_PATTERN.matcher(args[0]);
         if (var3.matches()) {
            MinecraftServer.getInstance().getPlayerManager().getIpBans().remove(args[0]);
            sendSuccess(source, this, "commands.unbanip.success", new Object[]{args[0]});
         } else {
            throw new CommandSyntaxException("commands.unbanip.invalid");
         }
      } else {
         throw new IncorrectUsageException("commands.unbanip.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerManager().getIpBans().getNames()) : null;
   }
}
