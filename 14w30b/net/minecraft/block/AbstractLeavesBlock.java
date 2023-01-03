package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.world.color.BiomeColors;
import net.minecraft.client.world.color.FoliageColors;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class AbstractLeavesBlock extends BlockWithCulling {
   public static final BooleanProperty DECAYABLE = BooleanProperty.of("decayable");
   public static final BooleanProperty CHECK_DECAY = BooleanProperty.of("check_decay");
   int[] decayRegion;
   @Environment(EnvType.CLIENT)
   protected int spriteIndex;
   @Environment(EnvType.CLIENT)
   protected boolean renderCutout;

   public AbstractLeavesBlock() {
      super(Material.LEAVES, false);
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setStrength(0.2F);
      this.setOpacity(1);
      this.setSound(GRASS_SOUND);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor() {
      return FoliageColors.get(0.5, 1.0);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      return FoliageColors.getDefaultColor();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return BiomeColors.getFoliageColor(world, pos);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      byte var4 = 1;
      int var5 = var4 + 1;
      int var6 = pos.getX();
      int var7 = pos.getY();
      int var8 = pos.getZ();
      if (world.isRegionLoaded(new BlockPos(var6 - var5, var7 - var5, var8 - var5), new BlockPos(var6 + var5, var7 + var5, var8 + var5))) {
         for(int var9 = -var4; var9 <= var4; ++var9) {
            for(int var10 = -var4; var10 <= var4; ++var10) {
               for(int var11 = -var4; var11 <= var4; ++var11) {
                  BlockPos var12 = pos.add(var9, var10, var11);
                  BlockState var13 = world.getBlockState(var12);
                  if (var13.getBlock().getMaterial() == Material.LEAVES && !var13.get(CHECK_DECAY)) {
                     world.setBlockState(var12, var13.set(CHECK_DECAY, true), 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         if (state.get(CHECK_DECAY) && state.get(DECAYABLE)) {
            byte var5 = 4;
            int var6 = var5 + 1;
            int var7 = pos.getX();
            int var8 = pos.getY();
            int var9 = pos.getZ();
            byte var10 = 32;
            int var11 = var10 * var10;
            int var12 = var10 / 2;
            if (this.decayRegion == null) {
               this.decayRegion = new int[var10 * var10 * var10];
            }

            if (world.isRegionLoaded(new BlockPos(var7 - var6, var8 - var6, var9 - var6), new BlockPos(var7 + var6, var8 + var6, var9 + var6))) {
               for(int var13 = -var5; var13 <= var5; ++var13) {
                  for(int var14 = -var5; var14 <= var5; ++var14) {
                     for(int var15 = -var5; var15 <= var5; ++var15) {
                        Block var16 = world.getBlockState(new BlockPos(var7 + var13, var8 + var14, var9 + var15)).getBlock();
                        if (var16 != Blocks.LOG && var16 != Blocks.LOG2) {
                           if (var16.getMaterial() == Material.LEAVES) {
                              this.decayRegion[(var13 + var12) * var11 + (var14 + var12) * var10 + var15 + var12] = -2;
                           } else {
                              this.decayRegion[(var13 + var12) * var11 + (var14 + var12) * var10 + var15 + var12] = -1;
                           }
                        } else {
                           this.decayRegion[(var13 + var12) * var11 + (var14 + var12) * var10 + var15 + var12] = 0;
                        }
                     }
                  }
               }

               for(int var17 = 1; var17 <= 4; ++var17) {
                  for(int var19 = -var5; var19 <= var5; ++var19) {
                     for(int var20 = -var5; var20 <= var5; ++var20) {
                        for(int var21 = -var5; var21 <= var5; ++var21) {
                           if (this.decayRegion[(var19 + var12) * var11 + (var20 + var12) * var10 + var21 + var12] == var17 - 1) {
                              if (this.decayRegion[(var19 + var12 - 1) * var11 + (var20 + var12) * var10 + var21 + var12] == -2) {
                                 this.decayRegion[(var19 + var12 - 1) * var11 + (var20 + var12) * var10 + var21 + var12] = var17;
                              }

                              if (this.decayRegion[(var19 + var12 + 1) * var11 + (var20 + var12) * var10 + var21 + var12] == -2) {
                                 this.decayRegion[(var19 + var12 + 1) * var11 + (var20 + var12) * var10 + var21 + var12] = var17;
                              }

                              if (this.decayRegion[(var19 + var12) * var11 + (var20 + var12 - 1) * var10 + var21 + var12] == -2) {
                                 this.decayRegion[(var19 + var12) * var11 + (var20 + var12 - 1) * var10 + var21 + var12] = var17;
                              }

                              if (this.decayRegion[(var19 + var12) * var11 + (var20 + var12 + 1) * var10 + var21 + var12] == -2) {
                                 this.decayRegion[(var19 + var12) * var11 + (var20 + var12 + 1) * var10 + var21 + var12] = var17;
                              }

                              if (this.decayRegion[(var19 + var12) * var11 + (var20 + var12) * var10 + (var21 + var12 - 1)] == -2) {
                                 this.decayRegion[(var19 + var12) * var11 + (var20 + var12) * var10 + (var21 + var12 - 1)] = var17;
                              }

                              if (this.decayRegion[(var19 + var12) * var11 + (var20 + var12) * var10 + var21 + var12 + 1] == -2) {
                                 this.decayRegion[(var19 + var12) * var11 + (var20 + var12) * var10 + var21 + var12 + 1] = var17;
                              }
                           }
                        }
                     }
                  }
               }
            }

            int var18 = this.decayRegion[var12 * var11 + var12 * var10 + var12];
            if (var18 >= 0) {
               world.setBlockState(pos, state.set(CHECK_DECAY, false), 4);
            } else {
               this.breakLeaves(world, pos);
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (world.isRaining(pos.up()) && !World.hasSolidTop(world, pos.down()) && random.nextInt(15) == 1) {
         double var5 = (double)((float)pos.getX() + random.nextFloat());
         double var7 = (double)pos.getY() - 0.05;
         double var9 = (double)((float)pos.getZ() + random.nextFloat());
         world.addParticle(ParticleType.DRIP_WATER, var5, var7, var9, 0.0, 0.0, 0.0);
      }
   }

   private void breakLeaves(World world, BlockPos pos) {
      this.dropItems(world, pos, world.getBlockState(pos), 0);
      world.removeBlock(pos);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return random.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(Blocks.SAPLING);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient) {
         int var6 = this.getSaplingDropChance(state);
         if (fortuneLevel > 0) {
            var6 -= 2 << fortuneLevel;
            if (var6 < 10) {
               var6 = 10;
            }
         }

         if (world.random.nextInt(var6) == 0) {
            Item var7 = this.getDropItem(state, world.random, fortuneLevel);
            this.dropItems(world, pos, new ItemStack(var7, 1, this.getDropItemMetadata(state)));
         }

         var6 = 200;
         if (fortuneLevel > 0) {
            var6 -= 10 << fortuneLevel;
            if (var6 < 40) {
               var6 = 40;
            }
         }

         this.dropAppleWithChance(world, pos, state, var6);
      }
   }

   protected void dropAppleWithChance(World world, BlockPos pos, BlockState state, int chance) {
   }

   protected int getSaplingDropChance(BlockState state) {
      return 20;
   }

   @Override
   public boolean isOpaqueCube() {
      return !this.fancyGraphics;
   }

   @Environment(EnvType.CLIENT)
   public void setFancyGraphics(boolean fancyGraphics) {
      this.renderCutout = fancyGraphics;
      this.fancyGraphics = fancyGraphics;
      this.spriteIndex = fancyGraphics ? 0 : 1;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return this.renderCutout ? BlockLayer.CUTOUT_MIPPED : BlockLayer.SOLID;
   }

   @Override
   public boolean isViewBlocking() {
      return false;
   }

   public abstract PlanksBlock.Variant getVariant(int index);
}
