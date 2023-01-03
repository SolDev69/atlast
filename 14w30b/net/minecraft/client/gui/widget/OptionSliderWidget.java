package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OptionSliderWidget extends ButtonWidget {
   private float value = 1.0F;
   public boolean dragging;
   private GameOptions.Option option;
   private final float min;
   private final float max;

   public OptionSliderWidget(int x, int y, int id, GameOptions.Option option) {
      this(x, y, id, option, 0.0F, 1.0F);
   }

   public OptionSliderWidget(int x, int y, int id, GameOptions.Option option, float min, float max) {
      super(x, y, id, 150, 20, "");
      this.option = option;
      this.min = min;
      this.max = max;
      MinecraftClient var7 = MinecraftClient.getInstance();
      this.value = option.normalize(var7.options.getValueFloat(option));
      this.message = var7.options.getValueAsString(option);
   }

   @Override
   protected int getYImage(boolean isHovered) {
      return 0;
   }

   @Override
   protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.dragging) {
            this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
            float var4 = this.option.denormalize(this.value);
            client.options.setValue(this.option, var4);
            this.value = this.option.normalize(var4);
            this.message = client.options.getValueAsString(this.option);
         }

         client.getTextureManager().bind(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
      if (super.isMouseOver(client, mouseX, mouseY)) {
         this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
         this.value = MathHelper.clamp(this.value, 0.0F, 1.0F);
         client.options.setValue(this.option, this.option.denormalize(this.value));
         this.message = client.options.getValueAsString(this.option);
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.dragging = false;
   }
}
