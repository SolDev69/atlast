package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Dispensable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ArrowEntity extends Entity implements Dispensable {
   private int blockX = -1;
   private int blockY = -1;
   private int blockZ = -1;
   private Block block;
   private int blockData;
   private boolean inGround;
   public int pickup;
   public int shake;
   public Entity shooter;
   private int lifeTicks;
   private int ticksInAir;
   private double damage = 2.0;
   private int punchLevel;

   public ArrowEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.viewDistanceScaling = 10.0;
      this.setDimensions(0.5F, 0.5F);
   }

   public ArrowEntity(World world, double x, double y, double z) {
      super(world);
      this.viewDistanceScaling = 10.0;
      this.setDimensions(0.5F, 0.5F);
      this.setPosition(x, y, z);
   }

   public ArrowEntity(World world, LivingEntity shooter, LivingEntity c_97zulxhng2, float speed, float divergence) {
      super(world);
      this.viewDistanceScaling = 10.0;
      this.shooter = shooter;
      if (shooter instanceof PlayerEntity) {
         this.pickup = 1;
      }

      this.y = shooter.y + (double)shooter.getEyeHeight() - 0.1F;
      double var6 = c_97zulxhng2.x - shooter.x;
      double var8 = c_97zulxhng2.getBoundingBox().minY + (double)(c_97zulxhng2.height / 3.0F) - this.y;
      double var10 = c_97zulxhng2.z - shooter.z;
      double var12 = (double)MathHelper.sqrt(var6 * var6 + var10 * var10);
      if (!(var12 < 1.0E-7)) {
         float var14 = (float)(Math.atan2(var10, var6) * 180.0 / (float) Math.PI) - 90.0F;
         float var15 = (float)(-(Math.atan2(var8, var12) * 180.0 / (float) Math.PI));
         double var16 = var6 / var12;
         double var18 = var10 / var12;
         this.refreshPositionAndAngles(shooter.x + var16, this.y, shooter.z + var18, var14, var15);
         float var20 = (float)(var12 * 0.2F);
         this.setVelocity(var6, var8 + (double)var20, var10, speed, divergence);
      }
   }

   public ArrowEntity(World world, LivingEntity shooter, float speed) {
      super(world);
      this.viewDistanceScaling = 10.0;
      this.shooter = shooter;
      if (shooter instanceof PlayerEntity) {
         this.pickup = 1;
      }

      this.setDimensions(0.5F, 0.5F);
      this.refreshPositionAndAngles(shooter.x, shooter.y + (double)shooter.getEyeHeight(), shooter.z, shooter.yaw, shooter.pitch);
      this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.y -= 0.1F;
      this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.setPosition(this.x, this.y, this.z);
      this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));
      this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI));
      this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float) Math.PI));
      this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, speed * 1.5F, 1.0F);
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(16, (byte)0);
   }

   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ, float min, float scale) {
      float var9 = MathHelper.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
      velocityX /= (double)var9;
      velocityY /= (double)var9;
      velocityZ /= (double)var9;
      velocityX += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)scale;
      velocityY += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)scale;
      velocityZ += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.0075F * (double)scale;
      velocityX *= (double)min;
      velocityY *= (double)min;
      velocityZ *= (double)min;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      float var10 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
      this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
      this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var10) * 180.0 / (float) Math.PI);
      this.lifeTicks = 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
         float var7 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
         this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
         this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var7) * 180.0 / (float) Math.PI);
         this.prevPitch = this.pitch;
         this.prevYaw = this.yaw;
         this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
         this.lifeTicks = 0;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
         float var1 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         this.prevYaw = this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
         this.prevPitch = this.pitch = (float)(Math.atan2(this.velocityY, (double)var1) * 180.0 / (float) Math.PI);
      }

      BlockPos var18 = new BlockPos(this.blockX, this.blockY, this.blockZ);
      BlockState var2 = this.world.getBlockState(var18);
      Block var3 = var2.getBlock();
      if (var3.getMaterial() != Material.AIR) {
         var3.updateShape(this.world, var18);
         Box var4 = var3.getCollisionShape(this.world, var18, var2);
         if (var4 != null && var4.contains(new Vec3d(this.x, this.y, this.z))) {
            this.inGround = true;
         }
      }

      if (this.shake > 0) {
         --this.shake;
      }

      if (this.inGround) {
         int var22 = var3.getMetadataFromState(var2);
         if (var3 == this.block && var22 == this.blockData) {
            ++this.lifeTicks;
            if (this.lifeTicks >= 1200) {
               this.remove();
            }
         } else {
            this.inGround = false;
            this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
            this.lifeTicks = 0;
            this.ticksInAir = 0;
         }
      } else {
         ++this.ticksInAir;
         Vec3d var20 = new Vec3d(this.x, this.y, this.z);
         Vec3d var5 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         HitResult var6 = this.world.rayTrace(var20, var5, false, true, false);
         var20 = new Vec3d(this.x, this.y, this.z);
         var5 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         if (var6 != null) {
            var5 = new Vec3d(var6.pos.x, var6.pos.y, var6.pos.z);
         }

         Entity var7 = null;
         List var8 = this.world.getEntities(this, this.getBoundingBox().grow(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
         double var9 = 0.0;

         for(int var11 = 0; var11 < var8.size(); ++var11) {
            Entity var12 = (Entity)var8.get(var11);
            if (var12.hasCollision() && (var12 != this.shooter || this.ticksInAir >= 5)) {
               float var13 = 0.3F;
               Box var14 = var12.getBoundingBox().expand((double)var13, (double)var13, (double)var13);
               HitResult var15 = var14.clip(var20, var5);
               if (var15 != null) {
                  double var16 = var20.distanceTo(var15.pos);
                  if (var16 < var9 || var9 == 0.0) {
                     var7 = var12;
                     var9 = var16;
                  }
               }
            }
         }

         if (var7 != null) {
            var6 = new HitResult(var7);
         }

         if (var6 != null && var6.entity != null && var6.entity instanceof PlayerEntity) {
            PlayerEntity var24 = (PlayerEntity)var6.entity;
            if (var24.abilities.invulnerable || this.shooter instanceof PlayerEntity && !((PlayerEntity)this.shooter).canAttack(var24)) {
               var6 = null;
            }
         }

         if (var6 != null) {
            if (var6.entity != null) {
               float var25 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
               int var29 = MathHelper.ceil((double)var25 * this.damage);
               if (this.isCritical()) {
                  var29 += this.random.nextInt(var29 / 2 + 2);
               }

               DamageSource var32;
               if (this.shooter == null) {
                  var32 = DamageSource.arrow(this, this);
               } else {
                  var32 = DamageSource.arrow(this, this.shooter);
               }

               if (this.isOnFire() && !(var6.entity instanceof EndermanEntity)) {
                  var6.entity.setOnFireFor(5);
               }

               if (var6.entity.damage(var32, (float)var29)) {
                  if (var6.entity instanceof LivingEntity) {
                     LivingEntity var34 = (LivingEntity)var6.entity;
                     if (!this.world.isClient) {
                        var34.setStuckArrows(var34.getStuckArrows() + 1);
                     }

                     if (this.punchLevel > 0) {
                        float var36 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                        if (var36 > 0.0F) {
                           var6.entity
                              .addVelocity(
                                 this.velocityX * (double)this.punchLevel * 0.6F / (double)var36,
                                 0.1,
                                 this.velocityZ * (double)this.punchLevel * 0.6F / (double)var36
                              );
                        }
                     }

                     if (this.shooter instanceof LivingEntity) {
                        EnchantmentHelper.applyProtectionWildcard(var34, this.shooter);
                        EnchantmentHelper.applyDamageWildcard((LivingEntity)this.shooter, var34);
                     }

                     if (this.shooter != null
                        && var6.entity != this.shooter
                        && var6.entity instanceof PlayerEntity
                        && this.shooter instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)this.shooter).networkHandler.sendPacket(new GameEventS2CPacket(6, 0.0F));
                     }
                  }

                  this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                  if (!(var6.entity instanceof EndermanEntity)) {
                     this.remove();
                  }
               } else {
                  this.velocityX *= -0.1F;
                  this.velocityY *= -0.1F;
                  this.velocityZ *= -0.1F;
                  this.yaw += 180.0F;
                  this.prevYaw += 180.0F;
                  this.ticksInAir = 0;
               }
            } else {
               BlockPos var26 = var6.getBlockPos();
               this.blockX = var26.getX();
               this.blockY = var26.getY();
               this.blockZ = var26.getZ();
               var2 = this.world.getBlockState(var26);
               this.block = var2.getBlock();
               this.blockData = this.block.getMetadataFromState(var2);
               this.velocityX = (double)((float)(var6.pos.x - this.x));
               this.velocityY = (double)((float)(var6.pos.y - this.y));
               this.velocityZ = (double)((float)(var6.pos.z - this.z));
               float var30 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
               this.x -= this.velocityX / (double)var30 * 0.05F;
               this.y -= this.velocityY / (double)var30 * 0.05F;
               this.z -= this.velocityZ / (double)var30 * 0.05F;
               this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
               this.inGround = true;
               this.shake = 7;
               this.setCritical(false);
               if (this.block.getMaterial() != Material.AIR) {
                  this.block.onEntityCollision(this.world, var26, var2, this);
               }
            }
         }

         if (this.isCritical()) {
            for(int var27 = 0; var27 < 4; ++var27) {
               this.world
                  .addParticle(
                     ParticleType.CRIT,
                     this.x + this.velocityX * (double)var27 / 4.0,
                     this.y + this.velocityY * (double)var27 / 4.0,
                     this.z + this.velocityZ * (double)var27 / 4.0,
                     -this.velocityX,
                     -this.velocityY + 0.2,
                     -this.velocityZ
                  );
            }
         }

         this.x += this.velocityX;
         this.y += this.velocityY;
         this.z += this.velocityZ;
         float var28 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
         this.pitch = (float)(Math.atan2(this.velocityY, (double)var28) * 180.0 / (float) Math.PI);

         while(this.pitch - this.prevPitch < -180.0F) {
            this.prevPitch -= 360.0F;
         }

         while(this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
         }

         while(this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
         }

         while(this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
         }

         this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
         this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
         float var31 = 0.99F;
         float var33 = 0.05F;
         if (this.isInWater()) {
            for(int var35 = 0; var35 < 4; ++var35) {
               float var37 = 0.25F;
               this.world
                  .addParticle(
                     ParticleType.WATER_BUBBLE,
                     this.x - this.velocityX * (double)var37,
                     this.y - this.velocityY * (double)var37,
                     this.z - this.velocityZ * (double)var37,
                     this.velocityX,
                     this.velocityY,
                     this.velocityZ
                  );
            }

            var31 = 0.6F;
         }

         if (this.isWet()) {
            this.extinguish();
         }

         this.velocityX *= (double)var31;
         this.velocityY *= (double)var31;
         this.velocityZ *= (double)var31;
         this.velocityY -= (double)var33;
         this.setPosition(this.x, this.y, this.z);
         this.checkBlockCollision();
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("xTile", (short)this.blockX);
      nbt.putShort("yTile", (short)this.blockY);
      nbt.putShort("zTile", (short)this.blockZ);
      nbt.putShort("life", (short)this.lifeTicks);
      Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.block);
      nbt.putString("inTile", var2 == null ? "" : var2.toString());
      nbt.putByte("inData", (byte)this.blockData);
      nbt.putByte("shake", (byte)this.shake);
      nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
      nbt.putByte("pickup", (byte)this.pickup);
      nbt.putDouble("damage", this.damage);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.blockX = nbt.getShort("xTile");
      this.blockY = nbt.getShort("yTile");
      this.blockZ = nbt.getShort("zTile");
      this.lifeTicks = nbt.getShort("life");
      if (nbt.isType("inTile", 8)) {
         this.block = Block.byId(nbt.getString("inTile"));
      } else {
         this.block = Block.byRawId(nbt.getByte("inTile") & 255);
      }

      this.blockData = nbt.getByte("inData") & 255;
      this.shake = nbt.getByte("shake") & 255;
      this.inGround = nbt.getByte("inGround") == 1;
      if (nbt.isType("damage", 99)) {
         this.damage = nbt.getDouble("damage");
      }

      if (nbt.isType("pickup", 99)) {
         this.pickup = nbt.getByte("pickup");
      } else if (nbt.isType("player", 99)) {
         this.pickup = nbt.getBoolean("player") ? 1 : 0;
      }
   }

   @Override
   public void onPlayerCollision(PlayerEntity player) {
      if (!this.world.isClient && this.inGround && this.shake <= 0) {
         boolean var2 = this.pickup == 1 || this.pickup == 2 && player.abilities.creativeMode;
         if (this.pickup == 1 && !player.inventory.insertStack(new ItemStack(Items.ARROW, 1))) {
            var2 = false;
         }

         if (var2) {
            this.playSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.sendPickup(this, 1);
            this.remove();
         }
      }
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setPunchLevel(int level) {
      this.punchLevel = level;
   }

   @Override
   public boolean canBePunched() {
      return false;
   }

   public void setCritical(boolean critical) {
      byte var2 = this.dataTracker.getByte(16);
      if (critical) {
         this.dataTracker.update(16, (byte)(var2 | 1));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -2));
      }
   }

   public boolean isCritical() {
      byte var1 = this.dataTracker.getByte(16);
      return (var1 & 1) != 0;
   }
}
