package net.minecraft.entity.living.mob.hostile.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;

public class EnderDragonPart extends Entity {
   public final EnderDragon dragon;
   public final String name;

   public EnderDragonPart(EnderDragon dragon, String name, float width, float height) {
      super(dragon.getWorld());
      this.setDimensions(width, height);
      this.dragon = dragon;
      this.name = name;
   }

   @Override
   protected void initDataTracker() {
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   public boolean hasCollision() {
      return true;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return this.isInvulnerable(source) ? false : this.dragon.damage(this, source, amount);
   }

   @Override
   public boolean is(Entity entity) {
      return this == entity || this.dragon == entity;
   }
}
