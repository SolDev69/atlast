package net.minecraft.scoreboard.criterion;

import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class HealthCriterion extends GenericCriterion {
   public HealthCriterion(String string) {
      super(string);
   }

   @Override
   public int countScore(List owners) {
      float var2 = 0.0F;

      for(PlayerEntity var4 : owners) {
         var2 += var4.getHealth() + var4.getAbsorption();
      }

      if (owners.size() > 0) {
         var2 /= (float)owners.size();
      }

      return MathHelper.ceil(var2);
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public ScoreboardCriterion.RenderType getRenderType() {
      return ScoreboardCriterion.RenderType.HEARTS;
   }
}
