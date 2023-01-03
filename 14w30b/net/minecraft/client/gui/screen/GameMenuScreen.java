package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.screen.menu.AchievementsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameMenuScreen extends Screen {
   private int f_83rjwerba;
   private int ticks;

   @Override
   public void init() {
      this.f_83rjwerba = 0;
      this.buttons.clear();
      byte var1 = -16;
      boolean var2 = true;
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 120 + var1, I18n.translate("menu.returnToMenu")));
      if (!this.client.isIntegratedServerRunning()) {
         ((ButtonWidget)this.buttons.get(0)).message = I18n.translate("menu.disconnect");
      }

      this.buttons.add(new ButtonWidget(4, this.titleWidth / 2 - 100, this.height / 4 + 24 + var1, I18n.translate("menu.returnToGame")));
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 96 + var1, 98, 20, I18n.translate("menu.options")));
      ButtonWidget var3;
      this.buttons.add(var3 = new ButtonWidget(7, this.titleWidth / 2 + 2, this.height / 4 + 96 + var1, 98, 20, I18n.translate("menu.shareToLan")));
      this.buttons.add(new ButtonWidget(5, this.titleWidth / 2 - 100, this.height / 4 + 48 + var1, 98, 20, I18n.translate("gui.achievements")));
      this.buttons.add(new ButtonWidget(6, this.titleWidth / 2 + 2, this.height / 4 + 48 + var1, 98, 20, I18n.translate("gui.stats")));
      var3.active = this.client.isInSingleplayer() && !this.client.getServer().isPublished();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      switch(buttonWidget.id) {
         case 0:
            this.client.openScreen(new OptionsScreen(this, this.client.options));
            break;
         case 1:
            buttonWidget.active = false;
            this.client.world.disconnect();
            this.client.setWorld(null);
            this.client.openScreen(new TitleScreen());
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.client.openScreen(null);
            this.client.closeScreen();
            break;
         case 5:
            this.client.openScreen(new AchievementsScreen(this, this.client.player.getStatHandler()));
            break;
         case 6:
            this.client.openScreen(new StatsScreen(this, this.client.player.getStatHandler()));
            break;
         case 7:
            this.client.openScreen(new OpenToLanScreen(this));
      }
   }

   @Override
   public void tick() {
      super.tick();
      ++this.ticks;
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("menu.game"), this.titleWidth / 2, 40, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }
}
