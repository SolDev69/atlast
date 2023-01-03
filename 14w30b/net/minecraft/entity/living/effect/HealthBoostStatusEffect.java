package net.minecraft.entity.living.effect;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AbstractEntityAttributeContainer;
import net.minecraft.resource.Identifier;

public class HealthBoostStatusEffect extends StatusEffect {
   public HealthBoostStatusEffect(int i, Identifier c_07ipdbewr, boolean bl, int j) {
      super(i, c_07ipdbewr, bl, j);
   }

   @Override
   public void removeModifiers(LivingEntity entity, AbstractEntityAttributeContainer container, int amplifier) {
      super.removeModifiers(entity, container, amplifier);
      if (entity.getHealth() > entity.getMaxHealth()) {
         entity.setHealth(entity.getMaxHealth());
      }
   }
}
