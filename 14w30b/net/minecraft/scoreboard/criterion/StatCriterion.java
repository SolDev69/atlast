package net.minecraft.scoreboard.criterion;

import net.minecraft.stat.Stat;

public class StatCriterion extends GenericCriterion {
   private final Stat stat;

   public StatCriterion(Stat stat) {
      super(stat.id);
      this.stat = stat;
   }
}
