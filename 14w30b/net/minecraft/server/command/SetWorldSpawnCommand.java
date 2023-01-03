package net.minecraft.server.command;

import net.minecraft.network.packet.s2c.play.PlayerSpawnPointS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand extends Command {
   @Override
   public String getName() {
      return "setworldspawn";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.setworldspawn.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      BlockPos var3;
      if (args.length == 0) {
         var3 = asPlayer(source).getSourceBlockPos();
      } else {
         if (args.length != 3 || source.getSourceWorld() == null) {
            throw new IncorrectUsageException("commands.setworldspawn.usage");
         }

         var3 = parseBlockPos(source, args, 0, true);
      }

      source.getSourceWorld().setSpawnPoint(var3);
      MinecraftServer.getInstance().getPlayerManager().sendToAll(new PlayerSpawnPointS2CPacket(var3));
      sendSuccess(source, this, "commands.setworldspawn.success", new Object[]{var3.getX(), var3.getY(), var3.getZ()});
   }
}
