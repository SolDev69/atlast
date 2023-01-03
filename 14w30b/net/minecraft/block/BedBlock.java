package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BedBlock extends HorizontalFacingBlock {
   public static final EnumProperty PART = EnumProperty.of("part", BedBlock.Part.class);
   public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");

   public BedBlock() {
      super(Material.WOOL);
      this.setDefaultState(this.stateDefinition.any().set(PART, BedBlock.Part.FOOT).set(OCCUPIED, false));
      this.setBoundingBox();
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         if (state.get(PART) != BedBlock.Part.HEAD) {
            pos = pos.offset((Direction)state.get(FACING));
            state = world.getBlockState(pos);
            if (state.getBlock() != this) {
               return true;
            }
         }

         if (world.dimension.hasWorldSpawn() && world.getBiome(pos) != Biome.HELL) {
            if (state.get(OCCUPIED)) {
               PlayerEntity var11 = this.getOccupant(world, pos);
               if (var11 != null) {
                  player.addMessage(new TranslatableText("tile.bed.occupied"));
                  return true;
               }

               state = state.set(OCCUPIED, false);
               world.setBlockState(pos, state, 4);
            }

            PlayerEntity.SleepAllowedStatus var12 = player.trySleep(pos);
            if (var12 == PlayerEntity.SleepAllowedStatus.OK) {
               state = state.set(OCCUPIED, true);
               world.setBlockState(pos, state, 4);
               return true;
            } else {
               if (var12 == PlayerEntity.SleepAllowedStatus.NOT_POSSIBLE_NOW) {
                  player.addMessage(new TranslatableText("tile.bed.noSleep"));
               } else if (var12 == PlayerEntity.SleepAllowedStatus.NOT_SAFE) {
                  player.addMessage(new TranslatableText("tile.bed.notSafe"));
               }

               return true;
            }
         } else {
            world.removeBlock(pos);
            BlockPos var9 = pos.offset(((Direction)state.get(FACING)).getOpposite());
            if (world.getBlockState(var9).getBlock() == this) {
               world.removeBlock(var9);
            }

            world.explode(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0F, true, true);
            return true;
         }
      }
   }

   private PlayerEntity getOccupant(World world, BlockPos pos) {
      for(PlayerEntity var4 : world.players) {
         if (var4.isSleeping() && var4.sleepingPos.equals(pos)) {
            return var4;
         }
      }

      return null;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.setBoundingBox();
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      Direction var5 = (Direction)state.get(FACING);
      if (state.get(PART) == BedBlock.Part.HEAD) {
         if (world.getBlockState(pos.offset(var5.getOpposite())).getBlock() != this) {
            world.removeBlock(pos);
         }
      } else if (world.getBlockState(pos.offset(var5)).getBlock() != this) {
         world.removeBlock(pos);
         if (!world.isClient) {
            this.dropItems(world, pos, state, 0);
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return state.get(PART) == BedBlock.Part.HEAD ? null : Items.BED;
   }

   private void setBoundingBox() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos getSpawnPos(World world, BlockPos pos, int skip) {
      Direction var3 = (Direction)world.getBlockState(pos).get(FACING);
      int var4 = pos.getX();
      int var5 = pos.getY();
      int var6 = pos.getZ();

      for(int var7 = 0; var7 <= 1; ++var7) {
         int var8 = var4 - var3.getOffsetX() * var7 - 1;
         int var9 = var6 - var3.getOffsetZ() * var7 - 1;
         int var10 = var8 + 2;
         int var11 = var9 + 2;

         for(int var12 = var8; var12 <= var10; ++var12) {
            for(int var13 = var9; var13 <= var11; ++var13) {
               BlockPos var14 = new BlockPos(var12, var5, var13);
               if (isValidSpawnPos(world, var14)) {
                  if (skip <= 0) {
                     return var14;
                  }

                  --skip;
               }
            }
         }
      }

      return null;
   }

   protected static boolean isValidSpawnPos(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down())
         && !world.getBlockState(pos).getBlock().getMaterial().isSolid()
         && !world.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (state.get(PART) == BedBlock.Part.FOOT) {
         super.dropItems(world, pos, state, luck, 0);
      }
   }

   @Override
   public int getPistonMoveBehavior() {
      return 1;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.BED;
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (player.abilities.creativeMode && state.get(PART) == BedBlock.Part.HEAD) {
         BlockPos var5 = pos.offset(((Direction)state.get(FACING)).getOpposite());
         if (world.getBlockState(var5).getBlock() == this) {
            world.removeBlock(var5);
         }
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      Direction var2 = Direction.byIdHorizontal(metadata);
      return (metadata & 8) > 0
         ? this.defaultState().set(PART, BedBlock.Part.HEAD).set(FACING, var2).set(OCCUPIED, (metadata & 4) > 0)
         : this.defaultState().set(PART, BedBlock.Part.FOOT).set(FACING, var2);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      if (state.get(PART) == BedBlock.Part.FOOT) {
         BlockState var4 = world.getBlockState(pos.offset((Direction)state.get(FACING)));
         if (var4.getBlock() == this) {
            state = state.set(OCCUPIED, var4.get(OCCUPIED));
         }
      }

      return state;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getIdHorizontal();
      if (state.get(PART) == BedBlock.Part.HEAD) {
         var2 |= 8;
         if (state.get(OCCUPIED)) {
            var2 |= 4;
         }
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, PART, OCCUPIED);
   }

   public static enum Part implements StringRepresentable {
      HEAD("head"),
      FOOT("foot");

      private final String id;

      private Part(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }
}
