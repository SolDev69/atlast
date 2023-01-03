package net.minecraft.text;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.locale.I18n;

public class TranslatableText extends BaseText {
   private final String key;
   private final Object[] args;
   private final Object lock = new Object();
   private long languageReloadTimestamp = -1L;
   List translations = Lists.newArrayList();
   public static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslatableText(String key, Object... args) {
      this.key = key;
      this.args = args;

      for(Object var6 : args) {
         if (var6 instanceof Text) {
            ((Text)var6).getStyle().setParent(this.getStyle());
         }
      }
   }

   synchronized void updateTranslations() {
      synchronized(this.lock) {
         long var2 = I18n.getLoadTimestamp();
         if (var2 == this.languageReloadTimestamp) {
            return;
         }

         this.languageReloadTimestamp = var2;
         this.translations.clear();
      }

      try {
         this.setTranslation(I18n.translate(this.key));
      } catch (TranslationException var6) {
         this.translations.clear();

         try {
            this.setTranslation(I18n.translateDefault(this.key));
         } catch (TranslationException var5) {
            throw var6;
         }
      }
   }

   protected void setTranslation(String translation) {
      boolean var2 = false;
      Matcher var3 = ARG_FORMAT.matcher(translation);
      int var4 = 0;
      int var5 = 0;

      try {
         int var7;
         for(; var3.find(var5); var5 = var7) {
            int var6 = var3.start();
            var7 = var3.end();
            if (var6 > var5) {
               LiteralText var8 = new LiteralText(String.format(translation.substring(var5, var6)));
               var8.getStyle().setParent(this.getStyle());
               this.translations.add(var8);
            }

            String var14 = var3.group(2);
            String var9 = translation.substring(var6, var7);
            if ("%".equals(var14) && "%%".equals(var9)) {
               LiteralText var15 = new LiteralText("%");
               var15.getStyle().setParent(this.getStyle());
               this.translations.add(var15);
            } else {
               if (!"s".equals(var14)) {
                  throw new TranslationException(this, "Unsupported format: '" + var9 + "'");
               }

               String var10 = var3.group(1);
               int var11 = var10 != null ? Integer.parseInt(var10) - 1 : var4++;
               if (var11 < this.args.length) {
                  this.translations.add(this.getArg(var11));
               }
            }
         }

         if (var5 < translation.length()) {
            LiteralText var13 = new LiteralText(String.format(translation.substring(var5)));
            var13.getStyle().setParent(this.getStyle());
            this.translations.add(var13);
         }
      } catch (IllegalFormatException var12) {
         throw new TranslationException(this, var12);
      }
   }

   private Text getArg(int index) {
      if (index >= this.args.length) {
         throw new TranslationException(this, index);
      } else {
         Object var2 = this.args[index];
         Object var3;
         if (var2 instanceof Text) {
            var3 = (Text)var2;
         } else {
            var3 = new LiteralText(var2 == null ? "null" : var2.toString());
            ((Text)var3).getStyle().setParent(this.getStyle());
         }

         return (Text)var3;
      }
   }

   @Override
   public Text setStyle(Style style) {
      super.setStyle(style);

      for(Object var5 : this.args) {
         if (var5 instanceof Text) {
            ((Text)var5).getStyle().setParent(this.getStyle());
         }
      }

      if (this.languageReloadTimestamp > -1L) {
         for(Text var7 : this.translations) {
            var7.getStyle().setParent(style);
         }
      }

      return this;
   }

   @Override
   public Iterator iterator() {
      this.updateTranslations();
      return Iterators.concat(concatenate(this.translations), concatenate(this.siblings));
   }

   @Override
   public String getString() {
      this.updateTranslations();
      StringBuilder var1 = new StringBuilder();

      for(Text var3 : this.translations) {
         var1.append(var3.getString());
      }

      return var1.toString();
   }

   public TranslatableText copy() {
      Object[] var1 = new Object[this.args.length];

      for(int var2 = 0; var2 < this.args.length; ++var2) {
         if (this.args[var2] instanceof Text) {
            var1[var2] = ((Text)this.args[var2]).copy();
         } else {
            var1[var2] = this.args[var2];
         }
      }

      TranslatableText var5 = new TranslatableText(this.key, var1);
      var5.setStyle(this.getStyle().deepCopy());

      for(Text var4 : this.getSiblings()) {
         var5.append(var4.copy());
      }

      return var5;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof TranslatableText)) {
         return false;
      } else {
         TranslatableText var2 = (TranslatableText)object;
         return Arrays.equals(this.args, var2.args) && this.key.equals(var2.key) && super.equals(object);
      }
   }

   @Override
   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.key.hashCode();
      return 31 * var1 + Arrays.hashCode(this.args);
   }

   @Override
   public String toString() {
      return "TranslatableComponent{key='"
         + this.key
         + '\''
         + ", args="
         + Arrays.toString(this.args)
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getStyle()
         + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
