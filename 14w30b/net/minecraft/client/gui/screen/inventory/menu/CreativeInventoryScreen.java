package net.minecraft.client.gui.screen.inventory.menu;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.menu.AchievementsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@Environment(EnvType.CLIENT)
public class CreativeInventoryScreen extends PlayerInventoryScreen {
   private static final Identifier ITEM_GROUPS = new Identifier("textures/gui/container/creative_inventory/tabs.png");
   private static SimpleInventory inventory = new SimpleInventory("tmp", true, 45);
   private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getId();
   private float scrollPosition;
   private boolean hasScrollBar;
   private boolean isMouseButtonDown;
   private TextFieldWidget searchField;
   private List slots;
   private InventorySlot slot;
   private boolean scrolling;
   private CreativeInventoryListener listener;

   public CreativeInventoryScreen(PlayerEntity player) {
      super(new CreativeInventoryScreen.CreativePlayerMenu(player));
      player.menu = this.menu;
      this.passEvents = true;
      this.backgroundHeight = 136;
      this.backgroundWidth = 195;
   }

   @Override
   public void tick() {
      if (!this.client.interactionManager.hasCreativeInventory()) {
         this.client.openScreen(new SurvivalInventoryScreen(this.client.player));
      }
   }

   @Override
   protected void clickSlot(InventorySlot invSlot, int slotId, int clickData, int actionType) {
      this.scrolling = true;
      boolean var5 = actionType == 1;
      actionType = slotId == -999 && actionType == 0 ? 4 : actionType;
      if (invSlot == null && selectedTab != ItemGroup.INVENTORY.getId() && actionType != 5) {
         PlayerInventory var15 = this.client.player.inventory;
         if (var15.getCursorStack() != null) {
            if (clickData == 0) {
               this.client.player.dropItem(var15.getCursorStack(), true);
               this.client.interactionManager.dropStackFromCreativeMenu(var15.getCursorStack());
               var15.setCursorStack(null);
            }

            if (clickData == 1) {
               ItemStack var17 = var15.getCursorStack().split(1);
               this.client.player.dropItem(var17, true);
               this.client.interactionManager.dropStackFromCreativeMenu(var17);
               if (var15.getCursorStack().size == 0) {
                  var15.setCursorStack(null);
               }
            }
         }
      } else if (invSlot == this.slot && var5) {
         for(int var14 = 0; var14 < this.client.player.playerMenu.getStacks().size(); ++var14) {
            this.client.interactionManager.addStackToCreativeMenu(null, var14);
         }
      } else if (selectedTab == ItemGroup.INVENTORY.getId()) {
         if (invSlot == this.slot) {
            this.client.player.inventory.setCursorStack(null);
         } else if (actionType == 4 && invSlot != null && invSlot.hasStack()) {
            ItemStack var6 = invSlot.removeStack(clickData == 0 ? 1 : invSlot.getStack().getMaxSize());
            this.client.player.dropItem(var6, true);
            this.client.interactionManager.dropStackFromCreativeMenu(var6);
         } else if (actionType == 4 && this.client.player.inventory.getCursorStack() != null) {
            this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
            this.client.interactionManager.dropStackFromCreativeMenu(this.client.player.inventory.getCursorStack());
            this.client.player.inventory.setCursorStack(null);
         } else {
            this.client
               .player
               .playerMenu
               .onClickSlot(
                  invSlot == null ? slotId : ((CreativeInventoryScreen.CreativeInventorySlot)invSlot).invSlot.id, clickData, actionType, this.client.player
               );
            this.client.player.playerMenu.updateListeners();
         }
      } else if (actionType != 5 && invSlot.inventory == inventory) {
         PlayerInventory var13 = this.client.player.inventory;
         ItemStack var7 = var13.getCursorStack();
         ItemStack var8 = invSlot.getStack();
         if (actionType == 2) {
            if (var8 != null && clickData >= 0 && clickData < 9) {
               ItemStack var19 = var8.copy();
               var19.size = var19.getMaxSize();
               this.client.player.inventory.setStack(clickData, var19);
               this.client.player.playerMenu.updateListeners();
            }

            return;
         }

         if (actionType == 3) {
            if (var13.getCursorStack() == null && invSlot.hasStack()) {
               ItemStack var18 = invSlot.getStack().copy();
               var18.size = var18.getMaxSize();
               var13.setCursorStack(var18);
            }

            return;
         }

         if (actionType == 4) {
            if (var8 != null) {
               ItemStack var9 = var8.copy();
               var9.size = clickData == 0 ? 1 : var9.getMaxSize();
               this.client.player.dropItem(var9, true);
               this.client.interactionManager.dropStackFromCreativeMenu(var9);
            }

            return;
         }

         if (var7 != null && var8 != null && var7.matchesItem(var8)) {
            if (clickData == 0) {
               if (var5) {
                  var7.size = var7.getMaxSize();
               } else if (var7.size < var7.getMaxSize()) {
                  ++var7.size;
               }
            } else if (var7.size <= 1) {
               var13.setCursorStack(null);
            } else {
               --var7.size;
            }
         } else if (var8 != null && var7 == null) {
            var13.setCursorStack(ItemStack.copyOf(var8));
            var7 = var13.getCursorStack();
            if (var5) {
               var7.size = var7.getMaxSize();
            }
         } else {
            var13.setCursorStack(null);
         }
      } else {
         this.menu.onClickSlot(invSlot == null ? slotId : invSlot.id, clickData, actionType, this.client.player);
         if (InventoryMenu.unpackClickDragStage(clickData) == 2) {
            for(int var11 = 0; var11 < 9; ++var11) {
               this.client.interactionManager.addStackToCreativeMenu(this.menu.getSlot(45 + var11).getStack(), 36 + var11);
            }
         } else if (invSlot != null) {
            ItemStack var12 = this.menu.getSlot(invSlot.id).getStack();
            this.client.interactionManager.addStackToCreativeMenu(var12, invSlot.id - this.menu.slots.size() + 9 + 36);
         }
      }
   }

