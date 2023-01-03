package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireBlock extends Block {
   public static final IntegerProperty AGE = IntegerProperty.of("age", 0, 15);
   public static final BooleanProperty FLIP = BooleanProperty.of("flip");
   public static final BooleanProperty ALT = BooleanProperty.of("alt");
   public static final BooleanProperty NORTH = BooleanProperty.of("north");
   public static final BooleanProperty EAST = BooleanProperty.of("east");
   public static final BooleanProperty SOUTH = BooleanProperty.of("south");
   public static final BooleanProperty WEST = BooleanProperty.of("west");
   public static final IntegerProperty UPPER = IntegerProperty.of("upper", 0, 2);
   private final Map burnChance = Maps.newIdentityHashMap();
   private final Map spreadChance = Maps.newIdentityHashMap();

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      int var4 = pos.getX();
      int var5 = pos.getY();
      int var6 = pos.getZ();
      if (!World.hasSolidTop(world, pos.down()) && !Blocks.FIRE.canBurn(world, pos.down())) {
         boolean var7 = (var4 + var5 + var6 & 1) == 1;
         boolean var8 = (var4 / 2 + var5 / 2 + var6 / 2 & 1) == 1;
         int var9 = 0;
         if (this.canBurn(world, pos.up())) {
            var9 = var7 ? 1 : 2;
         }

         return state.set(NORTH, this.canBurn(world, pos.north()))
            .set(EAST, this.canBurn(world, pos.east()))
            .set(SOUTH, this.canBurn(world, pos.south()))
            .set(WEST, this.canBurn(world, pos.west()))
            .set(UPPER, var9)
            .set(FLIP, var8)
            .set(ALT, var7);
      } else {
         return this.defaultState();
      }
   }

   protected FireBlock() {
      super(Material.FIRE);
      this.setDefaultState(
         this.stateDefinition
            .any()
            .set(AGE, 0)
            .set(FLIP, false)
            .set(ALT, false)
            .set(NORTH, false)
            .set(EAST, false)
            .set(SOUTH, false)
            .set(WEST, false)
            .set(UPPER, 0)
      );
      this.setTicksRandomly(true);
   }

   public static void registerBurnProperties() {
      Blocks.FIRE.registerBurnProperties(Blocks.PLANKS, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.DOUBLE_WOODEN_SLAB, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.WOODEN_SLAB, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.FENCE, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.OAK_STAIRS, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.BIRCH_STAIRS, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.SPRUCE_STAIRS, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.JUNGLE_STAIRS, 5, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.LOG, 5, 5);
      Blocks.FIRE.registerBurnProperties(Blocks.LOG2, 5, 5);
      Blocks.FIRE.registerBurnProperties(Blocks.LEAVES, 30, 60);
      Blocks.FIRE.registerBurnProperties(Blocks.LEAVES2, 30, 60);
      Blocks.FIRE.registerBurnProperties(Blocks.BOOKSHELF, 30, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.TNT, 15, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.TALLGRASS, 60, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.DOUBLE_PLANT, 60, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.YELLOW_FLOWER, 60, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.RED_FLOWER, 60, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.DEADBUSH, 60, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.WOOL, 30, 60);
      Blocks.FIRE.registerBurnProperties(Blocks.VINE, 15, 100);
      Blocks.FIRE.registerBurnProperties(Blocks.COAL_BLOCK, 5, 5);
      Blocks.FIRE.registerBurnProperties(Blocks.HAY, 60, 20);
      Blocks.FIRE.registerBurnProperties(Blocks.CARPET, 60, 20);
   }

   public void registerBurnProperties(Block block, int burnChance, int spreadChance) {
      this.burnChance.put(block, burnChance);
      this.spreadChance.put(block, spreadChance);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public int getTickRate(World world) {
      return 30;
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (world.getGameRules().getBoolean("doFireTick")) {
         if (!this.canSurvive(world, pos)) {
            world.removeBlock(pos);
         }

         Block var5 = world.getBlockState(pos.down()).getBlock();
         boolean var6 = var5 == Blocks.NETHERRACK;
         if (world.dimension instanceof TheEndDimension && var5 == Blocks.BEDROCK) {
            var6 = true;
         }

         if (!var6 && world.isRaining() && this.shouldExtinguish(world, pos)) {
            world.removeBlock(pos);
         } else {
            int var7 = state.get(AGE);
            if (var7 < 15) {
               state = state.set(AGE, var7 + random.nextInt(3) / 2);
               world.setBlockState(pos, state, 4);
            }

            world.scheduleTick(pos, this, this.getTickRate(world) + random.nextInt(10));
            if (!var6) {
               if (!this.hasFlammableNeighbor(world, pos)) {
                  if (!World.hasSolidTop(world, pos.down()) || var7 > 3) {
                     world.removeBlock(pos);
                  }

                  return;
               }

               if (!this.canBurn(world, pos.down()) && var7 == 15 && random.nextInt(4) == 0) {
                  world.removeBlock(pos);
                  return;
               }
            }

            boolean var8 = world.isHumid(pos);
            byte var9 = 0;
            if (var8) {
               var9 = -50;
            }

            this.spread(world, pos.east(), 300 + var9, random, var7);
            this.spread(world, pos.west(), 300 + var9, random, var7);
            this.spread(world, pos.down(), 250 + var9, random, var7);
            this.spread(world, pos.up(), 250 + var9, random, var7);
            this.spread(world, pos.north(), 300 + var9, random, var7);
            this.spread(world, pos.south(), 300 + var9, random, var7);

            for(int var10 = -1; var10 <= 1; ++var10) {
               for(int var11 = -1; var11 <= 1; ++var11) {
                  for(int var12 = -1; var12 <= 4; ++var12) {
                     if (var10 != 0 || var12 != 0 || var11 != 0) {
                        int var13 = 100;
                        if (var12 > 1) {
                           var13 += (var12 - 1) * 100;
                        }

                        BlockPos var14 = pos.add(var10, var12, var11);
                        int var15 = this.getBurnChance(world, var14);
                        if (var15 > 0) {
                           int var16 = (var15 + 40 + world.getDifficulty().getIndex() * 7) / (var7 + 30);
                           if (var8) {
                              var16 /= 2;
                           }

                           if (var16 > 0 && random.nextInt(var13) <= var16 && (!world.isRaining() || !this.shouldExtinguish(world, var14))) {
                              int var17 = var7 + random.nextInt(5) / 4;
                              if (var17 > 15) {
                                 var17 = 15;
                              }

                              world.setBlockState(var14, state.set(AGE, var17), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   protected boolean shouldExtinguish(World world, BlockPos pos) {
      return world.isRaining(pos) || world.isRaining(pos.west()) || world.isRaining(pos.east()) || world.isRaining(pos.north()) || world.isRaining(pos.south());
   }

   @Override
   public boolean acceptsImmediateTicks() {
      return false;
   }

   private int getSpreadChance(Block block) {
      Integer var2 = (Integer)this.spreadChance.get(block);
      return var2 == null ? 0 : var2;
   }

   private int getBurnChance(Block block) {
      Integer var2 = (Integer)this.burnChance.get(block);
      return var2 == null ? 0 : var2;
   }

   private void spread(World world, BlockPos pos, int spreadFactor, Random random, int metadata) {
      int var6 = this.getSpreadChance(world.getBlockState(pos).getBlock());
      if (random.nextInt(spreadFactor) < var6) {
         BlockState var7 = world.getBlockState(pos);
         if (random.nextInt(metadata + 10) < 5 && !world.isRaining(pos)) {
            int var8 = metadata + random.nextInt(5) / 4;
            if (var8 > 15) {
               var8 = 15;
            }

            world.setBlockState(pos, this.defaultState().set(AGE, var8), 3);
         } else {
            world.removeBlock(pos);
         }

         if (var7.getBlock() == Blocks.TNT) {
            Blocks.TNT.onBroken(world, pos, var7.set(TntBlock.EXPLODE, true));
         }
      }
   }

   private boolean hasFlammableNeighbor(World world, BlockPos pos) {
      for(Direction var6 : Direction.values()) {
         if (this.canBurn(world, pos.offset(var6))) {
            return true;
         }
      }

      return false;
   }

   private int getBurnChance(World world, BlockPos pos) {
      if (!world.isAir(pos)) {
         return 0;
      } else {
         int var3 = 0;

         for(Direction var7 : Direction.values()) {
            var3 = Math.max(this.getBurnChance(world.getBlockState(pos.offset(var7)).getBlock()), var3);
         }

         return var3;
      }
   }

   @Override
   public boolean hasCollision() {
      return false;
   }

   public boolean canBurn(IWorld world, BlockPos pos) {
      return this.getBurnChance(world.getBlockState(pos).getBlock()) > 0;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return World.hasSolidTop(world, pos.down()) || this.hasFlammableNeighbor(world, pos);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!World.hasSolidTop(world, pos.down()) && !this.hasFlammableNeighbor(world, pos)) {
         world.removeBlock(pos);
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      if (world.dimension.getId() > 0 || !Blocks.NETHER_PORTAL.create(world, pos)) {
         if (!World.hasSolidTop(world, pos.down()) && !this.hasFlammableNeighbor(world, pos)) {
            world.removeBlock(pos);
         } else {
            world.scheduleTick(pos, this, this.getTickRate(world) + world.random.nextInt(10));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (random.nextInt(24) == 0) {
         world.playSound(
            (double)((float)pos.getX() + 0.5F),
            (double)((float)pos.getY() + 0.5F),
            (double)((float)pos.getZ() + 0.5F),
            "fire.fire",
            1.0F + random.nextFloat(),
            random.nextFloat() * 0.7F + 0.3F,
            false
         );
      }

      if (!World.hasSolidTop(world, pos.down()) && !Blocks.FIRE.canBurn(world, pos.down())) {
         if (Blocks.FIRE.canBurn(world, pos.west())) {
            for(int var9 = 0; var9 < 2; ++var9) {
               float var14 = (float)pos.getX() + random.nextFloat() * 0.1F;
               float var19 = (float)pos.getY() + random.nextFloat();
               float var24 = (float)pos.getZ() + random.nextFloat();
               world.addParticle(ParticleType.SMOKE_LARGE, (double)var14, (double)var19, (double)var24, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.FIRE.canBurn(world, pos.east())) {
            for(int var10 = 0; var10 < 2; ++var10) {
               float var15 = (float)(pos.getX() + 1) - random.nextFloat() * 0.1F;
               float var20 = (float)pos.getY() + random.nextFloat();
               float var25 = (float)pos.getZ() + random.nextFloat();
               world.addParticle(ParticleType.SMOKE_LARGE, (double)var15, (double)var20, (double)var25, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.FIRE.canBurn(world, pos.north())) {
            for(int var11 = 0; var11 < 2; ++var11) {
               float var16 = (float)pos.getX() + random.nextFloat();
               float var21 = (float)pos.getY() + random.nextFloat();
               float var26 = (float)pos.getZ() + random.nextFloat() * 0.1F;
               world.addParticle(ParticleType.SMOKE_LARGE, (double)var16, (double)var21, (double)var26, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.FIRE.canBurn(world, pos.south())) {
            for(int var12 = 0; var12 < 2; ++var12) {
               float var17 = (float)pos.getX() + random.nextFloat();
               float var22 = (float)pos.getY() + random.nextFloat();
               float var27 = (float)(pos.getZ() + 1) - random.nextFloat() * 0.1F;
               world.addParticle(ParticleType.SMOKE_LARGE, (double)var17, (double)var22, (double)var27, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.FIRE.canBurn(world, pos.up())) {
            for(int var13 = 0; var13 < 2; ++var13) {
               float var18 = (float)pos.getX() + random.nextFloat();
               float var23 = (float)(pos.getY() + 1) - random.nextFloat() * 0.1F;
               float var28 = (float)pos.getZ() + random.nextFloat();
               world.addParticle(ParticleType.SMOKE_LARGE, (double)var18, (double)var23, (double)var28, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for(int var5 = 0; var5 < 3; ++var5) {
            float var6 = (float)pos.getX() + random.nextFloat();
            float var7 = (float)pos.getY() + random.nextFloat() * 0.5F + 0.5F;
            float var8 = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleType.SMOKE_LARGE, (double)var6, (double)var7, (double)var8, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.LAVA;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(AGE, metadata);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(AGE);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT);
   }
}
