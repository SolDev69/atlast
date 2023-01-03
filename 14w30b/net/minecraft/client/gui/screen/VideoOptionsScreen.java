package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Window;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class VideoOptionsScreen extends Screen {
   private Screen parent;
   protected String title = "Video Settings";
   private GameOptions options;
   private EntryListWidget listWidget;
   private static final GameOptions.Option[] VIDEO_OPTIONS = new GameOptions.Option[]{
      GameOptions.Option.GRAPHICS,
      GameOptions.Option.RENDER_DISTANCE,
      GameOptions.Option.AMBIENT_OCCLUSION,
      GameOptions.Option.FRAMERATE_LIMIT,
      GameOptions.Option.ANAGLYPH,
      GameOptions.Option.VIEW_BOBBING,
      GameOptions.Option.GUI_SCALE,
      GameOptions.Option.GAMMA,
      GameOptions.Option.RENDER_CLOUDS,
      GameOptions.Option.PARTICLES,
      GameOptions.Option.USE_FULLSCREEN,
      GameOptions.Option.ENABLE_VSYNC,
      GameOptions.Option.MAPMAP_LEVELS,
      GameOptions.Option.BLOCK_ALTERNATIVES,
      GameOptions.Option.USE_VBO
   };

   public VideoOptionsScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      this.title = I18n.translate("options.videoTitle");
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 100, this.height - 27, I18n.translate("gui.done")));
      if (!GLX.useVbos) {
         GameOptions.Option[] var1 = new GameOptions.Option[VIDEO_OPTIONS.length - 1];
         int var2 = 0;

         for(GameOptions.Option var6 : VIDEO_OPTIONS) {
            if (var6 == GameOptions.Option.USE_VBO) {
               break;
            }

            var1[var2] = var6;
            ++var2;
         }

         this.listWidget = new OptionListWidget(this.client, this.titleWidth, this.height, 32, this.height - 32, 25, var1);
      } else {
         this.listWidget = new OptionListWidget(this.client, this.titleWidth, this.height, 32, this.height - 32, 25, VIDEO_OPTIONS);
      }
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.listWidget.m_94jnhyuiz();
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
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      int var4 = this.options.guiScale;
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.listWidget.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.options.guiScale != var4) {
         Window var5 = new Window(this.client, this.client.width, this.client.height);
         int var6 = var5.getWidth();
         int var7 = var5.getHeight();
         this.init(this.client, var6, var7);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      int var4 = this.options.guiScale;
      super.mouseReleased(mouseX, mouseY, mouseButton);
      this.listWidget.mouseReleased(mouseX, mouseY, mouseButton);
      if (this.options.guiScale != var4) {
         Window var5 = new Window(this.client, this.client.width, this.client.height);
         int var6 = var5.getWidth();
         int var7 = var5.getHeight();
         this.init(this.client, var6, var7);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.listWidget.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 5, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }
}
