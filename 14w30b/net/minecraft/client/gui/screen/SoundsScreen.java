package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundsScreen extends Screen {
   private final Screen parent;
   private final GameOptions options;
   protected String name = "Options";
   private String off;

   public SoundsScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      int var1 = 0;
      this.name = I18n.translate("options.sounds.title");
      this.off = I18n.translate("options.off");
      this.buttons
         .add(
            new SoundsScreen.SoundSliderWidget(
               SoundCategory.MASTER.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), SoundCategory.MASTER, true
            )
         );
      var1 += 2;

      for(SoundCategory var5 : SoundCategory.values()) {
         if (var5 != SoundCategory.MASTER) {
            this.buttons
               .add(
                  new SoundsScreen.SoundSliderWidget(
                     var5.getId(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5, false
                  )
               );
            ++var1;
         }
      }

      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 200) {
            this.client.options.save();
            this.client.openScreen(this.parent);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.name, this.titleWidth / 2, 15, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   protected String getVolume(SoundCategory soundCatecory) {
      float var2 = this.options.getSoundCategoryVolume(soundCatecory);
      return var2 == 0.0F ? this.off : (int)(var2 * 100.0F) + "%";
   }

   @Environment(EnvType.CLIENT)
   class SoundSliderWidget extends ButtonWidget {
      private final SoundCategory soundCatergory;
      private final String widgetName;
      public float sliderButtonPos = 1.0F;
      public boolean dragging;

      public SoundSliderWidget(int x, int y, int id, SoundCategory category, boolean doubleSlider) {
         super(x, y, id, doubleSlider ? 310 : 150, 20, "");
         this.soundCatergory = category;
         this.widgetName = I18n.translate("soundCategory." + category.getName());
         this.message = this.widgetName + ": " + SoundsScreen.this.getVolume(category);
         this.sliderButtonPos = SoundsScreen.this.options.getSoundCategoryVolume(category);
      }

      @Override
      protected int getYImage(boolean isHovered) {
         return 0;
      }

      @Override
      protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
         if (this.visible) {
            if (this.dragging) {
               this.sliderButtonPos = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
               this.sliderButtonPos = MathHelper.clamp(this.sliderButtonPos, 0.0F, 1.0F);
               client.options.setSoundCategoryVolume(this.soundCatergory, this.sliderButtonPos);
               client.options.save();
               this.message = this.widgetName + ": " + SoundsScreen.this.getVolume(this.soundCatergory);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x + (int)(this.sliderButtonPos * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexture(this.x + (int)(this.sliderButtonPos * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
         }
      }

      @Override
      public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
         if (super.isMouseOver(client, mouseX, mouseY)) {
            this.sliderButtonPos = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            this.sliderButtonPos = MathHelper.clamp(this.sliderButtonPos, 0.0F, 1.0F);
            client.options.setSoundCategoryVolume(this.soundCatergory, this.sliderButtonPos);
            client.options.save();
            this.message = this.widgetName + ": " + SoundsScreen.this.getVolume(this.soundCatergory);
            this.dragging = true;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void playDownSound(SoundManager soundManager) {
      }

      @Override
      public void mouseReleased(int mouseX, int mouseY) {
         if (this.dragging) {
            if (this.soundCatergory == SoundCategory.MASTER) {
               float var10000 = 1.0F;
            } else {
               SoundsScreen.this.options.getSoundCategoryVolume(this.soundCatergory);
            }

            SoundsScreen.this.client.getSoundManager().play(SimpleSoundEvent.of(new Identifier("gui.button.press"), 1.0F));
         }

         this.dragging = false;
      }
   }
}
