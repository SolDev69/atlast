package net.minecraft.stat;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ItemStat extends Stat {
   private final Item item;

   public ItemStat(String name, String nameId, Text item, Item c_30vndvelc) {
      super(name + nameId, item);
      this.item = c_30vndvelc;
      int var5 = Item.getRawId(c_30vndvelc);
      if (var5 != 0) {
         ScoreboardCriterion.BY_NAME.put(name + var5, this.getCriterion());
      }
   }

   @Environment(EnvType.CLIENT)
   public Item getItem() {
      return this.item;
   }
}
