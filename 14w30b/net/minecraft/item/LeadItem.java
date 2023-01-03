package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LeadItem extends Item {
   public LeadItem() {
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      Block var9 = world.getBlockState(pos).getBlock();
      if (var9 instanceof FenceBlock) {
         if (world.isClient) {
            return true;
         } else {
            attachLead(player, world, pos);
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean attachLead(PlayerEntity player, World world, BlockPos x) {
      LeadKnotEntity var3 = LeadKnotEntity.getOrCreate(world, x);
      boolean var4 = false;
      double var5 = 7.0;
      int var7 = x.getX();
      int var8 = x.getY();
      int var9 = x.getZ();

      for(MobEntity var12 : world.getEntities(
         MobEntity.class, new Box((double)var7 - var5, (double)var8 - var5, (double)var9 - var5, (double)var7 + var5, (double)var8 + var5, (double)var9 + var5)
      )) {
         if (var12.isLeashed() && var12.getHoldingEntity() == player) {
            if (var3 == null) {
               var3 = LeadKnotEntity.attatch(world, x);
            }

            var12.attachLeash(var3, true);
            var4 = true;
         }
      }

      return var4;
   }
}
