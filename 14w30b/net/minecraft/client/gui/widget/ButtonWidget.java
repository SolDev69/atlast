package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends GuiElement {
   protected static final Identifier BUTTON_TEXTURES = new Identifier("textures/gui/widgets.png");
   protected int width = 200;
   protected int height = 20;
   public int x;
   public int y;
   public String message;
   public int id;
   public boolean active = true;
   public boolean visible = true;
   protected boolean hovered;

   public ButtonWidget(int x, int y, int id, String message) {
      this(x, y, id, 200, 20, message);
   }

   public ButtonWidget(int x, int y, int id, int width, int height, String message) {
      this.id = x;
      this.x = y;
      this.y = id;
      this.width = width;
      this.height = height;
      this.message = message;
   }

   protected int getYImage(boolean isHovered) {
      byte var2 = 1;
      if (!this.active) {
         var2 = 0;
      } else if (isHovered) {
         var2 = 2;
      }

      return var2;
   }

   public void render(MinecraftClient client, int mouseX, int mouseY) {
      if (this.visible) {
         TextRenderer var4 = client.textRenderer;
         client.getTextureManager().bind(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int var5 = this.getYImage(this.hovered);
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         GlStateManager.blendFunc(770, 771);
         this.drawTexture(this.x, this.y, 0, 46 + var5 * 20, this.width / 2, this.height);
         this.drawTexture(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
         this.renderBg(client, mouseX, mouseY);
         int var6 = 14737632;
         if (!this.active) {
            var6 = 10526880;
         } else if (this.hovered) {
            var6 = 16777120;
         }

         this.drawCenteredString(var4, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, var6);
      }
   }

   protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
   }

   public void mouseReleased(int mouseX, int mouseY) {
   }

   public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
      return this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
   }

   public boolean isHovered() {
      return this.hovered;
   }

   public void renderToolTip(int mouseX, int mouseY) {
   }

   public void playDownSound(SoundManager soundManager) {
      soundManager.play(SimpleSoundEvent.of(new Identifier("gui.button.press"), 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void m_66aftacmi(int i) {
      this.width = i;
   }
}
