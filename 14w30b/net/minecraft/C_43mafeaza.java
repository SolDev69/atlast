package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_43mafeaza extends ListWidget {
   private final RealmsScrolledSelectionList f_85maijpti;

   public C_43mafeaza(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
      super(MinecraftClient.getInstance(), i, j, k, l, m);
      this.f_85maijpti = realmsScrolledSelectionList;
   }

   @Override
   protected int getEntriesSize() {
      return this.f_85maijpti.getItemCount();
   }

   @Override
   protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
      this.f_85maijpti.selectItem(y, isValid, lastMouseX, lastMouseY);
   }

   @Override
   protected boolean isEntrySelected(int index) {
      return this.f_85maijpti.isSelectedItem(index);
   }

   @Override
   protected void renderBackground() {
      this.f_85maijpti.renderBackground();
   }

   @Override
   protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
      this.f_85maijpti.renderItem(index, x, y, rowHeight, bufferBuilder, mouseX);
   }

   public int m_86vsryogl() {
      return super.width;
   }

   public int m_13icozequ() {
      return super.mouseY;
   }

   public int m_58mgdvadv() {
      return super.mouseX;
   }

   @Override
   protected int getListSize() {
      return this.f_85maijpti.getMaxPosition();
   }

   @Override
   protected int getScrollbarPosition() {
      return this.f_85maijpti.getScrollbarPosition();
   }
}
