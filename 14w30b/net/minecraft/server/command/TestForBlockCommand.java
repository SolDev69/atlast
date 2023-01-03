package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestForBlockCommand extends Command {
   @Override
   public String getName() {
      return "testforblock";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.testforblock.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 4) {
         throw new IncorrectUsageException("commands.testforblock.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         Block var4 = Block.byId(args[3]);
         if (var4 == null) {
            throw new InvalidNumberException("commands.setblock.notFound", args[3]);
         } else {
            int var5 = -1;
            if (args.length >= 5) {
               var5 = parseInt(args[4], -1, 15);
            }

            World var6 = source.getSourceWorld();
            if (!var6.isLoaded(var3)) {
               throw new CommandException("commands.testforblock.outOfWorld");
            } else {
               NbtCompound var7 = new NbtCompound();
               boolean var8 = false;
               if (args.length >= 6 && var4.hasBlockEntity()) {
                  String var9 = parseText(source, args, 5).buildString();

                  try {
                     var7 = StringNbtReader.parse(var9);
                     var8 = true;
                  } catch (NbtException var13) {
                     throw new CommandException("commands.setblock.tagError", var13.getMessage());
                  }
               }

               BlockState var14 = var6.getBlockState(var3);
               Block var10 = var14.getBlock();
               if (var10 != var4) {
                  throw new CommandException("commands.testforblock.failed.tile", var3.getX(), var3.getY(), var3.getZ(), var10.getName(), var4.getName());
               } else {
                  if (var5 > -1) {
                     int var11 = var14.getBlock().getMetadataFromState(var14);
                     if (var11 != var5) {
                        throw new CommandException("commands.testforblock.failed.data", var3.getX(), var3.getY(), var3.getZ(), var11, var5);
                     }
                  }

                  if (var8) {
                     BlockEntity var15 = var6.getBlockEntity(var3);
                     if (var15 == null) {
                        throw new CommandException("commands.testforblock.failed.tileEntity", var3.getX(), var3.getY(), var3.getZ());
                     }

                     NbtCompound var12 = new NbtCompound();
                     var15.writeNbt(var12);
                     if (!matchesNbt(var7, var12, true)) {
                        throw new CommandException("commands.testforblock.failed.nbt", var3.getX(), var3.getY(), var3.getZ());
                     }
                  }

                  source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 1);
                  sendSuccess(source, this, "commands.testforblock.success", new Object[]{var3.getX(), var3.getY(), var3.getZ()});
               }
            }
         }
      }
   }

   public static boolean matchesNbt(NbtElement target, NbtElement nbt, boolean checkListsRecursively) {
      if (target == nbt) {
         return true;
      } else if (target == null) {
         return true;
      } else if (nbt == null) {
         return false;
      } else if (!target.getClass().equals(nbt.getClass())) {
         return false;
      } else if (target instanceof NbtCompound) {
         NbtCompound var9 = (NbtCompound)target;
         NbtCompound var10 = (NbtCompound)nbt;

         for(String var12 : var9.getKeys()) {
            NbtElement var13 = var9.get(var12);
            if (!matchesNbt(var13, var10.get(var12), checkListsRecursively)) {
               return false;
            }
         }

         return true;
      } else if (target instanceof NbtList && checkListsRecursively) {
         NbtList var3 = (NbtList)target;
         NbtList var4 = (NbtList)nbt;
         if (var3.size() == 0) {
            return var4.size() == 0;
         } else {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
               NbtElement var6 = var3.get(var5);
               boolean var7 = false;

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  if (matchesNbt(var6, var4.get(var8), checkListsRecursively)) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return target.equals(nbt);
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 4 ? suggestMatching(args, Block.REGISTRY.keySet()) : null;
   }
}
