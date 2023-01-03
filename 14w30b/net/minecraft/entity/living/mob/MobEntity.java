package net.minecraft.entity.living.mob;

import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MovementControl;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.decoration.DecorationEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public abstract class MobEntity extends LivingEntity {
   public int ambientSoundDelay;
   protected int experiencePoints;
   private LookControl lookControl;
   protected MovementControl movementControl;
   protected JumpControl jumpControl;
   private BodyControl bodyControl;
   protected EntityNavigation entityNavigation;
   protected final GoalSelector goalSelector;
   protected final GoalSelector targetSelector;
   private LivingEntity targetEntity;
   private MobVisibilityCache mobVisibilityCache;
   private ItemStack[] inventorySlots = new ItemStack[5];
   protected float[] inventoryDropChances = new float[5];
   private boolean canPickupLoot;
   private boolean persistent;
   private boolean isLeashed;
   private Entity holdingEntity;
   private NbtCompound leashNbt;

   public MobEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.goalSelector = new GoalSelector(c_54ruxjwzt != null && c_54ruxjwzt.profiler != null ? c_54ruxjwzt.profiler : null);
      this.targetSelector = new GoalSelector(c_54ruxjwzt != null && c_54ruxjwzt.profiler != null ? c_54ruxjwzt.profiler : null);
      this.lookControl = new LookControl(this);
      this.movementControl = new MovementControl(this);
      this.jumpControl = new JumpControl(this);
      this.bodyControl = new BodyControl(this);
      this.entityNavigation = this.createNavigation(c_54ruxjwzt);
      this.mobVisibilityCache = new MobVisibilityCache(this);

      for(int var2 = 0; var2 < this.inventoryDropChances.length; ++var2) {
         this.inventoryDropChances[var2] = 0.085F;
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributes().registerAttribute(EntityAttributes.FOLLOW_RANGE).setBase(16.0);
   }

   protected EntityNavigation createNavigation(World world) {
      return new MobEntityNavigation(this, world);
   }

   public LookControl getLookControl() {
      return this.lookControl;
   }

   public MovementControl getMovementControl() {
      return this.movementControl;
   }

   public JumpControl getJumpControl() {
      return this.jumpControl;
   }

   public EntityNavigation getNavigation() {
      return this.entityNavigation;
   }

   public MobVisibilityCache getMobVisibilityCache() {
      return this.mobVisibilityCache;
   }

   public LivingEntity getTargetEntity() {
      return this.targetEntity;
   }

   public void setAttackTarget(LivingEntity targetEntity) {
      this.targetEntity = targetEntity;
   }

   public boolean canAttackEntity(Class entityClass) {
      return CreeperEntity.class != entityClass && GhastEntity.class != entityClass;
   }

   public void onEatingGrass() {
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
   }

   public int getMinAmbientSoundDelay() {
      return 80;
   }

   public void playAmbientSound() {
      String var1 = this.getAmbientSound();
      if (var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   @Override
   public void baseTick() {
      super.baseTick();
      this.world.profiler.push("mobBaseTick");
      if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundDelay++) {
         this.ambientSoundDelay = -this.getMinAmbientSoundDelay();
         this.playAmbientSound();
      }

      this.world.profiler.pop();
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      if (this.experiencePoints > 0) {
         int var2 = this.experiencePoints;
         ItemStack[] var3 = this.getEquipmentStacks();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] != null && this.inventoryDropChances[var4] <= 1.0F) {
               var2 += 1 + this.random.nextInt(3);
            }
         }

         return var2;
      } else {
         return this.experiencePoints;
      }
   }

   public void doSpawnEffects() {
      if (this.world.isClient) {
         for(int var1 = 0; var1 < 20; ++var1) {
            double var2 = this.random.nextGaussian() * 0.02;
            double var4 = this.random.nextGaussian() * 0.02;
            double var6 = this.random.nextGaussian() * 0.02;
            double var8 = 10.0;
            this.world
               .addParticle(
                  ParticleType.EXPLOSION_NORMAL,
                  this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - var2 * var8,
                  this.y + (double)(this.random.nextFloat() * this.height) - var4 * var8,
                  this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width - var6 * var8,
                  var2,
                  var4,
                  var6
               );
         }
      } else {
         this.world.doEntityEvent(this, (byte)20);
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 20) {
         this.doSpawnEffects();
      } else {
         super.doEvent(event);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient) {
         this.updateLeashStatus();
      }
   }

   @Override
   protected float bodyMovement(float yaw, float movement) {
      this.bodyControl.tick();
      return movement;
   }

   protected String getAmbientSound() {
      return null;
   }

   protected Item getDefaultDropLoot() {
      return Item.byRawId(0);
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      Item var3 = this.getDefaultDropLoot();
      if (var3 != null) {
         int var4 = this.random.nextInt(3);
         if (lootingMultiplier > 0) {
            var4 += this.random.nextInt(lootingMultiplier + 1);
         }

         for(int var5 = 0; var5 < var4; ++var5) {
            this.dropItem(var3, 1);
         }
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("CanPickUpLoot", this.canPickupLoot());
      nbt.putBoolean("PersistenceRequired", this.persistent);
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.inventorySlots.length; ++var3) {
         NbtCompound var4 = new NbtCompound();
         if (this.inventorySlots[var3] != null) {
            this.inventorySlots[var3].writeNbt(var4);
         }

         var2.add(var4);
      }

      nbt.put("Equipment", var2);
      NbtList var6 = new NbtList();

      for(int var7 = 0; var7 < this.inventoryDropChances.length; ++var7) {
         var6.add(new NbtFloat(this.inventoryDropChances[var7]));
      }

      nbt.put("DropChances", var6);
      nbt.putBoolean("Leashed", this.isLeashed);
      if (this.holdingEntity != null) {
         NbtCompound var8 = new NbtCompound();
         if (this.holdingEntity instanceof LivingEntity) {
            var8.putLong("UUIDMost", this.holdingEntity.getUuid().getMostSignificantBits());
            var8.putLong("UUIDLeast", this.holdingEntity.getUuid().getLeastSignificantBits());
         } else if (this.holdingEntity instanceof DecorationEntity) {
            BlockPos var5 = ((DecorationEntity)this.holdingEntity).getBlockPos();
            var8.putInt("X", var5.getX());
            var8.putInt("Y", var5.getY());
            var8.putInt("Z", var5.getZ());
         }

         nbt.put("Leash", var8);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("CanPickUpLoot", 1)) {
         this.setCanPickupLoot(nbt.getBoolean("CanPickUpLoot"));
      }

      this.persistent = nbt.getBoolean("PersistenceRequired");
      if (nbt.isType("Equipment", 9)) {
         NbtList var2 = nbt.getList("Equipment", 10);

         for(int var3 = 0; var3 < this.inventorySlots.length; ++var3) {
            this.inventorySlots[var3] = ItemStack.fromNbt(var2.getCompound(var3));
         }
      }

      if (nbt.isType("DropChances", 9)) {
         NbtList var4 = nbt.getList("DropChances", 5);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            this.inventoryDropChances[var5] = var4.getFloat(var5);
         }
      }

      this.isLeashed = nbt.getBoolean("Leashed");
      if (this.isLeashed && nbt.isType("Leash", 10)) {
         this.leashNbt = nbt.getCompound("Leash");
      }
   }

   public void setForwardVelocity(float velocity) {
      this.forwardSpeed = velocity;
   }

   @Override
   public void setMovementSpeed(float movementSpeed) {
      super.setMovementSpeed(movementSpeed);
      this.setForwardVelocity(movementSpeed);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      this.world.profiler.push("looting");
      if (!this.world.isClient && this.canPickupLoot() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
         for(ItemEntity var3 : this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0))) {
            if (!var3.removed && var3.getItemStack() != null && !var3.m_40frtuhfs()) {
               this.m_30rammcie(var3);
            }
         }
      }

      this.world.profiler.pop();
   }

   protected void m_30rammcie(ItemEntity c_32myydzeb) {
      ItemStack var2 = c_32myydzeb.getItemStack();
      int var3 = getSlotForEquipment(var2);
      if (var3 > -1) {
         boolean var4 = true;
         ItemStack var5 = this.getStackInInventory(var3);
         if (var5 != null) {
            if (var3 == 0) {
               if (var2.getItem() instanceof SwordItem && !(var5.getItem() instanceof SwordItem)) {
                  var4 = true;
               } else if (var2.getItem() instanceof SwordItem && var5.getItem() instanceof SwordItem) {
                  SwordItem var8 = (SwordItem)var2.getItem();
                  SwordItem var10 = (SwordItem)var5.getItem();
                  if (var8.getAttackDamage() != var10.getAttackDamage()) {
                     var4 = var8.getAttackDamage() > var10.getAttackDamage();
                  } else {
                     var4 = var2.getMetadata() > var5.getMetadata() || var2.hasNbt() && !var5.hasNbt();
                  }
               } else if (var2.getItem() instanceof BowItem && var5.getItem() instanceof BowItem) {
                  var4 = var2.hasNbt() && !var5.hasNbt();
               } else {
                  var4 = false;
               }
            } else if (var2.getItem() instanceof ArmorItem && !(var5.getItem() instanceof ArmorItem)) {
               var4 = true;
            } else if (var2.getItem() instanceof ArmorItem && var5.getItem() instanceof ArmorItem) {
               ArmorItem var6 = (ArmorItem)var2.getItem();
               ArmorItem var7 = (ArmorItem)var5.getItem();
               if (var6.protection != var7.protection) {
                  var4 = var6.protection > var7.protection;
               } else {
                  var4 = var2.getMetadata() > var5.getMetadata() || var2.hasNbt() && !var5.hasNbt();
               }
            } else {
               var4 = false;
            }
         }

         if (var4 && this.m_33ahawbcj(var2)) {
            if (var5 != null && this.random.nextFloat() - 0.1F < this.inventoryDropChances[var3]) {
               this.dropItem(var5, 0.0F);
            }

            if (var2.getItem() == Items.DIAMOND && c_32myydzeb.getThrower() != null) {
               PlayerEntity var9 = this.world.getPlayer(c_32myydzeb.getThrower());
               if (var9 != null) {
                  var9.incrementStat(Achievements.GIVE_DIAMOND);
               }
            }

            this.setEquipmentStack(var3, var2);
            this.inventoryDropChances[var3] = 2.0F;
            this.persistent = true;
            this.sendPickup(c_32myydzeb, 1);
            c_32myydzeb.remove();
         }
      }
   }

   protected boolean m_33ahawbcj(ItemStack c_72owraavl) {
      return true;
   }

   protected boolean canDespawn() {
      return true;
   }

   protected void checkDespawn() {
      if (this.persistent) {
         this.despawnTicks = 0;
      } else {
         PlayerEntity var1 = this.world.getClosestPlayer(this, -1.0);
         if (var1 != null) {
            double var2 = var1.x - this.x;
            double var4 = var1.y - this.y;
            double var6 = var1.z - this.z;
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            if (this.canDespawn() && var8 > 16384.0) {
               this.remove();
            }

            if (this.despawnTicks > 600 && this.random.nextInt(800) == 0 && var8 > 1024.0 && this.canDespawn()) {
               this.remove();
            } else if (var8 < 1024.0) {
               this.despawnTicks = 0;
            }
         }
      }
   }

   @Override
   protected final void tickAISetup() {
      ++this.despawnTicks;
      this.world.profiler.push("checkDespawn");
      this.checkDespawn();
      this.world.profiler.pop();
      this.world.profiler.push("sensing");
      this.mobVisibilityCache.clear();
      this.world.profiler.pop();
      this.world.profiler.push("targetSelector");
      this.targetSelector.tick();
      this.world.profiler.pop();
      this.world.profiler.push("goalSelector");
      this.goalSelector.tick();
      this.world.profiler.pop();
      this.world.profiler.push("navigation");
      this.entityNavigation.tick();
      this.world.profiler.pop();
      this.world.profiler.push("mob tick");
      this.m_45jbqtvrb();
      this.world.profiler.pop();
      this.world.profiler.push("controls");
      this.world.profiler.push("move");
      this.movementControl.tickUpdateMovement();
      this.world.profiler.swap("look");
      this.lookControl.tick();
      this.world.profiler.swap("jump");
      this.jumpControl.tick();
      this.world.profiler.pop();
      this.world.profiler.pop();
   }

   protected void m_45jbqtvrb() {
   }

   public int getLookPitchSpeed() {
      return 40;
   }

   public void lookAtEntity(Entity target, float maxYawChange, float maxPitchChange) {
      double var4 = target.x - this.x;
      double var8 = target.z - this.z;
      double var6;
      if (target instanceof LivingEntity) {
         LivingEntity var10 = (LivingEntity)target;
         var6 = var10.y + (double)var10.getEyeHeight() - (this.y + (double)this.getEyeHeight());
      } else {
         var6 = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0 - (this.y + (double)this.getEyeHeight());
      }

      double var14 = (double)MathHelper.sqrt(var4 * var4 + var8 * var8);
      float var12 = (float)(Math.atan2(var8, var4) * 180.0 / (float) Math.PI) - 90.0F;
      float var13 = (float)(-(Math.atan2(var6, var14) * 180.0 / (float) Math.PI));
      this.pitch = this.changeAngle(this.pitch, var13, maxPitchChange);
      this.yaw = this.changeAngle(this.yaw, var12, maxYawChange);
   }

   private float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
      float var4 = MathHelper.wrapDegrees(newAngle - oldAngle);
      if (var4 > maxChangeInAngle) {
         var4 = maxChangeInAngle;
      }

      if (var4 < -maxChangeInAngle) {
         var4 = -maxChangeInAngle;
      }

      return oldAngle + var4;
   }

   public boolean canSpawn() {
      return true;
   }

   public boolean m_52qkzdxky() {
      return this.world.canBuildIn(this.getBoundingBox(), this)
         && this.world.getCollisions(this, this.getBoundingBox()).isEmpty()
         && (this.m_84jincljh() == MobEntity.Environment.IN_WATER || !this.world.containsLiquid(this.getBoundingBox()));
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getShadowScale() {
      return 1.0F;
   }

   public int getLimitPerChunk() {
      return 4;
   }

   @Override
   public int getSafeFallDistance() {
      if (this.getTargetEntity() == null) {
         return 3;
      } else {
         int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         var1 -= (3 - this.world.getDifficulty().getIndex()) * 4;
         if (var1 < 0) {
            var1 = 0;
         }

         return var1 + 3;
      }
   }

   @Override
   public ItemStack getStackInHand() {
      return this.inventorySlots[0];
   }

   @Override
   public ItemStack getStackInInventory(int id) {
      return this.inventorySlots[id];
   }

   @Override
   public ItemStack getArmorStack(int armorSlot) {
      return this.inventorySlots[armorSlot + 1];
   }

   @Override
   public void setEquipmentStack(int slot, ItemStack stack) {
      this.inventorySlots[slot] = stack;
   }

   @Override
   public ItemStack[] getEquipmentStacks() {
      return this.inventorySlots;
   }

   @Override
   protected void dropEquipment(boolean hasBeenHit, int lootingLevel) {
      for(int var3 = 0; var3 < this.getEquipmentStacks().length; ++var3) {
         ItemStack var4 = this.getStackInInventory(var3);
         boolean var5 = this.inventoryDropChances[var3] > 1.0F;
         if (var4 != null && (hasBeenHit || var5) && this.random.nextFloat() - (float)lootingLevel * 0.01F < this.inventoryDropChances[var3]) {
            if (!var5 && var4.isDamageable()) {
               int var6 = Math.max(var4.getMaxDamage() - 25, 1);
               int var7 = var4.getMaxDamage() - this.random.nextInt(this.random.nextInt(var6) + 1);
               if (var7 > var6) {
                  var7 = var6;
               }

               if (var7 < 1) {
                  var7 = 1;
               }

               var4.setDamage(var7);
            }

            this.dropItem(var4, 0.0F);
         }
      }
   }

   protected void initSpawningEquipment(LocalDifficulty localDifficulty) {
      if (this.random.nextFloat() < 0.15F * localDifficulty.getMultiplier()) {
         int var2 = this.random.nextInt(2);
         float var3 = this.world.getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         if (this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         if (this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         for(int var4 = 3; var4 >= 0; --var4) {
            ItemStack var5 = this.getArmorStack(var4);
            if (var4 < 3 && this.random.nextFloat() < var3) {
               break;
            }

            if (var5 == null) {
               Item var6 = getEquipmentForSlot(var4 + 1, var2);
               if (var6 != null) {
                  this.setEquipmentStack(var4 + 1, new ItemStack(var6));
               }
            }
         }
      }
   }

   public static int getSlotForEquipment(ItemStack equipment) {
      if (equipment.getItem() == Item.byBlock(Blocks.PUMPKIN) || equipment.getItem() == Items.SKULL) {
         return 4;
      } else {
         if (equipment.getItem() instanceof ArmorItem) {
            switch(((ArmorItem)equipment.getItem()).slot) {
               case 0:
                  return 4;
               case 1:
                  return 3;
               case 2:
                  return 2;
               case 3:
                  return 1;
            }
         }

         return 0;
      }
   }

   public static Item getEquipmentForSlot(int slot, int equipmentLevel) {
      switch(slot) {
         case 4:
            if (equipmentLevel == 0) {
               return Items.LEATHER_HELMET;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_HELMET;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_HELMET;
            } else if (equipmentLevel == 3) {
               return Items.IRON_HELMET;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_HELMET;
            }
         case 3:
            if (equipmentLevel == 0) {
               return Items.LEATHER_CHESTPLATE;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_CHESTPLATE;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_CHESTPLATE;
            } else if (equipmentLevel == 3) {
               return Items.IRON_CHESTPLATE;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_CHESTPLATE;
            }
         case 2:
            if (equipmentLevel == 0) {
               return Items.LEATHER_LEGGINGS;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_LEGGINGS;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_LEGGINGS;
            } else if (equipmentLevel == 3) {
               return Items.IRON_LEGGINGS;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_LEGGINGS;
            }
         case 1:
            if (equipmentLevel == 0) {
               return Items.LEATHER_BOOTS;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_BOOTS;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_BOOTS;
            } else if (equipmentLevel == 3) {
               return Items.IRON_BOOTS;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_BOOTS;
            }
         default:
            return null;
      }
   }

   protected void enchantEquipmentItemStack(LocalDifficulty c_03obpszvn) {
      float var2 = c_03obpszvn.getMultiplier();
      if (this.getStackInHand() != null && this.random.nextFloat() < 0.25F * var2) {
         EnchantmentHelper.addRandomEnchantment(this.random, this.getStackInHand(), (int)(5.0F + var2 * (float)this.random.nextInt(18)));
      }

      for(int var3 = 0; var3 < 4; ++var3) {
         ItemStack var4 = this.getArmorStack(var3);
         if (var4 != null && this.random.nextFloat() < 0.5F * var2) {
            EnchantmentHelper.addRandomEnchantment(this.random, var4, (int)(5.0F + var2 * (float)this.random.nextInt(18)));
         }
      }
   }

   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, 1));
      return entityData;
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   public void setPersistent() {
      this.persistent = true;
   }

   public void setInventoryDropChances(int arrayIndex, float value) {
      this.inventoryDropChances[arrayIndex] = value;
   }

   public boolean canPickupLoot() {
      return this.canPickupLoot;
   }

   public void setCanPickupLoot(boolean blValue) {
      this.canPickupLoot = blValue;
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   @Override
   public final boolean interact(PlayerEntity player) {
      if (this.isLeashed() && this.getHoldingEntity() == player) {
         this.detachLeash(true, !player.abilities.creativeMode);
         return true;
      } else {
         ItemStack var2 = player.inventory.getMainHandStack();
         if (var2 != null && var2.getItem() == Items.LEAD && this.isTameable()) {
            if (!(this instanceof TameableEntity) || !((TameableEntity)this).isTamed()) {
               this.attachLeash(player, true);
               --var2.size;
               return true;
            }

            if (((TameableEntity)this).m_77pxwyntx(player)) {
               this.attachLeash(player, true);
               --var2.size;
               return true;
            }
         }

         return this.canInteract(player) ? true : super.interact(player);
      }
   }

   protected boolean canInteract(PlayerEntity player) {
      return false;
   }

   protected void updateLeashStatus() {
      if (this.leashNbt != null) {
         this.readLeashNbt();
      }

      if (this.isLeashed) {
         if (this.holdingEntity == null || this.holdingEntity.removed) {
            this.detachLeash(true, true);
         }
      }
   }

   public void detachLeash(boolean sendPacket, boolean dropItem) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.holdingEntity = null;
         if (!this.world.isClient && dropItem) {
            this.dropItem(Items.LEAD, 1);
         }

         if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getEntityTracker().sendToListeners(this, new EntityAttachS2CPacket(1, this, null));
         }
      }
   }

   public boolean isTameable() {
      return !this.isLeashed() && !(this instanceof Monster);
   }

   public boolean isLeashed() {
      return this.isLeashed;
   }

   public Entity getHoldingEntity() {
      return this.holdingEntity;
   }

   public void attachLeash(Entity entity, boolean sendPacket) {
      this.isLeashed = true;
      this.holdingEntity = entity;
      if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).getEntityTracker().sendToListeners(this, new EntityAttachS2CPacket(1, this, this.holdingEntity));
      }
   }

   private void readLeashNbt() {
      if (this.isLeashed && this.leashNbt != null) {
         if (this.leashNbt.isType("UUIDMost", 4) && this.leashNbt.isType("UUIDLeast", 4)) {
            UUID var5 = new UUID(this.leashNbt.getLong("UUIDMost"), this.leashNbt.getLong("UUIDLeast"));

            for(LivingEntity var4 : this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(10.0, 10.0, 10.0))) {
               if (var4.getUuid().equals(var5)) {
                  this.holdingEntity = var4;
                  break;
               }
            }
         } else if (this.leashNbt.isType("X", 99) && this.leashNbt.isType("Y", 99) && this.leashNbt.isType("Z", 99)) {
            BlockPos var1 = new BlockPos(this.leashNbt.getInt("X"), this.leashNbt.getInt("Y"), this.leashNbt.getInt("Z"));
            LeadKnotEntity var2 = LeadKnotEntity.getOrCreate(this.world, var1);
            if (var2 == null) {
               var2 = LeadKnotEntity.attatch(this.world, var1);
            }

            this.holdingEntity = var2;
         } else {
            this.detachLeash(false, true);
         }
      }

      this.leashNbt = null;
   }

   public MobEntity.Environment m_84jincljh() {
      return MobEntity.Environment.ON_GROUND;
   }

   @Override
   public boolean m_81zmldzmm(int i, ItemStack c_72owraavl) {
      int var3;
      if (i == 99) {
         var3 = 0;
      } else {
         var3 = i - 100 + 1;
         if (var3 < 0 || var3 >= this.inventorySlots.length) {
            return false;
         }
      }

      if (c_72owraavl != null && getSlotForEquipment(c_72owraavl) != var3 && (var3 != 4 || !(c_72owraavl.getItem() instanceof BlockItem))) {
         return false;
      } else {
         this.setEquipmentStack(var3, c_72owraavl);
         return true;
      }
   }

   public static enum Environment {
      ON_GROUND,
      IN_AIR,
      IN_WATER;
   }
}
