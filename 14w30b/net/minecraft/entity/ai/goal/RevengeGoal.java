package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.Box;

public class RevengeGoal extends TrackTargetGoal {
   private boolean callOthersForRevenge;
   private int lastAttackedTime;
   private final Class[] f_58bdftssb;

   public RevengeGoal(PathAwareEntity c_60guwxsid, boolean bl, Class... classs) {
      super(c_60guwxsid, false);
      this.callOthersForRevenge = bl;
      this.f_58bdftssb = classs;
      this.setControls(1);
   }

   @Override
   public boolean canStart() {
      int var1 = this.entity.getLastAttackedTime();
      return var1 != this.lastAttackedTime && this.canTarget(this.entity.getAttacker(), false);
   }

   @Override
   public void start() {
      this.entity.setAttackTarget(this.entity.getAttacker());
      this.lastAttackedTime = this.entity.getLastAttackedTime();
      if (this.callOthersForRevenge) {
         double var1 = this.getFollowRange();

         for(PathAwareEntity var5 : this.entity
            .world
            .getEntities(
               this.entity.getClass(),
               new Box(this.entity.x, this.entity.y, this.entity.z, this.entity.x + 1.0, this.entity.y + 1.0, this.entity.z + 1.0).expand(var1, 10.0, var1)
            )) {
            if (this.entity != var5 && var5.getTargetEntity() == null && !var5.isInSameTeam(this.entity.getAttacker())) {
               boolean var6 = false;

               for(Class var10 : this.f_58bdftssb) {
                  if (var5.getClass() == var10) {
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  this.m_01exkogok(var5, this.entity.getAttacker());
               }
            }
         }
      }

      super.start();
   }

   protected void m_01exkogok(PathAwareEntity c_60guwxsid, LivingEntity c_97zulxhng) {
      c_60guwxsid.setAttackTarget(c_97zulxhng);
   }
}
