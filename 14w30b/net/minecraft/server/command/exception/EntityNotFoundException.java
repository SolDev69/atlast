package net.minecraft.server.command.exception;

public class EntityNotFoundException extends CommandException {
   public EntityNotFoundException() {
      this("commands.generic.entity.notFound");
   }

   public EntityNotFoundException(String string, Object... objects) {
      super(string, objects);
   }
}
