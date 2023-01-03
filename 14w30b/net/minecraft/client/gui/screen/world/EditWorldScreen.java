package net.minecraft.client.gui.screen.world;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.WorldData;
import net.minecraft.world.storage.WorldStorageSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class EditWorldScreen extends Screen {
   private Screen parent;
   private TextFieldWidget searchField;
   private final String worldName;

   public EditWorldScreen(Screen parent, String worldName) {
      this.parent = parent;
      this.worldName = worldName;
   }

   @Override
   public void tick() {
      this.searchField.tick();
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 96 + 12, I18n.translate("selectWorld.renameButton")));
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.cancel")));
      WorldStorageSource var1 = this.client.getWorldStorageSource();
      WorldData var2 = var1.getData(this.worldName);
      String var3 = var2.getName();
      this.searchField = new TextFieldWidget(2, this.textRenderer, this.titleWidth / 2 - 100, 60, 200, 20);
      this.searchField.setFocused(true);
      this.searchField.setText(var3);
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 1) {
            this.client.openScreen(this.parent);
         } else if (buttonWidget.id == 0) {
            WorldStorageSource var2 = this.client.getWorldStorageSource();
            var2.rename(this.worldName, this.searchField.getText().trim());
            this.client.openScreen(this.parent);
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      this.searchField.keyPressed(chr, key);
      ((ButtonWidget)this.buttons.get(0)).active = this.searchField.getText().trim().length() > 0;
      if (key == 28 || key == 156) {
         this.buttonClicked((ButtonWidget)this.buttons.get(0));
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("selectWorld.renameTitle"), this.titleWidth / 2, 20, 16777215);
      this.drawString(this.textRenderer, I18n.translate("selectWorld.enterName"), this.titleWidth / 2 - 100, 47, 10526880);
      this.searchField.render();
      super.render(mouseX, mouseY, tickDelta);
   }
}
