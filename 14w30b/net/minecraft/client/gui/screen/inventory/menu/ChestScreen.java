package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.ChestMenu;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChestScreen extends InventoryMenuScreen {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
   private Inventory bottom;
   private Inventory top;
   private int rows;

   public ChestScreen(Inventory bottom, Inventory top) {
      super(new ChestMenu(bottom, top, MinecraftClient.getInstance().player));
      this.bottom = bottom;
      this.top = top;
      this.passEvents = false;
      short var3 = 222;
      int var4 = var3 - 108;
      this.rows = top.getSize() / 9;
      this.backgroundHeight = var4 + this.rows * 18;
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      this.textRenderer.drawWithoutShadow(this.top.getDisplayName().buildString(), 8, 6, 4210752);
      this.textRenderer.drawWithoutShadow(this.bottom.getDisplayName().buildString(), 8, this.backgroundHeight - 96 + 2, 4210752);
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
      this.drawTexture(var4, var5 + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
   }
}
