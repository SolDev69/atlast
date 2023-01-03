package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerBanEntry;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class BanCommand extends Command {
   @Override
   public String getName() {
      return "ban";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.ban.usage";
   }

   @Override
   public boolean canUse(CommandSource source) {
      return MinecraftServer.getInstance().getPlayerManager().getPlayerBans().isEnabled() && super.canUse(source);
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length >= 1 && args[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getInstance();
         GameProfile var4 = var3.getPlayerCache().remove(args[0]);
         if (var4 == null) {
            throw new CommandException("commands.ban.failed", args[0]);
         } else {
            String var5 = null;
            if (args.length >= 2) {
               var5 = parseText(source, args, 1).buildString();
            }

            PlayerBanEntry var6 = new PlayerBanEntry(var4, null, source.getName(), null, var5);
            var3.getPlayerManager().getPlayerBans().add(var6);
            ServerPlayerEntity var7 = var3.getPlayerManager().get(args[0]);
            if (var7 != null) {
               var7.networkHandler.disconnect("You are banned from this server.");
            }

            sendSuccess(source, this, "commands.ban.success", new Object[]{args[0]});
         }
      } else {
         throw new IncorrectUsageException("commands.ban.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length >= 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }
}
