package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SignBlock extends BlockWithBlockEntity {
   protected SignBlock() {
      super(Material.WOOD);
      float var1 = 0.25F;
      float var2 = 1.0F;
      this.setShape(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Box getOutlineShape(World world, BlockPos pos) {
      this.updateShape(world, pos);
      return super.getOutlineShape(world, pos);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new SignBlockEntity();
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.SIGN;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Items.SIGN;
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         return var9 instanceof SignBlockEntity ? ((SignBlockEntity)var9).onUse(player) : false;
      }
   }
}
