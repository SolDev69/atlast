package net.minecraft.server.command.handler;

import java.util.List;
import java.util.Map;
import net.minecraft.server.command.source.CommandSource;

public interface CommandHandler {
   int run(CommandSource source, String command);

   List getSuggestions(CommandSource source, String command);

   List getAvailableCommands(CommandSource source);

   Map getCommands();
}
