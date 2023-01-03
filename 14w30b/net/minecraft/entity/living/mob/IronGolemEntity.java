package net.minecraft.entity.living.mob;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoToEntityTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderThroughVillageAtNightGoal;
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
import net.minecraft.world.World;
import net.minecraft.world.village.Village;
import net.ornithemc.api.EnvType;

public class IronGolemEntity extends GolemEntity {
   private int mobTickCooldown;
   Village village;
   private int attackTicksLeft;
   private int lookingAtVillagerTicksLeft;

   public IronGolemEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(1.4F, 2.9F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(2, new GoToEntityTargetGoal(this, 0.9, 32.0F));
      this.goalSelector.addGoal(3, new WanderThroughVillageAtNightGoal(this, 0.6, true));
      this.goalSelector.addGoal(4, new GoToWalkTargetGoal(this, 1.0));
      this.goalSelector.addGoal(5, new IronGolemLookGoal(this));
      this.goalSelector.addGoal(6, new WanderAroundGoal(this, 0.6));
      this.goalSelector.addGoal(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new TrackIronGolemTargetGoal(this));
      this.targetSelector.addGoal(2, new RevengeGoal(this, false));
      this.targetSelector.addGoal(3, new ActiveTargetGoal(this, MobEntity.class, 10, false, true, Monster.VISIBLE_MONSTER_FILTER));
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)0);
   }

   @Override
   protected void m_45jbqtvrb() {
      if (--this.mobTickCooldown <= 0) {
         this.mobTickCooldown = 70 + this.random.nextInt(50);
         this.village = this.world.getVillageData().getClosestVillage(new BlockPos(this), 32);
         if (this.village == null) {
            this.resetVillageRadius();
         } else {
            BlockPos var1 = this.village.getCenter();
            this.setVillagePosAndRadius(var1, (int)((float)this.village.getRadius() * 0.6F));
         }
      }

      super.m_45jbqtvrb();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(100.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   protected int removeBreathUnderWater(int breath) {
      return breath;
   }

   @Override
   protected void pushAway(Entity entity) {
      if (entity instanceof Monster && this.getRandom().nextInt(20) == 0) {
         this.setAttackTarget((LivingEntity)entity);
      }

      super.pushAway(entity);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (this.attackTicksLeft > 0) {
         --this.attackTicksLeft;
      }

      if (this.lookingAtVillagerTicksLeft > 0) {
         --this.lookingAtVillagerTicksLeft;
      }

      if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 2.5000003E-7F && this.random.nextInt(5) == 0) {
         int var1 = MathHelper.floor(this.x);
         int var2 = MathHelper.floor(this.y - 0.2F);
         int var3 = MathHelper.floor(this.z);
         BlockState var4 = this.world.getBlockState(new BlockPos(var1, var2, var3));
         Block var5 = var4.getBlock();
         if (var5.getMaterial() != Material.AIR) {
            this.world
               .addParticle(
                  ParticleType.BLOCK_CRACK,
                  this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
                  this.getBoundingBox().minY + 0.1,
                  this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
                  4.0 * ((double)this.random.nextFloat() - 0.5),
                  0.5,
                  ((double)this.random.nextFloat() - 0.5) * 4.0,
                  Block.serialize(var4)
               );
         }
      }
   }

   @Override
   public boolean canAttackEntity(Class entityClass) {
      return this.isPlayerCreated() && PlayerEntity.class.isAssignableFrom(entityClass) ? false : super.canAttackEntity(entityClass);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("PlayerCreated", this.isPlayerCreated());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setPlayerCreated(nbt.getBoolean("PlayerCreated"));
   }

   @Override
   public boolean attack(Entity entity) {
      this.attackTicksLeft = 10;
      this.world.doEntityEvent(this, (byte)4);
      boolean var2 = entity.damage(DamageSource.mob(this), (float)(7 + this.random.nextInt(15)));
      if (var2) {
         entity.velocityY += 0.4F;
      }

      this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      return var2;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 4) {
         this.attackTicksLeft = 10;
         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      } else if (event == 11) {
         this.lookingAtVillagerTicksLeft = 400;
      } else {
         super.doEvent(event);
      }
   }

   public Village getVillage() {
      return this.village;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public int getAttackTicksLeft() {
      return this.attackTicksLeft;
   }

   public void setLookingAtVillager(boolean lookingAtVillager) {
      this.lookingAtVillagerTicksLeft = lookingAtVillager ? 400 : 0;
      this.world.doEntityEvent(this, (byte)11);
   }

   @Override
   protected String getHurtSound() {
      return "mob.irongolem.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.irongolem.death";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Item.byBlock(Blocks.RED_FLOWER), 1, (float)FlowerBlock.Type.POPPY.getIndex());
      }

      int var6 = 3 + this.random.nextInt(3);

      for(int var5 = 0; var5 < var6; ++var5) {
         this.dropItem(Items.IRON_INGOT, 1);
      }
   }

   public int getLookingAtVillagerTicks() {
      return this.lookingAtVillagerTicksLeft;
   }

   public boolean isPlayerCreated() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean playerCreated) {
      byte var2 = this.dataTracker.getByte(16);
      if (playerCreated) {
         this.dataTracker.update(16, (byte)(var2 | 1));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -2));
      }
   }

   @Override
   public void onKilled(DamageSource source) {
      if (!this.isPlayerCreated() && this.attackingPlayer != null && this.village != null) {
         this.village.updateReputation(this.attackingPlayer.getName(), -5);
      }

      super.onKilled(source);
   }
}
