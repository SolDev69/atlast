package net.minecraft.server.command;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDataCommand extends Command {
   @Override
   public String getName() {
      return "blockdata";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.blockdata.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 4) {
         throw new IncorrectUsageException("commands.blockdata.usage");
      } else {
         source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 0);
         BlockPos var3 = parseBlockPos(source, args, 0, false);
         World var4 = source.getSourceWorld();
         if (!var4.isLoaded(var3)) {
            throw new CommandException("commands.blockdata.outOfWorld");
         } else {
            BlockEntity var5 = var4.getBlockEntity(var3);
            if (var5 == null) {
               throw new CommandException("commands.blockdata.notValid");
            } else {
               NbtCompound var6 = new NbtCompound();
               var5.writeNbt(var6);
               NbtCompound var7 = (NbtCompound)var6.copy();

               NbtCompound var8;
               try {
                  var8 = StringNbtReader.parse(parseText(source, args, 3).buildString());
               } catch (NbtException var10) {
                  throw new CommandException("commands.blockdata.tagError", var10.getMessage());
               }

               var6.merge(var8);
               var6.putInt("x", var3.getX());
               var6.putInt("y", var3.getY());
               var6.putInt("z", var3.getZ());
               if (var6.equals(var7)) {
                  throw new CommandException("commands.blockdata.failed", var6.toString());
               } else {
                  var5.readNbt(var6);
                  var5.markDirty();
                  var4.onBlockChanged(var3);
                  source.addResult(CommandResults.Type.AFFECTED_BLOCKS, 1);
                  sendSuccess(source, this, "commands.blockdata.success", new Object[]{var6.toString()});
               }
            }
         }
      }
   }
}
