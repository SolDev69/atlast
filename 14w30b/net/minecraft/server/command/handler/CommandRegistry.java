package net.minecraft.server.command.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.ICommand;
import net.minecraft.server.command.TargetSelector;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Formatting;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandRegistry implements CommandHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map commandsByName = Maps.newHashMap();
   private final Set commands = Sets.newHashSet();

   @Override
   public int run(CommandSource source, String command) {
      command = command.trim();
      if (command.startsWith("/")) {
         command = command.substring(1);
      }

      String[] var3 = command.split(" ");
      String var4 = var3[0];
      var3 = getCommandArgs(var3);
      ICommand var5 = (ICommand)this.commandsByName.get(var4);
      int var6 = this.getIndexOfTargetSelector(var5, var3);
      int var7 = 0;
      if (var5 == null) {
         TranslatableText var8 = new TranslatableText("commands.generic.notFound");
         var8.getStyle().setColor(Formatting.RED);
         source.sendMessage(var8);
      } else if (var5.canUse(source)) {
         if (var6 > -1) {
            List var14 = TargetSelector.select(source, var3[var6], Entity.class);
            String var9 = var3[var6];
            source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var14.size());

            for(Entity var11 : var14) {
               var3[var6] = var11.getUuid().toString();
               if (this.run(source, var3, var5, command)) {
                  ++var7;
               }
            }

            var3[var6] = var9;
         } else {
            source.addResult(CommandResults.Type.AFFECTED_ENTITIES, 1);
            if (this.run(source, var3, var5, command)) {
               ++var7;
            }
         }
      } else {
         TranslatableText var15 = new TranslatableText("commands.generic.permission");
         var15.getStyle().setColor(Formatting.RED);
         source.sendMessage(var15);
      }

      source.addResult(CommandResults.Type.SUCCESS_COUNT, var7);
      return var7;
   }

   protected boolean run(CommandSource source, String[] args, ICommand command, String rawCommand) {
      try {
         command.run(source, args);
         return true;
      } catch (IncorrectUsageException var7) {
         TranslatableText var11 = new TranslatableText("commands.generic.usage", new TranslatableText(var7.getMessage(), var7.getArgs()));
         var11.getStyle().setColor(Formatting.RED);
         source.sendMessage(var11);
      } catch (CommandException var8) {
         TranslatableText var10 = new TranslatableText(var8.getMessage(), var8.getArgs());
         var10.getStyle().setColor(Formatting.RED);
         source.sendMessage(var10);
      } catch (Throwable var9) {
         TranslatableText var6 = new TranslatableText("commands.generic.exception");
         var6.getStyle().setColor(Formatting.RED);
         source.sendMessage(var6);
         LOGGER.error("Couldn't process command: '" + rawCommand + "'", var9);
      }

      return false;
   }

   public ICommand register(ICommand command) {
      List var2 = command.getAliases();
      this.commandsByName.put(command.getName(), command);
      this.commands.add(command);
      if (var2 != null) {
         for(String var4 : var2) {
            ICommand var5 = (ICommand)this.commandsByName.get(var4);
            if (var5 == null || !var5.getName().equals(var4)) {
               this.commandsByName.put(var4, command);
            }
         }
      }

      return command;
   }

   private static String[] getCommandArgs(String[] args) {
      String[] var1 = new String[args.length - 1];
      System.arraycopy(args, 1, var1, 0, args.length - 1);
      return var1;
   }

   @Override
   public List getSuggestions(CommandSource source, String command) {
      String[] var3 = command.split(" ", -1);
      String var4 = var3[0];
      if (var3.length == 1) {
         ArrayList var8 = Lists.newArrayList();

         for(Entry var7 : this.commandsByName.entrySet()) {
            if (Command.doesStringStartWith(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).canUse(source)) {
               var8.add(var7.getKey());
            }
         }

         return var8;
      } else {
         if (var3.length > 1) {
            ICommand var5 = (ICommand)this.commandsByName.get(var4);
            if (var5 != null && var5.canUse(source)) {
               return var5.getSuggestions(source, getCommandArgs(var3));
            }
         }

         return null;
      }
   }

   @Override
   public List getAvailableCommands(CommandSource source) {
      ArrayList var2 = Lists.newArrayList();

      for(ICommand var4 : this.commands) {
         if (var4.canUse(source)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   @Override
   public Map getCommands() {
      return this.commandsByName;
   }

   private int getIndexOfTargetSelector(ICommand command, String[] args) {
      if (command == null) {
         return -1;
      } else {
         for(int var3 = 0; var3 < args.length; ++var3) {
            if (command.hasTargetSelectorAt(args, var3) && TargetSelector.matchesMultiple(args[var3])) {
               return var3;
            }
         }

         return -1;
      }
   }
}
