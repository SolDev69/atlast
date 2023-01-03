package net.minecraft.entity.living.effect;

import net.minecraft.resource.Identifier;

public class InstantStatusEffect extends StatusEffect {
   public InstantStatusEffect(int i, Identifier c_07ipdbewr, boolean bl, int j) {
      super(i, c_07ipdbewr, bl, j);
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public boolean shouldApply(int duration, int amplifier) {
      return duration >= 1;
   }
}
