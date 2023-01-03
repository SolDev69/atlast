package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class WhitelistCommand extends Command {
   @Override
   public String getName() {
      return "whitelist";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.whitelist.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.whitelist.usage");
      } else {
         MinecraftServer var3 = MinecraftServer.getInstance();
         if (args[0].equals("on")) {
            var3.getPlayerManager().setWhitelistEnabled(true);
            sendSuccess(source, this, "commands.whitelist.enabled", new Object[0]);
         } else if (args[0].equals("off")) {
            var3.getPlayerManager().setWhitelistEnabled(false);
            sendSuccess(source, this, "commands.whitelist.disabled", new Object[0]);
         } else if (args[0].equals("list")) {
            source.sendMessage(
               new TranslatableText("commands.whitelist.list", var3.getPlayerManager().getWhitelistNames().length, var3.getPlayerManager().getSavedIds().length)
            );
            String[] var4 = var3.getPlayerManager().getWhitelistNames();
            source.sendMessage(new LiteralText(listArgs(var4)));
         } else if (args[0].equals("add")) {
            if (args.length < 2) {
               throw new IncorrectUsageException("commands.whitelist.add.usage");
            }

            GameProfile var5 = var3.getPlayerCache().remove(args[1]);
            if (var5 == null) {
               throw new CommandException("commands.whitelist.add.failed", args[1]);
            }

            var3.getPlayerManager().addToWhitelist(var5);
            sendSuccess(source, this, "commands.whitelist.add.success", new Object[]{args[1]});
         } else if (args[0].equals("remove")) {
            if (args.length < 2) {
               throw new IncorrectUsageException("commands.whitelist.remove.usage");
            }

            GameProfile var6 = var3.getPlayerManager().getWhitelist().getPlayer(args[1]);
            if (var6 == null) {
               throw new CommandException("commands.whitelist.remove.failed", args[1]);
            }

            var3.getPlayerManager().removeFromWhitelist(var6);
            sendSuccess(source, this, "commands.whitelist.remove.success", new Object[]{args[1]});
         } else if (args[0].equals("reload")) {
            var3.getPlayerManager().reloadWhitelist();
            sendSuccess(source, this, "commands.whitelist.reloaded", new Object[0]);
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"on", "off", "list", "add", "remove", "reload"});
      } else {
         if (args.length == 2) {
            if (args[0].equals("remove")) {
               return suggestMatching(args, MinecraftServer.getInstance().getPlayerManager().getWhitelistNames());
            }

            if (args[0].equals("add")) {
               return suggestMatching(args, MinecraftServer.getInstance().getPlayerCache().getNames());
            }
         }

         return null;
      }
   }
}
