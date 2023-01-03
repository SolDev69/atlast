package net.minecraft.client.gui.widget;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.C_37rnsjynt;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextFieldWidget extends GuiElement {
   private final int f_51uwnaejv;
   private final TextRenderer textRenderer;
   public int x;
   public int y;
   private final int width;
   private final int height;
   private String text = "";
   private int maxLength = 32;
   private int focusedTicks;
   private boolean hasBorder = true;
   private boolean focusUnlocked = true;
   private boolean focused;
   private boolean editable = true;
   private int firstCharacterIndex;
   private int selectionStart;
   private int selectionEnd;
   private int editableColor = 14737632;
   private int uneditableColor = 7368816;
   private boolean visible = true;
   private C_37rnsjynt.C_59lpdyoky f_69nbbwwvu;
   private Predicate f_64aegyakh = Predicates.alwaysTrue();

   public TextFieldWidget(int textRenderer, TextRenderer x, int y, int width, int height, int m) {
      this.f_51uwnaejv = textRenderer;
      this.textRenderer = x;
      this.x = y;
      this.y = width;
      this.width = height;
      this.height = m;
   }

   public void m_30nnadsqo(C_37rnsjynt.C_59lpdyoky c_59lpdyoky) {
      this.f_69nbbwwvu = c_59lpdyoky;
   }

   public void tick() {
      ++this.focusedTicks;
   }

   public void setText(String text) {
      if (this.f_64aegyakh.apply(text)) {
         if (text.length() > this.maxLength) {
            this.text = text.substring(0, this.maxLength);
         } else {
            this.text = text;
         }

         this.setCursorToEnd();
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int var1 = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
      int var2 = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
      return this.text.substring(var1, var2);
   }

   public void m_75zfafuxs(Predicate predicate) {
      this.f_64aegyakh = predicate;
   }

   public void write(String text) {
      String var2 = "";
      String var3 = SharedConstants.stripInvalidChars(text);
      int var4 = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
      int var5 = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
      int var6 = this.maxLength - this.text.length() - (var4 - var5);
      int var7 = 0;
      if (this.text.length() > 0) {
         var2 = var2 + this.text.substring(0, var4);
      }

      if (var6 < var3.length()) {
         var2 = var2 + var3.substring(0, var6);
         var7 = var6;
      } else {
         var2 = var2 + var3;
         var7 = var3.length();
      }

      if (this.text.length() > 0 && var5 < this.text.length()) {
         var2 = var2 + this.text.substring(var5);
      }

      if (this.f_64aegyakh.apply(var2)) {
         this.text = var2;
         this.moveCursor(var4 - this.selectionEnd + var7);
         if (this.f_69nbbwwvu != null) {
            this.f_69nbbwwvu.m_40mldmjxh(this.f_51uwnaejv, this.text);
         }
      }
   }

   public void eraseWords(int wordOffset) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.selectionStart) {
            this.write("");
         } else {
            this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
         }
      }
   }

   public void eraseCharacters(int characterOffset) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.selectionStart) {
            this.write("");
         } else {
            boolean var2 = characterOffset < 0;
            int var3 = var2 ? this.selectionStart + characterOffset : this.selectionStart;
            int var4 = var2 ? this.selectionStart : this.selectionStart + characterOffset;
            String var5 = "";
            if (var3 >= 0) {
               var5 = this.text.substring(0, var3);
            }

            if (var4 < this.text.length()) {
               var5 = var5 + this.text.substring(var4);
            }

            this.text = var5;
            if (var2) {
               this.moveCursor(characterOffset);
            }

            if (this.f_69nbbwwvu != null) {
               this.f_69nbbwwvu.m_40mldmjxh(this.f_51uwnaejv, this.text);
            }
         }
      }
   }

   public int m_80ivbzupk() {
      return this.f_51uwnaejv;
   }

   public int getWordSkipPosition(int wordOffset) {
      return this.getWordSkipPosition(wordOffset, this.getCursor());
   }

   public int getWordSkipPosition(int wordOffset, int cursorPosition) {
      return this.getWordSkipPosition(wordOffset, cursorPosition, true);
   }

   public int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
      int var4 = cursorPosition;
      boolean var5 = wordOffset < 0;
      int var6 = Math.abs(wordOffset);

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!var5) {
            int var8 = this.text.length();
            var4 = this.text.indexOf(32, var4);
            if (var4 == -1) {
               var4 = var8;
            } else {
               while(skipOverSpaces && var4 < var8 && this.text.charAt(var4) == ' ') {
                  ++var4;
               }
            }
         } else {
            while(skipOverSpaces && var4 > 0 && this.text.charAt(var4 - 1) == ' ') {
               --var4;
            }

            while(var4 > 0 && this.text.charAt(var4 - 1) != ' ') {
               --var4;
            }
         }
      }

      return var4;
   }

   public void moveCursor(int offset) {
      this.setCursor(this.selectionEnd + offset);
   }

   public void setCursor(int cursor) {
      this.selectionStart = cursor;
      int var2 = this.text.length();
      this.selectionStart = MathHelper.clamp(this.selectionStart, 0, var2);
      this.setSelectionEnd(this.selectionStart);
   }

   public void setCursorToStart() {
      this.setCursor(0);
   }

   public void setCursorToEnd() {
      this.setCursor(this.text.length());
   }

   public boolean keyPressed(char character, int code) {
      if (!this.focused) {
         return false;
      } else {
         switch(character) {
            case '\u0001':
               this.setCursorToEnd();
               this.setSelectionEnd(0);
               return true;
            case '\u0003':
               Screen.setClipboard(this.getSelectedText());
               return true;
            case '\u0016':
               if (this.editable) {
                  this.write(Screen.getClipboard());
               }

               return true;
            case '\u0018':
               Screen.setClipboard(this.getSelectedText());
               if (this.editable) {
                  this.write("");
               }

               return true;
            default:
               switch(code) {
                  case 14:
                     if (Screen.isControlDown()) {
                        if (this.editable) {
                           this.eraseWords(-1);
                        }
                     } else if (this.editable) {
                        this.eraseCharacters(-1);
                     }

                     return true;
                  case 199:
                     if (Screen.isShiftDown()) {
                        this.setSelectionEnd(0);
                     } else {
                        this.setCursorToStart();
                     }

                     return true;
                  case 203:
                     if (Screen.isShiftDown()) {
                        if (Screen.isControlDown()) {
                           this.setSelectionEnd(this.getWordSkipPosition(-1, this.getSelectionEnd()));
                        } else {
                           this.setSelectionEnd(this.getSelectionEnd() - 1);
                        }
                     } else if (Screen.isControlDown()) {
                        this.setCursor(this.getWordSkipPosition(-1));
                     } else {
                        this.moveCursor(-1);
                     }

                     return true;
                  case 205:
                     if (Screen.isShiftDown()) {
                        if (Screen.isControlDown()) {
                           this.setSelectionEnd(this.getWordSkipPosition(1, this.getSelectionEnd()));
                        } else {
                           this.setSelectionEnd(this.getSelectionEnd() + 1);
                        }
                     } else if (Screen.isControlDown()) {
                        this.setCursor(this.getWordSkipPosition(1));
                     } else {
                        this.moveCursor(1);
                     }

                     return true;
                  case 207:
                     if (Screen.isShiftDown()) {
                        this.setSelectionEnd(this.text.length());
                     } else {
                        this.setCursorToEnd();
                     }

                     return true;
                  case 211:
                     if (Screen.isControlDown()) {
                        if (this.editable) {
                           this.eraseWords(1);
                        }
                     } else if (this.editable) {
                        this.eraseCharacters(1);
                     }

                     return true;
                  default:
                     if (SharedConstants.isValidChatChar(character)) {
                        if (this.editable) {
                           this.write(Character.toString(character));
                        }

                        return true;
                     } else {
                        return false;
                     }
               }
         }
      }
   }

   public void mouseClicked(int mouseX, int mouseY, int button) {
      boolean var4 = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
      if (this.focusUnlocked) {
         this.setFocused(var4);
      }

      if (this.focused && var4 && button == 0) {
         int var5 = mouseX - this.x;
         if (this.hasBorder) {
            var5 -= 4;
         }

         String var6 = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
         this.setCursor(this.textRenderer.trimToWidth(var6, var5).length() + this.firstCharacterIndex);
      }
   }

   public void render() {
      if (this.isVisible()) {
         if (this.hasBorder()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int var1 = this.editable ? this.editableColor : this.uneditableColor;
         int var2 = this.selectionStart - this.firstCharacterIndex;
         int var3 = this.selectionEnd - this.firstCharacterIndex;
         String var4 = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
         boolean var5 = var2 >= 0 && var2 <= var4.length();
         boolean var6 = this.focused && this.focusedTicks / 6 % 2 == 0 && var5;
         int var7 = this.hasBorder ? this.x + 4 : this.x;
         int var8 = this.hasBorder ? this.y + (this.height - 8) / 2 : this.y;
         int var9 = var7;
         if (var3 > var4.length()) {
            var3 = var4.length();
         }

         if (var4.length() > 0) {
            String var10 = var5 ? var4.substring(0, var2) : var4;
            var9 = this.textRenderer.drawWithShadow(var10, (float)var7, (float)var8, var1);
         }

         boolean var14 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
         int var11 = var9;
         if (!var5) {
            var11 = var2 > 0 ? var7 + this.width : var7;
         } else if (var14) {
            var11 = var9 - 1;
            --var9;
         }

         if (var4.length() > 0 && var5 && var2 < var4.length()) {
            var9 = this.textRenderer.drawWithShadow(var4.substring(var2), (float)var9, (float)var8, var1);
         }

         if (var6) {
            if (var14) {
               GuiElement.fill(var11, var8 - 1, var11 + 1, var8 + 1 + this.textRenderer.fontHeight, -3092272);
            } else {
               this.textRenderer.drawWithShadow("_", (float)var11, (float)var8, var1);
            }
         }

         if (var3 != var2) {
            int var12 = var7 + this.textRenderer.getStringWidth(var4.substring(0, var3));
            this.renderSelection(var11, var8 - 1, var12 - 1, var8 + 1 + this.textRenderer.fontHeight);
         }
      }
   }

   private void renderSelection(int x1, int y1, int x2, int y2) {
      if (x1 < x2) {
         int var5 = x1;
         x1 = x2;
         x2 = var5;
      }

      if (y1 < y2) {
         int var7 = y1;
         y1 = y2;
         y2 = var7;
      }

      if (x2 > this.x + this.width) {
         x2 = this.x + this.width;
      }

      if (x1 > this.x + this.width) {
         x1 = this.x + this.width;
      }

      Tessellator var8 = Tessellator.getInstance();
      BufferBuilder var6 = var8.getBufferBuilder();
      GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture();
      GlStateManager.enableColorLogicOp();
      GlStateManager.logicOp(5387);
      var6.start();
      var6.vertex((double)x1, (double)y2, 0.0);
      var6.vertex((double)x2, (double)y2, 0.0);
      var6.vertex((double)x2, (double)y1, 0.0);
      var6.vertex((double)x1, (double)y1, 0.0);
      var8.end();
      GlStateManager.disableColorLogicOp();
      GlStateManager.enableTexture();
   }

   public void setMaxLength(int maximumLength) {
      this.maxLength = maximumLength;
      if (this.text.length() > maximumLength) {
         this.text = this.text.substring(0, maximumLength);
      }
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public int getCursor() {
      return this.selectionStart;
   }

   public boolean hasBorder() {
      return this.hasBorder;
   }

   public void setHasBorder(boolean hasBorder) {
      this.hasBorder = hasBorder;
   }

   public void setEditableColor(int color) {
      this.editableColor = color;
   }

   public void setUneditableColor(int color) {
      this.uneditableColor = color;
   }

   public void setFocused(boolean focused) {
      if (focused && !this.focused) {
         this.focusedTicks = 0;
      }

      this.focused = focused;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public int getSelectionEnd() {
      return this.selectionEnd;
   }

   public int getInnerWidth() {
      return this.hasBorder() ? this.width - 8 : this.width;
   }

   public void setSelectionEnd(int index) {
      int var2 = this.text.length();
      if (index > var2) {
         index = var2;
      }

      if (index < 0) {
         index = 0;
      }

      this.selectionEnd = index;
      if (this.textRenderer != null) {
         if (this.firstCharacterIndex > var2) {
            this.firstCharacterIndex = var2;
         }

         int var3 = this.getInnerWidth();
         String var4 = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), var3);
         int var5 = var4.length() + this.firstCharacterIndex;
         if (index == this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.textRenderer.trimToWidth(this.text, var3, true).length();
         }

         if (index > var5) {
            this.firstCharacterIndex += index - var5;
         } else if (index <= this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.firstCharacterIndex - index;
         }

         this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, var2);
      }
   }

   public void setFocusUnlocked(boolean focusUnlocked) {
      this.focusUnlocked = focusUnlocked;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }
}
