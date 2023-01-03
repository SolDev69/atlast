package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.command.source.CommandSource;

public interface ICommand extends Comparable {
   String getName();

   String getUsage(CommandSource source);

   List getAliases();

   void run(CommandSource source, String[] args);

   boolean canUse(CommandSource source);

   List getSuggestions(CommandSource source, String[] args);

   boolean hasTargetSelectorAt(String[] args, int index);
}
