package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PortalBlock extends TransparentBlock {
   public static final EnumProperty AXIS = EnumProperty.of("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);

   public PortalBlock() {
      super(Material.PORTAL, false);
      this.setDefaultState(this.stateDefinition.any().set(AXIS, Direction.Axis.X));
      this.setTicksRandomly(true);
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      super.tick(world, pos, state, random);
      if (world.dimension.isOverworld() && world.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < world.getDifficulty().getIndex()) {
         int var5 = pos.getY();
         BlockPos var6 = pos;

         while(!World.hasSolidTop(world, var6) && var6.getY() > 0) {
            var6 = var6.down();
         }

         if (var5 > 0 && !world.getBlockState(var6.up()).getBlock().isConductor()) {
            Entity var7 = SpawnEggItem.spawnEntity(world, 57, (double)var6.getX() + 0.5, (double)var6.getY() + 1.1, (double)var6.getZ() + 0.5);
            if (var7 != null) {
               var7.netherPortalCooldown = var7.getDefaultNetherPortalCooldown();
            }
         }
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      Direction.Axis var3 = (Direction.Axis)world.getBlockState(pos).get(AXIS);
      float var4 = 0.125F;
      float var5 = 0.125F;
      if (var3 == Direction.Axis.X) {
         var4 = 0.5F;
      }

      if (var3 == Direction.Axis.Z) {
         var5 = 0.5F;
      }

      this.setShape(0.5F - var4, 0.0F, 0.5F - var5, 0.5F + var4, 1.0F, 0.5F + var5);
   }

   public static int getMatadataForAxis(Direction.Axis axis) {
      if (axis == Direction.Axis.X) {
         return 1;
      } else {
         return axis == Direction.Axis.Z ? 2 : 0;
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public boolean create(World world, BlockPos x) {
      PortalBlock.PortalBuilder var3 = new PortalBlock.PortalBuilder(world, x, Direction.Axis.X);
      if (var3.isValid() && var3.foundPortalBlocks == 0) {
         var3.build();
         return true;
      } else {
         PortalBlock.PortalBuilder var4 = new PortalBlock.PortalBuilder(world, x, Direction.Axis.Z);
         if (var4.isValid() && var4.foundPortalBlocks == 0) {
            var4.build();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      Direction.Axis var5 = (Direction.Axis)state.get(AXIS);
      if (var5 == Direction.Axis.X) {
         PortalBlock.PortalBuilder var6 = new PortalBlock.PortalBuilder(world, pos, Direction.Axis.X);
         if (!var6.isValid() || var6.foundPortalBlocks < var6.width * var6.height) {
            world.setBlockState(pos, Blocks.AIR.defaultState());
         }
      } else if (var5 == Direction.Axis.Z) {
         PortalBlock.PortalBuilder var7 = new PortalBlock.PortalBuilder(world, pos, Direction.Axis.Z);
         if (!var7.isValid() || var7.foundPortalBlocks < var7.width * var7.height) {
            world.setBlockState(pos, Blocks.AIR.defaultState());
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      Direction.Axis var4 = null;
      BlockState var5 = world.getBlockState(pos);
      if (world.getBlockState(pos).getBlock() == this) {
         var4 = (Direction.Axis)var5.get(AXIS);
         if (var4 == null) {
            return false;
         }

         if (var4 == Direction.Axis.Z && face != Direction.EAST && face != Direction.WEST) {
            return false;
         }

         if (var4 == Direction.Axis.X && face != Direction.SOUTH && face != Direction.NORTH) {
            return false;
         }
      }

      boolean var6 = world.getBlockState(pos.west()).getBlock() == this && world.getBlockState(pos.west(2)).getBlock() != this;
      boolean var7 = world.getBlockState(pos.east()).getBlock() == this && world.getBlockState(pos.east(2)).getBlock() != this;
      boolean var8 = world.getBlockState(pos.north()).getBlock() == this && world.getBlockState(pos.north(2)).getBlock() != this;
      boolean var9 = world.getBlockState(pos.south()).getBlock() == this && world.getBlockState(pos.south(2)).getBlock() != this;
      boolean var10 = var6 || var7 || var4 == Direction.Axis.X;
      boolean var11 = var8 || var9 || var4 == Direction.Axis.Z;
      if (var10 && face == Direction.WEST) {
         return true;
      } else if (var10 && face == Direction.EAST) {
         return true;
      } else if (var11 && face == Direction.NORTH) {
         return true;
      } else {
         return var11 && face == Direction.SOUTH;
      }
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (entity.vehicle == null && entity.rider == null) {
         entity.onPortalCollision();
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (random.nextInt(100) == 0) {
         world.playSound(
            (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "portal.portal", 0.5F, random.nextFloat() * 0.4F + 0.8F, false
         );
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)((float)pos.getX() + random.nextFloat());
         double var8 = (double)((float)pos.getY() + random.nextFloat());
         double var10 = (double)((float)pos.getZ() + random.nextFloat());
         double var12 = ((double)random.nextFloat() - 0.5) * 0.5;
         double var14 = ((double)random.nextFloat() - 0.5) * 0.5;
         double var16 = ((double)random.nextFloat() - 0.5) * 0.5;
         int var18 = random.nextInt(2) * 2 - 1;
         if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
            var6 = (double)pos.getX() + 0.5 + 0.25 * (double)var18;
            var12 = (double)(random.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)pos.getZ() + 0.5 + 0.25 * (double)var18;
            var16 = (double)(random.nextFloat() * 2.0F * (float)var18);
         }

         world.addParticle(ParticleType.PORTAL, var6, var8, var10, var12, var14, var16);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(0);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(AXIS, (metadata & 3) == 2 ? Direction.Axis.Z : Direction.Axis.X);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return getMatadataForAxis((Direction.Axis)state.get(AXIS));
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, AXIS);
   }

   public static class PortalBuilder {
      private final World world;
      private final Direction.Axis axis;
      private final Direction right;
      private final Direction left;
      private int foundPortalBlocks = 0;
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public PortalBuilder(World world, BlockPos pos, Direction.Axis axis) {
         this.world = world;
         this.axis = axis;
         if (axis == Direction.Axis.X) {
            this.left = Direction.EAST;
            this.right = Direction.WEST;
         } else {
            this.left = Direction.NORTH;
            this.right = Direction.SOUTH;
         }

         BlockPos var4 = pos;

         while(pos.getY() > var4.getY() - 21 && pos.getY() > 0 && this.canBeReplacedByPortal(world.getBlockState(pos.down()).getBlock())) {
            pos = pos.down();
         }

         int var5 = this.findWidth(pos, this.left) - 1;
         if (var5 >= 0) {
            this.bottomLeft = pos.offset(this.left, var5);
            this.width = this.findWidth(this.bottomLeft, this.right);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.findHeight();
         }
      }

      protected int findWidth(BlockPos pos, Direction dir) {
         int var3;
         for(var3 = 0; var3 < 22; ++var3) {
            BlockPos var4 = pos.offset(dir, var3);
            if (!this.canBeReplacedByPortal(this.world.getBlockState(var4).getBlock()) || this.world.getBlockState(var4.down()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block var5 = this.world.getBlockState(pos.offset(dir, var3)).getBlock();
         return var5 == Blocks.OBSIDIAN ? var3 : 0;
      }

      protected int findHeight() {
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int var1 = 0; var1 < this.width; ++var1) {
               BlockPos var2 = this.bottomLeft.offset(this.right, var1).up(this.height);
               Block var3 = this.world.getBlockState(var2).getBlock();
               if (!this.canBeReplacedByPortal(var3)) {
                  break label56;
               }

               if (var3 == Blocks.NETHER_PORTAL) {
                  ++this.foundPortalBlocks;
               }

               if (var1 == 0) {
                  var3 = this.world.getBlockState(var2.offset(this.left)).getBlock();
                  if (var3 != Blocks.OBSIDIAN) {
                     break label56;
                  }
               } else if (var1 == this.width - 1) {
                  var3 = this.world.getBlockState(var2.offset(this.right)).getBlock();
                  if (var3 != Blocks.OBSIDIAN) {
                     break label56;
                  }
               }
            }
         }

         for(int var4 = 0; var4 < this.width; ++var4) {
            if (this.world.getBlockState(this.bottomLeft.offset(this.right, var4).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean canBeReplacedByPortal(Block block) {
         return block.material == Material.AIR || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void build() {
         for(int var1 = 0; var1 < this.width; ++var1) {
            BlockPos var2 = this.bottomLeft.offset(this.right, var1);

            for(int var3 = 0; var3 < this.height; ++var3) {
               this.world.setBlockState(var2.up(var3), Blocks.NETHER_PORTAL.defaultState().set(PortalBlock.AXIS, this.axis), 2);
            }
         }
      }
   }
}
