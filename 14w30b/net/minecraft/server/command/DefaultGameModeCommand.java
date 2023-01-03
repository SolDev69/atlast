package net.minecraft.server.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.WorldSettings;

public class DefaultGameModeCommand extends GameModeCommand {
   @Override
   public String getName() {
      return "defaultgamemode";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.defaultgamemode.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length <= 0) {
         throw new IncorrectUsageException("commands.defaultgamemode.usage");
      } else {
         WorldSettings.GameMode var3 = this.parseGameMode(source, args[0]);
         this.setDefault(var3);
         sendSuccess(source, this, "commands.defaultgamemode.success", new Object[]{new TranslatableText("gameMode." + var3.getId())});
      }
   }

   protected void setDefault(WorldSettings.GameMode gameMode) {
      MinecraftServer var2 = MinecraftServer.getInstance();
      var2.setDefaultGameMode(gameMode);
      if (var2.shouldForceGameMode()) {
         for(ServerPlayerEntity var4 : MinecraftServer.getInstance().getPlayerManager().players) {
            var4.setGameMode(gameMode);
            var4.fallDistance = 0.0F;
         }
      }
   }
}
