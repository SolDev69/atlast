package net.minecraft.world.village;

import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.MobSpawnerHelper;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VillageSiege {
   private World world;
   private boolean ready;
   private int state = -1;
   private int zombieCount;
   private int cooldown;
   private Village village;
   private int siegeX;
   private int siegeY;
   private int siegeZ;

   public VillageSiege(World world) {
      this.world = world;
   }

   public void tick() {
      if (this.world.isSunny()) {
         this.state = 0;
      } else if (this.state != 2) {
         if (this.state == 0) {
            float var1 = this.world.getTimeOfDay(0.0F);
            if ((double)var1 < 0.5 || (double)var1 > 0.501) {
               return;
            }

            this.state = this.world.random.nextInt(10) == 0 ? 1 : 2;
            this.ready = false;
            if (this.state == 2) {
               return;
            }
         }

         if (this.state != -1) {
            if (!this.ready) {
               if (!this.initSiege()) {
                  return;
               }

               this.ready = true;
            }

            if (this.cooldown > 0) {
               --this.cooldown;
            } else {
               this.cooldown = 2;
               if (this.zombieCount > 0) {
                  this.spawnZombies();
                  --this.zombieCount;
               } else {
                  this.state = 2;
               }
            }
         }
      }
   }

   private boolean initSiege() {
      for(PlayerEntity var3 : this.world.players) {
         if (!var3.isSpectator()) {
            this.village = this.world.getVillageData().getClosestVillage(new BlockPos(var3), 1);
            if (this.village != null
               && this.village.getDoorCount() >= 10
               && this.village.getTimeSinceLastDoorAdded() >= 20
               && this.village.getPopulationSize() >= 20) {
               BlockPos var4 = this.village.getCenter();
               float var5 = (float)this.village.getRadius();
               boolean var6 = false;

               for(int var7 = 0; var7 < 10; ++var7) {
                  float var8 = this.world.random.nextFloat() * (float) Math.PI * 2.0F;
                  this.siegeX = var4.getX() + (int)((double)(MathHelper.cos(var8) * var5) * 0.9);
                  this.siegeY = var4.getY();
                  this.siegeZ = var4.getZ() + (int)((double)(MathHelper.sin(var8) * var5) * 0.9);
                  var6 = false;

                  for(Village var10 : this.world.getVillageData().getVillages()) {
                     if (var10 != this.village && var10.contains(new BlockPos(this.siegeX, this.siegeY, this.siegeZ))) {
                        var6 = true;
                        break;
                     }
                  }

                  if (!var6) {
                     break;
                  }
               }

               if (var6) {
                  return false;
               }

               Vec3d var11 = this.findSpawnPos(new BlockPos(this.siegeX, this.siegeY, this.siegeZ));
               if (var11 != null) {
                  this.cooldown = 0;
                  this.zombieCount = 20;
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean spawnZombies() {
      Vec3d var1 = this.findSpawnPos(new BlockPos(this.siegeX, this.siegeY, this.siegeZ));
      if (var1 == null) {
         return false;
      } else {
         ZombieEntity var2;
         try {
            var2 = new ZombieEntity(this.world);
            var2.initialize(this.world.getLocalDifficulty(new BlockPos(var2)), null);
            var2.setVillager(false);
         } catch (Exception var4) {
            var4.printStackTrace();
            return false;
         }

         var2.refreshPositionAndAngles(var1.x, var1.y, var1.z, this.world.random.nextFloat() * 360.0F, 0.0F);
         this.world.addEntity(var2);
         BlockPos var3 = this.village.getCenter();
         var2.setVillagePosAndRadius(var3, this.village.getRadius());
         return true;
      }
   }

   private Vec3d findSpawnPos(BlockPos siegePos) {
      for(int var2 = 0; var2 < 10; ++var2) {
         BlockPos var3 = siegePos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
         if (this.village.contains(var3) && MobSpawnerHelper.canSpawn(MobEntity.Environment.ON_GROUND, this.world, var3)) {
            return new Vec3d((double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
         }
      }

      return null;
   }
}
