package net.minecraft.entity.vehicle;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

public class RideableMinecartEntity extends MinecartEntity {
   public RideableMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public RideableMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public boolean interact(PlayerEntity player) {
      if (this.rider != null && this.rider instanceof PlayerEntity && this.rider != player) {
         return true;
      } else if (this.rider != null && this.rider != player) {
         return false;
      } else {
         if (!this.world.isClient) {
            player.startRiding(this);
         }

         return true;
      }
   }

   @Override
   public void onActivatorRail(int x, int y, int z, boolean powered) {
      if (powered) {
         if (this.rider != null) {
            this.rider.startRiding(null);
         }

         if (this.getDamageWobbleTicks() == 0) {
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.setDamageWobbleStrength(50.0F);
            this.onDamaged();
         }
      }
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.RIDEABLE;
   }
}
