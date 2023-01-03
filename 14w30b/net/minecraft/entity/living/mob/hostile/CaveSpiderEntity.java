package net.minecraft.entity.living.mob.hostile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class CaveSpiderEntity extends SpiderEntity {
   public CaveSpiderEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.7F, 0.5F);
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(12.0);
   }

   @Override
   public boolean attack(Entity entity) {
      if (super.attack(entity)) {
         if (entity instanceof LivingEntity) {
            byte var2 = 0;
            if (this.world.getDifficulty() == Difficulty.NORMAL) {
               var2 = 7;
            } else if (this.world.getDifficulty() == Difficulty.HARD) {
               var2 = 15;
            }

            if (var2 > 0) {
               ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffect.POISON.id, var2 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      return entityData;
   }

   @Override
   public float getEyeHeight() {
      return 0.45F;
   }
}
