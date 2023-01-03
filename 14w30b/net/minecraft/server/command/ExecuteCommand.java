package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.handler.CommandHandler;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExecuteCommand extends Command {
   @Override
   public String getName() {
      return "execute";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.execute.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 5) {
         throw new IncorrectUsageException("commands.execute.usage");
      } else {
         final Entity var3 = parseEntity(source, args[0], Entity.class);
         double var4 = parseRawCoordinate(var3.x, args[1], false);
         double var6 = parseRawCoordinate(var3.y, args[2], false);
         double var8 = parseRawCoordinate(var3.z, args[3], false);
         final BlockPos var10 = new BlockPos(var4, var6, var8);
         byte var11 = 4;
         if (args[4].equals("detect") && args.length > 10) {
            World var12 = source.getSourceWorld();
            double var13 = parseRawCoordinate(var4, args[5], false);
            double var15 = parseRawCoordinate(var6, args[6], false);
            double var17 = parseRawCoordinate(var8, args[7], false);
            Block var19 = parseBlock(source, args[8]);
            int var20 = parseInt(args[9], -1, 15);
            BlockPos var21 = new BlockPos(var13, var15, var17);
            BlockState var22 = var12.getBlockState(var21);
            if (var22.getBlock() != var19 || var20 >= 0 && var22.getBlock().getMetadataFromState(var22) != var20) {
               throw new CommandException("commands.execute.failed", "detect", var3.getName());
            }

            var11 = 10;
         }

         String var24 = parseString(args, var11);
         CommandSource var14 = new CommandSource() {
            @Override
            public String getName() {
               return var3.getName();
            }

            @Override
            public Text getDisplayName() {
               return var3.getDisplayName();
            }

            @Override
            public void sendMessage(Text message) {
               source.sendMessage(message);
            }

            @Override
            public boolean canUseCommand(int permissionLevel, String command) {
               return source.canUseCommand(permissionLevel, command);
            }

            @Override
            public BlockPos getSourceBlockPos() {
               return var10;
            }

            @Override
            public World getSourceWorld() {
               return var3.world;
            }

            @Override
            public Entity asEntity() {
               return var3;
            }

            @Override
            public boolean sendCommandFeedback() {
               MinecraftServer var1 = MinecraftServer.getInstance();
               return var1 == null || var1.worlds[0].getGameRules().getBoolean("commandBlockOutput");
            }

            @Override
            public void addResult(CommandResults.Type type, int result) {
               var3.addResult(type, result);
               source.addResult(type, result);
            }
         };
         CommandHandler var25 = MinecraftServer.getInstance().getCommandHandler();

         try {
            int var16 = var25.run(var14, var24);
            if (var16 < 1) {
               throw new CommandException("commands.execute.allInvocationsFailed", var24);
            }
         } catch (Throwable var23) {
            throw new CommandException("commands.execute.failed", var24, var3.getName());
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
