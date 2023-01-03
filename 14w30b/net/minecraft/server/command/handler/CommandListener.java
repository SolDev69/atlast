package net.minecraft.server.command.handler;

import net.minecraft.server.command.ICommand;
import net.minecraft.server.command.source.CommandSource;

public interface CommandListener {
   void sendSuccess(CommandSource source, ICommand command, int flags, String message, Object... args);
}
