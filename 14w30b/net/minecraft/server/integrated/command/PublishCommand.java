package net.minecraft.server.integrated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.world.WorldSettings;

public class PublishCommand extends Command {
   @Override
   public String getName() {
      return "publish";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.publish.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      String var3 = MinecraftServer.getInstance().publish(WorldSettings.GameMode.SURVIVAL, false);
      if (var3 != null) {
         sendSuccess(source, this, "commands.publish.started", new Object[]{var3});
      } else {
         sendSuccess(source, this, "commands.publish.failed", new Object[0]);
      }
   }
}
