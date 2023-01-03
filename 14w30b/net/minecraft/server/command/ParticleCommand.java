package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ParticleCommand extends Command {
   @Override
   public String getName() {
      return "particle";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.particle.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 8) {
         throw new IncorrectUsageException("commands.particle.usage");
      } else {
         boolean var3 = false;
         ParticleType var4 = null;

         for(ParticleType var8 : ParticleType.values()) {
            if (var8.isForCommands()) {
               if (args[0].startsWith(var8.getName())) {
                  var3 = true;
                  var4 = var8;
                  break;
               }
            } else if (args[0].equals(var8.getName())) {
               var3 = true;
               var4 = var8;
               break;
            }
         }

         if (!var3) {
            throw new CommandException("commands.particle.notFound", args[0]);
         } else {
            String var29 = args[0];
            double var30 = (double)((float)parseRawCoordinate((double)source.getSourceBlockPos().getX() + 0.5, args[1], true));
            double var31 = (double)((float)parseRawCoordinate((double)source.getSourceBlockPos().getY() + 0.5, args[2], true));
            double var10 = (double)((float)parseRawCoordinate((double)source.getSourceBlockPos().getZ() + 0.5, args[3], true));
            double var12 = (double)((float)parseDouble(args[4]));
            double var14 = (double)((float)parseDouble(args[5]));
            double var16 = (double)((float)parseDouble(args[6]));
            double var18 = (double)((float)parseDouble(args[7]));
            int var20 = 0;
            if (args.length > 8) {
               var20 = parseInt(args[8], 0);
            }

            boolean var21 = false;
            if (args.length > 9 && "force".equals(args[9])) {
               var21 = true;
            }

            World var22 = source.getSourceWorld();
            if (var22 instanceof ServerWorld) {
               ServerWorld var23 = (ServerWorld)var22;
               int[] var24 = new int[var4.getIdForCommands()];
               if (var4.isForCommands()) {
                  String[] var25 = args[0].split("_", 3);

                  for(int var26 = 1; var26 < var25.length; ++var26) {
                     try {
                        var24[var26 - 1] = Integer.parseInt(var25[var26]);
                     } catch (NumberFormatException var28) {
                        throw new CommandException("commands.particle.notFound", args[0]);
                     }
                  }
               }

               var23.addParticle(var4, var21, var30, var31, var10, var20, var12, var14, var16, var18, var24);
               sendSuccess(source, this, "commands.particle.success", new Object[]{var29, Math.max(var20, 1)});
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, ParticleType.getForCommands());
      } else {
         return args.length == 9 ? suggestMatching(args, new String[]{"normal", "force"}) : null;
      }
   }
}
