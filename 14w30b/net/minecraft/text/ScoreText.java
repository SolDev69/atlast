package net.minecraft.text;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.MinecraftServer;

public class ScoreText extends BaseText {
   private final String owner;
   private final String objective;
   private String value = "";

   public ScoreText(String owner, String objective) {
      this.owner = owner;
      this.objective = objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public String getString() {
      MinecraftServer var1 = MinecraftServer.getInstance();
      if (var1 != null && var1.hasGameDir() && StringUtils.isStringEmpty(this.value)) {
         Scoreboard var2 = var1.getWorld(0).getScoreboard();
         ScoreboardObjective var3 = var2.getObjective(this.objective);
         if (var2.hasScore(this.owner, var3)) {
            ScoreboardScore var4 = var2.getScore(this.owner, var3);
            this.setValue(String.format("%d", var4.get()));
         } else {
            this.value = "";
         }
      }

      return this.value;
   }

   public ScoreText copy() {
      ScoreText var1 = new ScoreText(this.owner, this.objective);
      var1.setValue(this.value);
      var1.setStyle(this.getStyle().deepCopy());

      for(Text var3 : this.getSiblings()) {
         var1.append(var3.copy());
      }

      return var1;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ScoreText)) {
         return false;
      } else {
         ScoreText var2 = (ScoreText)object;
         return this.owner.equals(var2.owner) && this.objective.equals(var2.objective) && super.equals(object);
      }
   }

   @Override
   public String toString() {
      return "ScoreComponent{name='"
         + this.owner
         + '\''
         + "objective='"
         + this.objective
         + '\''
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getStyle()
         + '}';
   }
}
