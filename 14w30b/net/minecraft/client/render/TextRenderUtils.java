package net.minecraft.client.render;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextRenderUtils {
   public static String prepareText(String text, boolean allowFormatting) {
      return !allowFormatting && !MinecraftClient.getInstance().options.chatColors ? Formatting.strip(text) : text;
   }

   public static List wrapText(Text text, int width, TextRenderer textRender, boolean stripLeadingSpaces, boolean allowFormatting) {
      int var5 = 0;
      LiteralText var6 = new LiteralText("");
      ArrayList var7 = Lists.newArrayList();
      ArrayList var8 = Lists.newArrayList(text);

      for(int var9 = 0; var9 < var8.size(); ++var9) {
         Text var10 = (Text)var8.get(var9);
         String var11 = var10.getString();
         boolean var12 = false;
         if (var11.contains("\n")) {
            int var13 = var11.indexOf(10);
            String var14 = var11.substring(var13 + 1);
            var11 = var11.substring(0, var13 + 1);
            LiteralText var15 = new LiteralText(var14);
            var15.setStyle(var10.getStyle().deepCopy());
            var8.add(var9 + 1, var15);
            var12 = true;
         }

         String var21 = prepareText(var10.getStyle().asString() + var11, allowFormatting);
         String var22 = var21.endsWith("\n") ? var21.substring(0, var21.length() - 1) : var21;
         int var23 = textRender.getStringWidth(var22);
         LiteralText var16 = new LiteralText(var22);
         var16.setStyle(var10.getStyle().deepCopy());
         if (var5 + var23 > width) {
            String var17 = textRender.trimToWidth(var21, width - var5, false);
            String var18 = var17.length() < var21.length() ? var21.substring(var17.length()) : null;
            if (var18 != null && var18.length() > 0) {
               int var19 = var17.lastIndexOf(" ");
               if (var19 >= 0 && textRender.getStringWidth(var21.substring(0, var19)) > 0) {
                  var17 = var21.substring(0, var19);
                  if (stripLeadingSpaces) {
                     ++var19;
                  }

                  var18 = var21.substring(var19);
               } else if (var5 > 0 && !var21.contains(" ")) {
                  var17 = "";
                  var18 = var21;
               }

               LiteralText var20 = new LiteralText(var18);
               var20.setStyle(var10.getStyle().deepCopy());
               var8.add(var9 + 1, var20);
            }

            var23 = textRender.getStringWidth(var17);
            var16 = new LiteralText(var17);
            var16.setStyle(var10.getStyle().deepCopy());
            var12 = true;
         }

         if (var5 + var23 <= width) {
            var5 += var23;
            var6.append(var16);
         } else {
            var12 = true;
         }

         if (var12) {
            var7.add(var6);
            var5 = 0;
            var6 = new LiteralText("");
         }
      }

      var7.add(var6);
      return var7;
   }
}
