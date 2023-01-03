package net.minecraft.server.command;

import net.minecraft.network.packet.s2c.play.SoundEventS2CPacket;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class PlaySoundCommand extends Command {
   @Override
   public String getName() {
      return "playsound";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.playsound.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException(this.getUsage(source));
      } else {
         int var3 = 0;
         String var4 = args[var3++];
         ServerPlayerEntity var5 = parsePlayer(source, args[var3++]);
         double var6 = (double)source.getSourceBlockPos().getX();
         if (args.length > var3) {
            var6 = parseRawCoordinate(var6, args[var3++], true);
         }

         double var8 = (double)source.getSourceBlockPos().getY();
         if (args.length > var3) {
            var8 = parseRawCoordinate(var8, args[var3++], 0, 0, false);
         }

         double var10 = (double)source.getSourceBlockPos().getZ();
         if (args.length > var3) {
            var10 = parseRawCoordinate(var10, args[var3++], true);
         }

         double var12 = 1.0;
         if (args.length > var3) {
            var12 = parseDouble(args[var3++], 0.0, Float.MAX_VALUE);
         }

         double var14 = 1.0;
         if (args.length > var3) {
            var14 = parseDouble(args[var3++], 0.0, 2.0);
         }

         double var16 = 0.0;
         if (args.length > var3) {
            var16 = parseDouble(args[var3], 0.0, 1.0);
         }

         double var18 = var12 > 1.0 ? var12 * 16.0 : 16.0;
         double var20 = var5.getDistanceTo(var6, var8, var10);
         if (var20 > var18) {
            if (var16 <= 0.0) {
               throw new CommandException("commands.playsound.playerTooFar", var5.getName());
            }

            double var22 = var6 - var5.x;
            double var24 = var8 - var5.y;
            double var26 = var10 - var5.z;
            double var28 = Math.sqrt(var22 * var22 + var24 * var24 + var26 * var26);
            if (var28 > 0.0) {
               var6 = var5.x + var22 / var28 * 2.0;
               var8 = var5.y + var24 / var28 * 2.0;
               var10 = var5.z + var26 / var28 * 2.0;
            }

            var12 = var16;
         }

         var5.networkHandler.sendPacket(new SoundEventS2CPacket(var4, var6, var8, var10, (float)var12, (float)var14));
         sendSuccess(source, this, "commands.playsound.success", new Object[]{var4, var5.getName()});
      }
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 1;
   }
}
