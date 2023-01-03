package net.minecraft.client.gui.screen.multiplayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanScanWidget implements EntryListWidget.Entry {
   private final MinecraftClient client = MinecraftClient.getInstance();

   @Override
   public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
      int var9 = y + height / 2 - this.client.textRenderer.fontHeight / 2;
      this.client
         .textRenderer
         .drawWithoutShadow(
            I18n.translate("lanServer.scanning"),
            this.client.currentScreen.titleWidth / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2,
            var9,
            16777215
         );
      String var10;
      switch((int)(MinecraftClient.getTime() / 300L % 4L)) {
         case 0:
         default:
            var10 = "O o o";
            break;
         case 1:
         case 3:
            var10 = "o O o";
            break;
         case 2:
            var10 = "o o O";
      }

      this.client
         .textRenderer
         .drawWithoutShadow(
            var10,
            this.client.currentScreen.titleWidth / 2 - this.client.textRenderer.getStringWidth(var10) / 2,
            var9 + this.client.textRenderer.fontHeight,
            8421504
         );
   }

   @Override
   public void m_82anuocxe(int i, int j, int k) {
   }

   @Override
   public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
      return false;
   }

   @Override
   public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
   }
}
