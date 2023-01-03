package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnooperScreen extends Screen {
   private final Screen parent;
   private final GameOptions options;
   private final List snooperKeys = Lists.newArrayList();
   private final List snooperValues = Lists.newArrayList();
   private String title;
   private String[] description;
   private SnooperScreen.SnooperListWidget snooperList;
   private ButtonWidget snooperToggleButton;

   public SnooperScreen(Screen parent, GameOptions options) {
      this.parent = parent;
      this.options = options;
   }

   @Override
   public void init() {
      this.title = I18n.translate("options.snooper.title");
      String var1 = I18n.translate("options.snooper.desc");
      ArrayList var2 = Lists.newArrayList();

      for(String var4 : this.textRenderer.wrapLines(var1, this.titleWidth - 30)) {
         var2.add(var4);
      }

      this.description = var2.toArray(new String[0]);
      this.snooperKeys.clear();
      this.snooperValues.clear();
      this.buttons
         .add(
            this.snooperToggleButton = new ButtonWidget(
               1, this.titleWidth / 2 - 152, this.height - 30, 150, 20, this.options.getValueAsString(GameOptions.Option.SNOOPER_ENABLED)
            )
         );
      this.buttons.add(new ButtonWidget(2, this.titleWidth / 2 + 2, this.height - 30, 150, 20, I18n.translate("gui.done")));
      boolean var6 = this.client.getServer() != null && this.client.getServer().getSnooper() != null;

      for(Entry var5 : new TreeMap(this.client.getSnooper().getSnooperDataAsLinkedHashmap()).entrySet()) {
         this.snooperKeys.add((var6 ? "C " : "") + (String)var5.getKey());
         this.snooperValues.add(this.textRenderer.trimToWidth((String)var5.getValue(), this.titleWidth - 220));
      }

      if (var6) {
         for(Entry var9 : new TreeMap(this.client.getServer().getSnooper().getSnooperDataAsLinkedHashmap()).entrySet()) {
            this.snooperKeys.add("S " + (String)var9.getKey());
            this.snooperValues.add(this.textRenderer.trimToWidth((String)var9.getValue(), this.titleWidth - 220));
         }
      }

      this.snooperList = new SnooperScreen.SnooperListWidget();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.snooperList.m_94jnhyuiz();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 2) {
            this.options.save();
            this.options.save();
            this.client.openScreen(this.parent);
         }

         if (buttonWidget.id == 1) {
            this.options.setValue(GameOptions.Option.SNOOPER_ENABLED, 1);
            this.snooperToggleButton.message = this.options.getValueAsString(GameOptions.Option.SNOOPER_ENABLED);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.snooperList.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 8, 16777215);
      int var4 = 22;

      for(String var8 : this.description) {
         this.drawCenteredString(this.textRenderer, var8, this.titleWidth / 2, var4, 8421504);
         var4 += this.textRenderer.fontHeight;
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   @Environment(EnvType.CLIENT)
   class SnooperListWidget extends ListWidget {
      public SnooperListWidget() {
         super(
            SnooperScreen.this.client,
            SnooperScreen.this.titleWidth,
            SnooperScreen.this.height,
            80,
            SnooperScreen.this.height - 40,
            SnooperScreen.this.textRenderer.fontHeight + 1
         );
      }

      @Override
      protected int getEntriesSize() {
         return SnooperScreen.this.snooperKeys.size();
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
         SnooperScreen.this.textRenderer.drawWithoutShadow((String)SnooperScreen.this.snooperKeys.get(index), 10, y, 16777215);
         SnooperScreen.this.textRenderer.drawWithoutShadow((String)SnooperScreen.this.snooperValues.get(index), 230, y, 16777215);
      }

      @Override
      protected int getScrollbarPosition() {
         return this.width - 10;
      }
   }
}
