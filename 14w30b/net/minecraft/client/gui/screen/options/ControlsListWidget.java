package net.minecraft.client.gui.screen.options;

import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class ControlsListWidget extends EntryListWidget {
   private final ControlsOptionsScreen parent;
   private final MinecraftClient minecraftClient;
   private final EntryListWidget.Entry[] entries;
   private int maxKeyNameLength = 0;

   public ControlsListWidget(ControlsOptionsScreen parent, MinecraftClient client) {
      super(client, parent.titleWidth, parent.height, 63, parent.height - 32, 20);
      this.parent = parent;
      this.minecraftClient = client;
      KeyBinding[] var3 = (KeyBinding[])ArrayUtils.clone(client.options.ingameKeys);
      this.entries = new EntryListWidget.Entry[var3.length + KeyBinding.getCategories().size()];
      Arrays.sort((Object[])var3);
      int var4 = 0;
      String var5 = null;

      for(KeyBinding var9 : var3) {
         String var10 = var9.getCategory();
         if (!var10.equals(var5)) {
            var5 = var10;
            this.entries[var4++] = new ControlsListWidget.CategoryEntry(var10);
         }

         int var11 = client.textRenderer.getStringWidth(I18n.translate(var9.getName()));
         if (var11 > this.maxKeyNameLength) {
            this.maxKeyNameLength = var11;
         }

         this.entries[var4++] = new ControlsListWidget.KeyBindingEntry(var9);
      }
   }

   @Override
   protected int getEntriesSize() {
      return this.entries.length;
   }

   @Override
   public EntryListWidget.Entry getEntry(int index) {
      return this.entries[index];
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15;
   }

   @Override
   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   @Environment(EnvType.CLIENT)
   public class CategoryEntry implements EntryListWidget.Entry {
      private final String name;
      private final int nameWidth;

      public CategoryEntry(String string) {
         this.name = I18n.translate(string);
         this.nameWidth = ControlsListWidget.this.minecraftClient.textRenderer.getStringWidth(this.name);
      }

      @Override
      public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
         ControlsListWidget.this.minecraftClient
            .textRenderer
            .drawWithoutShadow(
               this.name,
               ControlsListWidget.this.minecraftClient.currentScreen.titleWidth / 2 - this.nameWidth / 2,
               y + height - ControlsListWidget.this.minecraftClient.textRenderer.fontHeight - 1,
               16777215
            );
      }

      @Override
      public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         return false;
      }

      @Override
      public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
      }

      @Override
      public void m_82anuocxe(int i, int j, int k) {
      }
   }

   @Environment(EnvType.CLIENT)
   public class KeyBindingEntry implements EntryListWidget.Entry {
      private final KeyBinding keyBinding;
      private final String name;
      private final ButtonWidget keyBindingButton;
      private final ButtonWidget resetButton;

      private KeyBindingEntry(KeyBinding keyBinding) {
         this.keyBinding = keyBinding;
         this.name = I18n.translate(keyBinding.getName());
         this.keyBindingButton = new ButtonWidget(0, 0, 0, 75, 18, I18n.translate(keyBinding.getName()));
         this.resetButton = new ButtonWidget(0, 0, 0, 50, 18, I18n.translate("controls.reset"));
      }

      @Override
      public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
         boolean var9 = ControlsListWidget.this.parent.selectedKeyBinding == this.keyBinding;
         ControlsListWidget.this.minecraftClient
            .textRenderer
            .drawWithoutShadow(
               this.name,
               x + 90 - ControlsListWidget.this.maxKeyNameLength,
               y + height / 2 - ControlsListWidget.this.minecraftClient.textRenderer.fontHeight / 2,
               16777215
            );
         this.resetButton.x = x + 190;
         this.resetButton.y = y;
         this.resetButton.active = this.keyBinding.getKeyCode() != this.keyBinding.getDefaultKeyCode();
         this.resetButton.render(ControlsListWidget.this.minecraftClient, bufferBuilder, mouseX);
         this.keyBindingButton.x = x + 105;
         this.keyBindingButton.y = y;
         this.keyBindingButton.message = GameOptions.getKeyName(this.keyBinding.getKeyCode());
         boolean var10 = false;
         if (this.keyBinding.getKeyCode() != 0) {
            for(KeyBinding var14 : ControlsListWidget.this.minecraftClient.options.ingameKeys) {
               if (var14 != this.keyBinding && var14.getKeyCode() == this.keyBinding.getKeyCode()) {
                  var10 = true;
                  break;
               }
            }
         }

         if (var9) {
            this.keyBindingButton.message = Formatting.WHITE + "> " + Formatting.YELLOW + this.keyBindingButton.message + Formatting.WHITE + " <";
         } else if (var10) {
            this.keyBindingButton.message = Formatting.RED + this.keyBindingButton.message;
         }

         this.keyBindingButton.render(ControlsListWidget.this.minecraftClient, bufferBuilder, mouseX);
      }

      @Override
      public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         if (this.keyBindingButton.isMouseOver(ControlsListWidget.this.minecraftClient, mouseX, mouseY)) {
            ControlsListWidget.this.parent.selectedKeyBinding = this.keyBinding;
            return true;
         } else if (this.resetButton.isMouseOver(ControlsListWidget.this.minecraftClient, mouseX, mouseY)) {
            ControlsListWidget.this.minecraftClient.options.setKeyCode(this.keyBinding, this.keyBinding.getDefaultKeyCode());
            KeyBinding.updateKeyCodeMap();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         this.keyBindingButton.mouseReleased(mouseX, mouseY);
         this.resetButton.mouseReleased(mouseX, mouseY);
      }

      @Override
      public void m_82anuocxe(int i, int j, int k) {
      }
   }
}
