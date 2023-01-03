package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BucketItem extends Item {
   private Block liquid;

   public BucketItem(Block liquid) {
      this.maxStackSize = 1;
      this.liquid = liquid;
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      boolean var4 = this.liquid == Blocks.AIR;
      HitResult var5 = this.getUseTarget(world, player, var4);
      if (var5 == null) {
         return stack;
      } else {
         if (var5.type == HitResult.Type.BLOCK) {
            BlockPos var6 = var5.getBlockPos();
            if (!world.canModify(player, var6)) {
               return stack;
            }

            if (var4) {
               if (!player.canUseItem(var6.offset(var5.face), var5.face, stack)) {
                  return stack;
               }

               BlockState var7 = world.getBlockState(var6);
               Material var8 = var7.getBlock().getMaterial();
               if (var8 == Material.WATER && var7.get(LiquidBlock.LEVEL) == 0) {
                  world.removeBlock(var6);
                  player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
                  return this.fill(stack, player, Items.WATER_BUCKET);
               }

               if (var8 == Material.LAVA && var7.get(LiquidBlock.LEVEL) == 0) {
                  world.removeBlock(var6);
                  player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
                  return this.fill(stack, player, Items.LAVA_BUCKET);
               }
            } else {
               if (this.liquid == Blocks.AIR) {
                  return new ItemStack(Items.BUCKET);
               }

               BlockPos var9 = var6.offset(var5.face);
               if (!player.canUseItem(var9, var5.face, stack)) {
                  return stack;
               }

               if (this.place(world, var9) && !player.abilities.creativeMode) {
                  player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
                  return new ItemStack(Items.BUCKET);
               }
            }
         }

         return stack;
      }
   }

   private ItemStack fill(ItemStack stack, PlayerEntity player, Item item) {
      if (player.abilities.creativeMode) {
         return stack;
      } else if (--stack.size <= 0) {
         return new ItemStack(item);
      } else {
         if (!player.inventory.insertStack(new ItemStack(item))) {
            player.dropItem(new ItemStack(item, 1, 0), false);
         }

         return stack;
      }
   }

   public boolean place(World world, BlockPos x) {
      if (this.liquid == Blocks.AIR) {
         return false;
      } else {
         Material var3 = world.getBlockState(x).getBlock().getMaterial();
         boolean var4 = !var3.isSolid();
         if (!world.isAir(x) && !var4) {
            return false;
         } else {
            if (world.dimension.yeetsWater() && this.liquid == Blocks.FLOWING_WATER) {
               int var5 = x.getX();
               int var6 = x.getY();
               int var7 = x.getZ();
               world.playSound(
                  (double)((float)var5 + 0.5F),
                  (double)((float)var6 + 0.5F),
                  (double)((float)var7 + 0.5F),
                  "random.fizz",
                  0.5F,
                  2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
               );

               for(int var8 = 0; var8 < 8; ++var8) {
                  world.addParticle(
                     ParticleType.SMOKE_LARGE, (double)var5 + Math.random(), (double)var6 + Math.random(), (double)var7 + Math.random(), 0.0, 0.0, 0.0
                  );
               }
            } else {
               if (!world.isClient && var4 && !var3.isLiquid()) {
                  world.breakBlock(x, true);
               }

               world.setBlockState(x, this.liquid.defaultState(), 3);
            }

            return true;
         }
      }
   }
}
