package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SlimeBlock extends TransparentBlock {
   public SlimeBlock() {
      super(Material.CLAY, false);
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.slipperiness = 0.8F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   @Override
   public void onFallenOn(World world, BlockPos pos, Entity entity, float fallDistance) {
      if (entity.isSneaking()) {
         super.onFallenOn(world, pos, entity, fallDistance);
      } else {
         entity.applyFallDamage(fallDistance, 0.0F);
      }
   }

   @Override
   public void beforeCollision(World world, Entity pos) {
      if (pos.isSneaking()) {
         super.beforeCollision(world, pos);
      } else if (pos.velocityY < 0.0) {
         pos.velocityY = -pos.velocityY;
      }
   }

   @Override
   public void onSteppedOn(World world, BlockPos pos, Entity entity) {
      if (Math.abs(entity.velocityY) < 0.1 && !entity.isSneaking()) {
         double var4 = 0.4 + Math.abs(entity.velocityY) * 0.2;
         entity.velocityX *= var4;
         entity.velocityZ *= var4;
      }

      super.onSteppedOn(world, pos, entity);
   }
}
