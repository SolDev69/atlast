package net.minecraft.entity.living.effect;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AbstractEntityAttributeContainer;
import net.minecraft.resource.Identifier;

public class AbsorptionStatusEffect extends StatusEffect {
   protected AbsorptionStatusEffect(int i, Identifier c_07ipdbewr, boolean bl, int j) {
      super(i, c_07ipdbewr, bl, j);
   }

   @Override
   public void removeModifiers(LivingEntity entity, AbstractEntityAttributeContainer container, int amplifier) {
      entity.setAbsorption(entity.getAbsorption() - (float)(4 * (amplifier + 1)));
      super.removeModifiers(entity, container, amplifier);
   }

   @Override
   public void addModifiers(LivingEntity entity, AbstractEntityAttributeContainer container, int amplifier) {
      entity.setAbsorption(entity.getAbsorption() + (float)(4 * (amplifier + 1)));
      super.addModifiers(entity, container, amplifier);
   }
}
