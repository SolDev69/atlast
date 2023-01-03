package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.WorldSettings;

public class GameModeCommand extends Command {
   @Override
   public String getName() {
      return "gamemode";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.gamemode.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length <= 0) {
         throw new IncorrectUsageException("commands.gamemode.usage");
      } else {
         WorldSettings.GameMode var3 = this.parseGameMode(source, args[0]);
         ServerPlayerEntity var4 = args.length >= 2 ? parsePlayer(source, args[1]) : asPlayer(source);
         var4.setGameMode(var3);
         var4.fallDistance = 0.0F;
         if (source.getSourceWorld().getGameRules().getBoolean("sendCommandFeedback")) {
            var4.sendMessage(new TranslatableText("gameMode.changed"));
         }

         TranslatableText var5 = new TranslatableText("gameMode." + var3.getId());
         if (var4 != source) {
            sendSuccess(source, this, 1, "commands.gamemode.success.other", new Object[]{var4.getName(), var5});
         } else {
            sendSuccess(source, this, 1, "commands.gamemode.success.self", new Object[]{var5});
         }
      }
   }

   protected WorldSettings.GameMode parseGameMode(CommandSource source, String s) {
      if (s.equalsIgnoreCase(WorldSettings.GameMode.SURVIVAL.getId()) || s.equalsIgnoreCase("s")) {
         return WorldSettings.GameMode.SURVIVAL;
      } else if (s.equalsIgnoreCase(WorldSettings.GameMode.CREATIVE.getId()) || s.equalsIgnoreCase("c")) {
         return WorldSettings.GameMode.CREATIVE;
      } else if (s.equalsIgnoreCase(WorldSettings.GameMode.ADVENTURE.getId()) || s.equalsIgnoreCase("a")) {
         return WorldSettings.GameMode.ADVENTURE;
      } else {
         return !s.equalsIgnoreCase(WorldSettings.GameMode.SPECTATOR.getId()) && !s.equalsIgnoreCase("sp")
            ? WorldSettings.getGameModeById(parseInt(s, 0, WorldSettings.GameMode.values().length - 2))
            : WorldSettings.GameMode.SPECTATOR;
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"survival", "creative", "adventure", "spectator"});
      } else {
         return args.length == 2 ? suggestMatching(args, this.getPlayerNames()) : null;
      }
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getInstance().getPlayerNames();
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 1;
   }
}
