package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.EntityNotFoundException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpreadPlayersCommand extends Command {
   @Override
   public String getName() {
      return "spreadplayers";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.spreadplayers.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 6) {
         throw new IncorrectUsageException("commands.spreadplayers.usage");
      } else {
         int var3 = 0;
         BlockPos var4 = source.getSourceBlockPos();
         double var5 = parseRawCoordinate((double)var4.getX(), args[var3++], true);
         double var7 = parseRawCoordinate((double)var4.getZ(), args[var3++], true);
         double var9 = parseDouble(args[var3++], 0.0);
         double var11 = parseDouble(args[var3++], var9 + 1.0);
         boolean var13 = parseBoolean(args[var3++]);
         ArrayList var14 = Lists.newArrayList();

         while(var3 < args.length) {
            String var15 = args[var3++];
            if (TargetSelector.isValid(var15)) {
               List var16 = TargetSelector.select(source, var15, LivingEntity.class);
               if (var16.size() == 0) {
                  throw new EntityNotFoundException();
               }

               var14.addAll(var16);
            } else {
               ServerPlayerEntity var22 = MinecraftServer.getInstance().getPlayerManager().get(var15);
               if (var22 == null) {
                  throw new PlayerNotFoundException();
               }

               var14.add(var22);
            }
         }

         source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var14.size());
         if (var14.isEmpty()) {
            throw new EntityNotFoundException();
         } else {
            source.sendMessage(new TranslatableText("commands.spreadplayers.spreading." + (var13 ? "teams" : "players"), var14.size(), var11, var5, var7, var9));
            this.spreadPlayers(source, var14, new SpreadPlayersCommand.Pile(var5, var7), var9, var11, ((LivingEntity)var14.get(0)).world, var13);
         }
      }
   }

   private void spreadPlayers(
      CommandSource source, List players, SpreadPlayersCommand.Pile pile, double spreadDistance, double maxRange, World world, boolean teams
   ) {
      Random var10 = new Random();
      double var11 = pile.x - maxRange;
      double var13 = pile.z - maxRange;
      double var15 = pile.x + maxRange;
      double var17 = pile.z + maxRange;
      SpreadPlayersCommand.Pile[] var19 = this.makePiles(var10, teams ? this.getTeamCount(players) : players.size(), var11, var13, var15, var17);
      int var20 = this.spreadPlayers(pile, spreadDistance, world, var10, var11, var13, var15, var17, var19, teams);
      double var21 = this.teleportPlayers(players, world, var19, teams);
      sendSuccess(source, this, "commands.spreadplayers.success." + (teams ? "teams" : "players"), new Object[]{var19.length, pile.x, pile.z});
      if (var19.length > 1) {
         source.sendMessage(new TranslatableText("commands.spreadplayers.info." + (teams ? "teams" : "players"), String.format("%.2f", var21), var20));
      }
   }

   private int getTeamCount(List entities) {
      HashSet var2 = Sets.newHashSet();

      for(LivingEntity var4 : entities) {
         if (var4 instanceof PlayerEntity) {
            var2.add(var4.getScoreboardTeam());
         } else {
            var2.add(null);
         }
      }

      return var2.size();
   }

   private int spreadPlayers(
      SpreadPlayersCommand.Pile pile,
      double spreadDistance,
      World world,
      Random rand,
      double minX,
      double minZ,
      double maxX,
      double maxZ,
      SpreadPlayersCommand.Pile[] piles,
      boolean teams
   ) {
      boolean var16 = true;
      double var18 = Float.MAX_VALUE;

      int var17;
      for(var17 = 0; var17 < 10000 && var16; ++var17) {
         var16 = false;
         var18 = Float.MAX_VALUE;

         for(int var20 = 0; var20 < piles.length; ++var20) {
            SpreadPlayersCommand.Pile var21 = piles[var20];
            int var22 = 0;
            SpreadPlayersCommand.Pile var23 = new SpreadPlayersCommand.Pile();

            for(int var24 = 0; var24 < piles.length; ++var24) {
               if (var20 != var24) {
                  SpreadPlayersCommand.Pile var25 = piles[var24];
                  double var26 = var21.distanceTo(var25);
                  var18 = Math.min(var26, var18);
                  if (var26 < spreadDistance) {
                     ++var22;
                     var23.x += var25.x - var21.x;
                     var23.z += var25.z - var21.z;
                  }
               }
            }

            if (var22 > 0) {
               var23.x /= (double)var22;
               var23.z /= (double)var22;
               double var32 = (double)var23.absolute();
               if (var32 > 0.0) {
                  var23.normalize();
                  var21.subtract(var23);
               } else {
                  var21.setPileLocation(rand, minX, minZ, maxX, maxZ);
               }

               var16 = true;
            }

            if (var21.clamp(minX, minZ, maxX, maxZ)) {
               var16 = true;
            }
         }

         if (!var16) {
            for(SpreadPlayersCommand.Pile var31 : piles) {
               if (!var31.isNotFireOrLiquidAtTopOfWorld(world)) {
                  var31.setPileLocation(rand, minX, minZ, maxX, maxZ);
                  var16 = true;
               }
            }
         }
      }

      if (var17 >= 10000) {
         throw new CommandException(
            "commands.spreadplayers.failure." + (teams ? "teams" : "players"), piles.length, pile.x, pile.z, String.format("%.2f", var18)
         );
      } else {
         return var17;
      }
   }

   private double teleportPlayers(List players, World world, SpreadPlayersCommand.Pile[] piles, boolean teams) {
      double var5 = 0.0;
      int var7 = 0;
      HashMap var8 = Maps.newHashMap();

      for(int var9 = 0; var9 < players.size(); ++var9) {
         LivingEntity var10 = (LivingEntity)players.get(var9);
         SpreadPlayersCommand.Pile var11;
         if (teams) {
            AbstractTeam var12 = var10 instanceof PlayerEntity ? var10.getScoreboardTeam() : null;
            if (!var8.containsKey(var12)) {
               var8.put(var12, piles[var7++]);
            }

            var11 = (SpreadPlayersCommand.Pile)var8.get(var12);
         } else {
            var11 = piles[var7++];
         }

         var10.refreshPosition(
            (double)((float)MathHelper.floor(var11.x) + 0.5F), (double)var11.getAboveHighestNonAir(world), (double)MathHelper.floor(var11.z) + 0.5
         );
         double var17 = Double.MAX_VALUE;

         for(int var14 = 0; var14 < piles.length; ++var14) {
            if (var11 != piles[var14]) {
               double var15 = var11.distanceTo(piles[var14]);
               var17 = Math.min(var15, var17);
            }
         }

         var5 += var17;
      }

      return var5 / (double)players.size();
   }

   private SpreadPlayersCommand.Pile[] makePiles(Random rand, int count, double minX, double minZ, double maxX, double maxZ) {
      SpreadPlayersCommand.Pile[] var11 = new SpreadPlayersCommand.Pile[count];

      for(int var12 = 0; var12 < var11.length; ++var12) {
         SpreadPlayersCommand.Pile var13 = new SpreadPlayersCommand.Pile();
         var13.setPileLocation(rand, minX, minZ, maxX, maxZ);
         var11[var12] = var13;
      }

      return var11;
   }

   static class Pile {
      double x;
      double z;

      Pile() {
      }

      Pile(double x, double z) {
         this.x = x;
         this.z = z;
      }

      double distanceTo(SpreadPlayersCommand.Pile other) {
         double var2 = this.x - other.x;
         double var4 = this.z - other.z;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void normalize() {
         double var1 = (double)this.absolute();
         this.x /= var1;
         this.z /= var1;
      }

      float absolute() {
         return MathHelper.sqrt(this.x * this.x + this.z * this.z);
      }

      public void subtract(SpreadPlayersCommand.Pile other) {
         this.x -= other.x;
         this.z -= other.z;
      }

      public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
         boolean var9 = false;
         if (this.x < minX) {
            this.x = minX;
            var9 = true;
         } else if (this.x > maxX) {
            this.x = maxX;
            var9 = true;
         }

         if (this.z < minZ) {
            this.z = minZ;
            var9 = true;
         } else if (this.z > maxZ) {
            this.z = maxZ;
            var9 = true;
         }

         return var9;
      }

      public int getAboveHighestNonAir(World world) {
         BlockPos var2 = new BlockPos(this.x, 256.0, this.z);

         while(var2.getY() > 0) {
            var2 = var2.down();
            if (world.getBlockState(var2).getBlock().getMaterial() != Material.AIR) {
               return var2.getY() + 1;
            }
         }

         return 257;
      }

      public boolean isNotFireOrLiquidAtTopOfWorld(World world) {
         BlockPos var2 = new BlockPos(this.x, 256.0, this.z);

         while(var2.getY() > 0) {
            var2 = var2.down();
            Material var3 = world.getBlockState(var2).getBlock().getMaterial();
            if (var3 != Material.AIR) {
               return !var3.isLiquid() && var3 != Material.FIRE;
            }
         }

         return false;
      }

      public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
         this.x = MathHelper.nextDouble(random, minX, maxX);
         this.z = MathHelper.nextDouble(random, minZ, maxZ);
      }
   }
}
