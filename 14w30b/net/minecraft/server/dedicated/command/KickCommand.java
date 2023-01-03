package net.minecraft.server.dedicated.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class KickCommand extends Command {
   @Override
   public String getName() {
      return "kick";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.kick.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length > 0 && args[0].length() > 1) {
         ServerPlayerEntity var3 = MinecraftServer.getInstance().getPlayerManager().get(args[0]);
         String var4 = "Kicked by an operator.";
         boolean var5 = false;
         if (var3 == null) {
            throw new PlayerNotFoundException();
         } else {
            if (args.length >= 2) {
               var4 = parseText(source, args, 1).buildString();
               var5 = true;
            }

            var3.networkHandler.disconnect(var4);
            if (var5) {
               sendSuccess(source, this, "commands.kick.success.reason", new Object[]{var3.getName(), var4});
            } else {
               sendSuccess(source, this, "commands.kick.success", new Object[]{var3.getName()});
            }
         }
      } else {
         throw new IncorrectUsageException("commands.kick.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length >= 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }
}
