package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import java.net.URI;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier TEXTURE = new Identifier("textures/gui/demo_background.png");

   @Override
   public void init() {
      this.buttons.clear();
      byte var1 = -16;
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 116, this.height / 2 + 62 + var1, 114, 20, I18n.translate("demo.help.buy")));
      this.buttons.add(new ButtonWidget(2, this.titleWidth / 2 + 2, this.height / 2 + 62 + var1, 114, 20, I18n.translate("demo.help.later")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      switch(buttonWidget.id) {
         case 1:
            buttonWidget.active = false;

            try {
               Class var2 = Class.forName("java.awt.Desktop");
               Object var3 = var2.getMethod("getDesktop").invoke(null);
               var2.getMethod("browse", URI.class).invoke(var3, new URI("http://www.minecraft.net/store?source=demo"));
            } catch (Throwable var4) {
               LOGGER.error("Couldn't open link", var4);
            }
            break;
         case 2:
            this.client.openScreen(null);
            this.client.closeScreen();
      }
   }

   @Override
   public void tick() {
      super.tick();
   }

   @Override
   public void renderBackground() {
      super.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var1 = (this.titleWidth - 248) / 2;
      int var2 = (this.height - 166) / 2;
      this.drawTexture(var1, var2, 0, 0, 248, 166);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      int var4 = (this.titleWidth - 248) / 2 + 10;
      int var5 = (this.height - 166) / 2 + 8;
      this.textRenderer.drawWithoutShadow(I18n.translate("demo.help.title"), var4, var5, 2039583);
      var5 += 12;
      GameOptions var6 = this.client.options;
      this.textRenderer
         .drawWithoutShadow(
            I18n.translate(
               "demo.help.movementShort",
               GameOptions.getKeyName(var6.forwardKey.getKeyCode()),
               GameOptions.getKeyName(var6.leftKey.getKeyCode()),
               GameOptions.getKeyName(var6.backKey.getKeyCode()),
               GameOptions.getKeyName(var6.rightKey.getKeyCode())
            ),
            var4,
            var5,
            5197647
         );
      this.textRenderer.drawWithoutShadow(I18n.translate("demo.help.movementMouse"), var4, var5 + 12, 5197647);
      this.textRenderer.drawWithoutShadow(I18n.translate("demo.help.jump", GameOptions.getKeyName(var6.jumpKey.getKeyCode())), var4, var5 + 24, 5197647);
      this.textRenderer
         .drawWithoutShadow(I18n.translate("demo.help.inventory", GameOptions.getKeyName(var6.inventoryKey.getKeyCode())), var4, var5 + 36, 5197647);
      this.textRenderer.drawTrimmed(I18n.translate("demo.help.fullWrapped"), var4, var5 + 68, 218, 2039583);
      super.render(mouseX, mouseY, tickDelta);
   }
}
