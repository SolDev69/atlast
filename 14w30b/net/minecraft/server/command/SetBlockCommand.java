package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetBlockCommand extends Command {
   @Override
   public String getName() {
      return "setblock";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.setblock.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 4) {
         throw new IncorrectUsageException("commands.setblock.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         Block var4 = Command.parseBlock(source, args[3]);
         int var5 = 0;
         if (args.length >= 5) {
            var5 = parseInt(args[4], 0, 15);
         }

         World var6 = source.getSourceWorld();
         if (!var6.isLoaded(var3)) {
            throw new CommandException("commands.setblock.outOfWorld");
         } else {
            NbtCompound var7 = new NbtCompound();
            boolean var8 = false;
            if (args.length >= 7 && var4.hasBlockEntity()) {
               String var9 = parseText(source, args, 6).buildString();

               try {
                  var7 = StringNbtReader.parse(var9);
                  var8 = true;
               } catch (NbtException var12) {
                  throw new CommandException("commands.setblock.tagError", var12.getMessage());
               }
            }

            if (args.length >= 6) {
               if (args[5].equals("destroy")) {
                  var6.breakBlock(var3, true);
                  if (var4 == Blocks.AIR) {
                     sendSuccess(source, this, "commands.setblock.success", new Object[0]);
                     return;
                  }
               } else if (args[5].equals("keep") && !var6.isAir(var3)) {
                  throw new CommandException("commands.setblock.noChange");
               }
            }

            BlockEntity var13 = var6.getBlockEntity(var3);
            if (var13 != null) {
               if (var13 instanceof Inventory) {
                  ((Inventory)var13).clear();
               }

               var6.setBlockState(var3, Blocks.AIR.defaultState(), var4 == Blocks.AIR ? 2 : 4);
            }

            BlockState var10 = var4.getStateFromMetadata(var5);
            if (!var6.setBlockState(var3, var10, 2)) {
               throw new CommandException("commands.setblock.noChange");
            } else {
               if (var8) {
                  BlockEntity var11 = var6.getBlockEntity(var3);
                  if (var11 != null) {
                     var7.putInt("x", var3.getX());
                     var7.putInt("y", var3.getY());
                     var7.putInt("z", var3.getZ());
                     var11.readNbt(var7);
                  }
               }

               var6.onBlockChanged(var3, var10.getBlock());
               source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 1);
               sendSuccess(source, this, "commands.setblock.success", new Object[0]);
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 4) {
         return suggestMatching(args, Block.REGISTRY.keySet());
      } else {
         return args.length == 6 ? suggestMatching(args, new String[]{"replace", "destroy", "keep"}) : null;
      }
   }
}
