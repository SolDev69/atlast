package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
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

public class FillCommand extends Command {
   @Override
   public String getName() {
      return "fill";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.fill.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 7) {
         throw new IncorrectUsageException("commands.fill.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         BlockPos var4 = parseBlockPos(source, args, 3, false);
         Block var5 = Command.parseBlock(source, args[6]);
         int var6 = 0;
         if (args.length >= 8) {
            var6 = parseInt(args[7], 0, 15);
         }

         BlockPos var7 = new BlockPos(Math.min(var3.getX(), var4.getX()), Math.min(var3.getY(), var4.getY()), Math.min(var3.getZ(), var4.getZ()));
         BlockPos var8 = new BlockPos(Math.max(var3.getX(), var4.getX()), Math.max(var3.getY(), var4.getY()), Math.max(var3.getZ(), var4.getZ()));
         int var9 = (var8.getX() - var7.getX() + 1) * (var8.getY() - var7.getY() + 1) * (var8.getZ() - var7.getZ() + 1);
         if (var9 > 32768) {
            throw new CommandException("commands.fill.tooManyBlocks", var9, 32768);
         } else if (var7.getY() >= 0 && var8.getY() < 256) {
            World var10 = source.getSourceWorld();

            for(int var11 = var7.getZ(); var11 < var8.getZ() + 16; var11 += 16) {
               for(int var12 = var7.getX(); var12 < var8.getX() + 16; var12 += 16) {
                  if (!var10.isLoaded(new BlockPos(var12, var8.getY() - var7.getY(), var11))) {
                     throw new CommandException("commands.fill.outOfWorld");
                  }
               }
            }

            NbtCompound var23 = new NbtCompound();
            boolean var24 = false;
            if (args.length >= 10 && var5.hasBlockEntity()) {
               String var13 = parseText(source, args, 9).buildString();

               try {
                  var23 = StringNbtReader.parse(var13);
                  var24 = true;
               } catch (NbtException var21) {
                  throw new CommandException("commands.fill.tagError", var21.getMessage());
               }
            }

            ArrayList var25 = Lists.newArrayList();
            var9 = 0;

            for(int var14 = var7.getZ(); var14 <= var8.getZ(); ++var14) {
               for(int var15 = var7.getY(); var15 <= var8.getY(); ++var15) {
                  for(int var16 = var7.getX(); var16 <= var8.getX(); ++var16) {
                     BlockPos var17 = new BlockPos(var16, var15, var14);
                     if (args.length >= 9) {
                        if (!args[8].equals("outline") && !args[8].equals("hollow")) {
                           if (args[8].equals("destroy")) {
                              var10.breakBlock(var17, true);
                           } else if (args[8].equals("keep")) {
                              if (!var10.isAir(var17)) {
                                 continue;
                              }
                           } else if (args[8].equals("replace") && !var5.hasBlockEntity()) {
                              if (args.length > 9) {
                                 Block var18 = Command.parseBlock(source, args[9]);
                                 if (var10.getBlockState(var17).getBlock() != var18) {
                                    continue;
                                 }
                              }

                              if (args.length > 10) {
                                 int var29 = Command.parseInt(args[10]);
                                 BlockState var19 = var10.getBlockState(var17);
                                 if (var19.getBlock().getMetadataFromState(var19) != var29) {
                                    continue;
                                 }
                              }
                           }
                        } else if (var16 != var7.getX()
                           && var16 != var8.getX()
                           && var15 != var7.getY()
                           && var15 != var8.getY()
                           && var14 != var7.getZ()
                           && var14 != var8.getZ()) {
                           if (args[8].equals("hollow")) {
                              var10.setBlockState(var17, Blocks.AIR.defaultState(), 2);
                              var25.add(var17);
                           }
                           continue;
                        }
                     }

                     BlockEntity var30 = var10.getBlockEntity(var17);
                     if (var30 != null) {
                        if (var30 instanceof Inventory) {
                           ((Inventory)var30).clear();
                        }

                        var10.setBlockState(var17, Blocks.BARRIER.defaultState(), var5 == Blocks.BARRIER ? 2 : 4);
                     }

                     BlockState var31 = var5.getStateFromMetadata(var6);
                     if (var10.setBlockState(var17, var31, 2)) {
                        var25.add(var17);
                        ++var9;
                        if (var24) {
                           BlockEntity var20 = var10.getBlockEntity(var17);
                           if (var20 != null) {
                              var23.putInt("x", var17.getX());
                              var23.putInt("y", var17.getY());
                              var23.putInt("z", var17.getZ());
                              var20.readNbt(var23);
                           }
                        }
                     }
                  }
               }
            }

            for(BlockPos var27 : var25) {
               Block var28 = var10.getBlockState(var27).getBlock();
               var10.onBlockChanged(var27, var28);
            }

            if (var9 <= 0) {
               throw new CommandException("commands.fill.failed");
            } else {
               source.addResult(CommandResults.Type.AFFECTED_BLOCKS, var9);
               sendSuccess(source, this, "commands.fill.success", new Object[]{var9});
            }
         } else {
            throw new CommandException("commands.fill.outOfWorld");
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 7) {
         return suggestMatching(args, Block.REGISTRY.keySet());
      } else {
         return args.length == 9 ? suggestMatching(args, new String[]{"replace", "destroy", "keep", "hollow", "outline"}) : null;
      }
   }
}
