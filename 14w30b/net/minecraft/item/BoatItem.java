package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
   public BoatItem() {
      this.maxStackSize = 1;
      this.setItemGroup(ItemGroup.TRANSPORTATION);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      float var4 = 1.0F;
      float var5 = player.prevPitch + (player.pitch - player.prevPitch) * var4;
      float var6 = player.prevYaw + (player.yaw - player.prevYaw) * var4;
      double var7 = player.prevX + (player.x - player.prevX) * (double)var4;
      double var9 = player.prevY + (player.y - player.prevY) * (double)var4 + (double)player.getEyeHeight();
      double var11 = player.prevZ + (player.z - player.prevZ) * (double)var4;
      Vec3d var13 = new Vec3d(var7, var9, var11);
      float var14 = MathHelper.cos(-var6 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var15 = MathHelper.sin(-var6 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var16 = -MathHelper.cos(-var5 * (float) (Math.PI / 180.0));
      float var17 = MathHelper.sin(-var5 * (float) (Math.PI / 180.0));
      float var18 = var15 * var16;
      float var20 = var14 * var16;
      double var21 = 5.0;
      Vec3d var23 = var13.add((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
      HitResult var24 = world.rayTrace(var13, var23, true);
      if (var24 == null) {
         return stack;
      } else {
         Vec3d var25 = player.m_01qqqsfds(var4);
         boolean var26 = false;
         float var27 = 1.0F;
         List var28 = world.getEntities(
            player, player.getBoundingBox().grow(var25.x * var21, var25.y * var21, var25.z * var21).expand((double)var27, (double)var27, (double)var27)
         );

         for(int var29 = 0; var29 < var28.size(); ++var29) {
            Entity var30 = (Entity)var28.get(var29);
            if (var30.hasCollision()) {
               float var31 = var30.getExtraHitboxSize();
               Box var32 = var30.getBoundingBox().expand((double)var31, (double)var31, (double)var31);
               if (var32.contains(var13)) {
                  var26 = true;
               }
            }
         }

         if (var26) {
            return stack;
         } else {
            if (var24.type == HitResult.Type.BLOCK) {
               BlockPos var33 = var24.getBlockPos();
               if (world.getBlockState(var33).getBlock() == Blocks.SNOW_LAYER) {
                  var33 = var33.down();
               }

               BoatEntity var34 = new BoatEntity(
                  world, (double)((float)var33.getX() + 0.5F), (double)((float)var33.getY() + 1.0F), (double)((float)var33.getZ() + 0.5F)
               );
               var34.yaw = (float)(((MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + 0.5) & 3) - 1) * 90);
               if (!world.getCollisions(var34, var34.getBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
                  return stack;
               }

               if (!world.isClient) {
                  world.addEntity(var34);
               }

               if (!player.abilities.creativeMode) {
                  --stack.size;
               }

               player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
            }

            return stack;
         }
      }
   }
}
