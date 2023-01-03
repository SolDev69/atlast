package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreBlock extends Block {
   public OreBlock() {
      super(Material.STONE);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      if (this == Blocks.COAL_ORE) {
         return Items.COAL;
      } else if (this == Blocks.DIAMOND_ORE) {
         return Items.DIAMOND;
      } else if (this == Blocks.LAPIS_ORE) {
         return Items.DYE;
      } else if (this == Blocks.EMERALD_ORE) {
         return Items.EMERALD;
      } else {
         return this == Blocks.QUARTZ_ORE ? Items.QUARTZ : Item.byBlock(this);
      }
   }

   @Override
   public int getBaseDropCount(Random random) {
      return this == Blocks.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
   }

   @Override
   public int getDropCount(int fortuneLevel, Random random) {
      if (fortuneLevel > 0 && Item.byBlock(this) != this.getDropItem((BlockState)this.stateDefinition().all().iterator().next(), random, fortuneLevel)) {
         int var3 = random.nextInt(fortuneLevel + 2) - 1;
         if (var3 < 0) {
            var3 = 0;
         }

         return this.getBaseDropCount(random) * (var3 + 1);
      } else {
         return this.getBaseDropCount(random);
      }
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      super.dropItems(world, pos, state, luck, fortuneLevel);
      if (this.getDropItem(state, world.random, fortuneLevel) != Item.byBlock(this)) {
         int var6 = 0;
         if (this == Blocks.COAL_ORE) {
            var6 = MathHelper.nextInt(world.random, 0, 2);
         } else if (this == Blocks.DIAMOND_ORE) {
            var6 = MathHelper.nextInt(world.random, 3, 7);
         } else if (this == Blocks.EMERALD_ORE) {
            var6 = MathHelper.nextInt(world.random, 3, 7);
         } else if (this == Blocks.LAPIS_ORE) {
            var6 = MathHelper.nextInt(world.random, 2, 5);
         } else if (this == Blocks.QUARTZ_ORE) {
            var6 = MathHelper.nextInt(world.random, 2, 5);
         }

         this.dropXp(world, pos, var6);
      }
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return this == Blocks.LAPIS_ORE ? DyeColor.BLUE.getMetadata() : 0;
   }
}