   @Override
   public void init() {
      if (this.client.interactionManager.hasCreativeInventory()) {
         super.init();
         this.buttons.clear();
         Keyboard.enableRepeatEvents(true);
         this.searchField = new TextFieldWidget(0, this.textRenderer, this.x + 82, this.y + 6, 89, this.textRenderer.fontHeight);
         this.searchField.setMaxLength(15);
         this.searchField.setHasBorder(false);
         this.searchField.setVisible(false);
         this.searchField.setEditableColor(16777215);
         int var1 = selectedTab;
         selectedTab = -1;
         this.setSelectedTab(ItemGroup.BY_ID[var1]);
         this.listener = new CreativeInventoryListener(this.client);
         this.client.player.playerMenu.addListener(this.listener);
      } else {
         this.client.openScreen(new SurvivalInventoryScreen(this.client.player));
      }
   }

   @Override
   public void removed() {
      super.removed();
      if (this.client.player != null && this.client.player.inventory != null) {
         this.client.player.playerMenu.removeListener(this.listener);
      }

      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (selectedTab != ItemGroup.SEARCH.getId()) {
         if (GameOptions.isPressed(this.client.options.chatKey)) {
            this.setSelectedTab(ItemGroup.SEARCH);
         } else {
            super.keyPressed(chr, key);
         }
      } else {
         if (this.scrolling) {
            this.scrolling = false;
            this.searchField.setText("");
         }

         if (!this.moveHoveredSlotToHotbar(key)) {
            if (this.searchField.keyPressed(chr, key)) {
               this.search();
            } else {
               super.keyPressed(chr, key);
            }
         }
      }
   }

