package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StrongholdPieces {
   private static final StrongholdPieces.StrongholdPieceWeight[] PIECE_WEIGHTS = new StrongholdPieces.StrongholdPieceWeight[]{
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.StraightCorridor.class, 40, 0),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.Prison.class, 5, 5),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.LeftTurn.class, 20, 0),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.RightTurn.class, 20, 0),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.StraightStairs.class, 5, 5),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.SpiralStaircase.class, 5, 5),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.FiveWayCrossing.class, 5, 4),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4),
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.Library.class, 10, 2) {
         @Override
         public boolean isValid(int generationDepth) {
            return super.isValid(generationDepth) && generationDepth > 4;
         }
      },
      new StrongholdPieces.StrongholdPieceWeight(StrongholdPieces.EndPortalRoom.class, 20, 1) {
         @Override
         public boolean isValid(int generationDepth) {
            return super.isValid(generationDepth) && generationDepth > 5;
         }
      }
   };
   private static List currentWeights;
   private static Class forcedPiece;
   static int totalWeight;
   private static final StrongholdPieces.StoneBrickPicker STONE_BRICK_PICKER = new StrongholdPieces.StoneBrickPicker();

   public static void register() {
      StructureManager.registerPiece(StrongholdPieces.ChestCorridor.class, "SHCC");
      StructureManager.registerPiece(StrongholdPieces.PlainCorridor.class, "SHFC");
      StructureManager.registerPiece(StrongholdPieces.FiveWayCrossing.class, "SH5C");
      StructureManager.registerPiece(StrongholdPieces.LeftTurn.class, "SHLT");
      StructureManager.registerPiece(StrongholdPieces.Library.class, "SHLi");
      StructureManager.registerPiece(StrongholdPieces.EndPortalRoom.class, "SHPR");
      StructureManager.registerPiece(StrongholdPieces.Prison.class, "SHPH");
      StructureManager.registerPiece(StrongholdPieces.RightTurn.class, "SHRT");
      StructureManager.registerPiece(StrongholdPieces.RoomCrossing.class, "SHRC");
      StructureManager.registerPiece(StrongholdPieces.SpiralStaircase.class, "SHSD");
      StructureManager.registerPiece(StrongholdPieces.Start.class, "SHStart");
      StructureManager.registerPiece(StrongholdPieces.StraightCorridor.class, "SHS");
      StructureManager.registerPiece(StrongholdPieces.StraightStairs.class, "SHSSD");
   }

   public static void resetWeights() {
      currentWeights = Lists.newArrayList();

      for(StrongholdPieces.StrongholdPieceWeight var3 : PIECE_WEIGHTS) {
         var3.amountGenerated = 0;
         currentWeights.add(var3);
      }

      forcedPiece = null;
   }

   private static boolean findTotalWeight() {
      boolean var0 = false;
      totalWeight = 0;

      for(StrongholdPieces.StrongholdPieceWeight var2 : currentWeights) {
         if (var2.maxAmount > 0 && var2.amountGenerated < var2.maxAmount) {
            var0 = true;
         }

         totalWeight += var2.weight;
      }

      return var0;
   }

   private static StrongholdPieces.StrongholdPiece createPiece(
      Class type, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      Object var8 = null;
      if (type == StrongholdPieces.StraightCorridor.class) {
         var8 = StrongholdPieces.StraightCorridor.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.Prison.class) {
         var8 = StrongholdPieces.Prison.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.LeftTurn.class) {
         var8 = StrongholdPieces.LeftTurn.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.RightTurn.class) {
         var8 = StrongholdPieces.RightTurn.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.RoomCrossing.class) {
         var8 = StrongholdPieces.RoomCrossing.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.StraightStairs.class) {
         var8 = StrongholdPieces.StraightStairs.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.SpiralStaircase.class) {
         var8 = StrongholdPieces.SpiralStaircase.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.FiveWayCrossing.class) {
         var8 = StrongholdPieces.FiveWayCrossing.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.ChestCorridor.class) {
         var8 = StrongholdPieces.ChestCorridor.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.Library.class) {
         var8 = StrongholdPieces.Library.of(pieces, random, x, y, z, facing, generationDepth);
      } else if (type == StrongholdPieces.EndPortalRoom.class) {
         var8 = StrongholdPieces.EndPortalRoom.of(pieces, random, x, y, z, facing, generationDepth);
      }

      return (StrongholdPieces.StrongholdPiece)var8;
   }

   private static StrongholdPieces.StrongholdPiece generateNextPiece(
      StrongholdPieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      if (!findTotalWeight()) {
         return null;
      } else {
         if (forcedPiece != null) {
            StrongholdPieces.StrongholdPiece var8 = createPiece(forcedPiece, pieces, random, x, y, z, facing, generationDepth);
            forcedPiece = null;
            if (var8 != null) {
               return var8;
            }
         }

         int var13 = 0;

         while(var13 < 5) {
            ++var13;
            int var9 = random.nextInt(totalWeight);

            for(StrongholdPieces.StrongholdPieceWeight var11 : currentWeights) {
               var9 -= var11.weight;
               if (var9 < 0) {
                  if (!var11.isValid(generationDepth) || var11 == startPiece.previous) {
                     break;
                  }

                  StrongholdPieces.StrongholdPiece var12 = createPiece(var11.type, pieces, random, x, y, z, facing, generationDepth);
                  if (var12 != null) {
                     ++var11.amountGenerated;
                     startPiece.previous = var11;
                     if (!var11.isValid()) {
                        currentWeights.remove(var11);
                     }

                     return var12;
                  }
               }
            }
         }

         StructureBox var14 = StrongholdPieces.PlainCorridor.findSize(pieces, random, x, y, z, facing);
         return var14 != null && var14.minY > 1 ? new StrongholdPieces.PlainCorridor(generationDepth, random, var14, facing) : null;
      }
   }

   private static StructurePiece tryGenerateNextPiece(
      StrongholdPieces.Start startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      if (generationDepth > 50) {
         return null;
      } else if (Math.abs(x - startPiece.getBoundingBox().minX) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 112) {
         StrongholdPieces.StrongholdPiece var8 = generateNextPiece(startPiece, pieces, random, x, y, z, facing, generationDepth + 1);
         if (var8 != null) {
            pieces.add(var8);
            startPiece.children.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.StrongholdPiece {
      private static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.ENDER_PEARL, 0, 1, 1, 10),
            new LootEntry(Items.DIAMOND, 0, 1, 3, 3),
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
            new LootEntry(Items.GOLD_INGOT, 0, 1, 3, 5),
            new LootEntry(Items.REDSTONE, 0, 4, 9, 5),
            new LootEntry(Items.BREAD, 0, 1, 3, 15),
            new LootEntry(Items.APPLE, 0, 1, 3, 15),
            new LootEntry(Items.IRON_PICKAXE, 0, 1, 1, 5),
            new LootEntry(Items.IRON_SWORD, 0, 1, 1, 5),
            new LootEntry(Items.IRON_CHESTPLATE, 0, 1, 1, 5),
            new LootEntry(Items.IRON_HELMET, 0, 1, 1, 5),
            new LootEntry(Items.IRON_LEGGINGS, 0, 1, 1, 5),
            new LootEntry(Items.IRON_BOOTS, 0, 1, 1, 5),
            new LootEntry(Items.GOLDEN_APPLE, 0, 1, 1, 1),
            new LootEntry(Items.SADDLE, 0, 1, 1, 1),
            new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
         }
      );
      private boolean hasChest;

      public ChestCorridor() {
      }

      public ChestCorridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
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
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, 7, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.ChestCorridor(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 1, 0);
            this.generateEntrance(world, random, box, StrongholdPieces.StrongholdPiece.EntranceType.OPENING, 1, 1, 6);
            this.fillWithOutline(world, box, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultState(), Blocks.STONE_BRICKS.defaultState(), false);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SMOOTHBRICK.getIndex()), 3, 1, 1, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SMOOTHBRICK.getIndex()), 3, 1, 5, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SMOOTHBRICK.getIndex()), 3, 2, 2, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SMOOTHBRICK.getIndex()), 3, 2, 4, box);

            for(int var4 = 2; var4 <= 4; ++var4) {
               this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SMOOTHBRICK.getIndex()), 2, 1, var4, box);
            }

            if (!this.hasChest && box.contains(new BlockPos(this.transformX(3, 3), this.transformY(2), this.transformZ(3, 3)))) {
               this.hasChest = true;
               this.placeChestWithLoot(
                  world, box, random, 3, 2, 3, LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)), 2 + random.nextInt(2)
               );
            }

            return true;
         }
      }
   }

   public static class EndPortalRoom extends StrongholdPieces.StrongholdPiece {
      private boolean hasSpawner;

      public EndPortalRoom() {
      }

      public EndPortalRoom(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Mob", this.hasSpawner);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasSpawner = nbt.getBoolean("Mob");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         if (start != null) {
            ((StrongholdPieces.Start)start).endPortalRoom = this;
         }
      }

      public static StrongholdPieces.EndPortalRoom of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -4, -1, 0, 11, 8, 16, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.EndPortalRoom(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fill(world, box, 0, 0, 0, 10, 7, 15, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.generateEntrance(world, random, box, StrongholdPieces.StrongholdPiece.EntranceType.GATES, 4, 1, 0);
         int var4 = 6;
         this.fill(world, box, 1, var4, 1, 1, var4, 14, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 9, var4, 1, 9, var4, 14, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 2, var4, 1, 8, var4, 2, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 2, var4, 14, 8, var4, 14, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 1, 1, 1, 2, 1, 4, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 8, 1, 1, 9, 1, 4, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fillWithOutline(world, box, 1, 1, 1, 1, 1, 3, Blocks.FLOWING_LAVA.defaultState(), Blocks.FLOWING_LAVA.defaultState(), false);
         this.fillWithOutline(world, box, 9, 1, 1, 9, 1, 3, Blocks.FLOWING_LAVA.defaultState(), Blocks.FLOWING_LAVA.defaultState(), false);
         this.fill(world, box, 3, 1, 8, 7, 1, 12, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fillWithOutline(world, box, 4, 1, 9, 6, 1, 11, Blocks.FLOWING_LAVA.defaultState(), Blocks.FLOWING_LAVA.defaultState(), false);

         for(int var5 = 3; var5 < 14; var5 += 2) {
            this.fillWithOutline(world, box, 0, 3, var5, 0, 4, var5, Blocks.IRON_BARS.defaultState(), Blocks.IRON_BARS.defaultState(), false);
            this.fillWithOutline(world, box, 10, 3, var5, 10, 4, var5, Blocks.IRON_BARS.defaultState(), Blocks.IRON_BARS.defaultState(), false);
         }

         for(int var13 = 2; var13 < 9; var13 += 2) {
            this.fillWithOutline(world, box, var13, 3, 15, var13, 4, 15, Blocks.IRON_BARS.defaultState(), Blocks.IRON_BARS.defaultState(), false);
         }

         int var14 = this.postProcessBlockMetadata(Blocks.STONE_BRICK_STAIRS, 3);
         this.fill(world, box, 4, 1, 5, 6, 1, 7, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 4, 2, 6, 6, 2, 7, false, random, StrongholdPieces.STONE_BRICK_PICKER);
         this.fill(world, box, 4, 3, 7, 6, 3, 7, false, random, StrongholdPieces.STONE_BRICK_PICKER);

         for(int var6 = 4; var6 <= 6; ++var6) {
            this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.getStateFromMetadata(var14), var6, 1, 4, box);
            this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.getStateFromMetadata(var14), var6, 2, 5, box);
            this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.getStateFromMetadata(var14), var6, 3, 6, box);
         }

         int var15 = Direction.NORTH.getIdHorizontal();
         int var7 = Direction.SOUTH.getIdHorizontal();
         int var8 = Direction.EAST.getIdHorizontal();
         int var9 = Direction.WEST.getIdHorizontal();
         if (this.facing != null) {
            switch(this.facing) {
               case SOUTH:
                  var15 = Direction.SOUTH.getIdHorizontal();
                  var7 = Direction.NORTH.getIdHorizontal();
                  break;
               case WEST:
                  var15 = Direction.WEST.getIdHorizontal();
                  var7 = Direction.EAST.getIdHorizontal();
                  var8 = Direction.SOUTH.getIdHorizontal();
                  var9 = Direction.NORTH.getIdHorizontal();
                  break;
               case EAST:
                  var15 = Direction.EAST.getIdHorizontal();
                  var7 = Direction.WEST.getIdHorizontal();
                  var8 = Direction.SOUTH.getIdHorizontal();
                  var9 = Direction.NORTH.getIdHorizontal();
            }
         }

         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var15).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 4, 3, 8, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var15).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 5, 3, 8, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var15).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 6, 3, 8, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var7).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 4, 3, 12, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var7).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 5, 3, 12, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var7).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 6, 3, 12, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var8).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 9, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var8).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 10, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var8).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 11, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var9).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 9, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var9).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 10, box);
         this.setBlockState(world, Blocks.END_PORTAL_FRAME.getStateFromMetadata(var9).set(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 11, box);
         if (!this.hasSpawner) {
            var4 = this.transformY(3);
            BlockPos var10 = new BlockPos(this.transformX(5, 6), var4, this.transformZ(5, 6));
            if (box.contains(var10)) {
               this.hasSpawner = true;
               world.setBlockState(var10, Blocks.MOB_SPAWNER.defaultState(), 2);
               BlockEntity var11 = world.getBlockEntity(var10);
               if (var11 instanceof MobSpawnerBlockEntity) {
                  ((MobSpawnerBlockEntity)var11).getSpawner().setType("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class FiveWayCrossing extends StrongholdPieces.StrongholdPiece {
      private boolean leftLow;
      private boolean leftHigh;
      private boolean rightLow;
      private boolean rightHigh;

      public FiveWayCrossing() {
      }

      public FiveWayCrossing(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
         this.leftLow = random.nextBoolean();
         this.leftHigh = random.nextBoolean();
         this.rightLow = random.nextBoolean();
         this.rightHigh = random.nextInt(3) > 0;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("leftLow", this.leftLow);
         nbt.putBoolean("leftHigh", this.leftHigh);
         nbt.putBoolean("rightLow", this.rightLow);
         nbt.putBoolean("rightHigh", this.rightHigh);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.leftLow = nbt.getBoolean("leftLow");
         this.leftHigh = nbt.getBoolean("leftHigh");
         this.rightLow = nbt.getBoolean("rightLow");
         this.rightHigh = nbt.getBoolean("rightHigh");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         int var4 = 3;
         int var5 = 5;
         if (this.facing == Direction.WEST || this.facing == Direction.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 5, 1);
         if (this.leftLow) {
            this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, var4, 1);
         }

         if (this.leftHigh) {
            this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, var5, 7);
         }

         if (this.rightLow) {
            this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, var4, 1);
         }

         if (this.rightHigh) {
            this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, var5, 7);
         }
      }

      public static StrongholdPieces.FiveWayCrossing of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -4, -3, 0, 10, 9, 11, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.FiveWayCrossing(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 9, 8, 10, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 4, 3, 0);
            if (this.leftLow) {
               this.fillWithOutline(world, box, 0, 3, 1, 0, 5, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            if (this.rightLow) {
               this.fillWithOutline(world, box, 9, 3, 1, 9, 5, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            if (this.leftHigh) {
               this.fillWithOutline(world, box, 0, 5, 7, 0, 7, 9, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            if (this.rightHigh) {
               this.fillWithOutline(world, box, 9, 5, 7, 9, 7, 9, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            this.fillWithOutline(world, box, 5, 1, 10, 7, 3, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fill(world, box, 1, 2, 1, 8, 2, 6, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 4, 1, 5, 4, 4, 9, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 8, 1, 5, 8, 4, 9, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 1, 4, 7, 3, 4, 9, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 1, 3, 5, 3, 3, 6, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fillWithOutline(world, box, 1, 3, 4, 3, 3, 4, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fillWithOutline(world, box, 1, 4, 6, 3, 4, 6, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fill(world, box, 5, 1, 7, 7, 1, 8, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fillWithOutline(world, box, 5, 1, 9, 7, 1, 9, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fillWithOutline(world, box, 5, 2, 7, 7, 2, 7, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fillWithOutline(world, box, 4, 5, 7, 4, 5, 9, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fillWithOutline(world, box, 8, 5, 7, 8, 5, 9, Blocks.STONE_SLAB.defaultState(), Blocks.STONE_SLAB.defaultState(), false);
            this.fillWithOutline(world, box, 5, 5, 7, 7, 5, 9, Blocks.DOUBLE_STONE_SLAB.defaultState(), Blocks.DOUBLE_STONE_SLAB.defaultState(), false);
            this.setBlockState(world, Blocks.TORCH.defaultState(), 6, 5, 6, box);
            return true;
         }
      }
   }

   public static class LeftTurn extends StrongholdPieces.StrongholdPiece {
      public LeftTurn() {
      }

      public LeftTurn(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
            this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, 1, 1);
         } else {
            this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, 1, 1);
         }
      }

      public static StrongholdPieces.LeftTurn of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.LeftTurn(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 1, 0);
            if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
               this.fillWithOutline(world, box, 4, 1, 1, 4, 3, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            } else {
               this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            return true;
         }
      }
   }

   public static class Library extends StrongholdPieces.StrongholdPiece {
      private static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.BOOK, 0, 1, 3, 20),
            new LootEntry(Items.PAPER, 0, 2, 7, 20),
            new LootEntry(Items.MAP, 0, 1, 1, 1),
            new LootEntry(Items.COMPASS, 0, 1, 1, 1)
         }
      );
      private boolean isTall;

      public Library() {
      }

      public Library(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
         this.isTall = box.getSpanY() > 6;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Tall", this.isTall);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.isTall = nbt.getBoolean("Tall");
      }

      public static StrongholdPieces.Library of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -4, -1, 0, 14, 11, 15, facing);
         if (!isValidStructureBox(var7) || StructurePiece.getIntersectingPiece(pieces, var7) != null) {
            var7 = StructureBox.orient(x, y, z, -4, -1, 0, 14, 6, 15, facing);
            if (!isValidStructureBox(var7) || StructurePiece.getIntersectingPiece(pieces, var7) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(generationDepth, random, var7, facing);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            byte var4 = 11;
            if (!this.isTall) {
               var4 = 6;
            }

            this.fill(world, box, 0, 0, 0, 13, var4 - 1, 14, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 4, 1, 0);
            this.fillRandomlyWithOutline(world, box, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.WEB.defaultState(), Blocks.WEB.defaultState(), false);
            boolean var5 = true;
            boolean var6 = true;

            for(int var7 = 1; var7 <= 13; ++var7) {
               if ((var7 - 1) % 4 == 0) {
                  this.fillWithOutline(world, box, 1, 1, var7, 1, 4, var7, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
                  this.fillWithOutline(world, box, 12, 1, var7, 12, 4, var7, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 2, 3, var7, box);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 11, 3, var7, box);
                  if (this.isTall) {
                     this.fillWithOutline(world, box, 1, 6, var7, 1, 9, var7, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
                     this.fillWithOutline(world, box, 12, 6, var7, 12, 9, var7, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
                  }
               } else {
                  this.fillWithOutline(world, box, 1, 1, var7, 1, 4, var7, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
                  this.fillWithOutline(world, box, 12, 1, var7, 12, 4, var7, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
                  if (this.isTall) {
                     this.fillWithOutline(world, box, 1, 6, var7, 1, 9, var7, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
                     this.fillWithOutline(world, box, 12, 6, var7, 12, 9, var7, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
                  }
               }
            }

            for(int var10 = 3; var10 < 12; var10 += 2) {
               this.fillWithOutline(world, box, 3, 1, var10, 4, 3, var10, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
               this.fillWithOutline(world, box, 6, 1, var10, 7, 3, var10, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
               this.fillWithOutline(world, box, 9, 1, var10, 10, 3, var10, Blocks.BOOKSHELF.defaultState(), Blocks.BOOKSHELF.defaultState(), false);
            }

            if (this.isTall) {
               this.fillWithOutline(world, box, 1, 5, 1, 3, 5, 13, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
               this.fillWithOutline(world, box, 10, 5, 1, 12, 5, 13, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
               this.fillWithOutline(world, box, 4, 5, 1, 9, 5, 2, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
               this.fillWithOutline(world, box, 4, 5, 12, 9, 5, 13, Blocks.PLANKS.defaultState(), Blocks.PLANKS.defaultState(), false);
               this.setBlockState(world, Blocks.PLANKS.defaultState(), 9, 5, 11, box);
               this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 5, 11, box);
               this.setBlockState(world, Blocks.PLANKS.defaultState(), 9, 5, 10, box);
               this.fillWithOutline(world, box, 3, 6, 2, 3, 6, 12, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
               this.fillWithOutline(world, box, 10, 6, 2, 10, 6, 10, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
               this.fillWithOutline(world, box, 4, 6, 2, 9, 6, 2, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
               this.fillWithOutline(world, box, 4, 6, 12, 8, 6, 12, Blocks.FENCE.defaultState(), Blocks.FENCE.defaultState(), false);
               this.setBlockState(world, Blocks.FENCE.defaultState(), 9, 6, 11, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), 8, 6, 11, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), 9, 6, 10, box);
               int var11 = this.postProcessBlockMetadata(Blocks.LADDER, 3);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 1, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 2, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 3, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 4, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 5, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 6, 13, box);
               this.setBlockState(world, Blocks.LADDER.getStateFromMetadata(var11), 10, 7, 13, box);
               byte var8 = 7;
               byte var9 = 7;
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 1, 9, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8, 9, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 1, 8, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8, 8, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 1, 7, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8, 7, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 2, 7, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 + 1, 7, var9, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 1, 7, var9 - 1, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8 - 1, 7, var9 + 1, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8, 7, var9 - 1, box);
               this.setBlockState(world, Blocks.FENCE.defaultState(), var8, 7, var9 + 1, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8 - 2, 8, var9, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8 + 1, 8, var9, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8 - 1, 8, var9 - 1, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8 - 1, 8, var9 + 1, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8, 8, var9 - 1, box);
               this.setBlockState(world, Blocks.TORCH.defaultState(), var8, 8, var9 + 1, box);
            }

            this.placeChestWithLoot(
               world, box, random, 3, 3, 5, LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random, 1, 5, 2)), 1 + random.nextInt(4)
            );
            if (this.isTall) {
               this.setBlockState(world, Blocks.AIR.defaultState(), 12, 9, 1, box);
               this.placeChestWithLoot(
                  world,
                  box,
                  random,
                  12,
                  8,
                  1,
                  LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random, 1, 5, 2)),
                  1 + random.nextInt(4)
               );
            }

            return true;
         }
      }
   }

   public static class PlainCorridor extends StrongholdPieces.StrongholdPiece {
      private int steps;

      public PlainCorridor() {
      }

      public PlainCorridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
         this.steps = facing != Direction.NORTH && facing != Direction.SOUTH ? box.getSpanX() : box.getSpanZ();
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("Steps", this.steps);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.steps = nbt.getInt("Steps");
      }

      public static StructureBox findSize(List pieces, Random random, int x, int y, int z, Direction facing) {
         boolean var6 = true;
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, 4, facing);
         StructurePiece var8 = StructurePiece.getIntersectingPiece(pieces, var7);
         if (var8 == null) {
            return null;
         } else {
            if (var8.getBoundingBox().minY == var7.minY) {
               for(int var9 = 3; var9 >= 1; --var9) {
                  var7 = StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, var9 - 1, facing);
                  if (!var8.getBoundingBox().intersects(var7)) {
                     return StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, var9, facing);
                  }
               }
            }

            return null;
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            for(int var4 = 0; var4 < this.steps; ++var4) {
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 0, 0, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 0, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 0, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 0, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 4, 0, var4, box);

               for(int var5 = 1; var5 <= 3; ++var5) {
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 0, var5, var4, box);
                  this.setBlockState(world, Blocks.AIR.defaultState(), 1, var5, var4, box);
                  this.setBlockState(world, Blocks.AIR.defaultState(), 2, var5, var4, box);
                  this.setBlockState(world, Blocks.AIR.defaultState(), 3, var5, var4, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 4, var5, var4, box);
               }

               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 0, 4, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 4, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 4, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 4, var4, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 4, 4, var4, box);
            }

            return true;
         }
      }
   }

   public static class Prison extends StrongholdPieces.StrongholdPiece {
      public Prison() {
      }

      public Prison(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 1, 1);
      }

      public static StrongholdPieces.Prison of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -1, 0, 9, 5, 11, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.Prison(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 8, 4, 10, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 1, 0);
            this.fillWithOutline(world, box, 1, 1, 10, 3, 3, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fill(world, box, 4, 1, 1, 4, 3, 1, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 4, 1, 3, 4, 3, 3, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 4, 1, 7, 4, 3, 7, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fill(world, box, 4, 1, 9, 4, 3, 9, false, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.fillWithOutline(world, box, 4, 1, 4, 4, 3, 6, Blocks.IRON_BARS.defaultState(), Blocks.IRON_BARS.defaultState(), false);
            this.fillWithOutline(world, box, 5, 1, 5, 7, 3, 5, Blocks.IRON_BARS.defaultState(), Blocks.IRON_BARS.defaultState(), false);
            this.setBlockState(world, Blocks.IRON_BARS.defaultState(), 4, 3, 2, box);
            this.setBlockState(world, Blocks.IRON_BARS.defaultState(), 4, 3, 8, box);
            this.setBlockState(world, Blocks.IRON_DOOR.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.IRON_DOOR, 3)), 4, 1, 2, box);
            this.setBlockState(world, Blocks.IRON_DOOR.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.IRON_DOOR, 3) + 8), 4, 2, 2, box);
            this.setBlockState(world, Blocks.IRON_DOOR.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.IRON_DOOR, 3)), 4, 1, 8, box);
            this.setBlockState(world, Blocks.IRON_DOOR.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.IRON_DOOR, 3) + 8), 4, 2, 8, box);
            return true;
         }
      }
   }

   public static class RightTurn extends StrongholdPieces.LeftTurn {
      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
            this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, 1, 1);
         } else {
            this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, 1, 1);
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 1, 0);
            if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
               this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            } else {
               this.fillWithOutline(world, box, 4, 1, 1, 4, 3, 3, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            return true;
         }
      }
   }

   public static class RoomCrossing extends StrongholdPieces.StrongholdPiece {
      private static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
            new LootEntry(Items.GOLD_INGOT, 0, 1, 3, 5),
            new LootEntry(Items.REDSTONE, 0, 4, 9, 5),
            new LootEntry(Items.COAL, 0, 3, 8, 10),
            new LootEntry(Items.BREAD, 0, 1, 3, 15),
            new LootEntry(Items.APPLE, 0, 1, 3, 15),
            new LootEntry(Items.IRON_PICKAXE, 0, 1, 1, 1)
         }
      );
      protected int type;

      public RoomCrossing() {
      }

      public RoomCrossing(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
         this.type = random.nextInt(5);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putInt("Type", this.type);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.type = nbt.getInt("Type");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 4, 1);
         this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, 1, 4);
         this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -4, -1, 0, 11, 7, 11, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.RoomCrossing(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 10, 6, 10, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 4, 1, 0);
            this.fillWithOutline(world, box, 4, 1, 10, 6, 3, 10, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fillWithOutline(world, box, 0, 1, 4, 0, 3, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fillWithOutline(world, box, 10, 1, 4, 10, 3, 6, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            switch(this.type) {
               case 0:
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 1, 5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 2, 5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 3, 5, box);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 4, 3, 5, box);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 6, 3, 5, box);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 5, 3, 4, box);
                  this.setBlockState(world, Blocks.TORCH.defaultState(), 5, 3, 6, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 4, 1, 4, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 4, 1, 5, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 4, 1, 6, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 6, 1, 4, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 6, 1, 5, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 6, 1, 6, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 5, 1, 4, box);
                  this.setBlockState(world, Blocks.STONE_SLAB.defaultState(), 5, 1, 6, box);
                  break;
               case 1:
                  for(int var8 = 0; var8 < 5; ++var8) {
                     this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 1, 3 + var8, box);
                     this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 7, 1, 3 + var8, box);
                     this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3 + var8, 1, 3, box);
                     this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3 + var8, 1, 7, box);
                  }

                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 1, 5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 2, 5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 5, 3, 5, box);
                  this.setBlockState(world, Blocks.FLOWING_WATER.defaultState(), 5, 4, 5, box);
                  break;
               case 2:
                  for(int var4 = 1; var4 <= 9; ++var4) {
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 1, 3, var4, box);
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 9, 3, var4, box);
                  }

                  for(int var5 = 1; var5 <= 9; ++var5) {
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), var5, 3, 1, box);
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), var5, 3, 9, box);
                  }

                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 5, 1, 4, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 5, 1, 6, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 5, 3, 4, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 5, 3, 6, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 1, 5, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, 1, 5, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, 3, 5, box);
                  this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, 3, 5, box);

                  for(int var6 = 1; var6 <= 3; ++var6) {
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, var6, 4, box);
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, var6, 4, box);
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 4, var6, 6, box);
                     this.setBlockState(world, Blocks.COBBLESTONE.defaultState(), 6, var6, 6, box);
                  }

                  this.setBlockState(world, Blocks.TORCH.defaultState(), 5, 3, 5, box);

                  for(int var7 = 2; var7 <= 8; ++var7) {
                     this.setBlockState(world, Blocks.PLANKS.defaultState(), 2, 3, var7, box);
                     this.setBlockState(world, Blocks.PLANKS.defaultState(), 3, 3, var7, box);
                     if (var7 <= 3 || var7 >= 7) {
                        this.setBlockState(world, Blocks.PLANKS.defaultState(), 4, 3, var7, box);
                        this.setBlockState(world, Blocks.PLANKS.defaultState(), 5, 3, var7, box);
                        this.setBlockState(world, Blocks.PLANKS.defaultState(), 6, 3, var7, box);
                     }

                     this.setBlockState(world, Blocks.PLANKS.defaultState(), 7, 3, var7, box);
                     this.setBlockState(world, Blocks.PLANKS.defaultState(), 8, 3, var7, box);
                  }

                  this.setBlockState(
                     world, Blocks.LADDER.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.LADDER, Direction.WEST.getId())), 9, 1, 3, box
                  );
                  this.setBlockState(
                     world, Blocks.LADDER.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.LADDER, Direction.WEST.getId())), 9, 2, 3, box
                  );
                  this.setBlockState(
                     world, Blocks.LADDER.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.LADDER, Direction.WEST.getId())), 9, 3, 3, box
                  );
                  this.placeChestWithLoot(
                     world, box, random, 3, 4, 8, LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)), 1 + random.nextInt(4)
                  );
            }

            return true;
         }
      }
   }

   public static class SpiralStaircase extends StrongholdPieces.StrongholdPiece {
      private boolean isSource;

      public SpiralStaircase() {
      }

      public SpiralStaircase(int generationDepth, Random random, int x, int z) {
         super(generationDepth);
         this.isSource = true;
         this.facing = Direction.Plane.HORIZONTAL.pick(random);
         this.entranceType = StrongholdPieces.StrongholdPiece.EntranceType.OPENING;
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               this.box = new StructureBox(x, 64, z, x + 5 - 1, 74, z + 5 - 1);
               break;
            default:
               this.box = new StructureBox(x, 64, z, x + 5 - 1, 74, z + 5 - 1);
         }
      }

      public SpiralStaircase(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.isSource = false;
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Source", this.isSource);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.isSource = nbt.getBoolean("Source");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         if (this.isSource) {
            StrongholdPieces.forcedPiece = StrongholdPieces.FiveWayCrossing.class;
         }

         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 1, 1);
      }

      public static StrongholdPieces.SpiralStaircase of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -7, 0, 5, 11, 5, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.SpiralStaircase(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 10, 4, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 7, 0);
            this.generateEntrance(world, random, box, StrongholdPieces.StrongholdPiece.EntranceType.OPENING, 1, 1, 4);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 6, 1, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 5, 1, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 1, 6, 1, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 5, 2, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 4, 3, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 1, 5, 3, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 4, 3, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 3, 3, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 3, 4, 3, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 3, 2, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 2, 1, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 3, 3, 1, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 2, 1, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 1, 1, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 1, 2, 1, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 1, 2, box);
            this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.STONE.getIndex()), 1, 1, 3, box);
            return true;
         }
      }
   }

   public static class Start extends StrongholdPieces.SpiralStaircase {
      public StrongholdPieces.StrongholdPieceWeight previous;
      public StrongholdPieces.EndPortalRoom endPortalRoom;
      public List children = Lists.newArrayList();

      public Start() {
      }

      public Start(int i, Random random, int j, int k) {
         super(0, random, j, k);
      }

      @Override
      public BlockPos getCenterPos() {
         return this.endPortalRoom != null ? this.endPortalRoom.getCenterPos() : super.getCenterPos();
      }
   }

   static class StoneBrickPicker extends StructurePiece.BlockPicker {
      private StoneBrickPicker() {
      }

      @Override
      public void pick(Random randomm, int x, int y, int z, boolean isNonAir) {
         if (isNonAir) {
            float var6 = randomm.nextFloat();
            if (var6 < 0.2F) {
               this.state = Blocks.STONE_BRICKS.getStateFromMetadata(StonebrickBlock.CRACKED_INDEX);
            } else if (var6 < 0.5F) {
               this.state = Blocks.STONE_BRICKS.getStateFromMetadata(StonebrickBlock.MOSSY_INDEX);
            } else if (var6 < 0.55F) {
               this.state = Blocks.MONSTER_EGG.getStateFromMetadata(InfestedBlock.Variant.STONEBRICK.getIndex());
            } else {
               this.state = Blocks.STONE_BRICKS.defaultState();
            }
         } else {
            this.state = Blocks.AIR.defaultState();
         }
      }
   }

   public static class StraightCorridor extends StrongholdPieces.StrongholdPiece {
      private boolean left;
      private boolean right;

      public StraightCorridor() {
      }

      public StraightCorridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
         this.left = random.nextInt(2) == 0;
         this.right = random.nextInt(2) == 0;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Left", this.left);
         nbt.putBoolean("Right", this.right);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.left = nbt.getBoolean("Left");
         this.right = nbt.getBoolean("Right");
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 1, 1);
         if (this.left) {
            this.generatePieceLeft((StrongholdPieces.Start)start, pieces, random, 1, 2);
         }

         if (this.right) {
            this.generatePieceRight((StrongholdPieces.Start)start, pieces, random, 1, 2);
         }
      }

      public static StrongholdPieces.StraightCorridor of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -1, 0, 5, 5, 7, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.StraightCorridor(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 1, 0);
            this.generateEntrance(world, random, box, StrongholdPieces.StrongholdPiece.EntranceType.OPENING, 1, 1, 6);
            this.setBlockWithThreshold(world, box, random, 0.1F, 1, 2, 1, Blocks.TORCH.defaultState());
            this.setBlockWithThreshold(world, box, random, 0.1F, 3, 2, 1, Blocks.TORCH.defaultState());
            this.setBlockWithThreshold(world, box, random, 0.1F, 1, 2, 5, Blocks.TORCH.defaultState());
            this.setBlockWithThreshold(world, box, random, 0.1F, 3, 2, 5, Blocks.TORCH.defaultState());
            if (this.left) {
               this.fillWithOutline(world, box, 0, 1, 2, 0, 3, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            if (this.right) {
               this.fillWithOutline(world, box, 4, 1, 2, 4, 3, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            }

            return true;
         }
      }
   }

   public static class StraightStairs extends StrongholdPieces.StrongholdPiece {
      public StraightStairs() {
      }

      public StraightStairs(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.entranceType = this.pickEntranceType(random);
         this.box = box;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         this.generatePieceForward((StrongholdPieces.Start)start, pieces, random, 1, 1);
      }

      public static StrongholdPieces.StraightStairs of(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
         StructureBox var7 = StructureBox.orient(x, y, z, -1, -7, 0, 5, 11, 8, facing);
         return isValidStructureBox(var7) && StructurePiece.getIntersectingPiece(pieces, var7) == null
            ? new StrongholdPieces.StraightStairs(generationDepth, random, var7, facing)
            : null;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fill(world, box, 0, 0, 0, 4, 10, 7, true, random, StrongholdPieces.STONE_BRICK_PICKER);
            this.generateEntrance(world, random, box, this.entranceType, 1, 7, 0);
            this.generateEntrance(world, random, box, StrongholdPieces.StrongholdPiece.EntranceType.OPENING, 1, 1, 7);
            int var4 = this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 2);

            for(int var5 = 0; var5 < 6; ++var5) {
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 1, 6 - var5, 1 + var5, box);
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 2, 6 - var5, 1 + var5, box);
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 3, 6 - var5, 1 + var5, box);
               if (var5 < 5) {
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 1, 5 - var5, 1 + var5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 2, 5 - var5, 1 + var5, box);
                  this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), 3, 5 - var5, 1 + var5, box);
               }
            }

            return true;
         }
      }
   }

   abstract static class StrongholdPiece extends StructurePiece {
      protected StrongholdPieces.StrongholdPiece.EntranceType entranceType = StrongholdPieces.StrongholdPiece.EntranceType.OPENING;

      public StrongholdPiece() {
      }

      protected StrongholdPiece(int i) {
         super(i);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         nbt.putString("EntryDoor", this.entranceType.name());
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         this.entranceType = StrongholdPieces.StrongholdPiece.EntranceType.valueOf(nbt.getString("EntryDoor"));
      }

      protected void generateEntrance(World world, Random random, StructureBox box, StrongholdPieces.StrongholdPiece.EntranceType type, int x, int y, int z) {
         switch(type) {
            case OPENING:
            default:
               this.fillWithOutline(world, box, x, y, z, x + 3 - 1, y + 3 - 1, z, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
               break;
            case WOOD_DOOR:
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y + 1, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 1, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y + 1, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y, z, box);
               this.setBlockState(world, Blocks.WOODEN_DOOR.defaultState(), x + 1, y, z, box);
               this.setBlockState(world, Blocks.WOODEN_DOOR.getStateFromMetadata(8), x + 1, y + 1, z, box);
               break;
            case GATES:
               this.setBlockState(world, Blocks.AIR.defaultState(), x + 1, y, z, box);
               this.setBlockState(world, Blocks.AIR.defaultState(), x + 1, y + 1, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x, y, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x, y + 1, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x, y + 2, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x + 1, y + 2, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x + 2, y + 2, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x + 2, y + 1, z, box);
               this.setBlockState(world, Blocks.IRON_BARS.defaultState(), x + 2, y, z, box);
               break;
            case IRON_DOOR:
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y + 1, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 1, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y + 2, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y + 1, z, box);
               this.setBlockState(world, Blocks.STONE_BRICKS.defaultState(), x + 2, y, z, box);
               this.setBlockState(world, Blocks.IRON_DOOR.defaultState(), x + 1, y, z, box);
               this.setBlockState(world, Blocks.IRON_DOOR.getStateFromMetadata(8), x + 1, y + 1, z, box);
               this.setBlockState(
                  world, Blocks.STONE_BUTTON.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_BUTTON, 4)), x + 2, y + 1, z + 1, box
               );
               this.setBlockState(
                  world, Blocks.STONE_BUTTON.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STONE_BUTTON, 3)), x + 2, y + 1, z - 1, box
               );
         }
      }

      protected StrongholdPieces.StrongholdPiece.EntranceType pickEntranceType(Random random) {
         int var2 = random.nextInt(5);
         switch(var2) {
            case 0:
            case 1:
            default:
               return StrongholdPieces.StrongholdPiece.EntranceType.OPENING;
            case 2:
               return StrongholdPieces.StrongholdPiece.EntranceType.WOOD_DOOR;
            case 3:
               return StrongholdPieces.StrongholdPiece.EntranceType.GATES;
            case 4:
               return StrongholdPieces.StrongholdPiece.EntranceType.IRON_DOOR;
         }
      }

      protected StructurePiece generatePieceForward(StrongholdPieces.Start startPiece, List pieces, Random random, int offset, int yOffset) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.minZ - 1, this.facing, this.getGenerationDepth()
                  );
               case SOUTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.maxZ + 1, this.facing, this.getGenerationDepth()
                  );
               case WEST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX - 1, this.box.minY + yOffset, this.box.minZ + offset, this.facing, this.getGenerationDepth()
                  );
               case EAST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.maxX + 1, this.box.minY + yOffset, this.box.minZ + offset, this.facing, this.getGenerationDepth()
                  );
            }
         }

         return null;
      }

      protected StructurePiece generatePieceLeft(StrongholdPieces.Start startPiece, List pieces, Random random, int yOffset, int offset) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX - 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.WEST, this.getGenerationDepth()
                  );
               case SOUTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX - 1, this.box.minY + yOffset, this.box.minZ + offset, Direction.WEST, this.getGenerationDepth()
                  );
               case WEST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
                  );
               case EAST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece, pieces, random, this.box.minX + offset, this.box.minY + yOffset, this.box.minZ - 1, Direction.NORTH, this.getGenerationDepth()
                  );
            }
         }

         return null;
      }

      protected StructurePiece generatePieceRight(StrongholdPieces.Start startPiece, List piecesList, Random random, int yOffset, int offset) {
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece,
                     piecesList,
                     random,
                     this.box.maxX + 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.EAST,
                     this.getGenerationDepth()
                  );
               case SOUTH:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece,
                     piecesList,
                     random,
                     this.box.maxX + 1,
                     this.box.minY + yOffset,
                     this.box.minZ + offset,
                     Direction.EAST,
                     this.getGenerationDepth()
                  );
               case WEST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece,
                     piecesList,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.maxZ + 1,
                     Direction.SOUTH,
                     this.getGenerationDepth()
                  );
               case EAST:
                  return StrongholdPieces.tryGenerateNextPiece(
                     startPiece,
                     piecesList,
                     random,
                     this.box.minX + offset,
                     this.box.minY + yOffset,
                     this.box.maxZ + 1,
                     Direction.SOUTH,
                     this.getGenerationDepth()
                  );
            }
         }

         return null;
      }

      protected static boolean isValidStructureBox(StructureBox box) {
         return box != null && box.minY > 10;
      }

      public static enum EntranceType {
         OPENING,
         WOOD_DOOR,
         GATES,
         IRON_DOOR;
      }
   }

   static class StrongholdPieceWeight {
      public Class type;
      public final int weight;
      public int amountGenerated;
      public int maxAmount;

      public StrongholdPieceWeight(Class type, int weight, int maxAmount) {
         this.type = type;
         this.weight = weight;
         this.maxAmount = maxAmount;
      }

      public boolean isValid(int generationDepth) {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }

      public boolean isValid() {
         return this.maxAmount == 0 || this.amountGenerated < this.maxAmount;
      }
   }
}
