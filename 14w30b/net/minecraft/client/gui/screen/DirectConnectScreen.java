package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class DirectConnectScreen extends Screen {
   private final Screen parent;
   private final ServerListEntry server;
   private TextFieldWidget serverField;

   public DirectConnectScreen(Screen parent, ServerListEntry server) {
      this.parent = parent;
      this.server = server;
   }

   @Override
   public void tick() {
      this.serverField.tick();
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectServer.select")));
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
      this.serverField = new TextFieldWidget(2, this.textRenderer, this.titleWidth / 2 - 100, 116, 200, 20);
      this.serverField.setMaxLength(128);
      this.serverField.setFocused(true);
      this.serverField.setText(this.client.options.lastServer);
      ((ButtonWidget)this.buttons.get(0)).active = this.serverField.getText().length() > 0 && this.serverField.getText().split(":").length > 0;
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
      this.client.options.lastServer = this.serverField.getText();
      this.client.options.save();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 1) {
            this.parent.confirmResult(false, 0);
         } else if (buttonWidget.id == 0) {
            this.server.address = this.serverField.getText();
            this.parent.confirmResult(true, 0);
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (this.serverField.keyPressed(chr, key)) {
         ((ButtonWidget)this.buttons.get(0)).active = this.serverField.getText().length() > 0 && this.serverField.getText().split(":").length > 0;
      } else if (key == 28 || key == 156) {
         this.buttonClicked((ButtonWidget)this.buttons.get(0));
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("selectServer.direct"), this.titleWidth / 2, 20, 16777215);
      this.drawString(this.textRenderer, I18n.translate("addServer.enterIp"), this.titleWidth / 2 - 100, 100, 10526880);
      this.serverField.render();
      super.render(mouseX, mouseY, tickDelta);
   }
}
