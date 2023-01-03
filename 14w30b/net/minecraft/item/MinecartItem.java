package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.dispenser.DispenseBehavior;
import net.minecraft.block.dispenser.DispenseItemBehavior;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;

public class MinecartItem extends Item {
   private static final DispenseBehavior DISPENSE_BEHAVIOR = new DispenseItemBehavior() {
      private final DispenseItemBehavior fallback = new DispenseItemBehavior();

      @Override
      public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
         Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
         World var4 = source.getWorld();
         double var5 = source.getX() + (double)((float)var3.getOffsetX() * 1.125F);
         double var7 = Math.floor(source.getY()) + (double)var3.getOffsetY();
         double var9 = source.getZ() + (double)((float)var3.getOffsetZ() * 1.125F);
         BlockPos var11 = source.getPos().offset(var3);
         BlockState var12 = var4.getBlockState(var11);
         AbstractRailBlock.Shape var13 = var12.getBlock() instanceof AbstractRailBlock
            ? (AbstractRailBlock.Shape)var12.get(((AbstractRailBlock)var12.getBlock()).getShapeProperty())
            : AbstractRailBlock.Shape.NORTH_SOUTH;
         double var14;
         if (AbstractRailBlock.isRail(var12)) {
            if (var13.isAscending()) {
               var14 = 0.6;
            } else {
               var14 = 0.1;
            }
         } else {
            if (var12.getBlock().getMaterial() != Material.AIR || !AbstractRailBlock.isRail(var4.getBlockState(var11.down()))) {
               return this.fallback.dispense(source, stack);
            }

            BlockState var16 = var4.getBlockState(var11.down());
            AbstractRailBlock.Shape var17 = var16.getBlock() instanceof AbstractRailBlock
               ? (AbstractRailBlock.Shape)var16.get(((RailBlock)var16.getBlock()).getShapeProperty())
               : AbstractRailBlock.Shape.NORTH_SOUTH;
            if (var3 != Direction.DOWN && var17.isAscending()) {
               var14 = -0.4;
            } else {
               var14 = -0.9;
            }
         }

         MinecartEntity var18 = MinecartEntity.create(var4, var5, var7 + var14, var9, ((MinecartItem)stack.getItem()).variant);
         if (stack.hasCustomHoverName()) {
            var18.setCustomName(stack.getHoverName());
         }

         var4.addEntity(var18);
         stack.split(1);
         return stack;
      }

      @Override
      protected void playSound(IBlockSource source) {
         source.getWorld().doEvent(1000, source.getPos(), 0);
      }
   };
   private final MinecartEntity.Type variant;

   public MinecartItem(MinecartEntity.Type variant) {
      this.maxStackSize = 1;
      this.variant = variant;
      this.setItemGroup(ItemGroup.TRANSPORTATION);
      DispenserBlock.BEHAVIORS.put(this, DISPENSE_BEHAVIOR);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      BlockState var9 = world.getBlockState(pos);
      if (AbstractRailBlock.isRail(var9)) {
         if (!world.isClient) {
            AbstractRailBlock.Shape var10 = var9.getBlock() instanceof AbstractRailBlock
               ? (AbstractRailBlock.Shape)var9.get(((AbstractRailBlock)var9.getBlock()).getShapeProperty())
               : AbstractRailBlock.Shape.NORTH_SOUTH;
            float var11 = 0.0F;
            if (var10.isAscending()) {
               var11 = 0.5F;
            }

            MinecartEntity var12 = MinecartEntity.create(
               world, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.0625F + var11), (double)((float)pos.getZ() + 0.5F), this.variant
            );
            if (stack.hasCustomHoverName()) {
               var12.setCustomName(stack.getHoverName());
            }

            world.addEntity(var12);
         }

         --stack.size;
         return true;
      } else {
         return false;
      }
   }
}
