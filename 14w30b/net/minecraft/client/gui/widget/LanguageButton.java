package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageButton extends ButtonWidget {
   public LanguageButton(int x, int y, int id) {
      super(x, y, id, 20, 20, "");
   }

   @Override
   public void render(MinecraftClient client, int mouseX, int mouseY) {
      if (this.visible) {
         client.getTextureManager().bind(ButtonWidget.BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int var5 = 106;
         if (var4) {
            var5 += this.height;
         }

         this.drawTexture(this.x, this.y, 0, var5, this.width, this.height);
      }
   }
}
