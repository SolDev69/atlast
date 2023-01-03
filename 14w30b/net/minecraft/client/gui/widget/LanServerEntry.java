package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanServerEntry implements EntryListWidget.Entry {
   private final MultiplayerScreen multiplayerScreen;
   protected final MinecraftClient client;
   protected final LanServerQueryManager.LanServerInfo lanServerInfo;
   private long time = 0L;

   protected LanServerEntry(MultiplayerScreen multiplayerScreen, LanServerQueryManager.LanServerInfo lanServerInfo) {
      this.multiplayerScreen = multiplayerScreen;
      this.lanServerInfo = lanServerInfo;
      this.client = MinecraftClient.getInstance();
   }

   @Override
   public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
      this.client.textRenderer.drawWithoutShadow(I18n.translate("lanServer.title"), x + 32 + 3, y + 1, 16777215);
      this.client.textRenderer.drawWithoutShadow(this.lanServerInfo.getMotd(), x + 32 + 3, y + 12, 8421504);
      if (this.client.options.hideServerAddress) {
         this.client.textRenderer.drawWithoutShadow(I18n.translate("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, 3158064);
      } else {
         this.client.textRenderer.drawWithoutShadow(this.lanServerInfo.getPort(), x + 32 + 3, y + 12 + 11, 3158064);
      }
   }

   @Override
   public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
      this.multiplayerScreen.moveToServer(id);
      if (MinecraftClient.getTime() - this.time < 250L) {
         this.multiplayerScreen.connect();
      }

      this.time = MinecraftClient.getTime();
      return false;
   }

   @Override
   public void m_82anuocxe(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
   }

   public LanServerQueryManager.LanServerInfo getLanServerInfo() {
      return this.lanServerInfo;
   }
}
