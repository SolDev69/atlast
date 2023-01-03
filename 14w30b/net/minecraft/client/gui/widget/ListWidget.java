package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Mouse;

@Environment(EnvType.CLIENT)
public abstract class ListWidget {
   protected final MinecraftClient client;
   protected int width;
   private int height;
   protected int yStart;
   protected int yEnd;
   protected int xEnd;
   protected int xStart;
   protected final int entryHeight;
   private int upButtonId;
   private int downButtonId;
   protected int mouseX;
   protected int mouseY;
   protected boolean centerAlongY = true;
   private float mouseYStart = -2.0F;
   private float scrollSpeedMultiplier;
   protected float scrollAmount;
   private int pos = -1;
   private long time;
   protected boolean f_48lnfurqk = true;
   private boolean renderSelectionBox = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean dragging = true;

   public ListWidget(MinecraftClient client, int width, int height, int yStart, int yEnd, int entryHeight) {
      this.client = client;
      this.width = width;
      this.height = height;
      this.yStart = yStart;
      this.yEnd = yEnd;
      this.entryHeight = entryHeight;
      this.xStart = 0;
      this.xEnd = width;
   }

   public void updateBounds(int right, int height, int top, int bottom) {
      this.width = right;
      this.height = height;
      this.yStart = top;
      this.yEnd = bottom;
      this.xStart = 0;
      this.xEnd = right;
   }

   public void setRenderSelection(boolean renderSelection) {
      this.renderSelectionBox = renderSelection;
   }

   protected void setHeader(boolean renderHeader, int headerHeight) {
      this.renderHeader = renderHeader;
      this.headerHeight = headerHeight;
      if (!renderHeader) {
         this.headerHeight = 0;
      }
   }

   protected abstract int getEntriesSize();

