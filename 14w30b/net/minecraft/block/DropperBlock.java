package net.minecraft.block;

import net.minecraft.block.dispenser.DispenseBehavior;
import net.minecraft.block.dispenser.DispenseItemBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockSource;
import net.minecraft.world.World;

public class DropperBlock extends DispenserBlock {
   private final DispenseBehavior dispenseBehavior = new DispenseItemBehavior();

   @Override
   protected DispenseBehavior getDispenseBehavior(ItemStack stack) {
      return this.dispenseBehavior;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new DropperBlockEntity();
   }

   @Override
   protected void dispense(World world, BlockPos pos) {
      BlockSource var3 = new BlockSource(world, pos);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getBlockEntity();
      if (var4 != null) {
         int var5 = var4.pickNonEmptySlot();
         if (var5 < 0) {
            world.doEvent(1001, pos, 0);
         } else {
            ItemStack var6 = var4.getStack(var5);
            if (var6 != null) {
               Direction var7 = (Direction)world.getBlockState(pos).get(FACING);
               BlockPos var8 = pos.offset(var7);
               Inventory var9 = HopperBlockEntity.getInventoryAt(world, (double)var8.getX(), (double)var8.getY(), (double)var8.getZ());
               ItemStack var10;
               if (var9 == null) {
                  var10 = this.dispenseBehavior.dispense(var3, var6);
                  if (var10 != null && var10.size == 0) {
                     var10 = null;
                  }
               } else {
                  var10 = HopperBlockEntity.pushItems(var9, var6.copy().split(1), var7.getOpposite());
                  if (var10 == null) {
                     var10 = var6.copy();
                     if (--var10.size == 0) {
                        var10 = null;
                     }
                  } else {
                     var10 = var6.copy();
                  }
               }

               var4.setStack(var5, var10);
            }
         }
      }
   }
}
