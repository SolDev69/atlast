package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand extends Command {
   @Override
   public String getName() {
      return "worldborder";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.worldborder.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.worldborder.usage");
      } else {
         WorldBorder var3 = this.getWorldBorder();
         if (args[0].equals("set")) {
            if (args.length != 2 && args.length != 3) {
               throw new IncorrectUsageException("commands.worldborder.set.usage");
            }

            double var4 = var3.getSizeLerpTarget();
            double var6 = parseDouble(args[1], 1.0, 6.0E7);
            int var8 = args.length > 2 ? parseInt(args[2], 0) * 1000 : 0;
            if (var8 > 0) {
               var3.setSize(var4, var6, var8);
               if (var4 > var6) {
                  sendSuccess(
                     source,
                     this,
                     "commands.worldborder.setSlowly.shrink.success",
                     new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), MathHelper.floor((float)(var8 / 1000))}
                  );
               } else {
                  sendSuccess(
                     source,
                     this,
                     "commands.worldborder.setSlowly.grow.success",
                     new Object[]{String.format("%.1f", var6), String.format("%.1f", var4), MathHelper.floor((float)(var8 / 1000))}
                  );
               }
            } else {
               var3.setSize(var6);
               sendSuccess(source, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", var6), String.format("%.1f", var4)});
            }
         } else if (args[0].equals("add")) {
            if (args.length != 2 && args.length != 3) {
               throw new IncorrectUsageException("commands.worldborder.add.usage");
            }

            double var9 = var3.getLerpSize();
            double var17 = var9 + parseDouble(args[1], -var9, 6.0E7 - var9);
            int var20 = var3.getLerpTime() + (args.length > 2 ? parseInt(args[2], 0) * 1000 : 0);
            if (var20 > 0) {
               var3.setSize(var9, var17, var20);
               if (var9 > var17) {
                  sendSuccess(
                     source,
                     this,
                     "commands.worldborder.setSlowly.shrink.success",
                     new Object[]{String.format("%.1f", var17), String.format("%.1f", var9), MathHelper.floor((float)(var20 / 1000))}
                  );
               } else {
                  sendSuccess(
                     source,
                     this,
                     "commands.worldborder.setSlowly.grow.success",
                     new Object[]{String.format("%.1f", var17), String.format("%.1f", var9), MathHelper.floor((float)(var20 / 1000))}
                  );
               }
            } else {
               var3.setSize(var17);
               sendSuccess(source, this, "commands.worldborder.set.success", new Object[]{String.format("%.1f", var17), String.format("%.1f", var9)});
            }
         } else if (args[0].equals("center")) {
            if (args.length != 3) {
               throw new IncorrectUsageException("commands.worldborder.center.usage");
            }

            BlockPos var10 = source.getSourceBlockPos();
            double var5 = parseRawCoordinate((double)var10.getX() + 0.5, args[1], true);
            double var7 = parseRawCoordinate((double)var10.getZ() + 0.5, args[2], true);
            var3.setCenter(var5, var7);
            sendSuccess(source, this, "commands.worldborder.center.success", new Object[]{var5, var7});
         } else if (args[0].equals("damage")) {
            if (args.length < 2) {
               throw new IncorrectUsageException("commands.worldborder.damage.usage");
            }

            if (args[1].equals("buffer")) {
               if (args.length != 3) {
                  throw new IncorrectUsageException("commands.worldborder.damage.buffer.usage");
               }

               double var11 = parseDouble(args[2], 0.0);
               double var18 = var3.getSafeZone();
               var3.setSafeZone(var11);
               sendSuccess(source, this, "commands.worldborder.damage.buffer.success", new Object[]{String.format("%.1f", var11), String.format("%.1f", var18)});
            } else if (args[1].equals("amount")) {
               if (args.length != 3) {
                  throw new IncorrectUsageException("commands.worldborder.damage.amount.usage");
               }

               double var12 = parseDouble(args[2], 0.0);
               double var19 = var3.getDamagePerBlock();
               var3.setDamagePerBlock(var12);
               sendSuccess(source, this, "commands.worldborder.damage.amount.success", new Object[]{String.format("%.2f", var12), String.format("%.2f", var19)});
            }
         } else if (args[0].equals("warning")) {
            if (args.length < 2) {
               throw new IncorrectUsageException("commands.worldborder.warning.usage");
            }

            int var13 = parseInt(args[2], 0);
            if (args[1].equals("time")) {
               if (args.length != 3) {
                  throw new IncorrectUsageException("commands.worldborder.warning.time.usage");
               }

               int var15 = var3.getWarningTime();
               var3.setWarningTime(var13);
               sendSuccess(source, this, "commands.worldborder.warning.time.success", new Object[]{var13, var15});
            } else if (args[1].equals("distance")) {
               if (args.length != 3) {
                  throw new IncorrectUsageException("commands.worldborder.warning.distance.usage");
               }

               int var16 = var3.getWarningBlocks();
               var3.setWarningBlocks(var13);
               sendSuccess(source, this, "commands.worldborder.warning.distance.success", new Object[]{var13, var16});
            }
         } else if (args[0].equals("get")) {
            double var14 = var3.getLerpSize();
            source.addResult(CommandResults.Type.QUERY_RESULT, MathHelper.floor(var14 + 0.5));
            source.sendMessage(new TranslatableText("commands.worldborder.get.success", String.format("%.0f", var14)));
         }
      }
   }

   protected WorldBorder getWorldBorder() {
      return MinecraftServer.getInstance().worlds[0].getWorldBorder();
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"set", "center", "damage", "warning", "add", "get"});
      } else if (args.length == 2 && args[0].equals("damage")) {
         return suggestMatching(args, new String[]{"buffer", "amount"});
      } else {
         return args.length == 2 && args[0].equals("warning") ? suggestMatching(args, new String[]{"time", "distance"}) : null;
      }
   }
}
