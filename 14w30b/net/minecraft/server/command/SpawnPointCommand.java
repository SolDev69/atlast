package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SpawnPointCommand extends Command {
   @Override
   public String getName() {
      return "spawnpoint";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.spawnpoint.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length > 0 && args.length < 4) {
         throw new IncorrectUsageException("commands.spawnpoint.usage");
      } else {
         ServerPlayerEntity var3 = args.length > 0 ? parsePlayer(source, args[0]) : asPlayer(source);
         BlockPos var4 = args.length > 3 ? parseBlockPos(source, args, 1, true) : var3.getSourceBlockPos();
         if (var3.world != null) {
            var3.setSpawnpoint(var4, true);
            sendSuccess(source, this, "commands.spawnpoint.success", new Object[]{var3.getName(), var4.getX(), var4.getY(), var4.getZ()});
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length != 1 && args.length != 2 ? null : suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
