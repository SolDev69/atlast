package net.minecraft.text;

public class LiteralText extends BaseText {
   private final String string;

   public LiteralText(String string) {
      this.string = string;
   }

   public String getRawString() {
      return this.string;
   }

   @Override
   public String getString() {
      return this.string;
   }

   public LiteralText copy() {
      LiteralText var1 = new LiteralText(this.string);
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
      } else if (!(object instanceof LiteralText)) {
         return false;
      } else {
         LiteralText var2 = (LiteralText)object;
         return this.string.equals(var2.getRawString()) && super.equals(object);
      }
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
