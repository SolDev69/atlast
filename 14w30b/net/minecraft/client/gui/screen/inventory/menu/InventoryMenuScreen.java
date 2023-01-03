package net.minecraft.client.gui.screen.inventory.menu;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public abstract class InventoryMenuScreen extends Screen {
   protected static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
   protected int backgroundWidth = 176;
   protected int backgroundHeight = 166;
   public InventoryMenu menu;
   protected int x;
   protected int y;
   private InventorySlot hoveredSlot;
   private InventorySlot touchDragSlotStart;
   private boolean touchIsRightClickDrag;
   private ItemStack touchDragStack;
   private int touchDropX;
   private int touchDropY;
   private InventorySlot touchDropOriginSlot;
   private long touchDropTime;
   private ItemStack touchDropReturningStack;
   private InventorySlot draggedInvSlot;
   private long touchDropTimer;
   protected final Set draggedInvSlots = Sets.newHashSet();
   protected boolean isDraggingStack;
   private int clickDragMode;
   private int clickDragButton;
   private boolean cancelNextMouseRelease;
   private int draggedStackRemainder;
   private long lastButtonClickTime;
   private InventorySlot clickedInvSlot;
   private int lastClickedButton;
   private boolean isDoubleClicking;
   private ItemStack shiftClickedStack;

   public InventoryMenuScreen(InventoryMenu menu) {
      this.menu = menu;
      this.cancelNextMouseRelease = true;
   }

   @Override
   public void init() {
      super.init();
      this.client.player.menu = this.menu;
      this.x = (this.titleWidth - this.backgroundWidth) / 2;
      this.y = (this.height - this.backgroundHeight) / 2;
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      int var4 = this.x;
      int var5 = this.y;
      this.drawBackground(tickDelta, mouseX, mouseY);
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      super.render(mouseX, mouseY, tickDelta);
      Lighting.turnOnGui();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var4, (float)var5, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.hoveredSlot = null;
      short var6 = 240;
      short var7 = 240;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var6 / 1.0F, (float)var7 / 1.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      for(int var8 = 0; var8 < this.menu.slots.size(); ++var8) {
         InventorySlot var9 = (InventorySlot)this.menu.slots.get(var8);
         this.drawSlot(var9);
         if (this.isMouseOverSlot(var9, mouseX, mouseY) && var9.isActive()) {
            this.hoveredSlot = var9;
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            int var10 = var9.x;
            int var11 = var9.y;
            GlStateManager.colorMask(true, true, true, false);
            this.fillGradient(var10, var11, var10 + 16, var11 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
         }
      }

      Lighting.turnOff();
      this.drawForeground(mouseX, mouseY);
      Lighting.turnOnGui();
      PlayerInventory var15 = this.client.player.inventory;
      ItemStack var16 = this.touchDragStack == null ? var15.getCursorStack() : this.touchDragStack;
      if (var16 != null) {
         byte var17 = 8;
         int var20 = this.touchDragStack == null ? 8 : 16;
         String var12 = null;
         if (this.touchDragStack != null && this.touchIsRightClickDrag) {
            var16 = var16.copy();
            var16.size = MathHelper.ceil((float)var16.size / 2.0F);
         } else if (this.isDraggingStack && this.draggedInvSlots.size() > 1) {
            var16 = var16.copy();
            var16.size = this.draggedStackRemainder;
            if (var16.size == 0) {
               var12 = "" + Formatting.YELLOW + "0";
            }
         }

         this.drawItem(var16, mouseX - var4 - var17, mouseY - var5 - var20, var12);
      }

      if (this.touchDropReturningStack != null) {
         float var18 = (float)(MinecraftClient.getTime() - this.touchDropTime) / 100.0F;
         if (var18 >= 1.0F) {
            var18 = 1.0F;
            this.touchDropReturningStack = null;
         }

         int var21 = this.touchDropOriginSlot.x - this.touchDropX;
         int var22 = this.touchDropOriginSlot.y - this.touchDropY;
         int var13 = this.touchDropX + (int)((float)var21 * var18);
         int var14 = this.touchDropY + (int)((float)var22 * var18);
         this.drawItem(this.touchDropReturningStack, var13, var14, null);
      }

      GlStateManager.popMatrix();
      if (var15.getCursorStack() == null && this.hoveredSlot != null && this.hoveredSlot.hasStack()) {
         ItemStack var19 = this.hoveredSlot.getStack();
         this.renderTooltip(var19, mouseX, mouseY);
      }

      GlStateManager.enableLighting();
      GlStateManager.disableDepth();
      Lighting.turnOn();
   }

   private void drawItem(ItemStack stack, int x, int y, String itemInfo) {
      GlStateManager.translatef(0.0F, 0.0F, 32.0F);
      this.drawOffset = 200.0F;
      this.itemRenderer.zOffset = 200.0F;
      this.itemRenderer.renderGuiItem(stack, x, y);
      this.itemRenderer.renderGuiItemDecorations(this.textRenderer, stack, x, y - (this.touchDragStack == null ? 0 : 8), itemInfo);
      this.drawOffset = 0.0F;
      this.itemRenderer.zOffset = 0.0F;
   }

   protected void drawForeground(int mouseX, int mouseY) {
   }

   protected abstract void drawBackground(float tickDelta, int mouseX, int mouseY);

   private void drawSlot(InventorySlot invSlot) {
      int var2 = invSlot.x;
      int var3 = invSlot.y;
      ItemStack var4 = invSlot.getStack();
      boolean var5 = false;
      boolean var6 = invSlot == this.touchDragSlotStart && this.touchDragStack != null && !this.touchIsRightClickDrag;
      ItemStack var7 = this.client.player.inventory.getCursorStack();
      String var8 = null;
      if (invSlot == this.touchDragSlotStart && this.touchDragStack != null && this.touchIsRightClickDrag && var4 != null) {
         var4 = var4.copy();
         var4.size /= 2;
      } else if (this.isDraggingStack && this.draggedInvSlots.contains(invSlot) && var7 != null) {
         if (this.draggedInvSlots.size() == 1) {
            return;
         }

         if (InventoryMenu.canClickDragInto(invSlot, var7, true) && this.menu.canClickDragInto(invSlot)) {
            var4 = var7.copy();
            var5 = true;
            InventoryMenu.updateClickDragStackSize(this.draggedInvSlots, this.clickDragMode, var4, invSlot.getStack() == null ? 0 : invSlot.getStack().size);
            if (var4.size > var4.getMaxSize()) {
               var8 = Formatting.YELLOW + "" + var4.getMaxSize();
               var4.size = var4.getMaxSize();
            }

            if (var4.size > invSlot.getMaxStackSize(var4)) {
               var8 = Formatting.YELLOW + "" + invSlot.getMaxStackSize(var4);
               var4.size = invSlot.getMaxStackSize(var4);
            }
         } else {
            this.draggedInvSlots.remove(invSlot);
            this.updateDraggedStackRemainder();
         }
      }

      this.drawOffset = 100.0F;
      this.itemRenderer.zOffset = 100.0F;
      if (var4 == null) {
         String var9 = invSlot.getTexture();
         if (var9 != null) {
            TextureAtlasSprite var10 = this.client.getSpriteAtlasTexture().getSprite(var9);
            GlStateManager.disableLighting();
            this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
            this.drawSprite(var2, var3, var10, 16, 16);
            GlStateManager.enableLighting();
            var6 = true;
         }
      }

      if (!var6) {
         if (var5) {
            fill(var2, var3, var2 + 16, var3 + 16, -2130706433);
         }

         GlStateManager.disableDepth();
         this.itemRenderer.renderGuiItem(var4, var2, var3);
         this.itemRenderer.renderGuiItemDecorations(this.textRenderer, var4, var2, var3, var8);
      }

      this.itemRenderer.zOffset = 0.0F;
      this.drawOffset = 0.0F;
   }

   private void updateDraggedStackRemainder() {
      ItemStack var1 = this.client.player.inventory.getCursorStack();
      if (var1 != null && this.isDraggingStack) {
         this.draggedStackRemainder = var1.size;

         for(InventorySlot var3 : this.draggedInvSlots) {
            ItemStack var4 = var1.copy();
            int var5 = var3.getStack() == null ? 0 : var3.getStack().size;
            InventoryMenu.updateClickDragStackSize(this.draggedInvSlots, this.clickDragMode, var4, var5);
            if (var4.size > var4.getMaxSize()) {
               var4.size = var4.getMaxSize();
            }

            if (var4.size > var3.getMaxStackSize(var4)) {
               var4.size = var3.getMaxStackSize(var4);
            }

            this.draggedStackRemainder -= var4.size - var5;
         }
      }
   }

   private InventorySlot getHoveredSlot(int mouseX, int mouseY) {
      for(int var3 = 0; var3 < this.menu.slots.size(); ++var3) {
         InventorySlot var4 = (InventorySlot)this.menu.slots.get(var3);
         if (this.isMouseOverSlot(var4, mouseX, mouseY)) {
            return var4;
         }
      }

      return null;
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      boolean var4 = mouseButton == this.client.options.pickItemKey.getKeyCode() + 100;
      InventorySlot var5 = this.getHoveredSlot(mouseX, mouseY);
      long var6 = MinecraftClient.getTime();
      this.isDoubleClicking = this.clickedInvSlot == var5 && var6 - this.lastButtonClickTime < 250L && this.lastClickedButton == mouseButton;
      this.cancelNextMouseRelease = false;
      if (mouseButton == 0 || mouseButton == 1 || var4) {
         int var8 = this.x;
         int var9 = this.y;
         boolean var10 = mouseX < var8 || mouseY < var9 || mouseX >= var8 + this.backgroundWidth || mouseY >= var9 + this.backgroundHeight;
         int var11 = -1;
         if (var5 != null) {
            var11 = var5.id;
         }

         if (var10) {
            var11 = -999;
         }

         if (this.client.options.touchscreen && var10 && this.client.player.inventory.getCursorStack() == null) {
            this.client.openScreen(null);
            return;
         }

         if (var11 != -1) {
            if (this.client.options.touchscreen) {
               if (var5 != null && var5.hasStack()) {
                  this.touchDragSlotStart = var5;
                  this.touchDragStack = null;
                  this.touchIsRightClickDrag = mouseButton == 1;
               } else {
                  this.touchDragSlotStart = null;
               }
            } else if (!this.isDraggingStack) {
               if (this.client.player.inventory.getCursorStack() == null) {
                  if (mouseButton == this.client.options.pickItemKey.getKeyCode() + 100) {
                     this.clickSlot(var5, var11, mouseButton, 3);
                  } else {
                     boolean var12 = var11 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                     byte var13 = 0;
                     if (var12) {
                        this.shiftClickedStack = var5 != null && var5.hasStack() ? var5.getStack() : null;
                        var13 = 1;
                     } else if (var11 == -999) {
                        var13 = 4;
                     }

                     this.clickSlot(var5, var11, mouseButton, var13);
                  }

                  this.cancelNextMouseRelease = true;
               } else {
                  this.isDraggingStack = true;
                  this.clickDragButton = mouseButton;
                  this.draggedInvSlots.clear();
                  if (mouseButton == 0) {
                     this.clickDragMode = 0;
                  } else if (mouseButton == 1) {
                     this.clickDragMode = 1;
                  } else if (mouseButton == this.client.options.pickItemKey.getKeyCode() + 100) {
                     this.clickDragMode = 2;
                  }
               }
            }
         }
      }

      this.clickedInvSlot = var5;
      this.lastButtonClickTime = var6;
      this.lastClickedButton = mouseButton;
   }

   @Override
   protected void mouseDragged(int mouseX, int mouseY, int mouseButton, long duration) {
      InventorySlot var6 = this.getHoveredSlot(mouseX, mouseY);
      ItemStack var7 = this.client.player.inventory.getCursorStack();
      if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
         if (mouseButton == 0 || mouseButton == 1) {
            if (this.touchDragStack == null) {
               if (var6 != this.touchDragSlotStart) {
                  this.touchDragStack = this.touchDragSlotStart.getStack().copy();
               }
            } else if (this.touchDragStack.size > 1 && var6 != null && InventoryMenu.canClickDragInto(var6, this.touchDragStack, false)) {
               long var8 = MinecraftClient.getTime();
               if (this.draggedInvSlot == var6) {
                  if (var8 - this.touchDropTimer > 500L) {
                     this.clickSlot(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, 0);
                     this.clickSlot(var6, var6.id, 1, 0);
                     this.clickSlot(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, 0);
                     this.touchDropTimer = var8 + 750L;
                     --this.touchDragStack.size;
                  }
               } else {
                  this.draggedInvSlot = var6;
                  this.touchDropTimer = var8;
               }
            }
         }
      } else if (this.isDraggingStack
         && var6 != null
         && var7 != null
         && var7.size > this.draggedInvSlots.size()
         && InventoryMenu.canClickDragInto(var6, var7, true)
         && var6.canSetStack(var7)
         && this.menu.canClickDragInto(var6)) {
         this.draggedInvSlots.add(var6);
         this.updateDraggedStackRemainder();
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      InventorySlot var4 = this.getHoveredSlot(mouseX, mouseY);
      int var5 = this.x;
      int var6 = this.y;
      boolean var7 = mouseX < var5 || mouseY < var6 || mouseX >= var5 + this.backgroundWidth || mouseY >= var6 + this.backgroundHeight;
      int var8 = -1;
      if (var4 != null) {
         var8 = var4.id;
      }

      if (var7) {
         var8 = -999;
      }

      if (this.isDoubleClicking && var4 != null && mouseButton == 0 && this.menu.canRemoveForPickupAll(null, var4)) {
         if (isShiftDown()) {
            if (var4 != null && var4.inventory != null && this.shiftClickedStack != null) {
               for(InventorySlot var14 : this.menu.slots) {
                  if (var14 != null
                     && var14.canPickUp(this.client.player)
                     && var14.hasStack()
                     && var14.inventory == var4.inventory
                     && InventoryMenu.canClickDragInto(var14, this.shiftClickedStack, true)) {
                     this.clickSlot(var14, var14.id, mouseButton, 1);
                  }
               }
            }
         } else {
            this.clickSlot(var4, var8, mouseButton, 6);
         }

         this.isDoubleClicking = false;
         this.lastButtonClickTime = 0L;
      } else {
         if (this.isDraggingStack && this.clickDragButton != mouseButton) {
            this.isDraggingStack = false;
            this.draggedInvSlots.clear();
            this.cancelNextMouseRelease = true;
            return;
         }

         if (this.cancelNextMouseRelease) {
            this.cancelNextMouseRelease = false;
            return;
         }

         if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
            if (mouseButton == 0 || mouseButton == 1) {
               if (this.touchDragStack == null && var4 != this.touchDragSlotStart) {
                  this.touchDragStack = this.touchDragSlotStart.getStack();
               }

               boolean var12 = InventoryMenu.canClickDragInto(var4, this.touchDragStack, false);
               if (var8 != -1 && this.touchDragStack != null && var12) {
                  this.clickSlot(this.touchDragSlotStart, this.touchDragSlotStart.id, mouseButton, 0);
                  this.clickSlot(var4, var8, 0, 0);
                  if (this.client.player.inventory.getCursorStack() != null) {
                     this.clickSlot(this.touchDragSlotStart, this.touchDragSlotStart.id, mouseButton, 0);
                     this.touchDropX = mouseX - var5;
                     this.touchDropY = mouseY - var6;
                     this.touchDropOriginSlot = this.touchDragSlotStart;
                     this.touchDropReturningStack = this.touchDragStack;
                     this.touchDropTime = MinecraftClient.getTime();
                  } else {
                     this.touchDropReturningStack = null;
                  }
               } else if (this.touchDragStack != null) {
                  this.touchDropX = mouseX - var5;
                  this.touchDropY = mouseY - var6;
                  this.touchDropOriginSlot = this.touchDragSlotStart;
                  this.touchDropReturningStack = this.touchDragStack;
                  this.touchDropTime = MinecraftClient.getTime();
               }

               this.touchDragStack = null;
               this.touchDragSlotStart = null;
            }
         } else if (this.isDraggingStack && !this.draggedInvSlots.isEmpty()) {
            this.clickSlot(null, -999, InventoryMenu.packClickData(0, this.clickDragMode), 5);

            for(InventorySlot var10 : this.draggedInvSlots) {
               this.clickSlot(var10, var10.id, InventoryMenu.packClickData(1, this.clickDragMode), 5);
            }

            this.clickSlot(null, -999, InventoryMenu.packClickData(2, this.clickDragMode), 5);
         } else if (this.client.player.inventory.getCursorStack() != null) {
            if (mouseButton == this.client.options.pickItemKey.getKeyCode() + 100) {
               this.clickSlot(var4, var8, mouseButton, 3);
            } else {
               boolean var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
               if (var9) {
                  this.shiftClickedStack = var4 != null && var4.hasStack() ? var4.getStack() : null;
               }

               this.clickSlot(var4, var8, mouseButton, var9 ? 1 : 0);
            }
         }
      }

      if (this.client.player.inventory.getCursorStack() == null) {
         this.lastButtonClickTime = 0L;
      }

      this.isDraggingStack = false;
   }

   private boolean isMouseOverSlot(InventorySlot invSlot, int mouseX, int mouseY) {
      return this.isMouseInRegion(invSlot.x, invSlot.y, 16, 16, mouseX, mouseY);
   }

   protected boolean isMouseInRegion(int x, int y, int width, int height, int mouseX, int mouseY) {
      int var7 = this.x;
      int var8 = this.y;
      mouseX -= var7;
      mouseY -= var8;
      return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
   }

   protected void clickSlot(InventorySlot invSlot, int slotId, int clickData, int actionType) {
      if (invSlot != null) {
         slotId = invSlot.id;
      }

      this.client.interactionManager.clickSlot(this.menu.networkId, slotId, clickData, actionType, this.client.player);
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (key == 1 || key == this.client.options.inventoryKey.getKeyCode()) {
         this.client.player.closeMenu();
      }

      this.moveHoveredSlotToHotbar(key);
      if (this.hoveredSlot != null && this.hoveredSlot.hasStack()) {
         if (key == this.client.options.pickItemKey.getKeyCode()) {
            this.clickSlot(this.hoveredSlot, this.hoveredSlot.id, 0, 3);
         } else if (key == this.client.options.dropKey.getKeyCode()) {
            this.clickSlot(this.hoveredSlot, this.hoveredSlot.id, isControlDown() ? 1 : 0, 4);
         }
      }
   }

   protected boolean moveHoveredSlotToHotbar(int key) {
      if (this.client.player.inventory.getCursorStack() == null && this.hoveredSlot != null) {
         for(int var2 = 0; var2 < 9; ++var2) {
            if (key == this.client.options.hotbarKeys[var2].getKeyCode()) {
               this.clickSlot(this.hoveredSlot, this.hoveredSlot.id, var2, 2);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void removed() {
      if (this.client.player != null) {
         this.menu.close(this.client.player);
      }
   }

   @Override
   public boolean shouldPauseGame() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.client.player.isAlive() || this.client.player.removed) {
         this.client.player.closeMenu();
      }
   }
}
