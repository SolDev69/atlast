package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;

public class PardonCommand extends Command {
   @Override
   public String getName() {
      return "pardon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.unban.usage";
   }

   @Override
   public boolean canUse(CommandSource source) {
      return MinecraftServer.getInstance().getPlayerManager().getPlayerBans().isEnabled() && super.canUse(source);
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length == 1 && args[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getInstance();
         GameProfile var4 = var3.getPlayerManager().getPlayerBans().getPlayer(args[0]);
         if (var4 == null) {
            throw new CommandException("commands.unban.failed", args[0]);
         } else {
            var3.getPlayerManager().getPlayerBans().remove(var4);
            sendSuccess(source, this, "commands.unban.success", new Object[]{args[0]});
         }
      } else {
         throw new IncorrectUsageException("commands.unban.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerManager().getPlayerBans().getNames()) : null;
   }
}
