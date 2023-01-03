package net.minecraft.client.render;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class TextRenderer implements ResourceReloadListener {
   private static final Identifier[] PAGES = new Identifier[256];
   private int[] specialCharacterWidth = new int[256];
   public int fontHeight = 9;
   public Random random = new Random();
   private byte[] glyphSizes = new byte[65536];
   private int[] colors = new int[32];
   private final Identifier fontTexture;
   private final TextureManager textureManager;
   private float xPos;
   private float zPos;
   private boolean unicode;
   private boolean rightToLeft;
   private float r;
   private float g;
   private float b;
   private float a;
   private int color;
   private boolean obfuscated;
   private boolean bold;
   private boolean reset;
   private boolean underline;
   private boolean strikethrough;

   public TextRenderer(GameOptions options, Identifier fontTexture, TextureManager textureManager, boolean unicode) {
      this.fontTexture = fontTexture;
      this.textureManager = textureManager;
      this.unicode = unicode;
      textureManager.bind(this.fontTexture);

      for(int var5 = 0; var5 < 32; ++var5) {
         int var6 = (var5 >> 3 & 1) * 85;
         int var7 = (var5 >> 2 & 1) * 170 + var6;
         int var8 = (var5 >> 1 & 1) * 170 + var6;
         int var9 = (var5 >> 0 & 1) * 170 + var6;
         if (var5 == 6) {
            var7 += 85;
         }

         if (options.anaglyph) {
            int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
            int var11 = (var7 * 30 + var8 * 70) / 100;
            int var12 = (var7 * 30 + var9 * 70) / 100;
            var7 = var10;
            var8 = var11;
            var9 = var12;
         }

         if (var5 >= 16) {
            var7 /= 4;
            var8 /= 4;
            var9 /= 4;
         }

         this.colors[var5] = (var7 & 0xFF) << 16 | (var8 & 0xFF) << 8 | var9 & 0xFF;
      }

      this.getGlyphSizes();
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      this.init();
   }

   private void init() {
      BufferedImage var1;
      try {
         var1 = TextureUtil.readImage(MinecraftClient.getInstance().getResourceManager().getResource(this.fontTexture).asStream());
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }

      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      int[] var4 = new int[var2 * var3];
      var1.getRGB(0, 0, var2, var3, var4, 0, var2);
      int var5 = var3 / 16;
      int var6 = var2 / 16;
      byte var7 = 1;
      float var8 = 8.0F / (float)var6;

      for(int var9 = 0; var9 < 256; ++var9) {
         int var10 = var9 % 16;
         int var11 = var9 / 16;
         if (var9 == 32) {
            this.specialCharacterWidth[var9] = 3 + var7;
         }

         int var12;
         for(var12 = var6 - 1; var12 >= 0; --var12) {
            int var13 = var10 * var6 + var12;
            boolean var14 = true;

            for(int var15 = 0; var15 < var5 && var14; ++var15) {
               int var16 = (var11 * var6 + var15) * var2;
               if ((var4[var13 + var16] >> 24 & 0xFF) != 0) {
                  var14 = false;
               }
            }

            if (!var14) {
               break;
            }
         }

         this.specialCharacterWidth[var9] = (int)(0.5 + (double)((float)(++var12) * var8)) + var7;
      }
   }

   private void getGlyphSizes() {
      InputStream var1 = null;

      try {
         var1 = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("font/glyph_sizes.bin")).asStream();
         var1.read(this.glyphSizes);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      } finally {
         IOUtils.closeQuietly(var1);
      }
   }

   private float renderText(int formattingChar, char unicodeChar, boolean reset) {
      if (unicodeChar == ' ') {
         return 4.0F;
      } else {
         return "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .indexOf(unicodeChar)
                  != -1
               && !this.unicode
            ? this.renderBasicText(formattingChar, reset)
            : this.renderUnicodeText(unicodeChar, reset);
      }
   }

   private float renderBasicText(int formattingChar, boolean reset) {
      float var3 = (float)(formattingChar % 16 * 8);
      float var4 = (float)(formattingChar / 16 * 8);
      float var5 = reset ? 1.0F : 0.0F;
      this.textureManager.bind(this.fontTexture);
      float var6 = (float)this.specialCharacterWidth[formattingChar] - 0.01F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(var3 / 128.0F, var4 / 128.0F);
      GL11.glVertex3f(this.xPos + var5, this.zPos, 0.0F);
      GL11.glTexCoord2f(var3 / 128.0F, (var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.xPos - var5, this.zPos + 7.99F, 0.0F);
      GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, var4 / 128.0F);
      GL11.glVertex3f(this.xPos + var6 - 1.0F + var5, this.zPos, 0.0F);
      GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, (var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.xPos + var6 - 1.0F - var5, this.zPos + 7.99F, 0.0F);
      GL11.glEnd();
      return (float)this.specialCharacterWidth[formattingChar];
   }

   private Identifier getFontPage(int page) {
      if (PAGES[page] == null) {
         PAGES[page] = new Identifier(String.format("textures/font/unicode_page_%02x.png", page));
      }

      return PAGES[page];
   }

   private void bindFontPageTexture(int page) {
      this.textureManager.bind(this.getFontPage(page));
   }

   private float renderUnicodeText(char unicodeCharacter, boolean reset) {
      if (this.glyphSizes[unicodeCharacter] == 0) {
         return 0.0F;
      } else {
         int var3 = unicodeCharacter / 256;
         this.bindFontPageTexture(var3);
         int var4 = this.glyphSizes[unicodeCharacter] >>> 4;
         int var5 = this.glyphSizes[unicodeCharacter] & 15;
         float var6 = (float)var4;
         float var7 = (float)(var5 + 1);
         float var8 = (float)(unicodeCharacter % 16 * 16) + var6;
         float var9 = (float)((unicodeCharacter & 255) / 16 * 16);
         float var10 = var7 - var6 - 0.02F;
         float var11 = reset ? 1.0F : 0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(var8 / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.xPos + var11, this.zPos, 0.0F);
         GL11.glTexCoord2f(var8 / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.xPos - var11, this.zPos + 7.99F, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.xPos + var10 / 2.0F + var11, this.zPos, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.xPos + var10 / 2.0F - var11, this.zPos + 7.99F, 0.0F);
         GL11.glEnd();
         return (var7 - var6) / 2.0F + 1.0F;
      }
   }

   public int drawWithShadow(String text, float x, float y, int color) {
      return this.draw(text, x, y, color, true);
   }

   public int drawWithoutShadow(String text, int x, int y, int color) {
      return this.draw(text, (float)x, (float)y, color, false);
   }

   public int draw(String text, float x, float y, int color, boolean shadow) {
      GlStateManager.enableAlphaTest();
      this.reset();
      int var7;
      if (shadow) {
         var7 = this.drawLayer(text, x + 1.0F, y + 1.0F, color, true);
         var7 = Math.max(var7, this.drawLayer(text, x, y, color, false));
      } else {
         var7 = this.drawLayer(text, x, y, color, false);
      }

      return var7;
   }

   private String mirror(String text) {
      try {
         Bidi var2 = new Bidi(new ArabicShaping(8).shape(text), 127);
         var2.setReorderingMode(0);
         return var2.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return text;
      }
   }

   private void reset() {
      this.obfuscated = false;
      this.bold = false;
      this.reset = false;
      this.underline = false;
      this.strikethrough = false;
   }

   private void drawLine(String text, boolean hasShadow) {
      for(int var3 = 0; var3 < text.length(); ++var3) {
         char var4 = text.charAt(var3);
         if (var4 == 167 && var3 + 1 < text.length()) {
            int var12 = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(var3 + 1));
            if (var12 < 16) {
               this.obfuscated = false;
               this.bold = false;
               this.strikethrough = false;
               this.underline = false;
               this.reset = false;
               if (var12 < 0 || var12 > 15) {
                  var12 = 15;
               }

               if (hasShadow) {
                  var12 += 16;
               }

               int var14 = this.colors[var12];
               this.color = var14;
               GlStateManager.color4f((float)(var14 >> 16) / 255.0F, (float)(var14 >> 8 & 0xFF) / 255.0F, (float)(var14 & 0xFF) / 255.0F, this.a);
            } else if (var12 == 16) {
               this.obfuscated = true;
            } else if (var12 == 17) {
               this.bold = true;
            } else if (var12 == 18) {
               this.strikethrough = true;
            } else if (var12 == 19) {
               this.underline = true;
            } else if (var12 == 20) {
               this.reset = true;
            } else if (var12 == 21) {
               this.obfuscated = false;
               this.bold = false;
               this.strikethrough = false;
               this.underline = false;
               this.reset = false;
               GlStateManager.color4f(this.r, this.g, this.b, this.a);
            }

            ++var3;
         } else {
            int var5 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(var4);
            if (this.obfuscated && var5 != -1) {
               int var6;
               do {
                  var6 = this.random.nextInt(this.specialCharacterWidth.length);
               } while(this.specialCharacterWidth[var5] != this.specialCharacterWidth[var6]);

               var5 = var6;
            }

            float var13 = this.unicode ? 0.5F : 1.0F;
            boolean var7 = (var4 == 0 || var5 == -1 || this.unicode) && hasShadow;
            if (var7) {
               this.xPos -= var13;
               this.zPos -= var13;
            }

            float var8 = this.renderText(var5, var4, this.reset);
            if (var7) {
               this.xPos += var13;
               this.zPos += var13;
            }

            if (this.bold) {
               this.xPos += var13;
               if (var7) {
                  this.xPos -= var13;
                  this.zPos -= var13;
               }

               this.renderText(var5, var4, this.reset);
               this.xPos -= var13;
               if (var7) {
                  this.xPos += var13;
                  this.zPos += var13;
               }

               ++var8;
            }

            if (this.strikethrough) {
               Tessellator var9 = Tessellator.getInstance();
               BufferBuilder var10 = var9.getBufferBuilder();
               GlStateManager.disableTexture();
               var10.start();
               var10.vertex((double)this.xPos, (double)(this.zPos + (float)(this.fontHeight / 2)), 0.0);
               var10.vertex((double)(this.xPos + var8), (double)(this.zPos + (float)(this.fontHeight / 2)), 0.0);
               var10.vertex((double)(this.xPos + var8), (double)(this.zPos + (float)(this.fontHeight / 2) - 1.0F), 0.0);
               var10.vertex((double)this.xPos, (double)(this.zPos + (float)(this.fontHeight / 2) - 1.0F), 0.0);
               var9.end();
               GlStateManager.enableTexture();
            }

            if (this.underline) {
               Tessellator var15 = Tessellator.getInstance();
               BufferBuilder var16 = var15.getBufferBuilder();
               GlStateManager.disableTexture();
               var16.start();
               int var11 = this.underline ? -1 : 0;
               var16.vertex((double)(this.xPos + (float)var11), (double)(this.zPos + (float)this.fontHeight), 0.0);
               var16.vertex((double)(this.xPos + var8), (double)(this.zPos + (float)this.fontHeight), 0.0);
               var16.vertex((double)(this.xPos + var8), (double)(this.zPos + (float)this.fontHeight - 1.0F), 0.0);
               var16.vertex((double)(this.xPos + (float)var11), (double)(this.zPos + (float)this.fontHeight - 1.0F), 0.0);
               var15.end();
               GlStateManager.enableTexture();
            }

            this.xPos += (float)((int)var8);
         }
      }
   }

   private int drawLayer(String line, int x, int y, int maxWidth, int color, boolean colored) {
      if (this.rightToLeft) {
         int var7 = this.getStringWidth(this.mirror(line));
         x = x + maxWidth - var7;
      }

      return this.drawLayer(line, (float)x, (float)y, color, colored);
   }

   private int drawLayer(String text, float x, float y, int color, boolean colored) {
      if (text == null) {
         return 0;
      } else {
         if (this.rightToLeft) {
            text = this.mirror(text);
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (colored) {
            color = (color & 16579836) >> 2 | color & 0xFF000000;
         }

         this.r = (float)(color >> 16 & 0xFF) / 255.0F;
         this.g = (float)(color >> 8 & 0xFF) / 255.0F;
         this.b = (float)(color & 0xFF) / 255.0F;
         this.a = (float)(color >> 24 & 0xFF) / 255.0F;
         GlStateManager.color4f(this.r, this.g, this.b, this.a);
         this.xPos = x;
         this.zPos = y;
         this.drawLine(text, colored);
         return (int)this.xPos;
      }
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         int var2 = 0;
         boolean var3 = false;

         for(int var4 = 0; var4 < text.length(); ++var4) {
            char var5 = text.charAt(var4);
            int var6 = this.getSpecialCharacterWidth(var5);
            if (var6 < 0 && var4 < text.length() - 1) {
               var5 = text.charAt(++var4);
               if (var5 == 'l' || var5 == 'L') {
                  var3 = true;
               } else if (var5 == 'r' || var5 == 'R') {
                  var3 = false;
               }

               var6 = 0;
            }

            var2 += var6;
            if (var3 && var6 > 0) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public int getSpecialCharacterWidth(char character) {
      if (character == 167) {
         return -1;
      } else if (character == ' ') {
         return 4;
      } else {
         int var2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(character);
         if (character > 0 && var2 != -1 && !this.unicode) {
            return this.specialCharacterWidth[var2];
         } else if (this.glyphSizes[character] != 0) {
            int var3 = this.glyphSizes[character] >>> 4;
            int var4 = this.glyphSizes[character] & 15;
            if (var4 > 7) {
               var4 = 15;
               var3 = 0;
            }

            ++var4;
            return (var4 - var3) / 2 + 1;
         } else {
            return 0;
         }
      }
   }

   public String trimToWidth(String text, int width) {
      return this.trimToWidth(text, width, false);
   }

   public String trimToWidth(String text, int width, boolean shouldInverse) {
      StringBuilder var4 = new StringBuilder();
      int var5 = 0;
      int var6 = shouldInverse ? text.length() - 1 : 0;
      int var7 = shouldInverse ? -1 : 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var6; var10 >= 0 && var10 < text.length() && var5 < width; var10 += var7) {
         char var11 = text.charAt(var10);
         int var12 = this.getSpecialCharacterWidth(var11);
         if (var8) {
            var8 = false;
            if (var11 == 'l' || var11 == 'L') {
               var9 = true;
            } else if (var11 == 'r' || var11 == 'R') {
               var9 = false;
            }
         } else if (var12 < 0) {
            var8 = true;
         } else {
            var5 += var12;
            if (var9) {
               ++var5;
            }
         }

         if (var5 > width) {
            break;
         }

         if (shouldInverse) {
            var4.insert(0, var11);
         } else {
            var4.append(var11);
         }
      }

      return var4.toString();
   }

   private String trimEndNewlines(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public void drawTrimmed(String text, int x, int y, int maxWidth, int color) {
      this.reset();
      this.color = color;
      text = this.trimEndNewlines(text);
      this.drawText(text, x, y, maxWidth, false);
   }

   private void drawText(String text, int x, int y, int maxWidth, boolean color) {
      for(String var8 : this.wrapLines(text, maxWidth)) {
         this.drawLayer(var8, x, y, maxWidth, this.color, color);
         y += this.fontHeight;
      }
   }

   public int getTextBoxHeight(String text, int lineLength) {
      return this.fontHeight * this.wrapLines(text, lineLength).size();
   }

   public void setUnicode(boolean unicode) {
      this.unicode = unicode;
   }

   public boolean getUnicode() {
      return this.unicode;
   }

   public void setRightToLeft(boolean rightToLeft) {
      this.rightToLeft = rightToLeft;
   }

   public List wrapLines(String text, int lineLength) {
      return Arrays.asList(this.wrapStringToWidth(text, lineLength).split("\n"));
   }

   String wrapStringToWidth(String text, int width) {
      int var3 = this.getCharacterCountForWidth(text, width);
      if (text.length() <= var3) {
         return text;
      } else {
         String var4 = text.substring(0, var3);
         char var5 = text.charAt(var3);
         boolean var6 = var5 == ' ' || var5 == '\n';
         String var7 = isolateFormatting(var4) + text.substring(var3 + (var6 ? 1 : 0));
         return var4 + "\n" + this.wrapStringToWidth(var7, width);
      }
   }

   private int getCharacterCountForWidth(String text, int offset) {
      int var3 = text.length();
      int var4 = 0;
      int var5 = 0;
      int var6 = -1;

      for(boolean var7 = false; var5 < var3; ++var5) {
         char var8 = text.charAt(var5);
         switch(var8) {
            case '\n':
               --var5;
               break;
            case ' ':
               var6 = var5;
            default:
               var4 += this.getSpecialCharacterWidth(var8);
               if (var7) {
                  ++var4;
               }
               break;
            case '§':
               if (var5 < var3 - 1) {
                  char var9 = text.charAt(++var5);
                  if (var9 == 'l' || var9 == 'L') {
                     var7 = true;
                  } else if (var9 == 'r' || var9 == 'R' || isColorCharacter(var9)) {
                     var7 = false;
                  }
               }
         }

         if (var8 == '\n') {
            var6 = ++var5;
            break;
         }

         if (var4 > offset) {
            break;
         }
      }

      return var5 != var3 && var6 != -1 && var6 < var5 ? var6 : var5;
   }

   private static boolean isColorCharacter(char chr) {
      return chr >= '0' && chr <= '9' || chr >= 'a' && chr <= 'f' || chr >= 'A' && chr <= 'F';
   }

   private static boolean isFormattingCharacter(char chr) {
      return chr >= 'k' && chr <= 'o' || chr >= 'K' && chr <= 'O' || chr == 'r' || chr == 'R';
   }

   public static String isolateFormatting(String text) {
      String var1 = "";
      int var2 = -1;
      int var3 = text.length();

      while((var2 = text.indexOf(167, var2 + 1)) != -1) {
         if (var2 < var3 - 1) {
            char var4 = text.charAt(var2 + 1);
            if (isColorCharacter(var4)) {
               var1 = "§" + var4;
            } else if (isFormattingCharacter(var4)) {
               var1 = var1 + "§" + var4;
            }
         }
      }

      return var1;
   }

   public boolean isRightToLeft() {
      return this.rightToLeft;
   }

   public int getColor(char code) {
      return this.colors["0123456789abcdef".indexOf(code)];
   }
}
