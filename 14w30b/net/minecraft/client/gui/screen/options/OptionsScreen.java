package net.minecraft.client.gui.screen.options;

import net.minecraft.C_19uipimfe;
import net.minecraft.C_23gokypgt;
import net.minecraft.C_39cmizuwc;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SnooperScreen;
import net.minecraft.client.gui.screen.SoundsScreen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.SoundPool;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OptionsScreen extends Screen implements ConfirmationListener {
   private static final GameOptions.Option[] RENDER_OPTIONS = new GameOptions.Option[]{GameOptions.Option.FOV};
   private final Screen parent;
   private final GameOptions options;
   private ButtonWidget difficultyButton;
   private LockButtonWidget lockButton;
   protected String title = "Options";

   public OptionsScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      int var1 = 0;
      this.title = I18n.translate("options.title");

      for(GameOptions.Option var5 : RENDER_OPTIONS) {
         if (var5.isFloatOption()) {
            this.buttons.add(new OptionSliderWidget(var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5));
         } else {
            OptionButtonWidget var6 = new OptionButtonWidget(
               var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5, this.options.getValueAsString(var5)
            );
            this.buttons.add(var6);
         }

         ++var1;
      }

      if (this.client.world != null) {
         Difficulty var7 = this.client.world.getDifficulty();
         this.difficultyButton = new ButtonWidget(
            108, this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, this.m_82kplcedq(var7)
         );
         this.buttons.add(this.difficultyButton);
         if (this.client.isInSingleplayer() && !this.client.world.getData().isHardcore()) {
            this.difficultyButton.m_66aftacmi(this.difficultyButton.getWidth() - 20);
            this.lockButton = new LockButtonWidget(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y);
            this.buttons.add(this.lockButton);
            this.lockButton.setLocked(this.client.world.getData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      }

      this.buttons.add(new ButtonWidget(110, this.titleWidth / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation")));
      this.buttons
         .add(
            new ButtonWidget(8675309, this.titleWidth / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...") {
               @Override
               public void playDownSound(SoundManager soundManager) {
                  SoundPool var2 = soundManager.getRandom(
                     SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER
                  );
                  if (var2 != null) {
                     soundManager.play(SimpleSoundEvent.of(var2.getId(), 0.5F));
                  }
               }
            }
         );
      this.buttons.add(new ButtonWidget(106, this.titleWidth / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.sounds")));
      this.buttons.add(new ButtonWidget(107, this.titleWidth / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.stream")));
      this.buttons.add(new ButtonWidget(101, this.titleWidth / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.video")));
      this.buttons.add(new ButtonWidget(100, this.titleWidth / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.controls")));
      this.buttons.add(new ButtonWidget(102, this.titleWidth / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.language")));
      this.buttons.add(new ButtonWidget(103, this.titleWidth / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.multiplayer.title")));
      this.buttons.add(new ButtonWidget(105, this.titleWidth / 2 - 155, this.height / 6 + 144 - 6, 150, 20, I18n.translate("options.resourcepack")));
      this.buttons.add(new ButtonWidget(104, this.titleWidth / 2 + 5, this.height / 6 + 144 - 6, 150, 20, I18n.translate("options.snooper.view")));
      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")));
   }

   public String m_82kplcedq(Difficulty c_57rxazdrb) {
      LiteralText var2 = new LiteralText("");
      var2.append(new TranslatableText("options.difficulty"));
      var2.append(": ");
      var2.append(new TranslatableText(c_57rxazdrb.getName()));
      return var2.buildFormattedString();
   }

   @Override
   public void confirmResult(boolean result, int id) {
      this.client.openScreen(this);
      if (id == 109 && result && this.client.world != null) {
         this.client.world.getData().setDifficultyLocked(true);
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id < 100 && buttonWidget instanceof OptionButtonWidget) {
            GameOptions.Option var2 = ((OptionButtonWidget)buttonWidget).getOption();
            this.options.setValue(var2, 1);
            buttonWidget.message = this.options.getValueAsString(GameOptions.Option.byId(buttonWidget.id));
         }

         if (buttonWidget.id == 108) {
            this.client.world.getData().setDifficulty(Difficulty.byIndex(this.client.world.getDifficulty().getIndex() + 1));
            this.difficultyButton.message = this.m_82kplcedq(this.client.world.getDifficulty());
         }

         if (buttonWidget.id == 109) {
            this.client
               .openScreen(
                  new ConfirmScreen(
                     this,
                     new TranslatableText("difficulty.lock.title").buildFormattedString(),
                     new TranslatableText("difficulty.lock.question", new TranslatableText(this.client.world.getData().getDifficulty().getName()))
                        .buildFormattedString(),
                     109
                  )
               );
         }

         if (buttonWidget.id == 110) {
            this.client.options.save();
            this.client.openScreen(new C_19uipimfe(this));
         }

         if (buttonWidget.id == 8675309) {
            this.client.gameRenderer.nextShader();
         }

         if (buttonWidget.id == 101) {
            this.client.options.save();
            this.client.openScreen(new VideoOptionsScreen(this, this.options));
         }

         if (buttonWidget.id == 100) {
            this.client.options.save();
            this.client.openScreen(new ControlsOptionsScreen(this, this.options));
         }

         if (buttonWidget.id == 102) {
            this.client.options.save();
            this.client.openScreen(new LanguageOptionsScreen(this, this.options, this.client.getLanguageManager()));
         }

         if (buttonWidget.id == 103) {
            this.client.options.save();
            this.client.openScreen(new ChatOptionsScreen(this, this.options));
         }

         if (buttonWidget.id == 104) {
            this.client.options.save();
            this.client.openScreen(new SnooperScreen(this, this.options));
         }

         if (buttonWidget.id == 200) {
            this.client.options.save();
            this.client.openScreen(this.parent);
         }

         if (buttonWidget.id == 105) {
            this.client.options.save();
            this.client.openScreen(new ResourcePackScreen(this));
         }

         if (buttonWidget.id == 106) {
            this.client.options.save();
            this.client.openScreen(new SoundsScreen(this, this.options));
         }

         if (buttonWidget.id == 107) {
            this.client.options.save();
            TwitchStream var3 = this.client.getTwitchStream();
            if (var3.m_01cdoylst() && var3.m_78qjaxsih()) {
               this.client.openScreen(new C_23gokypgt(this, this.options));
            } else {
               C_39cmizuwc.m_95brgjnxo(this);
            }
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 15, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }
}
