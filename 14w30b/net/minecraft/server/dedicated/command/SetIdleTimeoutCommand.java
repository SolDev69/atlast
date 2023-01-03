package net.minecraft.server.dedicated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;

public class SetIdleTimeoutCommand extends Command {
   @Override
   public String getName() {
      return "setidletimeout";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.setidletimeout.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length != 1) {
         throw new IncorrectUsageException("commands.setidletimeout.usage");
      } else {
         int var3 = parseInt(args[0], 0);
         MinecraftServer.getInstance().setPlayerIdleTimeout(var3);
         sendSuccess(source, this, "commands.setidletimeout.success", new Object[]{var3});
      }
   }
}
