package net.minecraft.entity.living.mob.hostile.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.RangedAttackMob;
import net.minecraft.entity.living.mob.hostile.HostileEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.EnvironmentInterface;
import net.ornithemc.api.EnvironmentInterfaces;

@EnvironmentInterfaces({@EnvironmentInterface(
   value = EnvType.CLIENT,
   itf = Boss.class
)})
public class WitherEntity extends HostileEntity implements Boss, RangedAttackMob {
   private float[] sideHeadPitches = new float[2];
   private float[] sideHeadYaws = new float[2];
   private float[] prevSideHeadPitches = new float[2];
   private float[] prevSideHeadYaws = new float[2];
   private int[] skullCooldowns = new int[2];
   private int[] chargedSkullCooldowns = new int[2];
   private int blockBreakingCooldown;
   private static final Predicate UNDEAD_FILTER = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof LivingEntity && ((LivingEntity)c_47ldwddrb).getMobType() != LivingEntityType.UNDEAD;
      }
   };

   public WitherEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setHealth(this.getMaxHealth());
      this.setDimensions(0.9F, 3.5F);
      this.immuneToFire = true;
      ((MobEntityNavigation)this.getNavigation()).setCanSwim(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(7, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, false));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, MobEntity.class, 0, false, false, UNDEAD_FILTER));
      this.experiencePoints = 50;
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(17, new Integer(0));
      this.dataTracker.put(18, new Integer(0));
      this.dataTracker.put(19, new Integer(0));
      this.dataTracker.put(20, new Integer(0));
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("Invul", this.getInvulerabilityTimer());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setInvulerabilityTimer(nbt.getInt("Invul"));
   }

   @Override
   protected String getAmbientSound() {
      return "mob.wither.idle";
   }

   @Override
   protected String getHurtSound() {
      return "mob.wither.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.wither.death";
   }

   @Override
   public void tickAI() {
      this.velocityY *= 0.6F;
      if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
         Entity var1 = this.world.getEntity(this.getTrackedEntityId(0));
         if (var1 != null) {
            if (this.y < var1.y || !this.isAtHalfHealth() && this.y < var1.y + 5.0) {
               if (this.velocityY < 0.0) {
                  this.velocityY = 0.0;
               }

               this.velocityY += (0.5 - this.velocityY) * 0.6F;
            }

            double var2 = var1.x - this.x;
            double var4 = var1.z - this.z;
            double var6 = var2 * var2 + var4 * var4;
            if (var6 > 9.0) {
               double var8 = (double)MathHelper.sqrt(var6);
               this.velocityX += (var2 / var8 * 0.5 - this.velocityX) * 0.6F;
               this.velocityZ += (var4 / var8 * 0.5 - this.velocityZ) * 0.6F;
            }
         }
      }

      if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.05F) {
         this.yaw = (float)Math.atan2(this.velocityZ, this.velocityX) * (180.0F / (float)Math.PI) - 90.0F;
      }

      super.tickAI();

      for(int var20 = 0; var20 < 2; ++var20) {
         this.prevSideHeadYaws[var20] = this.sideHeadYaws[var20];
         this.prevSideHeadPitches[var20] = this.sideHeadPitches[var20];
      }

      for(int var21 = 0; var21 < 2; ++var21) {
         int var23 = this.getTrackedEntityId(var21 + 1);
         Entity var3 = null;
         if (var23 > 0) {
            var3 = this.world.getEntity(var23);
         }

         if (var3 != null) {
            double var27 = this.getHeadX(var21 + 1);
            double var28 = this.getHeadY(var21 + 1);
            double var29 = this.getHeadZ(var21 + 1);
            double var10 = var3.x - var27;
            double var12 = var3.y + (double)var3.getEyeHeight() - var28;
            double var14 = var3.z - var29;
            double var16 = (double)MathHelper.sqrt(var10 * var10 + var14 * var14);
            float var18 = (float)(Math.atan2(var14, var10) * 180.0 / (float) Math.PI) - 90.0F;
            float var19 = (float)(-(Math.atan2(var12, var16) * 180.0 / (float) Math.PI));
            this.sideHeadPitches[var21] = this.getNextAngle(this.sideHeadPitches[var21], var19, 40.0F);
            this.sideHeadYaws[var21] = this.getNextAngle(this.sideHeadYaws[var21], var18, 10.0F);
         } else {
            this.sideHeadYaws[var21] = this.getNextAngle(this.sideHeadYaws[var21], this.bodyYaw, 10.0F);
         }
      }

      boolean var22 = this.isAtHalfHealth();

      for(int var24 = 0; var24 < 3; ++var24) {
         double var26 = this.getHeadX(var24);
         double var5 = this.getHeadY(var24);
         double var7 = this.getHeadZ(var24);
         this.world
            .addParticle(
               ParticleType.SMOKE_NORMAL,
               var26 + this.random.nextGaussian() * 0.3F,
               var5 + this.random.nextGaussian() * 0.3F,
               var7 + this.random.nextGaussian() * 0.3F,
               0.0,
               0.0,
               0.0
            );
         if (var22 && this.world.random.nextInt(4) == 0) {
            this.world
               .addParticle(
                  ParticleType.SPELL_MOB,
                  var26 + this.random.nextGaussian() * 0.3F,
                  var5 + this.random.nextGaussian() * 0.3F,
                  var7 + this.random.nextGaussian() * 0.3F,
                  0.7F,
                  0.7F,
                  0.5
               );
         }
      }

      if (this.getInvulerabilityTimer() > 0) {
         for(int var25 = 0; var25 < 3; ++var25) {
            this.world
               .addParticle(
                  ParticleType.SPELL_MOB,
                  this.x + this.random.nextGaussian() * 1.0,
                  this.y + (double)(this.random.nextFloat() * 3.3F),
                  this.z + this.random.nextGaussian() * 1.0,
                  0.7F,
                  0.7F,
                  0.9F
               );
         }
      }
   }

   @Override
   protected void m_45jbqtvrb() {
      if (this.getInvulerabilityTimer() > 0) {
         int var13 = this.getInvulerabilityTimer() - 1;
         if (var13 <= 0) {
            this.world.explode(this, this.x, this.y + (double)this.getEyeHeight(), this.z, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
            this.world.doGlobalEvent(1013, new BlockPos(this), 0);
         }

         this.setInvulerabilityTimer(var13);
         if (this.time % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.m_45jbqtvrb();

         for(int var1 = 1; var1 < 3; ++var1) {
            if (this.time >= this.skullCooldowns[var1 - 1]) {
               this.skullCooldowns[var1 - 1] = this.time + 10 + this.random.nextInt(10);
               if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD)
                  && this.chargedSkullCooldowns[var1 - 1]++ > 15) {
                  float var2 = 10.0F;
                  float var3 = 5.0F;
                  double var4 = MathHelper.nextDouble(this.random, this.x - (double)var2, this.x + (double)var2);
                  double var6 = MathHelper.nextDouble(this.random, this.y - (double)var3, this.y + (double)var3);
                  double var8 = MathHelper.nextDouble(this.random, this.z - (double)var2, this.z + (double)var2);
                  this.shootSkullAt(var1 + 1, var4, var6, var8, true);
                  this.chargedSkullCooldowns[var1 - 1] = 0;
               }

               int var14 = this.getTrackedEntityId(var1);
               if (var14 > 0) {
                  Entity var17 = this.world.getEntity(var14);
                  if (var17 != null && var17.isAlive() && !(this.getSquaredDistanceTo(var17) > 900.0) && this.canSee(var17)) {
                     this.shootSkullAt(var1 + 1, (LivingEntity)var17);
                     this.skullCooldowns[var1 - 1] = this.time + 40 + this.random.nextInt(20);
                     this.chargedSkullCooldowns[var1 - 1] = 0;
                  } else {
                     this.setTrackedEntityId(var1, 0);
                  }
               } else {
                  List var16 = this.world
                     .getEntities(LivingEntity.class, this.getBoundingBox().expand(20.0, 8.0, 20.0), Predicates.and(UNDEAD_FILTER, EntityFilter.NOT_SPECTATOR));

                  for(int var19 = 0; var19 < 10 && !var16.isEmpty(); ++var19) {
                     LivingEntity var5 = (LivingEntity)var16.get(this.random.nextInt(var16.size()));
                     if (var5 != this && var5.isAlive() && this.canSee(var5)) {
                        if (var5 instanceof PlayerEntity) {
                           if (!((PlayerEntity)var5).abilities.invulnerable) {
                              this.setTrackedEntityId(var1, var5.getNetworkId());
                           }
                        } else {
                           this.setTrackedEntityId(var1, var5.getNetworkId());
                        }
                        break;
                     }

                     var16.remove(var5);
                  }
               }
            }
         }

         if (this.getTargetEntity() != null) {
            this.setTrackedEntityId(0, this.getTargetEntity().getNetworkId());
         } else {
            this.setTrackedEntityId(0, 0);
         }

         if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            if (this.blockBreakingCooldown == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
               int var12 = MathHelper.floor(this.y);
               int var15 = MathHelper.floor(this.x);
               int var18 = MathHelper.floor(this.z);
               boolean var20 = false;

               for(int var21 = -1; var21 <= 1; ++var21) {
                  for(int var22 = -1; var22 <= 1; ++var22) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var23 = var15 + var21;
                        int var9 = var12 + var7;
                        int var10 = var18 + var22;
                        Block var11 = this.world.getBlockState(new BlockPos(var23, var9, var10)).getBlock();
                        if (var11.getMaterial() != Material.AIR
                           && var11 != Blocks.BEDROCK
                           && var11 != Blocks.END_PORTAL
                           && var11 != Blocks.END_PORTAL_FRAME
                           && var11 != Blocks.COMMAND_BLOCK
                           && var11 != Blocks.BARRIER) {
                           var20 = this.world.breakBlock(new BlockPos(var23, var9, var10), true) || var20;
                        }
                     }
                  }
               }

               if (var20) {
                  this.world.doEvent(null, 1012, new BlockPos(this), 0);
               }
            }
         }

         if (this.time % 20 == 0) {
            this.heal(1.0F);
         }
      }
   }

   public void onSummoned() {
      this.setInvulerabilityTimer(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   @Override
   public void onCobwebCollision() {
   }

   @Override
   public int getArmorProtection() {
      return 4;
   }

   private double getHeadX(int headIndex) {
      if (headIndex <= 0) {
         return this.x;
      } else {
         float var2 = (this.bodyYaw + (float)(180 * (headIndex - 1))) / 180.0F * (float) Math.PI;
         float var3 = MathHelper.cos(var2);
         return this.x + (double)var3 * 1.3;
      }
   }

   private double getHeadY(int headIndec) {
      return headIndec <= 0 ? this.y + 3.0 : this.y + 2.2;
   }

   private double getHeadZ(int headIndex) {
      if (headIndex <= 0) {
         return this.z;
      } else {
         float var2 = (this.bodyYaw + (float)(180 * (headIndex - 1))) / 180.0F * (float) Math.PI;
         float var3 = MathHelper.sin(var2);
         return this.z + (double)var3 * 1.3;
      }
   }

   private float getNextAngle(float prevAngle, float desiredAngle, float maxDelta) {
      float var4 = MathHelper.wrapDegrees(desiredAngle - prevAngle);
      if (var4 > maxDelta) {
         var4 = maxDelta;
      }

      if (var4 < -maxDelta) {
         var4 = -maxDelta;
      }

      return prevAngle + var4;
   }

   private void shootSkullAt(int headIndex, LivingEntity target) {
      this.shootSkullAt(headIndex, target.x, target.y + (double)target.getEyeHeight() * 0.5, target.z, headIndex == 0 && this.random.nextFloat() < 0.001F);
   }

   private void shootSkullAt(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
      this.world.doEvent(null, 1014, new BlockPos(this), 0);
      double var9 = this.getHeadX(headIndex);
      double var11 = this.getHeadY(headIndex);
      double var13 = this.getHeadZ(headIndex);
      double var15 = targetX - var9;
      double var17 = targetY - var11;
      double var19 = targetZ - var13;
      WitherSkullEntity var21 = new WitherSkullEntity(this.world, this, var15, var17, var19);
      if (charged) {
         var21.setCharged(true);
      }

      var21.y = var11;
      var21.x = var9;
      var21.z = var13;
      this.world.addEntity(var21);
   }

   @Override
   public void doRangedAttack(LivingEntity target, float range) {
      this.shootSkullAt(0, target);
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
         return false;
      } else if (this.getInvulerabilityTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
         return false;
      } else {
         if (this.isAtHalfHealth()) {
            Entity var3 = source.getSource();
            if (var3 instanceof ArrowEntity) {
               return false;
            }
         }

         Entity var5 = source.getAttacker();
         if (var5 != null && !(var5 instanceof PlayerEntity) && var5 instanceof LivingEntity && ((LivingEntity)var5).getMobType() == this.getMobType()) {
            return false;
         } else {
            if (this.blockBreakingCooldown <= 0) {
               this.blockBreakingCooldown = 20;
            }

            for(int var4 = 0; var4 < this.chargedSkullCooldowns.length; ++var4) {
               this.chargedSkullCooldowns[var4] += 3;
            }

            return super.damage(source, amount);
         }
      }
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      ItemEntity var3 = this.dropItem(Items.NETHER_STAR, 1);
      if (var3 != null) {
         var3.m_23igftynk();
      }

      if (!this.world.isClient) {
         for(PlayerEntity var5 : this.world.getEntities(PlayerEntity.class, this.getBoundingBox().expand(50.0, 100.0, 50.0))) {
            var5.incrementStat(Achievements.KILL_WITHER);
         }
      }
   }

   @Override
   protected void checkDespawn() {
      this.despawnTicks = 0;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return 15728880;
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   public void addStatusEffect(StatusEffectInstance instance) {
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(300.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.6F);
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(40.0);
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getHeadYaw(int id) {
      return this.sideHeadYaws[id];
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getHeadPitch(int id) {
      return this.sideHeadPitches[id];
   }

   public int getInvulerabilityTimer() {
      return this.dataTracker.getInt(20);
   }

   public void setInvulerabilityTimer(int properrty) {
      this.dataTracker.update(20, properrty);
   }

   public int getTrackedEntityId(int id) {
      return this.dataTracker.getInt(17 + id);
   }

   public void setTrackedEntityId(int headIndex, int id) {
      this.dataTracker.update(17 + headIndex, id);
   }

   public boolean isAtHalfHealth() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.UNDEAD;
   }

   @Override
   public void startRiding(Entity entity) {
      this.vehicle = null;
   }
}
