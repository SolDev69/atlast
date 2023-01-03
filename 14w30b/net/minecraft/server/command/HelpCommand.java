package net.minecraft.server.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandNotFoundException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class HelpCommand extends Command {
   @Override
   public String getName() {
      return "help";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.help.usage";
   }

   @Override
   public List getAliases() {
      return Arrays.asList("?");
   }

   @Override
   public void run(CommandSource source, String[] args) {
      List var3 = this.getAvailableCommands(source);
      boolean var4 = true;
      int var5 = (var3.size() - 1) / 7;
      int var6 = 0;

      try {
         var6 = args.length == 0 ? 0 : parseInt(args[0], 1, var5 + 1) - 1;
      } catch (InvalidNumberException var12) {
         Map var8 = this.getCommands();
         ICommand var9 = (ICommand)var8.get(args[0]);
         if (var9 != null) {
            throw new IncorrectUsageException(var9.getUsage(source));
         }

         if (MathHelper.parseInt(args[0], -1) != -1) {
            throw var12;
         }

         throw new CommandNotFoundException();
      }

      int var7 = Math.min((var6 + 1) * 7, var3.size());
      TranslatableText var14 = new TranslatableText("commands.help.header", var6 + 1, var5 + 1);
      var14.getStyle().setColor(Formatting.DARK_GREEN);
      source.sendMessage(var14);

      for(int var15 = var6 * 7; var15 < var7; ++var15) {
         ICommand var10 = (ICommand)var3.get(var15);
         TranslatableText var11 = new TranslatableText(var10.getUsage(source));
         var11.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + var10.getName() + " "));
         source.sendMessage(var11);
      }

      if (var6 == 0 && source instanceof PlayerEntity) {
         TranslatableText var16 = new TranslatableText("commands.help.footer");
         var16.getStyle().setColor(Formatting.GREEN);
         source.sendMessage(var16);
      }
   }

   protected List getAvailableCommands(CommandSource source) {
      List var2 = MinecraftServer.getInstance().getCommandHandler().getAvailableCommands(source);
      Collections.sort(var2);
      return var2;
   }

   protected Map getCommands() {
      return MinecraftServer.getInstance().getCommandHandler().getCommands();
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         Set var3 = this.getCommands().keySet();
         return suggestMatching(args, var3.toArray(new String[var3.size()]));
      } else {
         return null;
      }
   }
}
