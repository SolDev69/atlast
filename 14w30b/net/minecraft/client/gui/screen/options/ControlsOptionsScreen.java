package net.minecraft.client.gui.screen.options;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends Screen {
   private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{
      GameOptions.Option.INVERT_MOUSE, GameOptions.Option.SENSITIVITY, GameOptions.Option.TOUCHSCREEN
   };
   private Screen parent;
   protected String title = "Controls";
   private GameOptions options;
   public KeyBinding selectedKeyBinding = null;
   public long f_79xsamikd;
   private ControlsListWidget controlsList;
   private ButtonWidget resetAllButton;

   public ControlsOptionsScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      this.controlsList = new ControlsListWidget(this, this.client);
      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 155, this.height - 29, 150, 20, I18n.translate("gui.done")));
      this.buttons
         .add(this.resetAllButton = new ButtonWidget(201, this.titleWidth / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("controls.resetAll")));
      this.title = I18n.translate("controls.title");
      int var1 = 0;

      for(GameOptions.Option var5 : OPTIONS) {
         if (var5.isFloatOption()) {
            this.buttons.add(new OptionSliderWidget(var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5));
         } else {
            this.buttons
               .add(
                  new OptionButtonWidget(
                     var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, 18 + 24 * (var1 >> 1), var5, this.options.getValueAsString(var5)
                  )
               );
         }

         ++var1;
      }
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.controlsList.m_94jnhyuiz();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 200) {
         this.client.openScreen(this.parent);
      } else if (buttonWidget.id == 201) {
         for(KeyBinding var5 : this.client.options.ingameKeys) {
            var5.setKeyCode(var5.getDefaultKeyCode());
         }

         KeyBinding.updateKeyCodeMap();
      } else if (buttonWidget.id < 100 && buttonWidget instanceof OptionButtonWidget) {
         this.options.setValue(((OptionButtonWidget)buttonWidget).getOption(), 1);
         buttonWidget.message = this.options.getValueAsString(GameOptions.Option.byId(buttonWidget.id));
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.selectedKeyBinding != null) {
         this.options.setKeyCode(this.selectedKeyBinding, -100 + mouseButton);
         this.selectedKeyBinding = null;
         KeyBinding.updateKeyCodeMap();
      } else if (mouseButton != 0 || !this.controlsList.mouseClicked(mouseX, mouseY, mouseButton)) {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton != 0 || !this.controlsList.mouseReleased(mouseX, mouseY, mouseButton)) {
         super.mouseReleased(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (this.selectedKeyBinding != null) {
         if (key == 1) {
            this.options.setKeyCode(this.selectedKeyBinding, 0);
         } else {
            this.options.setKeyCode(this.selectedKeyBinding, key);
         }

         this.selectedKeyBinding = null;
         this.f_79xsamikd = MinecraftClient.getTime();
         KeyBinding.updateKeyCodeMap();
      } else {
         super.keyPressed(chr, key);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.controlsList.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 8, 16777215);
      boolean var4 = true;

      for(KeyBinding var8 : this.options.ingameKeys) {
         if (var8.getKeyCode() != var8.getDefaultKeyCode()) {
            var4 = false;
            break;
         }
      }

      this.resetAllButton.active = !var4;
      super.render(mouseX, mouseY, tickDelta);
   }
}
