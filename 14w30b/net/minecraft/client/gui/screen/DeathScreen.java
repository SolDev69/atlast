package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen implements ConfirmationListener {
   private int ticksSinceDeath;
   private boolean isHardcore = false;

   @Override
   public void init() {
      this.buttons.clear();
      if (this.client.world.getData().isHardcore()) {
         if (this.client.isIntegratedServerRunning()) {
            this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.deleteWorld")));
         } else {
            this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.leaveServer")));
         }
      } else {
         this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 72, I18n.translate("deathScreen.respawn")));
         this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 96, I18n.translate("deathScreen.titleScreen")));
         if (this.client.getSession() == null) {
            ((ButtonWidget)this.buttons.get(1)).active = false;
         }
      }

      for(ButtonWidget var2 : this.buttons) {
         var2.active = false;
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      switch(buttonWidget.id) {
         case 0:
            this.client.player.tryRespawn();
            this.client.openScreen(null);
            break;
         case 1:
            ConfirmScreen var2 = new ConfirmScreen(
               this, I18n.translate("deathScreen.quit.confirm"), "", I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"), 0
            );
            this.client.openScreen(var2);
            var2.disableButtons(20);
      }
   }

   @Override
   public void confirmResult(boolean result, int id) {
      if (result) {
         this.client.world.disconnect();
         this.client.setWorld(null);
         this.client.openScreen(new TitleScreen());
      } else {
         this.client.player.tryRespawn();
         this.client.openScreen(null);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.fillGradient(0, 0, this.titleWidth, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      boolean var4 = this.client.world.getData().isHardcore();
      String var5 = var4 ? I18n.translate("deathScreen.title.hardcore") : I18n.translate("deathScreen.title");
      this.drawCenteredString(this.textRenderer, var5, this.titleWidth / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if (var4) {
         this.drawCenteredString(this.textRenderer, I18n.translate("deathScreen.hardcoreInfo"), this.titleWidth / 2, 144, 16777215);
      }

      this.drawCenteredString(
         this.textRenderer, I18n.translate("deathScreen.score") + ": " + Formatting.YELLOW + this.client.player.getScore(), this.titleWidth / 2, 100, 16777215
      );
      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   public boolean shouldPauseGame() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      ++this.ticksSinceDeath;
      if (this.ticksSinceDeath == 20) {
         for(ButtonWidget var2 : this.buttons) {
            var2.active = true;
         }
      }
   }
}
