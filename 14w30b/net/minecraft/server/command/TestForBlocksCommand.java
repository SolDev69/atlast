package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBox;

public class TestForBlocksCommand extends Command {
   @Override
   public String getName() {
      return "testforblocks";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.compare.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 9) {
         throw new IncorrectUsageException("commands.compare.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         BlockPos var4 = parseBlockPos(source, args, 3, false);
         BlockPos var5 = parseBlockPos(source, args, 6, false);
         StructureBox var6 = new StructureBox(var3, var4);
         StructureBox var7 = new StructureBox(var5, var5.add(var6.getDiagonal()));
         int var8 = var6.getSpanX() * var6.getSpanY() * var6.getSpanZ();
         if (var8 > 524288) {
            throw new CommandException("commands.compare.tooManyBlocks", var8, 524288);
         } else if (var6.minY >= 0 && var6.maxY < 256 && var7.minY >= 0 && var7.maxY < 256) {
            World var9 = source.getSourceWorld();
            if (var9.isRegionLoaded(var6) && var9.isRegionLoaded(var7)) {
               boolean var10 = false;
               if (args.length > 9 && args[9].equals("masked")) {
                  var10 = true;
               }

               var8 = 0;
               BlockPos var11 = new BlockPos(var7.minX - var6.minX, var7.minY - var6.minY, var7.minZ - var6.minZ);

               for(int var12 = var6.minZ; var12 <= var6.maxZ; ++var12) {
                  for(int var13 = var6.minY; var13 <= var6.maxY; ++var13) {
                     for(int var14 = var6.minX; var14 <= var6.maxX; ++var14) {
                        BlockPos var15 = new BlockPos(var14, var13, var12);
                        BlockPos var16 = var15.add(var11);
                        boolean var17 = false;
                        BlockState var18 = var9.getBlockState(var15);
                        if (!var10 || var18.getBlock() != Blocks.AIR) {
                           if (var18 == var9.getBlockState(var16)) {
                              BlockEntity var19 = var9.getBlockEntity(var15);
                              BlockEntity var20 = var9.getBlockEntity(var16);
                              if (var19 != null && var20 != null) {
                                 NbtCompound var21 = new NbtCompound();
                                 var19.writeNbt(var21);
                                 var21.remove("x");
                                 var21.remove("y");
                                 var21.remove("z");
                                 NbtCompound var22 = new NbtCompound();
                                 var20.writeNbt(var22);
                                 var22.remove("x");
                                 var22.remove("y");
                                 var22.remove("z");
                                 if (!var21.equals(var22)) {
                                    var17 = true;
                                 }
                              } else if (var19 != null) {
                                 var17 = true;
                              }
                           } else {
                              var17 = true;
                           }

                           ++var8;
                           if (var17) {
                              throw new CommandException("commands.compare.failed");
                           }
                        }
                     }
                  }
               }

               source.addResult(CommandResults.Type.AFFECTED_BLOCKS, var8);
               sendSuccess(source, this, "commands.compare.success", new Object[]{var8});
            } else {
               throw new CommandException("commands.compare.outOfWorld");
            }
         } else {
            throw new CommandException("commands.compare.outOfWorld");
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 10 ? suggestMatching(args, new String[]{"masked", "all"}) : null;
   }
}
