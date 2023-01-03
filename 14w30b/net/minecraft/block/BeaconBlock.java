package net.minecraft.block;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BeaconBlock extends BlockWithBlockEntity {
   public BeaconBlock() {
      super(Material.GLASS);
      this.setStrength(3.0F);
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new BeaconBlockEntity();
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof BeaconBlockEntity) {
            player.openInventoryMenu((BeaconBlockEntity)var9);
         }

         return true;
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      super.onPlaced(world, pos, state, entity, stack);
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof BeaconBlockEntity) {
            ((BeaconBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
