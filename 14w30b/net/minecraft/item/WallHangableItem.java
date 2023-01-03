package net.minecraft.item;

import net.minecraft.entity.decoration.DecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WallHangableItem extends Item {
   private final Class decorationClass;

   public WallHangableItem(Class decorationClass) {
      this.decorationClass = decorationClass;
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (face == Direction.DOWN) {
         return false;
      } else if (face == Direction.UP) {
         return false;
      } else {
         BlockPos var9 = pos.offset(face);
         if (!player.canUseItem(var9, face, stack)) {
            return false;
         } else {
            DecorationEntity var10 = this.hang(world, var9, face);
            if (var10 != null && var10.isPosValid()) {
               if (!world.isClient) {
                  world.addEntity(var10);
               }

               --stack.size;
            }

            return true;
         }
      }
   }

   private DecorationEntity hang(World world, BlockPos x, Direction y) {
      if (this.decorationClass == PaintingEntity.class) {
         return new PaintingEntity(world, x, y);
      } else {
         return this.decorationClass == ItemFrameEntity.class ? new ItemFrameEntity(world, x, y) : null;
      }
   }
}
