package net.minecraft.entity.living.mob.hostile;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.RangedAttackMob;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class WitchEntity extends HostileEntity implements RangedAttackMob {
   private static final UUID DRINKING_SPEED_PENALTY_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier DRINKING_SPEED_PENALTY = new AttributeModifier(DRINKING_SPEED_PENALTY_UUID, "Drinking speed penalty", -0.25, 0)
      .setSerialized(false);
   private static final Item[] LOOT = new Item[]{
      Items.GLOWSTONE_DUST, Items.SUGAR, Items.REDSTONE, Items.SPIDER_EYE, Items.GLASS_BOTTLE, Items.GUNPOWDER, Items.STICK, Items.STICK
   };
   private int drinkTimeLeft;

   public WitchEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.6F, 1.95F);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
      this.goalSelector.addGoal(2, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(2, this.fleeExplodingCreeperGoal);
      this.goalSelector.addGoal(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(3, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, false));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.getDataTracker().put(21, (byte)0);
   }

   @Override
   protected String getAmbientSound() {
      return "mob.witch.idle";
   }

   @Override
   protected String getHurtSound() {
      return "mob.witch.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.witch.death";
   }

   public void setAgressive(boolean agressive) {
      this.getDataTracker().update(21, Byte.valueOf((byte)(agressive ? 1 : 0)));
   }

   public boolean isAgressive() {
      return this.getDataTracker().getByte(21) == 1;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(26.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   public void tickAI() {
      if (!this.world.isClient) {
         if (this.isAgressive()) {
            if (this.drinkTimeLeft-- <= 0) {
               this.setAgressive(false);
               ItemStack var5 = this.getStackInHand();
               this.setEquipmentStack(0, null);
               if (var5 != null && var5.getItem() == Items.POTION) {
                  List var6 = Items.POTION.getPotionEffects(var5);
                  if (var6 != null) {
                     for(StatusEffectInstance var4 : var6) {
                        this.addStatusEffect(new StatusEffectInstance(var4));
                     }
                  }
               }

               this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).removeModifier(DRINKING_SPEED_PENALTY);
            }
         } else {
            short var1 = -1;
            if (this.random.nextFloat() < 0.15F && this.isSubmergedIn(Material.WATER) && !this.hasStatusEffect(StatusEffect.WATER_BREATHING)) {
               var1 = 8237;
            } else if (this.random.nextFloat() < 0.15F && this.isOnFire() && !this.hasStatusEffect(StatusEffect.FIRE_RESISTANCE)) {
               var1 = 16307;
            } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               var1 = 16341;
            } else if (this.random.nextFloat() < 0.25F
               && this.getTargetEntity() != null
               && !this.hasStatusEffect(StatusEffect.SPEED)
               && this.getTargetEntity().getSquaredDistanceTo(this) > 121.0) {
               var1 = 16274;
            } else if (this.random.nextFloat() < 0.25F
               && this.getTargetEntity() != null
               && !this.hasStatusEffect(StatusEffect.SPEED)
               && this.getTargetEntity().getSquaredDistanceTo(this) > 121.0) {
               var1 = 16274;
            }

            if (var1 > -1) {
               this.setEquipmentStack(0, new ItemStack(Items.POTION, 1, var1));
               this.drinkTimeLeft = this.getStackInHand().getUseDuration();
               this.setAgressive(true);
               IEntityAttributeInstance var2 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
               var2.removeModifier(DRINKING_SPEED_PENALTY);
               var2.addModifier(DRINKING_SPEED_PENALTY);
            }
         }

         if (this.random.nextFloat() < 7.5E-4F) {
            this.world.doEntityEvent(this, (byte)15);
         }
      }

      super.tickAI();
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 15) {
         for(int var2 = 0; var2 < this.random.nextInt(35) + 10; ++var2) {
            this.world
               .addParticle(
                  ParticleType.SPELL_WITCH,
                  this.x + this.random.nextGaussian() * 0.13F,
                  this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
                  this.z + this.random.nextGaussian() * 0.13F,
                  0.0,
                  0.0,
                  0.0
               );
         }
      } else {
         super.doEvent(event);
      }
   }

   @Override
   protected float damageAfterEffectsAndEnchantments(DamageSource source, float damage) {
      damage = super.damageAfterEffectsAndEnchantments(source, damage);
      if (source.getAttacker() == this) {
         damage = 0.0F;
      }

      if (source.getMagic()) {
         damage = (float)((double)damage * 0.15);
      }

      return damage;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3) + 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = this.random.nextInt(3);
         Item var6 = LOOT[this.random.nextInt(LOOT.length)];
         if (lootingMultiplier > 0) {
            var5 += this.random.nextInt(lootingMultiplier + 1);
         }

         for(int var7 = 0; var7 < var5; ++var7) {
            this.dropItem(var6, 1);
         }
      }
   }

   @Override
   public void doRangedAttack(LivingEntity target, float range) {
      if (!this.isAgressive()) {
         PotionEntity var3 = new PotionEntity(this.world, this, 32732);
         double var4 = target.y + (double)target.getEyeHeight() - 1.1F;
         var3.pitch -= -20.0F;
         double var6 = target.x + target.velocityX - this.x;
         double var8 = var4 - this.y;
         double var10 = target.z + target.velocityZ - this.z;
         float var12 = MathHelper.sqrt(var6 * var6 + var10 * var10);
         if (var12 >= 8.0F && !target.hasStatusEffect(StatusEffect.SLOWNESS)) {
            var3.setPotionValue(32698);
         } else if (target.getHealth() >= 8.0F && !target.hasStatusEffect(StatusEffect.POISON)) {
            var3.setPotionValue(32660);
         } else if (var12 <= 3.0F && !target.hasStatusEffect(StatusEffect.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            var3.setPotionValue(32696);
         }

         var3.setVelocity(var6, var8 + (double)(var12 * 0.2F), var10, 0.75F, 8.0F);
         this.world.addEntity(var3);
      }
   }

   @Override
   public float getEyeHeight() {
      return 1.62F;
   }
}
