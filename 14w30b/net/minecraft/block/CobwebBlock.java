package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CobwebBlock extends Block {
   public CobwebBlock() {
      super(Material.COBWEB);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      entity.onCobwebCollision();
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return null;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.STRING;
   }

   @Override
   protected boolean hasSilkTouchDrops() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }
}
