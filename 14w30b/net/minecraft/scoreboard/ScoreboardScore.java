package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;

public class ScoreboardScore {
   public static final Comparator COMPARATOR = new Comparator() {
      public int compare(ScoreboardScore c_64uztyeff, ScoreboardScore c_64uztyeff2) {
         if (c_64uztyeff.get() > c_64uztyeff2.get()) {
            return 1;
         } else {
            return c_64uztyeff.get() < c_64uztyeff2.get() ? -1 : c_64uztyeff2.getOwner().compareToIgnoreCase(c_64uztyeff.getOwner());
         }
      }
   };
   private final Scoreboard scoreboard;
   private final ScoreboardObjective objective;
   private final String owner;
   private int score;
   private boolean locked;
   private boolean forceUpdate;

   public ScoreboardScore(Scoreboard scoreboard, ScoreboardObjective objective, String owner) {
      this.scoreboard = scoreboard;
      this.objective = objective;
      this.owner = owner;
      this.forceUpdate = true;
   }

   public void increase(int amount) {
      if (this.objective.getCriterion().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.set(this.get() + amount);
      }
   }

   public void decrease(int amount) {
      if (this.objective.getCriterion().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.set(this.get() - amount);
      }
   }

   public void increment() {
      if (this.objective.getCriterion().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.increase(1);
      }
   }

   public int get() {
      return this.score;
   }

   public void set(int score) {
      int var2 = this.score;
      this.score = score;
      if (var2 != score || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreboard().onScoreUpdated(this);
      }
   }

   public ScoreboardObjective getObjective() {
      return this.objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public void setToTotalOf(List owners) {
      this.set(this.objective.getCriterion().countScore(owners));
   }
}
