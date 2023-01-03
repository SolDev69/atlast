package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_84obvpdwb extends Screen {
   private RealmsScreen f_21ihbibbb;

   public C_84obvpdwb(RealmsScreen realmsScreen) {
      this.f_21ihbibbb = realmsScreen;
      super.buttons = Collections.synchronizedList(Lists.newArrayList());
   }

   public RealmsScreen m_23xkexgoe() {
      return this.f_21ihbibbb;
   }

   @Override
   public void init() {
      this.f_21ihbibbb.init();
      super.init();
   }

   public void m_22opaheap(String string, int i, int j, int k) {
      super.drawCenteredString(this.textRenderer, string, i, j, k);
   }

   public void m_22ygridsn(String string, int i, int j, int k) {
      super.drawString(this.textRenderer, string, i, j, k);
   }

   @Override
   public void drawTexture(int x, int y, int u, int v, int width, int height) {
      this.f_21ihbibbb.blit(x, y, u, v, width, height);
      super.drawTexture(x, y, u, v, width, height);
   }

   @Override
   public void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
      super.fillGradient(x1, y1, x2, y2, color1, color2);
   }

   @Override
   public void renderBackground() {
      super.renderBackground();
   }

   @Override
   public boolean shouldPauseGame() {
      return super.shouldPauseGame();
   }

   @Override
   public void renderBackground(int offset) {
      super.renderBackground(offset);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.f_21ihbibbb.render(mouseX, mouseY, tickDelta);
   }

   @Override
   public void renderTooltip(ItemStack stack, int x, int y) {
      super.renderTooltip(stack, x, y);
   }

   @Override
   public void renderTooltip(String text, int x, int y) {
      super.renderTooltip(text, x, y);
   }

   @Override
   public void renderTooltip(List text, int x, int y) {
      super.renderTooltip(text, x, y);
   }

   @Override
   public void tick() {
      this.f_21ihbibbb.tick();
      super.tick();
   }

   public int m_60sormira() {
      return this.textRenderer.fontHeight;
   }

   public int m_48cwbrijg(String string) {
      return this.textRenderer.getStringWidth(string);
   }

   public void m_99zzwknxk(String string, int i, int j, int k) {
      this.textRenderer.drawWithShadow(string, (float)i, (float)j, k);
   }

   public List m_48kxyfzhd(String string, int i) {
      return this.textRenderer.wrapLines(string, i);
   }

   @Override
   public final void buttonClicked(ButtonWidget buttonWidget) {
      this.f_21ihbibbb.buttonClicked(((C_59atfncjq)buttonWidget).m_97qvqxoua());
   }

   public void m_02oyttahp() {
      super.buttons.clear();
   }

   public void m_66tptftwm(RealmsButton realmsButton) {
      super.buttons.add(realmsButton.getProxy());
   }

   public List m_76byhkwst() {
      ArrayList var1 = Lists.newArrayListWithExpectedSize(super.buttons.size());

      for(ButtonWidget var3 : super.buttons) {
         var1.add(((C_59atfncjq)var3).m_97qvqxoua());
      }

      return var1;
   }

   public void m_15tvfewmg(RealmsButton realmsButton) {
      super.buttons.remove(realmsButton);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      this.f_21ihbibbb.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void handleMouse() {
      this.f_21ihbibbb.mouseEvent();
      super.handleMouse();
   }

   @Override
   public void handleKeyboard() {
      this.f_21ihbibbb.keyboardEvent();
      super.handleKeyboard();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      this.f_21ihbibbb.mouseReleased(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseDragged(int mouseX, int mouseY, int mouseButton, long duration) {
      this.f_21ihbibbb.mouseDragged(mouseX, mouseY, mouseButton, duration);
   }

   @Override
   public void keyPressed(char chr, int key) {
      this.f_21ihbibbb.keyPressed(chr, key);
   }

   @Override
   public void confirmResult(boolean result, int id) {
      this.f_21ihbibbb.confirmResult(result, id);
   }

   @Override
   public void removed() {
      this.f_21ihbibbb.removed();
      super.removed();
   }
}
