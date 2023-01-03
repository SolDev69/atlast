package net.minecraft.server.dedicated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;

public class StopCommand extends Command {
   @Override
   public String getName() {
      return "stop";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.stop.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (MinecraftServer.getInstance().worlds != null) {
         sendSuccess(source, this, "commands.stop.start", new Object[0]);
      }

      MinecraftServer.getInstance().stopRunning();
   }
}
