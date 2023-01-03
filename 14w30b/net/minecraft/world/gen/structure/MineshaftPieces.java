package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MineshaftPieces {
   private static final List LOOT_ENTRIES = Lists.newArrayList(
      new LootEntry[]{
         new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
         new LootEntry(Items.GOLD_INGOT, 0, 1, 3, 5),
         new LootEntry(Items.REDSTONE, 0, 4, 9, 5),
         new LootEntry(Items.DYE, DyeColor.BLUE.getMetadata(), 4, 9, 5),
         new LootEntry(Items.DIAMOND, 0, 1, 2, 3),
         new LootEntry(Items.COAL, 0, 3, 8, 10),
         new LootEntry(Items.BREAD, 0, 1, 3, 15),
         new LootEntry(Items.IRON_PICKAXE, 0, 1, 1, 1),
         new LootEntry(Item.byBlock(Blocks.RAIL), 0, 4, 8, 1),
         new LootEntry(Items.MELON_SEEDS, 0, 2, 4, 10),
         new LootEntry(Items.PUMPKIN_SEEDS, 0, 2, 4, 10),
         new LootEntry(Items.SADDLE, 0, 1, 1, 3),
         new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1)
      }
   );

   public static void register() {
      StructureManager.registerPiece(MineshaftPieces.Corridor.class, "MSCorridor");
      StructureManager.registerPiece(MineshaftPieces.Crossing.class, "MSCrossing");
      StructureManager.registerPiece(MineshaftPieces.Room.class, "MSRoom");
      StructureManager.registerPiece(MineshaftPieces.Stairs.class, "MSStairs");
   }

   private static StructurePiece createPiece(List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth) {
      int var7 = random.nextInt(100);
      if (var7 >= 80) {
         StructureBox var8 = MineshaftPieces.Crossing.findSize(pieces, random, x, y, z, facing);
         if (var8 != null) {
            return new MineshaftPieces.Crossing(generationDepth, random, var8, facing);
         }
      } else if (var7 >= 70) {
         StructureBox var9 = MineshaftPieces.Stairs.findSize(pieces, random, x, y, z, facing);
         if (var9 != null) {
            return new MineshaftPieces.Stairs(generationDepth, random, var9, facing);
         }
      } else {
         StructureBox var10 = MineshaftPieces.Corridor.findSize(pieces, random, x, y, z, facing);
         if (var10 != null) {
            return new MineshaftPieces.Corridor(generationDepth, random, var10, facing);
         }
      }

      return null;
   }

   private static StructurePiece generateNextPiece(
      StructurePiece startPiece, List pieces, Random random, int x, int y, int z, Direction facing, int generationDepth
   ) {
      if (generationDepth > 8) {
         return null;
      } else if (Math.abs(x - startPiece.getBoundingBox().minX) <= 80 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 80) {
         StructurePiece var8 = createPiece(pieces, random, x, y, z, facing, generationDepth + 1);
         if (var8 != null) {
            pieces.add(var8);
            var8.addChildren(startPiece, pieces, random);
         }

         return var8;
      } else {
         return null;
      }
   }

   public static class Corridor extends StructurePiece {
      private boolean hasRails;
      private boolean hasCobwebs;
      private boolean hasSpiderSpawner;
      private int sections;

      public Corridor() {
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         nbt.putBoolean("hr", this.hasRails);
         nbt.putBoolean("sc", this.hasCobwebs);
         nbt.putBoolean("hps", this.hasSpiderSpawner);
         nbt.putInt("Num", this.sections);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         this.hasRails = nbt.getBoolean("hr");
         this.hasCobwebs = nbt.getBoolean("sc");
         this.hasSpiderSpawner = nbt.getBoolean("hps");
         this.sections = nbt.getInt("Num");
      }

      public Corridor(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
         this.hasRails = random.nextInt(3) == 0;
         this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
         if (this.facing != Direction.NORTH && this.facing != Direction.SOUTH) {
            this.sections = box.getSpanX() / 5;
         } else {
            this.sections = box.getSpanZ() / 5;
         }
      }

      public static StructureBox findSize(List pieces, Random random, int x, int y, int z, Direction facing) {
         StructureBox var6 = new StructureBox(x, y, z, x, y + 2, z);

         int var7;
         for(var7 = random.nextInt(3) + 2; var7 > 0; --var7) {
            int var8 = var7 * 5;
            switch(facing) {
               case NORTH:
                  var6.maxX = x + 2;
                  var6.minZ = z - (var8 - 1);
                  break;
               case SOUTH:
                  var6.maxX = x + 2;
                  var6.maxZ = z + (var8 - 1);
                  break;
               case WEST:
                  var6.minX = x - (var8 - 1);
                  var6.maxZ = z + 2;
                  break;
               case EAST:
                  var6.maxX = x + (var8 - 1);
                  var6.maxZ = z + 2;
            }

            if (StructurePiece.getIntersectingPiece(pieces, var6) == null) {
               break;
            }
         }

         return var7 > 0 ? var6 : null;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         int var4 = this.getGenerationDepth();
         int var5 = random.nextInt(4);
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  if (var5 <= 1) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX, this.box.minY - 1 + random.nextInt(3), this.box.minZ - 1, this.facing, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX - 1, this.box.minY - 1 + random.nextInt(3), this.box.minZ, Direction.WEST, var4
                     );
                  } else {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.maxX + 1, this.box.minY - 1 + random.nextInt(3), this.box.minZ, Direction.EAST, var4
                     );
                  }
                  break;
               case SOUTH:
                  if (var5 <= 1) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX, this.box.minY - 1 + random.nextInt(3), this.box.maxZ + 1, this.facing, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX - 1, this.box.minY - 1 + random.nextInt(3), this.box.maxZ - 3, Direction.WEST, var4
                     );
                  } else {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.maxX + 1, this.box.minY - 1 + random.nextInt(3), this.box.maxZ - 3, Direction.EAST, var4
                     );
                  }
                  break;
               case WEST:
                  if (var5 <= 1) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX - 1, this.box.minY - 1 + random.nextInt(3), this.box.minZ, this.facing, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX, this.box.minY - 1 + random.nextInt(3), this.box.minZ - 1, Direction.NORTH, var4
                     );
                  } else {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.minX, this.box.minY - 1 + random.nextInt(3), this.box.maxZ + 1, Direction.SOUTH, var4
                     );
                  }
                  break;
               case EAST:
                  if (var5 <= 1) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.maxX + 1, this.box.minY - 1 + random.nextInt(3), this.box.minZ, this.facing, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.maxX - 3, this.box.minY - 1 + random.nextInt(3), this.box.minZ - 1, Direction.NORTH, var4
                     );
                  } else {
                     MineshaftPieces.generateNextPiece(
                        start, pieces, random, this.box.maxX - 3, this.box.minY - 1 + random.nextInt(3), this.box.maxZ + 1, Direction.SOUTH, var4
                     );
                  }
            }
         }

         if (var4 < 8) {
            if (this.facing != Direction.NORTH && this.facing != Direction.SOUTH) {
               for(int var8 = this.box.minX + 3; var8 + 3 <= this.box.maxX; var8 += 5) {
                  int var9 = random.nextInt(5);
                  if (var9 == 0) {
                     MineshaftPieces.generateNextPiece(start, pieces, random, var8, this.box.minY, this.box.minZ - 1, Direction.NORTH, var4 + 1);
                  } else if (var9 == 1) {
                     MineshaftPieces.generateNextPiece(start, pieces, random, var8, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, var4 + 1);
                  }
               }
            } else {
               for(int var6 = this.box.minZ + 3; var6 + 3 <= this.box.maxZ; var6 += 5) {
                  int var7 = random.nextInt(5);
                  if (var7 == 0) {
                     MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY, var6, Direction.WEST, var4 + 1);
                  } else if (var7 == 1) {
                     MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY, var6, Direction.EAST, var4 + 1);
                  }
               }
            }
         }
      }

      @Override
      protected boolean placeChestWithLoot(World world, StructureBox box, Random random, int x, int y, int z, List entries, int amount) {
         BlockPos var9 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
         if (box.contains(var9) && world.getBlockState(var9).getBlock().getMaterial() == Material.AIR) {
            int var10 = random.nextBoolean() ? 1 : 0;
            world.setBlockState(var9, Blocks.RAIL.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.RAIL, var10)), 2);
            ChestMinecartEntity var11 = new ChestMinecartEntity(
               world, (double)((float)var9.getX() + 0.5F), (double)((float)var9.getY() + 0.5F), (double)((float)var9.getZ() + 0.5F)
            );
            LootEntry.addLoot(random, entries, var11, amount);
            world.addEntity(var11);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            boolean var4 = false;
            boolean var5 = true;
            boolean var6 = false;
            boolean var7 = true;
            int var8 = this.sections * 5 - 1;
            this.fillWithOutline(world, box, 0, 0, 0, 2, 1, var8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fillRandomlyWithOutline(world, box, random, 0.8F, 0, 2, 0, 2, 2, var8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            if (this.hasCobwebs) {
               this.fillRandomlyWithOutline(world, box, random, 0.6F, 0, 0, 0, 2, 1, var8, Blocks.WEB.defaultState(), Blocks.AIR.defaultState(), false);
            }

            for(int var9 = 0; var9 < this.sections; ++var9) {
               int var10 = 2 + var9 * 5;
               this.fillWithOutline(world, box, 0, 0, var10, 0, 1, var10, Blocks.FENCE.defaultState(), Blocks.AIR.defaultState(), false);
               this.fillWithOutline(world, box, 2, 0, var10, 2, 1, var10, Blocks.FENCE.defaultState(), Blocks.AIR.defaultState(), false);
               if (random.nextInt(4) == 0) {
                  this.fillWithOutline(world, box, 0, 2, var10, 0, 2, var10, Blocks.PLANKS.defaultState(), Blocks.AIR.defaultState(), false);
                  this.fillWithOutline(world, box, 2, 2, var10, 2, 2, var10, Blocks.PLANKS.defaultState(), Blocks.AIR.defaultState(), false);
               } else {
                  this.fillWithOutline(world, box, 0, 2, var10, 2, 2, var10, Blocks.PLANKS.defaultState(), Blocks.AIR.defaultState(), false);
               }

               this.setBlockWithThreshold(world, box, random, 0.1F, 0, 2, var10 - 1, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.1F, 2, 2, var10 - 1, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.1F, 0, 2, var10 + 1, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.1F, 2, 2, var10 + 1, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.05F, 0, 2, var10 - 2, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.05F, 2, 2, var10 - 2, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.05F, 0, 2, var10 + 2, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.05F, 2, 2, var10 + 2, Blocks.WEB.defaultState());
               this.setBlockWithThreshold(world, box, random, 0.05F, 1, 2, var10 - 1, Blocks.TORCH.getStateFromMetadata(Direction.UP.getId()));
               this.setBlockWithThreshold(world, box, random, 0.05F, 1, 2, var10 + 1, Blocks.TORCH.getStateFromMetadata(Direction.UP.getId()));
               if (random.nextInt(100) == 0) {
                  this.placeChestWithLoot(
                     world,
                     box,
                     random,
                     2,
                     0,
                     var10 - 1,
                     LootEntry.addAll(MineshaftPieces.LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)),
                     3 + random.nextInt(4)
                  );
               }

               if (random.nextInt(100) == 0) {
                  this.placeChestWithLoot(
                     world,
                     box,
                     random,
                     0,
                     0,
                     var10 + 1,
                     LootEntry.addAll(MineshaftPieces.LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)),
                     3 + random.nextInt(4)
                  );
               }

               if (this.hasCobwebs && !this.hasSpiderSpawner) {
                  int var11 = this.transformY(0);
                  int var12 = var10 - 1 + random.nextInt(3);
                  int var13 = this.transformX(1, var12);
                  var12 = this.transformZ(1, var12);
                  BlockPos var14 = new BlockPos(var13, var11, var12);
                  if (box.contains(var14)) {
                     this.hasSpiderSpawner = true;
                     world.setBlockState(var14, Blocks.MOB_SPAWNER.defaultState(), 2);
                     BlockEntity var15 = world.getBlockEntity(var14);
                     if (var15 instanceof MobSpawnerBlockEntity) {
                        ((MobSpawnerBlockEntity)var15).getSpawner().setType("CaveSpider");
                     }
                  }
               }
            }

            for(int var16 = 0; var16 <= 2; ++var16) {
               for(int var18 = 0; var18 <= var8; ++var18) {
                  byte var20 = -1;
                  BlockState var22 = this.getBlockState(world, var16, var20, var18, box);
                  if (var22.getBlock().getMaterial() == Material.AIR) {
                     byte var23 = -1;
                     this.setBlockState(world, Blocks.PLANKS.defaultState(), var16, var23, var18, box);
                  }
               }
            }

            if (this.hasRails) {
               for(int var17 = 0; var17 <= var8; ++var17) {
                  BlockState var19 = this.getBlockState(world, 1, -1, var17, box);
                  if (var19.getBlock().getMaterial() != Material.AIR && var19.getBlock().isOpaque()) {
                     this.setBlockWithThreshold(
                        world, box, random, 0.7F, 1, 0, var17, Blocks.RAIL.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.RAIL, 0))
                     );
                  }
               }
            }

            return true;
         }
      }
   }

   public static class Crossing extends StructurePiece {
      private Direction direction;
      private boolean hasTwoFloors;

      public Crossing() {
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         nbt.putBoolean("tf", this.hasTwoFloors);
         nbt.putInt("D", this.direction.getIdHorizontal());
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         this.hasTwoFloors = nbt.getBoolean("tf");
         this.direction = Direction.byIdHorizontal(nbt.getInt("D"));
      }

      public Crossing(int generationDepth, Random random, StructureBox box, Direction direction) {
         super(generationDepth);
         this.direction = direction;
         this.box = box;
         this.hasTwoFloors = box.getSpanY() > 3;
      }

      public static StructureBox findSize(List piecesList, Random random, int x, int y, int z, Direction facing) {
         StructureBox var6 = new StructureBox(x, y, z, x, y + 2, z);
         if (random.nextInt(4) == 0) {
            var6.maxY += 4;
         }

         switch(facing) {
            case NORTH:
               var6.minX = x - 1;
               var6.maxX = x + 3;
               var6.minZ = z - 4;
               break;
            case SOUTH:
               var6.minX = x - 1;
               var6.maxX = x + 3;
               var6.maxZ = z + 4;
               break;
            case WEST:
               var6.minX = x - 4;
               var6.minZ = z - 1;
               var6.maxZ = z + 3;
               break;
            case EAST:
               var6.maxX = x + 4;
               var6.minZ = z - 1;
               var6.maxZ = z + 3;
         }

         return StructurePiece.getIntersectingPiece(piecesList, var6) != null ? null : var6;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         int var4 = this.getGenerationDepth();
         switch(this.direction) {
            case NORTH:
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.minZ - 1, Direction.NORTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY, this.box.minZ + 1, Direction.WEST, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.minZ + 1, Direction.EAST, var4);
               break;
            case SOUTH:
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY, this.box.minZ + 1, Direction.WEST, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.minZ + 1, Direction.EAST, var4);
               break;
            case WEST:
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.minZ - 1, Direction.NORTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY, this.box.minZ + 1, Direction.WEST, var4);
               break;
            case EAST:
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.minZ - 1, Direction.NORTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, var4);
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.minZ + 1, Direction.EAST, var4);
         }

         if (this.hasTwoFloors) {
            if (random.nextBoolean()) {
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY + 3 + 1, this.box.minZ - 1, Direction.NORTH, var4);
            }

            if (random.nextBoolean()) {
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY + 3 + 1, this.box.minZ + 1, Direction.WEST, var4);
            }

            if (random.nextBoolean()) {
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY + 3 + 1, this.box.minZ + 1, Direction.EAST, var4);
            }

            if (random.nextBoolean()) {
               MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX + 1, this.box.minY + 3 + 1, this.box.maxZ + 1, Direction.SOUTH, var4);
            }
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            if (this.hasTwoFloors) {
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX + 1,
                  this.box.minY,
                  this.box.minZ,
                  this.box.maxX - 1,
                  this.box.minY + 3 - 1,
                  this.box.maxZ,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX,
                  this.box.minY,
                  this.box.minZ + 1,
                  this.box.maxX,
                  this.box.minY + 3 - 1,
                  this.box.maxZ - 1,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX + 1,
                  this.box.maxY - 2,
                  this.box.minZ,
                  this.box.maxX - 1,
                  this.box.maxY,
                  this.box.maxZ,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX,
                  this.box.maxY - 2,
                  this.box.minZ + 1,
                  this.box.maxX,
                  this.box.maxY,
                  this.box.maxZ - 1,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX + 1,
                  this.box.minY + 3,
                  this.box.minZ + 1,
                  this.box.maxX - 1,
                  this.box.minY + 3,
                  this.box.maxZ - 1,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
            } else {
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX + 1,
                  this.box.minY,
                  this.box.minZ,
                  this.box.maxX - 1,
                  this.box.maxY,
                  this.box.maxZ,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
               this.fillWithOutline(
                  world,
                  box,
                  this.box.minX,
                  this.box.minY,
                  this.box.minZ + 1,
                  this.box.maxX,
                  this.box.maxY,
                  this.box.maxZ - 1,
                  Blocks.AIR.defaultState(),
                  Blocks.AIR.defaultState(),
                  false
               );
            }

            this.fillWithOutline(
               world,
               box,
               this.box.minX + 1,
               this.box.minY,
               this.box.minZ + 1,
               this.box.minX + 1,
               this.box.maxY,
               this.box.minZ + 1,
               Blocks.PLANKS.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );
            this.fillWithOutline(
               world,
               box,
               this.box.minX + 1,
               this.box.minY,
               this.box.maxZ - 1,
               this.box.minX + 1,
               this.box.maxY,
               this.box.maxZ - 1,
               Blocks.PLANKS.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );
            this.fillWithOutline(
               world,
               box,
               this.box.maxX - 1,
               this.box.minY,
               this.box.minZ + 1,
               this.box.maxX - 1,
               this.box.maxY,
               this.box.minZ + 1,
               Blocks.PLANKS.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );
            this.fillWithOutline(
               world,
               box,
               this.box.maxX - 1,
               this.box.minY,
               this.box.maxZ - 1,
               this.box.maxX - 1,
               this.box.maxY,
               this.box.maxZ - 1,
               Blocks.PLANKS.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );

            for(int var4 = this.box.minX; var4 <= this.box.maxX; ++var4) {
               for(int var5 = this.box.minZ; var5 <= this.box.maxZ; ++var5) {
                  if (this.getBlockState(world, var4, this.box.minY - 1, var5, box).getBlock().getMaterial() == Material.AIR) {
                     this.setBlockState(world, Blocks.PLANKS.defaultState(), var4, this.box.minY - 1, var5, box);
                  }
               }
            }

            return true;
         }
      }
   }

   public static class Room extends StructurePiece {
      private List entrances = Lists.newLinkedList();

      public Room() {
      }

      public Room(int generationDepth, Random random, int x, int z) {
         super(generationDepth);
         this.box = new StructureBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6));
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         int var4 = this.getGenerationDepth();
         int var6 = this.box.getSpanY() - 3 - 1;
         if (var6 <= 0) {
            var6 = 1;
         }

         int var9;
         for(var9 = 0; var9 < this.box.getSpanX(); var9 += 4) {
            var9 += random.nextInt(this.box.getSpanX());
            if (var9 + 3 > this.box.getSpanX()) {
               break;
            }

            StructurePiece var7 = MineshaftPieces.generateNextPiece(
               start, pieces, random, this.box.minX + var9, this.box.minY + random.nextInt(var6) + 1, this.box.minZ - 1, Direction.NORTH, var4
            );
            if (var7 != null) {
               StructureBox var8 = var7.getBoundingBox();
               this.entrances.add(new StructureBox(var8.minX, var8.minY, this.box.minZ, var8.maxX, var8.maxY, this.box.minZ + 1));
            }
         }

         for(var9 = 0; var9 < this.box.getSpanX(); var9 += 4) {
            var9 += random.nextInt(this.box.getSpanX());
            if (var9 + 3 > this.box.getSpanX()) {
               break;
            }

            StructurePiece var16 = MineshaftPieces.generateNextPiece(
               start, pieces, random, this.box.minX + var9, this.box.minY + random.nextInt(var6) + 1, this.box.maxZ + 1, Direction.SOUTH, var4
            );
            if (var16 != null) {
               StructureBox var19 = var16.getBoundingBox();
               this.entrances.add(new StructureBox(var19.minX, var19.minY, this.box.maxZ - 1, var19.maxX, var19.maxY, this.box.maxZ));
            }
         }

         for(var9 = 0; var9 < this.box.getSpanZ(); var9 += 4) {
            var9 += random.nextInt(this.box.getSpanZ());
            if (var9 + 3 > this.box.getSpanZ()) {
               break;
            }

            StructurePiece var17 = MineshaftPieces.generateNextPiece(
               start, pieces, random, this.box.minX - 1, this.box.minY + random.nextInt(var6) + 1, this.box.minZ + var9, Direction.WEST, var4
            );
            if (var17 != null) {
               StructureBox var20 = var17.getBoundingBox();
               this.entrances.add(new StructureBox(this.box.minX, var20.minY, var20.minZ, this.box.minX + 1, var20.maxY, var20.maxZ));
            }
         }

         for(var9 = 0; var9 < this.box.getSpanZ(); var9 += 4) {
            var9 += random.nextInt(this.box.getSpanZ());
            if (var9 + 3 > this.box.getSpanZ()) {
               break;
            }

            StructurePiece var18 = MineshaftPieces.generateNextPiece(
               start, pieces, random, this.box.maxX + 1, this.box.minY + random.nextInt(var6) + 1, this.box.minZ + var9, Direction.EAST, var4
            );
            if (var18 != null) {
               StructureBox var21 = var18.getBoundingBox();
               this.entrances.add(new StructureBox(this.box.maxX - 1, var21.minY, var21.minZ, this.box.maxX, var21.maxY, var21.maxZ));
            }
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fillWithOutline(
               world,
               box,
               this.box.minX,
               this.box.minY,
               this.box.minZ,
               this.box.maxX,
               this.box.minY,
               this.box.maxZ,
               Blocks.DIRT.defaultState(),
               Blocks.AIR.defaultState(),
               true
            );
            this.fillWithOutline(
               world,
               box,
               this.box.minX,
               this.box.minY + 1,
               this.box.minZ,
               this.box.maxX,
               Math.min(this.box.minY + 3, this.box.maxY),
               this.box.maxZ,
               Blocks.AIR.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );

            for(StructureBox var5 : this.entrances) {
               this.fillWithOutline(
                  world, box, var5.minX, var5.maxY - 2, var5.minZ, var5.maxX, var5.maxY, var5.maxZ, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false
               );
            }

            this.placeUpperHemisphere(
               world, box, this.box.minX, this.box.minY + 4, this.box.minZ, this.box.maxX, this.box.maxY, this.box.maxZ, Blocks.AIR.defaultState(), false
            );
            return true;
         }
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         NbtList var2 = new NbtList();

         for(StructureBox var4 : this.entrances) {
            var2.add(var4.toNbt());
         }

         nbt.put("Entrances", var2);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         NbtList var2 = nbt.getList("Entrances", 11);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.entrances.add(new StructureBox(var2.getIntArray(var3)));
         }
      }
   }

   public static class Stairs extends StructurePiece {
      public Stairs() {
      }

      public Stairs(int generationDepth, Random random, StructureBox box, Direction facing) {
         super(generationDepth);
         this.facing = facing;
         this.box = box;
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
      }

      public static StructureBox findSize(List piecesList, Random random, int x, int y, int z, Direction facing) {
         StructureBox var6 = new StructureBox(x, y - 5, z, x, y + 2, z);
         switch(facing) {
            case NORTH:
               var6.maxX = x + 2;
               var6.minZ = z - 8;
               break;
            case SOUTH:
               var6.maxX = x + 2;
               var6.maxZ = z + 8;
               break;
            case WEST:
               var6.minX = x - 8;
               var6.maxZ = z + 2;
               break;
            case EAST:
               var6.maxX = x + 8;
               var6.maxZ = z + 2;
         }

         return StructurePiece.getIntersectingPiece(piecesList, var6) != null ? null : var6;
      }

      @Override
      public void addChildren(StructurePiece start, List pieces, Random random) {
         int var4 = this.getGenerationDepth();
         if (this.facing != null) {
            switch(this.facing) {
               case NORTH:
                  MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX, this.box.minY, this.box.minZ - 1, Direction.NORTH, var4);
                  break;
               case SOUTH:
                  MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX, this.box.minY, this.box.maxZ + 1, Direction.SOUTH, var4);
                  break;
               case WEST:
                  MineshaftPieces.generateNextPiece(start, pieces, random, this.box.minX - 1, this.box.minY, this.box.minZ, Direction.WEST, var4);
                  break;
               case EAST:
                  MineshaftPieces.generateNextPiece(start, pieces, random, this.box.maxX + 1, this.box.minY, this.box.minZ, Direction.EAST, var4);
            }
         }
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.bordersOnLiquids(world, box)) {
            return false;
         } else {
            this.fillWithOutline(world, box, 0, 5, 0, 2, 7, 1, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
            this.fillWithOutline(world, box, 0, 0, 7, 2, 2, 8, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);

            for(int var4 = 0; var4 < 5; ++var4) {
               this.fillWithOutline(
                  world, box, 0, 5 - var4 - (var4 < 4 ? 1 : 0), 2 + var4, 2, 7 - var4, 2 + var4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false
               );
            }

            return true;
         }
      }
   }
}
