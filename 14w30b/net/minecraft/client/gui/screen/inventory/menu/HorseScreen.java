package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.HorseMenu;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class HorseScreen extends InventoryMenuScreen {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
   private Inventory armorInventory;
   private Inventory chestInventory;
   private HorseBaseEntity horse;
   private float mouseX;
   private float mouseY;

   public HorseScreen(Inventory armorInventory, Inventory chestInventory, HorseBaseEntity horse) {
      super(new HorseMenu(armorInventory, chestInventory, horse, MinecraftClient.getInstance().player));
      this.armorInventory = armorInventory;
      this.chestInventory = chestInventory;
      this.horse = horse;
      this.passEvents = false;
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      this.textRenderer.drawWithoutShadow(this.chestInventory.getDisplayName().buildString(), 8, 6, 4210752);
      this.textRenderer.drawWithoutShadow(this.armorInventory.getDisplayName().buildString(), 8, this.backgroundHeight - 96 + 2, 4210752);
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      if (this.horse.hasChest()) {
         this.drawTexture(var4 + 79, var5 + 17, 0, this.backgroundHeight, 90, 54);
      }

      if (this.horse.drawHoverEffect()) {
         this.drawTexture(var4 + 7, var5 + 35, 0, this.backgroundHeight + 54, 18, 18);
      }

      SurvivalInventoryScreen.renderEntity(var4 + 51, var5 + 60, 17, (float)(var4 + 51) - this.mouseX, (float)(var5 + 75 - 50) - this.mouseY, this.horse);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.mouseX = (float)mouseX;
      this.mouseY = (float)mouseY;
      super.render(mouseX, mouseY, tickDelta);
   }
}
