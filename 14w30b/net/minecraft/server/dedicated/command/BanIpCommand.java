package net.minecraft.server.dedicated.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.IpBanEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BanIpCommand extends Command {
   public static final Pattern REGEX_PATTERN = Pattern.compile(
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
   );

   @Override
   public String getName() {
      return "ban-ip";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canUse(CommandSource source) {
      return MinecraftServer.getInstance().getPlayerManager().getIpBans().isEnabled() && super.canUse(source);
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.banip.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length >= 1 && args[0].length() > 1) {
         Matcher var3 = REGEX_PATTERN.matcher(args[0]);
         Text var4 = args.length >= 2 ? parseText(source, args, 1) : null;
         if (var3.matches()) {
            this.ban(source, args[0], var4 == null ? null : var4.buildString());
         } else {
            ServerPlayerEntity var5 = MinecraftServer.getInstance().getPlayerManager().get(args[0]);
            if (var5 == null) {
               throw new PlayerNotFoundException("commands.banip.invalid");
            }

            this.ban(source, var5.getIp(), var4 == null ? null : var4.buildString());
         }
      } else {
         throw new IncorrectUsageException("commands.banip.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }

   protected void ban(CommandSource commmand, String name, String reason) {
      IpBanEntry var4 = new IpBanEntry(name, null, commmand.getName(), null, reason);
      MinecraftServer.getInstance().getPlayerManager().getIpBans().add(var4);
      List var5 = MinecraftServer.getInstance().getPlayerManager().getAtIp(name);
      String[] var6 = new String[var5.size()];
      int var7 = 0;

      for(ServerPlayerEntity var9 : var5) {
         var9.networkHandler.disconnect("You have been IP banned.");
         var6[var7++] = var9.getName();
      }

      if (var5.isEmpty()) {
         sendSuccess(commmand, this, "commands.banip.success", new Object[]{name});
      } else {
         sendSuccess(commmand, this, "commands.banip.success.players", new Object[]{name, listArgs(var6)});
      }
   }
}