   protected abstract void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY);

   protected abstract boolean isEntrySelected(int index);

   protected int getListSize() {
      return this.getEntriesSize() * this.entryHeight + this.headerHeight;
   }

   protected abstract void renderBackground();

   protected void m_68rrjxwti(int i, int j, int k) {
   }

   protected abstract void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX);

   protected void renderHeader(int x, int y, Tessellator bufferBuilder) {
   }

   protected void render(int x, int y) {
   }

   protected void renderDecorations(int mouseX, int mouseY) {
   }

   public int getEntryAt(int x, int y) {
      int var3 = this.xStart + this.width / 2 - this.getRowWidth() / 2;
      int var4 = this.xStart + this.width / 2 + this.getRowWidth() / 2;
      int var5 = y - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
      int var6 = var5 / this.entryHeight;
      return x < this.getScrollbarPosition() && x >= var3 && x <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getEntriesSize() ? var6 : -1;
   }

   public void setScrollButtonIds(int homeButtonId, int endButtonId) {
      this.upButtonId = homeButtonId;
      this.downButtonId = endButtonId;
   }

   private void capScrolling() {
      int var1 = this.getMaxScroll();
      if (var1 < 0) {
         var1 /= 2;
      }

      if (!this.centerAlongY && var1 < 0) {
         var1 = 0;
      }

      this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, (float)var1);
   }

   public int getMaxScroll() {
      return Math.max(0, this.getListSize() - (this.yEnd - this.yStart - 4));
   }

   public int getScrollAmount() {
      return (int)this.scrollAmount;
   }

   public boolean isMouseInList(int mouseY) {
      return mouseY >= this.yStart && mouseY <= this.yEnd && this.mouseX >= this.xStart && this.mouseX <= this.xEnd;
   }

   public void scroll(int amount) {
      this.scrollAmount += (float)amount;
      this.capScrolling();
      this.mouseYStart = -2.0F;
   }

   public void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == this.upButtonId) {
            this.scrollAmount -= (float)(this.entryHeight * 2 / 3);
            this.mouseYStart = -2.0F;
            this.capScrolling();
         } else if (buttonWidget.id == this.downButtonId) {
            this.scrollAmount += (float)(this.entryHeight * 2 / 3);
            this.mouseYStart = -2.0F;
            this.capScrolling();
         }
      }
   }

   public void render(int mouseX, int mouseY, float tickDelta) {
      if (this.f_48lnfurqk) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
         this.renderBackground();
         int var4 = this.getScrollbarPosition();
         int var5 = var4 + 6;
         this.capScrolling();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator var6 = Tessellator.getInstance();
         BufferBuilder var7 = var6.getBufferBuilder();
         this.client.getTextureManager().bind(GuiElement.OPTIONS_BACKGROUND);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.start();
         var7.color(2105376);
         var7.vertex(
            (double)this.xStart, (double)this.yEnd, 0.0, (double)((float)this.xStart / var8), (double)((float)(this.yEnd + (int)this.scrollAmount) / var8)
         );
         var7.vertex((double)this.xEnd, (double)this.yEnd, 0.0, (double)((float)this.xEnd / var8), (double)((float)(this.yEnd + (int)this.scrollAmount) / var8));
         var7.vertex(
            (double)this.xEnd, (double)this.yStart, 0.0, (double)((float)this.xEnd / var8), (double)((float)(this.yStart + (int)this.scrollAmount) / var8)
         );
         var7.vertex(
            (double)this.xStart, (double)this.yStart, 0.0, (double)((float)this.xStart / var8), (double)((float)(this.yStart + (int)this.scrollAmount) / var8)
         );
         var6.end();
         int var9 = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
         int var10 = this.yStart + 4 - (int)this.scrollAmount;
         if (this.renderHeader) {
            this.renderHeader(var9, var10, var6);
         }

         this.renderList(var9, var10, mouseX, mouseY);
         GlStateManager.enableDepth();
         byte var11 = 4;
         this.renderHoleBackground(0, this.yStart, 255, 255);
         this.renderHoleBackground(this.yEnd, this.height, 255, 255);
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlphaTest();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture();
         var7.start();
         var7.color(0, 0);
         var7.vertex((double)this.xStart, (double)(this.yStart + var11), 0.0, 0.0, 1.0);
         var7.vertex((double)this.xEnd, (double)(this.yStart + var11), 0.0, 1.0, 1.0);
         var7.color(0, 255);
         var7.vertex((double)this.xEnd, (double)this.yStart, 0.0, 1.0, 0.0);
         var7.vertex((double)this.xStart, (double)this.yStart, 0.0, 0.0, 0.0);
         var6.end();
         var7.start();
         var7.color(0, 255);
         var7.vertex((double)this.xStart, (double)this.yEnd, 0.0, 0.0, 1.0);
         var7.vertex((double)this.xEnd, (double)this.yEnd, 0.0, 1.0, 1.0);
         var7.color(0, 0);
         var7.vertex((double)this.xEnd, (double)(this.yEnd - var11), 0.0, 1.0, 0.0);
         var7.vertex((double)this.xStart, (double)(this.yEnd - var11), 0.0, 0.0, 0.0);
         var6.end();
         int var12 = this.getMaxScroll();
         if (var12 > 0) {
            int var13 = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getListSize();
            var13 = MathHelper.clamp(var13, 32, this.yEnd - this.yStart - 8);
            int var14 = (int)this.scrollAmount * (this.yEnd - this.yStart - var13) / var12 + this.yStart;
            if (var14 < this.yStart) {
               var14 = this.yStart;
            }

            var7.start();
            var7.color(0, 255);
            var7.vertex((double)var4, (double)this.yEnd, 0.0, 0.0, 1.0);
            var7.vertex((double)var5, (double)this.yEnd, 0.0, 1.0, 1.0);
            var7.vertex((double)var5, (double)this.yStart, 0.0, 1.0, 0.0);
            var7.vertex((double)var4, (double)this.yStart, 0.0, 0.0, 0.0);
            var6.end();
            var7.start();
            var7.color(8421504, 255);
            var7.vertex((double)var4, (double)(var14 + var13), 0.0, 0.0, 1.0);
            var7.vertex((double)var5, (double)(var14 + var13), 0.0, 1.0, 1.0);
            var7.vertex((double)var5, (double)var14, 0.0, 1.0, 0.0);
            var7.vertex((double)var4, (double)var14, 0.0, 0.0, 0.0);
            var6.end();
            var7.start();
            var7.color(12632256, 255);
            var7.vertex((double)var4, (double)(var14 + var13 - 1), 0.0, 0.0, 1.0);
            var7.vertex((double)(var5 - 1), (double)(var14 + var13 - 1), 0.0, 1.0, 1.0);
            var7.vertex((double)(var5 - 1), (double)var14, 0.0, 1.0, 0.0);
            var7.vertex((double)var4, (double)var14, 0.0, 0.0, 0.0);
            var6.end();
         }

         this.renderDecorations(mouseX, mouseY);
         GlStateManager.enableTexture();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlphaTest();
         GlStateManager.enableBlend();
      }
   }

   public void m_94jnhyuiz() {
      if (this.isMouseInList(this.mouseY)) {
         if (!Mouse.isButtonDown(0) || !this.isDragging()) {
            this.mouseYStart = -1.0F;
         } else if (this.mouseYStart == -1.0F) {
            boolean var1 = true;
            if (this.mouseY >= this.yStart && this.mouseY <= this.yEnd) {
               int var2 = this.width / 2 - this.getRowWidth() / 2;
               int var3 = this.width / 2 + this.getRowWidth() / 2;
               int var4 = this.mouseY - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
               int var5 = var4 / this.entryHeight;
               if (this.mouseX >= var2 && this.mouseX <= var3 && var5 >= 0 && var4 >= 0 && var5 < this.getEntriesSize()) {
                  boolean var6 = var5 == this.pos && MinecraftClient.getTime() - this.time < 250L;
                  this.selectEntry(var5, var6, this.mouseX, this.mouseY);
                  this.pos = var5;
                  this.time = MinecraftClient.getTime();
               } else if (this.mouseX >= var2 && this.mouseX <= var3 && var4 < 0) {
                  this.render(this.mouseX - var2, this.mouseY - this.yStart + (int)this.scrollAmount - 4);
                  var1 = false;
               }

               int var11 = this.getScrollbarPosition();
               int var7 = var11 + 6;
               if (this.mouseX >= var11 && this.mouseX <= var7) {
                  this.scrollSpeedMultiplier = -1.0F;
                  int var8 = this.getMaxScroll();
                  if (var8 < 1) {
                     var8 = 1;
                  }

                  int var9 = (int)((float)((this.yEnd - this.yStart) * (this.yEnd - this.yStart)) / (float)this.getListSize());
                  var9 = MathHelper.clamp(var9, 32, this.yEnd - this.yStart - 8);
                  this.scrollSpeedMultiplier /= (float)(this.yEnd - this.yStart - var9) / (float)var8;
               } else {
                  this.scrollSpeedMultiplier = 1.0F;
               }

               if (var1) {
                  this.mouseYStart = (float)this.mouseY;
               } else {
                  this.mouseYStart = -2.0F;
               }
            } else {
               this.mouseYStart = -2.0F;
            }
         } else if (this.mouseYStart >= 0.0F) {
            this.scrollAmount -= ((float)this.mouseY - this.mouseYStart) * this.scrollSpeedMultiplier;
            this.mouseYStart = (float)this.mouseY;
         }

         int var10 = Mouse.getEventDWheel();
         if (var10 != 0) {
            if (var10 > 0) {
               var10 = -1;
            } else if (var10 < 0) {
               var10 = 1;
            }

            this.scrollAmount += (float)(var10 * this.entryHeight / 2);
         }
      }
   }

   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public int getRowWidth() {
      return 220;
   }

   protected void renderList(int x, int y, int mouseX, int mouseY) {
      int var5 = this.getEntriesSize();
      Tessellator var6 = Tessellator.getInstance();
      BufferBuilder var7 = var6.getBufferBuilder();

      for(int var8 = 0; var8 < var5; ++var8) {
         int var9 = y + var8 * this.entryHeight + this.headerHeight;
         int var10 = this.entryHeight - 4;
         if (var9 > this.yEnd || var9 + var10 < this.yStart) {
            this.m_68rrjxwti(var8, x, var9);
         }

         if (this.renderSelectionBox && this.isEntrySelected(var8)) {
            int var11 = this.xStart + (this.width / 2 - this.getRowWidth() / 2);
            int var12 = this.xStart + this.width / 2 + this.getRowWidth() / 2;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture();
            var7.start();
            var7.color(8421504);
            var7.vertex((double)var11, (double)(var9 + var10 + 2), 0.0, 0.0, 1.0);
            var7.vertex((double)var12, (double)(var9 + var10 + 2), 0.0, 1.0, 1.0);
            var7.vertex((double)var12, (double)(var9 - 2), 0.0, 1.0, 0.0);
            var7.vertex((double)var11, (double)(var9 - 2), 0.0, 0.0, 0.0);
            var7.color(0);
            var7.vertex((double)(var11 + 1), (double)(var9 + var10 + 1), 0.0, 0.0, 1.0);
            var7.vertex((double)(var12 - 1), (double)(var9 + var10 + 1), 0.0, 1.0, 1.0);
            var7.vertex((double)(var12 - 1), (double)(var9 - 1), 0.0, 1.0, 0.0);
            var7.vertex((double)(var11 + 1), (double)(var9 - 1), 0.0, 0.0, 0.0);
            var6.end();
            GlStateManager.enableTexture();
         }

         this.renderEntry(var8, x, var9, var10, mouseX, mouseY);
      }
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   private void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha) {
      Tessellator var5 = Tessellator.getInstance();
      BufferBuilder var6 = var5.getBufferBuilder();
      this.client.getTextureManager().bind(GuiElement.OPTIONS_BACKGROUND);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = 32.0F;
      var6.start();
      var6.color(4210752, bottomAlpha);
      var6.vertex((double)this.xStart, (double)bottom, 0.0, 0.0, (double)((float)bottom / var7));
      var6.vertex((double)(this.xStart + this.width), (double)bottom, 0.0, (double)((float)this.width / var7), (double)((float)bottom / var7));
      var6.color(4210752, topAlpha);
      var6.vertex((double)(this.xStart + this.width), (double)top, 0.0, (double)((float)this.width / var7), (double)((float)top / var7));
      var6.vertex((double)this.xStart, (double)top, 0.0, 0.0, (double)((float)top / var7));
      var5.end();
   }

   public void setXPos(int x) {
      this.xStart = x;
      this.xEnd = x + this.width;
   }

   public int getItemHeight() {
      return this.entryHeight;
   }
}
