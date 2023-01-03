package net.minecraft.stat.achievement;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.stat.Stat;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AchievementStat extends Stat {
   public final int column;
   public final int row;
   public final AchievementStat parent;
   private final String description;
   @Environment(EnvType.CLIENT)
   private AchievementStatFormatter achievementFormatter;
   public final ItemStack icon;
   private boolean challenge;

   public AchievementStat(String statName, String achievementName, int column, int row, Item logo, AchievementStat parent) {
      this(statName, achievementName, column, row, new ItemStack(logo), parent);
   }

   public AchievementStat(String statName, String achievementName, int column, int row, Block block, AchievementStat parent) {
      this(statName, achievementName, column, row, new ItemStack(block), parent);
   }

   public AchievementStat(String statName, String achievementName, int column, int row, ItemStack item, AchievementStat parent) {
      super(statName, new TranslatableText("achievement." + achievementName));
      this.icon = item;
      this.description = "achievement." + achievementName + ".desc";
      this.column = column;
      this.row = row;
      if (column < Achievements.minColumn) {
         Achievements.minColumn = column;
      }

      if (row < Achievements.minRow) {
         Achievements.minRow = row;
      }

      if (column > Achievements.maxColumn) {
         Achievements.maxColumn = column;
      }

      if (row > Achievements.maxRow) {
         Achievements.maxRow = row;
      }

      this.parent = parent;
   }

   public AchievementStat setLocal() {
      this.local = true;
      return this;
   }

   public AchievementStat setChallenge() {
      this.challenge = true;
      return this;
   }

   public AchievementStat register() {
      super.register();
      Achievements.ALL.add(this);
      return this;
   }

   @Override
   public boolean isAchievement() {
      return true;
   }

   @Override
   public Text getDecoratedName() {
      Text var1 = super.getDecoratedName();
      var1.getStyle().setColor(this.isChallenge() ? Formatting.DARK_PURPLE : Formatting.GREEN);
      return var1;
   }

   public AchievementStat setDataType(Class class_) {
      return (AchievementStat)super.setDataType(class_);
   }

   @Environment(EnvType.CLIENT)
   public String getDescription() {
      return this.achievementFormatter != null ? this.achievementFormatter.format(I18n.translate(this.description)) : I18n.translate(this.description);
   }

   @Environment(EnvType.CLIENT)
   public AchievementStat setFormatter(AchievementStatFormatter formatter) {
      this.achievementFormatter = formatter;
      return this;
   }

   public boolean isChallenge() {
      return this.challenge;
   }
}
