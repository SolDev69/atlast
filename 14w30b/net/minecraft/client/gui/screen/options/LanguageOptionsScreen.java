package net.minecraft.client.gui.screen.options;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Window;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageOptionsScreen extends Screen {
   protected Screen parent;
   private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionListWidget;
   private final GameOptions gameOptions;
   private final LanguageManager languageManager;
   private OptionButtonWidget forceUnicodeFontButton;
   private OptionButtonWidget doneButton;

   public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
      this.parent = parent;
      this.gameOptions = options;
      this.languageManager = languageManager;
   }

   @Override
   public void init() {
      this.buttons
         .add(
            this.forceUnicodeFontButton = new OptionButtonWidget(
               100,
               this.titleWidth / 2 - 155,
               this.height - 38,
               GameOptions.Option.FORCE_UNICODE_FONT,
               this.gameOptions.getValueAsString(GameOptions.Option.FORCE_UNICODE_FONT)
            )
         );
      this.buttons.add(this.doneButton = new OptionButtonWidget(6, this.titleWidth / 2 - 155 + 160, this.height - 38, I18n.translate("gui.done")));
      this.languageSelectionListWidget = new LanguageOptionsScreen.LanguageSelectionListWidget(this.client);
      this.languageSelectionListWidget.setScrollButtonIds(7, 8);
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.languageSelectionListWidget.m_94jnhyuiz();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         switch(buttonWidget.id) {
            case 5:
               break;
            case 6:
               this.client.openScreen(this.parent);
               break;
            case 100:
               if (buttonWidget instanceof OptionButtonWidget) {
                  this.gameOptions.setValue(((OptionButtonWidget)buttonWidget).getOption(), 1);
                  buttonWidget.message = this.gameOptions.getValueAsString(GameOptions.Option.FORCE_UNICODE_FONT);
                  Window var2 = new Window(this.client, this.client.width, this.client.height);
                  int var3 = var2.getWidth();
                  int var4 = var2.getHeight();
                  this.init(this.client, var3, var4);
               }
               break;
            default:
               this.languageSelectionListWidget.buttonClicked(buttonWidget);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.languageSelectionListWidget.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, I18n.translate("options.language"), this.titleWidth / 2, 16, 16777215);
      this.drawCenteredString(this.textRenderer, "(" + I18n.translate("options.languageWarning") + ")", this.titleWidth / 2, this.height - 56, 8421504);
      super.render(mouseX, mouseY, tickDelta);
   }

   @Environment(EnvType.CLIENT)
   class LanguageSelectionListWidget extends ListWidget {
      private final List languages = Lists.newArrayList();
      private final Map defenitionByLanguage = Maps.newHashMap();

      public LanguageSelectionListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk, LanguageOptionsScreen.this.titleWidth, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);

         for(LanguageDefinition var4 : LanguageOptionsScreen.this.languageManager.getLanguages()) {
            this.defenitionByLanguage.put(var4.getCode(), var4);
            this.languages.add(var4.getCode());
         }
      }

      @Override
      protected int getEntriesSize() {
         return this.languages.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         LanguageDefinition var5 = (LanguageDefinition)this.defenitionByLanguage.get(this.languages.get(y));
         LanguageOptionsScreen.this.languageManager.setLanguage(var5);
         LanguageOptionsScreen.this.gameOptions.language = var5.getCode();
         this.client.reloadResources();
         LanguageOptionsScreen.this.textRenderer
            .setUnicode(LanguageOptionsScreen.this.languageManager.isUnicode() || LanguageOptionsScreen.this.gameOptions.forceUnicodeFont);
         LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.isRightToLeft());
         LanguageOptionsScreen.this.doneButton.message = I18n.translate("gui.done");
         LanguageOptionsScreen.this.forceUnicodeFontButton.message = LanguageOptionsScreen.this.gameOptions
            .getValueAsString(GameOptions.Option.FORCE_UNICODE_FONT);
         LanguageOptionsScreen.this.gameOptions.save();
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return ((String)this.languages.get(index)).equals(LanguageOptionsScreen.this.languageManager.getLanguage().getCode());
      }

      @Override
      protected int getListSize() {
         return this.getEntriesSize() * 18;
      }

      @Override
      protected void renderBackground() {
         LanguageOptionsScreen.this.renderBackground();
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         LanguageOptionsScreen.this.textRenderer.setRightToLeft(true);
         LanguageOptionsScreen.this.drawCenteredString(
            LanguageOptionsScreen.this.textRenderer,
            ((LanguageDefinition)this.defenitionByLanguage.get(this.languages.get(index))).toString(),
            this.width / 2,
            y + 1,
            16777215
         );
         LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
      }
   }
}
