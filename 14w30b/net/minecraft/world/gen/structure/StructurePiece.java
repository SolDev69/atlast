package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.WoodenDoorItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class StructurePiece {
   protected StructureBox box;
   protected Direction facing;
   protected int generationDepth;

   public StructurePiece() {
   }

   protected StructurePiece(int generationDepth) {
      this.generationDepth = generationDepth;
   }

   public NbtCompound toNbt() {
      NbtCompound var1 = new NbtCompound();
      var1.putString("id", StructureManager.getId(this));
      var1.put("BB", this.box.toNbt());
      var1.putInt("O", this.facing == null ? -1 : this.facing.getIdHorizontal());
      var1.putInt("GD", this.generationDepth);
      this.writeNbt(var1);
      return var1;
   }

   protected abstract void writeNbt(NbtCompound nbt);

   public void readNbt(World world, NbtCompound nbt) {
      if (nbt.contains("BB")) {
         this.box = new StructureBox(nbt.getIntArray("BB"));
      }

      int var3 = nbt.getInt("O");
      this.facing = var3 == -1 ? null : Direction.byIdHorizontal(var3);
      this.generationDepth = nbt.getInt("GD");
      this.readNbt(nbt);
   }

   protected abstract void readNbt(NbtCompound nbt);

   public void addChildren(StructurePiece start, List pieces, Random random) {
   }

   public abstract boolean postProcess(World world, Random random, StructureBox box);

   public StructureBox getBoundingBox() {
      return this.box;
   }

   public int getGenerationDepth() {
      return this.generationDepth;
   }

   public static StructurePiece getIntersectingPiece(List pieces, StructureBox box) {
      for(StructurePiece var3 : pieces) {
         if (var3.getBoundingBox() != null && var3.getBoundingBox().intersects(box)) {
            return var3;
         }
      }

      return null;
   }

   public BlockPos getCenterPos() {
      return new BlockPos(this.box.getCenter());
   }

   protected boolean bordersOnLiquids(World world, StructureBox box) {
      int var3 = Math.max(this.box.minX - 1, box.minX);
      int var4 = Math.max(this.box.minY - 1, box.minY);
      int var5 = Math.max(this.box.minZ - 1, box.minZ);
      int var6 = Math.min(this.box.maxX + 1, box.maxX);
      int var7 = Math.min(this.box.maxY + 1, box.maxY);
      int var8 = Math.min(this.box.maxZ + 1, box.maxZ);

      for(int var9 = var3; var9 <= var6; ++var9) {
         for(int var10 = var5; var10 <= var8; ++var10) {
            if (world.getBlockState(new BlockPos(var9, var4, var10)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (world.getBlockState(new BlockPos(var9, var7, var10)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int var11 = var3; var11 <= var6; ++var11) {
         for(int var13 = var4; var13 <= var7; ++var13) {
            if (world.getBlockState(new BlockPos(var11, var13, var5)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (world.getBlockState(new BlockPos(var11, var13, var8)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int var12 = var5; var12 <= var8; ++var12) {
         for(int var14 = var4; var14 <= var7; ++var14) {
            if (world.getBlockState(new BlockPos(var3, var14, var12)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (world.getBlockState(new BlockPos(var6, var14, var12)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int transformX(int x, int z) {
      if (this.facing == null) {
         return x;
      } else {
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               return this.box.minX + x;
            case WEST:
               return this.box.maxX - z;
            case EAST:
               return this.box.minX + z;
            default:
               return x;
         }
      }
   }

   protected int transformY(int y) {
      return this.facing == null ? y : y + this.box.minY;
   }

   protected int transformZ(int x, int z) {
      if (this.facing == null) {
         return z;
      } else {
         switch(this.facing) {
            case NORTH:
               return this.box.maxZ - z;
            case SOUTH:
               return this.box.minZ + z;
            case WEST:
            case EAST:
               return this.box.minZ + x;
            default:
               return z;
         }
      }
   }

   protected int postProcessBlockMetadata(Block block, int facing) {
      if (block == Blocks.RAIL) {
         if (this.facing == Direction.WEST || this.facing == Direction.EAST) {
            if (facing == 1) {
               return 0;
            }

            return 1;
         }
      } else if (block != Blocks.WOODEN_DOOR && block != Blocks.IRON_DOOR) {
         if (block != Blocks.STONE_STAIRS
            && block != Blocks.OAK_STAIRS
            && block != Blocks.NETHER_BRICK_STAIRS
            && block != Blocks.STONE_BRICK_STAIRS
            && block != Blocks.SANDSTONE_STAIRS) {
            if (block == Blocks.LADDER) {
               if (this.facing == Direction.SOUTH) {
                  if (facing == Direction.NORTH.getId()) {
                     return Direction.SOUTH.getId();
                  }

                  if (facing == Direction.SOUTH.getId()) {
                     return Direction.NORTH.getId();
                  }
               } else if (this.facing == Direction.WEST) {
                  if (facing == Direction.NORTH.getId()) {
                     return Direction.WEST.getId();
                  }

                  if (facing == Direction.SOUTH.getId()) {
                     return Direction.EAST.getId();
                  }

                  if (facing == Direction.WEST.getId()) {
                     return Direction.NORTH.getId();
                  }

                  if (facing == Direction.EAST.getId()) {
                     return Direction.SOUTH.getId();
                  }
               } else if (this.facing == Direction.EAST) {
                  if (facing == Direction.NORTH.getId()) {
                     return Direction.EAST.getId();
                  }

                  if (facing == Direction.SOUTH.getId()) {
                     return Direction.WEST.getId();
                  }

                  if (facing == Direction.WEST.getId()) {
                     return Direction.NORTH.getId();
                  }

                  if (facing == Direction.EAST.getId()) {
                     return Direction.SOUTH.getId();
                  }
               }
            } else if (block == Blocks.STONE_BUTTON) {
               if (this.facing == Direction.SOUTH) {
                  if (facing == 3) {
                     return 4;
                  }

                  if (facing == 4) {
                     return 3;
                  }
               } else if (this.facing == Direction.WEST) {
                  if (facing == 3) {
                     return 1;
                  }

                  if (facing == 4) {
                     return 2;
                  }

                  if (facing == 2) {
                     return 3;
                  }

                  if (facing == 1) {
                     return 4;
                  }
               } else if (this.facing == Direction.EAST) {
                  if (facing == 3) {
                     return 2;
                  }

                  if (facing == 4) {
                     return 1;
                  }

                  if (facing == 2) {
                     return 3;
                  }

                  if (facing == 1) {
                     return 4;
                  }
               }
            } else if (block == Blocks.TRIPWIRE_HOOK || block instanceof HorizontalFacingBlock) {
               Direction var3 = Direction.byIdHorizontal(facing);
               if (this.facing == Direction.SOUTH) {
                  if (var3 == Direction.SOUTH || var3 == Direction.NORTH) {
                     return var3.getOpposite().getIdHorizontal();
                  }
               } else if (this.facing == Direction.WEST) {
                  if (var3 == Direction.NORTH) {
                     return Direction.WEST.getIdHorizontal();
                  }

                  if (var3 == Direction.SOUTH) {
                     return Direction.EAST.getIdHorizontal();
                  }

                  if (var3 == Direction.WEST) {
                     return Direction.NORTH.getIdHorizontal();
                  }

                  if (var3 == Direction.EAST) {
                     return Direction.SOUTH.getIdHorizontal();
                  }
               } else if (this.facing == Direction.EAST) {
                  if (var3 == Direction.NORTH) {
                     return Direction.EAST.getIdHorizontal();
                  }

                  if (var3 == Direction.SOUTH) {
                     return Direction.WEST.getIdHorizontal();
                  }

                  if (var3 == Direction.WEST) {
                     return Direction.NORTH.getIdHorizontal();
                  }

                  if (var3 == Direction.EAST) {
                     return Direction.SOUTH.getIdHorizontal();
                  }
               }
            } else if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.LEVER || block == Blocks.DISPENSER) {
               if (this.facing == Direction.SOUTH) {
                  if (facing == Direction.NORTH.getId() || facing == Direction.SOUTH.getId()) {
                     return Direction.byId(facing).getOpposite().getId();
                  }
               } else if (this.facing == Direction.WEST) {
                  if (facing == Direction.NORTH.getId()) {
                     return Direction.WEST.getId();
                  }

                  if (facing == Direction.SOUTH.getId()) {
                     return Direction.EAST.getId();
                  }

                  if (facing == Direction.WEST.getId()) {
                     return Direction.NORTH.getId();
                  }

                  if (facing == Direction.EAST.getId()) {
                     return Direction.SOUTH.getId();
                  }
               } else if (this.facing == Direction.EAST) {
                  if (facing == Direction.NORTH.getId()) {
                     return Direction.EAST.getId();
                  }

                  if (facing == Direction.SOUTH.getId()) {
                     return Direction.WEST.getId();
                  }

                  if (facing == Direction.WEST.getId()) {
                     return Direction.NORTH.getId();
                  }

                  if (facing == Direction.EAST.getId()) {
                     return Direction.SOUTH.getId();
                  }
               }
            }
         } else if (this.facing == Direction.SOUTH) {
            if (facing == 2) {
               return 3;
            }

            if (facing == 3) {
               return 2;
            }
         } else if (this.facing == Direction.WEST) {
            if (facing == 0) {
               return 2;
            }

            if (facing == 1) {
               return 3;
            }

            if (facing == 2) {
               return 0;
            }

            if (facing == 3) {
               return 1;
            }
         } else if (this.facing == Direction.EAST) {
            if (facing == 0) {
               return 2;
            }

            if (facing == 1) {
               return 3;
            }

            if (facing == 2) {
               return 1;
            }

            if (facing == 3) {
               return 0;
            }
         }
      } else if (this.facing == Direction.SOUTH) {
         if (facing == 0) {
            return 2;
         }

         if (facing == 2) {
            return 0;
         }
      } else {
         if (this.facing == Direction.WEST) {
            return facing + 1 & 3;
         }

         if (this.facing == Direction.EAST) {
            return facing + 3 & 3;
         }
      }

      return facing;
   }

   protected void setBlockState(World world, BlockState state, int x, int y, int z, StructureBox box) {
      BlockPos var7 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
      if (box.contains(var7)) {
         world.setBlockState(var7, state, 2);
      }
   }

   protected BlockState getBlockState(World world, int x, int y, int z, StructureBox box) {
      int var6 = this.transformX(x, z);
      int var7 = this.transformY(y);
      int var8 = this.transformZ(x, z);
      return !box.contains(new BlockPos(var6, var7, var8)) ? Blocks.AIR.defaultState() : world.getBlockState(new BlockPos(var6, var7, var8));
   }

   protected void fillAir(World world, StructureBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for(int var9 = minY; var9 <= maxY; ++var9) {
         for(int var10 = minX; var10 <= maxX; ++var10) {
            for(int var11 = minZ; var11 <= maxZ; ++var11) {
               this.setBlockState(world, Blocks.AIR.defaultState(), var10, var9, var11, box);
            }
         }
      }
   }

   protected void fillWithOutline(
      World world, StructureBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState edge, BlockState filler, boolean avoidAir
   ) {
      for(int var12 = minY; var12 <= maxY; ++var12) {
         for(int var13 = minX; var13 <= maxX; ++var13) {
            for(int var14 = minZ; var14 <= maxZ; ++var14) {
               if (!avoidAir || this.getBlockState(world, var13, var12, var14, box).getBlock().getMaterial() != Material.AIR) {
                  if (var12 != minY && var12 != maxY && var13 != minX && var13 != maxX && var14 != minZ && var14 != maxZ) {
                     this.setBlockState(world, filler, var13, var12, var14, box);
                  } else {
                     this.setBlockState(world, edge, var13, var12, var14, box);
                  }
               }
            }
         }
      }
   }

   protected void fill(
      World world,
      StructureBox box,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      boolean avoidAir,
      Random random,
      StructurePiece.BlockPicker blocks
   ) {
      for(int var12 = minY; var12 <= maxY; ++var12) {
         for(int var13 = minX; var13 <= maxX; ++var13) {
            for(int var14 = minZ; var14 <= maxZ; ++var14) {
               if (!avoidAir || this.getBlockState(world, var13, var12, var14, box).getBlock().getMaterial() != Material.AIR) {
                  blocks.pick(random, var13, var12, var14, var12 == minY || var12 == maxY || var13 == minX || var13 == maxX || var14 == minZ || var14 == maxZ);
                  this.setBlockState(world, blocks.getBlockState(), var13, var12, var14, box);
               }
            }
         }
      }
   }

   protected void fillRandomlyWithOutline(
      World world,
      StructureBox box,
      Random random,
      float threshold,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockState edge,
      BlockState filler,
      boolean avoidAir
   ) {
      for(int var14 = minY; var14 <= maxY; ++var14) {
         for(int var15 = minX; var15 <= maxX; ++var15) {
            for(int var16 = minZ; var16 <= maxZ; ++var16) {
               if (!(random.nextFloat() > threshold)
                  && (!avoidAir || this.getBlockState(world, var15, var14, var16, box).getBlock().getMaterial() != Material.AIR)) {
                  if (var14 != minY && var14 != maxY && var15 != minX && var15 != maxX && var16 != minZ && var16 != maxZ) {
                     this.setBlockState(world, filler, var15, var14, var16, box);
                  } else {
                     this.setBlockState(world, edge, var15, var14, var16, box);
                  }
               }
            }
         }
      }
   }

   protected void setBlockWithThreshold(World world, StructureBox box, Random random, float threshold, int x, int y, int z, BlockState state) {
      if (random.nextFloat() < threshold) {
         this.setBlockState(world, state, x, y, z, box);
      }
   }

   protected void placeUpperHemisphere(
      World world, StructureBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state, boolean avoidAir
   ) {
      float var11 = (float)(maxX - minX + 1);
      float var12 = (float)(maxY - minY + 1);
      float var13 = (float)(maxZ - minZ + 1);
      float var14 = (float)minX + var11 / 2.0F;
      float var15 = (float)minZ + var13 / 2.0F;

      for(int var16 = minY; var16 <= maxY; ++var16) {
         float var17 = (float)(var16 - minY) / var12;

         for(int var18 = minX; var18 <= maxX; ++var18) {
            float var19 = ((float)var18 - var14) / (var11 * 0.5F);

            for(int var20 = minZ; var20 <= maxZ; ++var20) {
               float var21 = ((float)var20 - var15) / (var13 * 0.5F);
               if (!avoidAir || this.getBlockState(world, var18, var16, var20, box).getBlock().getMaterial() != Material.AIR) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if (var22 <= 1.05F) {
                     this.setBlockState(world, state, var18, var16, var20, box);
                  }
               }
            }
         }
      }
   }

   protected void fillAirColumnUp(World world, int x, int y, int z, StructureBox box) {
      BlockPos var6 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
      if (box.contains(var6)) {
         while(!world.isAir(var6) && var6.getY() < 255) {
            world.setBlockState(var6, Blocks.AIR.defaultState(), 2);
            var6 = var6.up();
         }
      }
   }

   protected void fillColumnDown(World world, BlockState state, int x, int y, int z, StructureBox box) {
      int var7 = this.transformX(x, z);
      int var8 = this.transformY(y);
      int var9 = this.transformZ(x, z);
      if (box.contains(new BlockPos(var7, var8, var9))) {
         while(
            (world.isAir(new BlockPos(var7, var8, var9)) || world.getBlockState(new BlockPos(var7, var8, var9)).getBlock().getMaterial().isLiquid())
               && var8 > 1
         ) {
            world.setBlockState(new BlockPos(var7, var8, var9), state, 2);
            --var8;
         }
      }
   }

   protected boolean placeChestWithLoot(World world, StructureBox box, Random random, int x, int y, int z, List entries, int amount) {
      BlockPos var9 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
      if (box.contains(var9) && world.getBlockState(var9).getBlock() != Blocks.CHEST) {
         BlockState var10 = Blocks.CHEST.defaultState();
         world.setBlockState(var9, Blocks.CHEST.updateFacing(world, var9, var10), 2);
         BlockEntity var11 = world.getBlockEntity(var9);
         if (var11 instanceof ChestBlockEntity) {
            LootEntry.addLoot(random, entries, (ChestBlockEntity)var11, amount);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean placeDispenserWithLoot(World world, StructureBox box, Random random, int x, int y, int z, int facing, List entries, int amount) {
      BlockPos var10 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
      if (box.contains(var10) && world.getBlockState(var10).getBlock() != Blocks.DISPENSER) {
         world.setBlockState(var10, Blocks.DISPENSER.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.DISPENSER, facing)), 2);
         BlockEntity var11 = world.getBlockEntity(var10);
         if (var11 instanceof DispenserBlockEntity) {
            LootEntry.addLoot(random, entries, (DispenserBlockEntity)var11, amount);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void placeWoodenDoor(World world, StructureBox box, Random random, int x, int y, int z, Direction facing) {
      BlockPos var8 = new BlockPos(this.transformX(x, z), this.transformY(y), this.transformZ(x, z));
      if (box.contains(var8)) {
         WoodenDoorItem.place(world, var8, facing.counterClockwiseY(), Blocks.WOODEN_DOOR);
      }
   }

   public abstract static class BlockPicker {
      protected BlockState state = Blocks.AIR.defaultState();

      protected BlockPicker() {
      }

      public abstract void pick(Random randomm, int x, int y, int z, boolean isNonAir);

      public BlockState getBlockState() {
         return this.state;
      }
   }
}
