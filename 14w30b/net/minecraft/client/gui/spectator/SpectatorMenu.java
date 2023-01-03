package net.minecraft.client.gui.spectator;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpectatorMenu {
   private static final SpectatorMenuItem CLOSE_HUD = new SpectatorMenu.CloseMenuItem();
   private static final SpectatorMenuItem SCROLL_LEFT = new SpectatorMenu.PaginationMenuItem(-1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new SpectatorMenu.PaginationMenuItem(1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new SpectatorMenu.PaginationMenuItem(1, false);
   public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem() {
      @Override
      public void select(SpectatorMenu hud) {
      }

      @Override
      public Text getDisplayName() {
         return new LiteralText("");
      }

      @Override
      public void render(float tickDelta, int slot) {
      }

      @Override
      public boolean isEnabled() {
         return false;
      }
   };
   private final SpectatorMenuListener listener;
   private final List previousPages = Lists.newArrayList();
   private SpectatorMenuCategory category;
   private int selectedSlot = -1;
   private int page;

   public SpectatorMenu(SpectatorMenuListener listener) {
      this.category = new RootCategory();
      this.listener = listener;
   }

   public SpectatorMenuItem getItem(int slot) {
      int var2 = slot + this.page * 6;
      if (this.page > 0 && slot == 0) {
         return SCROLL_LEFT;
      } else if (slot == 7) {
         return var2 < this.category.getItems().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED;
      } else if (slot == 8) {
         return CLOSE_HUD;
      } else {
         return var2 >= 0 && var2 < this.category.getItems().size()
            ? (SpectatorMenuItem)Objects.firstNonNull(this.category.getItems().get(var2), EMPTY_SLOT)
            : EMPTY_SLOT;
      }
   }

   public List getItems() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 <= 8; ++var2) {
         var1.add(this.getItem(var2));
      }

      return var1;
   }

   public SpectatorMenuItem getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public SpectatorMenuCategory getCategory() {
      return this.category;
   }

   public void selectSlot(int slot) {
      SpectatorMenuItem var2 = this.getItem(slot);
      if (var2 != EMPTY_SLOT) {
         if (this.selectedSlot == slot && var2.isEnabled()) {
            var2.select(this);
         } else {
            this.selectedSlot = slot;
         }
      }
   }

   public void close() {
      this.listener.onSpectatorMenuClosed(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void setCategory(SpectatorMenuCategory category) {
      this.previousPages.add(this.getSelectedPage());
      this.category = category;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorMenuPage getSelectedPage() {
      return new SpectatorMenuPage(this.category, this.getItems(), this.selectedSlot);
   }

   @Environment(EnvType.CLIENT)
   static class CloseMenuItem implements SpectatorMenuItem {
      private CloseMenuItem() {
      }

      @Override
      public void select(SpectatorMenu hud) {
         hud.close();
      }

      @Override
      public Text getDisplayName() {
         return new LiteralText("Close menu");
      }

      @Override
      public void render(float tickDelta, int slot) {
         MinecraftClient.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_WIDGETS_TEXTURE);
         GuiElement.drawTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
      }

      @Override
      public boolean isEnabled() {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   static class PaginationMenuItem implements SpectatorMenuItem {
      private final int slot;
      private final boolean enabled;

      public PaginationMenuItem(int slot, boolean enabled) {
         this.slot = slot;
         this.enabled = enabled;
      }

      @Override
      public void select(SpectatorMenu hud) {
         hud.page += this.slot;
      }

      @Override
      public Text getDisplayName() {
         return this.slot < 0 ? new LiteralText("Previous Page") : new LiteralText("Next Page");
      }

      @Override
      public void render(float tickDelta, int slot) {
         MinecraftClient.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_WIDGETS_TEXTURE);
         if (this.slot < 0) {
            GuiElement.drawTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         } else {
            GuiElement.drawTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         }
      }

      @Override
      public boolean isEnabled() {
         return this.enabled;
      }
   }
}
