package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.BrewingStandMenu;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BrewingStandScreen extends InventoryMenuScreen {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
   private final PlayerInventory playerInventory;
   private Inventory inventory;

   public BrewingStandScreen(PlayerInventory playerInventory, Inventory inventory) {
      super(new BrewingStandMenu(playerInventory, inventory));
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
      int var6 = this.inventory.getData(0);
      if (var6 > 0) {
         int var7 = (int)(28.0F * (1.0F - (float)var6 / 400.0F));
         if (var7 > 0) {
            this.drawTexture(var4 + 97, var5 + 16, 176, 0, 9, var7);
         }

         int var8 = var6 / 2 % 7;
         switch(var8) {
            case 0:
               var7 = 29;
               break;
            case 1:
               var7 = 24;
               break;
            case 2:
               var7 = 20;
               break;
            case 3:
               var7 = 16;
               break;
            case 4:
               var7 = 11;
               break;
            case 5:
               var7 = 6;
               break;
            case 6:
               var7 = 0;
         }

         if (var7 > 0) {
            this.drawTexture(var4 + 65, var5 + 14 + 29 - var7, 185, 29 - var7, 12, var7);
         }
      }
   }
}
