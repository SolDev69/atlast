package net.minecraft.entity.living.mob.hostile;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class CreeperEntity extends HostileEntity {
   private int lastFuseTime;
   private int currentFuseTime;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int mobHeadDropCount = 0;

   public CreeperEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new CreeperIgniteGoal(this));
      this.goalSelector.addGoal(2, this.fleeExplodingCreeperGoal);
      this.goalSelector.addGoal(3, new FleeEntityGoal(this, new Predicate() {
         public boolean apply(Entity c_47ldwddrb) {
            return c_47ldwddrb instanceof OcelotEntity;
         }
      }, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(2, new RevengeGoal(this, false));
      this.targetSelector.addGoal(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   public int getSafeFallDistance() {
      return this.getTargetEntity() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      super.applyFallDamage(distance, g);
      this.currentFuseTime = (int)((float)this.currentFuseTime + distance * 1.5F);
      if (this.currentFuseTime > this.fuseTime - 5) {
         this.currentFuseTime = this.fuseTime - 5;
      }
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)-1);
      this.dataTracker.put(17, (byte)0);
      this.dataTracker.put(18, (byte)0);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      if (this.dataTracker.getByte(17) == 1) {
         nbt.putBoolean("powered", true);
      }

      nbt.putShort("Fuse", (short)this.fuseTime);
      nbt.putByte("ExplosionRadius", (byte)this.explosionRadius);
      nbt.putBoolean("ignited", this.getIgnited());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.dataTracker.update(17, (byte)(nbt.getBoolean("powered") ? 1 : 0));
      if (nbt.isType("Fuse", 99)) {
         this.fuseTime = nbt.getShort("Fuse");
      }

      if (nbt.isType("ExplosionRadius", 99)) {
         this.explosionRadius = nbt.getByte("ExplosionRadius");
      }

      if (nbt.getBoolean("ignited")) {
         this.setIgnited();
      }
   }

   @Override
   public void tick() {
      if (this.isAlive()) {
         this.lastFuseTime = this.currentFuseTime;
         if (this.getIgnited()) {
            this.setFuseSpeed(1);
         }

         int var1 = this.getFuseSpeed();
         if (var1 > 0 && this.currentFuseTime == 0) {
            this.playSound("creeper.primed", 1.0F, 0.5F);
         }

         this.currentFuseTime += var1;
         if (this.currentFuseTime < 0) {
            this.currentFuseTime = 0;
         }

         if (this.currentFuseTime >= this.fuseTime) {
            this.currentFuseTime = this.fuseTime;
            this.explode();
         }
      }

      super.tick();
   }

   @Override
   protected String getHurtSound() {
      return "mob.creeper.say";
   }

   @Override
   protected String getDeathSound() {
      return "mob.creeper.death";
   }

   @Override
   public void onKilled(DamageSource source) {
      super.onKilled(source);
      if (source.getAttacker() instanceof SkeletonEntity) {
         int var2 = Item.getRawId(Items.RECORD_13);
         int var3 = Item.getRawId(Items.RECORD_WAIT);
         int var4 = var2 + this.random.nextInt(var3 - var2 + 1);
         this.dropItem(Item.byRawId(var4), 1);
      } else if (source.getAttacker() instanceof CreeperEntity
         && source.getAttacker() != this
         && ((CreeperEntity)source.getAttacker()).isCharged()
         && ((CreeperEntity)source.getAttacker()).shouldDropMobHead()) {
         ((CreeperEntity)source.getAttacker()).addMobHeadDrop();
         this.dropItem(new ItemStack(Items.SKULL, 1, 4), 0.0F);
      }
   }

   @Override
   public boolean attack(Entity entity) {
      return true;
   }

   public boolean isCharged() {
      return this.dataTracker.getByte(17) == 1;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getFuseTime(float timeDelta) {
      return ((float)this.lastFuseTime + (float)(this.currentFuseTime - this.lastFuseTime) * timeDelta) / (float)(this.fuseTime - 2);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.GUNPOWDER;
   }

   public int getFuseSpeed() {
      return this.dataTracker.getByte(16);
   }

   public void setFuseSpeed(int fuseSpeed) {
      this.dataTracker.update(16, (byte)fuseSpeed);
   }

   @Override
   public void onLightningStrike(LightningBoltEntity lightning) {
      super.onLightningStrike(lightning);
      this.dataTracker.update(17, (byte)1);
   }

   @Override
   protected boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.FLINT_AND_STEEL) {
         this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, "fire.ignite", 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
         player.swingHand();
         if (!this.world.isClient) {
            this.setIgnited();
            var2.damageAndBreak(1, player);
            return true;
         }
      }

      return super.canInteract(player);
   }

   private void explode() {
      if (!this.world.isClient) {
         boolean var1 = this.world.getGameRules().getBoolean("mobGriefing");
         if (this.isCharged()) {
            this.world.explode(this, this.x, this.y, this.z, (float)(this.explosionRadius * 2), var1);
         } else {
            this.world.explode(this, this.x, this.y, this.z, (float)this.explosionRadius, var1);
         }

         this.remove();
      }
   }

   public boolean getIgnited() {
      return this.dataTracker.getByte(18) != 0;
   }

   public void setIgnited() {
      this.dataTracker.update(18, (byte)1);
   }

   public boolean shouldDropMobHead() {
      return this.mobHeadDropCount < 1;
   }

   public void addMobHeadDrop() {
      ++this.mobHeadDropCount;
   }
}
