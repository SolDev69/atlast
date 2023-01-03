package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OptionListWidget extends EntryListWidget {
   private final List entries = Lists.newArrayList();

   public OptionListWidget(MinecraftClient client, int width, int height, int yStart, int yEnd, int entryHeight, GameOptions.Option... options) {
      super(client, width, height, yStart, yEnd, entryHeight);
      this.centerAlongY = false;

      for(int var8 = 0; var8 < options.length; var8 += 2) {
         GameOptions.Option var9 = options[var8];
         GameOptions.Option var10 = var8 < options.length - 1 ? options[var8 + 1] : null;
         ButtonWidget var11 = this.createWidget(client, width / 2 - 155, 0, var9);
         ButtonWidget var12 = this.createWidget(client, width / 2 - 155 + 160, 0, var10);
         this.entries.add(new OptionListWidget.Entry(var11, var12));
      }
   }

   private ButtonWidget createWidget(MinecraftClient client, int x, int y, GameOptions.Option option) {
      if (option == null) {
         return null;
      } else {
         int var5 = option.getId();
         return (ButtonWidget)(option.isFloatOption()
            ? new OptionSliderWidget(var5, x, y, option)
            : new OptionButtonWidget(var5, x, y, option, client.options.getValueAsString(option)));
      }
   }

   public OptionListWidget.Entry getEntry(int i) {
      return (OptionListWidget.Entry)this.entries.get(i);
   }

   @Override
   protected int getEntriesSize() {
      return this.entries.size();
   }

   @Override
   public int getRowWidth() {
      return 400;
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Environment(EnvType.CLIENT)
   public static class Entry implements EntryListWidget.Entry {
      private final MinecraftClient client = MinecraftClient.getInstance();
      private final ButtonWidget left;
      private final ButtonWidget right;

      public Entry(ButtonWidget left, ButtonWidget right) {
         this.left = left;
         this.right = right;
      }

      @Override
      public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
         if (this.left != null) {
            this.left.y = y;
            this.left.render(this.client, bufferBuilder, mouseX);
         }

         if (this.right != null) {
            this.right.y = y;
            this.right.render(this.client, bufferBuilder, mouseX);
         }
      }

      @Override
      public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         if (this.left.isMouseOver(this.client, mouseX, mouseY)) {
            if (this.left instanceof OptionButtonWidget) {
               this.client.options.setValue(((OptionButtonWidget)this.left).getOption(), 1);
               this.left.message = this.client.options.getValueAsString(GameOptions.Option.byId(this.left.id));
            }

            return true;
         } else if (this.right != null && this.right.isMouseOver(this.client, mouseX, mouseY)) {
            if (this.right instanceof OptionButtonWidget) {
               this.client.options.setValue(((OptionButtonWidget)this.right).getOption(), 1);
               this.right.message = this.client.options.getValueAsString(GameOptions.Option.byId(this.right.id));
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         if (this.left != null) {
            this.left.mouseReleased(mouseX, mouseY);
         }

         if (this.right != null) {
            this.right.mouseReleased(mouseX, mouseY);
         }
      }

      @Override
      public void m_82anuocxe(int i, int j, int k) {
      }
   }
}
