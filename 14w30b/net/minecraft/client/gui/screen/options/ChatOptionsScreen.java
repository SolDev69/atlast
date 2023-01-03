package net.minecraft.client.gui.screen.options;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChatOptionsScreen extends Screen {
   private static final GameOptions.Option[] CHAT_OPTIONS = new GameOptions.Option[]{
      GameOptions.Option.CHAT_VISIBILITY,
      GameOptions.Option.CHAT_COLOR,
      GameOptions.Option.CHAT_LINKS,
      GameOptions.Option.CHAT_OPACITY,
      GameOptions.Option.CHAT_LINKS_PROMPT,
      GameOptions.Option.CHAT_SCALE,
      GameOptions.Option.CHAT_HEIGHT_FOCUSED,
      GameOptions.Option.CHAT_HEIGHT_UNFOCUSED,
      GameOptions.Option.CHAT_WIDTH,
      GameOptions.Option.REDUCED_DEBUG_INFO
   };
   private final Screen parent;
   private final GameOptions options;
   private String chatOptionsTitle;
   private String multiplayerOptionsTitle;
   private int seperatorWidth;

   public ChatOptionsScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      int var1 = 0;
      this.chatOptionsTitle = I18n.translate("options.chat.title");
      this.multiplayerOptionsTitle = I18n.translate("options.multiplayer.title");

      for(GameOptions.Option var5 : CHAT_OPTIONS) {
         if (var5.isFloatOption()) {
            this.buttons.add(new OptionSliderWidget(var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5));
         } else {
            this.buttons
               .add(
                  new OptionButtonWidget(
                     var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5, this.options.getValueAsString(var5)
                  )
               );
         }

         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.seperatorWidth = this.height / 6 + 24 * (var1 >> 1);
      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 100, this.height / 6 + 120, I18n.translate("gui.done")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id < 100 && buttonWidget instanceof OptionButtonWidget) {
            this.options.setValue(((OptionButtonWidget)buttonWidget).getOption(), 1);
            buttonWidget.message = this.options.getValueAsString(GameOptions.Option.byId(buttonWidget.id));
         }

         if (buttonWidget.id == 200) {
            this.client.options.save();
            this.client.openScreen(this.parent);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.chatOptionsTitle, this.titleWidth / 2, 20, 16777215);
      this.drawCenteredString(this.textRenderer, this.multiplayerOptionsTitle, this.titleWidth / 2, this.seperatorWidth + 7, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }
}
