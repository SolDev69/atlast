package net.minecraft.server.dedicated;

import net.minecraft.server.command.source.CommandSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class PendingCommand {
   public final String command;
   public final CommandSource source;

   public PendingCommand(String command, CommandSource source) {
      this.command = command;
      this.source = source;
   }
}
