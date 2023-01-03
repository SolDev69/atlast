package net.minecraft.stat;

import net.minecraft.text.Text;

public class GeneralStat extends Stat {
   public GeneralStat(String string, Text c_21uoltggz, StatFormatter c_59bznbmob) {
      super(string, c_21uoltggz, c_59bznbmob);
   }

   public GeneralStat(String string, Text c_21uoltggz) {
      super(string, c_21uoltggz);
   }

   @Override
   public Stat register() {
      super.register();
      Stats.GENERAL.add(this);
      return this;
   }
}
