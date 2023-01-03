package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ScheduledTick;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBox;

public class CloneCommand extends Command {
   @Override
   public String getName() {
      return "clone";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.clone.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 9) {
         throw new IncorrectUsageException("commands.clone.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         BlockPos var4 = parseBlockPos(source, args, 3, false);
         BlockPos var5 = parseBlockPos(source, args, 6, false);
         StructureBox var6 = new StructureBox(var3, var4);
         StructureBox var7 = new StructureBox(var5, var5.add(var6.getDiagonal()));
         int var8 = var6.getSpanX() * var6.getSpanY() * var6.getSpanZ();
         if (var8 > 32768) {
            throw new CommandException("commands.clone.tooManyBlocks", var8, 32768);
         } else {
            boolean var9 = false;
            Block var10 = null;
            int var11 = -1;
            if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && var6.intersects(var7)) {
               throw new CommandException("commands.clone.noOverlap");
            } else {
               if (args.length >= 11 && args[10].equals("move")) {
                  var9 = true;
               }

               if (var6.minY >= 0 && var6.maxY < 256 && var7.minY >= 0 && var7.maxY < 256) {
                  World var12 = source.getSourceWorld();
                  if (var12.isRegionLoaded(var6) && var12.isRegionLoaded(var7)) {
                     boolean var13 = false;
                     if (args.length >= 10) {
                        if (args[9].equals("masked")) {
                           var13 = true;
                        } else if (args[9].equals("filtered")) {
                           if (args.length < 12) {
                              throw new IncorrectUsageException("commands.clone.usage");
                           }

                           var10 = parseBlock(source, args[11]);
                           if (args.length >= 13) {
                              var11 = parseInt(args[12], 0, 15);
                           }
                        }
                     }

                     ArrayList var14 = Lists.newArrayList();
                     ArrayList var15 = Lists.newArrayList();
                     ArrayList var16 = Lists.newArrayList();
                     LinkedList var17 = Lists.newLinkedList();
                     BlockPos var18 = new BlockPos(var7.minX - var6.minX, var7.minY - var6.minY, var7.minZ - var6.minZ);

                     for(int var19 = var6.minZ; var19 <= var6.maxZ; ++var19) {
                        for(int var20 = var6.minY; var20 <= var6.maxY; ++var20) {
                           for(int var21 = var6.minX; var21 <= var6.maxX; ++var21) {
                              BlockPos var22 = new BlockPos(var21, var20, var19);
                              BlockPos var23 = var22.add(var18);
                              BlockState var24 = var12.getBlockState(var22);
                              if ((!var13 || var24.getBlock() != Blocks.AIR)
                                 && (var10 == null || var24.getBlock() == var10 && (var11 < 0 || var24.getBlock().getMetadataFromState(var24) == var11))) {
                                 BlockEntity var25 = var12.getBlockEntity(var22);
                                 if (var25 != null) {
                                    NbtCompound var26 = new NbtCompound();
                                    var25.writeNbt(var26);
                                    var15.add(new CloneCommand.ClonedBlock(var23, var24, var26));
                                    var17.addLast(var22);
                                 } else if (!var24.getBlock().isOpaque() && !var24.getBlock().isFullCube()) {
                                    var16.add(new CloneCommand.ClonedBlock(var23, var24, null));
                                    var17.addFirst(var22);
                                 } else {
                                    var14.add(new CloneCommand.ClonedBlock(var23, var24, null));
                                    var17.addLast(var22);
                                 }
                              }
                           }
                        }
                     }

                     if (var9) {
                        for(BlockPos var31 : var17) {
                           BlockEntity var34 = var12.getBlockEntity(var31);
                           if (var34 instanceof Inventory) {
                              ((Inventory)var34).clear();
                           }

                           var12.setBlockState(var31, Blocks.BARRIER.defaultState(), 2);
                        }

                        for(BlockPos var32 : var17) {
                           var12.setBlockState(var32, Blocks.AIR.defaultState(), 3);
                        }
                     }

                     ArrayList var30 = Lists.newArrayList();
                     var30.addAll(var14);
                     var30.addAll(var15);
                     var30.addAll(var16);
                     List var33 = Lists.reverse(var30);

                     for(CloneCommand.ClonedBlock var40 : var33) {
                        BlockEntity var45 = var12.getBlockEntity(var40.pos);
                        if (var45 instanceof Inventory) {
                           ((Inventory)var45).clear();
                        }

                        var12.setBlockState(var40.pos, Blocks.BARRIER.defaultState(), 2);
                     }

                     var8 = 0;

                     for(CloneCommand.ClonedBlock var41 : var30) {
                        if (var12.setBlockState(var41.pos, var41.state, 2)) {
                           ++var8;
                        }
                     }

                     for(CloneCommand.ClonedBlock var42 : var15) {
                        BlockEntity var46 = var12.getBlockEntity(var42.pos);
                        if (var42.nbt != null && var46 != null) {
                           var42.nbt.putInt("x", var42.pos.getX());
                           var42.nbt.putInt("y", var42.pos.getY());
                           var42.nbt.putInt("z", var42.pos.getZ());
                           var46.readNbt(var42.nbt);
                           var46.markDirty();
                        }

                        var12.setBlockState(var42.pos, var42.state, 2);
                     }

                     for(CloneCommand.ClonedBlock var43 : var33) {
                        var12.onBlockChanged(var43.pos, var43.state.getBlock());
                     }

                     List var39 = var12.getScheduledTicks(var6, false);
                     if (var39 != null) {
                        for(ScheduledTick var47 : var39) {
                           if (var6.contains(var47.pos)) {
                              BlockPos var48 = var47.pos.add(var18);
                              var12.loadScheduledTick(var48, var47.getBlock(), (int)(var47.time - var12.getData().getTime()), var47.priority);
                           }
                        }
                     }

                     if (var8 <= 0) {
                        throw new CommandException("commands.clone.failed");
                     } else {
                        source.addResult(CommandResults.Type.AFFECTED_BLOCKS, var8);
                        sendSuccess(source, this, "commands.clone.success", new Object[]{var8});
                     }
                  } else {
                     throw new CommandException("commands.clone.outOfWorld");
                  }
               } else {
                  throw new CommandException("commands.clone.outOfWorld");
               }
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 10) {
         return suggestMatching(args, new String[]{"replace", "masked", "filtered"});
      } else if (args.length == 11) {
         return suggestMatching(args, new String[]{"normal", "force", "move"});
      } else {
         return args.length == 12 && "filtered".equals(args[9]) ? suggestMatching(args, Block.REGISTRY.keySet()) : null;
      }
   }

   static class ClonedBlock {
      public final BlockPos pos;
      public final BlockState state;
      public final NbtCompound nbt;

      public ClonedBlock(BlockPos pos, BlockState state, NbtCompound nbt) {
         this.pos = pos;
         this.state = state;
         this.nbt = nbt;
      }
   }
}
