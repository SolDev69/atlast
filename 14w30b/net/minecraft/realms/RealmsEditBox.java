package net.minecraft.realms;

import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class RealmsEditBox {
   public static final int BACKWARDS = -1;
   public static final int FORWARDS = 1;
   private static final int CURSOR_INSERT_WIDTH = 1;
   private static final int CURSOR_INSERT_COLOR = -3092272;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   private final TextRenderer font;
   private final int f_04hbylodn;
   private final int f_77ycnoyhm;
   private final int width;
   private final int height;
   private String value = "";
   private int maxLength = 32;
   private int frame;
   private boolean bordered = true;
   private boolean canLoseFocus = true;
   private boolean inFocus;
   private boolean isEditable = true;
   private int displayPos;
   private int cursorPos;
   private int highlightPos;
   private int textColor = 14737632;
   private int textColorUneditable = 7368816;
   private boolean visible = true;

   public RealmsEditBox(int i, int j, int k, int l) {
      this(MinecraftClient.getInstance().textRenderer, i, j, k, l);
   }

   public RealmsEditBox(TextRenderer c_30krpvtyk, int i, int j, int k, int l) {
      this.font = c_30krpvtyk;
      this.f_04hbylodn = i;
      this.f_77ycnoyhm = j;
      this.width = k;
      this.height = l;
   }

   public void tick() {
      ++this.frame;
   }

   public void setValue(String string) {
      if (string.length() > this.maxLength) {
         this.value = string.substring(0, this.maxLength);
      } else {
         this.value = string;
      }

      this.moveCursorToEnd();
   }

   public String getValue() {
      return this.value;
   }

   public String getHighlighted() {
      int var1 = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int var2 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      return this.value.substring(var1, var2);
   }

   public void insertText(String string) {
      String var2 = "";
      String var3 = SharedConstants.stripInvalidChars(string);
      int var4 = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
      int var5 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
      int var6 = this.maxLength - this.value.length() - (var4 - this.highlightPos);
      int var7 = 0;
      if (this.value.length() > 0) {
         var2 = var2 + this.value.substring(0, var4);
      }

      if (var6 < var3.length()) {
         var2 = var2 + var3.substring(0, var6);
         var7 = var6;
      } else {
         var2 = var2 + var3;
         var7 = var3.length();
      }

      if (this.value.length() > 0 && var5 < this.value.length()) {
         var2 = var2 + this.value.substring(var5);
      }

      this.value = var2;
      this.moveCursor(var4 - this.highlightPos + var7);
   }

   public void deleteWords(int i) {
      if (this.value.length() != 0) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            this.deleteChars(this.getWordPosition(i) - this.cursorPos);
         }
      }
   }

   public void deleteChars(int i) {
      if (this.value.length() != 0) {
         if (this.highlightPos != this.cursorPos) {
            this.insertText("");
         } else {
            boolean var2 = i < 0;
            int var3 = var2 ? this.cursorPos + i : this.cursorPos;
            int var4 = var2 ? this.cursorPos : this.cursorPos + i;
            String var5 = "";
            if (var3 >= 0) {
               var5 = this.value.substring(0, var3);
            }

            if (var4 < this.value.length()) {
               var5 = var5 + this.value.substring(var4);
            }

            this.value = var5;
            if (var2) {
               this.moveCursor(i);
            }
         }
      }
   }

   public int getWordPosition(int i) {
      return this.getWordPosition(i, this.getCursorPosition());
   }

   public int getWordPosition(int i, int j) {
      return this.getWordPosition(i, this.getCursorPosition(), true);
   }

   public int getWordPosition(int i, int j, boolean bl) {
      int var4 = j;
      boolean var5 = i < 0;
      int var6 = Math.abs(i);

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!var5) {
            int var8 = this.value.length();
            var4 = this.value.indexOf(32, var4);
            if (var4 == -1) {
               var4 = var8;
            } else {
               while(bl && var4 < var8 && this.value.charAt(var4) == ' ') {
                  ++var4;
               }
            }
         } else {
            while(bl && var4 > 0 && this.value.charAt(var4 - 1) == ' ') {
               --var4;
            }

            while(var4 > 0 && this.value.charAt(var4 - 1) != ' ') {
               --var4;
            }
         }
      }

      return var4;
   }

   public void moveCursor(int i) {
      this.moveCursorTo(this.highlightPos + i);
   }

   public void moveCursorTo(int i) {
      this.cursorPos = i;
      int var2 = this.value.length();
      if (this.cursorPos < 0) {
         this.cursorPos = 0;
      }

      if (this.cursorPos > var2) {
         this.cursorPos = var2;
      }

      this.setHighlightPos(this.cursorPos);
   }

   public void moveCursorToStart() {
      this.moveCursorTo(0);
   }

   public void moveCursorToEnd() {
      this.moveCursorTo(this.value.length());
   }

   public boolean keyPressed(char c, int i) {
      if (!this.inFocus) {
         return false;
      } else {
         switch(c) {
            case '\u0001':
               this.moveCursorToEnd();
               this.setHighlightPos(0);
               return true;
            case '\u0003':
               Screen.setClipboard(this.getHighlighted());
               return true;
            case '\u0016':
               if (this.isEditable) {
                  this.insertText(Screen.getClipboard());
               }

               return true;
            case '\u0018':
               Screen.setClipboard(this.getHighlighted());
               if (this.isEditable) {
                  this.insertText("");
               }

               return true;
            default:
               switch(i) {
                  case 14:
                     if (Screen.isControlDown()) {
                        if (this.isEditable) {
                           this.deleteWords(-1);
                        }
                     } else if (this.isEditable) {
                        this.deleteChars(-1);
                     }

                     return true;
                  case 199:
                     if (Screen.isShiftDown()) {
                        this.setHighlightPos(0);
                     } else {
                        this.moveCursorToStart();
                     }

                     return true;
                  case 203:
                     if (Screen.isShiftDown()) {
                        if (Screen.isControlDown()) {
                           this.setHighlightPos(this.getWordPosition(-1, this.getHighlightPos()));
                        } else {
                           this.setHighlightPos(this.getHighlightPos() - 1);
                        }
                     } else if (Screen.isControlDown()) {
                        this.moveCursorTo(this.getWordPosition(-1));
                     } else {
                        this.moveCursor(-1);
                     }

                     return true;
                  case 205:
                     if (Screen.isShiftDown()) {
                        if (Screen.isControlDown()) {
                           this.setHighlightPos(this.getWordPosition(1, this.getHighlightPos()));
                        } else {
                           this.setHighlightPos(this.getHighlightPos() + 1);
                        }
                     } else if (Screen.isControlDown()) {
                        this.moveCursorTo(this.getWordPosition(1));
                     } else {
                        this.moveCursor(1);
                     }

                     return true;
                  case 207:
                     if (Screen.isShiftDown()) {
                        this.setHighlightPos(this.value.length());
                     } else {
                        this.moveCursorToEnd();
                     }

                     return true;
                  case 211:
                     if (Screen.isControlDown()) {
                        if (this.isEditable) {
                           this.deleteWords(1);
                        }
                     } else if (this.isEditable) {
                        this.deleteChars(1);
                     }

                     return true;
                  default:
                     if (SharedConstants.isValidChatChar(c)) {
                        if (this.isEditable) {
                           this.insertText(Character.toString(c));
                        }

                        return true;
                     } else {
                        return false;
                     }
               }
         }
      }
   }

   public void mouseClicked(int i, int j, int k) {
      boolean var4 = i >= this.f_04hbylodn && i < this.f_04hbylodn + this.width && j >= this.f_77ycnoyhm && j < this.f_77ycnoyhm + this.height;
      if (this.canLoseFocus) {
         this.setFocus(var4);
      }

      if (this.inFocus && k == 0) {
         int var5 = i - this.f_04hbylodn;
         if (this.bordered) {
            var5 -= 4;
         }

         String var6 = this.font.trimToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         this.moveCursorTo(this.font.trimToWidth(var6, var5).length() + this.displayPos);
      }
   }

   public void render() {
      if (this.isVisible()) {
         if (this.isBordered()) {
            GuiElement.fill(this.f_04hbylodn - 1, this.f_77ycnoyhm - 1, this.f_04hbylodn + this.width + 1, this.f_77ycnoyhm + this.height + 1, -6250336);
            GuiElement.fill(this.f_04hbylodn, this.f_77ycnoyhm, this.f_04hbylodn + this.width, this.f_77ycnoyhm + this.height, -16777216);
         }

         int var1 = this.isEditable ? this.textColor : this.textColorUneditable;
         int var2 = this.cursorPos - this.displayPos;
         int var3 = this.highlightPos - this.displayPos;
         String var4 = this.font.trimToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
         boolean var5 = var2 >= 0 && var2 <= var4.length();
         boolean var6 = this.inFocus && this.frame / 6 % 2 == 0 && var5;
         int var7 = this.bordered ? this.f_04hbylodn + 4 : this.f_04hbylodn;
         int var8 = this.bordered ? this.f_77ycnoyhm + (this.height - 8) / 2 : this.f_77ycnoyhm;
         int var9 = var7;
         if (var3 > var4.length()) {
            var3 = var4.length();
         }

         if (var4.length() > 0) {
            String var10 = var5 ? var4.substring(0, var2) : var4;
            var9 = this.font.drawWithShadow(var10, (float)var7, (float)var8, var1);
         }

         boolean var14 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
         int var11 = var9;
         if (!var5) {
            var11 = var2 > 0 ? var7 + this.width : var7;
         } else if (var14) {
            var11 = var9 - 1;
            --var9;
         }

         if (var4.length() > 0 && var5 && var2 < var4.length()) {
            var9 = this.font.drawWithShadow(var4.substring(var2), (float)var9, (float)var8, var1);
         }

         if (var6) {
            if (var14) {
               GuiElement.fill(var11, var8 - 1, var11 + 1, var8 + 1 + this.font.fontHeight, -3092272);
            } else {
               this.font.drawWithShadow("_", (float)var11, (float)var8, var1);
            }
         }

         if (var3 != var2) {
            int var12 = var7 + this.font.getStringWidth(var4.substring(0, var3));
            this.renderHighlight(var11, var8 - 1, var12 - 1, var8 + 1 + this.font.fontHeight);
         }
      }
   }

   private void renderHighlight(int i, int j, int k, int l) {
      if (i < k) {
         int var5 = i;
         i = k;
         k = var5;
      }

      if (j < l) {
         int var6 = j;
         j = l;
         l = var6;
      }

      if (k > this.f_04hbylodn + this.width) {
         k = this.f_04hbylodn + this.width;
      }

      if (i > this.f_04hbylodn + this.width) {
         i = this.f_04hbylodn + this.width;
      }

      Tessellator var7 = Tessellator.getInstance();
      GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
      GL11.glDisable(3553);
      GL11.glEnable(3058);
      GL11.glLogicOp(5387);
      var7.getBufferBuilder().start();
      var7.getBufferBuilder().vertex((double)i, (double)l, 0.0);
      var7.getBufferBuilder().vertex((double)k, (double)l, 0.0);
      var7.getBufferBuilder().vertex((double)k, (double)j, 0.0);
      var7.getBufferBuilder().vertex((double)i, (double)j, 0.0);
      var7.end();
      GL11.glDisable(3058);
      GL11.glEnable(3553);
   }

   public void setMaxLength(int i) {
      this.maxLength = i;
      if (this.value.length() > i) {
         this.value = this.value.substring(0, i);
      }
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public int getCursorPosition() {
      return this.cursorPos;
   }

   public boolean isBordered() {
      return this.bordered;
   }

   public void setBordered(boolean bl) {
      this.bordered = bl;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public void setTextColor(int i) {
      this.textColor = i;
   }

   public int getTextColorUneditable() {
      return this.textColorUneditable;
   }

   public void setTextColorUneditable(int i) {
      this.textColorUneditable = i;
   }

   public void setFocus(boolean bl) {
      if (bl && !this.inFocus) {
         this.frame = 0;
      }

      this.inFocus = bl;
   }

   public boolean isFocused() {
      return this.inFocus;
   }

   public boolean isIsEditable() {
      return this.isEditable;
   }

   public void setIsEditable(boolean bl) {
      this.isEditable = bl;
   }

   public int getHighlightPos() {
      return this.highlightPos;
   }

   public int getInnerWidth() {
      return this.isBordered() ? this.width - 8 : this.width;
   }

   public void setHighlightPos(int i) {
      int var2 = this.value.length();
      if (i > var2) {
         i = var2;
      }

      if (i < 0) {
         i = 0;
      }

      this.highlightPos = i;
      if (this.font != null) {
         if (this.displayPos > var2) {
            this.displayPos = var2;
         }

         int var3 = this.getInnerWidth();
         String var4 = this.font.trimToWidth(this.value.substring(this.displayPos), var3);
         int var5 = var4.length() + this.displayPos;
         if (i == this.displayPos) {
            this.displayPos -= this.font.trimToWidth(this.value, var3, true).length();
         }

         if (i > var5) {
            this.displayPos += i - var5;
         } else if (i <= this.displayPos) {
            this.displayPos -= this.displayPos - i;
         }

         if (this.displayPos < 0) {
            this.displayPos = 0;
         }

         if (this.displayPos > var2) {
            this.displayPos = var2;
         }
      }
   }

   public boolean isCanLoseFocus() {
      return this.canLoseFocus;
   }

   public void setCanLoseFocus(boolean bl) {
      this.canLoseFocus = bl;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean bl) {
      this.visible = bl;
   }
}
