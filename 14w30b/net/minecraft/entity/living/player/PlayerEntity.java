package net.minecraft.entity.living.player;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.Monster;
import net.minecraft.entity.living.mob.hostile.HostileEntity;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragon;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonPart;
import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryLock;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.inventory.menu.PlayerMenu;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class PlayerEntity extends LivingEntity {
   public PlayerInventory inventory = new PlayerInventory(this);
   private EnderChestInventory enderchest = new EnderChestInventory();
   public InventoryMenu playerMenu;
   public InventoryMenu menu;
   protected HungerManager hungerManager = new HungerManager();
   protected int pressedJumpTwiceTimer;
   public float prevStrideDistance;
   public float strideDistance;
   public int xpCooldown;
   public double lastCapeX;
   public double lastCapeY;
   public double lastCapeZ;
   public double capeX;
   public double capeY;
   public double capeZ;
   protected boolean sleeping;
   public BlockPos sleepingPos;
   private int sleepTimer;
   public float sleepOffsetX;
   @Environment(EnvType.CLIENT)
   public float sleepOffsetY;
   public float sleepOffsetZ;
   private BlockPos spawnPoint;
   private boolean respawnForced;
   private BlockPos minecartTravelStartPos;
   public PlayerAbilities abilities = new PlayerAbilities();
   public int xpLevel;
   public int xp;
   public float xpProgress;
   private int f_87bysmrvy;
   private ItemStack heldItem;
   private int itemUseTimer;
   protected float speed = 0.1F;
   protected float flyingSpeed = 0.02F;
   private int lastXpSoundTime;
   private final GameProfile profiler;
   private boolean reducedDebugInfo = false;
   public FishingBobberEntity fishingBobber;

   public PlayerEntity(World world, GameProfile profile) {
      super(world);
      this.uuid = getUuid(profile);
      this.profiler = profile;
      this.playerMenu = new PlayerMenu(this.inventory, !world.isClient, this);
      this.menu = this.playerMenu;
      BlockPos var3 = world.getSpawnPoint();
      this.refreshPositionAndAngles((double)var3.getX() + 0.5, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5, 0.0F, 0.0F);
      this.halfCircleDegrees = 180.0F;
      this.fireResistance = 20;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributes().registerAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(1.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.1F);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)0);
      this.dataTracker.put(17, 0.0F);
      this.dataTracker.put(18, 0);
      this.dataTracker.put(10, (byte)0);
   }

   @Environment(EnvType.CLIENT)
   public ItemStack getItemInHand() {
      return this.heldItem;
   }

   @Environment(EnvType.CLIENT)
   public int getItemUseTimer() {
      return this.itemUseTimer;
   }

   public boolean isHoldingItem() {
      return this.heldItem != null;
   }

   @Environment(EnvType.CLIENT)
   public int getHeldItemCooldown() {
      return this.isHoldingItem() ? this.heldItem.getUseDuration() - this.itemUseTimer : 0;
   }

   public void stopUsingHand() {
      if (this.heldItem != null) {
         this.heldItem.stopUsing(this.world, this, this.itemUseTimer);
      }

      this.emptyHand();
   }

   public void emptyHand() {
      this.heldItem = null;
      this.itemUseTimer = 0;
      if (!this.world.isClient) {
         this.setSwimming(false);
      }
   }

   public boolean isSwordBlocking() {
      return this.isHoldingItem() && this.heldItem.getItem().getUseAction(this.heldItem) == UseAction.BLOCK;
   }

   @Override
   public void tick() {
      this.noClip = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.heldItem != null) {
         ItemStack var1 = this.inventory.getMainHandStack();
         if (var1 == this.heldItem) {
            if (this.itemUseTimer <= 25 && this.itemUseTimer % 4 == 0) {
               this.onConsumeItem(var1, 5);
            }

            if (--this.itemUseTimer == 0 && !this.world.isClient) {
               this.finishUsingItem();
            }
         } else {
            this.emptyHand();
         }
      }

      if (this.xpCooldown > 0) {
         --this.xpCooldown;
      }

      if (this.isSleeping()) {
         ++this.sleepTimer;
         if (this.sleepTimer > 100) {
            this.sleepTimer = 100;
         }

         if (!this.world.isClient) {
            if (!this.isValidSleepingPos()) {
               this.wakeUp(true, true, false);
            } else if (this.world.isSunny()) {
               this.wakeUp(false, true, true);
            }
         }
      } else if (this.sleepTimer > 0) {
         ++this.sleepTimer;
         if (this.sleepTimer >= 110) {
            this.sleepTimer = 0;
         }
      }

      super.tick();
      if (!this.world.isClient && this.menu != null && !this.menu.isValid(this)) {
         this.closeMenu();
         this.menu = this.playerMenu;
      }

      if (this.isOnFire() && this.abilities.invulnerable) {
         this.extinguish();
      }

      this.lastCapeX = this.capeX;
      this.lastCapeY = this.capeY;
      this.lastCapeZ = this.capeZ;
      double var9 = this.x - this.capeX;
      double var3 = this.y - this.capeY;
      double var5 = this.z - this.capeZ;
      double var7 = 10.0;
      if (var9 > var7) {
         this.lastCapeX = this.capeX = this.x;
      }

      if (var5 > var7) {
         this.lastCapeZ = this.capeZ = this.z;
      }

      if (var3 > var7) {
         this.lastCapeY = this.capeY = this.y;
      }

      if (var9 < -var7) {
         this.lastCapeX = this.capeX = this.x;
      }

      if (var5 < -var7) {
         this.lastCapeZ = this.capeZ = this.z;
      }

      if (var3 < -var7) {
         this.lastCapeY = this.capeY = this.y;
      }

      this.capeX += var9 * 0.25;
      this.capeZ += var5 * 0.25;
      this.capeY += var3 * 0.25;
      if (this.vehicle == null) {
         this.minecartTravelStartPos = null;
      }

      if (!this.world.isClient) {
         this.hungerManager.tick(this);
         this.incrementStat(Stats.MINUTES_PLAYED);
         if (this.isAlive()) {
            this.incrementStat(Stats.TIME_SINCE_DEATH);
         }
      }
   }

   @Override
   public int getMaxNetherPortalTime() {
      return this.abilities.invulnerable ? 0 : 80;
   }

   @Override
   protected String getSwimSound() {
      return "game.player.swim";
   }

   @Override
   protected String getSplashSound() {
      return "game.player.swim.splash";
   }

   @Override
   public int getDefaultNetherPortalCooldown() {
      return 10;
   }

   @Override
   public void playSound(String id, float volume, float pitch) {
      this.world.playSound(this, id, volume, pitch);
   }

   protected void onConsumeItem(ItemStack item, int particleCount) {
      if (item.getUseAction() == UseAction.DRINK) {
         this.playSound("random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
      }

      if (item.getUseAction() == UseAction.EAT) {
         for(int var3 = 0; var3 < particleCount; ++var3) {
            Vec3d var4 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            var4 = var4.rotateX(-this.pitch * (float) Math.PI / 180.0F);
            var4 = var4.rotateY(-this.yaw * (float) Math.PI / 180.0F);
            double var5 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d var7 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, var5, 0.6);
            var7 = var7.rotateX(-this.pitch * (float) Math.PI / 180.0F);
            var7 = var7.rotateY(-this.yaw * (float) Math.PI / 180.0F);
            var7 = var7.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
            if (item.isItemStackable()) {
               this.world
                  .addParticle(
                     ParticleType.ITEM_CRACK, var7.x, var7.y, var7.z, var4.x, var4.y + 0.05, var4.z, Item.getRawId(item.getItem()), item.getMetadata()
                  );
            } else {
               this.world.addParticle(ParticleType.ITEM_CRACK, var7.x, var7.y, var7.z, var4.x, var4.y + 0.05, var4.z, Item.getRawId(item.getItem()));
            }
         }

         this.playSound("random.eat", 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   protected void finishUsingItem() {
      if (this.heldItem != null) {
         this.onConsumeItem(this.heldItem, 16);
         int var1 = this.heldItem.size;
         ItemStack var2 = this.heldItem.finishUsing(this.world, this);
         if (var2 != this.heldItem || var2 != null && var2.size != var1) {
            this.inventory.inventorySlots[this.inventory.selectedSlot] = var2;
            if (var2.size == 0) {
               this.inventory.inventorySlots[this.inventory.selectedSlot] = null;
            }
         }

         this.emptyHand();
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 9) {
         this.finishUsingItem();
      } else if (event == 23) {
         this.reducedDebugInfo = false;
      } else if (event == 22) {
         this.reducedDebugInfo = true;
      } else {
         super.doEvent(event);
      }
   }

   @Override
   protected boolean isDead() {
      return this.getHealth() <= 0.0F || this.isSleeping();
   }

   protected void closeMenu() {
      this.menu = this.playerMenu;
   }

   @Override
   public void tickRiding() {
      if (!this.world.isClient && this.isSneaking()) {
         this.startRiding(null);
         this.setSneaking(false);
      } else {
         double var1 = this.x;
         double var3 = this.y;
         double var5 = this.z;
         float var7 = this.yaw;
         float var8 = this.pitch;
         super.tickRiding();
         this.prevStrideDistance = this.strideDistance;
         this.strideDistance = 0.0F;
         this.tickRidingRelatedStats(this.x - var1, this.y - var3, this.z - var5);
         if (this.vehicle instanceof PigEntity) {
            this.pitch = var8;
            this.yaw = var7;
            this.bodyYaw = ((PigEntity)this.vehicle).bodyYaw;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void postSpawn() {
      this.setDimensions(0.6F, 1.8F);
      super.postSpawn();
      this.setHealth(this.getMaxHealth());
      this.deathTicks = 0;
   }

   @Override
   protected void tickAISetup() {
      super.tickAISetup();
      this.updateHandSwing();
      this.headYaw = this.yaw;
   }

   @Override
   public void tickAI() {
      if (this.pressedJumpTwiceTimer > 0) {
         --this.pressedJumpTwiceTimer;
      }

      if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
         if (this.getHealth() < this.getMaxHealth() && this.time % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.hungerManager.needsFood() && this.time % 10 == 0) {
            this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
         }
      }

      this.inventory.tickItems();
      this.prevStrideDistance = this.strideDistance;
      super.tickAI();
      IEntityAttributeInstance var1 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
      if (!this.world.isClient) {
         var1.setBase((double)this.abilities.getWalkSpeed());
      }

      this.airSpeed = this.flyingSpeed;
      if (this.isSprinting()) {
         this.airSpeed = (float)((double)this.airSpeed + (double)this.flyingSpeed * 0.3);
      }

      this.setMovementSpeed((float)var1.get());
      float var2 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      float var3 = (float)(Math.atan(-this.velocityY * 0.2F) * 15.0);
      if (var2 > 0.1F) {
         var2 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var3 = 0.0F;
      }

      this.strideDistance += (var2 - this.strideDistance) * 0.4F;
      this.cameraPitch += (var3 - this.cameraPitch) * 0.8F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         Object var4 = null;
         Box var8;
         if (this.vehicle != null && !this.vehicle.removed) {
            var8 = this.getBoundingBox().union(this.vehicle.getBoundingBox()).expand(1.0, 0.0, 1.0);
         } else {
            var8 = this.getBoundingBox().expand(1.0, 0.5, 1.0);
         }

         List var5 = this.world.getEntities(this, var8);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            Entity var7 = (Entity)var5.get(var6);
            if (!var7.removed) {
               this.collideWithEntity(var7);
            }
         }
      }
   }

   private void collideWithEntity(Entity entity) {
      entity.onPlayerCollision(this);
   }

   public int getScore() {
      return this.dataTracker.getInt(18);
   }

   public void setScore(int score) {
      this.dataTracker.update(18, score);
   }

   public void addToScore(int amount) {
      int var2 = this.getScore();
      this.dataTracker.update(18, var2 + amount);
   }

   @Override
   public void onKilled(DamageSource source) {
      super.onKilled(source);
      this.setDimensions(0.2F, 0.2F);
      this.setPosition(this.x, this.y, this.z);
      this.velocityY = 0.1F;
      if (this.getName().equals("Notch")) {
         this.dropItem(new ItemStack(Items.APPLE, 1), true, false);
      }

      if (!this.world.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAll();
      }

      if (source != null) {
         this.velocityX = (double)(-MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
         this.velocityZ = (double)(-MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
      } else {
         this.velocityX = this.velocityZ = 0.0;
      }

      this.incrementStat(Stats.DEATHS);
      this.clearStat(Stats.TIME_SINCE_DEATH);
   }

   @Override
   protected String getHurtSound() {
      return "game.player.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "game.player.die";
   }

   @Override
   public void onKillEntity(Entity entity, int score) {
      this.addToScore(score);
      Collection var3 = this.getScoreboard().getObjectives(ScoreboardCriterion.TOTAL_KILL_COUNT);
      if (entity instanceof PlayerEntity) {
         this.incrementStat(Stats.PLAYERS_KILLED);
         var3.addAll(this.getScoreboard().getObjectives(ScoreboardCriterion.PLAYER_KILL_COUNT));
         var3.addAll(this.m_01oqzgzao(entity));
      } else {
         this.incrementStat(Stats.MOBS_KILLED);
      }

      for(ScoreboardObjective var5 : var3) {
         ScoreboardScore var6 = this.getScoreboard().getScore(this.getName(), var5);
         var6.increment();
      }
   }

   private Collection m_01oqzgzao(Entity c_47ldwddrb) {
      Team var2 = this.getScoreboard().getTeamOfMember(this.getName());
      if (var2 != null) {
         int var3 = var2.getColor().getIndex();
         if (var3 >= 0 && var3 < ScoreboardCriterion.KILLED_BY_TEAM_BY_COLOR.length) {
            for(ScoreboardObjective var5 : this.getScoreboard().getObjectives(ScoreboardCriterion.KILLED_BY_TEAM_BY_COLOR[var3])) {
               ScoreboardScore var6 = this.getScoreboard().getScore(c_47ldwddrb.getName(), var5);
               var6.increment();
            }
         }
      }

      Team var7 = this.getScoreboard().getTeamOfMember(c_47ldwddrb.getName());
      if (var7 != null) {
         int var8 = var7.getColor().getIndex();
         if (var8 >= 0 && var8 < ScoreboardCriterion.TEAM_KILL_BY_COLOR.length) {
            return this.getScoreboard().getObjectives(ScoreboardCriterion.TEAM_KILL_BY_COLOR[var8]);
         }
      }

      return Lists.newArrayList();
   }

   public ItemEntity dropItem(boolean wholeStack) {
      return this.dropItem(
         this.inventory
            .removeStack(this.inventory.selectedSlot, wholeStack && this.inventory.getMainHandStack() != null ? this.inventory.getMainHandStack().size : 1),
         false,
         true
      );
   }

   public ItemEntity dropItem(ItemStack item, boolean unused) {
      return this.dropItem(item, false, false);
   }

   public ItemEntity dropItem(ItemStack item, boolean velocityFromPlayerDirection, boolean thrownByPlayer) {
      if (item == null) {
         return null;
      } else if (item.size == 0) {
         return null;
      } else {
         double var4 = this.y - 0.3F + (double)this.getEyeHeight();
         ItemEntity var6 = new ItemEntity(this.world, this.x, var4, this.z, item);
         var6.setPickupCooldown(40);
         if (thrownByPlayer) {
            var6.setThrower(this.getName());
         }

         float var7 = 0.1F;
         if (velocityFromPlayerDirection) {
            float var8 = this.random.nextFloat() * 0.5F;
            float var9 = this.random.nextFloat() * (float) Math.PI * 2.0F;
            var6.velocityX = (double)(-MathHelper.sin(var9) * var8);
            var6.velocityZ = (double)(MathHelper.cos(var9) * var8);
            var6.velocityY = 0.2F;
         } else {
            var7 = 0.3F;
            var6.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var7);
            var6.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var7);
            var6.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * var7 + 0.1F);
            var7 = 0.02F;
            float var13 = this.random.nextFloat() * (float) Math.PI * 2.0F;
            var7 *= this.random.nextFloat();
            var6.velocityX += Math.cos((double)var13) * (double)var7;
            var6.velocityY += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
            var6.velocityZ += Math.sin((double)var13) * (double)var7;
         }

         this.spawnDroppedItem(var6);
         if (thrownByPlayer) {
            this.incrementStat(Stats.DROPS);
         }

         return var6;
      }
   }

   protected void spawnDroppedItem(ItemEntity item) {
      this.world.addEntity(item);
   }

   public float getMiningSpeed(Block block, boolean withEffectiveTool) {
      float var3 = this.inventory.getMiningSpeed(block);
      if (var3 > 1.0F) {
         int var4 = EnchantmentHelper.getEfficiencyLevel(this);
         ItemStack var5 = this.inventory.getMainHandStack();
         if (var4 > 0 && var5 != null) {
            float var6 = (float)(var4 * var4 + 1);
            if (!var5.canEffectivelyMine(block) && !(var3 > 1.0F)) {
               var3 += var6 * 0.08F;
            } else {
               var3 += var6;
            }
         }
      }

      if (this.hasStatusEffect(StatusEffect.HASTE)) {
         var3 *= 1.0F + (float)(this.getEffectInstance(StatusEffect.HASTE).getAmplifier() + 1) * 0.2F;
      }

      if (this.hasStatusEffect(StatusEffect.MINING_FAIGUE)) {
         float var7 = 1.0F;
         switch(this.getEffectInstance(StatusEffect.MINING_FAIGUE).getAmplifier()) {
            case 0:
               var7 = 0.3F;
               break;
            case 1:
               var7 = 0.09F;
               break;
            case 2:
               var7 = 0.0027F;
               break;
            case 3:
            default:
               var7 = 8.1E-4F;
         }

         var3 *= var7;
      }

      if (this.isSubmergedIn(Material.WATER) && !EnchantmentHelper.getAquaAffinityLevel(this)) {
         var3 /= 5.0F;
      }

      if (!this.onGround) {
         var3 /= 5.0F;
      }

      return var3;
   }

   public boolean canBreakBlock(Block block) {
      return this.inventory.canToolBreak(block);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.uuid = getUuid(this.profiler);
      NbtList var2 = nbt.getList("Inventory", 10);
      this.inventory.readNbt(var2);
      this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
      this.sleeping = nbt.getBoolean("Sleeping");
      this.sleepTimer = nbt.getShort("SleepTimer");
      this.xpProgress = nbt.getFloat("XpP");
      this.xpLevel = nbt.getInt("XpLevel");
      this.xp = nbt.getInt("XpTotal");
      this.f_87bysmrvy = nbt.getInt("XpSeed");
      if (this.f_87bysmrvy == 0) {
         this.f_87bysmrvy = this.random.nextInt();
      }

      this.setScore(nbt.getInt("Score"));
      if (this.sleeping) {
         this.sleepingPos = new BlockPos(this);
         this.wakeUp(true, true, false);
      }

      if (nbt.isType("SpawnX", 99) && nbt.isType("SpawnY", 99) && nbt.isType("SpawnZ", 99)) {
         this.spawnPoint = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
         this.respawnForced = nbt.getBoolean("SpawnForced");
      }

      this.hungerManager.readNbt(nbt);
      this.abilities.readNbt(nbt);
      if (nbt.isType("EnderItems", 9)) {
         NbtList var3 = nbt.getList("EnderItems", 10);
         this.enderchest.readNbt(var3);
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.put("Inventory", this.inventory.writeNbt(new NbtList()));
      nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
      nbt.putBoolean("Sleeping", this.sleeping);
      nbt.putShort("SleepTimer", (short)this.sleepTimer);
      nbt.putFloat("XpP", this.xpProgress);
      nbt.putInt("XpLevel", this.xpLevel);
      nbt.putInt("XpTotal", this.xp);
      nbt.putInt("XpSeed", this.f_87bysmrvy);
      nbt.putInt("Score", this.getScore());
      if (this.spawnPoint != null) {
         nbt.putInt("SpawnX", this.spawnPoint.getX());
         nbt.putInt("SpawnY", this.spawnPoint.getY());
         nbt.putInt("SpawnZ", this.spawnPoint.getZ());
         nbt.putBoolean("SpawnForced", this.respawnForced);
      }

      this.hungerManager.writeNbt(nbt);
      this.abilities.writeNbt(nbt);
      nbt.put("EnderItems", this.enderchest.toNbt());
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (this.abilities.invulnerable && !source.isOutOfWorld()) {
         return false;
      } else {
         this.despawnTicks = 0;
         if (this.getHealth() <= 0.0F) {
            return false;
         } else {
            if (this.isSleeping() && !this.world.isClient) {
               this.wakeUp(true, true, false);
            }

            if (source.isScaledWithDifficulty()) {
               if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                  amount = 0.0F;
               }

               if (this.world.getDifficulty() == Difficulty.EASY) {
                  amount = amount / 2.0F + 1.0F;
               }

               if (this.world.getDifficulty() == Difficulty.HARD) {
                  amount = amount * 3.0F / 2.0F;
               }
            }

            if (amount == 0.0F) {
               return false;
            } else {
               Entity var3 = source.getAttacker();
               if (var3 instanceof ArrowEntity && ((ArrowEntity)var3).shooter != null) {
                  var3 = ((ArrowEntity)var3).shooter;
               }

               return super.damage(source, amount);
            }
         }
      }
   }

   public boolean canAttack(PlayerEntity player) {
      AbstractTeam var2 = this.getScoreboardTeam();
      AbstractTeam var3 = player.getScoreboardTeam();
      if (var2 == null) {
         return true;
      } else {
         return !var2.isEqual(var3) ? true : var2.allowFriendlyFire();
      }
   }

   @Override
   protected void damageArmor(float value) {
      this.inventory.damageArmor(value);
   }

   @Override
   public int getArmorProtection() {
      return this.inventory.getArmorProtectionValue();
   }

   public float getArmorEquippedRatio() {
      int var1 = 0;

      for(ItemStack var5 : this.inventory.armorSlots) {
         if (var5 != null) {
            ++var1;
         }
      }

      return (float)var1 / (float)this.inventory.armorSlots.length;
   }

   @Override
   protected void applyDamage(DamageSource source, float damage) {
      if (!this.isInvulnerable(source)) {
         if (!source.bypassesArmor() && this.isSwordBlocking() && damage > 0.0F) {
            damage = (1.0F + damage) * 0.5F;
         }

         damage = this.damageAfterArmorResistance(source, damage);
         damage = this.damageAfterEffectsAndEnchantments(source, damage);
         float var7 = Math.max(damage - this.getAbsorption(), 0.0F);
         this.setAbsorption(this.getAbsorption() - (damage - var7));
         if (var7 != 0.0F) {
            this.addFatigue(source.getExhaustion());
            float var4 = this.getHealth();
            this.setHealth(this.getHealth() - var7);
            this.getDamageTracker().onDamage(source, var4, var7);
            if (var7 < 3.4028235E37F) {
               this.incrementStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
            }
         }
      }
   }

   public void openSignEditor(SignBlockEntity sign) {
   }

   public void openCommandBlockScreen(CommandExecutor executor) {
   }

   public void openTraderMenu(Trader trader) {
   }

   public void openInventoryMenu(Inventory inventory) {
   }

   public void openHorseMenu(HorseBaseEntity horse, Inventory inventory) {
   }

   public void openMenu(MenuProvider menuProvider) {
   }

   public void openEditBookScreen(ItemStack book) {
   }

   public boolean interact(Entity target) {
      if (this.isSpectator()) {
         if (target instanceof Inventory) {
            this.openInventoryMenu((Inventory)target);
         }

         return false;
      } else {
         ItemStack var2 = this.getMainHandStack();
         ItemStack var3 = var2 != null ? var2.copy() : null;
         if (!target.interact(this)) {
            if (var2 != null && target instanceof LivingEntity) {
               if (this.abilities.creativeMode) {
                  var2 = var3;
               }

               if (var2.canInteract(this, (LivingEntity)target)) {
                  if (var2.size <= 0 && !this.abilities.creativeMode) {
                     this.clearSelectedSlot();
                  }

                  return true;
               }
            }

            return false;
         } else {
            if (var2 != null && var2 == this.getMainHandStack()) {
               if (var2.size <= 0 && !this.abilities.creativeMode) {
                  this.clearSelectedSlot();
               } else if (var2.size < var3.size && this.abilities.creativeMode) {
                  var2.size = var3.size;
               }
            }

            return true;
         }
      }
   }

   public ItemStack getMainHandStack() {
      return this.inventory.getMainHandStack();
   }

   public void clearSelectedSlot() {
      this.inventory.setStack(this.inventory.selectedSlot, null);
   }

   @Override
   public double getRideHeight() {
      return -0.35;
   }

   public void attack(Entity target) {
      if (target.canBePunched()) {
         if (!target.onPunched(this)) {
            float var2 = (float)this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).get();
            int var3 = 0;
            float var4 = 0.0F;
            if (target instanceof LivingEntity) {
               var4 = EnchantmentHelper.m_01divsmlb(this, (LivingEntity)target);
               var3 += EnchantmentHelper.getKnockbackLevel(this, (LivingEntity)target);
            }

            if (this.isSprinting()) {
               ++var3;
            }

            if (var2 > 0.0F || var4 > 0.0F) {
               boolean var5 = this.fallDistance > 0.0F
                  && !this.onGround
                  && !this.isClimbing()
                  && !this.isInWater()
                  && !this.hasStatusEffect(StatusEffect.BLINDNESS)
                  && this.vehicle == null
                  && target instanceof LivingEntity;
               if (var5 && var2 > 0.0F) {
                  var2 *= 1.5F;
               }

               var2 += var4;
               boolean var6 = false;
               int var7 = EnchantmentHelper.getFireAspectLevel(this);
               if (target instanceof LivingEntity && var7 > 0 && !target.isOnFire()) {
                  var6 = true;
                  target.setOnFireFor(1);
               }

               double var8 = target.velocityX;
               double var10 = target.velocityY;
               double var12 = target.velocityZ;
               boolean var14 = target.damage(DamageSource.player(this), var2);
               if (var14) {
                  if (var3 > 0) {
                     target.addVelocity(
                        (double)(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F),
                        0.1,
                        (double)(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F)
                     );
                     this.velocityX *= 0.6;
                     this.velocityZ *= 0.6;
                     this.setSprinting(false);
                  }

                  if (target instanceof ServerPlayerEntity && target.damaged) {
                     ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityS2CPacket(target));
                     target.damaged = false;
                     target.velocityX = var8;
                     target.velocityY = var10;
                     target.velocityZ = var12;
                  }

                  if (var5) {
                     this.addCritParticles(target);
                  }

                  if (var4 > 0.0F) {
                     this.addEnchantedCritParticles(target);
                  }

                  if (var2 >= 18.0F) {
                     this.incrementStat(Achievements.DEAL_OVERKILL_DAMAGE);
                  }

                  this.setAttackTarget(target);
                  if (target instanceof LivingEntity) {
                     EnchantmentHelper.applyProtectionWildcard((LivingEntity)target, this);
                  }

                  EnchantmentHelper.applyDamageWildcard(this, target);
                  ItemStack var15 = this.getMainHandStack();
                  Object var16 = target;
                  if (target instanceof EnderDragonPart) {
                     EnderDragon var17 = ((EnderDragonPart)target).dragon;
                     if (var17 instanceof LivingEntity) {
                        var16 = (LivingEntity)var17;
                     }
                  }

                  if (var15 != null && var16 instanceof LivingEntity) {
                     var15.attackEntity((LivingEntity)var16, this);
                     if (var15.size <= 0) {
                        this.clearSelectedSlot();
                     }
                  }

                  if (target instanceof LivingEntity) {
                     this.incrementStat(Stats.DAMAGE_DEALT, Math.round(var2 * 10.0F));
                     if (var7 > 0) {
                        target.setOnFireFor(var7 * 4);
                     }
                  }

                  this.addFatigue(0.3F);
               } else if (var6) {
                  target.extinguish();
               }
            }
         }
      }
   }

   public void addCritParticles(Entity entity) {
   }

   public void addEnchantedCritParticles(Entity entity) {
   }

   @Environment(EnvType.CLIENT)
   public void tryRespawn() {
   }

   @Override
   public void remove() {
      super.remove();
      this.playerMenu.close(this);
      if (this.menu != null) {
         this.menu.close(this);
      }
   }

   @Override
   public boolean isInWall() {
      return !this.sleeping && super.isInWall();
   }

   @Environment(EnvType.CLIENT)
   public boolean m_08txklcju() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.profiler;
   }

   public PlayerEntity.SleepAllowedStatus trySleep(BlockPos x) {
      if (!this.world.isClient) {
         if (this.isSleeping() || !this.isAlive()) {
            return PlayerEntity.SleepAllowedStatus.OTHER_PROBLEM;
         }

         if (!this.world.dimension.isOverworld()) {
            return PlayerEntity.SleepAllowedStatus.NOT_POSSIBLE_HERE;
         }

         if (this.world.isSunny()) {
            return PlayerEntity.SleepAllowedStatus.NOT_POSSIBLE_NOW;
         }

         if (Math.abs(this.x - (double)x.getX()) > 3.0 || Math.abs(this.y - (double)x.getY()) > 2.0 || Math.abs(this.z - (double)x.getZ()) > 3.0) {
            return PlayerEntity.SleepAllowedStatus.TOO_FAR_AWAY;
         }

         double var2 = 8.0;
         double var4 = 5.0;
         List var6 = this.world
            .getEntities(
               HostileEntity.class,
               new Box(
                  (double)x.getX() - var2,
                  (double)x.getY() - var4,
                  (double)x.getZ() - var2,
                  (double)x.getX() + var2,
                  (double)x.getY() + var4,
                  (double)x.getZ() + var2
               )
            );
         if (!var6.isEmpty()) {
            return PlayerEntity.SleepAllowedStatus.NOT_SAFE;
         }
      }

      if (this.hasVehicle()) {
         this.startRiding(null);
      }

      this.setDimensions(0.2F, 0.2F);
      if (this.world.isLoaded(x)) {
         Direction var7 = (Direction)this.world.getBlockState(x).get(HorizontalFacingBlock.FACING);
         float var3 = 0.5F;
         float var8 = 0.5F;
         switch(var7) {
            case SOUTH:
               var8 = 0.9F;
               break;
            case NORTH:
               var8 = 0.1F;
               break;
            case WEST:
               var3 = 0.1F;
               break;
            case EAST:
               var3 = 0.9F;
         }

         this.onEnterBed(var7);
         this.setPosition((double)((float)x.getX() + var3), (double)((float)x.getY() + 0.6875F), (double)((float)x.getZ() + var8));
      } else {
         this.setPosition((double)((float)x.getX() + 0.5F), (double)((float)x.getY() + 0.6875F), (double)((float)x.getZ() + 0.5F));
      }

      this.sleeping = true;
      this.sleepTimer = 0;
      this.sleepingPos = x;
      this.velocityX = this.velocityZ = this.velocityY = 0.0;
      if (!this.world.isClient) {
         this.world.updateSleepingPlayers();
      }

      return PlayerEntity.SleepAllowedStatus.OK;
   }

   private void onEnterBed(Direction facing) {
      this.sleepOffsetX = 0.0F;
      this.sleepOffsetZ = 0.0F;
      switch(facing) {
         case SOUTH:
            this.sleepOffsetZ = -1.8F;
            break;
         case NORTH:
            this.sleepOffsetZ = 1.8F;
            break;
         case WEST:
            this.sleepOffsetX = 1.8F;
            break;
         case EAST:
            this.sleepOffsetX = -1.8F;
      }
   }

   public void wakeUp(boolean resetSleepTimer, boolean update, boolean setSpawnPoint) {
      this.setDimensions(0.6F, 1.8F);
      BlockState var4 = this.world.getBlockState(this.sleepingPos);
      if (this.sleepingPos != null && var4.getBlock() == Blocks.BED) {
         this.world.setBlockState(this.sleepingPos, var4.set(BedBlock.OCCUPIED, false), 4);
         BlockPos var5 = BedBlock.getSpawnPos(this.world, this.sleepingPos, 0);
         if (var5 == null) {
            var5 = this.sleepingPos.up();
         }

         this.setPosition((double)((float)var5.getX() + 0.5F), (double)((float)var5.getY() + 0.1F), (double)((float)var5.getZ() + 0.5F));
      }

      this.sleeping = false;
      if (!this.world.isClient && update) {
         this.world.updateSleepingPlayers();
      }

      this.sleepTimer = resetSleepTimer ? 0 : 100;
      if (setSpawnPoint) {
         this.setSpawnpoint(this.sleepingPos, false);
      }
   }

   private boolean isValidSleepingPos() {
      return this.world.getBlockState(this.sleepingPos).getBlock() == Blocks.BED;
   }

   public static BlockPos getUpdatedSpawnpoint(World world, BlockPos oldSpawnpoint, boolean hasSpawnpointSet) {
      if (world.getBlockState(oldSpawnpoint).getBlock() != Blocks.BED) {
         if (!hasSpawnpointSet) {
            return null;
         } else {
            Material var3 = world.getBlockState(oldSpawnpoint).getBlock().getMaterial();
            Material var4 = world.getBlockState(oldSpawnpoint.up()).getBlock().getMaterial();
            boolean var5 = !var3.isSolid() && !var3.isLiquid();
            boolean var6 = !var4.isSolid() && !var4.isLiquid();
            return var5 && var6 ? oldSpawnpoint : null;
         }
      } else {
         return BedBlock.getSpawnPos(world, oldSpawnpoint, 0);
      }
   }

   @Environment(EnvType.CLIENT)
   public float getRespawnDirection() {
      if (this.sleepingPos != null) {
         Direction var1 = (Direction)this.world.getBlockState(this.sleepingPos).get(HorizontalFacingBlock.FACING);
         switch(var1) {
            case SOUTH:
               return 90.0F;
            case NORTH:
               return 270.0F;
            case WEST:
               return 0.0F;
            case EAST:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   @Override
   public boolean isSleeping() {
      return this.sleeping;
   }

   public boolean isSleptEnough() {
      return this.sleeping && this.sleepTimer >= 100;
   }

   @Environment(EnvType.CLIENT)
   public int getSleepTimer() {
      return this.sleepTimer;
   }

   public void addMessage(Text message) {
   }

   public BlockPos getSpawnPoint() {
      return this.spawnPoint;
   }

   public boolean isRespawnForced() {
      return this.respawnForced;
   }

   public void setSpawnpoint(BlockPos spawnPoint, boolean respawnForced) {
      if (spawnPoint != null) {
         this.spawnPoint = spawnPoint;
         this.respawnForced = respawnForced;
      } else {
         this.spawnPoint = null;
         this.respawnForced = false;
      }
   }

   public void incrementStat(Stat stat) {
      this.incrementStat(stat, 1);
   }

   public void incrementStat(Stat stat, int amount) {
   }

   public void clearStat(Stat stat) {
   }

   @Override
   public void jump() {
      super.jump();
      this.incrementStat(Stats.JUMPS);
      if (this.isSprinting()) {
         this.addFatigue(0.8F);
      } else {
         this.addFatigue(0.2F);
      }
   }

   @Override
   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      double var3 = this.x;
      double var5 = this.y;
      double var7 = this.z;
      if (this.abilities.flying && this.vehicle == null) {
         double var9 = this.velocityY;
         float var11 = this.airSpeed;
         this.airSpeed = this.abilities.getFlySpeed();
         super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
         this.velocityY = var9 * 0.6;
         this.airSpeed = var11;
      } else {
         super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
      }

      this.tickNonRidingMovmentRelatedStats(this.x - var3, this.y - var5, this.z - var7);
   }

   @Override
   public float getMovementSpeed() {
      return (float)this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get();
   }

   public void tickNonRidingMovmentRelatedStats(double x, double y, double z) {
      if (this.vehicle == null) {
         if (this.isSubmergedIn(Material.WATER)) {
            int var7 = Math.round(MathHelper.sqrt(x * x + y * y + z * z) * 100.0F);
            if (var7 > 0) {
               this.incrementStat(Stats.CM_DIVEN, var7);
               this.addFatigue(0.015F * (float)var7 * 0.01F);
            }
         } else if (this.isInWater()) {
            int var8 = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
            if (var8 > 0) {
               this.incrementStat(Stats.CM_SWUM, var8);
               this.addFatigue(0.015F * (float)var8 * 0.01F);
            }
         } else if (this.isClimbing()) {
            if (y > 0.0) {
               this.incrementStat(Stats.CM_CLIMB, (int)Math.round(y * 100.0));
            }
         } else if (this.onGround) {
            int var9 = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
            if (var9 > 0) {
               this.incrementStat(Stats.CM_WALKED, var9);
               if (this.isSprinting()) {
                  this.incrementStat(Stats.SPRINT_ONE_CM, var9);
                  this.addFatigue(0.099999994F * (float)var9 * 0.01F);
               } else {
                  if (this.isSneaking()) {
                     this.incrementStat(Stats.CROUCH_ONE_CM, var9);
                  }

                  this.addFatigue(0.01F * (float)var9 * 0.01F);
               }
            }
         } else {
            int var10 = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
            if (var10 > 25) {
               this.incrementStat(Stats.CM_FLOWN, var10);
            }
         }
      }
   }

   private void tickRidingRelatedStats(double x, double y, double z) {
      if (this.vehicle != null) {
         int var7 = Math.round(MathHelper.sqrt(x * x + y * y + z * z) * 100.0F);
         if (var7 > 0) {
            if (this.vehicle instanceof MinecartEntity) {
               this.incrementStat(Stats.CM_MINECART, var7);
               if (this.minecartTravelStartPos == null) {
                  this.minecartTravelStartPos = new BlockPos(this);
               } else if (this.minecartTravelStartPos
                     .squaredDistanceTo((double)MathHelper.floor(this.x), (double)MathHelper.floor(this.y), (double)MathHelper.floor(this.z))
                  >= 1000000.0) {
                  this.incrementStat(Achievements.TRAVEL_KILOMETER_BY_MINECART);
               }
            } else if (this.vehicle instanceof BoatEntity) {
               this.incrementStat(Stats.CM_SAILED, var7);
            } else if (this.vehicle instanceof PigEntity) {
               this.incrementStat(Stats.CM_PIG, var7);
            } else if (this.vehicle instanceof HorseBaseEntity) {
               this.incrementStat(Stats.CM_HORSE, var7);
            }
         }
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      if (!this.abilities.canFly) {
         if (distance >= 2.0F) {
            this.incrementStat(Stats.CM_FALLEN, (int)Math.round((double)distance * 100.0));
         }

         super.applyFallDamage(distance, g);
      }
   }

   @Override
   protected String getFallSound(int distance) {
      return distance > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
   }

   @Override
   public void onKill(LivingEntity victim) {
      if (victim instanceof Monster) {
         this.incrementStat(Achievements.KILL_ENEMY);
      }

      int var2 = Entities.getRawId(victim);
      Entities.SpawnEggData var3 = (Entities.SpawnEggData)Entities.RAW_ID_TO_SPAWN_EGG_DATA.get(var2);
      if (var3 != null) {
         this.incrementStat(var3.killEntityStat);
      }
   }

   @Override
   public void onCobwebCollision() {
      if (!this.abilities.flying) {
         super.onCobwebCollision();
      }
   }

   @Override
   public ItemStack getArmorStack(int armorSlot) {
      return this.inventory.getArmor(armorSlot);
   }

   public void increaseXp(int levels) {
      this.addToScore(levels);
      int var2 = Integer.MAX_VALUE - this.xp;
      if (levels > var2) {
         levels = var2;
      }

      this.xpProgress += (float)levels / (float)this.getXpAmount();

      for(this.xp += levels; this.xpProgress >= 1.0F; this.xpProgress /= (float)this.getXpAmount()) {
         this.xpProgress = (this.xpProgress - 1.0F) * (float)this.getXpAmount();
         this.addXp(1);
      }
   }

   public int getEnchantingSeed() {
      return this.f_87bysmrvy;
   }

   public void m_92coykull(int i) {
      this.xpLevel -= i;
      if (this.xpLevel < 0) {
         this.xpLevel = 0;
         this.xpProgress = 0.0F;
         this.xp = 0;
      }

      this.f_87bysmrvy = this.random.nextInt();
   }

   public void addXp(int levels) {
      this.xpLevel += levels;
      if (this.xpLevel < 0) {
         this.xpLevel = 0;
         this.xpProgress = 0.0F;
         this.xp = 0;
      }

      if (levels > 0 && this.xpLevel % 5 == 0 && (float)this.lastXpSoundTime < (float)this.time - 100.0F) {
         float var2 = this.xpLevel > 30 ? 1.0F : (float)this.xpLevel / 30.0F;
         this.world.playSound((Entity)this, "random.levelup", var2 * 0.75F, 1.0F);
         this.lastXpSoundTime = this.time;
      }
   }

   public int getXpAmount() {
      if (this.xpLevel >= 30) {
         return 112 + (this.xpLevel - 30) * 9;
      } else {
         return this.xpLevel >= 15 ? 37 + (this.xpLevel - 15) * 5 : 7 + this.xpLevel * 2;
      }
   }

   public void addFatigue(float amount) {
      if (!this.abilities.invulnerable) {
         if (!this.world.isClient) {
            this.hungerManager.addExhaustion(amount);
         }
      }
   }

   public HungerManager getHungerManager() {
      return this.hungerManager;
   }

   public boolean canEat(boolean ignoreHunger) {
      return (ignoreHunger || this.hungerManager.needsFood()) && !this.abilities.invulnerable;
   }

   public boolean needsHealing() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public void setUseItem(ItemStack stack, int maxUseTime) {
      if (stack != this.heldItem) {
         this.heldItem = stack;
         this.itemUseTimer = maxUseTime;
         if (!this.world.isClient) {
            this.setSwimming(true);
         }
      }
   }

   public boolean canModifyWorld() {
      return this.abilities.canModifyWorld;
   }

   public boolean canUseItem(BlockPos pos, Direction face, ItemStack stack) {
      if (this.abilities.canModifyWorld) {
         return true;
      } else if (stack == null) {
         return false;
      } else {
         BlockPos var4 = pos.offset(face.getOpposite());
         Block var5 = this.world.getBlockState(var4).getBlock();
         return stack.hasPlaceOnBlockOverride(var5) || stack.canAlwaysUse();
      }
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      if (this.world.getGameRules().getBoolean("keepInventory")) {
         return 0;
      } else {
         int var2 = this.xpLevel * 7;
         return var2 > 100 ? 100 : var2;
      }
   }

   @Override
   protected boolean shouldDropXp() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldShowNameTag() {
      return true;
   }

   public void copyFrom(PlayerEntity player, boolean comesFromTheEnd) {
      if (comesFromTheEnd) {
         this.inventory.copy(player.inventory);
         this.setHealth(player.getHealth());
         this.hungerManager = player.hungerManager;
         this.xpLevel = player.xpLevel;
         this.xp = player.xp;
         this.xpProgress = player.xpProgress;
         this.setScore(player.getScore());
         this.facing = player.facing;
      } else if (this.world.getGameRules().getBoolean("keepInventory")) {
         this.inventory.copy(player.inventory);
         this.xpLevel = player.xpLevel;
         this.xp = player.xp;
         this.xpProgress = player.xpProgress;
         this.setScore(player.getScore());
      }

      this.enderchest = player.enderchest;
      this.getDataTracker().update(10, player.getDataTracker().getByte(10));
   }

   @Override
   protected boolean canClimb() {
      return !this.abilities.flying;
   }

   public void syncAbilities() {
   }

   public void setGameMode(WorldSettings.GameMode gameMode) {
   }

   @Override
   public String getName() {
      return this.profiler.getName();
   }

   public EnderChestInventory getEnderChestInventory() {
      return this.enderchest;
   }

   @Override
   public ItemStack getStackInInventory(int id) {
      return id == 0 ? this.inventory.getMainHandStack() : this.inventory.armorSlots[id - 1];
   }

   @Override
   public ItemStack getStackInHand() {
      return this.inventory.getMainHandStack();
   }

   @Override
   public void setEquipmentStack(int slot, ItemStack stack) {
      this.inventory.armorSlots[slot] = stack;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isInvisibleTo(PlayerEntity player) {
      if (!this.isInvisible()) {
         return false;
      } else if (player.isSpectator()) {
         return false;
      } else {
         AbstractTeam var2 = this.getScoreboardTeam();
         return var2 == null || player == null || player.getScoreboardTeam() != var2 || !var2.showFriendlyInvisibles();
      }
   }

   public abstract boolean isSpectator();

   @Override
   public ItemStack[] getEquipmentStacks() {
      return this.inventory.armorSlots;
   }

   @Override
   public boolean hasLiquidCollision() {
      return !this.abilities.flying;
   }

   public Scoreboard getScoreboard() {
      return this.world.getScoreboard();
   }

   @Override
   public AbstractTeam getScoreboardTeam() {
      return this.getScoreboard().getTeamOfMember(this.getName());
   }

   @Override
   public Text getDisplayName() {
      LiteralText var1 = new LiteralText(Team.getMemberDisplayName(this.getScoreboardTeam(), this.getName()));
      var1.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
      var1.getStyle().setHoverEvent(this.getHoverEvent());
      var1.getStyle().setInsertion(this.getName());
      return var1;
   }

   @Override
   public float getEyeHeight() {
      float var1 = 1.62F;
      if (this.isSleeping()) {
         var1 = 0.2F;
      }

      if (this.isSneaking()) {
         var1 -= 0.08F;
      }

      return var1;
   }

   @Override
   public void setAbsorption(float absorption) {
      if (absorption < 0.0F) {
         absorption = 0.0F;
      }

      this.getDataTracker().update(17, absorption);
   }

   @Override
   public float getAbsorption() {
      return this.getDataTracker().getFloat(17);
   }

   public static UUID getUuid(GameProfile profile) {
      UUID var1 = profile.getId();
      if (var1 == null) {
         var1 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + profile.getName()).getBytes(Charsets.UTF_8));
      }

      return var1;
   }

   public boolean m_22uhjmmik(InventoryLock c_75axqlpkg) {
      if (c_75axqlpkg.isEmpty()) {
         return true;
      } else {
         ItemStack var2 = this.getMainHandStack();
         return var2 != null && var2.hasCustomHoverName() ? var2.getHoverName().equals(c_75axqlpkg.getKey()) : false;
      }
   }

   @Environment(EnvType.CLIENT)
   public boolean hidesCape(PlayerModelPart value) {
      return (this.getDataTracker().getByte(10) & value.getFlag()) == value.getFlag();
   }

   @Override
   public boolean sendCommandFeedback() {
      return MinecraftServer.getInstance().worlds[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public boolean m_81zmldzmm(int i, ItemStack c_72owraavl) {
      if (i >= 0 && i < this.inventory.inventorySlots.length) {
         this.inventory.setStack(i, c_72owraavl);
         return true;
      } else {
         int var3 = i - 100;
         if (var3 >= 0 && var3 < this.inventory.armorSlots.length) {
            int var5 = var3 + 1;
            if (c_72owraavl != null && c_72owraavl.getItem() != null) {
               if (c_72owraavl.getItem() instanceof ArmorItem) {
                  if (MobEntity.getSlotForEquipment(c_72owraavl) != var5) {
                     return false;
                  }
               } else if (var5 != 4 || c_72owraavl.getItem() != Items.SKULL && !(c_72owraavl.getItem() instanceof BlockItem)) {
                  return false;
               }
            }

            this.inventory.setStack(var3 + this.inventory.inventorySlots.length, c_72owraavl);
            return true;
         } else {
            int var4 = i - 200;
            if (var4 >= 0 && var4 < this.enderchest.getSize()) {
               this.enderchest.setStack(var4, c_72owraavl);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public boolean hasReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   @Environment(EnvType.CLIENT)
   public void setReducedDebugInfo(boolean reducedDebugInfo) {
      this.reducedDebugInfo = reducedDebugInfo;
   }

   public static enum ChatVisibility {
      FULL(0, "options.chat.visibility.full"),
      SYSTEM(1, "options.chat.visibility.system"),
      HIDDEN(2, "options.chat.visibility.hidden");

      private static final PlayerEntity.ChatVisibility[] ALL = new PlayerEntity.ChatVisibility[values().length];
      private final int index;
      private final String id;

      private ChatVisibility(int index, String id) {
         this.index = index;
         this.id = id;
      }

      public int getIndex() {
         return this.index;
      }

      public static PlayerEntity.ChatVisibility byIndex(int index) {
         return ALL[index % ALL.length];
      }

      @Environment(EnvType.CLIENT)
      public String getId() {
         return this.id;
      }

      static {
         for(PlayerEntity.ChatVisibility var3 : values()) {
            ALL[var3.index] = var3;
         }
      }
   }

   public static enum SleepAllowedStatus {
      OK,
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW,
      TOO_FAR_AWAY,
      OTHER_PROBLEM,
      NOT_SAFE;
   }
}
