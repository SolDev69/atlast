package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.FurnaceMenu;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FurnaceScreen extends InventoryMenuScreen {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");
   private final PlayerInventory playerInventory;
   private Inventory inventory;

   public FurnaceScreen(PlayerInventory playerInventory, Inventory inventory) {
      super(new FurnaceMenu(playerInventory, inventory));
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
      if (FurnaceBlockEntity.isLit(this.inventory)) {
         int var6 = this.m_65omdhiad(13);
         this.drawTexture(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 1);
      }

      int var7 = this.m_62ltnvaao(24);
      this.drawTexture(var4 + 79, var5 + 34, 176, 14, var7 + 1, 16);
   }

   private int m_62ltnvaao(int i) {
      int var2 = this.inventory.getData(2);
      int var3 = this.inventory.getData(3);
      return var3 != 0 && var2 != 0 ? var2 * i / var3 : 0;
   }

   private int m_65omdhiad(int i) {
      int var2 = this.inventory.getData(1);
      if (var2 == 0) {
         var2 = 200;
      }

      return this.inventory.getData(0) * i / var2;
   }
}
