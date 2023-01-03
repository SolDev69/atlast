package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.LootEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String[] TYPES = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   private static final List LOOT_ENTRIES = Lists.newArrayList(
      new LootEntry[]{
         new LootEntry(Items.SADDLE, 0, 1, 1, 10),
         new LootEntry(Items.IRON_INGOT, 0, 1, 4, 10),
         new LootEntry(Items.BREAD, 0, 1, 1, 10),
         new LootEntry(Items.WHEAT, 0, 1, 4, 10),
         new LootEntry(Items.GUNPOWDER, 0, 1, 4, 10),
         new LootEntry(Items.STRING, 0, 1, 4, 10),
         new LootEntry(Items.BUCKET, 0, 1, 1, 10),
         new LootEntry(Items.GOLDEN_APPLE, 0, 1, 1, 1),
         new LootEntry(Items.REDSTONE, 0, 1, 4, 10),
         new LootEntry(Items.RECORD_13, 0, 1, 1, 4),
         new LootEntry(Items.RECORD_CAT, 0, 1, 1, 4),
         new LootEntry(Items.NAME_TAG, 0, 1, 1, 10),
         new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 2),
         new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 5),
         new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
      }
   );

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      boolean var4 = true;
      int var5 = random.nextInt(2) + 2;
      int var6 = -var5 - 1;
      int var7 = var5 + 1;
      boolean var8 = true;
      boolean var9 = true;
      int var10 = random.nextInt(2) + 2;
      int var11 = -var10 - 1;
      int var12 = var10 + 1;
      int var13 = 0;

      for(int var14 = var6; var14 <= var7; ++var14) {
         for(int var15 = -1; var15 <= 4; ++var15) {
            for(int var16 = var11; var16 <= var12; ++var16) {
               BlockPos var17 = pos.add(var14, var15, var16);
               Material var18 = world.getBlockState(var17).getBlock().getMaterial();
               boolean var19 = var18.isSolid();
               if (var15 == -1 && !var19) {
                  return false;
               }

               if (var15 == 4 && !var19) {
                  return false;
               }

               if ((var14 == var6 || var14 == var7 || var16 == var11 || var16 == var12) && var15 == 0 && world.isAir(var17) && world.isAir(var17.up())) {
                  ++var13;
               }
            }
         }
      }

      if (var13 >= 1 && var13 <= 5) {
         for(int var23 = var6; var23 <= var7; ++var23) {
            for(int var26 = 3; var26 >= -1; --var26) {
               for(int var28 = var11; var28 <= var12; ++var28) {
                  BlockPos var30 = pos.add(var23, var26, var28);
                  if (var23 != var6 && var26 != -1 && var28 != var11 && var23 != var7 && var26 != 4 && var28 != var12) {
                     if (world.getBlockState(var30).getBlock() != Blocks.CHEST) {
                        world.removeBlock(var30);
                     }
                  } else if (var30.getY() >= 0 && !world.getBlockState(var30.down()).getBlock().getMaterial().isSolid()) {
                     world.removeBlock(var30);
                  } else if (world.getBlockState(var30).getBlock().getMaterial().isSolid() && world.getBlockState(var30).getBlock() != Blocks.CHEST) {
                     if (var26 == -1 && random.nextInt(4) != 0) {
                        world.setBlockState(var30, Blocks.MOSSY_COBBLESTONE.defaultState(), 2);
                     } else {
                        world.setBlockState(var30, Blocks.COBBLESTONE.defaultState(), 2);
                     }
                  }
               }
            }
         }

         for(int var24 = 0; var24 < 2; ++var24) {
            for(int var27 = 0; var27 < 3; ++var27) {
               int var29 = pos.getX() + random.nextInt(var5 * 2 + 1) - var5;
               int var31 = pos.getY();
               int var32 = pos.getZ() + random.nextInt(var10 * 2 + 1) - var10;
               BlockPos var33 = new BlockPos(var29, var31, var32);
               if (world.isAir(var33)) {
                  int var20 = 0;

                  for(Direction var22 : Direction.Plane.HORIZONTAL) {
                     if (world.getBlockState(var33.offset(var22)).getBlock().getMaterial().isSolid()) {
                        ++var20;
                     }
                  }

                  if (var20 == 1) {
                     world.setBlockState(var33, Blocks.CHEST.updateFacing(world, var33, Blocks.CHEST.defaultState()), 2);
                     List var34 = LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random));
                     BlockEntity var35 = world.getBlockEntity(var33);
                     if (var35 instanceof ChestBlockEntity) {
                        LootEntry.addLoot(random, var34, (ChestBlockEntity)var35, 8);
                     }
                     break;
                  }
               }
            }
         }

         world.setBlockState(pos, Blocks.MOB_SPAWNER.defaultState(), 2);
         BlockEntity var25 = world.getBlockEntity(pos);
         if (var25 instanceof MobSpawnerBlockEntity) {
            ((MobSpawnerBlockEntity)var25).getSpawner().setType(this.getRandomDungeonType(random));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String getRandomDungeonType(Random random) {
      return TYPES[random.nextInt(TYPES.length)];
   }
}
