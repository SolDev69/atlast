package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;

public class VillagePieces {
   public static void register() {
      StructureManager.registerPiece(VillagePieces.BlacksmithHouse.class, "ViBH");
      StructureManager.registerPiece(VillagePieces.LargeFarm.class, "ViDF");
      StructureManager.registerPiece(VillagePieces.Farm.class, "ViF");
      StructureManager.registerPiece(VillagePieces.LampPost.class, "ViL");
      StructureManager.registerPiece(VillagePieces.PriestHouse.class, "ViPH");
      StructureManager.registerPiece(VillagePieces.BalconyHouse.class, "ViSH");
      StructureManager.registerPiece(VillagePieces.TallHouse.class, "ViSmH");
      StructureManager.registerPiece(VillagePieces.ClerkHouse.class, "ViST");
      StructureManager.registerPiece(VillagePieces.SmallHouse.class, "ViS");
      StructureManager.registerPiece(VillagePieces.Start.class, "ViStart");
      StructureManager.registerPiece(VillagePieces.Road.class, "ViSR");
      StructureManager.registerPiece(VillagePieces.TinyHouse.class, "ViTRH");
      StructureManager.registerPiece(VillagePieces.Well.class, "ViW");
   }

   public static List getPieceChance(Random rand, int weight) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.BalconyHouse.class, 4, MathHelper.nextInt(rand, 2 + weight, 4 + weight * 2)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.ClerkHouse.class, 20, MathHelper.nextInt(rand, 0 + weight, 1 + weight)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.BlacksmithHouse.class, 20, MathHelper.nextInt(rand, 0 + weight, 2 + weight)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.TallHouse.class, 3, MathHelper.nextInt(rand, 2 + weight, 5 + weight * 3)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.PriestHouse.class, 15, MathHelper.nextInt(rand, 0 + weight, 2 + weight)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.LargeFarm.class, 3, MathHelper.nextInt(rand, 1 + weight, 4 + weight)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.Farm.class, 3, MathHelper.nextInt(rand, 2 + weight, 4 + weight * 2)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.SmallHouse.class, 15, MathHelper.nextInt(rand, 0, 1 + weight)));
      var2.add(new VillagePieces.VillagePieceWeight(VillagePieces.TinyHouse.class, 8, MathHelper.nextInt(rand, 0 + weight, 3 + weight * 2)));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         if (((VillagePieces.VillagePieceWeight)var3.next()).maxAmount == 0) {
            var3.remove();
         }
      }

      return var2;
   }

   private static int getTotalWeight(List weights) {
      boolean var1 = false;
      int var2 = 0;

      for(VillagePieces.VillagePieceWeight var4 : weights) {
         if (var4.maxAmount > 0 && var4.amountGenerated < var4.maxAmount) {
            var1 = true;
         }

         var2 += var4.weight;
      }

      return var1 ? var2 : -1;
   }

   private static VillagePieces.VillagePiece createPiece(
      VillagePieces.Start startPiece,
      VillagePieces.VillagePieceWeight weight,
      List pieces,
      Random random,
      int x,
      int y,
      int z,
      Direction facing,
      int generationDepth
   ) {
      Class var9 = weight.type;
      Object var10 = null;
      if (var9 == VillagePieces.BalconyHouse.class) {
         var10 = VillagePieces.BalconyHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.ClerkHouse.class) {
         var10 = VillagePieces.ClerkHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.BlacksmithHouse.class) {
         var10 = VillagePieces.BlacksmithHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.TallHouse.class) {
         var10 = VillagePieces.TallHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.PriestHouse.class) {
         var10 = VillagePieces.PriestHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.LargeFarm.class) {
         var10 = VillagePieces.LargeFarm.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.Farm.class) {
         var10 = VillagePieces.Farm.create(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.SmallHouse.class) {
         var10 = VillagePieces.SmallHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      } else if (var9 == VillagePieces.TinyHouse.class) {
         var10 = VillagePieces.TinyHouse.of(startPiece, pieces, random, x, y, z, facing, generationDepth);
      }

      return (VillagePieces.VillagePiece)var10;
   }

   private static VillagePieces.VillagePiece generateNextPiece(
      VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      int var8 = getTotalWeight(startPiece.weights);
      if (var8 <= 0) {
         return null;
      } else {
         int var9 = 0;

         while(var9 < 5) {
            ++var9;
            int var10 = random.nextInt(var8);

            for(VillagePieces.VillagePieceWeight var12 : startPiece.weights) {
               var10 -= var12.weight;
               if (var10 < 0) {
                  if (!var12.isLimitReached(generationDepth) || var12 == startPiece.previous && startPiece.weights.size() > 1) {
                     break;
                  }

                  VillagePieces.VillagePiece var13 = createPiece(startPiece, var12, pieces, random, x, y, z, facing, generationDepth);
                  if (var13 != null) {
                     ++var12.amountGenerated;
                     startPiece.previous = var12;
                     if (!var12.isLimitReached()) {
                        startPiece.weights.remove(var12);
                     }

                     return var13;
                  }
               }
            }
         }

         StructureBox var14 = VillagePieces.LampPost.findSize(startPiece, pieces, random, x, y, z, facing);
         return var14 != null ? new VillagePieces.LampPost(startPiece, generationDepth, random, var14, facing) : null;
      }
   }

   private static StructurePiece tryGenerateNextPiece(
      VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      if (generationDepth > 50) {
         return null;
      } else if (Math.abs(x - startPiece.getBoundingBox().minX) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 112) {
         VillagePieces.VillagePiece var8 = generateNextPiece(startPiece, pieces, random, x, y, z, facing, generationDepth + 1);
         if (var8 != null) {
            int var9 = (var8.box.minX + var8.box.maxX) / 2;
            int var10 = (var8.box.minZ + var8.box.maxZ) / 2;
            int var11 = var8.box.maxX - var8.box.minX;
            int var12 = var8.box.maxZ - var8.box.minZ;
            int var13 = var11 > var12 ? var11 : var12;
            if (startPiece.getBiomeSource().areBiomesValid(var9, var10, var13 / 2 + 4, VillageStructure.VALID_BIOMES)) {
               pieces.add(var8);
               startPiece.buildingPieces.add(var8);
               return var8;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static StructurePiece generateRoadPiece(
      VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      if (generationDepth > 3 + startPiece.size) {
         return null;
      } else if (Math.abs(x - startPiece.getBoundingBox().minX) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 112) {
         StructureBox var8 = VillagePieces.Road.findSize(startPiece, pieces, random, x, y, z, facing);
         if (var8 != null && var8.minY > 10) {
            VillagePieces.Road var9 = new VillagePieces.Road(startPiece, generationDepth, random, var8, facing);
            int var10 = (var9.box.minX + var9.box.maxX) / 2;
            int var11 = (var9.box.minZ + var9.box.maxZ) / 2;
            int var12 = var9.box.maxX - var9.box.minX;
            int var13 = var9.box.maxZ - var9.box.minZ;
            int var14 = var12 > var13 ? var12 : var13;
            if (startPiece.getBiomeSource().areBiomesValid(var10, var11, var14 / 2 + 4, VillageStructure.VALID_BIOMES)) {
               pieces.add(var9);
               startPiece.roadPieces.add(var9);
               return var9;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static class BalconyHouse extends VillagePieces.VillagePiece {
      private boolean hasRooftopTerrace;

      public BalconyHouse() {
      }

      public BalconyHouse(VillagePieces.Start statPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(statPiece, generationDepth);
         this.facing = facing;
         this.box = box;
         this.hasRooftopTerrace = random.nextBoolean();
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Terrace", this.hasRooftopTerrace);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasRooftopTerrace = nbt.getBoolean("Terrace");
      }

      public static VillagePieces.BalconyHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 5, 6, 5, facing);
         return StructurePiece.getIntersectingPiece(pieces, var8) != null
            ? null
            : new VillagePieces.BalconyHouse(startPiece, generationDepth, random, var8, facing);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 6 - 1, 0);
         }

         this.fillWithOutline(world, box, 0, 0, 0, 4, 0, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 0, 4, 4, 4, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 1, 4, 1, 3, 4, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 1, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 2, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 3, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 1, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 2, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 3, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 1, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 2, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 3, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 1, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 2, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 3, 4, box);
         this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 1, 4, 3, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 4, 3, 3, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 2, 2, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 1, 1, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 1, 2, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 1, 3, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 2, 3, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 3, 3, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 3, 2, 0, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 3, 1, 0, box);
         if (this.getBlockState(world, 2, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 2, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 2, 0, -1, box);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 3, 3, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         if (this.hasRooftopTerrace) {
            this.setBlockState(world, Blocks.FENCE.defaultState(), 0, 5, 0, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 5, 0, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 2, 5, 0, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 3, 5, 0, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 5, 0, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 0, 5, 4, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 5, 4, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 2, 5, 4, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 3, 5, 4, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 5, 4, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 5, 1, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 5, 2, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 5, 3, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 0, 5, 1, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 0, 5, 2, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 0, 5, 3, box);
         }

         if (this.hasRooftopTerrace) {
            int var4 = this.postProcessBlockMetadata(Blocks.LADDER, 3);
            this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var4), 3, 1, 3, box);
            this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var4), 3, 2, 3, box);
            this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var4), 3, 3, 3, box);
            this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var4), 3, 4, 3, box);
         }

         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing), 2, 3, 1, box);

         for(int var6 = 0; var6 < 5; ++var6) {
            for(int var5 = 0; var5 < 5; ++var5) {
               this.fillAirColumnUp(world, var5, 6, var6, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var5, -1, var6, box);
            }
         }

         this.spawnVillagers(world, box, 1, 1, 2, 1);
         return true;
      }
   }

   public static class BlacksmithHouse extends VillagePieces.VillagePiece {
      public BlacksmithHouse() {
      }

      public BlacksmithHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static VillagePieces.BlacksmithHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 9, 9, 6, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.BlacksmithHouse(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 9 - 1, 0);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 7, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 8, 0, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 8, 5, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 6, 1, 8, 6, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 7, 2, 8, 7, 3, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         int var4 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3);
         int var5 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 2);

         for(int var6 = -1; var6 <= 2; ++var6) {
            for(int var7 = 0; var7 <= 8; ++var7) {
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var4), var7, 6 + var6, var6, box);
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var5), var7, 6 + var6, 5 - var6, box);
            }
         }

         this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 5, 8, 1, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 1, 0, 8, 1, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 2, 1, 0, 7, 1, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 4, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 5, 0, 4, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 2, 5, 8, 4, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 2, 0, 8, 4, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 1, 0, 4, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 5, 7, 4, 5, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 2, 1, 8, 4, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 0, 7, 4, 0, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 2, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 2, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 6, 2, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 3, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 3, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 6, 3, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 3, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 3, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 3, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 3, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 5, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 3, 2, 5, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 2, 5, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 6, 2, 5, box);
         this.fillWithOutline(world, box, 1, 4, 1, 7, 4, 1, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 4, 4, 7, 4, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 7, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 0)), 7, 1, 3, box);
         int var9 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var9), 6, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var9), 5, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var9), 4, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var9), 3, 1, 4, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 6, 1, 3, box);
         this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.defaultState(), 6, 2, 3, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 1, 3, box);
         this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.defaultState(), 4, 2, 3, box);
         this.setBlockState(world, Blocks.CRAFTING_TABLE.defaultState(), 7, 1, 1, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 1, 1, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 1, 2, 0, box);
         this.placeWoodenDoor(world, box, random, 1, 1, 0, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));
         if (this.getBlockState(world, 1, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 1, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 1, 0, -1, box);
         }

         for(int var10 = 0; var10 < 6; ++var10) {
            for(int var8 = 0; var8 < 9; ++var8) {
               this.fillAirColumnUp(world, var8, 9, var10, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var8, -1, var10, box);
            }
         }

         this.spawnVillagers(world, box, 2, 1, 2, 1);
         return true;
      }

      @Override
      protected int getVillagerType(int index, int profession) {
         return 1;
      }
   }

   public static class ClerkHouse extends VillagePieces.VillagePiece {
      public ClerkHouse() {
      }

      public ClerkHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static VillagePieces.ClerkHouse of(
         VillagePieces.Start statPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 5, 12, 9, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.ClerkHouse(statPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 12 - 1, 0);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 3, 3, 7, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 1, 3, 9, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 3, 0, 8, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 0, 3, 10, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 1, 0, 10, 3, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 1, 4, 10, 3, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 4, 0, 4, 7, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 4, 4, 4, 7, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 8, 3, 4, 8, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 4, 3, 10, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 5, 3, 5, 7, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 9, 0, 4, 9, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 0, 4, 4, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 0, 11, 2, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 11, 2, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 2, 11, 0, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 2, 11, 4, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 1, 1, 6, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 1, 1, 7, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 2, 1, 7, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 3, 1, 6, box);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 3, 1, 7, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 1, 1, 5, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 2, 1, 6, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 3, 1, 5, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 1)), 1, 2, 7, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 0)), 3, 2, 7, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 3, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 3, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 6, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 7, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 6, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 7, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 6, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 7, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 6, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 7, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 3, 6, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 3, 6, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 3, 8, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.getOpposite()), 2, 4, 7, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.clockwiseY()), 1, 4, 6, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.counterClockwiseY()), 3, 4, 6, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing), 2, 4, 5, box);
         int var4 = this.postProcessBlockMetadata(Blocks.LADDER, 4);

         for(int var5 = 1; var5 <= 9; ++var5) {
            this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var4), 3, var5, 3, box);
         }

         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 1, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 2, 0, box);
         this.placeWoodenDoor(world, box, random, 2, 1, 0, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));
         if (this.getBlockState(world, 2, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 2, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 2, 0, -1, box);
         }

         for(int var7 = 0; var7 < 9; ++var7) {
            for(int var6 = 0; var6 < 5; ++var6) {
               this.fillAirColumnUp(world, var6, 12, var7, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var6, -1, var7, box);
            }
         }

         this.spawnVillagers(world, box, 2, 1, 2, 1);
         return true;
      }

      @Override
      protected int getVillagerType(int index, int profession) {
         return 2;
      }
   }

   public static class Farm extends VillagePieces.VillagePiece {
      private Block cropA;
      private Block cropB;

      public Farm() {
      }

      public Farm(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
         this.cropA = this.pickCrop(random);
         this.cropB = this.pickCrop(random);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("CA", Block.REGISTRY.getId(this.cropA));
         nbt.putInt("CB", Block.REGISTRY.getId(this.cropB));
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.cropA = Block.byRawId(nbt.getInt("CA"));
         this.cropB = Block.byRawId(nbt.getInt("CB"));
      }

      private Block pickCrop(Random random) {
         switch(random.nextInt(5)) {
            case 0:
               return Blocks.CARROTS;
            case 1:
               return Blocks.POTATOES;
            default:
               return Blocks.WHEAT;
         }
      }

      public static VillagePieces.Farm create(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 7, 4, 9, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.Farm(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 4 - 1, 0);
         }

         this.fillWithOutline(world, box, 0, 1, 0, 6, 4, 8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 0, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 6, 0, 0, 6, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 5, 0, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 8, 5, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 3, 0, 1, 3, 0, 7, Blocks.WATER.defaultState(), Blocks.WATER.defaultState(), false);

         for(int var4 = 1; var4 <= 7; ++var4) {
            this.setBlockState(world, this.cropA.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 1, 1, var4, box);
            this.setBlockState(world, this.cropA.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 2, 1, var4, box);
            this.setBlockState(world, this.cropB.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 4, 1, var4, box);
            this.setBlockState(world, this.cropB.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 5, 1, var4, box);
         }

         for(int var6 = 0; var6 < 9; ++var6) {
            for(int var5 = 0; var5 < 7; ++var5) {
               this.fillAirColumnUp(world, var5, 4, var6, box);
               this.fillColumnDown(world, Blocks.DIRT.defaultState(), var5, -1, var6, box);
            }
         }

         return true;
      }
   }

   public static class LampPost extends VillagePieces.VillagePiece {
      public LampPost() {
      }

      public LampPost(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static StructureBox findSize(VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing) {
         StructureBox var7 = StructureBox.orient(x, y, z, 0, 0, 0, 3, 4, 2, facing);
         return StructurePiece.getIntersectingPiece(pieces, var7) != null ? null : var7;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 4 - 1, 0);
         }

         this.fillWithOutline(world, box, 0, 0, 0, 2, 3, 1, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 0, 0, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 1, 0, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 2, 0, box);
         this.setBlockState(world, Blocks.WOOL.getStateFromMetadata(DyeColor.WHITE.getMetadata()), 1, 3, 0, box);
         boolean var4 = this.facing == Direction.EAST || this.facing == Direction.NORTH;
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.clockwiseY()), var4 ? 2 : 0, 3, 0, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing), 1, 3, 1, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.counterClockwiseY()), var4 ? 0 : 2, 3, 0, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.getOpposite()), 1, 3, -1, box);
         return true;
      }
   }

   public static class LargeFarm extends VillagePieces.VillagePiece {
      private Block cropA;
      private Block cropB;
      private Block cropC;
      private Block cropD;

      public LargeFarm() {
      }

      public LargeFarm(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
         this.cropA = this.pickCrop(random);
         this.cropB = this.pickCrop(random);
         this.cropC = this.pickCrop(random);
         this.cropD = this.pickCrop(random);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("CA", Block.REGISTRY.getId(this.cropA));
         nbt.putInt("CB", Block.REGISTRY.getId(this.cropB));
         nbt.putInt("CC", Block.REGISTRY.getId(this.cropC));
         nbt.putInt("CD", Block.REGISTRY.getId(this.cropD));
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.cropA = Block.byRawId(nbt.getInt("CA"));
         this.cropB = Block.byRawId(nbt.getInt("CB"));
         this.cropC = Block.byRawId(nbt.getInt("CC"));
         this.cropD = Block.byRawId(nbt.getInt("CD"));
      }

      private Block pickCrop(Random random) {
         switch(random.nextInt(5)) {
            case 0:
               return Blocks.CARROTS;
            case 1:
               return Blocks.POTATOES;
            default:
               return Blocks.WHEAT;
         }
      }

      public static VillagePieces.LargeFarm of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 13, 4, 9, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.LargeFarm(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 4 - 1, 0);
         }

         this.fillWithOutline(world, box, 0, 1, 0, 12, 4, 8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.defaultState(), Blocks.FARMLAND.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 0, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 6, 0, 0, 6, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 12, 0, 0, 12, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 11, 0, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 8, 11, 0, 8, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 3, 0, 1, 3, 0, 7, Blocks.WATER.defaultState(), Blocks.WATER.defaultState(), false);
         this.fillWithOutline(world, box, 9, 0, 1, 9, 0, 7, Blocks.WATER.defaultState(), Blocks.WATER.defaultState(), false);

         for(int var4 = 1; var4 <= 7; ++var4) {
            this.setBlockState(world, this.cropA.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 1, 1, var4, box);
            this.setBlockState(world, this.cropA.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 2, 1, var4, box);
            this.setBlockState(world, this.cropB.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 4, 1, var4, box);
            this.setBlockState(world, this.cropB.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 5, 1, var4, box);
            this.setBlockState(world, this.cropC.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 7, 1, var4, box);
            this.setBlockState(world, this.cropC.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 8, 1, var4, box);
            this.setBlockState(world, this.cropD.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 10, 1, var4, box);
            this.setBlockState(world, this.cropD.getStateFromMetadata(MathHelper.nextInt(random, 2, 7)), 11, 1, var4, box);
         }

         for(int var6 = 0; var6 < 9; ++var6) {
            for(int var5 = 0; var5 < 13; ++var5) {
               this.fillAirColumnUp(world, var5, 4, var6, box);
               this.fillColumnDown(world, Blocks.DIRT.defaultState(), var5, -1, var6, box);
            }
         }

         return true;
      }
   }

   public static class PriestHouse extends VillagePieces.VillagePiece {
      public PriestHouse() {
      }

      public PriestHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static VillagePieces.PriestHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 9, 7, 11, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.PriestHouse(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 7 - 1, 0);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 7, 4, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 1, 6, 8, 4, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 0, 6, 8, 0, 10, Blocks.DIRT.defaultState(), Blocks.DIRT.defaultState(), false);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, 0, 6, box);
         this.fillWithOutline(world, box, 2, 1, 6, 2, 1, 10, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 1, 6, 8, 1, 10, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 10, 7, 1, 10, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 1, 7, 0, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 0, 0, 8, 3, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 7, 1, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 5, 7, 1, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 0, 7, 3, 0, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 5, 7, 3, 5, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 1, 8, 4, 1, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 4, 8, 4, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 2, 8, 5, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 0, 4, 2, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 0, 4, 3, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 4, 2, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 4, 3, box);
         int var4 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3);
         int var5 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 2);

         for(int var6 = -1; var6 <= 2; ++var6) {
            for(int var7 = 0; var7 <= 8; ++var7) {
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var4), var7, 4 + var6, var6, box);
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var5), var7, 4 + var6, 5 - var6, box);
            }
         }

         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 2, 1, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 2, 4, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 1, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 3, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 5, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 3, 2, 5, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 2, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 6, 2, 5, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 2, 1, 3, box);
         this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.defaultState(), 2, 2, 3, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 1, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3)), 2, 1, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 1)), 1, 1, 3, box);
         this.fillWithOutline(world, box, 5, 0, 1, 7, 0, 3, Blocks.DOUBLE_STONE_SLAB.defaultState(), Blocks.DOUBLE_STONE_SLAB.defaultState(), false);
         this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.defaultState(), 6, 1, 1, box);
         this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.defaultState(), 6, 1, 2, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 1, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 2, 0, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing), 2, 3, 1, box);
         this.placeWoodenDoor(world, box, random, 2, 1, 0, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));
         if (this.getBlockState(world, 2, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 2, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 2, 0, -1, box);
         }

         this.setBlockState(world, Blocks.AIR.defaultState(), 6, 1, 5, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 6, 2, 5, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing.getOpposite()), 6, 3, 4, box);
         this.placeWoodenDoor(world, box, random, 6, 1, 5, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));

         for(int var8 = 0; var8 < 5; ++var8) {
            for(int var9 = 0; var9 < 9; ++var9) {
               this.fillAirColumnUp(world, var9, 7, var8, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var9, -1, var8, box);
            }
         }

         this.spawnVillagers(world, box, 4, 1, 2, 2);
         return true;
      }

      @Override
      protected int getVillagerType(int index, int profession) {
         return index == 0 ? 4 : super.getVillagerType(index, profession);
      }
   }

   public static class Road extends VillagePieces.RoadPiece {
      private int length;

      public Road() {
      }

      public Road(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
         this.length = Math.max(box.getSpanX(), box.getSpanZ());
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("Length", this.length);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.length = nbt.getInt("Length");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         boolean var4 = false;

         for(int var5 = random.nextInt(5); var5 < this.length - 8; var5 += 2 + random.nextInt(5)) {
            StructurePiece var6 = this.generatePieceLeft((VillagePieces.Start)start, pieces, random, 0, var5);
            if (var6 != null) {
               var5 += Math.max(var6.box.getSpanX(), var6.box.getSpanZ());
               var4 = true;
            }
         }

         for(int var7 = random.nextInt(5); var7 < this.length - 8; var7 += 2 + random.nextInt(5)) {
            StructurePiece var8 = this.generatePieceRight((VillagePieces.Start)start, pieces, random, 0, var7);
            if (var8 != null) {
               var7 += Math.max(var8.box.getSpanX(), var8.box.getSpanZ());
               var4 = true;
            }
         }

         if (var4 && random.nextInt(3) > 0 && this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.minX - 1, this.box.minY, this.box.minZ, Direction.WEST, this.getGenerationDepth()
                  );
                  break;
               case SOUTH:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.minX - 1, this.box.minY, this.box.maxZ - 2, Direction.WEST, this.getGenerationDepth()
                  );
                  break;
               case WEST:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.minX, this.box.minY, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
                  );
                  break;
               case EAST:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start,
                     pieces,
                     random,
                     this.box.maxX - 2,
                     this.box.minY,
                     this.box.minZ - 1,
                     Direction.NORTH,
                     this.getGenerationDepth()
                  );
            }
         }

         if (var4 && random.nextInt(3) > 0 && this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.minZ, Direction.EAST, this.getGenerationDepth()
                  );
                  break;
               case SOUTH:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.maxZ - 2, Direction.EAST, this.getGenerationDepth()
                  );
                  break;
               case WEST:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start, pieces, random, this.box.minX, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, this.getGenerationDepth()
                  );
                  break;
               case EAST:
                  VillagePieces.generateRoadPiece(
                     (VillagePieces.Start)start,
                     pieces,
                     random,
                     this.box.maxX - 2,
                     this.box.minY,
                     this.box.maxZ + 1,
                     Direction.SOUTH,
                     this.getGenerationDepth()
                  );
            }
         }
      }

      public static StructureBox findSize(VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing) {
         for(int var7 = 7 * MathHelper.nextInt(random, 3, 5); var7 >= 7; var7 -= 7) {
            StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 3, 3, var7, facing);
            if (StructurePiece.getIntersectingPiece(pieces, var8) == null) {
               return var8;
            }
         }

         return null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         BlockState var4 = this.updateBlock(Blocks.GRAVEL.defaultState());
         BlockState var5 = this.updateBlock(Blocks.COBBLESTONE.defaultState());

         for(int var6 = this.box.minX; var6 <= this.box.maxX; ++var6) {
            for(int var7 = this.box.minZ; var7 <= this.box.maxZ; ++var7) {
               BlockPos var8 = new BlockPos(var6, 64, var7);
               if (box.contains(var8)) {
                  var8 = world.getSurfaceHeight(var8).down();
                  world.setBlockState(var8, var4, 2);
                  world.setBlockState(var8.down(), var5, 2);
               }
            }
         }

         return true;
      }
   }

   public abstract static class RoadPiece extends VillagePieces.VillagePiece {
      public RoadPiece() {
      }

      protected RoadPiece(VillagePieces.Start c_68zuedqav, int i) {
         super(c_68zuedqav, i);
      }
   }

   public static class SmallHouse extends VillagePieces.VillagePiece {
      private static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.DIAMOND, 0, 1, 3, 3),
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
            new LootEntry(Items.GOLD_INGOT, 0, 1, 3, 5),
            new LootEntry(Items.BREAD, 0, 1, 3, 15),
            new LootEntry(Items.APPLE, 0, 1, 3, 15),
            new LootEntry(Items.IRON_PICKAXE, 0, 1, 1, 5),
            new LootEntry(Items.IRON_SWORD, 0, 1, 1, 5),
            new LootEntry(Items.IRON_CHESTPLATE, 0, 1, 1, 5),
            new LootEntry(Items.IRON_HELMET, 0, 1, 1, 5),
            new LootEntry(Items.IRON_LEGGINGS, 0, 1, 1, 5),
            new LootEntry(Items.IRON_BOOTS, 0, 1, 1, 5),
            new LootEntry(Item.byBlock(Blocks.OBSIDIAN), 0, 3, 7, 5),
            new LootEntry(Item.byBlock(Blocks.SAPLING), 0, 3, 7, 5),
            new LootEntry(Items.SADDLE, 0, 1, 1, 3),
            new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
         }
      );
      private boolean hasChest;

      public SmallHouse() {
      }

      public SmallHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static VillagePieces.SmallHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 10, 6, 7, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.SmallHouse(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Chest", this.hasChest);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasChest = nbt.getBoolean("Chest");
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 6 - 1, 0);
         }

         this.fillWithOutline(world, box, 0, 1, 0, 9, 4, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 9, 0, 6, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 0, 9, 4, 6, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 1, 8, 5, 5, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 0, 0, 4, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 0, 3, 4, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 6, 0, 4, 6, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 3, 3, 1, box);
         this.fillWithOutline(world, box, 3, 1, 2, 3, 3, 2, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 3, 5, 3, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 5, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 6, 5, 3, 6, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 1, 0, 5, 3, 0, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 9, 1, 0, 9, 3, 0, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 1, 4, 9, 4, 6, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.setBlockState(world, Blocks.FLOWING_LAVA.defaultState(), 7, 1, 5, box);
         this.setBlockState(world, Blocks.FLOWING_LAVA.defaultState(), 8, 1, 5, box);
         this.setBlockState(world, Blocks.IRON_BARS.defaultState(), 9, 2, 5, box);
         this.setBlockState(world, Blocks.IRON_BARS.defaultState(), 9, 2, 4, box);
         this.fillWithOutline(world, box, 7, 2, 4, 8, 2, 5, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, 1, 3, box);
         this.setBlockState(world, Blocks.FURNACE.defaultState(), 6, 2, 3, box);
         this.setBlockState(world, Blocks.FURNACE.defaultState(), 6, 3, 3, box);
         this.setBlockState(world, Blocks.DOUBLE_STONE_SLAB.defaultState(), 8, 1, 1, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 6, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 4, 2, 6, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 2, 1, 4, box);
         this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.defaultState(), 2, 2, 4, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 1, 1, 5, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3)), 2, 1, 5, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 1)), 1, 1, 4, box);
         if (!this.hasChest && box.contains(new BlockPos(this.transformX(5, 5), this.transformY(1), this.transformZ(5, 5)))) {
            this.hasChest = true;
            this.placeChestWithLoot(world, box, random, 5, 1, 5, LOOT_ENTRIES, 3 + random.nextInt(6));
         }

         for(int var4 = 6; var4 <= 8; ++var4) {
            if (this.getBlockState(world, var4, 0, -1, box).getBlock().getMaterial() == Material.AIR
               && this.getBlockState(world, var4, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), var4, 0, -1, box);
            }
         }

         for(int var6 = 0; var6 < 7; ++var6) {
            for(int var5 = 0; var5 < 10; ++var5) {
               this.fillAirColumnUp(world, var5, 6, var6, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var5, -1, var6, box);
            }
         }

         this.spawnVillagers(world, box, 7, 1, 1, 1);
         return true;
      }

      @Override
      protected int getVillagerType(int index, int profession) {
         return 3;
      }
   }

   public static class Start extends VillagePieces.Well {
      public BiomeSource biomeSource;
      public boolean isDesertVillage;
      public int size;
      public VillagePieces.VillagePieceWeight previous;
      public List weights;
      public List buildingPieces = Lists.newArrayList();
      public List roadPieces = Lists.newArrayList();

      public Start() {
      }

      public Start(BiomeSource biomeSource, int i, Random random, int x, int z, List children, int size) {
         super(null, 0, random, x, z);
         this.biomeSource = biomeSource;
         this.weights = children;
         this.size = size;
         Biome var8 = biomeSource.getBiomeOrDefault(new BlockPos(x, 0, z), Biome.DEFAULT);
         this.isDesertVillage = var8 == Biome.DESERT || var8 == Biome.DESERT_HILLS;
         this.setDesert(this.isDesertVillage);
      }

      public BiomeSource getBiomeSource() {
         return this.biomeSource;
      }
   }

   public static class TallHouse extends VillagePieces.VillagePiece {
      private boolean isTall;
      private int tablePlacement;

      public TallHouse() {
      }

      public TallHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
         this.isTall = random.nextBoolean();
         this.tablePlacement = random.nextInt(3);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("T", this.tablePlacement);
         nbt.putBoolean("C", this.isTall);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.tablePlacement = nbt.getInt("T");
         this.isTall = nbt.getBoolean("C");
      }

      public static VillagePieces.TallHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 4, 6, 5, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.TallHouse(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 6 - 1, 0);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 3, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 3, 0, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 1, 2, 0, 3, Blocks.DIRT.defaultState(), Blocks.DIRT.defaultState(), false);
         if (this.isTall) {
            this.fillWithOutline(world, box, 1, 4, 1, 2, 4, 3, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         } else {
            this.fillWithOutline(world, box, 1, 5, 1, 2, 5, 3, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         }

         this.setBlockState(world, Blocks.LOG.defaultState(), 1, 4, 0, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 2, 4, 0, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 1, 4, 4, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 2, 4, 4, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 4, 1, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 4, 2, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 4, 3, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 3, 4, 1, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 3, 4, 2, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 3, 4, 3, box);
         this.fillWithOutline(world, box, 0, 1, 0, 0, 3, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 0, 3, 3, 0, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 4, 0, 3, 4, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 4, 3, 3, 4, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 1, 3, 3, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 4, 2, 3, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 3, 2, 2, box);
         if (this.tablePlacement > 0) {
            this.setBlockState(world, Blocks.FENCE.defaultState(), this.tablePlacement, 1, 3, box);
            this.setBlockState(world, Blocks.WOODEN_PRESSURE_PLATE.defaultState(), this.tablePlacement, 2, 3, box);
         }

         this.setBlockState(world, Blocks.AIR.defaultState(), 1, 1, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 1, 2, 0, box);
         this.placeWoodenDoor(world, box, random, 1, 1, 0, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));
         if (this.getBlockState(world, 1, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 1, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 1, 0, -1, box);
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 4; ++var5) {
               this.fillAirColumnUp(world, var5, 6, var4, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var5, -1, var4, box);
            }
         }

         this.spawnVillagers(world, box, 1, 1, 2, 1);
         return true;
      }
   }

   public static class TinyHouse extends VillagePieces.VillagePiece {
      public TinyHouse() {
      }

      public TinyHouse(VillagePieces.Start startPiece, int generationDepth, Random random, StructureBox box, Direction facing) {
         super(startPiece, generationDepth);
         this.facing = facing;
         this.box = box;
      }

      public static VillagePieces.TinyHouse of(
         VillagePieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         StructureBox var8 = StructureBox.orient(x, y, z, 0, 0, 0, 9, 7, 12, facing);
         return isValidStructureBox(var8) && StructurePiece.getIntersectingPiece(pieces, var8) == null
            ? new VillagePieces.TinyHouse(startPiece, generationDepth, random, var8, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 7 - 1, 0);
         }

         this.fillWithOutline(world, box, 1, 1, 1, 7, 4, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 1, 6, 8, 4, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 0, 5, 8, 0, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 1, 7, 0, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 0, 3, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 0, 0, 8, 3, 10, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 7, 2, 0, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 5, 2, 1, 5, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 2, 0, 6, 2, 3, 10, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 3, 0, 10, 7, 3, 10, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 0, 7, 3, 0, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 5, 2, 3, 5, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 1, 8, 4, 1, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 4, 4, 3, 4, 4, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 2, 8, 5, 3, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 0, 4, 2, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 0, 4, 3, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 4, 2, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 4, 3, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 4, 4, box);
         int var4 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3);
         int var5 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 2);

         for(int var6 = -1; var6 <= 2; ++var6) {
            for(int var7 = 0; var7 <= 8; ++var7) {
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var4), var7, 4 + var6, var6, box);
               if ((var6 > -1 || var7 <= 1) && (var6 > 0 || var7 <= 3) && (var6 > 1 || var7 <= 4 || var7 >= 6)) {
                  this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var5), var7, 4 + var6, 5 - var6, box);
               }
            }
         }

         this.fillWithOutline(world, box, 3, 4, 5, 3, 4, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 4, 2, 7, 4, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 5, 4, 4, 5, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 5, 4, 6, 5, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 6, 3, 5, 6, 10, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
         int var10 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 0);

         for(int var11 = 4; var11 >= 1; --var11) {
            this.setBlockState(world, Blocks.PLANKS.defaultState(), var11, 2 + var11, 7 - var11, box);

            for(int var8 = 8 - var11; var8 <= 10; ++var8) {
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var10), var11, 2 + var11, var8, box);
            }
         }

         int var12 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 1);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 6, 6, 3, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 7, 5, 4, box);
         this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var12), 6, 6, 4, box);

         for(int var13 = 6; var13 <= 8; ++var13) {
            for(int var9 = 5; var9 <= 10; ++var9) {
               this.setBlockState(world, Blocks.OAK_STAIRS.getStateFromMetadata(var12), var13, 12 - var13, var9, box);
            }
         }

         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 2, 1, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 0, 2, 4, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 0, 2, 3, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 4, 2, 0, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 2, 0, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 6, 2, 0, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 1, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 2, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 3, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 4, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 2, 5, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 6, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 7, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 8, 2, 8, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 8, 2, 9, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 2, 2, 6, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 7, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 2, 2, 8, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 2, 2, 9, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 4, 4, 10, box);
         this.setBlockState(world, Blocks.GLASS_PANE.defaultState(), 5, 4, 10, box);
         this.setBlockState(world, Blocks.LOG.defaultState(), 6, 4, 10, box);
         this.setBlockState(world, Blocks.PLANKS.defaultState(), 5, 5, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 1, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 2, 0, box);
         this.setBlockState(world, Blocks.TORCH.defaultState().set(TorchBlock.FACING, this.facing), 2, 3, 1, box);
         this.placeWoodenDoor(world, box, random, 2, 1, 0, Direction.byIdHorizontal(this.postProcessBlockMetadata(Blocks.WOODEN_DOOR, 1)));
         this.fillWithOutline(world, box, 1, 0, -1, 3, 2, -1, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         if (this.getBlockState(world, 2, 0, -1, box).getBlock().getMaterial() == Material.AIR
            && this.getBlockState(world, 2, -1, -1, box).getBlock().getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3)), 2, 0, -1, box);
         }

         for(int var14 = 0; var14 < 5; ++var14) {
            for(int var16 = 0; var16 < 9; ++var16) {
               this.fillAirColumnUp(world, var16, 7, var14, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var16, -1, var14, box);
            }
         }

         for(int var15 = 5; var15 < 11; ++var15) {
            for(int var17 = 2; var17 < 9; ++var17) {
               this.fillAirColumnUp(world, var17, 7, var15, box);
               this.fillColumnDown(world, Blocks.COBBLESTONE.defaultState(), var17, -1, var15, box);
            }
         }

         this.spawnVillagers(world, box, 4, 1, 2, 2);
         return true;
      }
   }

   abstract static class VillagePiece extends StructurePiece {
      protected int hpos = -1;
      private int villageCount;
      private boolean desert;

      public VillagePiece() {
      }

      protected VillagePiece(VillagePieces.Start startPiece, int generationDepth) {
         super(generationDepth);
         if (startPiece != null) {
            this.desert = startPiece.isDesertVillage;
         }
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         nbt.putInt("HPos", this.hpos);
         nbt.putInt("VCount", this.villageCount);
         nbt.putBoolean("Desert", this.desert);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         this.hpos = nbt.getInt("HPos");
         this.villageCount = nbt.getInt("VCount");
         this.desert = nbt.getBoolean("Desert");
      }

      protected StructurePiece generatePieceLeft(VillagePieces.Start startPiece, List pieces, Random random, int yOffset, int offset) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX - 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.WEST, this.getGenerationDepth()
                  );
               case SOUTH:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX - 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.WEST, this.getGenerationDepth()
                  );
               case WEST:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
                  );
               case EAST:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
                  );
            }
         }

         return null;
      }

      protected StructurePiece generatePieceRight(VillagePieces.Start startPiece, List pieces, Random random, int yOffset, int offset) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.maxX + 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.EAST, this.getGenerationDepth()
                  );
               case SOUTH:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.maxX + 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.EAST, this.getGenerationDepth()
                  );
               case WEST:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.maxZ + 1, Direction.SOUTH, this.getGenerationDepth()
                  );
               case EAST:
                  return VillagePieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.maxZ + 1, Direction.SOUTH, this.getGenerationDepth()
                  );
            }
         }

         return null;
      }

      protected int updateHeight(World world, StructureBox box) {
         int var3 = 0;
         int var4 = 0;

         for(int var5 = this.box.minZ; var5 <= this.box.maxZ; ++var5) {
            for(int var6 = this.box.minX; var6 <= this.box.maxX; ++var6) {
               BlockPos var7 = new BlockPos(var6, 64, var5);
               if (box.contains(var7)) {
                  var3 += Math.max(world.getSurfaceHeight(var7).getY(), world.dimension.getMinSpawnY());
                  ++var4;
               }
            }
         }

         return var4 == 0 ? -1 : var3 / var4;
      }

      protected static boolean isValidStructureBox(StructureBox box) {
         return box != null && box.minY > 10;
      }

      protected void spawnVillagers(World world, StructureBox box, int x, int y, int z, int max) {
         if (this.villageCount < max) {
            for(int var7 = this.villageCount; var7 < max; ++var7) {
               int var8 = this.transformX(x + var7, z);
               int var9 = this.transformY(y);
               int var10 = this.transformZ(x + var7, z);
               if (!box.contains(new BlockPos(var8, var9, var10))) {
                  break;
               }

               ++this.villageCount;
               VillagerEntity var11 = new VillagerEntity(world);
               var11.refreshPositionAndAngles((double)var8 + 0.5, (double)var9, (double)var10 + 0.5, 0.0F, 0.0F);
               var11.initialize(world.getLocalDifficulty(new BlockPos(var11)), null);
               var11.setProfession(this.getVillagerType(var7, var11.getProfession()));
               world.addEntity(var11);
            }
         }
      }

      protected int getVillagerType(int index, int profession) {
         return profession;
      }

      protected BlockState updateBlock(BlockState block) {
         if (this.desert) {
            if (block.getBlock() == Blocks.LOG || block.getBlock() == Blocks.LOG2) {
               return Blocks.SANDSTONE.defaultState();
            }

            if (block.getBlock() == Blocks.COBBLESTONE) {
               return Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.DEFAULT.getIndex());
            }

            if (block.getBlock() == Blocks.PLANKS) {
               return Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex());
            }

            if (block.getBlock() == Blocks.OAK_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.defaultState().set(StairsBlock.FACING, block.get(StairsBlock.FACING));
            }

            if (block.getBlock() == Blocks.STONE_STAIRS) {
               return Blocks.SANDSTONE_STAIRS.defaultState().set(StairsBlock.FACING, block.get(StairsBlock.FACING));
            }

            if (block.getBlock() == Blocks.GRAVEL) {
               return Blocks.SANDSTONE.defaultState();
            }
         }

         return block;
      }

      @Override
      protected void setBlockState(World world, BlockState state, int x, int y, int z, StructureBox box) {
         BlockState var7 = this.updateBlock(state);
         super.setBlockState(world, var7, x, y, z, box);
      }

      @Override
      protected void fillWithOutline(
         World world, StructureBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState edge, BlockState filler, boolean avoidAir
      ) {
         BlockState var12 = this.updateBlock(edge);
         BlockState var13 = this.updateBlock(filler);
         super.fillWithOutline(world, box, minX, minY, minZ, maxX, maxY, maxZ, var12, var13, avoidAir);
      }

      @Override
      protected void fillColumnDown(World world, BlockState state, int x, int y, int z, StructureBox box) {
         BlockState var7 = this.updateBlock(state);
         super.fillColumnDown(world, var7, x, y, z, box);
      }

      protected void setDesert(boolean desert) {
         this.desert = desert;
      }
   }

   public static class VillagePieceWeight {
      public Class type;
      public final int weight;
      public int amountGenerated;
      public int maxAmount;

      public VillagePieceWeight(Class type, int weight, int maxAmount) {
         this.type = type;
         this.weight = weight;
         this.maxAmount = maxAmount;
      }

      public boolean isLimitReached(int direction) {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }

      public boolean isLimitReached() {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }
   }

   public static class Well extends VillagePieces.VillagePiece {
      public Well() {
      }

      public Well(VillagePieces.Start startPiece, int generationDepth, Random random, int x, int z) {
         super(startPiece, generationDepth);
         this.facing = Direction.Plane.HORIZONTAL.pick(random);
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               this.box = new StructureBox(x, 64, z, x + 6 - 1, 78, z + 6 - 1);
               break;
            default:
               this.box = new StructureBox(x, 64, z, x + 6 - 1, 78, z + 6 - 1);
         }
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         VillagePieces.generateRoadPiece(
            (VillagePieces.Start)start, pieces, random, this.box.minX - 1, this.box.maxY - 4, this.box.minZ + 1, Direction.WEST, this.getGenerationDepth()
         );
         VillagePieces.generateRoadPiece(
            (VillagePieces.Start)start, pieces, random, this.box.maxX + 1, this.box.maxY - 4, this.box.minZ + 1, Direction.EAST, this.getGenerationDepth()
         );
         VillagePieces.generateRoadPiece(
            (VillagePieces.Start)start, pieces, random, this.box.minX + 1, this.box.maxY - 4, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
         );
         VillagePieces.generateRoadPiece(
            (VillagePieces.Start)start, pieces, random, this.box.minX + 1, this.box.maxY - 4, this.box.maxZ + 1, Direction.SOUTH, this.getGenerationDepth()
         );
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.hpos < 0) {
            this.hpos = this.updateHeight(world, box);
            if (this.hpos < 0) {
               return true;
            }

            this.box.move(0, this.hpos - this.box.maxY + 3, 0);
         }

         this.fillWithOutline(world, box, 1, 0, 1, 4, 12, 4, Blocks.COBBLESTONE.defaultState(), Blocks.FLOWING_WATER.defaultState(), false);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 12, 2, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 3, 12, 2, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 2, 12, 3, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 3, 12, 3, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 13, 1, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 14, 1, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 13, 1, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 14, 1, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 13, 4, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 14, 4, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 13, 4, box);
         this.setBlockState(world, Blocks.FENCE.defaultState(), 4, 14, 4, box);
         this.fillWithOutline(world, box, 1, 15, 1, 4, 15, 4, Blocks.COBBLESTONE.defaultState(), Blocks.COBBLESTONE.defaultState(), false);

         for(int var4 = 0; var4 <= 5; ++var4) {
            for(int var5 = 0; var5 <= 5; ++var5) {
               if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
                  this.setBlockState(world, Blocks.GRAVEL.defaultState(), var5, 11, var4, box);
                  this.fillAirColumnUp(world, var5, 12, var4, box);
               }
            }
         }

         return true;
      }
   }
}
