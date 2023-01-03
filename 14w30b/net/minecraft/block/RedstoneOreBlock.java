package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class RedstoneOreBlock extends Block {
   private final boolean lit;

   public RedstoneOreBlock(boolean lit) {
      super(Material.STONE);
      if (lit) {
         this.setTicksRandomly(true);
      }

      this.lit = lit;
   }

   @Override
   public int getTickRate(World world) {
      return 30;
   }

   @Override
   public void startMining(World world, BlockPos pos, PlayerEntity player) {
      this.lightUp(world, pos);
      super.startMining(world, pos, player);
   }

   @Override
   public void onSteppedOn(World world, BlockPos pos, Entity entity) {
      this.lightUp(world, pos);
      super.onSteppedOn(world, pos, entity);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      this.lightUp(world, pos);
      return super.use(world, pos, state, player, face, dx, dy, dz);
   }

   private void lightUp(World world, BlockPos x) {
      this.addParticles(world, x);
      if (this == Blocks.REDSTONE_ORE) {
         world.setBlockState(x, Blocks.LIT_REDSTONE_ORE.defaultState());
      }
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (this == Blocks.LIT_REDSTONE_ORE) {
         world.setBlockState(pos, Blocks.REDSTONE_ORE.defaultState());
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.REDSTONE;
   }

   @Override
   public int getDropCount(int fortuneLevel, Random random) {
      return this.getBaseDropCount(random) + random.nextInt(fortuneLevel + 1);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 4 + random.nextInt(2);
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      super.dropItems(world, pos, state, luck, fortuneLevel);
      if (this.getDropItem(state, world.random, fortuneLevel) != Item.byBlock(this)) {
         int var6 = 1 + world.random.nextInt(5);
         this.dropXp(world, pos, var6);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
      if (this.lit) {
         this.addParticles(world, pos);
      }
   }

   private void addParticles(World world, BlockPos pos) {
      Random var3 = world.random;
      double var4 = 0.0625;

      for(int var6 = 0; var6 < 6; ++var6) {
         double var7 = (double)((float)pos.getX() + var3.nextFloat());
         double var9 = (double)((float)pos.getY() + var3.nextFloat());
         double var11 = (double)((float)pos.getZ() + var3.nextFloat());
         if (var6 == 0 && !world.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
            var9 = (double)pos.getY() + var4 + 1.0;
         }

         if (var6 == 1 && !world.getBlockState(pos.down()).getBlock().isOpaqueCube()) {
            var9 = (double)pos.getY() - var4;
         }

         if (var6 == 2 && !world.getBlockState(pos.south()).getBlock().isOpaqueCube()) {
            var11 = (double)pos.getZ() + var4 + 1.0;
         }

         if (var6 == 3 && !world.getBlockState(pos.north()).getBlock().isOpaqueCube()) {
            var11 = (double)pos.getZ() - var4;
         }

         if (var6 == 4 && !world.getBlockState(pos.east()).getBlock().isOpaqueCube()) {
            var7 = (double)pos.getX() + var4 + 1.0;
         }

         if (var6 == 5 && !world.getBlockState(pos.west()).getBlock().isOpaqueCube()) {
            var7 = (double)pos.getX() - var4;
         }

         if (var7 < (double)pos.getX()
            || var7 > (double)(pos.getX() + 1)
            || var9 < 0.0
            || var9 > (double)(pos.getY() + 1)
            || var11 < (double)pos.getZ()
            || var11 > (double)(pos.getZ() + 1)) {
            world.addParticle(ParticleType.REDSTONE, var7, var9, var11, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Blocks.REDSTONE_ORE);
   }
}
