package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
   private final Screen parent;
   private ButtonWidget allowCommandsButton;
   private ButtonWidget gameModeButton;
   private String gameMode = "survival";
   private boolean allowCommands;

   public OpenToLanScreen(Screen parent) {
      this.parent = parent;
   }

   @Override
   public void init() {
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(101, this.titleWidth / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start")));
      this.buttons.add(new ButtonWidget(102, this.titleWidth / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
      this.buttons.add(this.gameModeButton = new ButtonWidget(104, this.titleWidth / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode")));
      this.buttons.add(this.allowCommandsButton = new ButtonWidget(103, this.titleWidth / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands")));
      this.updateButtonTexts();
   }

   private void updateButtonTexts() {
      this.gameModeButton.message = I18n.translate("selectWorld.gameMode") + " " + I18n.translate("selectWorld.gameMode." + this.gameMode);
      this.allowCommandsButton.message = I18n.translate("selectWorld.allowCommands") + " ";
      if (this.allowCommands) {
         this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.on");
      } else {
         this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.off");
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 102) {
         this.client.openScreen(this.parent);
      } else if (buttonWidget.id == 104) {
         if (this.gameMode.equals("spectator")) {
            this.gameMode = "creative";
         } else if (this.gameMode.equals("creative")) {
            this.gameMode = "adventure";
         } else if (this.gameMode.equals("adventure")) {
            this.gameMode = "survival";
         } else {
            this.gameMode = "spectator";
         }

         this.updateButtonTexts();
      } else if (buttonWidget.id == 103) {
         this.allowCommands = !this.allowCommands;
         this.updateButtonTexts();
      } else if (buttonWidget.id == 101) {
         this.client.openScreen(null);
         String var2 = this.client.getServer().publish(WorldSettings.GameMode.byId(this.gameMode), this.allowCommands);
         Object var3;
         if (var2 != null) {
            var3 = new TranslatableText("commands.publish.started", var2);
         } else {
            var3 = new LiteralText("commands.publish.failed");
         }

         this.client.gui.getChat().addMessage((Text)var3);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("lanServer.title"), this.titleWidth / 2, 50, 16777215);
      this.drawCenteredString(this.textRenderer, I18n.translate("lanServer.otherPlayers"), this.titleWidth / 2, 82, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }
}
