package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DyeItem extends Item {
   public static final int[] COLORS = new int[]{
      1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
   };

   public DyeItem() {
      this.setStackable(true);
      this.setMaxDamage(0);
      this.setItemGroup(ItemGroup.MATERIALS);
   }

   @Override
   public String getTranslationKey(ItemStack stack) {
      int var2 = stack.getMetadata();
      return super.getTranslationKey() + "." + DyeColor.byMetadata(var2).getName();
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (!player.canUseItem(pos.offset(face), face, stack)) {
         return false;
      } else {
         DyeColor var9 = DyeColor.byMetadata(stack.getMetadata());
         if (var9 == DyeColor.WHITE) {
            if (fertilize(stack, world, pos)) {
               if (!world.isClient) {
                  world.doEvent(2005, pos, 0);
               }

               return true;
            }
         } else if (var9 == DyeColor.BROWN) {
            BlockState var10 = world.getBlockState(pos);
            Block var11 = var10.getBlock();
            if (var11 == Blocks.LOG && var10.get(PlanksBlock.VARIANT) == PlanksBlock.Variant.JUNGLE) {
               if (face == Direction.DOWN) {
                  return false;
               }

               if (face == Direction.UP) {
                  return false;
               }

               pos = pos.offset(face);
               if (world.isAir(pos)) {
                  BlockState var12 = Blocks.COCOA.getPlacementState(world, pos, face, dx, dy, dz, 0, player);
                  world.setBlockState(pos, var12, 2);
                  if (!player.abilities.creativeMode) {
                     --stack.size;
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   public static boolean fertilize(ItemStack stack, World world, BlockPos x) {
      BlockState var3 = world.getBlockState(x);
      if (var3.getBlock() instanceof Fertilizable) {
         Fertilizable var4 = (Fertilizable)var3.getBlock();
         if (var4.canGrow(world, x, var3, world.isClient)) {
            if (!world.isClient) {
               if (var4.canBeFertilized(world, world.random, x, var3)) {
                  var4.grow(world, world.random, x, var3);
               }

               --stack.size;
            }

            return true;
         }
      }

      return false;
   }

   @Environment(EnvType.CLIENT)
   public static void spawnParticles(World world, BlockPos x, int y) {
      if (y == 0) {
         y = 15;
      }

      Block var3 = world.getBlockState(x).getBlock();
      if (var3.getMaterial() != Material.AIR) {
         var3.updateShape(world, x);

         for(int var4 = 0; var4 < y; ++var4) {
            double var5 = random.nextGaussian() * 0.02;
            double var7 = random.nextGaussian() * 0.02;
            double var9 = random.nextGaussian() * 0.02;
            world.addParticle(
               ParticleType.VILLAGER_HAPPY,
               (double)((float)x.getX() + random.nextFloat()),
               (double)x.getY() + (double)random.nextFloat() * var3.getMaxY(),
               (double)((float)x.getZ() + random.nextFloat()),
               var5,
               var7,
               var9
            );
         }
      }
   }

   @Override
   public boolean canInteract(ItemStack stack, PlayerEntity player, LivingEntity entity) {
      if (entity instanceof SheepEntity) {
         SheepEntity var4 = (SheepEntity)entity;
         DyeColor var5 = DyeColor.byMetadata(stack.getMetadata());
         if (!var4.isSheared() && var4.getColorId() != var5) {
            var4.setColor(var5);
            --stack.size;
         }

         return true;
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      for(int var4 = 0; var4 < 16; ++var4) {
         list.add(new ItemStack(item, 1, var4));
      }
   }
}
