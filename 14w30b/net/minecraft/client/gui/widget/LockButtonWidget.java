package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LockButtonWidget extends ButtonWidget {
   private boolean locked = false;

   public LockButtonWidget(int x, int y, int id) {
      super(x, y, id, 20, 20, "");
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   @Override
   public void render(MinecraftClient client, int mouseX, int mouseY) {
      if (this.visible) {
         client.getTextureManager().bind(ButtonWidget.BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         LockButtonWidget.Icon var5;
         if (this.locked) {
            if (!this.active) {
               var5 = LockButtonWidget.Icon.LOCKED_DISABLED;
            } else if (var4) {
               var5 = LockButtonWidget.Icon.LOCKED_HOVER;
            } else {
               var5 = LockButtonWidget.Icon.LOCKED;
            }
         } else if (!this.active) {
            var5 = LockButtonWidget.Icon.UNLOCKED_DISABLED;
         } else if (var4) {
            var5 = LockButtonWidget.Icon.UNLOCKED_HOVER;
         } else {
            var5 = LockButtonWidget.Icon.UNLOCKED;
         }

         this.drawTexture(this.x, this.y, var5.getU(), var5.getV(), this.width, this.height);
      }
   }

   @Environment(EnvType.CLIENT)
   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int u;
      private final int v;

      private Icon(int u, int v) {
         this.u = u;
         this.v = v;
      }

      public int getU() {
         return this.u;
      }

      public int getV() {
         return this.v;
      }
   }
}
