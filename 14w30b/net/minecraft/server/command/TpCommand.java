package net.minecraft.server.command;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerMoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class TpCommand extends Command {
   @Override
   public String getName() {
      return "tp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.tp.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.tp.usage");
      } else {
         byte var3 = 0;
         Object var4;
         if (args.length != 2 && args.length != 4 && args.length != 6) {
            var4 = asPlayer(source);
         } else {
            var4 = parseEntity(source, args[0]);
            var3 = 1;
         }

         if (args.length == 1 || args.length == 2) {
            Entity var16 = parseEntity(source, args[args.length - 1]);
            if (var16.world != ((Entity)var4).world) {
               throw new CommandException("commands.tp.notSameDimension");
            } else {
               ((Entity)var4).startRiding(null);
               if (var4 instanceof ServerPlayerEntity) {
                  ((ServerPlayerEntity)var4).networkHandler.teleport(var16.x, var16.y, var16.z, var16.yaw, var16.pitch);
               } else {
                  ((Entity)var4).refreshPositionAndAngles(var16.x, var16.y, var16.z, var16.yaw, var16.pitch);
               }

               sendSuccess(source, this, "commands.tp.success", new Object[]{((Entity)var4).getName(), var16.getName()});
            }
         } else if (args.length < var3 + 3) {
            throw new IncorrectUsageException("commands.tp.usage");
         } else if (((Entity)var4).world != null) {
            int var5 = var3 + 1;
            Command.Coordinate var6 = parseCoordinate(((Entity)var4).x, args[var3], true);
            Command.Coordinate var7 = parseCoordinate(((Entity)var4).y, args[var5++], 0, 0, false);
            Command.Coordinate var8 = parseCoordinate(((Entity)var4).z, args[var5++], true);
            Command.Coordinate var9 = parseCoordinate((double)((Entity)var4).yaw, args.length > var5 ? args[var5++] : "~", false);
            Command.Coordinate var10 = parseCoordinate((double)((Entity)var4).pitch, args.length > var5 ? args[var5] : "~", false);
            if (var4 instanceof ServerPlayerEntity) {
               EnumSet var11 = EnumSet.noneOf(PlayerMoveS2CPacket.Argument.class);
               if (var6.isRelative()) {
                  var11.add(PlayerMoveS2CPacket.Argument.X);
               }

               if (var7.isRelative()) {
                  var11.add(PlayerMoveS2CPacket.Argument.Y);
               }

               if (var8.isRelative()) {
                  var11.add(PlayerMoveS2CPacket.Argument.Z);
               }

               if (var10.isRelative()) {
                  var11.add(PlayerMoveS2CPacket.Argument.PITCH);
               }

               if (var9.isRelative()) {
                  var11.add(PlayerMoveS2CPacket.Argument.YAW);
               }

               float var12 = (float)var9.getRelative();
               if (!var9.isRelative()) {
                  var12 = MathHelper.wrapDegrees(var12);
               }

               float var13 = (float)var10.getRelative();
               if (!var10.isRelative()) {
                  var13 = MathHelper.wrapDegrees(var13);
               }

               if (var13 > 90.0F || var13 < -90.0F) {
                  var13 = MathHelper.wrapDegrees(180.0F - var13);
                  var12 = MathHelper.wrapDegrees(var12 + 180.0F);
               }

               ((Entity)var4).startRiding(null);
               ((ServerPlayerEntity)var4).networkHandler.teleport(var6.getRelative(), var7.getRelative(), var8.getRelative(), var12, var13, var11);
               ((Entity)var4).setHeadYaw(var12);
            } else {
               float var17 = (float)MathHelper.wrapDegrees(var9.getAbsolute());
               float var18 = (float)MathHelper.wrapDegrees(var10.getAbsolute());
               if (var18 > 90.0F || var18 < -90.0F) {
                  var18 = MathHelper.wrapDegrees(180.0F - var18);
                  var17 = MathHelper.wrapDegrees(var17 + 180.0F);
               }

               ((Entity)var4).refreshPositionAndAngles(var6.getAbsolute(), var7.getAbsolute(), var8.getAbsolute(), var17, var18);
               ((Entity)var4).setHeadYaw(var17);
            }

            sendSuccess(
               source,
               this,
               "commands.tp.success.coordinates",
               new Object[]{((Entity)var4).getName(), var6.getAbsolute(), var7.getAbsolute(), var8.getAbsolute()}
            );
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
