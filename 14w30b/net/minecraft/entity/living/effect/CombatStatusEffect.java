package net.minecraft.entity.living.effect;

import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.resource.Identifier;

public class CombatStatusEffect extends StatusEffect {
   protected CombatStatusEffect(int i, Identifier c_07ipdbewr, boolean bl, int j) {
      super(i, c_07ipdbewr, bl, j);
   }

   @Override
   public double getModifier(int amplifier, AttributeModifier modifier) {
      return this.id == StatusEffect.WEAKNESS.id ? (double)(-0.5F * (float)(amplifier + 1)) : 1.3 * (double)(amplifier + 1);
   }
}
