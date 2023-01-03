package net.minecraft.text;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class BaseText implements Text {
   protected List siblings = Lists.newArrayList();
   private Style style;

   @Override
   public Text append(Text text) {
      text.getStyle().setParent(this.getStyle());
      this.siblings.add(text);
      return this;
   }

   @Override
   public List getSiblings() {
      return this.siblings;
   }

   @Override
   public Text append(String string) {
      return this.append(new LiteralText(string));
   }

   @Override
   public Text setStyle(Style style) {
      this.style = style;

      for(Text var3 : this.siblings) {
         var3.getStyle().setParent(this.getStyle());
      }

      return this;
   }

   @Override
   public Style getStyle() {
      if (this.style == null) {
         this.style = new Style();

         for(Text var2 : this.siblings) {
            var2.getStyle().setParent(this.style);
         }
      }

      return this.style;
   }

   @Override
   public Iterator iterator() {
      return Iterators.concat(Iterators.forArray(new BaseText[]{this}), concatenate(this.siblings));
   }

   @Override
   public final String buildString() {
      StringBuilder var1 = new StringBuilder();

      for(Text var3 : this) {
         var1.append(var3.getString());
      }

      return var1.toString();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public final String buildFormattedString() {
      StringBuilder var1 = new StringBuilder();

      for(Text var3 : this) {
         var1.append(var3.getStyle().asString());
         var1.append(var3.getString());
         var1.append(Formatting.RESET);
      }

      return var1.toString();
   }

   public static Iterator concatenate(Iterable text) {
      Iterator var1 = Iterators.concat(Iterators.transform(text.iterator(), new Function() {
         public Iterator apply(Text c_21uoltggz) {
            return c_21uoltggz.iterator();
         }
      }));
      return Iterators.transform(var1, new Function() {
         public Text apply(Text c_21uoltggz) {
            Text var2 = c_21uoltggz.copy();
            var2.setStyle(var2.getStyle().copy());
            return var2;
         }
      });
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BaseText)) {
         return false;
      } else {
         BaseText var2 = (BaseText)object;
         return this.siblings.equals(var2.siblings) && this.getStyle().equals(var2.getStyle());
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.style.hashCode() + this.siblings.hashCode();
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
