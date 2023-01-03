package net.minecraft.entity.living.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttribute;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.text.StringUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StatusEffect {
   public static final StatusEffect[] BY_ID = new StatusEffect[32];
   private static final Map REGISTRY = Maps.newHashMap();
   public static final StatusEffect UNKNOWN = null;
   public static final StatusEffect SPEED = new StatusEffect(1, new Identifier("speed"), false, 8171462)
      .setName("potion.moveSpeed")
      .setIcon(0, 0)
      .addModifier(EntityAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, 2);
   public static final StatusEffect SLOWNESS = new StatusEffect(2, new Identifier("slowness"), true, 5926017)
      .setName("potion.moveSlowdown")
      .setIcon(1, 0)
      .addModifier(EntityAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, 2);
   public static final StatusEffect HASTE = new StatusEffect(3, new Identifier("haste"), false, 14270531)
      .setName("potion.digSpeed")
      .setIcon(2, 0)
      .setEffectiveness(1.5);
   public static final StatusEffect MINING_FAIGUE = new StatusEffect(4, new Identifier("mining_fatigue"), true, 4866583)
      .setName("potion.digSlowDown")
      .setIcon(3, 0);
   public static final StatusEffect STRENGTH = new CombatStatusEffect(5, new Identifier("strength"), false, 9643043)
      .setName("potion.damageBoost")
      .setIcon(4, 0)
      .addModifier(EntityAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5, 2);
   public static final StatusEffect INSTANT_HEALTH = new InstantStatusEffect(6, new Identifier("instant_health"), false, 16262179).setName("potion.heal");
   public static final StatusEffect INSTANT_DAMAGE = new InstantStatusEffect(7, new Identifier("instant_damage"), true, 4393481).setName("potion.harm");
   public static final StatusEffect JUMP_BOOST = new StatusEffect(8, new Identifier("jump_boost"), false, 2293580).setName("potion.jump").setIcon(2, 1);
   public static final StatusEffect NAUSEA = new StatusEffect(9, new Identifier("nausea"), true, 5578058)
      .setName("potion.confusion")
      .setIcon(3, 1)
      .setEffectiveness(0.25);
   public static final StatusEffect REGENERATION = new StatusEffect(10, new Identifier("regeneration"), false, 13458603)
      .setName("potion.regeneration")
      .setIcon(7, 0)
      .setEffectiveness(0.25);
   public static final StatusEffect RESISTANCE = new StatusEffect(11, new Identifier("resistance"), false, 10044730).setName("potion.resistance").setIcon(6, 1);
   public static final StatusEffect FIRE_RESISTANCE = new StatusEffect(12, new Identifier("fire_resistance"), false, 14981690)
      .setName("potion.fireResistance")
      .setIcon(7, 1);
   public static final StatusEffect WATER_BREATHING = new StatusEffect(13, new Identifier("water_breathing"), false, 3035801)
      .setName("potion.waterBreathing")
      .setIcon(0, 2);
   public static final StatusEffect INVISIBILITY = new StatusEffect(14, new Identifier("invisibility"), false, 8356754)
      .setName("potion.invisibility")
      .setIcon(0, 1);
   public static final StatusEffect BLINDNESS = new StatusEffect(15, new Identifier("blindness"), true, 2039587)
      .setName("potion.blindness")
      .setIcon(5, 1)
      .setEffectiveness(0.25);
   public static final StatusEffect NIGHTVISION = new StatusEffect(16, new Identifier("night_vision"), false, 2039713)
      .setName("potion.nightVision")
      .setIcon(4, 1);
   public static final StatusEffect HUNGER = new StatusEffect(17, new Identifier("hunger"), true, 5797459).setName("potion.hunger").setIcon(1, 1);
   public static final StatusEffect WEAKNESS = new CombatStatusEffect(18, new Identifier("weakness"), true, 4738376)
      .setName("potion.weakness")
      .setIcon(5, 0)
      .addModifier(EntityAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0, 0);
   public static final StatusEffect POISON = new StatusEffect(19, new Identifier("poison"), true, 5149489)
      .setName("potion.poison")
      .setIcon(6, 0)
      .setEffectiveness(0.25);
   public static final StatusEffect WITHER = new StatusEffect(20, new Identifier("wither"), true, 3484199)
      .setName("potion.wither")
      .setIcon(1, 2)
      .setEffectiveness(0.25);
   public static final StatusEffect HEALTH_BOOST = new HealthBoostStatusEffect(21, new Identifier("health_boost"), false, 16284963)
      .setName("potion.healthBoost")
      .setIcon(2, 2)
      .addModifier(EntityAttributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0);
   public static final StatusEffect ABSORPTION = new AbsorptionStatusEffect(22, new Identifier("absorption"), false, 2445989)
      .setName("potion.absorption")
      .setIcon(2, 2);
   public static final StatusEffect SATURATION = new InstantStatusEffect(23, new Identifier("saturation"), false, 16262179).setName("potion.saturation");
   public static final StatusEffect f_27qoxdfyi = null;
   public static final StatusEffect f_08ktouptp = null;
   public static final StatusEffect f_33twilfog = null;
   public static final StatusEffect f_19swmafaq = null;
   public static final StatusEffect f_96clmnkcv = null;
   public static final StatusEffect f_92ccogbrg = null;
   public static final StatusEffect f_54ahxzzwl = null;
   public static final StatusEffect f_30lmelyft = null;
   public final int id;
   private final Map modifiers = Maps.newHashMap();
   private final boolean harmful;
   private final int potionColor;
   private String name = "";
   private int iconIndex = -1;
   private double effectiveness;
   private boolean usable;

   protected StatusEffect(int id, Identifier harmful, boolean potionColor, int j) {
      this.id = id;
      BY_ID[id] = this;
      REGISTRY.put(harmful, this);
      this.harmful = potionColor;
      if (potionColor) {
         this.effectiveness = 0.5;
      } else {
         this.effectiveness = 1.0;
      }

      this.potionColor = j;
   }

   public static StatusEffect get(String id) {
      return (StatusEffect)REGISTRY.get(new Identifier(id));
   }

   public static String[] m_01todocay() {
      String[] var0 = new String[REGISTRY.size()];
      int var1 = 0;

      for(Identifier var3 : REGISTRY.keySet()) {
         var0[var1++] = var3.toString();
      }

      return var0;
   }

   protected StatusEffect setIcon(int column, int row) {
      this.iconIndex = column + row * 8;
      return this;
   }

   public int getId() {
      return this.id;
   }

   public void apply(LivingEntity entity, int amplifier) {
      if (this.id == REGENERATION.id) {
         if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(1.0F);
         }
      } else if (this.id == POISON.id) {
         if (entity.getHealth() > 1.0F) {
            entity.damage(DamageSource.MAGIC, 1.0F);
         }
      } else if (this.id == WITHER.id) {
         entity.damage(DamageSource.WITHER, 1.0F);
      } else if (this.id == HUNGER.id && entity instanceof PlayerEntity) {
         ((PlayerEntity)entity).addFatigue(0.025F * (float)(amplifier + 1));
      } else if (this.id == SATURATION.id && entity instanceof PlayerEntity) {
         if (!entity.world.isClient) {
            ((PlayerEntity)entity).getHungerManager().add(amplifier + 1, 1.0F);
         }
      } else if ((this.id != INSTANT_HEALTH.id || entity.isAffectedBySmite()) && (this.id != INSTANT_DAMAGE.id || !entity.isAffectedBySmite())) {
         if (this.id == INSTANT_DAMAGE.id && !entity.isAffectedBySmite() || this.id == INSTANT_HEALTH.id && entity.isAffectedBySmite()) {
            entity.damage(DamageSource.MAGIC, (float)(6 << amplifier));
         }
      } else {
         entity.heal((float)Math.max(4 << amplifier, 0));
      }
   }

   public void affectHealth(Entity thrower, Entity entity, LivingEntity multiplier, int strength, double d) {
      if ((this.id != INSTANT_HEALTH.id || multiplier.isAffectedBySmite()) && (this.id != INSTANT_DAMAGE.id || !multiplier.isAffectedBySmite())) {
         if (this.id == INSTANT_DAMAGE.id && !multiplier.isAffectedBySmite() || this.id == INSTANT_HEALTH.id && multiplier.isAffectedBySmite()) {
            int var8 = (int)(d * (double)(6 << strength) + 0.5);
            if (thrower == null) {
               multiplier.damage(DamageSource.MAGIC, (float)var8);
            } else {
               multiplier.damage(DamageSource.magic(thrower, entity), (float)var8);
            }
         }
      } else {
         int var7 = (int)(d * (double)(4 << strength) + 0.5);
         multiplier.heal((float)var7);
      }
   }

   public boolean isInstant() {
      return false;
   }

   public boolean shouldApply(int duration, int amplifier) {
      if (this.id == REGENERATION.id) {
         int var5 = 50 >> amplifier;
         if (var5 > 0) {
            return duration % var5 == 0;
         } else {
            return true;
         }
      } else if (this.id == POISON.id) {
         int var4 = 25 >> amplifier;
         if (var4 > 0) {
            return duration % var4 == 0;
         } else {
            return true;
         }
      } else if (this.id == WITHER.id) {
         int var3 = 40 >> amplifier;
         if (var3 > 0) {
            return duration % var3 == 0;
         } else {
            return true;
         }
      } else {
         return this.id == HUNGER.id;
      }
   }

   public StatusEffect setName(String name) {
      this.name = name;
      return this;
   }

   public String getName() {
      return this.name;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasIcon() {
      return this.iconIndex >= 0;
   }

   @Environment(EnvType.CLIENT)
   public int getIconIndex() {
      return this.iconIndex;
   }

   @Environment(EnvType.CLIENT)
   public boolean isHarmful() {
      return this.harmful;
   }

   @Environment(EnvType.CLIENT)
   public static String getDurationString(StatusEffectInstance instance) {
      if (instance.isPermanent()) {
         return "**:**";
      } else {
         int var1 = instance.getDuration();
         return StringUtils.getDurationString(var1);
      }
   }

   protected StatusEffect setEffectiveness(double effectiveness) {
      this.effectiveness = effectiveness;
      return this;
   }

   public double getEffectiveness() {
      return this.effectiveness;
   }

   public boolean isUsable() {
      return this.usable;
   }

   public int getPotionColor() {
      return this.potionColor;
   }

   public StatusEffect addModifier(IEntityAttribute attribute, String id, double value, int operation) {
      AttributeModifier var6 = new AttributeModifier(UUID.fromString(id), this.getName(), value, operation);
      this.modifiers.put(attribute, var6);
      return this;
   }

   @Environment(EnvType.CLIENT)
   public Map getModifiers() {
      return this.modifiers;
   }

   public void removeModifiers(LivingEntity entity, AbstractEntityAttributeContainer container, int amplifier) {
      for(Entry var5 : this.modifiers.entrySet()) {
         IEntityAttributeInstance var6 = container.get((IEntityAttribute)var5.getKey());
         if (var6 != null) {
            var6.removeModifier((AttributeModifier)var5.getValue());
         }
      }
   }

   public void addModifiers(LivingEntity entity, AbstractEntityAttributeContainer container, int amplifier) {
      for(Entry var5 : this.modifiers.entrySet()) {
         IEntityAttributeInstance var6 = container.get((IEntityAttribute)var5.getKey());
         if (var6 != null) {
            AttributeModifier var7 = (AttributeModifier)var5.getValue();
            var6.removeModifier(var7);
            var6.addModifier(new AttributeModifier(var7.getId(), this.getName() + " " + amplifier, this.getModifier(amplifier, var7), var7.getOperation()));
         }
      }
   }

   public double getModifier(int amplifier, AttributeModifier modifier) {
      return modifier.get() * (double)(amplifier + 1);
   }
}
