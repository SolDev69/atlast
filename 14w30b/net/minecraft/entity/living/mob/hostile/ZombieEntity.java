package net.minecraft.entity.living.mob.hostile;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderThroughVillageAtNightGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.ClampedEntityAttribute;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttribute;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class ZombieEntity extends HostileEntity {
   protected static final IEntityAttribute REINFORCEMENTS_ATTRIBUTE = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
      .setDisplayName("Spawn Reinforcements Chance");
   private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier BABY_SPEED_BOOST_MODIFIER = new AttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, 1);
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this);
   private int ticksUntilConversion;
   private boolean canBreakDoors = false;
   private float zombieWidth = -1.0F;
   private float zombieHeight;

   public ZombieEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      ((MobEntityNavigation)this.getNavigation()).m_54onmfdow(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, PlayerEntity.class, 1.0, false));
      this.goalSelector.addGoal(2, this.fleeExplodingCreeperGoal);
      this.goalSelector.addGoal(5, new GoToWalkTargetGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.m_47ewtstup();
      this.setDimensions(0.6F, 1.95F);
   }

   protected void m_47ewtstup() {
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, VillagerEntity.class, 1.0, true));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, IronGolemEntity.class, 1.0, true));
      this.goalSelector.addGoal(6, new WanderThroughVillageAtNightGoal(this, 1.0, false));
      this.targetSelector.addGoal(1, new RevengeGoal(this, true, ZombiePigmanEntity.class));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, VillagerEntity.class, false));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, IronGolemEntity.class, true));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(40.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.23F);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(3.0);
      this.getAttributes().registerAttribute(REINFORCEMENTS_ATTRIBUTE).setBase(this.random.nextDouble() * 0.1F);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.getDataTracker().put(12, (byte)0);
      this.getDataTracker().put(13, (byte)0);
      this.getDataTracker().put(14, (byte)0);
   }

   @Override
   public int getArmorProtection() {
      int var1 = super.getArmorProtection() + 2;
      if (var1 > 20) {
         var1 = 20;
      }

      return var1;
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean canBreakDoors) {
      if (this.canBreakDoors != canBreakDoors) {
         this.canBreakDoors = canBreakDoors;
         if (canBreakDoors) {
            this.goalSelector.addGoal(1, this.breakDoorGoal);
         } else {
            this.goalSelector.removeGoal(this.breakDoorGoal);
         }
      }
   }

   @Override
   public boolean isBaby() {
      return this.getDataTracker().getByte(12) == 1;
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      if (this.isBaby()) {
         this.experiencePoints = (int)((float)this.experiencePoints * 2.5F);
      }

      return super.getXpDrop(playerEntity);
   }

   public void setBaby(boolean isBaby) {
      this.getDataTracker().update(12, (byte)(isBaby ? 1 : 0));
      if (this.world != null && !this.world.isClient) {
         IEntityAttributeInstance var2 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
         var2.removeModifier(BABY_SPEED_BOOST_MODIFIER);
         if (isBaby) {
            var2.addModifier(BABY_SPEED_BOOST_MODIFIER);
         }
      }

      this.setDimensions(isBaby);
   }

   public boolean isVillager() {
      return this.getDataTracker().getByte(13) == 1;
   }

   public void setVillager(boolean isVillager) {
      this.getDataTracker().update(13, (byte)(isVillager ? 1 : 0));
   }

   @Override
   public void tickAI() {
      if (this.world.isSunny() && !this.world.isClient && !this.isBaby()) {
         float var1 = this.getBrightness(1.0F);
         BlockPos var2 = new BlockPos(this.x, (double)Math.round(this.y), this.z);
         if (var1 > 0.5F && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.world.hasSkyAccess(var2)) {
            boolean var3 = true;
            ItemStack var4 = this.getStackInInventory(4);
            if (var4 != null) {
               if (var4.isDamageable()) {
                  var4.setDamage(var4.getDamage() + this.random.nextInt(2));
                  if (var4.getDamage() >= var4.getMaxDamage()) {
                     this.renderBrokenItem(var4);
                     this.setEquipmentStack(4, null);
                  }
               }

               var3 = false;
            }

            if (var3) {
               this.setOnFireFor(8);
            }
         }
      }

      if (this.hasVehicle() && this.getTargetEntity() != null && this.vehicle instanceof ChickenEntity) {
         ((MobEntity)this.vehicle).getNavigation().startMovingAlong(this.getNavigation().getCurrentPath(), 1.5);
      }

      super.tickAI();
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (super.damage(source, amount)) {
         LivingEntity var3 = this.getTargetEntity();
         if (var3 == null && source.getAttacker() instanceof LivingEntity) {
            var3 = (LivingEntity)source.getAttacker();
         }

         if (var3 != null
            && this.world.getDifficulty() == Difficulty.HARD
            && (double)this.random.nextFloat() < this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).get()) {
            int var4 = MathHelper.floor(this.x);
            int var5 = MathHelper.floor(this.y);
            int var6 = MathHelper.floor(this.z);
            ZombieEntity var7 = new ZombieEntity(this.world);

            for(int var8 = 0; var8 < 50; ++var8) {
               int var9 = var4 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int var10 = var5 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               int var11 = var6 + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
               if (World.hasSolidTop(this.world, new BlockPos(var9, var10 - 1, var11)) && this.world.getRawBrightness(new BlockPos(var9, var10, var11)) < 10) {
                  var7.setPosition((double)var9, (double)var10, (double)var11);
                  if (this.world.canBuildIn(var7.getBoundingBox(), var7)
                     && this.world.getCollisions(var7, var7.getBoundingBox()).isEmpty()
                     && !this.world.containsLiquid(var7.getBoundingBox())) {
                     this.world.addEntity(var7);
                     var7.setAttackTarget(var3);
                     var7.initialize(this.world.getLocalDifficulty(new BlockPos(var7)), null);
                     this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
                     var7.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      if (!this.world.isClient && this.isConverting()) {
         int var1 = this.getTicksUntilConversionDecrement();
         this.ticksUntilConversion -= var1;
         if (this.ticksUntilConversion <= 0) {
            this.convertToVillager();
         }
      }

      super.tick();
   }

   @Override
   public boolean attack(Entity entity) {
      boolean var2 = super.attack(entity);
      if (var2) {
         int var3 = this.world.getDifficulty().getIndex();
         if (this.getStackInHand() == null && this.isOnFire() && this.random.nextFloat() < (float)var3 * 0.3F) {
            entity.setOnFireFor(2 * var3);
         }
      }

      return var2;
   }

   @Override
   protected String getAmbientSound() {
      return "mob.zombie.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.zombie.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.zombie.death";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.zombie.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.ROTTEN_FLESH;
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.UNDEAD;
   }

   @Override
   protected void dropRareItem() {
      switch(this.random.nextInt(3)) {
         case 0:
            this.dropItem(Items.IRON_INGOT, 1);
            break;
         case 1:
            this.dropItem(Items.CARROT, 1);
            break;
         case 2:
            this.dropItem(Items.POTATO, 1);
      }
   }

   @Override
   protected void initSpawningEquipment(LocalDifficulty localDifficulty) {
      super.initSpawningEquipment(localDifficulty);
      if (this.random.nextFloat() < (this.world.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int var2 = this.random.nextInt(3);
         if (var2 == 0) {
            this.setEquipmentStack(0, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setEquipmentStack(0, new ItemStack(Items.IRON_SHOVEL));
         }
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      if (this.isBaby()) {
         nbt.putBoolean("IsBaby", true);
      }

      if (this.isVillager()) {
         nbt.putBoolean("IsVillager", true);
      }

      nbt.putInt("ConversionTime", this.isConverting() ? this.ticksUntilConversion : -1);
      nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.getBoolean("IsBaby")) {
         this.setBaby(true);
      }

      if (nbt.getBoolean("IsVillager")) {
         this.setVillager(true);
      }

      if (nbt.isType("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
         this.setConversionTime(nbt.getInt("ConversionTime"));
      }

      this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
   }

   @Override
   public void onKill(LivingEntity victim) {
      super.onKill(victim);
      if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && victim instanceof VillagerEntity) {
         if (this.world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return;
         }

         ZombieEntity var2 = new ZombieEntity(this.world);
         var2.copyPositionAndRotationFrom(victim);
         this.world.removeEntity(victim);
         var2.initialize(this.world.getLocalDifficulty(new BlockPos(var2)), null);
         var2.setVillager(true);
         if (victim.isBaby()) {
            var2.setBaby(true);
         }

         this.world.addEntity(var2);
         this.world.doEvent(null, 1016, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
      }
   }

   @Override
   public float getEyeHeight() {
      float var1 = 1.74F;
      if (this.isBaby()) {
         var1 = (float)((double)var1 - 0.81);
      }

      return var1;
   }

   @Override
   protected boolean m_33ahawbcj(ItemStack c_72owraavl) {
      return c_72owraavl.getItem() == Items.EGG && this.isBaby() && this.hasVehicle() ? false : super.m_33ahawbcj(c_72owraavl);
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      float var3 = localDifficulty.getMultiplier();
      this.setCanPickupLoot(this.random.nextFloat() < 0.55F * var3);
      if (entityData == null) {
         entityData = new ZombieEntity.Data(this.world.random.nextFloat() < 0.05F, this.world.random.nextFloat() < 0.05F);
      }

      if (entityData instanceof ZombieEntity.Data) {
         ZombieEntity.Data var4 = (ZombieEntity.Data)entityData;
         if (var4.isVillager) {
            this.setVillager(true);
         }

         if (var4.isBaby) {
            this.setBaby(true);
            if ((double)this.world.random.nextFloat() < 0.05) {
               List var5 = this.world.getEntities(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityFilter.NOT_RIDING);
               if (!var5.isEmpty()) {
                  ChickenEntity var6 = (ChickenEntity)var5.get(0);
                  var6.m_55jhlznqt(true);
                  this.startRiding(var6);
               }
            } else if ((double)this.world.random.nextFloat() < 0.05) {
               ChickenEntity var10 = new ChickenEntity(this.world);
               var10.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
               var10.initialize(localDifficulty, null);
               var10.m_55jhlznqt(true);
               this.world.addEntity(var10);
               this.startRiding(var10);
            }
         }
      }

      this.setCanBreakDoors(this.random.nextFloat() < var3 * 0.1F);
      this.initSpawningEquipment(localDifficulty);
      this.enchantEquipmentItemStack(localDifficulty);
      if (this.getStackInInventory(4) == null) {
         Calendar var8 = this.world.getCalendar();
         if (var8.get(2) + 1 == 10 && var8.get(5) == 31 && this.random.nextFloat() < 0.25F) {
            this.setEquipmentStack(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
            this.inventoryDropChances[4] = 0.0F;
         }
      }

      this.initializeAttribute(EntityAttributes.KNOCKBACK_RESISTANCE)
         .addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05F, 0));
      double var9 = this.random.nextDouble() * 1.5 * (double)var3;
      if (var9 > 1.0) {
         this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", var9, 2));
      }

      if (this.random.nextFloat() < var3 * 0.05F) {
         this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, 0));
         this.initializeAttribute(EntityAttributes.MAX_HEALTH)
            .addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, 2));
         this.setCanBreakDoors(true);
      }

      return entityData;
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.GOLDEN_APPLE && var2.getMetadata() == 0 && this.isVillager() && this.hasStatusEffect(StatusEffect.WEAKNESS)) {
         if (!player.abilities.creativeMode) {
            --var2.size;
         }

         if (var2.size <= 0) {
            player.inventory.setStack(player.inventory.selectedSlot, null);
         }

         if (!this.world.isClient) {
            this.setConversionTime(this.random.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void setConversionTime(int time) {
      this.ticksUntilConversion = time;
      this.getDataTracker().update(14, (byte)1);
      this.removeStatusEffect(StatusEffect.WEAKNESS.id);
      this.addStatusEffect(new StatusEffectInstance(StatusEffect.STRENGTH.id, time, Math.min(this.world.getDifficulty().getIndex() - 1, 0)));
      this.world.doEntityEvent(this, (byte)16);
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 16) {
         if (!this.isSilent()) {
            this.world
               .playSound(
                  this.x + 0.5, this.y + 0.5, this.z + 0.5, "mob.zombie.remedy", 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false
               );
         }
      } else {
         super.doEvent(event);
      }
   }

   @Override
   protected boolean canDespawn() {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return this.getDataTracker().getByte(14) == 1;
   }

   protected void convertToVillager() {
      VillagerEntity var1 = new VillagerEntity(this.world);
      var1.copyPositionAndRotationFrom(this);
      var1.initialize(this.world.getLocalDifficulty(new BlockPos(var1)), null);
      var1.setConvertedZombie();
      if (this.isBaby()) {
         var1.setBreedingAge(-24000);
      }

      this.world.removeEntity(this);
      this.world.addEntity(var1);
      var1.addStatusEffect(new StatusEffectInstance(StatusEffect.NAUSEA.id, 200, 0));
      this.world.doEvent(null, 1017, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
   }

   protected int getTicksUntilConversionDecrement() {
      int var1 = 1;
      if (this.random.nextFloat() < 0.01F) {
         int var2 = 0;

         for(int var3 = (int)this.x - 4; var3 < (int)this.x + 4 && var2 < 14; ++var3) {
            for(int var4 = (int)this.y - 4; var4 < (int)this.y + 4 && var2 < 14; ++var4) {
               for(int var5 = (int)this.z - 4; var5 < (int)this.z + 4 && var2 < 14; ++var5) {
                  Block var6 = this.world.getBlockState(new BlockPos(var3, var4, var5)).getBlock();
                  if (var6 == Blocks.IRON_BARS || var6 == Blocks.BED) {
                     if (this.random.nextFloat() < 0.3F) {
                        ++var1;
                     }

                     ++var2;
                  }
               }
            }
         }
      }

      return var1;
   }

   public void setDimensions(boolean isBaby) {
      this.setDimensions(isBaby ? 0.5F : 1.0F);
   }

   @Override
   protected final void setDimensions(float width, float height) {
      boolean var3 = this.zombieWidth > 0.0F && this.zombieHeight > 0.0F;
      this.zombieWidth = width;
      this.zombieHeight = height;
      if (!var3) {
         this.setDimensions(1.0F);
      }
   }

   protected final void setDimensions(float scale) {
      super.setDimensions(this.zombieWidth * scale, this.zombieHeight * scale);
   }

   @Override
   public double getRideHeight() {
      return super.getRideHeight() - 0.5;
   }

   @Override
   public void onKilled(DamageSource source) {
      super.onKilled(source);
      if (source.getAttacker() instanceof CreeperEntity
         && !(this instanceof ZombiePigmanEntity)
         && ((CreeperEntity)source.getAttacker()).isCharged()
         && ((CreeperEntity)source.getAttacker()).shouldDropMobHead()) {
         ((CreeperEntity)source.getAttacker()).addMobHeadDrop();
         this.dropItem(new ItemStack(Items.SKULL, 1, 2), 0.0F);
      }
   }

   class Data implements EntityData {
      public boolean isBaby = false;
      public boolean isVillager = false;

      private Data(boolean isBaby, boolean isVillager) {
         this.isBaby = isBaby;
         this.isVillager = isVillager;
      }
   }
}
