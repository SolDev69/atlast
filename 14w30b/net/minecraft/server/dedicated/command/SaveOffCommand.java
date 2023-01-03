package net.minecraft.server.dedicated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;

public class SaveOffCommand extends Command {
   @Override
   public String getName() {
      return "save-off";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.save-off.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      MinecraftServer var3 = MinecraftServer.getInstance();
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.worlds.length; ++var5) {
         if (var3.worlds[var5] != null) {
            ServerWorld var6 = var3.worlds[var5];
            if (!var6.isSaving) {
               var6.isSaving = true;
               var4 = true;
            }
         }
      }

      if (var4) {
         sendSuccess(source, this, "commands.save.disabled", new Object[0]);
      } else {
         throw new CommandException("commands.save-off.alreadyOff");
      }
   }
}
