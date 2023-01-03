package net.minecraft.server.dedicated.command;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;

public class OpCommand extends Command {
   @Override
   public String getName() {
      return "op";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.op.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length == 1 && args[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getInstance();
         GameProfile var4 = var3.getPlayerCache().remove(args[0]);
         if (var4 == null) {
            throw new CommandException("commands.op.failed", args[0]);
         } else {
            var3.getPlayerManager().addOp(var4);
            sendSuccess(source, this, "commands.op.success", new Object[]{args[0]});
         }
      } else {
         throw new IncorrectUsageException("commands.op.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         String var3 = args[args.length - 1];
         ArrayList var4 = Lists.newArrayList();

         for(GameProfile var8 : MinecraftServer.getInstance().getGameProfiles()) {
            if (!MinecraftServer.getInstance().getPlayerManager().isOp(var8) && doesStringStartWith(var3, var8.getName())) {
               var4.add(var8.getName());
            }
         }

         return var4;
      } else {
         return null;
      }
   }
}
