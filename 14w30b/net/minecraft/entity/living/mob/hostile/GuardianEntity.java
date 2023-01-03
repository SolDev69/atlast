package net.minecraft.entity.living.mob.hostile;

import com.google.common.base.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.FishingLootEntry;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MovementControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobEntitySwimNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.FishItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameEventS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class GuardianEntity extends HostileEntity {
   private float f_38whcpzwh;
   private float f_14aecfwov;
   private float f_53luqpilw;
   private float f_20gbbvhut;
   private float f_81xfdfvfs;
   private LivingEntity f_08rybxmga;
   private int f_78nisjdpq;
   private boolean f_38dgxcpyo;
   private WanderAroundGoal wanderGoal;

   public GuardianEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.experiencePoints = 10;
      this.setDimensions(0.85F, 0.85F);
      this.goalSelector.addGoal(4, new GuardianEntity.GuardianAttackEntityGoal(this));
      GoToWalkTargetGoal var2;
      this.goalSelector.addGoal(5, var2 = new GoToWalkTargetGoal(this, 1.0));
      this.goalSelector.addGoal(7, this.wanderGoal = new WanderAroundGoal(this, 1.0, 80));
      this.goalSelector.addGoal(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0F, 0.01F));
      this.goalSelector.addGoal(9, new LookAroundGoal(this));
      this.wanderGoal.setControls(3);
      var2.setControls(3);
      this.targetSelector.addGoal(1, new ActiveTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.GuardianTargetValidator(this)));
      this.movementControl = new GuardianEntity.GuardianMovementControl(this);
      this.f_14aecfwov = this.f_38whcpzwh = this.random.nextFloat();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(6.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.5);
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(16.0);
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(30.0);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setElder(nbt.getBoolean("Elder"));
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("Elder", this.isElder());
   }

   @Override
   protected EntityNavigation createNavigation(World world) {
      return new MobEntitySwimNavigation(this, world);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, 0);
      this.dataTracker.put(17, 0);
   }

   private boolean checkDataFlag(int value) {
      return (this.dataTracker.getInt(16) & value) != 0;
   }

   private void setDataFlagValue(int flag, boolean value) {
      int var3 = this.dataTracker.getInt(16);
      if (value) {
         this.dataTracker.update(16, var3 | flag);
      } else {
         this.dataTracker.update(16, var3 & ~flag);
      }
   }

   public boolean isMoving() {
      return this.checkDataFlag(2);
   }

   private void setMoving(boolean isMoving) {
      this.setDataFlagValue(2, isMoving);
   }

   public int getAttackChargeTime() {
      return this.isElder() ? 60 : 80;
   }

   public boolean isElder() {
      return this.checkDataFlag(4);
   }

   public void setElder(boolean isElder) {
      this.setDataFlagValue(4, isElder);
      if (isElder) {
         this.setDimensions(1.9975F, 1.9975F);
         this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
         this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(8.0);
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(80.0);
         this.setPersistent();
         this.wanderGoal.setGoalStartRngRange(400);
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public void m_28hbhdoxr() {
      this.setElder(true);
      this.f_81xfdfvfs = this.f_20gbbvhut = 1.0F;
   }

   private void m_71ywveaco(int i) {
      this.dataTracker.update(17, i);
   }

   public boolean m_16dqbnsqq() {
      return this.dataTracker.getInt(17) != 0;
   }

   public LivingEntity m_74mbxcnur() {
      if (!this.m_16dqbnsqq()) {
         return null;
      } else if (this.world.isClient) {
         if (this.f_08rybxmga != null) {
            return this.f_08rybxmga;
         } else {
            Entity var1 = this.world.getEntity(this.dataTracker.getInt(17));
            if (var1 instanceof LivingEntity) {
               this.f_08rybxmga = (LivingEntity)var1;
               return this.f_08rybxmga;
            } else {
               return null;
            }
         }
      } else {
         return this.getTargetEntity();
      }
   }

   @Override
   public void onDataValueChanged(int id) {
      super.onDataValueChanged(id);
      if (id == 16) {
         if (this.isElder() && this.width < 1.0F) {
            this.setDimensions(1.9975F, 1.9975F);
         }
      } else if (id == 17) {
         this.f_78nisjdpq = 0;
         this.f_08rybxmga = null;
      }
   }

   @Override
   public int getMinAmbientSoundDelay() {
      return 160;
   }

   @Override
   protected String getAmbientSound() {
      if (!this.isInWater()) {
         return "mob.guardian.land.idle";
      } else {
         return this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle";
      }
   }

   @Override
   protected String getHurtSound() {
      if (!this.isInWater()) {
         return "mob.guardian.land.hit";
      } else {
         return this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit";
      }
   }

   @Override
   protected String getDeathSound() {
      if (!this.isInWater()) {
         return "mob.guardian.land.death";
      } else {
         return this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death";
      }
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   public MobEntity.Environment m_84jincljh() {
      return MobEntity.Environment.IN_WATER;
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   @Override
   public float getPathfindingFavor(BlockPos x) {
      return this.world.getBlockState(x).getBlock().getMaterial() == Material.WATER ? 10.0F + this.world.getBrightness(x) - 0.5F : super.getPathfindingFavor(x);
   }

   @Override
   public void tickAI() {
      if (this.world.isClient) {
         this.f_14aecfwov = this.f_38whcpzwh;
         if (!this.isInWater()) {
            this.f_53luqpilw = 2.0F;
            if (this.velocityY > 0.0 && this.f_38dgxcpyo && !this.isSilent()) {
               this.world.playSound(this.x, this.y, this.z, "mob.guardian.flop", 1.0F, 1.0F, false);
            }

            this.f_38dgxcpyo = this.velocityY < 0.0 && this.world.isOpaqueFullCube(new BlockPos(this).down(), false);
         } else if (this.isMoving()) {
            if (this.f_53luqpilw < 0.5F) {
               this.f_53luqpilw = 4.0F;
            } else {
               this.f_53luqpilw += (0.5F - this.f_53luqpilw) * 0.1F;
            }
         } else {
            this.f_53luqpilw += (0.125F - this.f_53luqpilw) * 0.2F;
         }

         this.f_38whcpzwh += this.f_53luqpilw;
         this.f_81xfdfvfs = this.f_20gbbvhut;
         if (!this.isInWater()) {
            this.f_20gbbvhut = this.random.nextFloat();
         } else if (this.isMoving()) {
            this.f_20gbbvhut += (0.0F - this.f_20gbbvhut) * 0.25F;
         } else {
            this.f_20gbbvhut += (1.0F - this.f_20gbbvhut) * 0.06F;
         }

         if (this.isMoving() && this.isInWater()) {
            Vec3d var1 = this.m_01qqqsfds(0.0F);

            for(int var2 = 0; var2 < 2; ++var2) {
               this.world
                  .addParticle(
                     ParticleType.WATER_BUBBLE,
                     this.x + (this.random.nextDouble() - 0.5) * (double)this.width - var1.x * 1.5,
                     this.y + this.random.nextDouble() * (double)this.height - var1.y * 1.5,
                     this.z + (this.random.nextDouble() - 0.5) * (double)this.width - var1.z * 1.5,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (this.m_16dqbnsqq()) {
            if (this.f_78nisjdpq < this.getAttackChargeTime()) {
               ++this.f_78nisjdpq;
            }

            LivingEntity var14 = this.m_74mbxcnur();
            if (var14 != null) {
               this.getLookControl().setLookatValues(var14, 90.0F, 90.0F);
               this.getLookControl().tick();
               double var15 = (double)this.m_76qczvcqr(0.0F);
               double var4 = var14.x - this.x;
               double var6 = var14.y + (double)(var14.height * 0.5F) - (this.y + (double)this.getEyeHeight());
               double var8 = var14.z - this.z;
               double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
               var4 /= var10;
               var6 /= var10;
               var8 /= var10;
               double var12 = this.random.nextDouble();

               while(var12 < var10) {
                  var12 += 1.8 - var15 + this.random.nextDouble() * (1.7 - var15);
                  this.world
                     .addParticle(
                        ParticleType.WATER_BUBBLE,
                        this.x + var4 * var12,
                        this.y + var6 * var12 + (double)this.getEyeHeight(),
                        this.z + var8 * var12,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }
      }

      if (this.inWater) {
         this.setBreath(300);
      } else if (this.onGround) {
         this.velocityY += 0.5;
         this.velocityX += (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.velocityZ += (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.yaw = this.random.nextFloat() * 360.0F;
         this.onGround = false;
         this.velocityDirty = true;
      }

      if (this.m_16dqbnsqq()) {
         this.yaw = this.headYaw;
      }

      super.tickAI();
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float m_34qaovacf(float f) {
      return this.f_14aecfwov + (this.f_38whcpzwh - this.f_14aecfwov) * f;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float m_51aqfgnbj(float f) {
      return this.f_81xfdfvfs + (this.f_20gbbvhut - this.f_81xfdfvfs) * f;
   }

   public float m_76qczvcqr(float f) {
      return ((float)this.f_78nisjdpq + f) / (float)this.getAttackChargeTime();
   }

   @Override
   protected void m_45jbqtvrb() {
      super.m_45jbqtvrb();
      if (this.isElder()) {
         boolean var1 = true;
         boolean var2 = true;
         boolean var3 = true;
         boolean var4 = true;
         if ((this.time + this.getNetworkId()) % 1200 == 0) {
            StatusEffect var5 = StatusEffect.MINING_FAIGUE;

            for(ServerPlayerEntity var8 : this.world.getPlayers(ServerPlayerEntity.class, new Predicate() {
               public boolean apply(ServerPlayerEntity c_53mtutqhz) {
                  return GuardianEntity.this.getSquaredDistanceTo(c_53mtutqhz) < 2500.0 && c_53mtutqhz.interactionManager.isSurvival();
               }
            })) {
               if (!var8.hasStatusEffect(var5) || var8.getEffectInstance(var5).getAmplifier() < 2 || var8.getEffectInstance(var5).getDuration() < 1200) {
                  var8.networkHandler.sendPacket(new GameEventS2CPacket(10, 0.0F));
                  var8.addStatusEffect(new StatusEffectInstance(var5.id, 6000, 2));
               }
            }
         }

         if (!this.inVillage()) {
            this.setVillagePosAndRadius(new BlockPos(this), 16);
         }
      }
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3) + this.random.nextInt(lootingMultiplier + 1);
      if (var3 > 0) {
         this.dropItem(new ItemStack(Items.PRISMARINE_SHARD, var3, 0), 1.0F);
      }

      if (this.random.nextInt(3 + lootingMultiplier) > 1) {
         this.dropItem(new ItemStack(Items.FISH, 1, FishItem.Type.COD.getId()), 1.0F);
      } else if (this.random.nextInt(3 + lootingMultiplier) > 1) {
         this.dropItem(new ItemStack(Items.PRISMARINE_CRYSTALS, 1, 0), 1.0F);
      }

      if (allowDrops && this.isElder()) {
         this.dropItem(new ItemStack(Blocks.SPONGE, 1, 1), 1.0F);
      }
   }

   @Override
   protected void dropRareItem() {
      ItemStack var1 = ((FishingLootEntry)WeightedPicker.pick(this.random, FishingBobberEntity.getFishLoot())).getItemStack(this.random);
      this.dropItem(var1, 1.0F);
   }

   @Override
   protected boolean canSpawnAtLightLevel() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      return (this.random.nextInt(20) == 0 || !this.world.hasSkyAccessIgnoreLiquids(new BlockPos(this))) && super.canSpawn();
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (!this.isMoving() && !source.getMagic() && source.getSource() instanceof LivingEntity) {
         LivingEntity var3 = (LivingEntity)source.getSource();
         var3.damage(DamageSource.thorns(this), 2.0F);
         var3.playSound("damage.thorns", 0.5F, 1.0F);
      }

      this.wanderGoal.updateGoal();
      return super.damage(source, amount);
   }

   @Override
   public int getLookPitchSpeed() {
      return 180;
   }

   @Override
   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      if (this.isServer()) {
         if (this.isInWater()) {
            this.updateVelocity(sidewaysVelocity, forwardVelocity, 0.1F);
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= 0.9F;
            this.velocityY *= 0.9F;
            this.velocityZ *= 0.9F;
            if (!this.isMoving() && this.getTargetEntity() == null) {
               this.velocityY -= 0.005;
            }
         } else {
            super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
         }
      } else {
         super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
      }
   }

   static class GuardianAttackEntityGoal extends Goal {
      private GuardianEntity guardianEntity;
      private int attackChargeTicks;

      public GuardianAttackEntityGoal(GuardianEntity guardianEntity) {
         this.guardianEntity = guardianEntity;
         this.setControls(3);
      }

      @Override
      public boolean canStart() {
         LivingEntity var1 = this.guardianEntity.getTargetEntity();
         return var1 != null && var1.isAlive();
      }

      @Override
      public boolean shouldContinue() {
         return super.shouldContinue()
            && (this.guardianEntity.isElder() || this.guardianEntity.getSquaredDistanceTo(this.guardianEntity.getTargetEntity()) > 9.0);
      }

      @Override
      public void start() {
         this.attackChargeTicks = -10;
         this.guardianEntity.getNavigation().stopCurrentNavigation();
         this.guardianEntity.getLookControl().setLookatValues(this.guardianEntity.getTargetEntity(), 90.0F, 90.0F);
         this.guardianEntity.velocityDirty = true;
      }

      @Override
      public void stop() {
         this.guardianEntity.m_71ywveaco(0);
         this.guardianEntity.setAttackTarget(null);
         this.guardianEntity.wanderGoal.updateGoal();
      }

      @Override
      public void tick() {
         LivingEntity var1 = this.guardianEntity.getTargetEntity();
         this.guardianEntity.getNavigation().stopCurrentNavigation();
         this.guardianEntity.getLookControl().setLookatValues(var1, 90.0F, 90.0F);
         if (!this.guardianEntity.canSee(var1)) {
            this.guardianEntity.setAttackTarget(null);
         } else {
            ++this.attackChargeTicks;
            if (this.attackChargeTicks == 0) {
               this.guardianEntity.m_71ywveaco(this.guardianEntity.getTargetEntity().getNetworkId());
               this.guardianEntity.world.doEntityEvent(this.guardianEntity, (byte)21);
            } else if (this.attackChargeTicks >= this.guardianEntity.getAttackChargeTime()) {
               float var2 = 1.0F;
               if (this.guardianEntity.world.getDifficulty() == Difficulty.HARD) {
                  var2 += 2.0F;
               }

               if (this.guardianEntity.isElder()) {
                  var2 += 2.0F;
               }

               var1.damage(DamageSource.magic(this.guardianEntity, this.guardianEntity), var2);
               var1.damage(DamageSource.mob(this.guardianEntity), (float)this.guardianEntity.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).get());
               this.guardianEntity.setAttackTarget(null);
            } else if (this.attackChargeTicks >= 60 && this.attackChargeTicks % 20 == 0) {
            }

            super.tick();
         }
      }
   }

   static class GuardianMovementControl extends MovementControl {
      private GuardianEntity guardianEntity;

      public GuardianMovementControl(GuardianEntity guardianEntity) {
         super(guardianEntity);
         this.guardianEntity = guardianEntity;
      }

      @Override
      public void tickUpdateMovement() {
         if (this.updated && !this.guardianEntity.getNavigation().isIdle()) {
            double var1 = this.x - this.guardianEntity.x;
            double var3 = this.y - this.guardianEntity.y;
            double var5 = this.z - this.guardianEntity.z;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            var7 = (double)MathHelper.sqrt(var7);
            var3 /= var7;
            float var9 = (float)(Math.atan2(var5, var1) * 180.0 / (float) Math.PI) - 90.0F;
            this.guardianEntity.yaw = this.clampAndWrapAngle(this.guardianEntity.yaw, var9, 30.0F);
            this.guardianEntity.bodyYaw = this.guardianEntity.yaw;
            float var10 = (float)(this.speed * this.guardianEntity.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get());
            this.guardianEntity.setMovementSpeed(this.guardianEntity.getMovementSpeed() + (var10 - this.guardianEntity.getMovementSpeed()) * 0.125F);
            double var11 = Math.sin((double)(this.guardianEntity.time + this.guardianEntity.getNetworkId()) * 0.5) * 0.05;
            double var13 = Math.cos((double)(this.guardianEntity.yaw * (float) Math.PI / 180.0F));
            double var15 = Math.sin((double)(this.guardianEntity.yaw * (float) Math.PI / 180.0F));
            this.guardianEntity.velocityX += var11 * var13;
            this.guardianEntity.velocityZ += var11 * var15;
            var11 = Math.sin((double)(this.guardianEntity.time + this.guardianEntity.getNetworkId()) * 0.75) * 0.05;
            this.guardianEntity.velocityY += var11 * (var15 + var13) * 0.25;
            this.guardianEntity.velocityY += (double)this.guardianEntity.getMovementSpeed() * var3 * 0.1;
            LookControl var17 = this.guardianEntity.getLookControl();
            double var18 = this.guardianEntity.x + var1 / var7 * 2.0;
            double var20 = (double)this.guardianEntity.getEyeHeight() + this.guardianEntity.y + var3 / var7 * 1.0;
            double var22 = this.guardianEntity.z + var5 / var7 * 2.0;
            double var24 = var17.m_97ssyvlpp();
            double var26 = var17.m_41adiagvz();
            double var28 = var17.m_59fqsnxye();
            if (!var17.m_96bcvxfgw()) {
               var24 = var18;
               var26 = var20;
               var28 = var22;
            }

            this.guardianEntity
               .getLookControl()
               .lookAt(var24 + (var18 - var24) * 0.125, var26 + (var20 - var26) * 0.125, var28 + (var22 - var28) * 0.125, 10.0F, 40.0F);
            this.guardianEntity.setMoving(true);
         } else {
            this.guardianEntity.setMovementSpeed(0.0F);
            this.guardianEntity.setMoving(false);
         }
      }
   }

   static class GuardianTargetValidator implements Predicate {
      private GuardianEntity guardianEntity;

      public GuardianTargetValidator(GuardianEntity guardianEntity) {
         this.guardianEntity = guardianEntity;
      }

      public boolean apply(LivingEntity c_97zulxhng) {
         return (c_97zulxhng instanceof PlayerEntity || c_97zulxhng instanceof SquidEntity) && c_97zulxhng.getSquaredDistanceTo(this.guardianEntity) > 9.0;
      }
   }
}
