package net.minecraft.text;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.TargetSelector;
import net.minecraft.server.command.exception.EntityNotFoundException;
import net.minecraft.server.command.source.CommandSource;

public class TextUtils {
   public static Text updateForEntity(CommandSource source, Text text, Entity entity) {
      Object var3 = null;
      if (text instanceof ScoreText) {
         ScoreText var4 = (ScoreText)text;
         String var5 = var4.getOwner();
         if (TargetSelector.isValid(var5)) {
            List var6 = TargetSelector.select(source, var5, Entity.class);
            if (var6.size() != 1) {
               throw new EntityNotFoundException();
            }

            var5 = ((Entity)var6.get(0)).getName();
         }

         var3 = entity != null && var5.equals("*") ? new ScoreText(entity.getName(), var4.getObjective()) : new ScoreText(var5, var4.getObjective());
         ((ScoreText)var3).setValue(var4.getString());
      } else if (text instanceof SelectorText) {
         String var8 = ((SelectorText)text).getPattern();
         var3 = TargetSelector.getSelectionAsText(source, var8);
         if (var3 == null) {
            var3 = new LiteralText("");
         }
      } else if (text instanceof LiteralText) {
         var3 = new LiteralText(((LiteralText)text).getRawString());
      } else {
         if (!(text instanceof TranslatableText)) {
            return text;
         }

         Object[] var9 = ((TranslatableText)text).getArgs();

         for(int var11 = 0; var11 < var9.length; ++var11) {
            Object var13 = var9[var11];
            if (var13 instanceof Text) {
               var9[var11] = updateForEntity(source, (Text)var13, entity);
            }
         }

         var3 = new TranslatableText(((TranslatableText)text).getKey(), var9);
      }

      Style var10 = text.getStyle();
      if (var10 != null) {
         ((Text)var3).setStyle(var10.deepCopy());
      }

      for(Text var14 : text.getSiblings()) {
         ((Text)var3).append(updateForEntity(source, var14, entity));
      }

      return (Text)var3;
   }
}
