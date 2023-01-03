package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class MeCommand extends Command {
   @Override
   public String getName() {
      return "me";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.me.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length <= 0) {
         throw new IncorrectUsageException("commands.me.usage");
      } else {
         Text var3 = parseText(source, args, 0, !(source instanceof PlayerEntity));
         MinecraftServer.getInstance().getPlayerManager().sendSystemMessage(new TranslatableText("chat.type.emote", source.getDisplayName(), var3));
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
   }
}
