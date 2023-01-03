package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LilyPadItem extends GrassBlockItem {
   public LilyPadItem(Block c_68zcrzyxg) {
      super(c_68zcrzyxg, false);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      HitResult var4 = this.getUseTarget(world, player, true);
      if (var4 == null) {
         return stack;
      } else {
         if (var4.type == HitResult.Type.BLOCK) {
            BlockPos var5 = var4.getBlockPos();
            if (!world.canModify(player, var5)) {
               return stack;
            }

            if (!player.canUseItem(var5.offset(var4.face), var4.face, stack)) {
               return stack;
            }

            BlockPos var6 = var5.up();
            BlockState var7 = world.getBlockState(var5);
            if (var7.getBlock().getMaterial() == Material.WATER && var7.get(LiquidBlock.LEVEL) == 0 && world.isAir(var6)) {
               world.setBlockState(var6, Blocks.LILY_PAD.defaultState());
               if (!player.abilities.creativeMode) {
                  --stack.size;
               }

               player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
            }
         }

         return stack;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      return Blocks.LILY_PAD.getColor(stack.getMetadata());
   }
}
