package net.minecraft.server.command;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class KillCommand extends Command {
   @Override
   public String getName() {
      return "kill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.kill.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length == 0) {
         ServerPlayerEntity var4 = asPlayer(source);
         var4.m_59lfywdxf();
         sendSuccess(source, this, "commands.kill.successful", new Object[]{var4.getDisplayName()});
      } else {
         Entity var3 = parseEntity(source, args[0]);
         var3.m_59lfywdxf();
         sendSuccess(source, this, "commands.kill.successful", new Object[]{var3.getDisplayName()});
      }
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
