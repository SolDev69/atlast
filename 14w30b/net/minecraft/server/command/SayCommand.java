package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SayCommand extends Command {
   @Override
   public String getName() {
      return "say";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 1;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.say.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length > 0 && args[0].length() > 0) {
         Text var3 = parseText(source, args, 0, true);
         MinecraftServer.getInstance().getPlayerManager().sendSystemMessage(new TranslatableText("chat.type.announcement", source.getName(), var3));
      } else {
         throw new IncorrectUsageException("commands.say.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length >= 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }
}
