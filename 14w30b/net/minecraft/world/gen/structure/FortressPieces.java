package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FortressPieces {
   private static final FortressPieces.FortressPieceWeight[] BRIDGE_PIECE_WEIGHTS = new FortressPieces.FortressPieceWeight[]{
      new FortressPieces.FortressPieceWeight(FortressPieces.StraightBridge.class, 30, 0, true),
      new FortressPieces.FortressPieceWeight(FortressPieces.BridgeCrossing.class, 10, 4),
      new FortressPieces.FortressPieceWeight(FortressPieces.LargeCrossing.class, 10, 4),
      new FortressPieces.FortressPieceWeight(FortressPieces.BridgeStairs.class, 10, 3),
      new FortressPieces.FortressPieceWeight(FortressPieces.BlazeSpawner.class, 5, 2),
      new FortressPieces.FortressPieceWeight(FortressPieces.CastleEntrance.class, 5, 1)
   };
   private static final FortressPieces.FortressPieceWeight[] CASTLE_PIECE_WEIGHTS = new FortressPieces.FortressPieceWeight[]{
      new FortressPieces.FortressPieceWeight(FortressPieces.SmallCorridor.class, 25, 0, true),
      new FortressPieces.FortressPieceWeight(FortressPieces.SmallCorridorCrossing.class, 15, 5),
      new FortressPieces.FortressPieceWeight(FortressPieces.SmallCorridorRightTurn.class, 5, 10),
      new FortressPieces.FortressPieceWeight(FortressPieces.SmallCorridorLeftTurn.class, 5, 10),
      new FortressPieces.FortressPieceWeight(FortressPieces.StairsCorridor.class, 10, 3, true),
      new FortressPieces.FortressPieceWeight(FortressPieces.CorridorBalcony.class, 7, 2),
      new FortressPieces.FortressPieceWeight(FortressPieces.NetherwartFarm.class, 5, 2)
   };

   public static void register() {
      StructureManager.registerPiece(FortressPieces.BridgeCrossing.class, "NeBCr");
      StructureManager.registerPiece(FortressPieces.BridgeEnd.class, "NeBEF");
      StructureManager.registerPiece(FortressPieces.StraightBridge.class, "NeBS");
      StructureManager.registerPiece(FortressPieces.StairsCorridor.class, "NeCCS");
      StructureManager.registerPiece(FortressPieces.CorridorBalcony.class, "NeCTB");
      StructureManager.registerPiece(FortressPieces.CastleEntrance.class, "NeCE");
      StructureManager.registerPiece(FortressPieces.SmallCorridorCrossing.class, "NeSCSC");
      StructureManager.registerPiece(FortressPieces.SmallCorridorLeftTurn.class, "NeSCLT");
      StructureManager.registerPiece(FortressPieces.SmallCorridor.class, "NeSC");
      StructureManager.registerPiece(FortressPieces.SmallCorridorRightTurn.class, "NeSCRT");
      StructureManager.registerPiece(FortressPieces.NetherwartFarm.class, "NeCSR");
      StructureManager.registerPiece(FortressPieces.BlazeSpawner.class, "NeMT");
      StructureManager.registerPiece(FortressPieces.LargeCrossing.class, "NeRC");
      StructureManager.registerPiece(FortressPieces.BridgeStairs.class, "NeSR");
      StructureManager.registerPiece(FortressPieces.Start.class, "NeStart");
   }

   private static FortressPieces.FortressPiece createPiece(
      FortressPieces.FortressPieceWeight weight, List entries, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      Class var8 = weight.type;
      Object var9 = null;
      if (var8 == FortressPieces.StraightBridge.class) {
         var9 = FortressPieces.StraightBridge.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.BridgeCrossing.class) {
         var9 = FortressPieces.BridgeCrossing.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.LargeCrossing.class) {
         var9 = FortressPieces.LargeCrossing.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.BridgeStairs.class) {
         var9 = FortressPieces.BridgeStairs.of(entries, random, x, y, z, generationDepth, facing);
      } else if (var8 == FortressPieces.BlazeSpawner.class) {
         var9 = FortressPieces.BlazeSpawner.of(entries, random, x, y, z, generationDepth, facing);
      } else if (var8 == FortressPieces.CastleEntrance.class) {
         var9 = FortressPieces.CastleEntrance.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.SmallCorridor.class) {
         var9 = FortressPieces.SmallCorridor.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.SmallCorridorRightTurn.class) {
         var9 = FortressPieces.SmallCorridorRightTurn.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.SmallCorridorLeftTurn.class) {
         var9 = FortressPieces.SmallCorridorLeftTurn.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.StairsCorridor.class) {
         var9 = FortressPieces.StairsCorridor.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.CorridorBalcony.class) {
         var9 = FortressPieces.CorridorBalcony.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.SmallCorridorCrossing.class) {
         var9 = FortressPieces.SmallCorridorCrossing.of(entries, random, x, y, z, facing, generationDepth);
      } else if (var8 == FortressPieces.NetherwartFarm.class) {
         var9 = FortressPieces.NetherwartFarm.of(entries, random, x, y, z, facing, generationDepth);
      }

      return (FortressPieces.FortressPiece)var9;
   }

   public static class BlazeSpawner extends FortressPieces.FortressPiece {
      private boolean shouldGenerateMobSpawner;

      public BlazeSpawner() {
      }

      public BlazeSpawner(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.shouldGenerateMobSpawner = nbt.getBoolean("Mob");
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Mob", this.shouldGenerateMobSpawner);
      }

      public static FortressPieces.BlazeSpawner of(List pieces, Random random, int x, int y, int z, int facing, Direction generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -2, 0, 0, 7, 8, 9, generationDepth);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.BlazeSpawner(facing, random, var7, generationDepth)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 1, 6, 3, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 5, 6, 3, box);
         this.fillWithOutline(world, box, 0, 6, 3, 0, 6, 8, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 6, 3, 6, 6, 8, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 1, 6, 8, 5, 7, 8, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 2, 8, 8, 4, 8, 8, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         if (!this.shouldGenerateMobSpawner) {
            BlockPos var4 = new BlockPos(this.transformX(3, 5), this.transformY(5), this.transformZ(3, 5));
            if (box.contains(var4)) {
               this.shouldGenerateMobSpawner = true;
               world.setBlockState(var4, Blocks.MOB_SPAWNER.defaultState(), 2);
               BlockEntity var5 = world.getBlockEntity(var4);
               if (var5 instanceof MobSpawnerBlockEntity) {
                  ((MobSpawnerBlockEntity)var5).getSpawner().setType("Blaze");
               }
            }
         }

         for(int var6 = 0; var6 <= 6; ++var6) {
            for(int var7 = 0; var7 <= 6; ++var7) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var6, -1, var7, box);
            }
         }

         return true;
      }
   }

   public static class BridgeCrossing extends FortressPieces.FortressPiece {
      public BridgeCrossing() {
      }

      public BridgeCrossing(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      protected BridgeCrossing(Random random, int chunkX, int chunkZ) {
         super(0);
         this.facing = Direction.Plane.HORIZONTAL.pick(random);
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               this.box = new StructureBox(chunkX, 64, chunkZ, chunkX + 19 - 1, 73, chunkZ + 19 - 1);
               break;
            default:
               this.box = new StructureBox(chunkX, 64, chunkZ, chunkX + 19 - 1, 73, chunkZ + 19 - 1);
         }
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 8, 3, false);
         this.generatePieceLeft((FortressPieces.Start)start, pieces, random, 3, 8, false);
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 3, 8, false);
      }

      public static FortressPieces.BridgeCrossing of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -8, -3, 0, 19, 10, 19, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.BridgeCrossing(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 7; var4 <= 11; ++var4) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, 18 - var5, box);
            }
         }

         this.fillWithOutline(world, box, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var6 = 0; var6 <= 2; ++var6) {
            for(int var7 = 7; var7 <= 11; ++var7) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var6, -1, var7, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), 18 - var6, -1, var7, box);
            }
         }

         return true;
      }
   }

   public static class BridgeEnd extends FortressPieces.FortressPiece {
      private int seed;

      public BridgeEnd() {
      }

      public BridgeEnd(int generationDepth, Random seed, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
         this.seed = seed.nextInt();
      }

      public static FortressPieces.BridgeEnd of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -3, 0, 5, 10, 8, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.BridgeEnd(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.seed = nbt.getInt("Seed");
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("Seed", this.seed);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         Random var4 = new Random((long)this.seed);

         for(int var5 = 0; var5 <= 4; ++var5) {
            for(int var6 = 3; var6 <= 4; ++var6) {
               int var7 = var4.nextInt(8);
               this.fillWithOutline(
                  world, box, var5, var6, 0, var5, var6, var7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false
               );
            }
         }

         int var8 = var4.nextInt(8);
         this.fillWithOutline(world, box, 0, 5, 0, 0, 5, var8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         var8 = var4.nextInt(8);
         this.fillWithOutline(world, box, 4, 5, 0, 4, 5, var8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var10 = 0; var10 <= 4; ++var10) {
            int var12 = var4.nextInt(5);
            this.fillWithOutline(world, box, var10, 2, 0, var10, 2, var12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         }

         for(int var11 = 0; var11 <= 4; ++var11) {
            for(int var13 = 0; var13 <= 1; ++var13) {
               int var14 = var4.nextInt(3);
               this.fillWithOutline(
                  world, box, var11, var13, 0, var11, var13, var14, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false
               );
            }
         }

         return true;
      }
   }

   public static class BridgeStairs extends FortressPieces.FortressPiece {
      public BridgeStairs() {
      }

      public BridgeStairs(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 6, 2, false);
      }

      public static FortressPieces.BridgeStairs of(List pieces, Random random, int x, int y, int z, int facing, Direction type) {
         StructureBox var7 = StructureBox.orient(x, y, z, -2, 0, 0, 7, 11, 7, type);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.BridgeStairs(facing, random, var7, type)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 2, 0, 5, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 3, 2, 6, 5, 2, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 3, 4, 6, 5, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 5, 2, 5, box);
         this.fillWithOutline(world, box, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 0, 4, 5, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);

         for(int var4 = 0; var4 <= 6; ++var4) {
            for(int var5 = 0; var5 <= 6; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class CastleEntrance extends FortressPieces.FortressPiece {
      public CastleEntrance() {
      }

      public CastleEntrance(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 5, 3, true);
      }

      public static FortressPieces.CastleEntrance of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -5, -3, 0, 13, 14, 13, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.CastleEntrance(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);

         for(int var4 = 1; var4 <= 11; var4 += 2) {
            this.fillWithOutline(
               world, box, var4, 10, 0, var4, 11, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, var4, 10, 12, var4, 11, 12, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, 0, 10, var4, 0, 11, var4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, 12, 10, var4, 12, 11, var4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), var4, 13, 0, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), var4, 13, 12, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 0, 13, var4, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 12, 13, var4, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), var4 + 1, 13, 0, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), var4 + 1, 13, 12, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, var4 + 1, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 12, 13, var4 + 1, box);
         }

         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 0, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 12, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 0, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 12, 13, 0, box);

         for(int var6 = 3; var6 <= 9; var6 += 2) {
            this.fillWithOutline(world, box, 1, 7, var6, 1, 8, var6, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
            this.fillWithOutline(
               world, box, 11, 7, var6, 11, 8, var6, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
         }

         this.fillWithOutline(world, box, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var7 = 4; var7 <= 8; ++var7) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var7, -1, var5, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var7, -1, 12 - var5, box);
            }
         }

         for(int var8 = 0; var8 <= 2; ++var8) {
            for(int var10 = 4; var10 <= 8; ++var10) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var8, -1, var10, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), 12 - var8, -1, var10, box);
            }
         }

         this.fillWithOutline(world, box, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 6, 0, 6, box);
         this.setBlockState(world, Blocks.FLOWING_LAVA.defaultState(), 6, 5, 6, box);
         BlockPos var9 = new BlockPos(this.transformX(6, 6), this.transformY(5), this.transformZ(6, 6));
         if (box.contains(var9)) {
            world.tickBlockNow(Blocks.FLOWING_LAVA, var9, random);
         }

         return true;
      }
   }

   public static class CorridorBalcony extends FortressPieces.FortressPiece {
      public CorridorBalcony() {
      }

      public CorridorBalcony(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         byte var4 = 1;
         if (this.facing == Direction.WEST || this.facing == Direction.NORTH) {
            var4 = 5;
         }

         this.generatePieceLeft((FortressPieces.Start)start, pieces, random, 0, var4, random.nextInt(8) > 0);
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 0, var4, random.nextInt(8) > 0);
      }

      public static FortressPieces.CorridorBalcony of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -3, 0, 0, 9, 7, 9, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.CorridorBalcony(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 0, 1, 4, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 7, 3, 0, 7, 4, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 8, 8, 3, 8, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 6, 0, 3, 7, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 8, 3, 6, 8, 3, 7, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 4, 5, 1, 5, 5, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 7, 4, 5, 7, 5, 5, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);

         for(int var4 = 0; var4 <= 5; ++var4) {
            for(int var5 = 0; var5 <= 8; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var5, -1, var4, box);
            }
         }

         return true;
      }
   }

   abstract static class FortressPiece extends StructurePiece {
      protected static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.DIAMOND, 0, 1, 3, 5),
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 5),
            new LootEntry(Items.GOLD_INGOT, 0, 1, 3, 15),
            new LootEntry(Items.GOLDEN_SWORD, 0, 1, 1, 5),
            new LootEntry(Items.GOLDEN_CHESTPLATE, 0, 1, 1, 5),
            new LootEntry(Items.FLINT_AND_STEEL, 0, 1, 1, 5),
            new LootEntry(Items.NETHER_WART, 0, 3, 7, 5),
            new LootEntry(Items.SADDLE, 0, 1, 1, 10),
            new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 8),
            new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 5),
            new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 3),
            new LootEntry(Item.byBlock(Blocks.OBSIDIAN), 0, 2, 4, 2)
         }
      );

      public FortressPiece() {
      }

      protected FortressPiece(int i) {
         super(i);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
      }

      private int getTotalWeight(List pieceWeights) {
         boolean var2 = false;
         int var3 = 0;

         for(FortressPieces.FortressPieceWeight var5 : pieceWeights) {
            if (var5.maxAmount > 0 && var5.amountGenerated < var5.maxAmount) {
               var2 = true;
            }

            var3 += var5.weight;
         }

         return var2 ? var3 : -1;
      }

      private FortressPieces.FortressPiece generateNextPiece(
         FortressPieces.Start startPiece, List weights, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
      ) {
         int var10 = this.getTotalWeight(weights);
         boolean var11 = var10 > 0 && generationDepth <= 30;
         int var12 = 0;

         while(var12 < 5 && var11) {
            ++var12;
            int var13 = random.nextInt(var10);

            for(FortressPieces.FortressPieceWeight var15 : weights) {
               var13 -= var15.weight;
               if (var13 < 0) {
                  if (!var15.isValid(generationDepth) || var15 == startPiece.previous && !var15.isConnectorPiece) {
                     break;
                  }

                  FortressPieces.FortressPiece var16 = FortressPieces.createPiece(var15, pieces, random, x, y, z, facing, generationDepth);
                  if (var16 != null) {
                     ++var15.amountGenerated;
                     startPiece.previous = var15;
                     if (!var15.isValid()) {
                        weights.remove(var15);
                     }

                     return var16;
                  }
               }
            }
         }

         return FortressPieces.BridgeEnd.of(pieces, random, x, y, z, facing, generationDepth);
      }

      private StructurePiece generateNextPiece(
         FortressPieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth, boolean pickCastlePiece
      ) {
         if (Math.abs(x - startPiece.getBoundingBox().minX) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 112) {
            List var10 = startPiece.bridgePieces;
            if (pickCastlePiece) {
               var10 = startPiece.castlePieces;
            }

            FortressPieces.FortressPiece var11 = this.generateNextPiece(startPiece, var10, pieces, random, x, y, z, facing, generationDepth + 1);
            if (var11 != null) {
               pieces.add(var11);
               startPiece.children.add(var11);
            }

            return var11;
         } else {
            return FortressPieces.BridgeEnd.of(pieces, random, x, y, z, facing, generationDepth);
         }
      }

      protected StructurePiece generatePieceForward(
         FortressPieces.Start startPiece, List pieces, Random random, int offset, int yOffset, boolean pickCastlePiece
      ) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.minZ - 1,
                     this.facing,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case SOUTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.maxZ + 1,
                     this.facing,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case WEST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX - 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     this.facing,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case EAST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.maxX + 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     this.facing,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
            }
         }

         return null;
      }

      protected StructurePiece generatePieceLeft(FortressPieces.Start startPiece, List pieces, Random random, int yOffset, int offset, boolean pickCastlePiece) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX - 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.WEST,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case SOUTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX - 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.WEST,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case WEST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.minZ - 1,
                     Direction.NORTH,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case EAST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.minZ - 1,
                     Direction.NORTH,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
            }
         }

         return null;
      }

      protected StructurePiece generatePieceRight(FortressPieces.Start startPiece, List pieces, Random random, int yOffset, int offset, boolean pickCastlePiece) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.maxX + 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.EAST,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case SOUTH:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.maxX + 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.EAST,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case WEST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.maxZ + 1,
                     Direction.SOUTH,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
               case EAST:
                  return this.generateNextPiece(
                     startPiece,
                     pieces,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.maxZ + 1,
                     Direction.SOUTH,
                     this.getGenerationDepth(),
                     pickCastlePiece
                  );
            }
         }

         return null;
      }

      protected static boolean isValidStructureBox(StructureBox box) {
         return box != null && box.minY > 10;
      }
   }

   static class FortressPieceWeight {
      public Class type;
      public final int weight;
      public int amountGenerated;
      public int maxAmount;
      public boolean isConnectorPiece;

      public FortressPieceWeight(Class type, int weight, int maxAmount, boolean isConnectorPiece) {
         this.type = type;
         this.weight = weight;
         this.maxAmount = maxAmount;
         this.isConnectorPiece = isConnectorPiece;
      }

      public FortressPieceWeight(Class type, int weight, int maxAmount) {
         this(type, weight, maxAmount, false);
      }

      public boolean isValid(int generationDepth) {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }

      public boolean isValid() {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }
   }

   public static class LargeCrossing extends FortressPieces.FortressPiece {
      public LargeCrossing() {
      }

      public LargeCrossing(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 2, 0, false);
         this.generatePieceLeft((FortressPieces.Start)start, pieces, random, 0, 2, false);
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 0, 2, false);
      }

      public static FortressPieces.LargeCrossing of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -2, 0, 0, 7, 9, 7, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.LargeCrossing(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 0, 4, 5, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 6, 4, 5, 6, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 2, 0, 5, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 6, 5, 2, 6, 5, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);

         for(int var4 = 0; var4 <= 6; ++var4) {
            for(int var5 = 0; var5 <= 6; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class NetherwartFarm extends FortressPieces.FortressPiece {
      public NetherwartFarm() {
      }

      public NetherwartFarm(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 5, 3, true);
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 5, 11, true);
      }

      public static FortressPieces.NetherwartFarm of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -5, -3, 0, 13, 14, 13, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.NetherwartFarm(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 1; var4 <= 11; var4 += 2) {
            this.fillWithOutline(
               world, box, var4, 10, 0, var4, 11, 0, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, var4, 10, 12, var4, 11, 12, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, 0, 10, var4, 0, 11, var4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.fillWithOutline(
               world, box, 12, 10, var4, 12, 11, var4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), var4, 13, 0, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), var4, 13, 12, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 0, 13, var4, box);
            this.setBlockState(world, Blocks.NETHER_BRICKS.defaultState(), 12, 13, var4, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), var4 + 1, 13, 0, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), var4 + 1, 13, 12, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, var4 + 1, box);
            this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 12, 13, var4 + 1, box);
         }

         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 0, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 12, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 0, 13, 0, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_FENCE.defaultState(), 12, 13, 0, box);

         for(int var9 = 3; var9 <= 9; var9 += 2) {
            this.fillWithOutline(world, box, 1, 7, var9, 1, 8, var9, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
            this.fillWithOutline(
               world, box, 11, 7, var9, 11, 8, var9, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
            );
         }

         int var10 = this.postProcessBlockMetadata(Blocks.NETHER_BRICK_STAIRS, 3);

         for(int var5 = 0; var5 <= 6; ++var5) {
            int var6 = var5 + 4;

            for(int var7 = 5; var7 <= 7; ++var7) {
               this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var10), var7, 5 + var5, var6, box);
            }

            if (var6 >= 5 && var6 <= 8) {
               this.fillWithOutline(world, box, 5, 5, var6, 7, var5 + 4, var6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
            } else if (var6 >= 9 && var6 <= 10) {
               this.fillWithOutline(world, box, 5, 8, var6, 7, var5 + 4, var6, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
            }

            if (var5 >= 1) {
               this.fillWithOutline(world, box, 5, 6 + var5, var6, 7, 9 + var5, var6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }
         }

         for(int var11 = 5; var11 <= 7; ++var11) {
            this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var10), var11, 12, 11, box);
         }

         this.fillWithOutline(world, box, 5, 6, 7, 5, 7, 7, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 7, 6, 7, 7, 7, 7, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         int var12 = this.postProcessBlockMetadata(Blocks.NETHER_BRICK_STAIRS, 0);
         int var13 = this.postProcessBlockMetadata(Blocks.NETHER_BRICK_STAIRS, 1);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var13), 4, 5, 2, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var13), 4, 5, 3, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var13), 4, 5, 9, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var13), 4, 5, 10, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var12), 8, 5, 2, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var12), 8, 5, 3, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var12), 8, 5, 9, box);
         this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var12), 8, 5, 10, box);
         this.fillWithOutline(world, box, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultState(), Blocks.SOUL_SAND.defaultState(), false);
         this.fillWithOutline(world, box, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultState(), Blocks.SOUL_SAND.defaultState(), false);
         this.fillWithOutline(world, box, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultState(), Blocks.NETHER_WART.defaultState(), false);
         this.fillWithOutline(world, box, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultState(), Blocks.NETHER_WART.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var14 = 4; var14 <= 8; ++var14) {
            for(int var8 = 0; var8 <= 2; ++var8) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var14, -1, var8, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var14, -1, 12 - var8, box);
            }
         }

         for(int var15 = 0; var15 <= 2; ++var15) {
            for(int var16 = 4; var16 <= 8; ++var16) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var15, -1, var16, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), 12 - var15, -1, var16, box);
            }
         }

         return true;
      }
   }

   public static class SmallCorridor extends FortressPieces.FortressPiece {
      public SmallCorridor() {
      }

      public SmallCorridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 1, 0, true);
      }

      public static FortressPieces.SmallCorridor of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, 0, 0, 5, 7, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.SmallCorridor(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 1, 0, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 3, 0, 4, 3, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 1, 4, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 3, 4, 4, 3, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class SmallCorridorCrossing extends FortressPieces.FortressPiece {
      public SmallCorridorCrossing() {
      }

      public SmallCorridorCrossing(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 1, 0, true);
         this.generatePieceLeft((FortressPieces.Start)start, pieces, random, 0, 1, true);
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 0, 1, true);
      }

      public static FortressPieces.SmallCorridorCrossing of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, 0, 0, 5, 7, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.SmallCorridorCrossing(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class SmallCorridorLeftTurn extends FortressPieces.FortressPiece {
      private boolean hasChest;

      public SmallCorridorLeftTurn() {
      }

      public SmallCorridorLeftTurn(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
         this.hasChest = random.nextInt(3) == 0;
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasChest = nbt.getBoolean("Chest");
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Chest", this.hasChest);
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceLeft((FortressPieces.Start)start, pieces, random, 0, 1, true);
      }

      public static FortressPieces.SmallCorridorLeftTurn of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, 0, 0, 5, 7, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.SmallCorridorLeftTurn(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 1, 4, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 3, 4, 4, 3, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 4, 1, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 3, 3, 4, 3, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         if (this.hasChest && box.contains(new BlockPos(this.transformX(3, 3), this.transformY(2), this.transformZ(3, 3)))) {
            this.hasChest = false;
            this.placeChestWithLoot(world, box, random, 3, 2, 3, LOOT_ENTRIES, 2 + random.nextInt(4));
         }

         this.fillWithOutline(world, box, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class SmallCorridorRightTurn extends FortressPieces.FortressPiece {
      private boolean hasLootChest;

      public SmallCorridorRightTurn() {
      }

      public SmallCorridorRightTurn(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
         this.hasLootChest = random.nextInt(3) == 0;
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasLootChest = nbt.getBoolean("Chest");
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Chest", this.hasLootChest);
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceRight((FortressPieces.Start)start, pieces, random, 0, 1, true);
      }

      public static FortressPieces.SmallCorridorRightTurn of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, 0, 0, 5, 7, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.SmallCorridorRightTurn(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 1, 0, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 3, 0, 4, 3, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 3, 4, 1, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 3, 3, 4, 3, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         if (this.hasLootChest && box.contains(new BlockPos(this.transformX(1, 3), this.transformY(2), this.transformZ(1, 3)))) {
            this.hasLootChest = false;
            this.placeChestWithLoot(world, box, random, 1, 2, 3, LOOT_ENTRIES, 2 + random.nextInt(4));
         }

         this.fillWithOutline(world, box, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
            }
         }

         return true;
      }
   }

   public static class StairsCorridor extends FortressPieces.FortressPiece {
      public StairsCorridor() {
      }

      public StairsCorridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 1, 0, true);
      }

      public static FortressPieces.StairsCorridor of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -7, 0, 5, 14, 10, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.StairsCorridor(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         int var4 = this.postProcessBlockMetadata(Blocks.NETHER_BRICK_STAIRS, 2);

         for(int var5 = 0; var5 <= 9; ++var5) {
            int var6 = Math.max(1, 7 - var5);
            int var7 = Math.min(Math.max(var6 + 5, 14 - var5), 13);
            int var8 = var5;
            this.fillWithOutline(world, box, 0, 0, var5, 4, var6, var5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
            this.fillWithOutline(world, box, 1, var6 + 1, var5, 3, var7 - 1, var5, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            if (var5 <= 6) {
               this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var4), 1, var6 + 1, var5, box);
               this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var4), 2, var6 + 1, var5, box);
               this.setBlockState(world, Blocks.NETHER_BRICK_STAIRS.getStateFromMetadata(var4), 3, var6 + 1, var5, box);
            }

            this.fillWithOutline(world, box, 0, var7, var5, 4, var7, var5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
            this.fillWithOutline(
               world, box, 0, var6 + 1, var5, 0, var7 - 1, var5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false
            );
            this.fillWithOutline(
               world, box, 4, var6 + 1, var5, 4, var7 - 1, var5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false
            );
            if ((var5 & 1) == 0) {
               this.fillWithOutline(
                  world, box, 0, var6 + 2, var5, 0, var6 + 3, var5, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
               );
               this.fillWithOutline(
                  world, box, 4, var6 + 2, var5, 4, var6 + 3, var5, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false
               );
            }

            for(int var9 = 0; var9 <= 4; ++var9) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var9, -1, var8, box);
            }
         }

         return true;
      }
   }

   public static class Start extends FortressPieces.BridgeCrossing {
      public FortressPieces.FortressPieceWeight previous;
      public List bridgePieces;
      public List castlePieces;
      public List children = Lists.newArrayList();

      public Start() {
      }

      public Start(Random random, int i, int j) {
         super(random, i, j);
         this.bridgePieces = Lists.newArrayList();

         for(FortressPieces.FortressPieceWeight var7 : FortressPieces.BRIDGE_PIECE_WEIGHTS) {
            var7.amountGenerated = 0;
            this.bridgePieces.add(var7);
         }

         this.castlePieces = Lists.newArrayList();

         for(FortressPieces.FortressPieceWeight var11 : FortressPieces.CASTLE_PIECE_WEIGHTS) {
            var11.amountGenerated = 0;
            this.castlePieces.add(var11);
         }
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
      }
   }

   public static class StraightBridge extends FortressPieces.FortressPiece {
      public StraightBridge() {
      }

      public StraightBridge(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((FortressPieces.Start)start, pieces, random, 1, 3, false);
      }

      public static FortressPieces.StraightBridge of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -3, 0, 5, 10, 19, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new FortressPieces.StraightBridge(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);
         this.fillWithOutline(world, box, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultState(), Blocks.NETHER_BRICKS.defaultState(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, var5, box);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultState(), var4, -1, 18 - var5, box);
            }
         }

         this.fillWithOutline(world, box, 0, 1, 1, 0, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 4, 0, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 3, 14, 0, 4, 14, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 0, 1, 17, 0, 4, 17, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 1, 4, 4, 1, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 4, 4, 4, 4, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 3, 14, 4, 4, 14, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 17, 4, 4, 17, Blocks.NETHER_BRICK_FENCE.defaultState(), Blocks.NETHER_BRICK_FENCE.defaultState(), false);
         return true;
      }
   }
}
