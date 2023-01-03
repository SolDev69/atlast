package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.DispenserMenu;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DispenserScreen extends InventoryMenuScreen {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");
   private final PlayerInventory playerInventory;
   public Inventory inventory;

   public DispenserScreen(PlayerInventory playerInventory, Inventory inventory) {
      super(new DispenserMenu(playerInventory, inventory));
      this.playerInventory = playerInventory;
      this.inventory = inventory;
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      String var3 = this.inventory.getDisplayName().buildString();
      this.textRenderer.drawWithoutShadow(var3, this.backgroundWidth / 2 - this.textRenderer.getStringWidth(var3) / 2, 6, 4210752);
      this.textRenderer.drawWithoutShadow(this.playerInventory.getDisplayName().buildString(), 8, this.backgroundHeight - 96 + 2, 4210752);
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
   }
}
