package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private List goals = Lists.newArrayList();
   private List activeGoals = Lists.newArrayList();
   private final Profiler profiler;
   private int ticks;
   private int constantThree = 3;

   public GoalSelector(Profiler profiler) {
      this.profiler = profiler;
   }

   public void addGoal(int priority, Goal goal) {
      this.goals.add(new GoalSelector.Entry(priority, goal));
   }

   public void removeGoal(Goal goal) {
      Iterator var2 = this.goals.iterator();

      while(var2.hasNext()) {
         GoalSelector.Entry var3 = (GoalSelector.Entry)var2.next();
         Goal var4 = var3.goal;
         if (var4 == goal) {
            if (this.activeGoals.contains(var3)) {
               var4.stop();
               this.activeGoals.remove(var3);
            }

            var2.remove();
         }
      }
   }

   public void tick() {
      this.profiler.push("goalSetup");
      if (this.ticks++ % this.constantThree == 0) {
         for(GoalSelector.Entry var2 : this.goals) {
            boolean var3 = this.activeGoals.contains(var2);
            if (var3) {
               if (this.canUse(var2) && this.canContinue(var2)) {
                  continue;
               }

               var2.goal.stop();
               this.activeGoals.remove(var2);
            }

            if (this.canUse(var2) && var2.goal.canStart()) {
               var2.goal.start();
               this.activeGoals.add(var2);
            }
         }
      } else {
         Iterator var4 = this.activeGoals.iterator();

         while(var4.hasNext()) {
            GoalSelector.Entry var6 = (GoalSelector.Entry)var4.next();
            if (!this.canContinue(var6)) {
               var6.goal.stop();
               var4.remove();
            }
         }
      }

      this.profiler.pop();
      this.profiler.push("goalTick");

      for(GoalSelector.Entry var7 : this.activeGoals) {
         var7.goal.tick();
      }

      this.profiler.pop();
   }

   private boolean canContinue(GoalSelector.Entry goalListEntry) {
      return goalListEntry.goal.shouldContinue();
   }

   private boolean canUse(GoalSelector.Entry goalListEntry) {
      for(GoalSelector.Entry var3 : this.goals) {
         if (var3 != goalListEntry) {
            if (goalListEntry.priority >= var3.priority) {
               if (!this.differentControlBits(goalListEntry, var3) && this.activeGoals.contains(var3)) {
                  return false;
               }
            } else if (!var3.goal.canStop() && this.activeGoals.contains(var3)) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean differentControlBits(GoalSelector.Entry goalListEntry1, GoalSelector.Entry goalListEntry2) {
      return (goalListEntry1.goal.getControls() & goalListEntry2.goal.getControls()) == 0;
   }

   class Entry {
      public Goal goal;
      public int priority;

      public Entry(int priority, Goal goal) {
         this.priority = priority;
         this.goal = goal;
      }
   }
}
