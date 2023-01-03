package net.minecraft.client.gui.screen.menu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen {
   private final Screen parent;
   private final ServerListEntry server;
   private TextFieldWidget addressField;
   private TextFieldWidget serverNameField;
   private ButtonWidget resourcePackButton;

   public AddServerScreen(Screen parent, ServerListEntry server) {
      this.parent = parent;
      this.server = server;
   }

   @Override
   public void tick() {
      this.serverNameField.tick();
      this.addressField.tick();
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 96 + 18, I18n.translate("addServer.add")));
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 120 + 18, I18n.translate("gui.cancel")));
      this.buttons
         .add(
            this.resourcePackButton = new ButtonWidget(
               2,
               this.titleWidth / 2 - 100,
               this.height / 4 + 72,
               I18n.translate("addServer.resourcePack") + ": " + this.server.getResourcePackStatus().getMessage().buildFormattedString()
            )
         );
      this.serverNameField = new TextFieldWidget(0, this.textRenderer, this.titleWidth / 2 - 100, 66, 200, 20);
      this.serverNameField.setFocused(true);
      this.serverNameField.setText(this.server.name);
      this.addressField = new TextFieldWidget(1, this.textRenderer, this.titleWidth / 2 - 100, 106, 200, 20);
      this.addressField.setMaxLength(128);
      this.addressField.setText(this.server.address);
      ((ButtonWidget)this.buttons.get(0)).active = this.addressField.getText().length() > 0
         && this.addressField.getText().split(":").length > 0
         && this.serverNameField.getText().length() > 0;
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 2) {
            this.server
               .setResourcePackStatus(
                  ServerListEntry.ResourcePackStatus.values()[(this.server.getResourcePackStatus().ordinal() + 1)
                     % ServerListEntry.ResourcePackStatus.values().length]
               );
            this.resourcePackButton.message = I18n.translate("addServer.resourcePack")
               + ": "
               + this.server.getResourcePackStatus().getMessage().buildFormattedString();
         } else if (buttonWidget.id == 1) {
            this.parent.confirmResult(false, 0);
         } else if (buttonWidget.id == 0) {
            this.server.name = this.serverNameField.getText();
            this.server.address = this.addressField.getText();
            this.parent.confirmResult(true, 0);
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      this.serverNameField.keyPressed(chr, key);
      this.addressField.keyPressed(chr, key);
      if (key == 15) {
         this.serverNameField.setFocused(!this.serverNameField.isFocused());
         this.addressField.setFocused(!this.addressField.isFocused());
      }

      if (key == 28 || key == 156) {
         this.buttonClicked((ButtonWidget)this.buttons.get(0));
      }

      ((ButtonWidget)this.buttons.get(0)).active = this.addressField.getText().length() > 0
         && this.addressField.getText().split(":").length > 0
         && this.serverNameField.getText().length() > 0;
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.addressField.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("addServer.title"), this.titleWidth / 2, 17, 16777215);
      this.drawString(this.textRenderer, I18n.translate("addServer.enterName"), this.titleWidth / 2 - 100, 53, 10526880);
      this.drawString(this.textRenderer, I18n.translate("addServer.enterIp"), this.titleWidth / 2 - 100, 94, 10526880);
      this.serverNameField.render();
      this.addressField.render();
      super.render(mouseX, mouseY, tickDelta);
   }
}
