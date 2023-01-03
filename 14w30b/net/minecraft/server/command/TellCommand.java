package net.minecraft.server.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TellCommand extends Command {
   @Override
   public List getAliases() {
      return Arrays.asList("w", "msg");
   }

   @Override
   public String getName() {
      return "tell";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.message.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.message.usage");
      } else {
         ServerPlayerEntity var3 = parsePlayer(source, args[0]);
         if (var3 == source) {
            throw new PlayerNotFoundException("commands.message.sameTarget");
         } else {
            Text var4 = parseText(source, args, 1, !(source instanceof PlayerEntity));
            TranslatableText var5 = new TranslatableText("commands.message.display.incoming", source.getDisplayName(), var4.copy());
            TranslatableText var6 = new TranslatableText("commands.message.display.outgoing", var3.getDisplayName(), var4.copy());
            var5.getStyle().setColor(Formatting.GRAY).setItalic(true);
            var6.getStyle().setColor(Formatting.GRAY).setItalic(true);
            var3.sendMessage(var5);
            source.sendMessage(var6);
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
