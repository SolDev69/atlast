package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnderEyeItem extends Item {
   public EnderEyeItem() {
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      BlockState var9 = world.getBlockState(pos);
      if (!player.canUseItem(pos.offset(face), face, stack) || var9.getBlock() != Blocks.END_PORTAL_FRAME || var9.get(EndPortalFrameBlock.EYE)) {
         return false;
      } else if (world.isClient) {
         return true;
      } else {
         world.setBlockState(pos, var9.set(EndPortalFrameBlock.EYE, true), 2);
         world.updateComparators(pos, Blocks.END_PORTAL_FRAME);
         --stack.size;

         for(int var10 = 0; var10 < 16; ++var10) {
            double var11 = (double)((float)pos.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
            double var13 = (double)((float)pos.getY() + 0.8125F);
            double var15 = (double)((float)pos.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
            double var17 = 0.0;
            double var19 = 0.0;
            double var21 = 0.0;
            world.addParticle(ParticleType.SMOKE_NORMAL, var11, var13, var15, var17, var19, var21);
         }

         Direction var23 = (Direction)var9.get(EndPortalFrameBlock.FACING);
         int var24 = 0;
         int var12 = 0;
         boolean var25 = false;
         boolean var14 = true;
         Direction var26 = var23.clockwiseY();

         for(int var16 = -2; var16 <= 2; ++var16) {
            BlockPos var30 = pos.offset(var26, var16);
            BlockState var18 = world.getBlockState(var30);
            if (var18.getBlock() == Blocks.END_PORTAL_FRAME) {
               if (!var18.get(EndPortalFrameBlock.EYE)) {
                  var14 = false;
                  break;
               }

               var12 = var16;
               if (!var25) {
                  var24 = var16;
                  var25 = true;
               }
            }
         }

         if (var14 && var12 == var24 + 2) {
            BlockPos var27 = pos.offset(var23, 4);

            for(int var31 = var24; var31 <= var12; ++var31) {
               BlockPos var34 = var27.offset(var26, var31);
               BlockState var37 = world.getBlockState(var34);
               if (var37.getBlock() != Blocks.END_PORTAL_FRAME || !var37.get(EndPortalFrameBlock.EYE)) {
                  var14 = false;
                  break;
               }
            }

            for(int var32 = var24 - 1; var32 <= var12 + 1; var32 += 4) {
               var27 = pos.offset(var26, var32);

               for(int var35 = 1; var35 <= 3; ++var35) {
                  BlockPos var38 = var27.offset(var23, var35);
                  BlockState var20 = world.getBlockState(var38);
                  if (var20.getBlock() != Blocks.END_PORTAL_FRAME || !var20.get(EndPortalFrameBlock.EYE)) {
                     var14 = false;
                     break;
                  }
               }
            }

            if (var14) {
               for(int var33 = var24; var33 <= var12; ++var33) {
                  var27 = pos.offset(var26, var33);

                  for(int var36 = 1; var36 <= 3; ++var36) {
                     BlockPos var39 = var27.offset(var23, var36);
                     world.setBlockState(var39, Blocks.END_PORTAL.defaultState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      HitResult var4 = this.getUseTarget(world, player, false);
      if (var4 != null && var4.type == HitResult.Type.BLOCK && world.getBlockState(var4.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
         return stack;
      } else {
         if (!world.isClient) {
            BlockPos var5 = world.findNearestStructure("Stronghold", new BlockPos(player));
            if (var5 != null) {
               EnderEyeEntity var6 = new EnderEyeEntity(world, player.x, player.y, player.z);
               var6.setTarget(var5);
               world.addEntity(var6);
               world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               world.doEvent(null, 1002, new BlockPos(player), 0);
               if (!player.abilities.creativeMode) {
                  --stack.size;
               }

               player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
            }
         }

         return stack;
      }
   }
}
