package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EndPortalBlock extends BlockWithBlockEntity {
   protected EndPortalBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setLightLevel(1.0F);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new EndPortalBlockEntity();
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      float var3 = 0.0625F;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return face == Direction.DOWN ? super.shouldRenderFace(world, pos, face) : false;
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
      if (entity.vehicle == null && entity.rider == null && !world.isClient) {
         entity.teleportToDimension(1);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      double var5 = (double)((float)pos.getX() + random.nextFloat());
      double var7 = (double)((float)pos.getY() + 0.8F);
      double var9 = (double)((float)pos.getZ() + random.nextFloat());
      double var11 = 0.0;
      double var13 = 0.0;
      double var15 = 0.0;
      world.addParticle(ParticleType.SMOKE_NORMAL, var5, var7, var9, var11, var13, var15);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(0);
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.SPRUCE;
   }
}
