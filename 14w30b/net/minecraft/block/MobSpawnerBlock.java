package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MobSpawnerBlock extends BlockWithBlockEntity {
   protected MobSpawnerBlock() {
      super(Material.STONE);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new MobSpawnerBlockEntity();
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return null;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      super.dropItems(world, pos, state, luck, fortuneLevel);
      int var6 = 15 + world.random.nextInt(15) + world.random.nextInt(15);
      this.dropXp(world, pos, var6);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(0);
   }
}