   private void search() {
      CreativeInventoryScreen.CreativePlayerMenu var1 = (CreativeInventoryScreen.CreativePlayerMenu)this.menu;
      var1.tabs.clear();

      for(Item var3 : Item.REGISTRY) {
         if (var3 != null && var3.getItemGroup() != null) {
            var3.addToCreativeMenu(var3, null, var1.tabs);
         }
      }

      for(Enchantment var5 : Enchantment.ALL) {
         if (var5 != null && var5.target != null) {
            Items.ENCHANTED_BOOK.getStacksWithEnchantment(var5, var1.tabs);
         }
      }

      Iterator var9 = var1.tabs.iterator();
      String var11 = this.searchField.getText().toLowerCase();

      while(var9.hasNext()) {
         ItemStack var12 = (ItemStack)var9.next();
         boolean var13 = false;

         for(String var7 : var12.getTooltip(this.client.player, this.client.options.advancedItemTooltips)) {
            if (var7.toLowerCase().contains(var11)) {
               var13 = true;
               break;
            }
         }

         if (!var13) {
            var9.remove();
         }
      }

      this.scrollPosition = 0.0F;
      var1.scrollItems(0.0F);
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      ItemGroup var3 = ItemGroup.BY_ID[selectedTab];
      if (var3.hasTooltip()) {
         GlStateManager.enableBlend();
         this.textRenderer.drawWithoutShadow(I18n.translate(var3.getDisplayName()), 8, 6, 4210752);
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         int var4 = mouseX - this.x;
         int var5 = mouseY - this.y;

         for(ItemGroup var9 : ItemGroup.BY_ID) {
            if (this.isInRow(var9, var4, var5)) {
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         int var4 = mouseX - this.x;
         int var5 = mouseY - this.y;

         for(ItemGroup var9 : ItemGroup.BY_ID) {
            if (this.isInRow(var9, var4, var5)) {
               this.setSelectedTab(var9);
               return;
            }
         }
      }

      super.mouseReleased(mouseX, mouseY, mouseButton);
   }

   private boolean hasScrollbar() {
      return selectedTab != ItemGroup.INVENTORY.getId()
         && ItemGroup.BY_ID[selectedTab].hasScrollbar()
         && ((CreativeInventoryScreen.CreativePlayerMenu)this.menu).isMaxTabsReached();
   }

   private void setSelectedTab(ItemGroup group) {
      int var2 = selectedTab;
      selectedTab = group.getId();
      CreativeInventoryScreen.CreativePlayerMenu var3 = (CreativeInventoryScreen.CreativePlayerMenu)this.menu;
      this.draggedInvSlots.clear();
      var3.tabs.clear();
      group.showItems(var3.tabs);
      if (group == ItemGroup.INVENTORY) {
         InventoryMenu var4 = this.client.player.playerMenu;
         if (this.slots == null) {
            this.slots = var3.slots;
         }

         var3.slots = Lists.newArrayList();

         for(int var5 = 0; var5 < var4.slots.size(); ++var5) {
            CreativeInventoryScreen.CreativeInventorySlot var6 = new CreativeInventoryScreen.CreativeInventorySlot((InventorySlot)var4.slots.get(var5), var5);
            var3.slots.add(var6);
            if (var5 >= 5 && var5 < 9) {
               int var10 = var5 - 5;
               int var11 = var10 / 2;
               int var12 = var10 % 2;
               var6.x = 9 + var11 * 54;
               var6.y = 6 + var12 * 27;
            } else if (var5 >= 0 && var5 < 5) {
               var6.y = -2000;
               var6.x = -2000;
            } else if (var5 < var4.slots.size()) {
               int var7 = var5 - 9;
               int var8 = var7 % 9;
               int var9 = var7 / 9;
               var6.x = 9 + var8 * 18;
               if (var5 >= 36) {
                  var6.y = 112;
               } else {
                  var6.y = 54 + var9 * 18;
               }
            }
         }

         this.slot = new InventorySlot(inventory, 0, 173, 112);
         var3.slots.add(this.slot);
      } else if (var2 == ItemGroup.INVENTORY.getId()) {
         var3.slots = this.slots;
         this.slots = null;
      }

      if (this.searchField != null) {
         if (group == ItemGroup.SEARCH) {
            this.searchField.setVisible(true);
            this.searchField.setFocusUnlocked(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.search();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setFocusUnlocked(true);
            this.searchField.setFocused(false);
         }
      }

      this.scrollPosition = 0.0F;
      var3.scrollItems(0.0F);
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      int var1 = Mouse.getEventDWheel();
      if (var1 != 0 && this.hasScrollbar()) {
         int var2 = ((CreativeInventoryScreen.CreativePlayerMenu)this.menu).tabs.size() / 9 - 5 + 1;
         if (var1 > 0) {
            var1 = 1;
         }

         if (var1 < 0) {
            var1 = -1;
         }

         this.scrollPosition = (float)((double)this.scrollPosition - (double)var1 / (double)var2);
         this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
         ((CreativeInventoryScreen.CreativePlayerMenu)this.menu).scrollItems(this.scrollPosition);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      boolean var4 = Mouse.isButtonDown(0);
      int var5 = this.x;
      int var6 = this.y;
      int var7 = var5 + 175;
      int var8 = var6 + 18;
      int var9 = var7 + 14;
      int var10 = var8 + 112;
      if (!this.isMouseButtonDown && var4 && mouseX >= var7 && mouseY >= var8 && mouseX < var9 && mouseY < var10) {
         this.hasScrollBar = this.hasScrollbar();
      }

      if (!var4) {
         this.hasScrollBar = false;
      }

      this.isMouseButtonDown = var4;
      if (this.hasScrollBar) {
         this.scrollPosition = ((float)(mouseY - var8) - 7.5F) / ((float)(var10 - var8) - 15.0F);
         this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
         ((CreativeInventoryScreen.CreativePlayerMenu)this.menu).scrollItems(this.scrollPosition);
      }

      super.render(mouseX, mouseY, tickDelta);

      for(ItemGroup var14 : ItemGroup.BY_ID) {
         if (this.renderTabTooltipIfHovered(var14, mouseX, mouseY)) {
            break;
         }
      }

      if (this.slot != null && selectedTab == ItemGroup.INVENTORY.getId() && this.isMouseInRegion(this.slot.x, this.slot.y, 16, 16, mouseX, mouseY)) {
         this.renderTooltip(I18n.translate("inventory.binSlot"), mouseX, mouseY);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
   }

   @Override
   protected void renderTooltip(ItemStack stack, int x, int y) {
      if (selectedTab == ItemGroup.SEARCH.getId()) {
         List var4 = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips);
         ItemGroup var5 = stack.getItem().getItemGroup();
         if (var5 == null && stack.getItem() == Items.ENCHANTED_BOOK) {
            Map var6 = EnchantmentHelper.getEnchantments(stack);
            if (var6.size() == 1) {
               Enchantment var7 = Enchantment.byRawId(var6.keySet().iterator().next());

               for(ItemGroup var11 : ItemGroup.BY_ID) {
                  if (var11.containsEnchantmentTarget(var7.target)) {
                     var5 = var11;
                     break;
                  }
               }
            }
         }

         if (var5 != null) {
            var4.add(1, "" + Formatting.BOLD + Formatting.BLUE + I18n.translate(var5.getDisplayName()));
         }

         for(int var12 = 0; var12 < var4.size(); ++var12) {
            if (var12 == 0) {
               var4.set(var12, stack.getRarity().formatting + (String)var4.get(var12));
            } else {
               var4.set(var12, Formatting.GRAY + (String)var4.get(var12));
            }
         }

         this.renderTooltip(var4, x, y);
      } else {
         super.renderTooltip(stack, x, y);
      }
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Lighting.turnOnGui();
      ItemGroup var4 = ItemGroup.BY_ID[selectedTab];

      for(ItemGroup var8 : ItemGroup.BY_ID) {
         this.client.getTextureManager().bind(ITEM_GROUPS);
         if (var8.getId() != selectedTab) {
            this.renderTabIcon(var8);
         }
      }

      this.client.getTextureManager().bind(new Identifier("textures/gui/container/creative_inventory/tab_" + var4.getTexture()));
      this.drawTexture(this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
      this.searchField.render();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var9 = this.x + 175;
      int var10 = this.y + 18;
      int var11 = var10 + 112;
      this.client.getTextureManager().bind(ITEM_GROUPS);
      if (var4.hasScrollbar()) {
         this.drawTexture(var9, var10 + (int)((float)(var11 - var10 - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
      }

      this.renderTabIcon(var4);
      if (var4 == ItemGroup.INVENTORY) {
         SurvivalInventoryScreen.renderEntity(
            this.x + 43, this.y + 45, 20, (float)(this.x + 43 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player
         );
      }
   }

   protected boolean isInRow(ItemGroup group, int x, int z) {
      int var4 = group.getColumn();
      int var5 = 28 * var4;
      int var6 = 0;
      if (var4 == 5) {
         var5 = this.backgroundWidth - 28 + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      if (group.isTopRow()) {
         var6 -= 32;
      } else {
         var6 += this.backgroundHeight;
      }

      return x >= var5 && x <= var5 + 28 && z >= var6 && z <= var6 + 32;
   }

   protected boolean renderTabTooltipIfHovered(ItemGroup group, int mouseX, int mouseY) {
      int var4 = group.getColumn();
      int var5 = 28 * var4;
      int var6 = 0;
      if (var4 == 5) {
         var5 = this.backgroundWidth - 28 + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      if (group.isTopRow()) {
         var6 -= 32;
      } else {
         var6 += this.backgroundHeight;
      }

      if (this.isMouseInRegion(var5 + 3, var6 + 3, 23, 27, mouseX, mouseY)) {
         this.renderTooltip(I18n.translate(group.getDisplayName()), mouseX, mouseY);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabIcon(ItemGroup group) {
      boolean var2 = group.getId() == selectedTab;
      boolean var3 = group.isTopRow();
      int var4 = group.getColumn();
      int var5 = var4 * 28;
      int var6 = 0;
      int var7 = this.x + 28 * var4;
      int var8 = this.y;
      byte var9 = 32;
      if (var2) {
         var6 += 32;
      }

      if (var4 == 5) {
         var7 = this.x + this.backgroundWidth - 28;
      } else if (var4 > 0) {
         var7 += var4;
      }

      if (var3) {
         var8 -= 28;
      } else {
         var6 += 64;
         var8 += this.backgroundHeight - 4;
      }

      GlStateManager.disableLighting();
      this.drawTexture(var7, var8, var5, var6, 28, var9);
      this.drawOffset = 100.0F;
      this.itemRenderer.zOffset = 100.0F;
      var7 += 6;
      var8 += 8 + (var3 ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack var10 = group.getIcon();
      this.itemRenderer.renderGuiItem(var10, var7, var8);
      this.itemRenderer.renderGuiItemDecorations(this.textRenderer, var10, var7, var8);
      GlStateManager.disableLighting();
      this.itemRenderer.zOffset = 0.0F;
      this.drawOffset = 0.0F;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0) {
         this.client.openScreen(new AchievementsScreen(this, this.client.player.getStatHandler()));
      }

      if (buttonWidget.id == 1) {
         this.client.openScreen(new StatsScreen(this, this.client.player.getStatHandler()));
      }
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   @Environment(EnvType.CLIENT)
   class CreativeInventorySlot extends InventorySlot {
      private final InventorySlot invSlot;

      public CreativeInventorySlot(InventorySlot c_45vtulehf, int i) {
         super(c_45vtulehf.inventory, i, 0, 0);
         this.invSlot = c_45vtulehf;
      }

      @Override
      public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
         this.invSlot.onStackRemovedByPlayer(player, stack);
      }

      @Override
      public boolean canSetStack(ItemStack stack) {
         return this.invSlot.canSetStack(stack);
      }

      @Override
      public ItemStack getStack() {
         return this.invSlot.getStack();
      }

      @Override
      public boolean hasStack() {
         return this.invSlot.hasStack();
      }

      @Override
      public void setStack(ItemStack stack) {
         this.invSlot.setStack(stack);
      }

      @Override
      public void markDirty() {
         this.invSlot.markDirty();
      }

      @Override
      public int getMaxStackSize() {
         return this.invSlot.getMaxStackSize();
      }

      @Override
      public int getMaxStackSize(ItemStack stack) {
         return this.invSlot.getMaxStackSize(stack);
      }

      @Override
      public String getTexture() {
         return this.invSlot.getTexture();
      }

      @Override
      public ItemStack removeStack(int amount) {
         return this.invSlot.removeStack(amount);
      }

      @Override
      public boolean equals(Inventory inventory, int slot) {
         return this.invSlot.equals(inventory, slot);
      }
   }

   @Environment(EnvType.CLIENT)
   static class CreativePlayerMenu extends InventoryMenu {
      public List tabs = Lists.newArrayList();

      public CreativePlayerMenu(PlayerEntity c_84dqcqlog) {
         PlayerInventory var2 = c_84dqcqlog.inventory;

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.addSlot(new InventorySlot(CreativeInventoryScreen.inventory, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
            }
         }

         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new InventorySlot(var2, var5, 9 + var5 * 18, 112));
         }

         this.scrollItems(0.0F);
      }

      @Override
      public boolean isValid(PlayerEntity player) {
         return true;
      }

      public void scrollItems(float position) {
         int var2 = (this.tabs.size() + 8) / 9 - 5;
         int var3 = (int)((double)(position * (float)var2) + 0.5);
         if (var3 < 0) {
            var3 = 0;
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 9; ++var5) {
               int var6 = var5 + (var4 + var3) * 9;
               if (var6 >= 0 && var6 < this.tabs.size()) {
                  CreativeInventoryScreen.inventory.setStack(var5 + var4 * 9, (ItemStack)this.tabs.get(var6));
               } else {
                  CreativeInventoryScreen.inventory.setStack(var5 + var4 * 9, null);
               }
            }
         }
      }

      public boolean isMaxTabsReached() {
         return this.tabs.size() > 45;
      }

      @Override
      protected void quickMoveStack(int id, int clickData, boolean bl, PlayerEntity player) {
      }

      @Override
      public ItemStack quickMoveStack(PlayerEntity player, int id) {
         if (id >= this.slots.size() - 9 && id < this.slots.size()) {
            InventorySlot var3 = (InventorySlot)this.slots.get(id);
            if (var3 != null && var3.hasStack()) {
               var3.setStack(null);
            }
         }

         return null;
      }

      @Override
      public boolean canRemoveForPickupAll(ItemStack stack, InventorySlot invSlot) {
         return invSlot.y > 90;
      }

      @Override
      public boolean canClickDragInto(InventorySlot invSlot) {
         return invSlot.inventory instanceof PlayerInventory || invSlot.y > 90 && invSlot.x <= 162;
      }
   }
}
