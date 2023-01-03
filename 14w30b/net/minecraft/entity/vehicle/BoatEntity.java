package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BoatEntity extends Entity {
   private boolean empty = true;
   private double speedBoost = 0.07;
   private int velocity;
   private double boatX;
   private double boatY;
   private double boatZ;
   private double boatYaw;
   private double boatPitch;
   @Environment(EnvType.CLIENT)
   private double boatVelocityX;
   @Environment(EnvType.CLIENT)
   private double boatVelocityY;
   @Environment(EnvType.CLIENT)
   private double boatVelocityZ;

   public BoatEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.blocksBuilding = true;
      this.setDimensions(1.5F, 0.6F);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(17, new Integer(0));
      this.dataTracker.put(18, new Integer(1));
      this.dataTracker.put(19, new Float(0.0F));
   }

   @Override
   public Box getHardCollisionBox(Entity collidingEntity) {
      return collidingEntity.getBoundingBox();
   }

   @Override
   public Box getBox() {
      return this.getBoundingBox();
   }

   @Override
   public boolean isPushable() {
      return true;
   }

   public BoatEntity(World world, double x, double y, double z) {
      this(world);
      this.setPosition(x, y, z);
      this.velocityX = 0.0;
      this.velocityY = 0.0;
      this.velocityZ = 0.0;
      this.prevX = x;
      this.prevY = y;
      this.prevZ = z;
   }

   @Override
   public double getMountHeight() {
      return (double)this.height * 0.0 - 0.3F;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (!this.world.isClient && !this.removed) {
         if (this.rider != null && this.rider == source.getAttacker() && source instanceof ProjectileDamageSource) {
            return false;
         } else {
            this.setAnitmationSide(-this.getAnimationSide());
            this.setBreakingWindow(10);
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.onDamaged();
            boolean var3 = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
            if (var3 || this.getDamage() > 40.0F) {
               if (this.rider != null) {
                  this.rider.startRiding(this);
               }

               if (!var3) {
                  this.dropItem(Items.BOAT, 1, 0.0F);
               }

               this.remove();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void animateDamage() {
      this.setAnitmationSide(-this.getAnimationSide());
      this.setBreakingWindow(10);
      this.setDamage(this.getDamage() * 11.0F);
   }

   @Override
   public boolean hasCollision() {
      return !this.removed;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
      if (this.empty) {
         this.velocity = steps + 5;
      } else {
         double var10 = x - this.x;
         double var12 = y - this.y;
         double var14 = z - this.z;
         double var16 = var10 * var10 + var12 * var12 + var14 * var14;
         if (!(var16 > 1.0)) {
            return;
         }

         this.velocity = 3;
      }

      this.boatX = x;
      this.boatY = y;
      this.boatZ = z;
      this.boatYaw = (double)yaw;
      this.boatPitch = (double)pitch;
      this.velocityX = this.boatVelocityX;
      this.velocityY = this.boatVelocityY;
      this.velocityZ = this.boatVelocityZ;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.boatVelocityX = this.velocityX = velocityX;
      this.boatVelocityY = this.velocityY = velocityY;
      this.boatVelocityZ = this.velocityZ = velocityZ;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getBreakingWindow() > 0) {
         this.setBreakingWindow(this.getBreakingWindow() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      byte var1 = 5;
      double var2 = 0.0;

      for(int var4 = 0; var4 < var1; ++var4) {
         double var5 = this.getBoundingBox().minY + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(var4 + 0) / (double)var1 - 0.125;
         double var7 = this.getBoundingBox().minY + (this.getBoundingBox().maxY - this.getBoundingBox().minY) * (double)(var4 + 1) / (double)var1 - 0.125;
         Box var9 = new Box(this.getBoundingBox().minX, var5, this.getBoundingBox().minZ, this.getBoundingBox().maxX, var7, this.getBoundingBox().maxZ);
         if (this.world.containsLiquid(var9, Material.WATER)) {
            var2 += 1.0 / (double)var1;
         }
      }

      double var19 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      if (var19 > 0.2975) {
         double var6 = Math.cos((double)this.yaw * Math.PI / 180.0);
         double var8 = Math.sin((double)this.yaw * Math.PI / 180.0);

         for(int var10 = 0; (double)var10 < 1.0 + var19 * 60.0; ++var10) {
            double var11 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
            double var13 = (double)(this.random.nextInt(2) * 2 - 1) * 0.7;
            if (this.random.nextBoolean()) {
               double var15 = this.x - var6 * var11 * 0.8 + var8 * var13;
               double var17 = this.z - var8 * var11 * 0.8 - var6 * var13;
               this.world.addParticle(ParticleType.WATER_SPLASH, var15, this.y - 0.125, var17, this.velocityX, this.velocityY, this.velocityZ);
            } else {
               double var43 = this.x + var6 + var8 * var11 * 0.7;
               double var44 = this.z + var8 - var6 * var11 * 0.7;
               this.world.addParticle(ParticleType.WATER_SPLASH, var43, this.y - 0.125, var44, this.velocityX, this.velocityY, this.velocityZ);
            }
         }
      }

      if (this.world.isClient && this.empty) {
         if (this.velocity > 0) {
            double var23 = this.x + (this.boatX - this.x) / (double)this.velocity;
            double var31 = this.y + (this.boatY - this.y) / (double)this.velocity;
            double var36 = this.z + (this.boatZ - this.z) / (double)this.velocity;
            double var40 = MathHelper.wrapDegrees(this.boatYaw - (double)this.yaw);
            this.yaw = (float)((double)this.yaw + var40 / (double)this.velocity);
            this.pitch = (float)((double)this.pitch + (this.boatPitch - (double)this.pitch) / (double)this.velocity);
            --this.velocity;
            this.setPosition(var23, var31, var36);
            this.setRotation(this.yaw, this.pitch);
         } else {
            double var24 = this.x + this.velocityX;
            double var32 = this.y + this.velocityY;
            double var37 = this.z + this.velocityZ;
            this.setPosition(var24, var32, var37);
            if (this.onGround) {
               this.velocityX *= 0.5;
               this.velocityY *= 0.5;
               this.velocityZ *= 0.5;
            }

            this.velocityX *= 0.99F;
            this.velocityY *= 0.95F;
            this.velocityZ *= 0.99F;
         }
      } else {
         if (var2 < 1.0) {
            double var20 = var2 * 2.0 - 1.0;
            this.velocityY += 0.04F * var20;
         } else {
            if (this.velocityY < 0.0) {
               this.velocityY /= 2.0;
            }

            this.velocityY += 0.007F;
         }

         if (this.rider instanceof LivingEntity) {
            LivingEntity var21 = (LivingEntity)this.rider;
            float var25 = this.rider.yaw + -var21.sidewaysSpeed * 90.0F;
            this.velocityX += -Math.sin((double)(var25 * (float) Math.PI / 180.0F)) * this.speedBoost * (double)var21.forwardSpeed * 0.05F;
            this.velocityZ += Math.cos((double)(var25 * (float) Math.PI / 180.0F)) * this.speedBoost * (double)var21.forwardSpeed * 0.05F;
         }

         double var22 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         if (var22 > 0.35) {
            double var26 = 0.35 / var22;
            this.velocityX *= var26;
            this.velocityZ *= var26;
            var22 = 0.35;
         }

         if (var22 > var19 && this.speedBoost < 0.35) {
            this.speedBoost += (0.35 - this.speedBoost) / 35.0;
            if (this.speedBoost > 0.35) {
               this.speedBoost = 0.35;
            }
         } else {
            this.speedBoost -= (this.speedBoost - 0.07) / 35.0;
            if (this.speedBoost < 0.07) {
               this.speedBoost = 0.07;
            }
         }

         for(int var27 = 0; var27 < 4; ++var27) {
            int var33 = MathHelper.floor(this.x + ((double)(var27 % 2) - 0.5) * 0.8);
            int var34 = MathHelper.floor(this.z + ((double)(var27 / 2) - 0.5) * 0.8);

            for(int var38 = 0; var38 < 2; ++var38) {
               int var12 = MathHelper.floor(this.y) + var38;
               BlockPos var41 = new BlockPos(var33, var12, var34);
               Block var14 = this.world.getBlockState(var41).getBlock();
               if (var14 == Blocks.SNOW_LAYER) {
                  this.world.removeBlock(var41);
                  this.collidingHorizontally = false;
               } else if (var14 == Blocks.LILY_PAD) {
                  this.world.breakBlock(var41, true);
                  this.collidingHorizontally = false;
               }
            }
         }

         if (this.onGround) {
            this.velocityX *= 0.5;
            this.velocityY *= 0.5;
            this.velocityZ *= 0.5;
         }

         this.move(this.velocityX, this.velocityY, this.velocityZ);
         if (!this.collidingHorizontally || !(var19 > 0.2)) {
            this.velocityX *= 0.99F;
            this.velocityY *= 0.95F;
            this.velocityZ *= 0.99F;
         } else if (!this.world.isClient && !this.removed) {
            this.remove();

            for(int var28 = 0; var28 < 3; ++var28) {
               this.dropItem(Item.byBlock(Blocks.PLANKS), 1, 0.0F);
            }

            for(int var29 = 0; var29 < 2; ++var29) {
               this.dropItem(Items.STICK, 1, 0.0F);
            }
         }

         this.pitch = 0.0F;
         double var30 = (double)this.yaw;
         double var35 = this.prevX - this.x;
         double var39 = this.prevZ - this.z;
         if (var35 * var35 + var39 * var39 > 0.001) {
            var30 = (double)((float)(Math.atan2(var39, var35) * 180.0 / Math.PI));
         }

         double var42 = MathHelper.wrapDegrees(var30 - (double)this.yaw);
         if (var42 > 20.0) {
            var42 = 20.0;
         }

         if (var42 < -20.0) {
            var42 = -20.0;
         }

         this.yaw = (float)((double)this.yaw + var42);
         this.setRotation(this.yaw, this.pitch);
         if (!this.world.isClient) {
            List var16 = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F));
            if (var16 != null && !var16.isEmpty()) {
               for(int var45 = 0; var45 < var16.size(); ++var45) {
                  Entity var18 = (Entity)var16.get(var45);
                  if (var18 != this.rider && var18.isPushable() && var18 instanceof BoatEntity) {
                     var18.push(this);
                  }
               }
            }

            if (this.rider != null && this.rider.removed) {
               this.rider = null;
            }
         }
      }
   }

   @Override
   public void updateRiderPositon() {
      if (this.rider != null) {
         double var1 = Math.cos((double)this.yaw * Math.PI / 180.0) * 0.4;
         double var3 = Math.sin((double)this.yaw * Math.PI / 180.0) * 0.4;
         this.rider.setPosition(this.x + var1, this.y + this.getMountHeight() + this.rider.getRideHeight(), this.z + var3);
      }
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   public boolean interact(PlayerEntity player) {
      if (this.rider != null && this.rider instanceof PlayerEntity && this.rider != player) {
         return true;
      } else {
         if (!this.world.isClient) {
            player.startRiding(this);
         }

         return true;
      }
   }

   @Override
   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
      if (landed) {
         if (this.fallDistance > 3.0F) {
            this.applyFallDamage(this.fallDistance, 1.0F);
            if (!this.world.isClient && !this.removed) {
               this.remove();

               for(int var6 = 0; var6 < 3; ++var6) {
                  this.dropItem(Item.byBlock(Blocks.PLANKS), 1, 0.0F);
               }

               for(int var7 = 0; var7 < 2; ++var7) {
                  this.dropItem(Items.STICK, 1, 0.0F);
               }
            }

            this.fallDistance = 0.0F;
         }
      } else if (this.world.getBlockState(new BlockPos(this).down()).getBlock().getMaterial() != Material.WATER && dy < 0.0) {
         this.fallDistance = (float)((double)this.fallDistance - dy);
      }
   }

   public void setDamage(float damage) {
      this.dataTracker.update(19, damage);
   }

   public float getDamage() {
      return this.dataTracker.getFloat(19);
   }

   public void setBreakingWindow(int value) {
      this.dataTracker.update(17, value);
   }

   public int getBreakingWindow() {
      return this.dataTracker.getInt(17);
   }

   public void setAnitmationSide(int value) {
      this.dataTracker.update(18, value);
   }

   public int getAnimationSide() {
      return this.dataTracker.getInt(18);
   }

   @Environment(EnvType.CLIENT)
   public void setEmpty(boolean empty) {
      this.empty = empty;
   }
}
