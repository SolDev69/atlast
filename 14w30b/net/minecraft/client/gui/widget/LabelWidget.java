package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LabelWidget extends GuiElement {
   protected int width = 200;
   protected int height = 20;
   public int x;
   public int y;
   private List f_90beaanam;
   public int f_57dhilnej;
   private boolean centered;
   public boolean skip = true;
   private boolean hasBorder;
   private int textColor;
   private int fillColor;
   private int lineColor1;
   private int lineColor2;
   private TextRenderer textRenderer;
   private int offset;

   public LabelWidget(TextRenderer c_30krpvtyk, int i, int j, int k, int l, int m, int n) {
      this.textRenderer = c_30krpvtyk;
      this.f_57dhilnej = i;
      this.x = j;
      this.y = k;
      this.width = l;
      this.height = m;
      this.f_90beaanam = Lists.newArrayList();
      this.centered = false;
      this.hasBorder = false;
      this.textColor = n;
      this.fillColor = -1;
      this.lineColor1 = -1;
      this.lineColor2 = -1;
      this.offset = 0;
   }

   public void m_22radzyzf(String string) {
      this.f_90beaanam.add(I18n.translate(string));
   }

   public LabelWidget m_34zfrlvbr() {
      this.centered = true;
      return this;
   }

   public void render(MinecraftClient client, int mouseX, int mouseY) {
      if (this.skip) {
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         this.renderBorder(client, mouseX, mouseY);
         int var4 = this.y + this.height / 2 + this.offset / 2;
         int var5 = var4 - this.f_90beaanam.size() * 10 / 2;

         for(int var6 = 0; var6 < this.f_90beaanam.size(); ++var6) {
            if (this.centered) {
               this.drawCenteredString(this.textRenderer, (String)this.f_90beaanam.get(var6), this.x + this.width / 2, var5 + var6 * 10, this.textColor);
            } else {
               this.drawString(this.textRenderer, (String)this.f_90beaanam.get(var6), this.x, var5 + var6 * 10, this.textColor);
            }
         }
      }
   }

   protected void renderBorder(MinecraftClient client, int mouseX, int mouseY) {
      if (this.hasBorder) {
         int var4 = this.width + this.offset * 2;
         int var5 = this.height + this.offset * 2;
         int var6 = this.x - this.offset;
         int var7 = this.y - this.offset;
         fill(var6, var7, var6 + var4, var7 + var5, this.fillColor);
         this.drawHorizontalLine(var6, var6 + var4, var7, this.lineColor1);
         this.drawHorizontalLine(var6, var6 + var4, var7 + var5, this.lineColor2);
         this.drawVerticalLine(var6, var7, var7 + var5, this.lineColor1);
         this.drawVerticalLine(var6 + var4, var7, var7 + var5, this.lineColor2);
      }
   }
}
