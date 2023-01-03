package net.minecraft.entity.living.mob.ambient;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity implements EntityCategoryProvider {
   public AmbientEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   public boolean isTameable() {
      return false;
   }

   @Override
   protected boolean canInteract(PlayerEntity player) {
      return false;
   }
}
