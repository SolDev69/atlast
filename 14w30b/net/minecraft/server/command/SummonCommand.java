package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SummonCommand extends Command {
   @Override
   public String getName() {
      return "summon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.summon.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.summon.usage");
      } else {
         String var3 = args[0];
         BlockPos var4 = source.getSourceBlockPos();
         double var5 = (double)var4.getX() + 0.5;
         double var7 = (double)var4.getY();
         double var9 = (double)var4.getZ() + 0.5;
         if (args.length >= 4) {
            var5 = parseRawCoordinate(var5, args[1], true);
            var7 = parseRawCoordinate(var7, args[2], false);
            var9 = parseRawCoordinate(var9, args[3], true);
            var4 = new BlockPos(var5, var7, var9);
         }

         World var11 = source.getSourceWorld();
         if (!var11.isLoaded(var4)) {
            throw new CommandException("commands.summon.outOfWorld");
         } else if ("LightningBolt".equals(var3)) {
            var11.addGlobalEntity(new LightningBoltEntity(var11, var5, var7, var9));
            sendSuccess(source, this, "commands.summon.success", new Object[0]);
         } else {
            NbtCompound var12 = new NbtCompound();
            boolean var13 = false;
            if (args.length >= 5) {
               Text var14 = parseText(source, args, 4);

               try {
                  var12 = StringNbtReader.parse(var14.buildString());
                  var13 = true;
               } catch (NbtException var19) {
                  throw new CommandException("commands.summon.tagError", var19.getMessage());
               }
            }

            var12.putString("id", var3);

            Entity var20;
            try {
               var20 = Entities.create(var12, var11);
            } catch (RuntimeException var18) {
               throw new CommandException("commands.summon.failed");
            }

            if (var20 == null) {
               throw new CommandException("commands.summon.failed");
            } else {
               var20.refreshPositionAndAngles(var5, var7, var9, var20.yaw, var20.pitch);
               if (!var13 && var20 instanceof MobEntity) {
                  ((MobEntity)var20).initialize(var11.getLocalDifficulty(new BlockPos(var20)), null);
               }

               var11.addEntity(var20);
               Entity var15 = var20;

               for(NbtCompound var16 = var12; var15 != null && var16.isType("Riding", 10); var16 = var16.getCompound("Riding")) {
                  Entity var17 = Entities.create(var16.getCompound("Riding"), var11);
                  if (var17 != null) {
                     var17.refreshPositionAndAngles(var5, var7, var9, var17.yaw, var17.pitch);
                     var11.addEntity(var17);
                     var15.startRiding(var17);
                  }

                  var15 = var17;
               }

               sendSuccess(source, this, "commands.summon.success", new Object[0]);
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, Entities.getIds()) : null;
   }
}
