package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Dispensable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.IPosition;
import net.minecraft.world.IBlockSource;
import net.minecraft.world.World;

public abstract class DispenseProjectileBehavior extends DispenseItemBehavior {
   @Override
   public ItemStack dispenseItem(IBlockSource source, ItemStack stack) {
      World var3 = source.getWorld();
      IPosition var4 = DispenserBlock.getDispensePos(source);
      Direction var5 = DispenserBlock.getDirection(source.getBlockMetadata());
      Dispensable var6 = this.createProjectile(var3, var4);
      var6.setVelocity((double)var5.getOffsetX(), (double)((float)var5.getOffsetY() + 0.1F), (double)var5.getOffsetZ(), this.getForce(), this.getVariation());
      var3.addEntity((Entity)var6);
      stack.split(1);
      return stack;
   }

   @Override
   protected void playSound(IBlockSource source) {
      source.getWorld().doEvent(1002, source.getPos(), 0);
   }

   protected abstract Dispensable createProjectile(World world, IPosition pos);

   protected float getVariation() {
      return 6.0F;
   }

   protected float getForce() {
      return 1.1F;
   }
}
