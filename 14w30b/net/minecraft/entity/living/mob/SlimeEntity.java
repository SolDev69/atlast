package net.minecraft.entity.living.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MobEntityActiveTargetGoal;
import net.minecraft.entity.ai.goal.MobEntityPlayerTargetGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.WorldGeneratorType;

public class SlimeEntity extends MobEntity implements Monster {
   public float targetStretch;
   public float stretch;
   public float lastStretch;
   private boolean wasOnGround;

   public SlimeEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.movementControl = new SlimeEntity.MovementControl(this);
      this.goalSelector.addGoal(1, new SlimeEntity.SwimAroundGoal(this));
      this.goalSelector.addGoal(2, new SlimeEntity.MoveTowardsTargetGoal(this));
      this.goalSelector.addGoal(3, new SlimeEntity.LeapTowardsTargetGoal(this));
      this.goalSelector.addGoal(5, new SlimeEntity.MoveAroundGoal(this));
      this.targetSelector.addGoal(1, new MobEntityPlayerTargetGoal(this));
      this.targetSelector.addGoal(3, new MobEntityActiveTargetGoal(this, IronGolemEntity.class));
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)1);
   }

   protected void setSize(int size) {
      this.dataTracker.update(16, (byte)size);
      this.setDimensions(0.51000005F * (float)size, 0.51000005F * (float)size);
      this.setPosition(this.x, this.y, this.z);
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase((double)(size * size));
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase((double)(0.2F + 0.1F * (float)size));
      this.setHealth(this.getMaxHealth());
      this.experiencePoints = size;
   }

   public int getSize() {
      return this.dataTracker.getByte(16);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("Size", this.getSize() - 1);
      nbt.putBoolean("wasOnGround", this.wasOnGround);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      int var2 = nbt.getInt("Size");
      if (var2 < 0) {
         var2 = 0;
      }

      this.setSize(var2 + 1);
      this.wasOnGround = nbt.getBoolean("wasOnGround");
   }

   protected ParticleType getParticleName() {
      return ParticleType.SLIME;
   }

   protected String getSoundName() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   public void tick() {
      if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
         this.removed = true;
      }

      this.stretch += (this.targetStretch - this.stretch) * 0.5F;
      this.lastStretch = this.stretch;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int var1 = this.getSize();

         for(int var2 = 0; var2 < var1 * 8; ++var2) {
            float var3 = this.random.nextFloat() * (float) Math.PI * 2.0F;
            float var4 = this.random.nextFloat() * 0.5F + 0.5F;
            float var5 = MathHelper.sin(var3) * (float)var1 * 0.5F * var4;
            float var6 = MathHelper.cos(var3) * (float)var1 * 0.5F * var4;
            World var10000 = this.world;
            ParticleType var10001 = this.getParticleName();
            double var10002 = this.x + (double)var5;
            double var10004 = this.z + (double)var6;
            var10000.addParticle(var10001, var10002, this.getBoundingBox().minY, var10004, 0.0, 0.0, 0.0);
         }

         if (this.makesLandSound()) {
            this.playSound(this.getSoundName(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.targetStretch = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.targetStretch = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.updateStretch();
   }

   protected void updateStretch() {
      this.targetStretch *= 0.6F;
   }

   protected int getTicksUntilNextJump() {
      return this.random.nextInt(20) + 10;
   }

   protected SlimeEntity getInstance() {
      return new SlimeEntity(this.world);
   }

   @Override
   public void onDataValueChanged(int id) {
      if (id == 16) {
         int var2 = this.getSize();
         this.setDimensions(0.51000005F * (float)var2, 0.51000005F * (float)var2);
         this.yaw = this.headYaw;
         this.bodyYaw = this.headYaw;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.m_72fqgmfka();
         }
      }

      super.onDataValueChanged(id);
   }

   @Override
   public void remove() {
      int var1 = this.getSize();
      if (!this.world.isClient && var1 > 1 && this.getHealth() <= 0.0F) {
         int var2 = 2 + this.random.nextInt(3);

         for(int var3 = 0; var3 < var2; ++var3) {
            float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
            float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
            SlimeEntity var6 = this.getInstance();
            var6.setSize(var1 / 2);
            var6.refreshPositionAndAngles(this.x + (double)var4, this.y + 0.5, this.z + (double)var5, this.random.nextFloat() * 360.0F, 0.0F);
            this.world.addEntity(var6);
         }
      }

      super.remove();
   }

   @Override
   public void push(Entity entity) {
      super.push(entity);
      if (entity instanceof IronGolemEntity && this.isBig()) {
         this.damageTargetEntity((LivingEntity)entity);
      }
   }

   @Override
   public void onPlayerCollision(PlayerEntity player) {
      if (this.isBig()) {
         this.damageTargetEntity(player);
      }
   }

   protected void damageTargetEntity(LivingEntity entity) {
      int var2 = this.getSize();
      if (this.canSee(entity)
         && this.getSquaredDistanceTo(entity) < 0.6 * (double)var2 * 0.6 * (double)var2
         && entity.damage(DamageSource.mob(this), (float)this.getDamageAmount())) {
         this.playSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   @Override
   public float getEyeHeight() {
      return 0.625F * this.height;
   }

   protected boolean isBig() {
      return this.getSize() > 1;
   }

   protected int getDamageAmount() {
      return this.getSize();
   }

   @Override
   protected String getHurtSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   protected String getDeathSound() {
      return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
   }

   @Override
   protected Item getDefaultDropLoot() {
      return this.getSize() == 1 ? Items.SLIME_BALL : Item.byRawId(0);
   }

   @Override
   public boolean canSpawn() {
      WorldChunk var1 = this.world.getChunk(new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z)));
      if (this.world.getData().getGeneratorType() == WorldGeneratorType.FLAT && this.random.nextInt(4) != 1) {
         return false;
      } else {
         if (this.getSize() == 1 || this.world.getDifficulty() != Difficulty.PEACEFUL) {
            Biome var2 = this.world.getBiome(new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z)));
            if (var2 == Biome.SWAMPLAND
               && this.y > 50.0
               && this.y < 70.0
               && this.random.nextFloat() < 0.5F
               && this.random.nextFloat() < this.world.getMoonSize()
               && this.world.getRawBrightness(new BlockPos(this)) <= this.random.nextInt(8)) {
               return super.canSpawn();
            }

            if (this.random.nextInt(10) == 0 && var1.getRandomForSlime(987234911L).nextInt(10) == 0 && this.y < 40.0) {
               return super.canSpawn();
            }
         }

         return false;
      }
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   @Override
   public int getLookPitchSpeed() {
      return 0;
   }

   protected boolean makesJumpSound() {
      return this.getSize() > 0;
   }

   protected boolean makesLandSound() {
      return this.getSize() > 2;
   }

   @Override
   protected void jump() {
      this.velocityY = 0.42F;
      this.velocityDirty = true;
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      int var3 = this.random.nextInt(3);
      if (var3 < 2 && this.random.nextFloat() < 0.5F * localDifficulty.getMultiplier()) {
         ++var3;
      }

      int var4 = 1 << var3;
      this.setSize(var4);
      return super.initialize(localDifficulty, entityData);
   }

   static class LeapTowardsTargetGoal extends Goal {
      private SlimeEntity entity;
      private float f_35wvpswiq;
      private int f_15cuqkmkc;

      public LeapTowardsTargetGoal(SlimeEntity entity) {
         this.entity = entity;
         this.setControls(2);
      }

      @Override
      public boolean canStart() {
         return this.entity.getTargetEntity() == null && (this.entity.onGround || this.entity.isInWater() || this.entity.isInLava());
      }

      @Override
      public void tick() {
         if (--this.f_15cuqkmkc <= 0) {
            this.f_15cuqkmkc = 40 + this.entity.getRandom().nextInt(60);
            this.f_35wvpswiq = (float)this.entity.getRandom().nextInt(360);
         }

         ((SlimeEntity.MovementControl)this.entity.getMovementControl()).m_60twucwwq(this.f_35wvpswiq, false);
      }
   }

   static class MoveAroundGoal extends Goal {
      private SlimeEntity entity;

      public MoveAroundGoal(SlimeEntity entity) {
         this.entity = entity;
         this.setControls(5);
      }

      @Override
      public boolean canStart() {
         return true;
      }

      @Override
      public void tick() {
         ((SlimeEntity.MovementControl)this.entity.getMovementControl()).m_12qcemquw(1.0);
      }
   }

   static class MoveTowardsTargetGoal extends Goal {
      private SlimeEntity slime;
      private int f_64jihivtj;

      public MoveTowardsTargetGoal(SlimeEntity entity) {
         this.slime = entity;
         this.setControls(2);
      }

      @Override
      public boolean canStart() {
         LivingEntity var1 = this.slime.getTargetEntity();
         if (var1 == null) {
            return false;
         } else {
            return var1.isAlive();
         }
      }

      @Override
      public void start() {
         this.f_64jihivtj = 300;
         super.start();
      }

      @Override
      public boolean shouldContinue() {
         LivingEntity var1 = this.slime.getTargetEntity();
         if (var1 == null) {
            return false;
         } else if (!var1.isAlive()) {
            return false;
         } else {
            return --this.f_64jihivtj > 0;
         }
      }

      @Override
      public void tick() {
         this.slime.lookAtEntity(this.slime.getTargetEntity(), 10.0F, 10.0F);
         ((SlimeEntity.MovementControl)this.slime.getMovementControl()).m_60twucwwq(this.slime.yaw, this.slime.isBig());
      }
   }

   static class MovementControl extends net.minecraft.entity.ai.control.MovementControl {
      private float f_90hjxvrag;
      private int jumpCooldownTicks;
      private SlimeEntity entity;
      private boolean hasTarget;

      public MovementControl(SlimeEntity entity) {
         super(entity);
         this.entity = entity;
      }

      public void m_60twucwwq(float f, boolean hasTarget) {
         this.f_90hjxvrag = f;
         this.hasTarget = hasTarget;
      }

      public void m_12qcemquw(double speed) {
         this.speed = speed;
         this.updated = true;
      }

      @Override
      public void tickUpdateMovement() {
         this.mob.yaw = this.clampAndWrapAngle(this.mob.yaw, this.f_90hjxvrag, 30.0F);
         this.mob.headYaw = this.mob.yaw;
         this.mob.bodyYaw = this.mob.yaw;
         if (!this.updated) {
            this.mob.setForwardVelocity(0.0F);
         } else {
            this.updated = false;
            if (this.mob.onGround) {
               this.mob.setMovementSpeed((float)(this.speed * this.mob.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get()));
               if (this.jumpCooldownTicks-- <= 0) {
                  this.jumpCooldownTicks = this.entity.getTicksUntilNextJump();
                  if (this.hasTarget) {
                     this.jumpCooldownTicks /= 3;
                  }

                  this.entity.getJumpControl().setActive();
                  if (this.entity.makesJumpSound()) {
                     this.entity
                        .playSound(
                           this.entity.getSoundName(),
                           this.entity.getSoundVolume(),
                           ((this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F
                        );
                  }
               } else {
                  this.entity.sidewaysSpeed = this.entity.forwardSpeed = 0.0F;
                  this.mob.setMovementSpeed(0.0F);
               }
            } else {
               this.mob.setMovementSpeed((float)(this.speed * this.mob.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get()));
            }
         }
      }
   }

   static class SwimAroundGoal extends Goal {
      private SlimeEntity entity;

      public SwimAroundGoal(SlimeEntity entity) {
         this.entity = entity;
         this.setControls(5);
         ((MobEntityNavigation)entity.getNavigation()).setCanSwim(true);
      }

      @Override
      public boolean canStart() {
         return this.entity.isInWater() || this.entity.isInLava();
      }

      @Override
      public void tick() {
         if (this.entity.getRandom().nextFloat() < 0.8F) {
            this.entity.getJumpControl().setActive();
         }

         ((SlimeEntity.MovementControl)this.entity.getMovementControl()).m_12qcemquw(1.2);
      }
   }
}
