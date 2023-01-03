package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.SpectatorMenuPage;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Window;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpectatorGui extends GuiElement implements SpectatorMenuListener {
   private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
   public static final Identifier SPECTATOR_WIDGETS_TEXTURE = new Identifier("textures/gui/spectator_widgets.png");
   private final MinecraftClient client;
   private long lastHotbarSelectTime;
   private SpectatorMenu menu;

   public SpectatorGui(MinecraftClient client) {
      this.client = client;
   }

   public void selectSlot(int slot) {
      this.lastHotbarSelectTime = MinecraftClient.getTime();
      if (this.menu != null) {
         this.menu.selectSlot(slot);
      } else {
         this.menu = new SpectatorMenu(this);
      }
   }

   private float getHotbarAlpha() {
      long var1 = this.lastHotbarSelectTime - MinecraftClient.getTime() + 5000L;
      return MathHelper.clamp((float)var1 / 2000.0F, 0.0F, 1.0F);
   }

   public void renderHotbar(Window window, float tickDelta) {
      if (this.menu != null) {
         float var3 = this.getHotbarAlpha();
         if (var3 <= 0.0F) {
            this.menu.close();
         } else {
            int var4 = window.getWidth() / 2;
            float var5 = this.drawOffset;
            this.drawOffset = -90.0F;
            float var6 = (float)window.getHeight() - 22.0F * var3;
            SpectatorMenuPage var7 = this.menu.getSelectedPage();
            this.renderMenu(window, var3, var4, var6, var7);
            this.drawOffset = var5;
         }
      }
   }

   protected void renderMenu(Window window, float alpha, int x, float y, SpectatorMenuPage page) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
      this.client.getTextureManager().bind(WIDGETS_TEXTURE);
      this.drawTexture((float)(x - 91), y, 0, 0, 182, 22);
      if (page.getSelectedSlot() >= 0) {
         this.drawTexture((float)(x - 91 - 1 + page.getSelectedSlot() * 20), y - 1.0F, 0, 22, 24, 22);
      }

      Lighting.turnOnGui();

      for(int var6 = 0; var6 < 9; ++var6) {
         this.renderSlot(var6, window.getWidth() / 2 - 90 + var6 * 20 + 2, y + 3.0F, alpha, page.getItem(var6));
      }

      Lighting.turnOff();
      GlStateManager.disableRescaleNormal();
      GlStateManager.enableBlend();
   }

   private void renderSlot(int slot, int x, float y, float alpha, SpectatorMenuItem menuItem) {
      this.client.getTextureManager().bind(SPECTATOR_WIDGETS_TEXTURE);
      if (menuItem != SpectatorMenu.EMPTY_SLOT) {
         int var6 = (int)(alpha * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x, y, 0.0F);
         float var7 = menuItem.isEnabled() ? 1.0F : 0.25F;
         GlStateManager.color4f(var7, var7, var7, alpha);
         menuItem.render(var7, var6);
         GlStateManager.popMatrix();
         String var8 = String.valueOf(GameOptions.getKeyName(this.client.options.hotbarKeys[slot].getKeyCode()));
         if (var6 > 3 && menuItem.isEnabled()) {
            this.client
               .textRenderer
               .drawWithShadow(var8, (float)(x + 19 - 2 - this.client.textRenderer.getStringWidth(var8)), y + 6.0F + 3.0F, 16777215 + (var6 << 24));
         }
      }
   }

   public void renderTooltip(Window window) {
      int var2 = (int)(this.getHotbarAlpha() * 255.0F);
      if (var2 > 3 && this.menu != null) {
         SpectatorMenuItem var3 = this.menu.getSelectedItem();
         String var4 = var3 != SpectatorMenu.EMPTY_SLOT
            ? var3.getDisplayName().buildFormattedString()
            : this.menu.getCategory().getPrompt().buildFormattedString();
         if (var4 != null) {
            int var5 = (window.getWidth() - this.client.textRenderer.getStringWidth(var4)) / 2;
            int var6 = window.getHeight() - 35;
            GlStateManager.pushMatrix();
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            this.client.textRenderer.drawWithShadow(var4, (float)var5, (float)var6, 16777215 + (var2 << 24));
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public void onSpectatorMenuClosed(SpectatorMenu menu) {
      this.menu = null;
      this.lastHotbarSelectTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void mouseScrolled(int scroll) {
      int var2 = this.menu.getSelectedSlot() + scroll;

      while(var2 >= 0 && var2 <= 8 && (this.menu.getItem(var2) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(var2).isEnabled())) {
         var2 += scroll;
      }

      if (var2 >= 0 && var2 <= 8) {
         this.menu.selectSlot(var2);
         this.lastHotbarSelectTime = MinecraftClient.getTime();
      }
   }

   public void mouseMiddleClicked() {
      this.lastHotbarSelectTime = MinecraftClient.getTime();
      if (this.isMenuActive()) {
         int var1 = this.menu.getSelectedSlot();
         if (var1 != -1) {
            this.menu.selectSlot(var1);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }
   }
}
