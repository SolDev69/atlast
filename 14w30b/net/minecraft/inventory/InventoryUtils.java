package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryUtils {
   private static final Random RANDOM = new Random();

   public static void dropContents(World world, BlockPos pos, Inventory inventory) {
      dropContents(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
   }

   public static void dropContents(World world, Entity owner, Inventory inventory) {
      dropContents(world, owner.x, owner.y, owner.z, inventory);
   }

   private static void dropContents(World world, double x, double y, double z, Inventory inventory) {
      for(int var8 = 0; var8 < inventory.getSize(); ++var8) {
         ItemStack var9 = inventory.getStack(var8);
         if (var9 != null) {
            dropStack(world, x, y, z, var9);
         }
      }
   }

   private static void dropStack(World world, double x, double y, double z, ItemStack stack) {
      float var8 = RANDOM.nextFloat() * 0.8F + 0.1F;
      float var9 = RANDOM.nextFloat() * 0.8F + 0.1F;
      float var10 = RANDOM.nextFloat() * 0.8F + 0.1F;

      while(stack.size > 0) {
         int var11 = RANDOM.nextInt(21) + 10;
         if (var11 > stack.size) {
            var11 = stack.size;
         }

         stack.size -= var11;
         ItemEntity var12 = new ItemEntity(
            world, x + (double)var8, y + (double)var9, z + (double)var10, new ItemStack(stack.getItem(), var11, stack.getMetadata())
         );
         if (stack.hasNbt()) {
            var12.getItemStack().setNbt((NbtCompound)stack.getNbt().copy());
         }

         float var13 = 0.05F;
         var12.velocityX = RANDOM.nextGaussian() * (double)var13;
         var12.velocityY = RANDOM.nextGaussian() * (double)var13 + 0.2F;
         var12.velocityZ = RANDOM.nextGaussian() * (double)var13;
         world.addEntity(var12);
      }
   }
}
