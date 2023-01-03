package net.minecraft.entity.living.mob;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;

public class MobVisibilityCache {
   MobEntity mob;
   List visibleEntities = Lists.newArrayList();
   List invisibleEntities = Lists.newArrayList();

   public MobVisibilityCache(MobEntity mob) {
      this.mob = mob;
   }

   public void clear() {
      this.visibleEntities.clear();
      this.invisibleEntities.clear();
   }

   public boolean canSee(Entity entity) {
      if (this.visibleEntities.contains(entity)) {
         return true;
      } else if (this.invisibleEntities.contains(entity)) {
         return false;
      } else {
         this.mob.world.profiler.push("canSee");
         boolean var2 = this.mob.canSee(entity);
         this.mob.world.profiler.pop();
         if (var2) {
            this.visibleEntities.add(entity);
         } else {
            this.invisibleEntities.add(entity);
         }

         return var2;
      }
   }
}
