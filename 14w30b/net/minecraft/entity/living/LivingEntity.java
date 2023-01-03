package net.minecraft.entity.living;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.living.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributeContainer;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttribute;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtShort;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPickupS2CPacket;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class LivingEntity extends Entity {
   private static final UUID SPRINTING_SPEED_MODIFIER_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier SPRINTING_SPEED_MODIFIER = new AttributeModifier(SPRINTING_SPEED_MODIFIER_ID, "Sprinting speed boost", 0.3F, 2)
      .setSerialized(false);
   private AbstractEntityAttributeContainer attributes;
   private final DamageTracker damageTracker = new DamageTracker(this);
   private final Map statusEffects = Maps.newHashMap();
   private final ItemStack[] equippedItems = new ItemStack[5];
   public boolean handSwinging;
   public int handSwingTicks;
   public int arrowDespawnTimer;
   public float lastHealth;
   public int hurtTimer;
   public int hurtAnimationTicks;
   public float knockbackVelocity;
   public int deathTicks;
   public float lastHandSwingProgress;
   public float handSwingProgress;
   public float prevHandSwingAmount;
   public float handSwingAmount;
   public float handSwing;
   public int defaultMaxHealth = 20;
   public float prevCameraPitch;
   public float cameraPitch;
   public float randomLargeSeed;
   public float randomSmallSeed;
   public float bodyYaw;
   public float prevBodyYaw;
   public float headYaw;
   public float prevHeadYaw;
   public float airSpeed = 0.02F;
   protected PlayerEntity attackingPlayer;
   protected int playerHitTimer;
   protected boolean dead;
   protected int despawnTicks;
   protected float prevStepBobbingAmount;
   protected float stepBobbingAmount;
   protected float distanceTravelled;
   protected float prevDistanceTravelled;
   protected float halfCircleDegrees;
   protected int mobValue;
   protected float damageAmount;
   protected boolean jumping;
   public float sidewaysSpeed;
   public float forwardSpeed;
   protected float randomYaw;
   protected int bodyTrackingIncrements;
   protected double serverX;
   protected double serverY;
   protected double serverZ;
   protected double serverYaw;
   protected double serverPitch;
   private boolean effectsChanged = true;
   private LivingEntity attacker;
   private int lastAttackedTime;
   private LivingEntity attackTarget;
   private int lastAttackTime;
   private float movementSpeed;
   private int jumpingCooldown;
   private float absorption;

   @Override
   public void m_59lfywdxf() {
      this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
   }

   public LivingEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.initAttributes();
      this.setHealth(this.getMaxHealth());
      this.blocksBuilding = true;
      this.randomSmallSeed = (float)((Math.random() + 1.0) * 0.01F);
      this.setPosition(this.x, this.y, this.z);
      this.randomLargeSeed = (float)Math.random() * 12398.0F;
      this.yaw = (float)(Math.random() * (float) Math.PI * 2.0);
      this.headYaw = this.yaw;
      this.stepHeight = 0.5F;
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(7, 0);
      this.dataTracker.put(8, (byte)0);
      this.dataTracker.put(9, (byte)0);
      this.dataTracker.put(6, 1.0F);
   }

   protected void initAttributes() {
      this.getAttributes().registerAttribute(EntityAttributes.MAX_HEALTH);
      this.getAttributes().registerAttribute(EntityAttributes.KNOCKBACK_RESISTANCE);
      this.getAttributes().registerAttribute(EntityAttributes.MOVEMENT_SPEED);
   }

   @Override
   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
      if (!this.isInWater()) {
         this.checkWaterCollision();
      }

      if (!this.world.isClient && this.fallDistance > 3.0F && landed) {
         BlockState var6 = this.world.getBlockState(pos);
         Block var7 = var6.getBlock();
         float var8 = (float)MathHelper.ceil(this.fallDistance - 3.0F);
         if (var7.getMaterial() != Material.AIR) {
            double var9 = (double)Math.min(0.2F + var8 / 15.0F, 10.0F);
            if (var9 > 2.5) {
               var9 = 2.5;
            }

            int var11 = (int)(150.0 * var9);
            ((ServerWorld)this.world).addParticle(ParticleType.BLOCK_DUST, this.x, this.y, this.z, var11, 0.0, 0.0, 0.0, 0.15F, Block.serialize(var6));
         }
      }

      super.onFall(dy, landed, block, pos);
   }

   public boolean isWaterCreature() {
      return false;
   }

   @Override
   public void baseTick() {
      this.lastHandSwingProgress = this.handSwingProgress;
      super.baseTick();
      this.world.profiler.push("livingEntityBaseTick");
      if (this.isAlive()) {
         if (this.isInWall()) {
            this.damage(DamageSource.IN_WALL, 1.0F);
         } else if (!this.world.getWorldBorder().contains(this.getBoundingBox())) {
            double var1 = this.world.getWorldBorder().getDistanceFrom(this) + this.world.getWorldBorder().getSafeZone();
            if (var1 < 0.0) {
               this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-var1 * this.world.getWorldBorder().getDamagePerBlock())));
            }
         }
      }

      if (this.isImmuneToFire() || this.world.isClient) {
         this.extinguish();
      }

      boolean var6 = this instanceof PlayerEntity && ((PlayerEntity)this).abilities.invulnerable;
      if (this.isAlive() && this.isSubmergedIn(Material.WATER)) {
         if (!this.isWaterCreature() && !this.hasStatusEffect(StatusEffect.WATER_BREATHING.id) && !var6) {
            this.setBreath(this.removeBreathUnderWater(this.getBreath()));
            if (this.getBreath() == -20) {
               this.setBreath(0);

               for(int var2 = 0; var2 < 8; ++var2) {
                  float var3 = this.random.nextFloat() - this.random.nextFloat();
                  float var4 = this.random.nextFloat() - this.random.nextFloat();
                  float var5 = this.random.nextFloat() - this.random.nextFloat();
                  this.world
                     .addParticle(
                        ParticleType.WATER_BUBBLE,
                        this.x + (double)var3,
                        this.y + (double)var4,
                        this.z + (double)var5,
                        this.velocityX,
                        this.velocityY,
                        this.velocityZ
                     );
               }

               this.damage(DamageSource.DROWN, 2.0F);
            }
         }

         if (!this.world.isClient && this.hasVehicle() && this.vehicle instanceof LivingEntity) {
            this.startRiding(null);
         }
      } else {
         this.setBreath(300);
      }

      if (this.isAlive() && this.isWet()) {
         this.extinguish();
      }

      this.prevCameraPitch = this.cameraPitch;
      if (this.hurtTimer > 0) {
         --this.hurtTimer;
      }

      if (this.maxHealth > 0 && !(this instanceof ServerPlayerEntity)) {
         --this.maxHealth;
      }

      if (this.getHealth() <= 0.0F) {
         this.tickPostDeath();
      }

      if (this.playerHitTimer > 0) {
         --this.playerHitTimer;
      } else {
         this.attackingPlayer = null;
      }

      if (this.attackTarget != null && !this.attackTarget.isAlive()) {
         this.attackTarget = null;
      }

      if (this.attacker != null) {
         if (!this.attacker.isAlive()) {
            this.setAttacker(null);
         } else if (this.time - this.lastAttackedTime > 100) {
            this.setAttacker(null);
         }
      }

      this.tickStatusEffects();
      this.prevDistanceTravelled = this.distanceTravelled;
      this.prevBodyYaw = this.bodyYaw;
      this.prevHeadYaw = this.headYaw;
      this.prevYaw = this.yaw;
      this.prevPitch = this.pitch;
      this.world.profiler.pop();
   }

   public boolean isBaby() {
      return false;
   }

   protected void tickPostDeath() {
      ++this.deathTicks;
      if (this.deathTicks == 20) {
         if (!this.world.isClient && (this.playerHitTimer > 0 || this.shouldDropXp()) && this.isGrownUp() && this.world.getGameRules().getBoolean("doMobLoot")) {
            int var1 = this.getXpDrop(this.attackingPlayer);

            while(var1 > 0) {
               int var2 = XpOrbEntity.roundSize(var1);
               var1 -= var2;
               this.world.addEntity(new XpOrbEntity(this.world, this.x, this.y, this.z, var2));
            }
         }

         this.remove();

         for(int var8 = 0; var8 < 20; ++var8) {
            double var9 = this.random.nextGaussian() * 0.02;
            double var4 = this.random.nextGaussian() * 0.02;
            double var6 = this.random.nextGaussian() * 0.02;
            this.world
               .addParticle(
                  ParticleType.EXPLOSION_NORMAL,
                  this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  this.y + (double)(this.random.nextFloat() * this.height),
                  this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  var9,
                  var4,
                  var6
               );
         }
      }
   }

   protected boolean isGrownUp() {
      return !this.isBaby();
   }

   protected int removeBreathUnderWater(int breath) {
      int var2 = EnchantmentHelper.getRespirationLevel(this);
      return var2 > 0 && this.random.nextInt(var2 + 1) > 0 ? breath : breath - 1;
   }

   protected int getXpDrop(PlayerEntity playerEntity) {
      return 0;
   }

   protected boolean shouldDropXp() {
      return false;
   }

   public Random getRandom() {
      return this.random;
   }

   public LivingEntity getAttacker() {
      return this.attacker;
   }

   public int getLastAttackedTime() {
      return this.lastAttackedTime;
   }

   public void setAttacker(LivingEntity attacker) {
      this.attacker = attacker;
      this.lastAttackedTime = this.time;
   }

   public LivingEntity getAttackTarget() {
      return this.attackTarget;
   }

   public int getLastAttackTime() {
      return this.lastAttackTime;
   }

   public void setAttackTarget(Entity target) {
      if (target instanceof LivingEntity) {
         this.attackTarget = (LivingEntity)target;
      } else {
         this.attackTarget = null;
      }

      this.lastAttackTime = this.time;
   }

   public int getDespawnTimer() {
      return this.despawnTicks;
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putFloat("HealF", this.getHealth());
      nbt.putShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
      nbt.putShort("HurtTime", (short)this.hurtTimer);
      nbt.putInt("HurtByTimestamp", this.lastAttackedTime);
      nbt.putShort("DeathTime", (short)this.deathTicks);
      nbt.putFloat("AbsorptionAmount", this.getAbsorption());

      for(ItemStack var5 : this.getEquipmentStacks()) {
         if (var5 != null) {
            this.attributes.removeModifiers(var5.getAttributeModifiers());
         }
      }

      nbt.put("Attributes", EntityAttributes.toNbt(this.getAttributes()));

      for(ItemStack var12 : this.getEquipmentStacks()) {
         if (var12 != null) {
            this.attributes.addModifiers(var12.getAttributeModifiers());
         }
      }

      if (!this.statusEffects.isEmpty()) {
         NbtList var7 = new NbtList();

         for(StatusEffectInstance var11 : this.statusEffects.values()) {
            var7.add(var11.toNbt(new NbtCompound()));
         }

         nbt.put("ActiveEffects", var7);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.setAbsorption(nbt.getFloat("AbsorptionAmount"));
      if (nbt.isType("Attributes", 9) && this.world != null && !this.world.isClient) {
         EntityAttributes.readNbt(this.getAttributes(), nbt.getList("Attributes", 10));
      }

      if (nbt.isType("ActiveEffects", 9)) {
         NbtList var2 = nbt.getList("ActiveEffects", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            NbtCompound var4 = var2.getCompound(var3);
            StatusEffectInstance var5 = StatusEffectInstance.fromNbt(var4);
            if (var5 != null) {
               this.statusEffects.put(var5.getId(), var5);
            }
         }
      }

      if (nbt.isType("HealF", 99)) {
         this.setHealth(nbt.getFloat("HealF"));
      } else {
         NbtElement var6 = nbt.get("Health");
         if (var6 == null) {
            this.setHealth(this.getMaxHealth());
         } else if (var6.getType() == 5) {
            this.setHealth(((NbtFloat)var6).getFloat());
         } else if (var6.getType() == 2) {
            this.setHealth((float)((NbtShort)var6).getShort());
         }
      }

      this.hurtTimer = nbt.getShort("HurtTime");
      this.deathTicks = nbt.getShort("DeathTime");
      this.lastAttackedTime = nbt.getInt("HurtByTimestamp");
   }

   protected void tickStatusEffects() {
      Iterator var1 = this.statusEffects.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         StatusEffectInstance var3 = (StatusEffectInstance)this.statusEffects.get(var2);
         if (!var3.tick(this)) {
            if (!this.world.isClient) {
               var1.remove();
               this.onStatusEffectRemoved(var3);
            }
         } else if (var3.getDuration() % 600 == 0) {
            this.onStatusEffectUpgraded(var3, false);
         }
      }

      if (this.effectsChanged) {
         if (!this.world.isClient) {
            this.updateVisibility();
         }

         this.effectsChanged = false;
      }

      int var11 = this.dataTracker.getInt(7);
      boolean var12 = this.dataTracker.getByte(8) > 0;
      if (var11 > 0) {
         boolean var4 = false;
         if (!this.isInvisible()) {
            var4 = this.random.nextBoolean();
         } else {
            var4 = this.random.nextInt(15) == 0;
         }

         if (var12) {
            var4 &= this.random.nextInt(5) == 0;
         }

         if (var4 && var11 > 0) {
            double var5 = (double)(var11 >> 16 & 0xFF) / 255.0;
            double var7 = (double)(var11 >> 8 & 0xFF) / 255.0;
            double var9 = (double)(var11 >> 0 & 0xFF) / 255.0;
            this.world
               .addParticle(
                  var12 ? ParticleType.SPELL_MOB_AMBIENT : ParticleType.SPELL_MOB,
                  this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
                  this.y + this.random.nextDouble() * (double)this.height,
                  this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
                  var5,
                  var7,
                  var9
               );
         }
      }
   }

   protected void updateVisibility() {
      if (this.statusEffects.isEmpty()) {
         this.clearEffectParticles();
         this.setInvisible(false);
      } else {
         int var1 = PotionHelper.getColor(this.statusEffects.values());
         this.dataTracker.update(8, Byte.valueOf((byte)(PotionHelper.isAllAmbient(this.statusEffects.values()) ? 1 : 0)));
         this.dataTracker.update(7, var1);
         this.setInvisible(this.hasStatusEffect(StatusEffect.INVISIBILITY.id));
      }
   }

   protected void clearEffectParticles() {
      this.dataTracker.update(8, (byte)0);
      this.dataTracker.update(7, 0);
   }

   public void clearStatusEffects() {
      Iterator var1 = this.statusEffects.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         StatusEffectInstance var3 = (StatusEffectInstance)this.statusEffects.get(var2);
         if (!this.world.isClient) {
            var1.remove();
            this.onStatusEffectRemoved(var3);
         }
      }
   }

   public Collection getStatusEffects() {
      return this.statusEffects.values();
   }

   public boolean hasStatusEffect(int id) {
      return this.statusEffects.containsKey(id);
   }

   public boolean hasStatusEffect(StatusEffect effect) {
      return this.statusEffects.containsKey(effect.id);
   }

   public StatusEffectInstance getEffectInstance(StatusEffect effect) {
      return (StatusEffectInstance)this.statusEffects.get(effect.id);
   }

   public void addStatusEffect(StatusEffectInstance instance) {
      if (this.canHaveStatusEffect(instance)) {
         if (this.statusEffects.containsKey(instance.getId())) {
            ((StatusEffectInstance)this.statusEffects.get(instance.getId())).combine(instance);
            this.onStatusEffectUpgraded((StatusEffectInstance)this.statusEffects.get(instance.getId()), true);
         } else {
            this.statusEffects.put(instance.getId(), instance);
            this.onStatusEffectApplied(instance);
         }
      }
   }

   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      if (this.getMobType() == LivingEntityType.UNDEAD) {
         int var2 = effect.getId();
         if (var2 == StatusEffect.REGENERATION.id || var2 == StatusEffect.POISON.id) {
            return false;
         }
      }

      return true;
   }

   public boolean isAffectedBySmite() {
      return this.getMobType() == LivingEntityType.UNDEAD;
   }

   @Environment(EnvType.CLIENT)
   public void removeEffect(int id) {
      this.statusEffects.remove(id);
   }

   public void removeStatusEffect(int id) {
      StatusEffectInstance var2 = (StatusEffectInstance)this.statusEffects.remove(id);
      if (var2 != null) {
         this.onStatusEffectRemoved(var2);
      }
   }

   protected void onStatusEffectApplied(StatusEffectInstance instance) {
      this.effectsChanged = true;
      if (!this.world.isClient) {
         StatusEffect.BY_ID[instance.getId()].addModifiers(this, this.getAttributes(), instance.getAmplifier());
      }
   }

   protected void onStatusEffectUpgraded(StatusEffectInstance instance, boolean timerRanOut) {
      this.effectsChanged = true;
      if (timerRanOut && !this.world.isClient) {
         StatusEffect.BY_ID[instance.getId()].removeModifiers(this, this.getAttributes(), instance.getAmplifier());
         StatusEffect.BY_ID[instance.getId()].addModifiers(this, this.getAttributes(), instance.getAmplifier());
      }
   }

   protected void onStatusEffectRemoved(StatusEffectInstance effect) {
      this.effectsChanged = true;
      if (!this.world.isClient) {
         StatusEffect.BY_ID[effect.getId()].removeModifiers(this, this.getAttributes(), effect.getAmplifier());
      }
   }

   public void heal(float amount) {
      float var2 = this.getHealth();
      if (var2 > 0.0F) {
         this.setHealth(var2 + amount);
      }
   }

   public final float getHealth() {
      return this.dataTracker.getFloat(6);
   }

   public void setHealth(float amount) {
      this.dataTracker.update(6, MathHelper.clamp(amount, 0.0F, this.getMaxHealth()));
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (this.world.isClient) {
         return false;
      } else {
         this.despawnTicks = 0;
         if (this.getHealth() <= 0.0F) {
            return false;
         } else if (source.isFire() && this.hasStatusEffect(StatusEffect.FIRE_RESISTANCE)) {
            return false;
         } else {
            if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && this.getStackInInventory(4) != null) {
               this.getStackInInventory(4).damageAndBreak((int)(amount * 4.0F + this.random.nextFloat() * amount * 2.0F), this);
               amount *= 0.75F;
            }

            this.handSwingAmount = 1.5F;
            boolean var3 = true;
            if ((float)this.maxHealth > (float)this.defaultMaxHealth / 2.0F) {
               if (amount <= this.damageAmount) {
                  return false;
               }

               this.applyDamage(source, amount - this.damageAmount);
               this.damageAmount = amount;
               var3 = false;
            } else {
               this.damageAmount = amount;
               this.lastHealth = this.getHealth();
               this.maxHealth = this.defaultMaxHealth;
               this.applyDamage(source, amount);
               this.hurtTimer = this.hurtAnimationTicks = 10;
            }

            this.knockbackVelocity = 0.0F;
            Entity var4 = source.getAttacker();
            if (var4 != null) {
               if (var4 instanceof LivingEntity) {
                  this.setAttacker((LivingEntity)var4);
               }

               if (var4 instanceof PlayerEntity) {
                  this.playerHitTimer = 100;
                  this.attackingPlayer = (PlayerEntity)var4;
               } else if (var4 instanceof WolfEntity) {
                  WolfEntity var5 = (WolfEntity)var4;
                  if (var5.isTamed()) {
                     this.playerHitTimer = 100;
                     this.attackingPlayer = null;
                  }
               }
            }

            if (var3) {
               this.world.doEntityEvent(this, (byte)2);
               if (source != DamageSource.DROWN) {
                  this.onDamaged();
               }

               if (var4 != null) {
                  double var9 = var4.x - this.x;

                  double var7;
                  for(var7 = var4.z - this.z; var9 * var9 + var7 * var7 < 1.0E-4; var7 = (Math.random() - Math.random()) * 0.01) {
                     var9 = (Math.random() - Math.random()) * 0.01;
                  }

                  this.knockbackVelocity = (float)(Math.atan2(var7, var9) * 180.0 / (float) Math.PI - (double)this.yaw);
                  this.applyKnockback(var4, amount, var9, var7);
               } else {
                  this.knockbackVelocity = (float)((int)(Math.random() * 2.0) * 180);
               }
            }

            if (this.getHealth() <= 0.0F) {
               String var10 = this.getDeathSound();
               if (var3 && var10 != null) {
                  this.playSound(var10, this.getSoundVolume(), this.getSoundPitch());
               }

               this.onKilled(source);
            } else {
               String var11 = this.getHurtSound();
               if (var3 && var11 != null) {
                  this.playSound(var11, this.getSoundVolume(), this.getSoundPitch());
               }
            }

            return true;
         }
      }
   }

   public void renderBrokenItem(ItemStack itemStack) {
      this.playSound("random.break", 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

      for(int var2 = 0; var2 < 5; ++var2) {
         Vec3d var3 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         var3 = var3.rotateX(-this.pitch * (float) Math.PI / 180.0F);
         var3 = var3.rotateY(-this.yaw * (float) Math.PI / 180.0F);
         double var4 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
         Vec3d var6 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, var4, 0.6);
         var6 = var6.rotateX(-this.pitch * (float) Math.PI / 180.0F);
         var6 = var6.rotateY(-this.yaw * (float) Math.PI / 180.0F);
         var6 = var6.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
         this.world.addParticle(ParticleType.ITEM_CRACK, var6.x, var6.y, var6.z, var3.x, var3.y + 0.05, var3.z, Item.getRawId(itemStack.getItem()));
      }
   }

   public void onKilled(DamageSource source) {
      Entity var2 = source.getAttacker();
      LivingEntity var3 = this.getLastAttacker();
      if (this.mobValue >= 0 && var3 != null) {
         var3.onKillEntity(this, this.mobValue);
      }

      if (var2 != null) {
         var2.onKill(this);
      }

      this.dead = true;
      this.getDamageTracker().clearDamageHistory();
      if (!this.world.isClient) {
         int var4 = 0;
         if (var2 instanceof PlayerEntity) {
            var4 = EnchantmentHelper.getLootingLevel((LivingEntity)var2);
         }

         if (this.isGrownUp() && this.world.getGameRules().getBoolean("doMobLoot")) {
            this.dropLoot(this.playerHitTimer > 0, var4);
            this.dropEquipment(this.playerHitTimer > 0, var4);
            if (this.playerHitTimer > 0 && this.random.nextFloat() < 0.025F + (float)var4 * 0.01F) {
               this.dropRareItem();
            }
         }
      }

      this.world.doEntityEvent(this, (byte)3);
   }

   protected void dropEquipment(boolean hasBeenHit, int lootingLevel) {
   }

   public void applyKnockback(Entity entity, float amount, double velocityX, double velocityZ) {
      if (!(this.random.nextDouble() < this.initializeAttribute(EntityAttributes.KNOCKBACK_RESISTANCE).get())) {
         this.velocityDirty = true;
         float var7 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
         float var8 = 0.4F;
         this.velocityX /= 2.0;
         this.velocityY /= 2.0;
         this.velocityZ /= 2.0;
         this.velocityX -= velocityX / (double)var7 * (double)var8;
         this.velocityY += (double)var8;
         this.velocityZ -= velocityZ / (double)var7 * (double)var8;
         if (this.velocityY > 0.4F) {
            this.velocityY = 0.4F;
         }
      }
   }

   protected String getHurtSound() {
      return "game.neutral.hurt";
   }

   protected String getDeathSound() {
      return "game.neutral.die";
   }

   protected void dropRareItem() {
   }

   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
   }

   public boolean isClimbing() {
      int var1 = MathHelper.floor(this.x);
      int var2 = MathHelper.floor(this.getBoundingBox().minY);
      int var3 = MathHelper.floor(this.z);
      Block var4 = this.world.getBlockState(new BlockPos(var1, var2, var3)).getBlock();
      return (var4 == Blocks.LADDER || var4 == Blocks.VINE) && (!(this instanceof PlayerEntity) || !((PlayerEntity)this).isSpectator());
   }

   @Override
   public boolean isAlive() {
      return !this.removed && this.getHealth() > 0.0F;
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      super.applyFallDamage(distance, g);
      StatusEffectInstance var3 = this.getEffectInstance(StatusEffect.JUMP_BOOST);
      float var4 = var3 != null ? (float)(var3.getAmplifier() + 1) : 0.0F;
      int var5 = MathHelper.ceil((distance - 3.0F - var4) * g);
      if (var5 > 0) {
         this.playSound(this.getFallSound(var5), 1.0F, 1.0F);
         this.damage(DamageSource.FALL, (float)var5);
         int var6 = MathHelper.floor(this.x);
         int var7 = MathHelper.floor(this.y - 0.2F);
         int var8 = MathHelper.floor(this.z);
         Block var9 = this.world.getBlockState(new BlockPos(var6, var7, var8)).getBlock();
         if (var9.getMaterial() != Material.AIR) {
            Block.Sound var10 = var9.sound;
            this.playSound(var10.getStepSound(), var10.getVolume() * 0.5F, var10.getPitch() * 0.75F);
         }
      }
   }

   protected String getFallSound(int distance) {
      return distance > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void animateDamage() {
      this.hurtTimer = this.hurtAnimationTicks = 10;
      this.knockbackVelocity = 0.0F;
   }

   public int getArmorProtection() {
      int var1 = 0;

      for(ItemStack var5 : this.getEquipmentStacks()) {
         if (var5 != null && var5.getItem() instanceof ArmorItem) {
            int var6 = ((ArmorItem)var5.getItem()).protection;
            var1 += var6;
         }
      }

      return var1;
   }

   protected void damageArmor(float value) {
   }

   protected float damageAfterArmorResistance(DamageSource source, float damage) {
      if (!source.bypassesArmor()) {
         int var3 = 25 - this.getArmorProtection();
         float var4 = damage * (float)var3;
         this.damageArmor(damage);
         damage = var4 / 25.0F;
      }

      return damage;
   }

   protected float damageAfterEffectsAndEnchantments(DamageSource source, float damage) {
      if (source.isUnblockable()) {
         return damage;
      } else {
         if (this.hasStatusEffect(StatusEffect.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
            int var3 = (this.getEffectInstance(StatusEffect.RESISTANCE).getAmplifier() + 1) * 5;
            int var4 = 25 - var3;
            float var5 = damage * (float)var4;
            damage = var5 / 25.0F;
         }

         if (damage <= 0.0F) {
            return 0.0F;
         } else {
            int var6 = EnchantmentHelper.modifyProtection(this.getEquipmentStacks(), source);
            if (var6 > 20) {
               var6 = 20;
            }

            if (var6 > 0 && var6 <= 20) {
               int var7 = 25 - var6;
               float var8 = damage * (float)var7;
               damage = var8 / 25.0F;
            }

            return damage;
         }
      }
   }

   protected void applyDamage(DamageSource source, float damage) {
      if (!this.isInvulnerable(source)) {
         damage = this.damageAfterArmorResistance(source, damage);
         damage = this.damageAfterEffectsAndEnchantments(source, damage);
         float var7 = Math.max(damage - this.getAbsorption(), 0.0F);
         this.setAbsorption(this.getAbsorption() - (damage - var7));
         if (var7 != 0.0F) {
            float var4 = this.getHealth();
            this.setHealth(var4 - var7);
            this.getDamageTracker().onDamage(source, var4, var7);
            this.setAbsorption(this.getAbsorption() - var7);
         }
      }
   }

   public DamageTracker getDamageTracker() {
      return this.damageTracker;
   }

   public LivingEntity getLastAttacker() {
      if (this.damageTracker.getLastAttacker() != null) {
         return this.damageTracker.getLastAttacker();
      } else if (this.attackingPlayer != null) {
         return this.attackingPlayer;
      } else {
         return this.attacker != null ? this.attacker : null;
      }
   }

   public final float getMaxHealth() {
      return (float)this.initializeAttribute(EntityAttributes.MAX_HEALTH).get();
   }

   public final int getStuckArrows() {
      return this.dataTracker.getByte(9);
   }

   public final void setStuckArrows(int arrows) {
      this.dataTracker.update(9, (byte)arrows);
   }

   private int getMiningSpeedMultiplier() {
      if (this.hasStatusEffect(StatusEffect.HASTE)) {
         return 6 - (1 + this.getEffectInstance(StatusEffect.HASTE).getAmplifier()) * 1;
      } else {
         return this.hasStatusEffect(StatusEffect.MINING_FAIGUE) ? 6 + (1 + this.getEffectInstance(StatusEffect.MINING_FAIGUE).getAmplifier()) * 2 : 6;
      }
   }

   public void swingHand() {
      if (!this.handSwinging || this.handSwingTicks >= this.getMiningSpeedMultiplier() / 2 || this.handSwingTicks < 0) {
         this.handSwingTicks = -1;
         this.handSwinging = true;
         if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getEntityTracker().sendToListeners(this, new EntityAnimationS2CPacket(this, 0));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 2) {
         this.handSwingAmount = 1.5F;
         this.maxHealth = this.defaultMaxHealth;
         this.hurtTimer = this.hurtAnimationTicks = 10;
         this.knockbackVelocity = 0.0F;
         String var2 = this.getHurtSound();
         if (var2 != null) {
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }

         this.damage(DamageSource.GENERIC, 0.0F);
      } else if (event == 3) {
         String var3 = this.getDeathSound();
         if (var3 != null) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }

         this.setHealth(0.0F);
         this.onKilled(DamageSource.GENERIC);
      } else {
         super.doEvent(event);
      }
   }

   @Override
   protected void tickVoid() {
      this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
   }

   protected void updateHandSwing() {
      int var1 = this.getMiningSpeedMultiplier();
      if (this.handSwinging) {
         ++this.handSwingTicks;
         if (this.handSwingTicks >= var1) {
            this.handSwingTicks = 0;
            this.handSwinging = false;
         }
      } else {
         this.handSwingTicks = 0;
      }

      this.handSwingProgress = (float)this.handSwingTicks / (float)var1;
   }

   public IEntityAttributeInstance initializeAttribute(IEntityAttribute attribute) {
      return this.getAttributes().get(attribute);
   }

   public AbstractEntityAttributeContainer getAttributes() {
      if (this.attributes == null) {
         this.attributes = new EntityAttributeContainer();
      }

      return this.attributes;
   }

   public LivingEntityType getMobType() {
      return LivingEntityType.UNDEFINED;
   }

   public abstract ItemStack getStackInHand();

   public abstract ItemStack getStackInInventory(int id);

   @Environment(EnvType.CLIENT)
   public abstract ItemStack getArmorStack(int armorSlot);

   @Override
   public abstract void setEquipmentStack(int slot, ItemStack stack);

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      IEntityAttributeInstance var2 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
      if (var2.getModifier(SPRINTING_SPEED_MODIFIER_ID) != null) {
         var2.removeModifier(SPRINTING_SPEED_MODIFIER);
      }

      if (sprinting) {
         var2.addModifier(SPRINTING_SPEED_MODIFIER);
      }
   }

   @Override
   public abstract ItemStack[] getEquipmentStacks();

   protected float getSoundVolume() {
      return 1.0F;
   }

   protected float getSoundPitch() {
      return this.isBaby()
         ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F
         : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean isDead() {
      return this.getHealth() <= 0.0F;
   }

   public void refreshPosition(double x, double y, double z) {
      this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
   }

   public void dismountRider(Entity entity) {
      double var3 = entity.x;
      double var5 = entity.getBoundingBox().minY + (double)entity.height;
      double var7 = entity.z;
      byte var9 = 1;

      for(int var10 = -var9; var10 <= var9; ++var10) {
         for(int var11 = -var9; var11 < var9; ++var11) {
            if (var10 != 0 || var11 != 0) {
               int var12 = (int)(this.x + (double)var10);
               int var13 = (int)(this.z + (double)var11);
               Box var2 = this.getBoundingBox().move((double)var10, 1.0, (double)var11);
               if (this.world.getCollisionBoxes(var2).isEmpty()) {
                  if (World.hasSolidTop(this.world, new BlockPos(var12, (int)this.y, var13))) {
                     this.refreshPosition(this.x + (double)var10, this.y + 1.0, this.z + (double)var11);
                     return;
                  }

                  if (World.hasSolidTop(this.world, new BlockPos(var12, (int)this.y - 1, var13))
                     || this.world.getBlockState(new BlockPos(var12, (int)this.y - 1, var13)).getBlock().getMaterial() == Material.WATER) {
                     var3 = this.x + (double)var10;
                     var5 = this.y + 1.0;
                     var7 = this.z + (double)var11;
                  }
               }
            }
         }
      }

      this.refreshPosition(var3, var5, var7);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldShowNameTag() {
      return false;
   }

   protected float m_12vxcslau() {
      return 0.42F;
   }

   protected void jump() {
      this.velocityY = (double)this.m_12vxcslau();
      if (this.hasStatusEffect(StatusEffect.JUMP_BOOST)) {
         this.velocityY += (double)((float)(this.getEffectInstance(StatusEffect.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
      }

      if (this.isSprinting()) {
         float var1 = this.yaw * (float) (Math.PI / 180.0);
         this.velocityX -= (double)(MathHelper.sin(var1) * 0.2F);
         this.velocityZ += (double)(MathHelper.cos(var1) * 0.2F);
      }

      this.velocityDirty = true;
   }

   protected void aiEnabled() {
      this.velocityY += 0.04F;
   }

   protected void m_24htnczxz() {
      this.velocityY += 0.04F;
   }

   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      if (this.isServer()) {
         if (!this.isInWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
            if (!this.isInLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
               float var9 = 0.91F;
               if (this.onGround) {
                  var9 = this.world
                        .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               float var4 = 0.16277136F / (var9 * var9 * var9);
               float var12;
               if (this.onGround) {
                  var12 = this.getMovementSpeed() * var4;
               } else {
                  var12 = this.airSpeed;
               }

               this.updateVelocity(sidewaysVelocity, forwardVelocity, var12);
               var9 = 0.91F;
               if (this.onGround) {
                  var9 = this.world
                        .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               if (this.isClimbing()) {
                  float var14 = 0.15F;
                  this.velocityX = MathHelper.clamp(this.velocityX, (double)(-var14), (double)var14);
                  this.velocityZ = MathHelper.clamp(this.velocityZ, (double)(-var14), (double)var14);
                  this.fallDistance = 0.0F;
                  if (this.velocityY < -0.15) {
                     this.velocityY = -0.15;
                  }

                  boolean var15 = this.isSneaking() && this instanceof PlayerEntity;
                  if (var15 && this.velocityY < 0.0) {
                     this.velocityY = 0.0;
                  }
               }

               this.move(this.velocityX, this.velocityY, this.velocityZ);
               if (this.collidingHorizontally && this.isClimbing()) {
                  this.velocityY = 0.2;
               }

               if (this.world.isClient
                  && (
                     !this.world.isLoaded(new BlockPos((int)this.x, 0, (int)this.z))
                        || !this.world.getChunk(new BlockPos((int)this.x, 0, (int)this.z)).isLoaded()
                  )) {
                  if (this.y > 0.0) {
                     this.velocityY = -0.1;
                  } else {
                     this.velocityY = 0.0;
                  }
               } else {
                  this.velocityY -= 0.08;
               }

               this.velocityY *= 0.98F;
               this.velocityX *= (double)var9;
               this.velocityZ *= (double)var9;
            } else {
               double var8 = this.y;
               this.updateVelocity(sidewaysVelocity, forwardVelocity, 0.02F);
               this.move(this.velocityX, this.velocityY, this.velocityZ);
               this.velocityX *= 0.5;
               this.velocityY *= 0.5;
               this.velocityZ *= 0.5;
               this.velocityY -= 0.02;
               if (this.collidingHorizontally && this.canMove(this.velocityX, this.velocityY + 0.6F - this.y + var8, this.velocityZ)) {
                  this.velocityY = 0.3F;
               }
            }
         } else {
            double var3 = this.y;
            float var5 = 0.8F;
            float var6 = 0.02F;
            float var7 = (float)EnchantmentHelper.getDepthStriderLevel(this);
            if (var7 > 3.0F) {
               var7 = 3.0F;
            }

            if (!this.onGround) {
               var7 *= 0.5F;
            }

            if (var7 > 0.0F) {
               var5 += (0.54600006F - var5) * var7 / 3.0F;
               var6 += (this.getMovementSpeed() * 1.0F - var6) * var7 / 3.0F;
            }

            this.updateVelocity(sidewaysVelocity, forwardVelocity, var6);
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= (double)var5;
            this.velocityY *= 0.8F;
            this.velocityZ *= (double)var5;
            this.velocityY -= 0.02;
            if (this.collidingHorizontally && this.canMove(this.velocityX, this.velocityY + 0.6F - this.y + var3, this.velocityZ)) {
               this.velocityY = 0.3F;
            }
         }
      }

      this.prevHandSwingAmount = this.handSwingAmount;
      double var11 = this.x - this.prevX;
      double var13 = this.z - this.prevZ;
      float var16 = MathHelper.sqrt(var11 * var11 + var13 * var13) * 4.0F;
      if (var16 > 1.0F) {
         var16 = 1.0F;
      }

      this.handSwingAmount += (var16 - this.handSwingAmount) * 0.4F;
      this.handSwing += this.handSwingAmount;
   }

   public float getMovementSpeed() {
      return this.movementSpeed;
   }

   public void setMovementSpeed(float movementSpeed) {
      this.movementSpeed = movementSpeed;
   }

   public boolean attack(Entity entity) {
      this.setAttackTarget(entity);
      return false;
   }

   public boolean isSleeping() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient) {
         int var1 = this.getStuckArrows();
         if (var1 > 0) {
            if (this.arrowDespawnTimer <= 0) {
               this.arrowDespawnTimer = 20 * (30 - var1);
            }

            --this.arrowDespawnTimer;
            if (this.arrowDespawnTimer <= 0) {
               this.setStuckArrows(var1 - 1);
            }
         }

         for(int var2 = 0; var2 < 5; ++var2) {
            ItemStack var3 = this.equippedItems[var2];
            ItemStack var4 = this.getStackInInventory(var2);
            if (!ItemStack.matches(var4, var3)) {
               ((ServerWorld)this.world).getEntityTracker().sendToListeners(this, new EntityEquipmentS2CPacket(this.getNetworkId(), var2, var4));
               if (var3 != null) {
                  this.attributes.removeModifiers(var3.getAttributeModifiers());
               }

               if (var4 != null) {
                  this.attributes.addModifiers(var4.getAttributeModifiers());
               }

               this.equippedItems[var2] = var4 == null ? null : var4.copy();
            }
         }

         if (this.time % 20 == 0) {
            this.getDamageTracker().clearDamageHistory();
         }
      }

      this.tickAI();
      double var9 = this.x - this.prevX;
      double var10 = this.z - this.prevZ;
      float var5 = (float)(var9 * var9 + var10 * var10);
      float var6 = this.bodyYaw;
      float var7 = 0.0F;
      this.prevStepBobbingAmount = this.stepBobbingAmount;
      float var8 = 0.0F;
      if (var5 > 0.0025000002F) {
         var8 = 1.0F;
         var7 = (float)Math.sqrt((double)var5) * 3.0F;
         var6 = (float)Math.atan2(var10, var9) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.handSwingProgress > 0.0F) {
         var6 = this.yaw;
      }

      if (!this.onGround) {
         var8 = 0.0F;
      }

      this.stepBobbingAmount += (var8 - this.stepBobbingAmount) * 0.3F;
      this.world.profiler.push("headTurn");
      var7 = this.bodyMovement(var6, var7);
      this.world.profiler.pop();
      this.world.profiler.push("rangeChecks");

      while(this.yaw - this.prevYaw < -180.0F) {
         this.prevYaw -= 360.0F;
      }

      while(this.yaw - this.prevYaw >= 180.0F) {
         this.prevYaw += 360.0F;
      }

      while(this.bodyYaw - this.prevBodyYaw < -180.0F) {
         this.prevBodyYaw -= 360.0F;
      }

      while(this.bodyYaw - this.prevBodyYaw >= 180.0F) {
         this.prevBodyYaw += 360.0F;
      }

      while(this.pitch - this.prevPitch < -180.0F) {
         this.prevPitch -= 360.0F;
      }

      while(this.pitch - this.prevPitch >= 180.0F) {
         this.prevPitch += 360.0F;
      }

      while(this.headYaw - this.prevHeadYaw < -180.0F) {
         this.prevHeadYaw -= 360.0F;
      }

      while(this.headYaw - this.prevHeadYaw >= 180.0F) {
         this.prevHeadYaw += 360.0F;
      }

      this.world.profiler.pop();
      this.distanceTravelled += var7;
   }

   protected float bodyMovement(float yaw, float movement) {
      float var3 = MathHelper.wrapDegrees(yaw - this.bodyYaw);
      this.bodyYaw += var3 * 0.3F;
      float var4 = MathHelper.wrapDegrees(this.yaw - this.bodyYaw);
      boolean var5 = var4 < -90.0F || var4 >= 90.0F;
      if (var4 < -75.0F) {
         var4 = -75.0F;
      }

      if (var4 >= 75.0F) {
         var4 = 75.0F;
      }

      this.bodyYaw = this.yaw - var4;
      if (var4 * var4 > 2500.0F) {
         this.bodyYaw += var4 * 0.2F;
      }

      if (var5) {
         movement *= -1.0F;
      }

      return movement;
   }

   public void tickAI() {
      if (this.jumpingCooldown > 0) {
         --this.jumpingCooldown;
      }

      if (this.bodyTrackingIncrements > 0) {
         double var1 = this.x + (this.serverX - this.x) / (double)this.bodyTrackingIncrements;
         double var3 = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
         double var5 = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
         double var7 = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
         this.yaw = (float)((double)this.yaw + var7 / (double)this.bodyTrackingIncrements);
         this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
         --this.bodyTrackingIncrements;
         this.setPosition(var1, var3, var5);
         this.setRotation(this.yaw, this.pitch);
      } else if (!this.isServer()) {
         this.velocityX *= 0.98;
         this.velocityY *= 0.98;
         this.velocityZ *= 0.98;
      }

      if (Math.abs(this.velocityX) < 0.005) {
         this.velocityX = 0.0;
      }

      if (Math.abs(this.velocityY) < 0.005) {
         this.velocityY = 0.0;
      }

      if (Math.abs(this.velocityZ) < 0.005) {
         this.velocityZ = 0.0;
      }

      this.world.profiler.push("ai");
      if (this.isDead()) {
         this.jumping = false;
         this.sidewaysSpeed = 0.0F;
         this.forwardSpeed = 0.0F;
         this.randomYaw = 0.0F;
      } else if (this.isServer()) {
         this.world.profiler.push("newAi");
         this.tickAISetup();
         this.world.profiler.pop();
      }

      this.world.profiler.pop();
      this.world.profiler.push("jump");
      if (this.jumping) {
         if (this.isInWater()) {
            this.aiEnabled();
         } else if (this.isInLava()) {
            this.m_24htnczxz();
         } else if (this.onGround && this.jumpingCooldown == 0) {
            this.jump();
            this.jumpingCooldown = 10;
         }
      } else {
         this.jumpingCooldown = 0;
      }

      this.world.profiler.pop();
      this.world.profiler.push("travel");
      this.sidewaysSpeed *= 0.98F;
      this.forwardSpeed *= 0.98F;
      this.randomYaw *= 0.9F;
      this.moveEntityWithVelocity(this.sidewaysSpeed, this.forwardSpeed);
      this.world.profiler.pop();
      this.world.profiler.push("push");
      if (!this.world.isClient) {
         this.pushAwayCollidingEntities();
      }

      this.world.profiler.pop();
   }

   protected void tickAISetup() {
   }

   protected void pushAwayCollidingEntities() {
      List var1 = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F));
      if (var1 != null && !var1.isEmpty()) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Entity var3 = (Entity)var1.get(var2);
            if (var3.isPushable()) {
               this.pushAway(var3);
            }
         }
      }
   }

   protected void pushAway(Entity entity) {
      entity.push(this);
   }

   @Override
   public void startRiding(Entity entity) {
      if (this.vehicle != null && entity == null) {
         if (!this.world.isClient) {
            this.dismountRider(this.vehicle);
         }

         if (this.vehicle != null) {
            this.vehicle.rider = null;
         }

         this.vehicle = null;
      } else {
         super.startRiding(entity);
      }
   }

   @Override
   public void tickRiding() {
      super.tickRiding();
      this.prevStepBobbingAmount = this.stepBobbingAmount;
      this.stepBobbingAmount = 0.0F;
      this.fallDistance = 0.0F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
      this.serverX = x;
      this.serverY = y;
      this.serverZ = z;
      this.serverYaw = (double)yaw;
      this.serverPitch = (double)pitch;
      this.bodyTrackingIncrements = steps;
   }

   public void setJumping(boolean jumping) {
      this.jumping = jumping;
   }

   public void sendPickup(Entity entity, int count) {
      if (!entity.removed && !this.world.isClient) {
         EntityTracker var3 = ((ServerWorld)this.world).getEntityTracker();
         if (entity instanceof ItemEntity) {
            var3.sendToListeners(entity, new EntityPickupS2CPacket(entity.getNetworkId(), this.getNetworkId()));
         }

         if (entity instanceof ArrowEntity) {
            var3.sendToListeners(entity, new EntityPickupS2CPacket(entity.getNetworkId(), this.getNetworkId()));
         }

         if (entity instanceof XpOrbEntity) {
            var3.sendToListeners(entity, new EntityPickupS2CPacket(entity.getNetworkId(), this.getNetworkId()));
         }
      }
   }

   public boolean canSee(Entity entity) {
      return this.world
            .rayTrace(new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z), new Vec3d(entity.x, entity.y + (double)entity.getEyeHeight(), entity.z))
         == null;
   }

   @Override
   public Vec3d getCameraRotation() {
      return this.m_01qqqsfds(1.0F);
   }

   @Override
   public Vec3d m_01qqqsfds(float f) {
      if (f == 1.0F) {
         return this.m_37mcgfsrt(this.pitch, this.headYaw);
      } else {
         float var2 = this.prevPitch + (this.pitch - this.prevPitch) * f;
         float var3 = this.prevHeadYaw + (this.headYaw - this.prevHeadYaw) * f;
         return this.m_37mcgfsrt(var2, var3);
      }
   }

   @Environment(EnvType.CLIENT)
   public float getHandSwingProcess(float tickDelta) {
      float var2 = this.handSwingProgress - this.lastHandSwingProgress;
      if (var2 < 0.0F) {
         ++var2;
      }

      return this.lastHandSwingProgress + var2 * tickDelta;
   }

   public boolean isServer() {
      return !this.world.isClient;
   }

   @Override
   public boolean hasCollision() {
      return !this.removed;
   }

   @Override
   public boolean isPushable() {
      return !this.removed;
   }

   @Override
   protected void onDamaged() {
      this.damaged = this.random.nextDouble() >= this.initializeAttribute(EntityAttributes.KNOCKBACK_RESISTANCE).get();
   }

   @Override
   public float getHeadYaw() {
      return this.headYaw;
   }

   @Override
   public void setHeadYaw(float headYaw) {
      this.headYaw = headYaw;
   }

   public float getAbsorption() {
      return this.absorption;
   }

   public void setAbsorption(float absorption) {
      if (absorption < 0.0F) {
         absorption = 0.0F;
      }

      this.absorption = absorption;
   }

   public AbstractTeam getScoreboardTeam() {
      return this.world.getScoreboard().getTeamOfMember(this.getUuid().toString());
   }

   public boolean isInSameTeam(LivingEntity entity) {
      return this.isInTeam(entity.getScoreboardTeam());
   }

   public boolean isInTeam(AbstractTeam team) {
      return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
   }

   public void m_18fvbnxav() {
   }

   public void m_10fgolizq() {
   }

   protected void m_38sehrahq() {
      this.effectsChanged = true;
   }
}
