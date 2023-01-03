package net.minecraft.server.command.exception;

public class CommandNotFoundException extends CommandException {
   public CommandNotFoundException() {
      this("commands.generic.notFound");
   }

   public CommandNotFoundException(String string, Object... objects) {
      super(string, objects);
   }
}
