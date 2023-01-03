package net.minecraft.entity.living.mob.passive.animal.tamable;

import java.util.UUID;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.server.dedicated.UserConverter;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public abstract class TameableEntity extends AnimalEntity implements Tameable {
   protected SitGoal sitGoal = new SitGoal(this);

   public TameableEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.m_65opiswxw();
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)0);
      this.dataTracker.put(17, "");
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      if (this.getOwnerName() == null) {
         nbt.putString("OwnerUUID", "");
      } else {
         nbt.putString("OwnerUUID", this.getOwnerName());
      }

      nbt.putBoolean("Sitting", this.isSitting());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      String var2 = "";
      if (nbt.isType("OwnerUUID", 8)) {
         var2 = nbt.getString("OwnerUUID");
      } else {
         String var3 = nbt.getString("Owner");
         var2 = UserConverter.convertMobOwner(var3);
      }

      if (var2.length() > 0) {
         this.setOwner(var2);
         this.setTamed(true);
      }

      this.sitGoal.setEnabledWithOwner(nbt.getBoolean("Sitting"));
      this.setSitting(nbt.getBoolean("Sitting"));
   }

   protected void showEmoteParticle(boolean positive) {
      ParticleType var2 = ParticleType.HEART;
      if (!positive) {
         var2 = ParticleType.SMOKE_NORMAL;
      }

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
         this.showEmoteParticle(true);
      } else if (event == 6) {
         this.showEmoteParticle(false);
      } else {
         super.doEvent(event);
      }
   }

   public boolean isTamed() {
      return (this.dataTracker.getByte(16) & 4) != 0;
   }

   public void setTamed(boolean tamed) {
      byte var2 = this.dataTracker.getByte(16);
      if (tamed) {
         this.dataTracker.update(16, (byte)(var2 | 4));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -5));
      }

      this.m_65opiswxw();
   }

   protected void m_65opiswxw() {
   }

   public boolean isSitting() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setSitting(boolean sitting) {
      byte var2 = this.dataTracker.getByte(16);
      if (sitting) {
         this.dataTracker.update(16, (byte)(var2 | 1));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -2));
      }
   }

   @Override
   public String getOwnerName() {
      return this.dataTracker.getString(17);
   }

   public void setOwner(String owner) {
      this.dataTracker.update(17, owner);
   }

   public LivingEntity getOwner() {
      try {
         UUID var1 = UUID.fromString(this.getOwnerName());
         return var1 == null ? null : this.world.getPlayer(var1);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean m_77pxwyntx(LivingEntity c_97zulxhng) {
      return c_97zulxhng == this.getOwner();
   }

   public SitGoal getSitGoal() {
      return this.sitGoal;
   }

   public boolean shouldAttack(LivingEntity attackedEntity, LivingEntity attacker) {
      return true;
   }

   @Override
   public AbstractTeam getScoreboardTeam() {
      if (this.isTamed()) {
         LivingEntity var1 = this.getOwner();
         if (var1 != null) {
            return var1.getScoreboardTeam();
         }
      }

      return super.getScoreboardTeam();
   }

   @Override
   public boolean isInSameTeam(LivingEntity entity) {
      if (this.isTamed()) {
         LivingEntity var2 = this.getOwner();
         if (entity == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.isInSameTeam(entity);
         }
      }

      return super.isInSameTeam(entity);
   }

   @Override
   public void onKilled(DamageSource source) {
      if (!this.world.isClient
         && this.world.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof ServerPlayerEntity) {
         ((ServerPlayerEntity)this.getOwner()).sendMessage(this.getDamageTracker().getDeathMessage());
      }

      super.onKilled(source);
   }
}
