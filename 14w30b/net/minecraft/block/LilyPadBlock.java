package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LilyPadBlock extends PlantBlock {
   protected LilyPadBlock() {
      float var1 = 0.5F;
      float var2 = 0.015625F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      if (entity == null || !(entity instanceof BoatEntity)) {
         super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      }
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return new Box(
         (double)pos.getX() + this.minX,
         (double)pos.getY() + this.minY,
         (double)pos.getZ() + this.minZ,
         (double)pos.getX() + this.maxX,
         (double)pos.getY() + this.maxY,
         (double)pos.getZ() + this.maxZ
      );
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor() {
      return 7455580;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      return 7455580;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return 2129968;
   }

   @Override
   protected boolean canPlantOn(Block block) {
      return block == Blocks.WATER;
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         BlockState var4 = world.getBlockState(pos.down());
         return var4.getBlock().getMaterial() == Material.WATER && var4.get(LiquidBlock.LEVEL) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return 0;
   }
}
