package net.minecraft.text;

public class SelectorText extends BaseText {
   private final String pattern;

   public SelectorText(String pattern) {
      this.pattern = pattern;
   }

   public String getPattern() {
      return this.pattern;
   }

   @Override
   public String getString() {
      return this.pattern;
   }

   public SelectorText copy() {
      SelectorText var1 = new SelectorText(this.pattern);
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
      } else if (!(object instanceof SelectorText)) {
         return false;
      } else {
         SelectorText var2 = (SelectorText)object;
         return this.pattern.equals(var2.pattern) && super.equals(object);
      }
   }

   @Override
   public String toString() {
      return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
