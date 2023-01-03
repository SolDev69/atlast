package net.minecraft.server.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.world.WorldData;

public class ToggleDownfallCommand extends Command {
   @Override
   public String getName() {
      return "toggledownfall";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.downfall.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      this.toggleDownfall();
      sendSuccess(source, this, "commands.downfall.success", new Object[0]);
   }

   protected void toggleDownfall() {
      WorldData var1 = MinecraftServer.getInstance().worlds[0].getData();
      var1.setRaining(!var1.isRaining());
   }
}
