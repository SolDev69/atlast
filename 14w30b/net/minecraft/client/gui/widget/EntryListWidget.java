package net.minecraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget extends ListWidget {
   public EntryListWidget(MinecraftClient c_13piauvdk, int i, int j, int k, int l, int m) {
      super(c_13piauvdk, i, j, k, l, m);
   }

   @Override
   protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
   }

   @Override
   protected boolean isEntrySelected(int index) {
      return false;
   }

   @Override
   protected void renderBackground() {
   }

   @Override
   protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
      this.getEntry(index).render(index, x, y, this.getRowWidth(), rowHeight, bufferBuilder, mouseX, this.getEntryAt(bufferBuilder, mouseX) == index);
   }

   @Override
   protected void m_68rrjxwti(int i, int j, int k) {
      this.getEntry(i).m_82anuocxe(i, j, k);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      if (this.isMouseInList(mouseY)) {
         int var4 = this.getEntryAt(mouseX, mouseY);
         if (var4 >= 0) {
            int var5 = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
            int var6 = this.yStart + 4 - this.getScrollAmount() + var4 * this.entryHeight + this.headerHeight;
            int var7 = mouseX - var5;
            int var8 = mouseY - var6;
            if (this.getEntry(var4).mouseClicked(var4, mouseX, mouseY, button, var7, var8)) {
               this.setDragging(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseReleased(int mouseX, int mouseY, int button) {
      for(int var4 = 0; var4 < this.getEntriesSize(); ++var4) {
         int var5 = this.xStart + this.width / 2 - this.getRowWidth() / 2 + 2;
         int var6 = this.yStart + 4 - this.getScrollAmount() + var4 * this.entryHeight + this.headerHeight;
         int var7 = mouseX - var5;
         int var8 = mouseY - var6;
         this.getEntry(var4).mouseReleased(var4, mouseX, mouseY, button, var7, var8);
      }

      this.setDragging(true);
      return false;
   }

   public abstract EntryListWidget.Entry getEntry(int index);

   @Environment(EnvType.CLIENT)
   public interface Entry {
      void m_82anuocxe(int i, int j, int k);

      void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY);

      boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY);

      void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY);
   }
}
