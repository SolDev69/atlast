package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CauldronBlock extends Block {
   public static final IntegerProperty LEVEL = IntegerProperty.of("level", 0, 3);

   public CauldronBlock() {
      super(Material.IRON);
      this.setDefaultState(this.stateDefinition.any().set(LEVEL, 0));
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      float var7 = 0.125F;
      this.setShape(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setBlockItemBounds();
   }

   @Override
   public void setBlockItemBounds() {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      int var5 = state.get(LEVEL);
      float var6 = (float)pos.getY() + (6.0F + (float)(3 * var5)) / 16.0F;
      if (!world.isClient && entity.isOnFire() && var5 > 0 && entity.getBoundingBox().minY <= (double)var6) {
         entity.extinguish();
         this.setLevel(world, pos, state, var5 - 1);
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         ItemStack var9 = player.inventory.getMainHandStack();
         if (var9 == null) {
            return true;
         } else {
            int var10 = state.get(LEVEL);
            Item var11 = var9.getItem();
            if (var11 == Items.WATER_BUCKET) {
               if (var10 < 3) {
                  if (!player.abilities.creativeMode) {
                     player.inventory.setStack(player.inventory.selectedSlot, new ItemStack(Items.BUCKET));
                  }

                  this.setLevel(world, pos, state, 3);
               }

               return true;
            } else if (var11 == Items.GLASS_BOTTLE) {
               if (var10 > 0) {
                  if (!player.abilities.creativeMode) {
                     ItemStack var14 = new ItemStack(Items.POTION, 1, 0);
                     if (!player.inventory.insertStack(var14)) {
                        world.addEntity(new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 1.5, (double)pos.getZ() + 0.5, var14));
                     } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).setMenu(player.playerMenu);
                     }

                     --var9.size;
                     if (var9.size <= 0) {
                        player.inventory.setStack(player.inventory.selectedSlot, null);
                     }
                  }

                  this.setLevel(world, pos, state, var10 - 1);
               }

               return true;
            } else {
               if (var10 > 0 && var11 instanceof ArmorItem) {
                  ArmorItem var12 = (ArmorItem)var11;
                  if (var12.getMaterial() == ArmorItem.Material.CLOTH && var12.hasColor(var9)) {
                     var12.removeColor(var9);
                     this.setLevel(world, pos, state, var10 - 1);
                     return true;
                  }
               }

               if (var10 > 0 && var11 instanceof BannerItem && BannerBlockEntity.getPatternCount(var9) > 0) {
                  ItemStack var13 = var9.copy();
                  var13.size = 1;
                  BannerBlockEntity.removeLastPattern(var13);
                  if (var9.size <= 1 && !player.abilities.creativeMode) {
                     player.inventory.setStack(player.inventory.selectedSlot, var13);
                  } else {
                     if (!player.inventory.insertStack(var13)) {
                        world.addEntity(new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 1.5, (double)pos.getZ() + 0.5, var13));
                     } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).setMenu(player.playerMenu);
                     }

                     if (!player.abilities.creativeMode) {
                        --var9.size;
                     }
                  }

                  if (!player.abilities.creativeMode) {
                     this.setLevel(world, pos, state, var10 - 1);
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public void setLevel(World world, BlockPos pos, BlockState state, int level) {
      world.setBlockState(pos, state.set(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
      world.updateComparators(pos, this);
   }

   @Override
   public void randomPrecipitationTick(World world, BlockPos pos) {
      if (world.random.nextInt(20) == 1) {
         BlockState var3 = world.getBlockState(pos);
         if (var3.get(LEVEL) < 3) {
            world.setBlockState(pos, var3.next(LEVEL), 2);
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.CAULDRON;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.CAULDRON;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return world.getBlockState(pos).get(LEVEL);
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
}
