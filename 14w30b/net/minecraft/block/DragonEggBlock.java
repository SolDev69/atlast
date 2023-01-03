package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DragonEggBlock extends Block {
   public DragonEggBlock() {
      super(Material.EGG);
      this.setShape(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      world.scheduleTick(pos, this, this.getTickRate(world));
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      world.scheduleTick(pos, this, this.getTickRate(world));
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      this.tryFall(world, pos);
   }

   private void tryFall(World world, BlockPos pos) {
      if (FallingBlock.canFallThrough(world, pos.down()) && pos.getY() >= 0) {
         byte var3 = 32;
         if (!FallingBlock.fallImmediately && world.isRegionLoaded(pos.add(-var3, -var3, -var3), pos.add(var3, var3, var3))) {
            world.addEntity(
               new FallingBlockEntity(world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), this.defaultState())
            );
         } else {
            world.removeBlock(pos);
            BlockPos var4 = pos;

            while(FallingBlock.canFallThrough(world, var4) && var4.getY() > 0) {
               var4 = var4.down();
            }

            if (var4.getY() > 0) {
               world.setBlockState(var4, this.defaultState(), 2);
            }
         }
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      this.tryTeleport(world, pos);
      return true;
   }

   @Override
   public void startMining(World world, BlockPos pos, PlayerEntity player) {
      this.tryTeleport(world, pos);
   }

   private void tryTeleport(World world, BlockPos x) {
      BlockState var3 = world.getBlockState(x);
      if (var3.getBlock() == this) {
         for(int var4 = 0; var4 < 1000; ++var4) {
            BlockPos var5 = x.add(
               world.random.nextInt(16) - world.random.nextInt(16),
               world.random.nextInt(8) - world.random.nextInt(8),
               world.random.nextInt(16) - world.random.nextInt(16)
            );
            if (world.getBlockState(var5).getBlock().material == Material.AIR) {
               if (world.isClient) {
                  for(int var6 = 0; var6 < 128; ++var6) {
                     double var7 = world.random.nextDouble();
                     float var9 = (world.random.nextFloat() - 0.5F) * 0.2F;
                     float var10 = (world.random.nextFloat() - 0.5F) * 0.2F;
                     float var11 = (world.random.nextFloat() - 0.5F) * 0.2F;
                     double var12 = (double)var5.getX() + (double)(x.getX() - var5.getX()) * var7 + (world.random.nextDouble() - 0.5) * 1.0 + 0.5;
                     double var14 = (double)var5.getY() + (double)(x.getY() - var5.getY()) * var7 + world.random.nextDouble() * 1.0 - 0.5;
                     double var16 = (double)var5.getZ() + (double)(x.getZ() - var5.getZ()) * var7 + (world.random.nextDouble() - 0.5) * 1.0 + 0.5;
                     world.addParticle(ParticleType.PORTAL, var12, var14, var16, (double)var9, (double)var10, (double)var11);
                  }
               } else {
                  world.setBlockState(var5, var3, 2);
                  world.removeBlock(x);
               }

               return;
            }
         }
      }
   }

   @Override
   public int getTickRate(World world) {
      return 5;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byRawId(0);
   }
}
