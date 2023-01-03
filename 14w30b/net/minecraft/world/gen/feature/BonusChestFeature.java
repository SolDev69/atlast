package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.LootEntry;

public class BonusChestFeature extends Feature {
   private final List lootEntries;
   private final int amountOfLoot;

   public BonusChestFeature(List lootEntries, int amountOfLoot) {
      this.lootEntries = lootEntries;
      this.amountOfLoot = amountOfLoot;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      Block var4;
      while(((var4 = world.getBlockState(pos).getBlock()).getMaterial() == Material.AIR || var4.getMaterial() == Material.LEAVES) && pos.getY() > 1) {
         pos = pos.down();
      }

      if (pos.getY() < 1) {
         return false;
      } else {
         pos = pos.up();

         for(int var5 = 0; var5 < 4; ++var5) {
            BlockPos var6 = pos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
            if (world.isAir(var6) && World.hasSolidTop(world, var6.down())) {
               world.setBlockState(var6, Blocks.CHEST.defaultState(), 2);
               BlockEntity var7 = world.getBlockEntity(var6);
               if (var7 instanceof ChestBlockEntity) {
                  LootEntry.addLoot(random, this.lootEntries, (ChestBlockEntity)var7, this.amountOfLoot);
               }

               BlockPos var8 = var6.east();
               BlockPos var9 = var6.west();
               BlockPos var10 = var6.north();
               BlockPos var11 = var6.south();
               if (world.isAir(var9) && World.hasSolidTop(world, var9.down())) {
                  world.setBlockState(var9, Blocks.TORCH.defaultState(), 2);
               }

               if (world.isAir(var8) && World.hasSolidTop(world, var8.down())) {
                  world.setBlockState(var8, Blocks.TORCH.defaultState(), 2);
               }

               if (world.isAir(var10) && World.hasSolidTop(world, var10.down())) {
                  world.setBlockState(var10, Blocks.TORCH.defaultState(), 2);
               }

               if (world.isAir(var11) && World.hasSolidTop(world, var11.down())) {
                  world.setBlockState(var11, Blocks.TORCH.defaultState(), 2);
               }

               return true;
            }
         }

         return false;
      }
   }
}
