package net.minecraft.entity.projectile;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireballEntity extends ProjectileEntity {
   public int explosionPower = 1;

   public FireballEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Environment(EnvType.CLIENT)
   public FireballEntity(World c_54ruxjwzt, double d, double e, double f, double g, double h, double i) {
      super(c_54ruxjwzt, d, e, f, g, h, i);
   }

   public FireballEntity(World c_54ruxjwzt, LivingEntity c_97zulxhng, double d, double e, double f) {
      super(c_54ruxjwzt, c_97zulxhng, d, e, f);
   }

   @Override
   protected void onHit(HitResult hitResult) {
      if (!this.world.isClient) {
         if (hitResult.entity != null) {
            hitResult.entity.damage(DamageSource.fire(this, this.shooter), 6.0F);
         }

         this.world.explode(null, this.x, this.y, this.z, (float)this.explosionPower, true, this.world.getGameRules().getBoolean("mobGriefing"));
         this.remove();
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("ExplosionPower", this.explosionPower);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("ExplosionPower", 99)) {
         this.explosionPower = nbt.getInt("ExplosionPower");
      }
   }
}
