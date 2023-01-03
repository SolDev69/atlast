package net.minecraft;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_23gokypgt extends Screen {
   private static final GameOptions.Option[] f_58pqfagqp = new GameOptions.Option[]{
      GameOptions.Option.STREAM_BYTES_PER_PIXEL,
      GameOptions.Option.STREAM_FPS,
      GameOptions.Option.STREAM_KBPS,
      GameOptions.Option.STREAM_SEND_METADATA,
      GameOptions.Option.STREAM_VOLUME_MIC,
      GameOptions.Option.STREAM_VOLUME_SYSTEM,
      GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR,
      GameOptions.Option.STREAM_COMPRESSION
   };
   private static final GameOptions.Option[] f_81zfnnyqc = new GameOptions.Option[]{
      GameOptions.Option.STREAM_CHAT_ENABLED, GameOptions.Option.STREAM_CHAT_USER_FILTER
   };
   private final Screen f_94deweblv;
   private final GameOptions f_48usuqpbb;
   private String f_44mywjnda;
   private String f_24truiszx;
   private int f_31dxjalfz;
   private boolean f_10hchtxrg = false;

   public C_23gokypgt(Screen c_31rdgoemj, GameOptions c_99sowdzft) {
      this.f_94deweblv = c_31rdgoemj;
      this.f_48usuqpbb = c_99sowdzft;
   }

   @Override
   public void init() {
      int var1 = 0;
      this.f_44mywjnda = I18n.translate("options.stream.title");
      this.f_24truiszx = I18n.translate("options.stream.chat.title");

      for(GameOptions.Option var5 : f_58pqfagqp) {
         if (var5.isFloatOption()) {
            this.buttons.add(new OptionSliderWidget(var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var5));
         } else {
            this.buttons
               .add(
                  new OptionButtonWidget(
                     var5.getId(),
                     this.titleWidth / 2 - 155 + var1 % 2 * 160,
                     this.height / 6 + 24 * (var1 >> 1),
                     var5,
                     this.f_48usuqpbb.getValueAsString(var5)
                  )
               );
         }

         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.f_31dxjalfz = this.height / 6 + 24 * (var1 >> 1) + 6;
      var1 += 2;

      for(GameOptions.Option var11 : f_81zfnnyqc) {
         if (var11.isFloatOption()) {
            this.buttons.add(new OptionSliderWidget(var11.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), var11));
         } else {
            this.buttons
               .add(
                  new OptionButtonWidget(
                     var11.getId(),
                     this.titleWidth / 2 - 155 + var1 % 2 * 160,
                     this.height / 6 + 24 * (var1 >> 1),
                     var11,
                     this.f_48usuqpbb.getValueAsString(var11)
                  )
               );
         }

         ++var1;
      }

      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 155, this.height / 6 + 168, 150, 20, I18n.translate("gui.done")));
      ButtonWidget var8 = new ButtonWidget(201, this.titleWidth / 2 + 5, this.height / 6 + 168, 150, 20, I18n.translate("options.stream.ingestSelection"));
      var8.active = this.client.getTwitchStream().m_10yutarck() && this.client.getTwitchStream().m_96xxepobn().length > 0
         || this.client.getTwitchStream().m_70uizoygc();
      this.buttons.add(var8);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id < 100 && buttonWidget instanceof OptionButtonWidget) {
            GameOptions.Option var2 = ((OptionButtonWidget)buttonWidget).getOption();
            this.f_48usuqpbb.setValue(var2, 1);
            buttonWidget.message = this.f_48usuqpbb.getValueAsString(GameOptions.Option.byId(buttonWidget.id));
            if (this.client.getTwitchStream().m_99rcqogzt()
               && var2 != GameOptions.Option.STREAM_CHAT_ENABLED
               && var2 != GameOptions.Option.STREAM_CHAT_USER_FILTER) {
               this.f_10hchtxrg = true;
            }
         } else if (buttonWidget instanceof OptionSliderWidget) {
            if (buttonWidget.id == GameOptions.Option.STREAM_VOLUME_MIC.getId()) {
               this.client.getTwitchStream().m_19ohgzkkn();
            } else if (buttonWidget.id == GameOptions.Option.STREAM_VOLUME_SYSTEM.getId()) {
               this.client.getTwitchStream().m_19ohgzkkn();
            } else if (this.client.getTwitchStream().m_99rcqogzt()) {
               this.f_10hchtxrg = true;
            }
         }

         if (buttonWidget.id == 200) {
            this.client.options.save();
            this.client.openScreen(this.f_94deweblv);
         } else if (buttonWidget.id == 201) {
            this.client.options.save();
            this.client.openScreen(new C_84olcqpef(this));
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.f_44mywjnda, this.titleWidth / 2, 20, 16777215);
      this.drawCenteredString(this.textRenderer, this.f_24truiszx, this.titleWidth / 2, this.f_31dxjalfz, 16777215);
      if (this.f_10hchtxrg) {
         this.drawCenteredString(
            this.textRenderer, Formatting.RED + I18n.translate("options.stream.changes"), this.titleWidth / 2, 20 + this.textRenderer.fontHeight, 16777215
         );
      }

      super.render(mouseX, mouseY, tickDelta);
   }
}
