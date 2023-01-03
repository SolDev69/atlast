package net.minecraft.entity.living.mob;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategoryProvider;

public interface Monster extends EntityCategoryProvider {
   Predicate MONSTER_FILTER = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof Monster;
      }
   };
   Predicate VISIBLE_MONSTER_FILTER = new Predicate() {
      public boolean apply(Entity c_47ldwddrb) {
         return c_47ldwddrb instanceof Monster && !c_47ldwddrb.isInvisible();
      }
   };
}
