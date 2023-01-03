package net.minecraft.stat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.criterion.StatCriterion;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Stat {
   public final String id;
   private final Text name;
   public boolean local;
   private final StatFormatter formatter;
   private final ScoreboardCriterion criterion;
   private Class dataType;
   private static NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);
   public static StatFormatter NUMBER_FORMATTER = new StatFormatter() {
      @Environment(EnvType.CLIENT)
      @Override
      public String format(int value) {
         return Stat.NUMBER_FORMAT.format((long)value);
      }
   };
   private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00");
   public static StatFormatter TIME_FORMATTER = new StatFormatter() {
      @Environment(EnvType.CLIENT)
      @Override
      public String format(int value) {
         double var2 = (double)value / 20.0;
         double var4 = var2 / 60.0;
         double var6 = var4 / 60.0;
         double var8 = var6 / 24.0;
         double var10 = var8 / 365.0;
         if (var10 > 0.5) {
            return Stat.DECIMAL_FORMAT.format(var10) + " y";
         } else if (var8 > 0.5) {
            return Stat.DECIMAL_FORMAT.format(var8) + " d";
         } else if (var6 > 0.5) {
            return Stat.DECIMAL_FORMAT.format(var6) + " h";
         } else {
            return var4 > 0.5 ? Stat.DECIMAL_FORMAT.format(var4) + " m" : var2 + " s";
         }
      }
   };
   public static StatFormatter DISTANCE_FORMATTER = new StatFormatter() {
      @Environment(EnvType.CLIENT)
      @Override
      public String format(int value) {
         double var2 = (double)value / 100.0;
         double var4 = var2 / 1000.0;
         if (var4 > 0.5) {
            return Stat.DECIMAL_FORMAT.format(var4) + " km";
         } else {
            return var2 > 0.5 ? Stat.DECIMAL_FORMAT.format(var2) + " m" : value + " cm";
         }
      }
   };
   public static StatFormatter DIVIDE_BY_TEN_FORMATTER = new StatFormatter() {
      @Environment(EnvType.CLIENT)
      @Override
      public String format(int value) {
         return Stat.DECIMAL_FORMAT.format((double)value * 0.1);
      }
   };

   public Stat(String id, Text name, StatFormatter formatter) {
      this.id = id;
      this.name = name;
      this.formatter = formatter;
      this.criterion = new StatCriterion(this);
      ScoreboardCriterion.BY_NAME.put(this.criterion.getName(), this.criterion);
   }

   public Stat(String id, Text name) {
      this(id, name, NUMBER_FORMATTER);
   }

   public Stat setLocal() {
      this.local = true;
      return this;
   }

   public Stat register() {
      if (Stats.BY_ID.containsKey(this.id)) {
         throw new RuntimeException("Duplicate stat id: \"" + ((Stat)Stats.BY_ID.get(this.id)).name + "\" and \"" + this.name + "\" at id " + this.id);
      } else {
         Stats.ALL.add(this);
         Stats.BY_ID.put(this.id, this);
         return this;
      }
   }

   public boolean isAchievement() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   public String format(int value) {
      return this.formatter.format(value);
   }

   public Text getDecoratedName() {
      Text var1 = this.name.copy();
      var1.getStyle().setColor(Formatting.GRAY);
      var1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new LiteralText(this.id)));
      return var1;
   }

   public Text getNameForChat() {
      Text var1 = this.getDecoratedName();
      Text var2 = new LiteralText("[").append(var1).append("]");
      var2.setStyle(var1.getStyle());
      return var2;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         Stat var2 = (Stat)object;
         return this.id.equals(var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public String toString() {
      return "Stat{id="
         + this.id
         + ", nameId="
         + this.name
         + ", awardLocallyOnly="
         + this.local
         + ", formatter="
         + this.formatter
         + ", objectiveCriteria="
         + this.criterion
         + '}';
   }

   public ScoreboardCriterion getCriterion() {
      return this.criterion;
   }

   public Class getDataType() {
      return this.dataType;
   }

   public Stat setDataType(Class dataType) {
      this.dataType = dataType;
      return this;
   }
}
