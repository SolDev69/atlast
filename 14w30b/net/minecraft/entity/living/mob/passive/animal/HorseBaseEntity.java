package net.minecraft.entity.living.mob.passive.animal;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.ClampedEntityAttribute;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttribute;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.inventory.AnimalInventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.dedicated.UserConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class HorseBaseEntity extends AnimalEntity implements InventoryListener {
   private static final Predicate BREED_FILTER = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof HorseBaseEntity && ((HorseBaseEntity)c_47ldwddrb).isReadyToBreed();
      }
   };
   private static final IEntityAttribute JUMP_STRENGTH_ATTRIBUTE = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
      .setDisplayName("Jump Strength")
      .setTrackable(true);
   private static final String[] HORSE_ARMOR_TEXTURES = new String[]{
      null,
      "textures/entity/horse/armor/horse_armor_iron.png",
      "textures/entity/horse/armor/horse_armor_gold.png",
      "textures/entity/horse/armor/horse_armor_diamond.png"
   };
   private static final String[] HORSE_ARMOR_LABELS = new String[]{"", "meo", "goo", "dio"};
   private static final int[] HORSE_ARMOR_VALUES = new int[]{0, 5, 7, 11};
   private static final String[] HORSE_TEXTURES = new String[]{
      "textures/entity/horse/horse_white.png",
      "textures/entity/horse/horse_creamy.png",
      "textures/entity/horse/horse_chestnut.png",
      "textures/entity/horse/horse_brown.png",
      "textures/entity/horse/horse_black.png",
      "textures/entity/horse/horse_gray.png",
      "textures/entity/horse/horse_darkbrown.png"
   };
   private static final String[] HORSE_TEXTURE_LABELS = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] HORSE_MARKINGS_TEXTURES = new String[]{
      null,
      "textures/entity/horse/horse_markings_white.png",
      "textures/entity/horse/horse_markings_whitefield.png",
      "textures/entity/horse/horse_markings_whitedots.png",
      "textures/entity/horse/horse_markings_blackdots.png"
   };
   private static final String[] HORSE_MARKING_LABELS = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private int eatingGrassTicks;
   private int eatingTicks;
   private int angryTicks;
   public int type;
   public int cooldown;
   protected boolean inAir;
   private AnimalInventory inventory;
   private boolean hasBred;
   protected int temper;
   protected float jumpStrength;
   private boolean jumping;
   private float eatingGrassAnimationProgress;
   private float lastEatingGrassAnimationProgress;
   private float angryAnimationProgress;
   private float lastAngryAnimationProgress;
   private float eatingAnimationProgress;
   private float lastEatingAnimationProgress;
   private int soundTicks;
   private String horseName;
   private String[] horseData = new String[3];
   private boolean f_51yorigdb = false;

   public HorseBaseEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(1.4F, 1.6F);
      this.immuneToFire = false;
      this.setHasChestFlag(false);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EscapeDangerGoal(this, 1.2));
      this.goalSelector.addGoal(1, new HorseBondWithPlayerGoal(this, 1.2));
      this.goalSelector.addGoal(2, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(6, new WanderAroundGoal(this, 0.7));
      this.goalSelector.addGoal(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.updateInventory();
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, 0);
      this.dataTracker.put(19, (byte)0);
      this.dataTracker.put(20, 0);
      this.dataTracker.put(21, String.valueOf(""));
      this.dataTracker.put(22, 0);
   }

   public void setType(int type) {
      this.dataTracker.update(19, (byte)type);
      this.deleteName();
   }

   public int getType() {
      return this.dataTracker.getByte(19);
   }

   public void setVariant(int variant) {
      this.dataTracker.update(20, variant);
      this.deleteName();
   }

   public int getVariant() {
      return this.dataTracker.getInt(20);
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomName();
      } else {
         int var1 = this.getType();
         switch(var1) {
            case 0:
            default:
               return I18n.translate("entity.horse.name");
            case 1:
               return I18n.translate("entity.donkey.name");
            case 2:
               return I18n.translate("entity.mule.name");
            case 3:
               return I18n.translate("entity.zombiehorse.name");
            case 4:
               return I18n.translate("entity.skeletonhorse.name");
         }
      }
   }

   private boolean getDatatracker(int value) {
      return (this.dataTracker.getInt(16) & value) != 0;
   }

   private void setHorseFlag(int bitmask, boolean flag) {
      int var3 = this.dataTracker.getInt(16);
      if (flag) {
         this.dataTracker.update(16, var3 | bitmask);
      } else {
         this.dataTracker.update(16, var3 & ~bitmask);
      }
   }

   public boolean isNotBaby() {
      return !this.isBaby();
   }

   public boolean isTame() {
      return this.getDatatracker(2);
   }

   public boolean isAdult() {
      return this.isNotBaby();
   }

   public String getOwnerUuid() {
      return this.dataTracker.getString(21);
   }

   public void setOwnerName(String name) {
      this.dataTracker.update(21, name);
   }

   public float getSize() {
      int var1 = this.getBreedingAge();
      return var1 >= 0 ? 1.0F : 0.5F + (float)(-24000 - var1) / -24000.0F * 0.5F;
   }

   @Override
   public void setAgeSize(boolean isBaby) {
      if (isBaby) {
         this.resizeBounds(this.getSize());
      } else {
         this.resizeBounds(1.0F);
      }
   }

   public boolean isInAir() {
      return this.inAir;
   }

   public void setTame(boolean tame) {
      this.setHorseFlag(2, tame);
   }

   public void setInAir(boolean inAir) {
      this.inAir = inAir;
   }

   @Override
   public boolean isTameable() {
      return !this.isAngryHorse() && super.isTameable();
   }

   @Override
   protected void updateForLeashLength(float leashLength) {
      if (leashLength > 6.0F && this.isEating()) {
         this.setEatingGrass(false);
      }
   }

   public boolean hasChest() {
      return this.getDatatracker(8);
   }

   public int getArmor() {
      return this.dataTracker.getInt(22);
   }

   private int getArmorIndex(ItemStack item) {
      if (item == null) {
         return 0;
      } else {
         Item var2 = item.getItem();
         if (var2 == Items.IRON_HORSE_ARMOR) {
            return 1;
         } else if (var2 == Items.GOLDEN_HORSE_ARMOR) {
            return 2;
         } else {
            return var2 == Items.DIAMOND_HORSE_ARMOR ? 3 : 0;
         }
      }
   }

   public boolean isEating() {
      return this.getDatatracker(32);
   }

   public boolean isAngry() {
      return this.getDatatracker(64);
   }

   public boolean isReadyToBreed() {
      return this.getDatatracker(16);
   }

   public boolean hasBred() {
      return this.hasBred;
   }

   public void setArmourIndex(ItemStack item) {
      this.dataTracker.update(22, this.getArmorIndex(item));
      this.deleteName();
   }

   public void setBredFlag(boolean bred) {
      this.setHorseFlag(16, bred);
   }

   public void setHasChestFlag(boolean hasChest) {
      this.setHorseFlag(8, hasChest);
   }

   public void setHasBred(boolean hasBred) {
      this.hasBred = hasBred;
   }

   public void setSaddled(boolean saddled) {
      this.setHorseFlag(4, saddled);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int temper) {
      this.temper = temper;
   }

   public int addTemper(int difference) {
      int var2 = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
      this.setTemper(var2);
      return var2;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      Entity var3 = source.getAttacker();
      return this.rider != null && this.rider.equals(var3) ? false : super.damage(source, amount);
   }

   @Override
   public int getArmorProtection() {
      return HORSE_ARMOR_VALUES[this.getArmor()];
   }

   @Override
   public boolean isPushable() {
      return this.rider == null;
   }

   public boolean getSpawnBiome() {
      int var1 = MathHelper.floor(this.x);
      int var2 = MathHelper.floor(this.z);
      this.world.getBiome(new BlockPos(var1, 0, var2));
      return true;
   }

   public void dropChest() {
      if (!this.world.isClient && this.hasChest()) {
         this.dropItem(Item.byBlock(Blocks.CHEST), 1);
         this.setHasChestFlag(false);
      }
   }

   private void playEatingAnimation() {
      this.setEating();
      if (!this.isSilent()) {
         this.world.playSound(this, "eating", 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      if (distance > 1.0F) {
         this.playSound("mob.horse.land", 0.4F, 1.0F);
      }

      int var3 = MathHelper.ceil((distance * 0.5F - 3.0F) * g);
      if (var3 > 0) {
         this.damage(DamageSource.FALL, (float)var3);
         if (this.rider != null) {
            this.rider.damage(DamageSource.FALL, (float)var3);
         }

         Block var4 = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z)).getBlock();
         if (var4.getMaterial() != Material.AIR && !this.isSilent()) {
            Block.Sound var5 = var4.sound;
            this.world.playSound(this, var5.getStepSound(), var5.getVolume() * 0.5F, var5.getPitch() * 0.75F);
         }
      }
   }

   private int getInventorySize() {
      int var1 = this.getType();
      return !this.hasChest() || var1 != 1 && var1 != 2 ? 2 : 17;
   }

   private void updateInventory() {
      AnimalInventory var1 = this.inventory;
      this.inventory = new AnimalInventory("HorseChest", this.getInventorySize());
      this.inventory.setCustomName(this.getName());
      if (var1 != null) {
         var1.removeListener(this);
         int var2 = Math.min(var1.getSize(), this.inventory.getSize());

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1.getStack(var3);
            if (var4 != null) {
               this.inventory.setStack(var3, var4.copy());
            }
         }
      }

      this.inventory.addListener(this);
      this.updateSaddle();
   }

   private void updateSaddle() {
      if (!this.world.isClient) {
         this.setSaddled(this.inventory.getStack(0) != null);
         if (this.drawHoverEffect()) {
            this.setArmourIndex(this.inventory.getStack(1));
         }
      }
   }

   @Override
   public void onInventoryChanged(SimpleInventory inventory) {
      int var2 = this.getArmor();
      boolean var3 = this.isSaddled();
      this.updateSaddle();
      if (this.time > 20) {
         if (var2 == 0 && var2 != this.getArmor()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         } else if (var2 != this.getArmor()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!var3 && this.isSaddled()) {
            this.playSound("mob.horse.leather", 0.5F, 1.0F);
         }
      }
   }

   @Override
   public boolean canSpawn() {
      this.getSpawnBiome();
      return super.canSpawn();
   }

   protected HorseBaseEntity findClosestEntity(Entity entity, double stretch) {
      double var4 = Double.MAX_VALUE;
      Entity var6 = null;

      for(Entity var9 : this.world.getEntities(entity, entity.getBoundingBox().grow(stretch, stretch, stretch), BREED_FILTER)) {
         double var10 = var9.getSquaredDistanceTo(entity.x, entity.y, entity.z);
         if (var10 < var4) {
            var6 = var9;
            var4 = var10;
         }
      }

      return (HorseBaseEntity)var6;
   }

   public double getJumpStrength() {
      return this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).get();
   }

   @Override
   protected String getDeathSound() {
      this.setEating();
      int var1 = this.getType();
      if (var1 == 3) {
         return "mob.horse.zombie.death";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.death";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.death" : "mob.horse.donkey.death";
      }
   }

   @Override
   protected Item getDefaultDropLoot() {
      boolean var1 = this.random.nextInt(4) == 0;
      int var2 = this.getType();
      if (var2 == 4) {
         return Items.BONE;
      } else if (var2 == 3) {
         return var1 ? Item.byRawId(0) : Items.ROTTEN_FLESH;
      } else {
         return Items.LEATHER;
      }
   }

   @Override
   protected String getHurtSound() {
      this.setEating();
      if (this.random.nextInt(3) == 0) {
         this.updateAnger();
      }

      int var1 = this.getType();
      if (var1 == 3) {
         return "mob.horse.zombie.hit";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.hit";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit";
      }
   }

   public boolean isSaddled() {
      return this.getDatatracker(4);
   }

   @Override
   protected String getAmbientSound() {
      this.setEating();
      if (this.random.nextInt(10) == 0 && !this.isDead()) {
         this.updateAnger();
      }

      int var1 = this.getType();
      if (var1 == 3) {
         return "mob.horse.zombie.idle";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.idle";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle";
      }
   }

   protected String getAngreType() {
      this.setEating();
      this.updateAnger();
      int var1 = this.getType();
      if (var1 == 3 || var1 == 4) {
         return null;
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry";
      }
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      Block.Sound var3 = y.sound;
      if (this.world.getBlockState(x.up()).getBlock() == Blocks.SNOW_LAYER) {
         var3 = Blocks.SNOW_LAYER.sound;
      }

      if (!y.getMaterial().isLiquid()) {
         int var4 = this.getType();
         if (this.rider != null && var4 != 1 && var4 != 2) {
            ++this.soundTicks;
            if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
               this.playSound("mob.horse.gallop", var3.getVolume() * 0.15F, var3.getPitch());
               if (var4 == 0 && this.random.nextInt(10) == 0) {
                  this.playSound("mob.horse.breathe", var3.getVolume() * 0.6F, var3.getPitch());
               }
            } else if (this.soundTicks <= 5) {
               this.playSound("mob.horse.wood", var3.getVolume() * 0.15F, var3.getPitch());
            }
         } else if (var3 == Block.WOOD_SOUND) {
            this.playSound("mob.horse.wood", var3.getVolume() * 0.15F, var3.getPitch());
         } else {
            this.playSound("mob.horse.soft", var3.getVolume() * 0.15F, var3.getPitch());
         }
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributes().registerAttribute(JUMP_STRENGTH_ATTRIBUTE);
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(53.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.225F);
   }

   @Override
   public int getLimitPerChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   @Override
   protected float getSoundVolume() {
      return 0.8F;
   }

   @Override
   public int getMinAmbientSoundDelay() {
      return 400;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public boolean hasArmor() {
      return this.getType() == 0 || this.getArmor() > 0;
   }

   private void deleteName() {
      this.horseName = null;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public boolean m_42hzgygyd() {
      return this.f_51yorigdb;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   private void setName() {
      this.horseName = "horse/";
      this.horseData[0] = null;
      this.horseData[1] = null;
      this.horseData[2] = null;
      int var1 = this.getType();
      int var2 = this.getVariant();
      if (var1 == 0) {
         int var3 = var2 & 0xFF;
         int var4 = (var2 & 0xFF00) >> 8;
         if (var3 >= HORSE_TEXTURES.length) {
            this.f_51yorigdb = false;
            return;
         }

         this.horseData[0] = HORSE_TEXTURES[var3];
         this.horseName = this.horseName + HORSE_TEXTURE_LABELS[var3];
         if (var4 >= HORSE_MARKINGS_TEXTURES.length) {
            this.f_51yorigdb = false;
            return;
         }

         this.horseData[1] = HORSE_MARKINGS_TEXTURES[var4];
         this.horseName = this.horseName + HORSE_MARKING_LABELS[var4];
      } else {
         this.horseData[0] = "";
         this.horseName = this.horseName + "_" + var1 + "_";
      }

      int var5 = this.getArmor();
      if (var5 >= HORSE_ARMOR_TEXTURES.length) {
         this.f_51yorigdb = false;
      } else {
         this.horseData[2] = HORSE_ARMOR_TEXTURES[var5];
         this.horseName = this.horseName + HORSE_ARMOR_LABELS[var5];
         this.f_51yorigdb = true;
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public String getHorseName() {
      if (this.horseName == null) {
         this.setName();
      }

      return this.horseName;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public String[] getHorseData() {
      if (this.horseName == null) {
         this.setName();
      }

      return this.horseData;
   }

   public void openInventory(PlayerEntity player) {
      if (!this.world.isClient && (this.rider == null || this.rider == player) && this.isTame()) {
         this.inventory.setCustomName(this.getName());
         player.openHorseMenu(this, this.inventory);
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.SPAWN_EGG) {
         return super.canInteract(player);
      } else if (!this.isTame() && this.isAngryHorse()) {
         return false;
      } else if (this.isTame() && this.isNotBaby() && player.isSneaking()) {
         this.openInventory(player);
         return true;
      } else if (this.isAdult() && this.rider != null) {
         return super.canInteract(player);
      } else {
         if (var2 != null) {
            boolean var3 = false;
            if (this.drawHoverEffect()) {
               byte var4 = -1;
               if (var2.getItem() == Items.IRON_HORSE_ARMOR) {
                  var4 = 1;
               } else if (var2.getItem() == Items.GOLDEN_HORSE_ARMOR) {
                  var4 = 2;
               } else if (var2.getItem() == Items.DIAMOND_HORSE_ARMOR) {
                  var4 = 3;
               }

               if (var4 >= 0) {
                  if (!this.isTame()) {
                     this.playAngrySound();
                     return true;
                  }

                  this.openInventory(player);
                  return true;
               }
            }

            if (!var3 && !this.isAngryHorse()) {
               float var7 = 0.0F;
               short var5 = 0;
               byte var6 = 0;
               if (var2.getItem() == Items.WHEAT) {
                  var7 = 2.0F;
                  var5 = 20;
                  var6 = 3;
               } else if (var2.getItem() == Items.SUGAR) {
                  var7 = 1.0F;
                  var5 = 30;
                  var6 = 3;
               } else if (Block.byItem(var2.getItem()) == Blocks.HAY) {
                  var7 = 20.0F;
                  var5 = 180;
               } else if (var2.getItem() == Items.APPLE) {
                  var7 = 3.0F;
                  var5 = 60;
                  var6 = 3;
               } else if (var2.getItem() == Items.GOLDEN_CARROT) {
                  var7 = 4.0F;
                  var5 = 60;
                  var6 = 5;
                  if (this.isTame() && this.getBreedingAge() == 0) {
                     var3 = true;
                     this.lovePlayer(player);
                  }
               } else if (var2.getItem() == Items.GOLDEN_APPLE) {
                  var7 = 10.0F;
                  var5 = 240;
                  var6 = 10;
                  if (this.isTame() && this.getBreedingAge() == 0) {
                     var3 = true;
                     this.lovePlayer(player);
                  }
               }

               if (this.getHealth() < this.getMaxHealth() && var7 > 0.0F) {
                  this.heal(var7);
                  var3 = true;
               }

               if (!this.isNotBaby() && var5 > 0) {
                  this.growUp(var5);
                  var3 = true;
               }

               if (var6 > 0 && (var3 || !this.isTame()) && var6 < this.getMaxTemper()) {
                  var3 = true;
                  this.addTemper(var6);
               }

               if (var3) {
                  this.playEatingAnimation();
               }
            }

            if (!this.isTame() && !var3) {
               if (var2 != null && var2.canInteract(player, this)) {
                  return true;
               }

               this.playAngrySound();
               return true;
            }

            if (!var3 && this.isNotAngry() && !this.hasChest() && var2.getItem() == Item.byBlock(Blocks.CHEST)) {
               this.setHasChestFlag(true);
               this.playSound("mob.chickenplop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               var3 = true;
               this.updateInventory();
            }

            if (!var3 && this.isAdult() && !this.isSaddled() && var2.getItem() == Items.SADDLE) {
               this.openInventory(player);
               return true;
            }

            if (var3) {
               if (!player.abilities.creativeMode && --var2.size == 0) {
                  player.inventory.setStack(player.inventory.selectedSlot, null);
               }

               return true;
            }
         }

         if (!this.isAdult() || this.rider != null) {
            return super.canInteract(player);
         } else if (var2 != null && var2.canInteract(player, this)) {
            return true;
         } else {
            this.putPlayerOnBack(player);
            return true;
         }
      }
   }

   private void putPlayerOnBack(PlayerEntity player) {
      player.yaw = this.yaw;
      player.pitch = this.pitch;
      this.setEatingGrass(false);
      this.setAngry(false);
      if (!this.world.isClient) {
         player.startRiding(this);
      }
   }

   public boolean drawHoverEffect() {
      return this.getType() == 0;
   }

   public boolean isNotAngry() {
      int var1 = this.getType();
      return var1 == 2 || var1 == 1;
   }

   @Override
   protected boolean isDead() {
      if (this.rider != null && this.isSaddled()) {
         return true;
      } else {
         return this.isEating() || this.isAngry();
      }
   }

   public boolean isAngryHorse() {
      int var1 = this.getType();
      return var1 == 3 || var1 == 4;
   }

   public boolean noLove() {
      return this.isAngryHorse() || this.getType() == 2;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   private void setType() {
      this.type = 1;
   }

   @Override
   public void onKilled(DamageSource source) {
      super.onKilled(source);
      if (!this.world.isClient) {
         this.dropInventoryAndChest();
      }
   }

   @Override
   public void tickAI() {
      if (this.random.nextInt(200) == 0) {
         this.setType();
      }

      super.tickAI();
      if (!this.world.isClient) {
         if (this.random.nextInt(900) == 0 && this.deathTicks == 0) {
            this.heal(1.0F);
         }

         if (!this.isEating()
            && this.rider == null
            && this.random.nextInt(300) == 0
            && this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y) - 1, MathHelper.floor(this.z))).getBlock()
               == Blocks.GRASS) {
            this.setEatingGrass(true);
         }

         if (this.isEating() && ++this.eatingGrassTicks > 50) {
            this.eatingGrassTicks = 0;
            this.setEatingGrass(false);
         }

         if (this.isReadyToBreed() && !this.isNotBaby() && !this.isEating()) {
            HorseBaseEntity var1 = this.findClosestEntity(this, 16.0);
            if (var1 != null && this.getSquaredDistanceTo(var1) > 4.0) {
               this.entityNavigation.getNavigation(var1);
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.world.isClient && this.dataTracker.isDirty()) {
         this.dataTracker.markClean();
         this.deleteName();
      }

      if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
         this.eatingTicks = 0;
         this.setHorseFlag(128, false);
      }

      if (!this.world.isClient && this.angryTicks > 0 && ++this.angryTicks > 20) {
         this.angryTicks = 0;
         this.setAngry(false);
      }

      if (this.type > 0 && ++this.type > 8) {
         this.type = 0;
      }

      if (this.cooldown > 0) {
         ++this.cooldown;
         if (this.cooldown > 300) {
            this.cooldown = 0;
         }
      }

      this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
      if (this.isEating()) {
         this.eatingGrassAnimationProgress += (1.0F - this.eatingGrassAnimationProgress) * 0.4F + 0.05F;
         if (this.eatingGrassAnimationProgress > 1.0F) {
            this.eatingGrassAnimationProgress = 1.0F;
         }
      } else {
         this.eatingGrassAnimationProgress += (0.0F - this.eatingGrassAnimationProgress) * 0.4F - 0.05F;
         if (this.eatingGrassAnimationProgress < 0.0F) {
            this.eatingGrassAnimationProgress = 0.0F;
         }
      }

      this.lastAngryAnimationProgress = this.angryAnimationProgress;
      if (this.isAngry()) {
         this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress = 0.0F;
         this.angryAnimationProgress += (1.0F - this.angryAnimationProgress) * 0.4F + 0.05F;
         if (this.angryAnimationProgress > 1.0F) {
            this.angryAnimationProgress = 1.0F;
         }
      } else {
         this.jumping = false;
         this.angryAnimationProgress += (
                  0.8F * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress
               )
               * 0.6F
            - 0.05F;
         if (this.angryAnimationProgress < 0.0F) {
            this.angryAnimationProgress = 0.0F;
         }
      }

      this.lastEatingAnimationProgress = this.eatingAnimationProgress;
      if (this.getDatatracker(128)) {
         this.eatingAnimationProgress += (1.0F - this.eatingAnimationProgress) * 0.7F + 0.05F;
         if (this.eatingAnimationProgress > 1.0F) {
            this.eatingAnimationProgress = 1.0F;
         }
      } else {
         this.eatingAnimationProgress += (0.0F - this.eatingAnimationProgress) * 0.7F - 0.05F;
         if (this.eatingAnimationProgress < 0.0F) {
            this.eatingAnimationProgress = 0.0F;
         }
      }
   }

   private void setEating() {
      if (!this.world.isClient) {
         this.eatingTicks = 1;
         this.setHorseFlag(128, true);
      }
   }

   private boolean canBreed() {
      return this.rider == null
         && this.vehicle == null
         && this.isTame()
         && this.isNotBaby()
         && !this.noLove()
         && this.getHealth() >= this.getMaxHealth()
         && this.isInLove();
   }

   @Override
   public void setSwimming(boolean swimming) {
      this.setHorseFlag(32, swimming);
   }

   public void setEatingGrass(boolean eatingGrass) {
      this.setSwimming(eatingGrass);
   }

   public void setAngry(boolean angry) {
      if (angry) {
         this.setEatingGrass(false);
      }

      this.setHorseFlag(64, angry);
   }

   private void updateAnger() {
      if (!this.world.isClient) {
         this.angryTicks = 1;
         this.setAngry(true);
      }
   }

   public void playAngrySound() {
      this.updateAnger();
      String var1 = this.getAngreType();
      if (var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   public void dropInventoryAndChest() {
      this.dropInventory(this, this.inventory);
      this.dropChest();
   }

   private void dropInventory(Entity entity, AnimalInventory inventory) {
      if (inventory != null && !this.world.isClient) {
         for(int var3 = 0; var3 < inventory.getSize(); ++var3) {
            ItemStack var4 = inventory.getStack(var3);
            if (var4 != null) {
               this.dropItem(var4, 0.0F);
            }
         }
      }
   }

   public boolean bondWithPlayer(PlayerEntity player) {
      this.setOwnerName(player.getUuid().toString());
      this.setTame(true);
      return true;
   }

   @Override
   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      if (this.rider != null && this.rider instanceof LivingEntity && this.isSaddled()) {
         this.prevYaw = this.yaw = this.rider.yaw;
         this.pitch = this.rider.pitch * 0.5F;
         this.setRotation(this.yaw, this.pitch);
         this.headYaw = this.bodyYaw = this.yaw;
         sidewaysVelocity = ((LivingEntity)this.rider).sidewaysSpeed * 0.5F;
         forwardVelocity = ((LivingEntity)this.rider).forwardSpeed;
         if (forwardVelocity <= 0.0F) {
            forwardVelocity *= 0.25F;
            this.soundTicks = 0;
         }

         if (this.onGround && this.jumpStrength == 0.0F && this.isAngry() && !this.jumping) {
            sidewaysVelocity = 0.0F;
            forwardVelocity = 0.0F;
         }

         if (this.jumpStrength > 0.0F && !this.isInAir() && this.onGround) {
            this.velocityY = this.getJumpStrength() * (double)this.jumpStrength;
            if (this.hasStatusEffect(StatusEffect.JUMP_BOOST)) {
               this.velocityY += (double)((float)(this.getEffectInstance(StatusEffect.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
            }

            this.setInAir(true);
            this.velocityDirty = true;
            if (forwardVelocity > 0.0F) {
               float var3 = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
               float var4 = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
               this.velocityX += (double)(-0.4F * var3 * this.jumpStrength);
               this.velocityZ += (double)(0.4F * var4 * this.jumpStrength);
               this.playSound("mob.horse.jump", 0.4F, 1.0F);
            }

            this.jumpStrength = 0.0F;
         }

         this.stepHeight = 1.0F;
         this.airSpeed = this.getMovementSpeed() * 0.1F;
         if (!this.world.isClient) {
            this.setMovementSpeed((float)this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).get());
            super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
         }

         if (this.onGround) {
            this.jumpStrength = 0.0F;
            this.setInAir(false);
         }

         this.prevHandSwingAmount = this.handSwingAmount;
         double var10 = this.x - this.prevX;
         double var5 = this.z - this.prevZ;
         float var7 = MathHelper.sqrt(var10 * var10 + var5 * var5) * 4.0F;
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         this.handSwingAmount += (var7 - this.handSwingAmount) * 0.4F;
         this.handSwing += this.handSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.airSpeed = 0.02F;
         super.moveEntityWithVelocity(sidewaysVelocity, forwardVelocity);
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("EatingHaystack", this.isEating());
      nbt.putBoolean("ChestedHorse", this.hasChest());
      nbt.putBoolean("HasReproduced", this.hasBred());
      nbt.putBoolean("Bred", this.isReadyToBreed());
      nbt.putInt("Type", this.getType());
      nbt.putInt("Variant", this.getVariant());
      nbt.putInt("Temper", this.getTemper());
      nbt.putBoolean("Tame", this.isTame());
      nbt.putString("OwnerUUID", this.getOwnerUuid());
      if (this.hasChest()) {
         NbtList var2 = new NbtList();

         for(int var3 = 2; var3 < this.inventory.getSize(); ++var3) {
            ItemStack var4 = this.inventory.getStack(var3);
            if (var4 != null) {
               NbtCompound var5 = new NbtCompound();
               var5.putByte("Slot", (byte)var3);
               var4.writeNbt(var5);
               var2.add(var5);
            }
         }

         nbt.put("Items", var2);
      }

      if (this.inventory.getStack(1) != null) {
         nbt.put("ArmorItem", this.inventory.getStack(1).writeNbt(new NbtCompound()));
      }

      if (this.inventory.getStack(0) != null) {
         nbt.put("SaddleItem", this.inventory.getStack(0).writeNbt(new NbtCompound()));
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setEatingGrass(nbt.getBoolean("EatingHaystack"));
      this.setBredFlag(nbt.getBoolean("Bred"));
      this.setHasChestFlag(nbt.getBoolean("ChestedHorse"));
      this.setHasBred(nbt.getBoolean("HasReproduced"));
      this.setType(nbt.getInt("Type"));
      this.setVariant(nbt.getInt("Variant"));
      this.setTemper(nbt.getInt("Temper"));
      this.setTame(nbt.getBoolean("Tame"));
      String var2 = "";
      if (nbt.isType("OwnerUUID", 8)) {
         var2 = nbt.getString("OwnerUUID");
      } else {
         String var3 = nbt.getString("Owner");
         var2 = UserConverter.convertMobOwner(var3);
      }

      if (var2.length() > 0) {
         this.setOwnerName(var2);
      }

      IEntityAttributeInstance var9 = this.getAttributes().get("Speed");
      if (var9 != null) {
         this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(var9.getBase() * 0.25);
      }

      if (this.hasChest()) {
         NbtList var4 = nbt.getList("Items", 10);
         this.updateInventory();

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            NbtCompound var6 = var4.getCompound(var5);
            int var7 = var6.getByte("Slot") & 255;
            if (var7 >= 2 && var7 < this.inventory.getSize()) {
               this.inventory.setStack(var7, ItemStack.fromNbt(var6));
            }
         }
      }

      if (nbt.isType("ArmorItem", 10)) {
         ItemStack var10 = ItemStack.fromNbt(nbt.getCompound("ArmorItem"));
         if (var10 != null && isHorseArmor(var10.getItem())) {
            this.inventory.setStack(1, var10);
         }
      }

      if (nbt.isType("SaddleItem", 10)) {
         ItemStack var11 = ItemStack.fromNbt(nbt.getCompound("SaddleItem"));
         if (var11 != null && var11.getItem() == Items.SADDLE) {
            this.inventory.setStack(0, var11);
         }
      } else if (nbt.getBoolean("Saddle")) {
         this.inventory.setStack(0, new ItemStack(Items.SADDLE));
      }

      this.updateSaddle();
   }

   @Override
   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (other.getClass() != this.getClass()) {
         return false;
      } else {
         HorseBaseEntity var2 = (HorseBaseEntity)other;
         if (this.canBreed() && var2.canBreed()) {
            int var3 = this.getType();
            int var4 = var2.getType();
            return var3 == var4 || var3 == 0 && var4 == 1 || var3 == 1 && var4 == 0;
         } else {
            return false;
         }
      }
   }

   @Override
   public PassiveEntity makeChild(PassiveEntity other) {
      HorseBaseEntity var2 = (HorseBaseEntity)other;
      HorseBaseEntity var3 = new HorseBaseEntity(this.world);
      int var4 = this.getType();
      int var5 = var2.getType();
      int var6 = 0;
      if (var4 == var5) {
         var6 = var4;
      } else if (var4 == 0 && var5 == 1 || var4 == 1 && var5 == 0) {
         var6 = 2;
      }

      if (var6 == 0) {
         int var8 = this.random.nextInt(9);
         int var7;
         if (var8 < 4) {
            var7 = this.getVariant() & 0xFF;
         } else if (var8 < 8) {
            var7 = var2.getVariant() & 0xFF;
         } else {
            var7 = this.random.nextInt(7);
         }

         int var9 = this.random.nextInt(5);
         if (var9 < 2) {
            var7 |= this.getVariant() & 0xFF00;
         } else if (var9 < 4) {
            var7 |= var2.getVariant() & 0xFF00;
         } else {
            var7 |= this.random.nextInt(5) << 8 & 0xFF00;
         }

         var3.setVariant(var7);
      }

      var3.setType(var6);
      double var14 = this.initializeAttribute(EntityAttributes.MAX_HEALTH).getBase()
         + other.initializeAttribute(EntityAttributes.MAX_HEALTH).getBase()
         + (double)this.getChildHealthBonus();
      var3.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(var14 / 3.0);
      double var15 = this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).getBase()
         + other.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).getBase()
         + this.getChildJumpStrengthBonus();
      var3.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBase(var15 / 3.0);
      double var11 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).getBase()
         + other.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).getBase()
         + this.getMovementSpeedBonus();
      var3.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(var11 / 3.0);
      return var3;
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      int var3 = 0;
      int var4 = 0;
      if (entityData instanceof HorseBaseEntity.Data) {
         var3 = ((HorseBaseEntity.Data)entityData).type;
         var4 = ((HorseBaseEntity.Data)entityData).variant & 0xFF | this.random.nextInt(5) << 8;
      } else {
         if (this.random.nextInt(10) == 0) {
            var3 = 1;
         } else {
            int var5 = this.random.nextInt(7);
            int var6 = this.random.nextInt(5);
            var3 = 0;
            var4 = var5 | var6 << 8;
         }

         entityData = new HorseBaseEntity.Data(var3, var4);
      }

      this.setType(var3);
      this.setVariant(var4);
      if (this.random.nextInt(5) == 0) {
         this.setBreedingAge(-24000);
      }

      if (var3 != 4 && var3 != 3) {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase((double)this.getChildHealthBonus());
         if (var3 == 0) {
            this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(this.getMovementSpeedBonus());
         } else {
            this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.175F);
         }
      } else {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(15.0);
         this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.2F);
      }

      if (var3 != 2 && var3 != 1) {
         this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBase(this.getChildJumpStrengthBonus());
      } else {
         this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBase(0.5);
      }

      this.setHealth(this.getMaxHealth());
      return entityData;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getGrassAnimationProgress(float tickDelta) {
      return this.lastEatingGrassAnimationProgress + (this.eatingGrassAnimationProgress - this.lastEatingGrassAnimationProgress) * tickDelta;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getAngryAnimationProgress(float tickDelta) {
      return this.lastAngryAnimationProgress + (this.angryAnimationProgress - this.lastAngryAnimationProgress) * tickDelta;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getEatingAnimationProgress(float tickDelta) {
      return this.lastEatingAnimationProgress + (this.eatingAnimationProgress - this.lastEatingAnimationProgress) * tickDelta;
   }

   public void setJumpStrength(int strength) {
      if (this.isSaddled()) {
         if (strength < 0) {
            strength = 0;
         } else {
            this.jumping = true;
            this.updateAnger();
         }

         if (strength >= 90) {
            this.jumpStrength = 1.0F;
         } else {
            this.jumpStrength = 0.4F + 0.4F * (float)strength / 90.0F;
         }
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   protected void spawnPlayerReactionParticles(boolean positive) {
      ParticleType var2 = positive ? ParticleType.HEART : ParticleType.SMOKE_NORMAL;

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.random.nextGaussian() * 0.02;
         double var6 = this.random.nextGaussian() * 0.02;
         double var8 = this.random.nextGaussian() * 0.02;
         this.world
            .addParticle(
               var2,
               this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
               this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
               this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
               var4,
               var6,
               var8
            );
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 7) {
         this.spawnPlayerReactionParticles(true);
      } else if (event == 6) {
         this.spawnPlayerReactionParticles(false);
      } else {
         super.doEvent(event);
      }
   }

   @Override
   public void updateRiderPositon() {
      super.updateRiderPositon();
      if (this.lastAngryAnimationProgress > 0.0F) {
         float var1 = MathHelper.sin(this.bodyYaw * (float) Math.PI / 180.0F);
         float var2 = MathHelper.cos(this.bodyYaw * (float) Math.PI / 180.0F);
         float var3 = 0.7F * this.lastAngryAnimationProgress;
         float var4 = 0.15F * this.lastAngryAnimationProgress;
         this.rider
            .setPosition(
               this.x + (double)(var3 * var1), this.y + this.getMountHeight() + this.rider.getRideHeight() + (double)var4, this.z - (double)(var3 * var2)
            );
         if (this.rider instanceof LivingEntity) {
            ((LivingEntity)this.rider).bodyYaw = this.bodyYaw;
         }
      }
   }

   private float getChildHealthBonus() {
      return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
   }

   private double getChildJumpStrengthBonus() {
      return 0.4F + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
   }

   private double getMovementSpeedBonus() {
      return (0.45F + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
   }

   public static boolean isHorseArmor(Item item) {
      return item == Items.IRON_HORSE_ARMOR || item == Items.GOLDEN_HORSE_ARMOR || item == Items.DIAMOND_HORSE_ARMOR;
   }

   @Override
   public boolean isClimbing() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   @Override
   public boolean m_81zmldzmm(int i, ItemStack c_72owraavl) {
      if (i == 499 && this.isNotAngry()) {
         if (c_72owraavl == null && this.hasChest()) {
            this.setHasChestFlag(false);
            this.updateInventory();
            return true;
         }

         if (c_72owraavl != null && c_72owraavl.getItem() == Item.byBlock(Blocks.CHEST) && !this.hasChest()) {
            this.setHasChestFlag(true);
            this.updateInventory();
            return true;
         }
      }

      int var3 = i - 400;
      if (var3 >= 0 && var3 < 2 && var3 < this.inventory.getSize()) {
         if (var3 == 0 && c_72owraavl != null && c_72owraavl.getItem() != Items.SADDLE) {
            return false;
         } else if (var3 != 1 || (c_72owraavl == null || isHorseArmor(c_72owraavl.getItem())) && this.drawHoverEffect()) {
            this.inventory.setStack(var3, c_72owraavl);
            this.updateSaddle();
            return true;
         } else {
            return false;
         }
      } else {
         int var4 = i - 500 + 2;
         if (var4 >= 2 && var4 < this.inventory.getSize()) {
            this.inventory.setStack(var4, c_72owraavl);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class Data implements EntityData {
      public int type;
      public int variant;

      public Data(int type, int variant) {
         this.type = type;
         this.variant = variant;
      }
   }
}
