package net.minecraft.entity.living.mob;

import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.world.World;

public abstract class GolemEntity extends PathAwareEntity implements EntityCategoryProvider {
   public GolemEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected String getAmbientSound() {
      return "none";
   }

   @Override
   protected String getHurtSound() {
      return "none";
   }

   @Override
   protected String getDeathSound() {
      return "none";
   }

   @Override
   public int getMinAmbientSoundDelay() {
      return 120;
   }

   @Override
   protected boolean canDespawn() {
      return false;
   }
}
