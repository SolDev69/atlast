package net.minecraft.entity.weather;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class LightningBoltEntity extends WeatherEntity {
   private int stage;
   public long seed;
   private int fireAttempts;

   public LightningBoltEntity(World world, double x, double y, double z) {
      super(world);
      this.refreshPositionAndAngles(x, y, z, 0.0F, 0.0F);
      this.stage = 2;
      this.seed = this.random.nextLong();
      this.fireAttempts = this.random.nextInt(3) + 1;
      if (!world.isClient
         && world.getGameRules().getBoolean("doFireTick")
         && (world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD)
         && world.isRegionLoaded(new BlockPos(this), 10)) {
         BlockPos var8 = new BlockPos(this);
         if (world.getBlockState(var8).getBlock().getMaterial() == Material.AIR && Blocks.FIRE.canSurvive(world, var8)) {
            world.setBlockState(var8, Blocks.FIRE.defaultState());
         }

         for(int var9 = 0; var9 < 4; ++var9) {
            BlockPos var10 = var8.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (world.getBlockState(var10).getBlock().getMaterial() == Material.AIR && Blocks.FIRE.canSurvive(world, var10)) {
               world.setBlockState(var10, Blocks.FIRE.defaultState());
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.stage == 2) {
         this.world.playSound(this.x, this.y, this.z, "ambient.weather.thunder", 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
         this.world.playSound(this.x, this.y, this.z, "random.explode", 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      --this.stage;
      if (this.stage < 0) {
         if (this.fireAttempts == 0) {
            this.remove();
         } else if (this.stage < -this.random.nextInt(10)) {
            --this.fireAttempts;
            this.stage = 1;
            this.seed = this.random.nextLong();
            BlockPos var1 = new BlockPos(this);
            if (!this.world.isClient
               && this.world.getGameRules().getBoolean("doFireTick")
               && this.world.isRegionLoaded(var1, 10)
               && this.world.getBlockState(var1).getBlock().getMaterial() == Material.AIR
               && Blocks.FIRE.canSurvive(this.world, var1)) {
               this.world.setBlockState(var1, Blocks.FIRE.defaultState());
            }
         }
      }

      if (this.stage >= 0) {
         if (this.world.isClient) {
            this.world.setLightningCooldown(2);
         } else {
            double var6 = 3.0;
            List var3 = this.world.getEntities(this, new Box(this.x - var6, this.y - var6, this.z - var6, this.x + var6, this.y + 6.0 + var6, this.z + var6));

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               Entity var5 = (Entity)var3.get(var4);
               var5.onLightningStrike(this);
            }
         }
      }
   }

   @Override
   protected void initDataTracker() {
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
   }
}
