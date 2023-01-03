package net.minecraft.server.dedicated.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class BanListCommand extends Command {
   @Override
   public String getName() {
      return "banlist";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canUse(CommandSource source) {
      return (
            MinecraftServer.getInstance().getPlayerManager().getIpBans().isEnabled()
               || MinecraftServer.getInstance().getPlayerManager().getPlayerBans().isEnabled()
         )
         && super.canUse(source);
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.banlist.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length >= 1 && args[0].equalsIgnoreCase("ips")) {
         source.sendMessage(new TranslatableText("commands.banlist.ips", MinecraftServer.getInstance().getPlayerManager().getIpBans().getNames().length));
         source.sendMessage(new LiteralText(listArgs(MinecraftServer.getInstance().getPlayerManager().getIpBans().getNames())));
      } else {
         source.sendMessage(
            new TranslatableText("commands.banlist.players", MinecraftServer.getInstance().getPlayerManager().getPlayerBans().getNames().length)
         );
         source.sendMessage(new LiteralText(listArgs(MinecraftServer.getInstance().getPlayerManager().getPlayerBans().getNames())));
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, new String[]{"players", "ips"}) : null;
   }
}
