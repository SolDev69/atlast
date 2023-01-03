package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.IPosition;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;

public class DispenseItemBehavior implements DispenseBehavior {
   @Override
   public final ItemStack dispense(IBlockSource source, ItemStack stack) {
      ItemStack var3 = this.dispenseItem(source, stack);
      this.playSound(source);
      this.doWorldEvent(source, DispenserBlock.getDirection(source.getBlockMetadata()));
      return var3;
   }

   protected ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
      Direction var3 = DispenserBlock.getDirection(source.getBlockMetadata());
      IPosition var4 = DispenserBlock.getDispensePos(source);
      ItemStack var5 = stack.split(1);
      spawnItem(source.getWorld(), var5, 6, var3, var4);
      return stack;
   }

   public static void spawnItem(World world, ItemStack stack, int offset, Direction facing, IPosition pos) {
      double var5 = pos.getX();
      double var7 = pos.getY() - (facing != Direction.UP ? 0.3 : 0.0);
      double var9 = pos.getZ();
      ItemEntity var11 = new ItemEntity(world, var5, var7, var9, stack);
      double var12 = world.random.nextDouble() * 0.1 + 0.2;
      var11.velocityX = (double)facing.getOffsetX() * var12;
      var11.velocityY = 0.2F;
      var11.velocityZ = (double)facing.getOffsetZ() * var12;
      var11.velocityX += world.random.nextGaussian() * 0.0075F * (double)offset;
      var11.velocityY += world.random.nextGaussian() * 0.0075F * (double)offset;
      var11.velocityZ += world.random.nextGaussian() * 0.0075F * (double)offset;
      world.addEntity(var11);
   }

   protected void playSound(IBlockSource source) {
      source.getWorld().doEvent(1000, source.getPos(), 0);
   }

   protected void doWorldEvent(IBlockSource source, Direction facing) {
      source.getWorld().doEvent(2000, source.getPos(), this.toInt(facing));
   }

   private int toInt(Direction dir) {
      return dir.getOffsetX() + 1 + (dir.getOffsetZ() + 1) * 3;
   }
}
