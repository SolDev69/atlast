package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SoulSandBlock extends Block {
   public SoulSandBlock() {
      super(Material.SAND);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      float var4 = 0.125F;
      return new Box(
         (double)pos.getX(),
         (double)pos.getY(),
         (double)pos.getZ(),
         (double)(pos.getX() + 1),
         (double)((float)(pos.getY() + 1) - var4),
         (double)(pos.getZ() + 1)
      );
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      entity.velocityX *= 0.4;
      entity.velocityZ *= 0.4;
   }
}
