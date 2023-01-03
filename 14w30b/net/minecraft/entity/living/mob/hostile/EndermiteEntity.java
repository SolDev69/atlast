package net.minecraft.entity.living.mob.hostile;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
   private int f_99echehzu = 0;
   private boolean f_78pqbblrf = false;

   public EndermiteEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.experiencePoints = 3;
      this.setDimensions(0.4F, 0.3F);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, PlayerEntity.class, 1.0, false));
      this.goalSelector.addGoal(3, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, true));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(8.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(2.0);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected String getAmbientSound() {
      return "mob.silverfish.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.silverfish.kill";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Item.byRawId(0);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.f_99echehzu = nbt.getInt("Lifetime");
      this.f_78pqbblrf = nbt.getBoolean("PlayerSpawned");
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("Lifetime", this.f_99echehzu);
      nbt.putBoolean("PlayerSpawned", this.f_78pqbblrf);
   }

   @Override
   public void tick() {
      this.bodyYaw = this.yaw;
      super.tick();
   }

   public boolean m_16conjvuj() {
      return this.f_78pqbblrf;
   }

   public void m_64qjbbsko(boolean bl) {
      this.f_78pqbblrf = bl;
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (this.world.isClient) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.world
               .addParticle(
                  ParticleType.PORTAL,
                  this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
                  this.y + this.random.nextDouble() * (double)this.height,
                  this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
                  (this.random.nextDouble() - 0.5) * 2.0,
                  -this.random.nextDouble(),
                  (this.random.nextDouble() - 0.5) * 2.0
               );
         }
      } else {
         if (!this.isPersistent()) {
            ++this.f_99echehzu;
         }

         if (this.f_99echehzu >= 2400) {
            this.remove();
         }
      }
   }

   @Override
   protected boolean canSpawnAtLightLevel() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      if (super.canSpawn()) {
         PlayerEntity var1 = this.world.getClosestPlayer(this, 5.0);
         return var1 == null;
      } else {
         return false;
      }
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.ARTHROPOD;
   }
}
