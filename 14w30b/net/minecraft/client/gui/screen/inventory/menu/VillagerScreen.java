package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.mob.passive.Trader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.TraderMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TradeOffers;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VillagerScreen extends InventoryMenuScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");
   private Trader trader;
   private VillagerScreen.PaginationButton buttonForward;
   private VillagerScreen.PaginationButton buttonBack;
   private int currentPage;
   private Text name;

   public VillagerScreen(PlayerInventory inventory, Trader trader, World world) {
      super(new TraderMenu(inventory, trader, world));
      this.trader = trader;
      this.name = trader.getDisplayName();
   }

   @Override
   public void init() {
      super.init();
      int var1 = (this.titleWidth - this.backgroundWidth) / 2;
      int var2 = (this.height - this.backgroundHeight) / 2;
      this.buttons.add(this.buttonForward = new VillagerScreen.PaginationButton(1, var1 + 120 + 27, var2 + 24 - 1, true));
      this.buttons.add(this.buttonBack = new VillagerScreen.PaginationButton(2, var1 + 36 - 19, var2 + 24 - 1, false));
      this.buttonForward.active = false;
      this.buttonBack.active = false;
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      String var3 = this.name.buildString();
      this.textRenderer.drawWithoutShadow(var3, this.backgroundWidth / 2 - this.textRenderer.getStringWidth(var3) / 2, 6, 4210752);
      this.textRenderer.drawWithoutShadow(I18n.translate("container.inventory"), 8, this.backgroundHeight - 96 + 2, 4210752);
   }

   @Override
   public void tick() {
      super.tick();
      TradeOffers var1 = this.trader.getOffers(this.client.player);
      if (var1 != null) {
         this.buttonForward.active = this.currentPage < var1.size() - 1;
         this.buttonBack.active = this.currentPage > 0;
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      boolean var2 = false;
      if (buttonWidget == this.buttonForward) {
         ++this.currentPage;
         TradeOffers var3 = this.trader.getOffers(this.client.player);
         if (var3 != null && this.currentPage >= var3.size()) {
            this.currentPage = var3.size() - 1;
         }

         var2 = true;
      } else if (buttonWidget == this.buttonBack) {
         --this.currentPage;
         if (this.currentPage < 0) {
            this.currentPage = 0;
         }

         var2 = true;
      }

      if (var2) {
         ((TraderMenu)this.menu).setRecipeIndex(this.currentPage);
         PacketByteBuf var10 = new PacketByteBuf(Unpooled.buffer());

         try {
            var10.writeInt(this.currentPage);
            this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|TrSel", var10));
         } catch (Exception var8) {
            LOGGER.error("Couldn't send trade info", var8);
         } finally {
            var10.release();
         }
      }
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      TradeOffers var6 = this.trader.getOffers(this.client.player);
      if (var6 != null && !var6.isEmpty()) {
         int var7 = this.currentPage;
         if (var7 < 0 || var7 >= var6.size()) {
            return;
         }

         TradeOffer var8 = (TradeOffer)var6.get(var7);
         if (var8.isDisabled()) {
            this.client.getTextureManager().bind(TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexture(this.x + 83, this.y + 21, 212, 0, 28, 21);
            this.drawTexture(this.x + 83, this.y + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      super.render(mouseX, mouseY, tickDelta);
      TradeOffers var4 = this.trader.getOffers(this.client.player);
      if (var4 != null && !var4.isEmpty()) {
         int var5 = (this.titleWidth - this.backgroundWidth) / 2;
         int var6 = (this.height - this.backgroundHeight) / 2;
         int var7 = this.currentPage;
         TradeOffer var8 = (TradeOffer)var4.get(var7);
         ItemStack var9 = var8.getPrimaryPayment();
         ItemStack var10 = var8.getSecondaryPayment();
         ItemStack var11 = var8.getResult();
         GlStateManager.pushMatrix();
         Lighting.turnOnGui();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRenderer.zOffset = 100.0F;
         this.itemRenderer.renderGuiItem(var9, var5 + 36, var6 + 24);
         this.itemRenderer.renderGuiItemDecorations(this.textRenderer, var9, var5 + 36, var6 + 24);
         if (var10 != null) {
            this.itemRenderer.renderGuiItem(var10, var5 + 62, var6 + 24);
            this.itemRenderer.renderGuiItemDecorations(this.textRenderer, var10, var5 + 62, var6 + 24);
         }

         this.itemRenderer.renderGuiItem(var11, var5 + 120, var6 + 24);
         this.itemRenderer.renderGuiItemDecorations(this.textRenderer, var11, var5 + 120, var6 + 24);
         this.itemRenderer.zOffset = 0.0F;
         GlStateManager.disableLighting();
         if (this.isMouseInRegion(36, 24, 16, 16, mouseX, mouseY) && var9 != null) {
            this.renderTooltip(var9, mouseX, mouseY);
         } else if (var10 != null && this.isMouseInRegion(62, 24, 16, 16, mouseX, mouseY) && var10 != null) {
            this.renderTooltip(var10, mouseX, mouseY);
         } else if (var11 != null && this.isMouseInRegion(120, 24, 16, 16, mouseX, mouseY) && var11 != null) {
            this.renderTooltip(var11, mouseX, mouseY);
         } else if (var8.isDisabled() && (this.isMouseInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isMouseInRegion(83, 51, 28, 21, mouseX, mouseY))) {
            this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.disableDepth();
         Lighting.turnOn();
      }
   }

   public Trader getTrader() {
      return this.trader;
   }

   @Environment(EnvType.CLIENT)
   static class PaginationButton extends ButtonWidget {
      private final boolean hasHeight;

      public PaginationButton(int x, int y, int id, boolean hasHeight) {
         super(x, y, id, 12, 19, "");
         this.hasHeight = hasHeight;
      }

      @Override
      public void render(MinecraftClient client, int mouseX, int mouseY) {
         if (this.visible) {
            client.getTextureManager().bind(VillagerScreen.TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var4 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int var5 = 0;
            int var6 = 176;
            if (!this.active) {
               var6 += this.width * 2;
            } else if (var4) {
               var6 += this.width;
            }

            if (!this.hasHeight) {
               var5 += this.height;
            }

            this.drawTexture(this.x, this.y, var6, var5, this.width, this.height);
         }
      }
   }
}
