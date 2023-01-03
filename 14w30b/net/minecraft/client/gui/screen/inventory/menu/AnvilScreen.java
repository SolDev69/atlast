package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.AnvilMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.InventoryMenuListener;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.Charsets;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends InventoryMenuScreen implements InventoryMenuListener {
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
   private AnvilMenu anvilScreenHandler;
   private TextFieldWidget renameTextField;
   private PlayerInventory inventory;

   public AnvilScreen(PlayerInventory playerInventory, World world) {
      super(new AnvilMenu(playerInventory, world, MinecraftClient.getInstance().player));
      this.inventory = playerInventory;
      this.anvilScreenHandler = (AnvilMenu)this.menu;
   }

   @Override
   public void init() {
      super.init();
      Keyboard.enableRepeatEvents(true);
      int var1 = (this.titleWidth - this.backgroundWidth) / 2;
      int var2 = (this.height - this.backgroundHeight) / 2;
      this.renameTextField = new TextFieldWidget(0, this.textRenderer, var1 + 62, var2 + 24, 103, 12);
      this.renameTextField.setEditableColor(-1);
      this.renameTextField.setUneditableColor(-1);
      this.renameTextField.setHasBorder(false);
      this.renameTextField.setMaxLength(40);
      this.menu.removeListener(this);
      this.menu.addListener(this);
   }

   @Override
   public void removed() {
      super.removed();
      Keyboard.enableRepeatEvents(false);
      this.menu.removeListener(this);
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      this.textRenderer.drawWithoutShadow(I18n.translate("container.repair"), 60, 6, 4210752);
      if (this.anvilScreenHandler.repairCost > 0) {
         int var3 = 8453920;
         boolean var4 = true;
         String var5 = I18n.translate("container.repair.cost", this.anvilScreenHandler.repairCost);
         if (this.anvilScreenHandler.repairCost >= 40 && !this.client.player.abilities.creativeMode) {
            var5 = I18n.translate("container.repair.expensive");
            var3 = 16736352;
         } else if (!this.anvilScreenHandler.getSlot(2).hasStack()) {
            var4 = false;
         } else if (!this.anvilScreenHandler.getSlot(2).canPickUp(this.inventory.player)) {
            var3 = 16736352;
         }

         if (var4) {
            int var6 = 0xFF000000 | (var3 & 16579836) >> 2 | var3 & 0xFF000000;
            int var7 = this.backgroundWidth - 8 - this.textRenderer.getStringWidth(var5);
            byte var8 = 67;
            if (this.textRenderer.getUnicode()) {
               fill(var7 - 3, var8 - 2, this.backgroundWidth - 7, var8 + 10, -16777216);
               fill(var7 - 2, var8 - 1, this.backgroundWidth - 8, var8 + 9, -12895429);
            } else {
               this.textRenderer.drawWithoutShadow(var5, var7, var8 + 1, var6);
               this.textRenderer.drawWithoutShadow(var5, var7 + 1, var8, var6);
               this.textRenderer.drawWithoutShadow(var5, var7 + 1, var8 + 1, var6);
            }

            this.textRenderer.drawWithoutShadow(var5, var7, var8, var3);
         }
      }

      GlStateManager.enableLighting();
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (this.renameTextField.keyPressed(chr, key)) {
         this.sendRenameUpdates();
      } else {
         super.keyPressed(chr, key);
      }
   }

   private void sendRenameUpdates() {
      String var1 = this.renameTextField.getText();
      InventorySlot var2 = this.anvilScreenHandler.getSlot(0);
      if (var2 != null && var2.hasStack() && !var2.getStack().hasCustomHoverName() && var1.equals(var2.getStack().getHoverName())) {
         var1 = "";
      }

      this.anvilScreenHandler.setItemName(var1);
      this.client.player.networkHandler.sendPacket(new CustomPayloadC2SPacket("MC|ItemName", var1.getBytes(Charsets.UTF_8)));
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.renameTextField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      super.render(mouseX, mouseY, tickDelta);
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      this.renameTextField.render();
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      this.drawTexture(var4 + 59, var5 + 20, 0, this.backgroundHeight + (this.anvilScreenHandler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
      if ((this.anvilScreenHandler.getSlot(0).hasStack() || this.anvilScreenHandler.getSlot(1).hasStack()) && !this.anvilScreenHandler.getSlot(2).hasStack()) {
         this.drawTexture(var4 + 99, var5 + 45, this.backgroundWidth, 0, 28, 21);
      }
   }

   @Override
   public void updateMenu(InventoryMenu menu, List stacks) {
      this.onSlotChanged(menu, 0, menu.getSlot(0).getStack());
   }

   @Override
   public void onSlotChanged(InventoryMenu menu, int id, ItemStack stack) {
      if (id == 0) {
         this.renameTextField.setText(stack == null ? "" : stack.getHoverName());
         this.renameTextField.setEditable(stack != null);
         if (stack != null) {
            this.sendRenameUpdates();
         }
      }
   }

   @Override
   public void onDataChanged(InventoryMenu menu, int id, int value) {
   }

   @Override
   public void updateData(InventoryMenu menu, Inventory inventory) {
   }
}
