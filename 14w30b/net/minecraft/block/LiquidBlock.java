package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.world.color.BiomeColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class LiquidBlock extends Block {
   public static final IntegerProperty LEVEL = IntegerProperty.of("level", 0, 15);

   protected LiquidBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setDefaultState(this.stateDefinition.any().set(LEVEL, 0));
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setTicksRandomly(true);
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return this.material != Material.LAVA;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return this.material == Material.WATER ? BiomeColors.getWaterColor(world, pos) : 16777215;
   }

   public static float getHeightLoss(int level) {
      if (level >= 8) {
         level = 0;
      }

      return (float)(level + 1) / 9.0F;
   }

   protected int getLevel(IWorld world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().getMaterial() == this.material ? world.getBlockState(pos).get(LEVEL) : -1;
   }

   protected int getDistanceToSource(IWorld world, BlockPos pos) {
      int var3 = this.getLevel(world, pos);
      return var3 >= 8 ? 0 : var3;
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
   public boolean hasCollision(BlockState state, boolean allowFluids) {
      return allowFluids && state.get(LEVEL) == 0;
   }

   @Override
   public boolean isFaceSolid(IWorld world, BlockPos pos, Direction face) {
      Material var4 = world.getBlockState(pos).getBlock().getMaterial();
      if (var4 == this.material) {
         return false;
      } else if (face == Direction.UP) {
         return true;
      } else {
         return var4 == Material.ICE ? false : super.isFaceSolid(world, pos, face);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      if (world.getBlockState(pos).getBlock().getMaterial() == this.material) {
         return false;
      } else {
         return face == Direction.UP ? true : super.shouldRenderFace(world, pos, face);
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public int getRenderType() {
      return 1;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   protected Vec3d getFlow(IWorld world, BlockPos pos) {
      Vec3d var3 = new Vec3d(0.0, 0.0, 0.0);
      int var4 = this.getDistanceToSource(world, pos);

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockPos var7 = pos.offset(var6);
         int var8 = this.getDistanceToSource(world, var7);
         if (var8 < 0) {
            if (!world.getBlockState(var7).getBlock().getMaterial().blocksMovement()) {
               var8 = this.getDistanceToSource(world, var7.down());
               if (var8 >= 0) {
                  int var9 = var8 - (var4 - 8);
                  var3 = var3.add(
                     (double)((var7.getX() - pos.getX()) * var9), (double)((var7.getY() - pos.getY()) * var9), (double)((var7.getZ() - pos.getZ()) * var9)
                  );
               }
            }
         } else if (var8 >= 0) {
            int var14 = var8 - var4;
            var3 = var3.add(
               (double)((var7.getX() - pos.getX()) * var14), (double)((var7.getY() - pos.getY()) * var14), (double)((var7.getZ() - pos.getZ()) * var14)
            );
         }
      }

      if (world.getBlockState(pos).get(LEVEL) >= 8) {
         for(Direction var11 : Direction.Plane.HORIZONTAL) {
            BlockPos var12 = pos.offset(var11);
            if (this.isFaceSolid(world, var12, var11) || this.isFaceSolid(world, var12.up(), var11)) {
               var3 = var3.normalize().add(0.0, -6.0, 0.0);
               break;
            }
         }
      }

      return var3.normalize();
   }

   @Override
   public Vec3d applyMaterialDrag(World world, BlockPos pos, Entity entity, Vec3d velocity) {
      return velocity.add(this.getFlow(world, pos));
   }

   @Override
   public int getTickRate(World world) {
      if (this.material == Material.WATER) {
         return 5;
      } else if (this.material == Material.LAVA) {
         return world.dimension.isDark() ? 10 : 30;
      } else {
         return 0;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightColor(IWorld world, BlockPos pos) {
      int var3 = world.getLightColor(pos, 0);
      int var4 = world.getLightColor(pos.up(), 0);
      int var5 = var3 & 0xFF;
      int var6 = var4 & 0xFF;
      int var7 = var3 >> 16 & 0xFF;
      int var8 = var4 >> 16 & 0xFF;
      return (var5 > var6 ? var5 : var6) | (var7 > var8 ? var7 : var8) << 16;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return this.material == Material.WATER ? BlockLayer.TRANSLUCENT : BlockLayer.SOLID;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      double var5 = (double)pos.getX();
      double var7 = (double)pos.getY();
      double var9 = (double)pos.getZ();
      if (this.material == Material.WATER) {
         int var11 = state.get(LEVEL);
         if (var11 > 0 && var11 < 8) {
            if (random.nextInt(64) == 0) {
               world.playSound(var5 + 0.5, var7 + 0.5, var9 + 0.5, "liquid.water", random.nextFloat() * 0.25F + 0.75F, random.nextFloat() * 1.0F + 0.5F, false);
            }
         } else if (random.nextInt(10) == 0) {
            world.addParticle(
               ParticleType.SUSPENDED, var5 + (double)random.nextFloat(), var7 + (double)random.nextFloat(), var9 + (double)random.nextFloat(), 0.0, 0.0, 0.0
            );
         }
      }

      if (this.material == Material.LAVA
         && world.getBlockState(pos.up()).getBlock().getMaterial() == Material.AIR
         && !world.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
         if (random.nextInt(100) == 0) {
            double var18 = var5 + (double)random.nextFloat();
            double var13 = var7 + this.maxY;
            double var15 = var9 + (double)random.nextFloat();
            world.addParticle(ParticleType.LAVA, var18, var13, var15, 0.0, 0.0, 0.0);
            world.playSound(var18, var13, var15, "liquid.lavapop", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }

         if (random.nextInt(200) == 0) {
            world.playSound(var5, var7, var9, "liquid.lava", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

      if (random.nextInt(10) == 0 && World.hasSolidTop(world, pos.down())) {
         Material var19 = world.getBlockState(pos.down(2)).getBlock().getMaterial();
         if (!var19.blocksMovement() && !var19.isLiquid()) {
            double var12 = var5 + (double)random.nextFloat();
            double var14 = var7 - 1.05;
            double var16 = var9 + (double)random.nextFloat();
            if (this.material == Material.WATER) {
               world.addParticle(ParticleType.DRIP_WATER, var12, var14, var16, 0.0, 0.0, 0.0);
            } else {
               world.addParticle(ParticleType.DRIP_LAVA, var12, var14, var16, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static double getFlowAngle(IWorld world, BlockPos pos, Material material) {
      Vec3d var3 = getFlowing(material).getFlow(world, pos);
      return var3.x == 0.0 && var3.z == 0.0 ? -1000.0 : Math.atan2(var3.z, var3.x) - (Math.PI / 2);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.checkSpreadCollisions(world, pos, state);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.checkSpreadCollisions(world, pos, state);
   }

   public boolean checkSpreadCollisions(World world, BlockPos pos, BlockState state) {
      if (this.material == Material.LAVA) {
         boolean var4 = false;

         for(Direction var8 : Direction.values()) {
            if (var8 != Direction.DOWN && world.getBlockState(pos.offset(var8)).getBlock().getMaterial() == Material.WATER) {
               var4 = true;
               break;
            }
         }

         if (var4) {
            Integer var9 = (Integer)state.get(LEVEL);
            if (var9 == 0) {
               world.setBlockState(pos, Blocks.OBSIDIAN.defaultState());
               this.fizz(world, pos);
               return true;
            }

            if (var9 <= 4) {
               world.setBlockState(pos, Blocks.COBBLESTONE.defaultState());
               this.fizz(world, pos);
               return true;
            }
         }
      }

      return false;
   }

   protected void fizz(World world, BlockPos pos) {
      double var3 = (double)pos.getX();
      double var5 = (double)pos.getY();
      double var7 = (double)pos.getZ();
      world.playSound(var3 + 0.5, var5 + 0.5, var7 + 0.5, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

      for(int var9 = 0; var9 < 8; ++var9) {
         world.addParticle(ParticleType.SMOKE_LARGE, var3 + Math.random(), var5 + 1.2, var7 + Math.random(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(LEVEL, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(LEVEL);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, LEVEL);
   }

   public static FlowingLiquidBlock getFlowing(Material material) {
      if (material == Material.WATER) {
         return Blocks.FLOWING_WATER;
      } else if (material == Material.LAVA) {
         return Blocks.FLOWING_LAVA;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public static LiquidSourceBlock getSource(Material material) {
      if (material == Material.WATER) {
         return Blocks.WATER;
      } else if (material == Material.LAVA) {
         return Blocks.LAVA;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }
}
