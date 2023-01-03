package net.minecraft.entity.living.effect;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private int id;
   private int duration;
   private int amplifier;
   private boolean splash;
   private boolean ambient;
   @Environment(EnvType.CLIENT)
   private boolean permanent;
   private boolean particles;

   public StatusEffectInstance(int id, int duration) {
      this(id, duration, 0);
   }

   public StatusEffectInstance(int id, int duration, int amplifier) {
      this(id, duration, amplifier, false, true);
   }

   public StatusEffectInstance(int id, int duration, int amplifier, boolean ambient, boolean particles) {
      this.id = id;
      this.duration = duration;
      this.amplifier = amplifier;
      this.ambient = ambient;
      this.particles = particles;
   }

   public StatusEffectInstance(StatusEffectInstance instance) {
      this.id = instance.id;
      this.duration = instance.duration;
      this.amplifier = instance.amplifier;
      this.ambient = instance.ambient;
      this.particles = instance.particles;
   }

   public void combine(StatusEffectInstance instance) {
      if (this.id != instance.id) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      if (instance.amplifier > this.amplifier) {
         this.amplifier = instance.amplifier;
         this.duration = instance.duration;
      } else if (instance.amplifier == this.amplifier && this.duration < instance.duration) {
         this.duration = instance.duration;
      } else if (!instance.ambient && this.ambient) {
         this.ambient = instance.ambient;
      }

      this.particles = instance.particles;
   }

   public int getId() {
      return this.id;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public void setSplash(boolean splash) {
      this.splash = splash;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean hasParticles() {
      return this.particles;
   }

   public boolean tick(LivingEntity entity) {
      if (this.duration > 0) {
         if (StatusEffect.BY_ID[this.id].shouldApply(this.duration, this.amplifier)) {
            this.apply(entity);
         }

         this.tickDuration();
      }

      return this.duration > 0;
   }

   private int tickDuration() {
      return --this.duration;
   }

   public void apply(LivingEntity entity) {
      if (this.duration > 0) {
         StatusEffect.BY_ID[this.id].apply(entity, this.amplifier);
      }
   }

   public String getName() {
      return StatusEffect.BY_ID[this.id].getName();
   }

   @Override
   public int hashCode() {
      return this.id;
   }

   @Override
   public String toString() {
      String var1 = "";
      if (this.getAmplifier() > 0) {
         var1 = this.getName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
      } else {
         var1 = this.getName() + ", Duration: " + this.getDuration();
      }

      if (this.splash) {
         var1 = var1 + ", Splash: true";
      }

      if (!this.particles) {
         var1 = var1 + ", Particles: false";
      }

      return StatusEffect.BY_ID[this.id].isUsable() ? "(" + var1 + ")" : var1;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof StatusEffectInstance)) {
         return false;
      } else {
         StatusEffectInstance var2 = (StatusEffectInstance)object;
         return this.id == var2.id
            && this.amplifier == var2.amplifier
            && this.duration == var2.duration
            && this.splash == var2.splash
            && this.ambient == var2.ambient;
      }
   }

   public NbtCompound toNbt(NbtCompound nbt) {
      nbt.putByte("Id", (byte)this.getId());
      nbt.putByte("Amplifier", (byte)this.getAmplifier());
      nbt.putInt("Duration", this.getDuration());
      nbt.putBoolean("Ambient", this.isAmbient());
      nbt.putBoolean("ShowParticles", this.hasParticles());
      return nbt;
   }

   public static StatusEffectInstance fromNbt(NbtCompound nbt) {
      byte var1 = nbt.getByte("Id");
      if (var1 >= 0 && var1 < StatusEffect.BY_ID.length && StatusEffect.BY_ID[var1] != null) {
         byte var2 = nbt.getByte("Amplifier");
         int var3 = nbt.getInt("Duration");
         boolean var4 = nbt.getBoolean("Ambient");
         boolean var5 = true;
         if (nbt.isType("ShowParticles", 1)) {
            var5 = nbt.getBoolean("ShowParticles");
         }

         return new StatusEffectInstance(var1, var3, var2, var4, var5);
      } else {
         return null;
      }
   }

   @Environment(EnvType.CLIENT)
   public void setPermanent(boolean permanent) {
      this.permanent = permanent;
   }

   @Environment(EnvType.CLIENT)
   public boolean isPermanent() {
      return this.permanent;
   }
}
