package net.minecraft.entity.living.mob.hostile.boss;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.Monster;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.EnvironmentInterface;
import net.ornithemc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
   value = EnvType.CLIENT,
   itf = Boss.class
)})
public class EnderDragonEntity extends MobEntity implements Boss, EnderDragon, Monster {
   public double targetX;
   public double targetY;
   public double targetZ;
   public double[][] circularSegmentBuffer = new double[64][3];
   public int latestSegment = -1;
   public EnderDragonPart[] parts;
   public EnderDragonPart head;
   public EnderDragonPart body;
   public EnderDragonPart tailBase;
   public EnderDragonPart tailMiddle;
   public EnderDragonPart tailEnd;
   public EnderDragonPart rightWing;
   public EnderDragonPart leftWing;
   public float lastWingPosition;
   public float wingPosition;
   public boolean needsNewTarget;
   public boolean restrictMovement;
   private Entity target;
   public int ticksSinceDeath;
   public EnderCrystalEntity connectedCrystal;

   public EnderDragonEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.parts = new EnderDragonPart[]{
         this.head = new EnderDragonPart(this, "head", 6.0F, 6.0F),
         this.body = new EnderDragonPart(this, "body", 8.0F, 8.0F),
         this.tailBase = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
         this.tailMiddle = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
         this.tailEnd = new EnderDragonPart(this, "tail", 4.0F, 4.0F),
         this.rightWing = new EnderDragonPart(this, "wing", 4.0F, 4.0F),
         this.leftWing = new EnderDragonPart(this, "wing", 4.0F, 4.0F)
      };
      this.setHealth(this.getMaxHealth());
      this.setDimensions(16.0F, 8.0F);
      this.noClip = true;
      this.immuneToFire = true;
      this.targetY = 100.0;
      this.ignoreCameraFrustum = true;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(200.0);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
   }

   public double[] getSegmentProperties(int segment, float tickDelta) {
      if (this.getHealth() <= 0.0F) {
         tickDelta = 0.0F;
      }

      tickDelta = 1.0F - tickDelta;
      int var3 = this.latestSegment - segment * 1 & 63;
      int var4 = this.latestSegment - segment * 1 - 1 & 63;
      double[] var5 = new double[3];
      double var6 = this.circularSegmentBuffer[var3][0];
      double var8 = MathHelper.wrapDegrees(this.circularSegmentBuffer[var4][0] - var6);
      var5[0] = var6 + var8 * (double)tickDelta;
      var6 = this.circularSegmentBuffer[var3][1];
      var8 = this.circularSegmentBuffer[var4][1] - var6;
      var5[1] = var6 + var8 * (double)tickDelta;
      var5[2] = this.circularSegmentBuffer[var3][2] + (this.circularSegmentBuffer[var4][2] - this.circularSegmentBuffer[var3][2]) * (double)tickDelta;
      return var5;
   }

   @Override
   public void tickAI() {
      if (this.world.isClient) {
         float var1 = MathHelper.cos(this.wingPosition * (float) Math.PI * 2.0F);
         float var2 = MathHelper.cos(this.lastWingPosition * (float) Math.PI * 2.0F);
         if (var2 <= -0.3F && var1 >= -0.3F && !this.isSilent()) {
            this.world.playSound(this.x, this.y, this.z, "mob.enderdragon.wings", 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
         }
      }

      this.lastWingPosition = this.wingPosition;
      if (this.getHealth() <= 0.0F) {
         float var29 = (this.random.nextFloat() - 0.5F) * 8.0F;
         float var34 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float var35 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.world.addParticle(ParticleType.EXPLOSION_LARGE, this.x + (double)var29, this.y + 2.0 + (double)var34, this.z + (double)var35, 0.0, 0.0, 0.0);
      } else {
         this.tickEndCrystalInteraction();
         float var27 = 0.2F / (MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 10.0F + 1.0F);
         var27 *= (float)Math.pow(2.0, this.velocityY);
         if (this.restrictMovement) {
            this.wingPosition += var27 * 0.5F;
         } else {
            this.wingPosition += var27;
         }

         this.yaw = MathHelper.wrapDegrees(this.yaw);
         if (this.latestSegment < 0) {
            for(int var30 = 0; var30 < this.circularSegmentBuffer.length; ++var30) {
               this.circularSegmentBuffer[var30][0] = (double)this.yaw;
               this.circularSegmentBuffer[var30][1] = this.y;
            }
         }

         if (++this.latestSegment == this.circularSegmentBuffer.length) {
            this.latestSegment = 0;
         }

         this.circularSegmentBuffer[this.latestSegment][0] = (double)this.yaw;
         this.circularSegmentBuffer[this.latestSegment][1] = this.y;
         if (this.world.isClient) {
            if (this.bodyTrackingIncrements > 0) {
               double var31 = this.x + (this.serverX - this.x) / (double)this.bodyTrackingIncrements;
               double var4 = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
               double var6 = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
               double var8 = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
               this.yaw = (float)((double)this.yaw + var8 / (double)this.bodyTrackingIncrements);
               this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
               --this.bodyTrackingIncrements;
               this.setPosition(var31, var4, var6);
               this.setRotation(this.yaw, this.pitch);
            }
         } else {
            double var32 = this.targetX - this.x;
            double var36 = this.targetY - this.y;
            double var40 = this.targetZ - this.z;
            double var42 = var32 * var32 + var36 * var36 + var40 * var40;
            if (this.target != null) {
               this.targetX = this.target.x;
               this.targetZ = this.target.z;
               double var10 = this.targetX - this.x;
               double var12 = this.targetZ - this.z;
               double var14 = Math.sqrt(var10 * var10 + var12 * var12);
               double var16 = 0.4F + var14 / 80.0 - 1.0;
               if (var16 > 10.0) {
                  var16 = 10.0;
               }

               this.targetY = this.target.getBoundingBox().minY + var16;
            } else {
               this.targetX += this.random.nextGaussian() * 2.0;
               this.targetZ += this.random.nextGaussian() * 2.0;
            }

            if (this.needsNewTarget || var42 < 100.0 || var42 > 22500.0 || this.collidingHorizontally || this.collidingVertically) {
               this.chooseTarget();
            }

            var36 /= (double)MathHelper.sqrt(var32 * var32 + var40 * var40);
            float var45 = 0.6F;
            var36 = MathHelper.clamp(var36, (double)(-var45), (double)var45);
            this.velocityY += var36 * 0.1F;
            this.yaw = MathHelper.wrapDegrees(this.yaw);
            double var11 = 180.0 - Math.atan2(var32, var40) * 180.0 / (float) Math.PI;
            double var13 = MathHelper.wrapDegrees(var11 - (double)this.yaw);
            if (var13 > 50.0) {
               var13 = 50.0;
            }

            if (var13 < -50.0) {
               var13 = -50.0;
            }

            Vec3d var15 = new Vec3d(this.targetX - this.x, this.targetY - this.y, this.targetZ - this.z).normalize();
            double var54 = (double)(-MathHelper.cos(this.yaw * (float) Math.PI / 180.0F));
            Vec3d var18 = new Vec3d((double)MathHelper.sin(this.yaw * (float) Math.PI / 180.0F), this.velocityY, var54).normalize();
            float var19 = ((float)var18.dot(var15) + 0.5F) / 1.5F;
            if (var19 < 0.0F) {
               var19 = 0.0F;
            }

            this.randomYaw *= 0.8F;
            float var20 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 1.0F + 1.0F;
            double var21 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ) * 1.0 + 1.0;
            if (var21 > 40.0) {
               var21 = 40.0;
            }

            this.randomYaw = (float)((double)this.randomYaw + var13 * (0.7F / var21 / (double)var20));
            this.yaw += this.randomYaw * 0.1F;
            float var23 = (float)(2.0 / (var21 + 1.0));
            float var24 = 0.06F;
            this.updateVelocity(0.0F, -1.0F, var24 * (var19 * var23 + (1.0F - var23)));
            if (this.restrictMovement) {
               this.move(this.velocityX * 0.8F, this.velocityY * 0.8F, this.velocityZ * 0.8F);
            } else {
               this.move(this.velocityX, this.velocityY, this.velocityZ);
            }

            Vec3d var25 = new Vec3d(this.velocityX, this.velocityY, this.velocityZ).normalize();
            float var26 = ((float)var25.dot(var18) + 1.0F) / 2.0F;
            var26 = 0.8F + 0.15F * var26;
            this.velocityX *= (double)var26;
            this.velocityZ *= (double)var26;
            this.velocityY *= 0.91F;
         }

         this.bodyYaw = this.yaw;
         this.head.width = this.head.height = 3.0F;
         this.tailBase.width = this.tailBase.height = 2.0F;
         this.tailMiddle.width = this.tailMiddle.height = 2.0F;
         this.tailEnd.width = this.tailEnd.height = 2.0F;
         this.body.height = 3.0F;
         this.body.width = 5.0F;
         this.rightWing.height = 2.0F;
         this.rightWing.width = 4.0F;
         this.leftWing.height = 3.0F;
         this.leftWing.width = 4.0F;
         float var33 = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
         float var3 = MathHelper.cos(var33);
         float var39 = -MathHelper.sin(var33);
         float var5 = this.yaw * (float) Math.PI / 180.0F;
         float var41 = MathHelper.sin(var5);
         float var7 = MathHelper.cos(var5);
         this.body.tick();
         this.body.refreshPositionAndAngles(this.x + (double)(var41 * 0.5F), this.y, this.z - (double)(var7 * 0.5F), 0.0F, 0.0F);
         this.rightWing.tick();
         this.rightWing.refreshPositionAndAngles(this.x + (double)(var7 * 4.5F), this.y + 2.0, this.z + (double)(var41 * 4.5F), 0.0F, 0.0F);
         this.leftWing.tick();
         this.leftWing.refreshPositionAndAngles(this.x - (double)(var7 * 4.5F), this.y + 2.0, this.z - (double)(var41 * 4.5F), 0.0F, 0.0F);
         if (!this.world.isClient && this.hurtTimer == 0) {
            this.flingLivingEntities(this.world.getEntities(this, this.rightWing.getBoundingBox().expand(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0)));
            this.flingLivingEntities(this.world.getEntities(this, this.leftWing.getBoundingBox().expand(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0)));
            this.damageLivingEntities(this.world.getEntities(this, this.head.getBoundingBox().expand(1.0, 1.0, 1.0)));
         }

         double[] var43 = this.getSegmentProperties(5, 1.0F);
         double[] var9 = this.getSegmentProperties(0, 1.0F);
         float var46 = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F - this.randomYaw * 0.01F);
         float var48 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F - this.randomYaw * 0.01F);
         this.head.tick();
         this.head
            .refreshPositionAndAngles(
               this.x + (double)(var46 * 5.5F * var3),
               this.y + (var9[1] - var43[1]) * 1.0 + (double)(var39 * 5.5F),
               this.z - (double)(var48 * 5.5F * var3),
               0.0F,
               0.0F
            );

         for(int var44 = 0; var44 < 3; ++var44) {
            EnderDragonPart var47 = null;
            if (var44 == 0) {
               var47 = this.tailBase;
            }

            if (var44 == 1) {
               var47 = this.tailMiddle;
            }

            if (var44 == 2) {
               var47 = this.tailEnd;
            }

            double[] var49 = this.getSegmentProperties(12 + var44 * 2, 1.0F);
            float var50 = this.yaw * (float) Math.PI / 180.0F + this.wrapAngle(var49[0] - var43[0]) * (float) Math.PI / 180.0F * 1.0F;
            float var51 = MathHelper.sin(var50);
            float var52 = MathHelper.cos(var50);
            float var53 = 1.5F;
            float var55 = (float)(var44 + 1) * 2.0F;
            var47.tick();
            var47.refreshPositionAndAngles(
               this.x - (double)((var41 * var53 + var51 * var55) * var3),
               this.y + (var49[1] - var43[1]) * 1.0 - (double)((var55 + var53) * var39) + 1.5,
               this.z + (double)((var7 * var53 + var52 * var55) * var3),
               0.0F,
               0.0F
            );
         }

         if (!this.world.isClient) {
            this.restrictMovement = this.destroyBlocks(this.head.getBoundingBox()) | this.destroyBlocks(this.body.getBoundingBox());
         }
      }
   }

   private void tickEndCrystalInteraction() {
      if (this.connectedCrystal != null) {
         if (this.connectedCrystal.removed) {
            if (!this.world.isClient) {
               this.damage(this.head, DamageSource.explosion(null), 10.0F);
            }

            this.connectedCrystal = null;
         } else if (this.time % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.random.nextInt(10) == 0) {
         float var1 = 32.0F;
         List var2 = this.world.getEntities(EnderCrystalEntity.class, this.getBoundingBox().expand((double)var1, (double)var1, (double)var1));
         EnderCrystalEntity var3 = null;
         double var4 = Double.MAX_VALUE;

         for(EnderCrystalEntity var7 : var2) {
            double var8 = var7.getSquaredDistanceTo(this);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }

         this.connectedCrystal = var3;
      }
   }

   private void flingLivingEntities(List entities) {
      double var2 = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
      double var4 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;

      for(Entity var7 : entities) {
         if (var7 instanceof LivingEntity) {
            double var8 = var7.x - var2;
            double var10 = var7.z - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.addVelocity(var8 / var12 * 4.0, 0.2F, var10 / var12 * 4.0);
         }
      }
   }

   private void damageLivingEntities(List entities) {
      for(int var2 = 0; var2 < entities.size(); ++var2) {
         Entity var3 = (Entity)entities.get(var2);
         if (var3 instanceof LivingEntity) {
            var3.damage(DamageSource.mob(this), 10.0F);
         }
      }
   }

   private void chooseTarget() {
      this.needsNewTarget = false;
      ArrayList var1 = Lists.newArrayList(this.world.players);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         if (((PlayerEntity)var2.next()).isSpectator()) {
            var2.remove();
         }
      }

      if (this.random.nextInt(2) == 0 && !var1.isEmpty()) {
         this.target = (Entity)var1.get(this.random.nextInt(var1.size()));
      } else {
         boolean var3;
         do {
            this.targetX = 0.0;
            this.targetY = (double)(70.0F + this.random.nextFloat() * 50.0F);
            this.targetZ = 0.0;
            this.targetX += (double)(this.random.nextFloat() * 120.0F - 60.0F);
            this.targetZ += (double)(this.random.nextFloat() * 120.0F - 60.0F);
            double var4 = this.x - this.targetX;
            double var6 = this.y - this.targetY;
            double var8 = this.z - this.targetZ;
            var3 = var4 * var4 + var6 * var6 + var8 * var8 > 100.0;
         } while(!var3);

         this.target = null;
      }
   }

   private float wrapAngle(double angle) {
      return (float)MathHelper.wrapDegrees(angle);
   }

   private boolean destroyBlocks(Box box) {
      int var2 = MathHelper.floor(box.minX);
      int var3 = MathHelper.floor(box.minY);
      int var4 = MathHelper.floor(box.minZ);
      int var5 = MathHelper.floor(box.maxX);
      int var6 = MathHelper.floor(box.maxY);
      int var7 = MathHelper.floor(box.maxZ);
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var2; var10 <= var5; ++var10) {
         for(int var11 = var3; var11 <= var6; ++var11) {
            for(int var12 = var4; var12 <= var7; ++var12) {
               Block var13 = this.world.getBlockState(new BlockPos(var10, var11, var12)).getBlock();
               if (var13.getMaterial() != Material.AIR) {
                  if (var13 != Blocks.BARRIER
                     && var13 != Blocks.OBSIDIAN
                     && var13 != Blocks.END_STONE
                     && var13 != Blocks.BEDROCK
                     && this.world.getGameRules().getBoolean("mobGriefing")) {
                     var9 = this.world.removeBlock(new BlockPos(var10, var11, var12)) || var9;
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         double var16 = box.minX + (box.maxX - box.minX) * (double)this.random.nextFloat();
         double var17 = box.minY + (box.maxY - box.minY) * (double)this.random.nextFloat();
         double var14 = box.minZ + (box.maxZ - box.minZ) * (double)this.random.nextFloat();
         this.world.addParticle(ParticleType.EXPLOSION_LARGE, var16, var17, var14, 0.0, 0.0, 0.0);
      }

      return var8;
   }

   @Override
   public boolean damage(EnderDragonPart part, DamageSource source, float amount) {
      if (part != this.head) {
         amount = amount / 4.0F + 1.0F;
      }

      float var4 = this.yaw * (float) Math.PI / 180.0F;
      float var5 = MathHelper.sin(var4);
      float var6 = MathHelper.cos(var4);
      this.targetX = this.x + (double)(var5 * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
      this.targetY = this.y + (double)(this.random.nextFloat() * 3.0F) + 1.0;
      this.targetZ = this.z - (double)(var6 * 5.0F) + (double)((this.random.nextFloat() - 0.5F) * 2.0F);
      this.target = null;
      if (source.getAttacker() instanceof PlayerEntity || source.isExplosive()) {
         this.damageDragon(source, amount);
      }

      return true;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return false;
   }

   protected boolean damageDragon(DamageSource soure, float amount) {
      return super.damage(soure, amount);
   }

   @Override
   public void m_59lfywdxf() {
      this.remove();
   }

   @Override
   protected void tickPostDeath() {
      ++this.ticksSinceDeath;
      if (this.ticksSinceDeath >= 180 && this.ticksSinceDeath <= 200) {
         float var1 = (this.random.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.world.addParticle(ParticleType.EXPLOSION_HUGE, this.x + (double)var1, this.y + 2.0 + (double)var2, this.z + (double)var3, 0.0, 0.0, 0.0);
      }

      if (!this.world.isClient) {
         if (this.ticksSinceDeath > 150 && this.ticksSinceDeath % 5 == 0) {
            int var4 = 1000;

            while(var4 > 0) {
               int var6 = XpOrbEntity.roundSize(var4);
               var4 -= var6;
               this.world.addEntity(new XpOrbEntity(this.world, this.x, this.y, this.z, var6));
            }
         }

         if (this.ticksSinceDeath == 1) {
            this.world.doGlobalEvent(1018, new BlockPos(this), 0);
         }
      }

      this.move(0.0, 0.1F, 0.0);
      this.bodyYaw = this.yaw += 20.0F;
      if (this.ticksSinceDeath == 200 && !this.world.isClient) {
         int var5 = 2000;

         while(var5 > 0) {
            int var7 = XpOrbEntity.roundSize(var5);
            var5 -= var7;
            this.world.addEntity(new XpOrbEntity(this.world, this.x, this.y, this.z, var7));
         }

         this.createPortal(new BlockPos(this.x, 64.0, this.z));
         this.remove();
      }
   }

   private void createPortal(BlockPos x) {
      boolean var2 = true;
      double var3 = 12.25;
      double var5 = 6.25;

      for(int var7 = -1; var7 <= 32; ++var7) {
         for(int var8 = -4; var8 <= 4; ++var8) {
            for(int var9 = -4; var9 <= 4; ++var9) {
               double var10 = (double)(var8 * var8 + var9 * var9);
               if (!(var10 > 12.25)) {
                  BlockPos var12 = x.add(var8, var7, var9);
                  if (var7 < 0) {
                     if (var10 <= 6.25) {
                        this.world.setBlockState(var12, Blocks.BEDROCK.defaultState());
                     }
                  } else if (var7 > 0) {
                     this.world.setBlockState(var12, Blocks.AIR.defaultState());
                  } else if (var10 > 6.25) {
                     this.world.setBlockState(var12, Blocks.BEDROCK.defaultState());
                  } else {
                     this.world.setBlockState(var12, Blocks.END_PORTAL.defaultState());
                  }
               }
            }
         }
      }

      this.world.setBlockState(x, Blocks.BEDROCK.defaultState());
      this.world.setBlockState(x.up(), Blocks.BEDROCK.defaultState());
      BlockPos var13 = x.up(2);
      this.world.setBlockState(var13, Blocks.BEDROCK.defaultState());
      this.world.setBlockState(var13.west(), Blocks.TORCH.defaultState().set(TorchBlock.FACING, Direction.EAST));
      this.world.setBlockState(var13.east(), Blocks.TORCH.defaultState().set(TorchBlock.FACING, Direction.WEST));
      this.world.setBlockState(var13.north(), Blocks.TORCH.defaultState().set(TorchBlock.FACING, Direction.SOUTH));
      this.world.setBlockState(var13.south(), Blocks.TORCH.defaultState().set(TorchBlock.FACING, Direction.NORTH));
      this.world.setBlockState(x.up(3), Blocks.BEDROCK.defaultState());
      this.world.setBlockState(x.up(4), Blocks.DRAGON_EGG.defaultState());
   }

   @Override
   protected void checkDespawn() {
   }

   @Override
   public Entity[] getParts() {
      return this.parts;
   }

   @Override
   public boolean hasCollision() {
      return false;
   }

   @Override
   public World getWorld() {
      return this.world;
   }

   @Override
   protected String getAmbientSound() {
      return "mob.enderdragon.growl";
   }

   @Override
   protected String getHurtSound() {
      return "mob.enderdragon.hit";
   }

   @Override
   protected float getSoundVolume() {
      return 5.0F;
   }
}
