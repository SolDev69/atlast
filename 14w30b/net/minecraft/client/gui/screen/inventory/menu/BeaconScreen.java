package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.BeaconMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BeaconScreen extends InventoryMenuScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
   private Inventory inventory;
   private BeaconScreen.DoneButtonWidget doneButton;
   private boolean acceptsPayment;

   public BeaconScreen(PlayerInventory playerInventory, Inventory inventory) {
      super(new BeaconMenu(playerInventory, inventory));
      this.inventory = inventory;
      this.backgroundWidth = 230;
      this.backgroundHeight = 219;
   }

   @Override
   public void init() {
      super.init();
      this.buttons.add(this.doneButton = new BeaconScreen.DoneButtonWidget(-1, this.x + 164, this.y + 107));
      this.buttons.add(new BeaconScreen.CancelButtonWidget(-2, this.x + 190, this.y + 107));
      this.acceptsPayment = true;
      this.doneButton.active = false;
   }

   @Override
   public void tick() {
      super.tick();
      int var1 = this.inventory.getData(0);
      int var2 = this.inventory.getData(1);
      int var3 = this.inventory.getData(2);
      if (this.acceptsPayment && var1 >= 0) {
         this.acceptsPayment = false;

         for(int var4 = 0; var4 <= 2; ++var4) {
            int var5 = BeaconBlockEntity.EFFECTS[var4].length;
            int var6 = var5 * 22 + (var5 - 1) * 2;

            for(int var7 = 0; var7 < var5; ++var7) {
               int var8 = BeaconBlockEntity.EFFECTS[var4][var7].id;
               BeaconScreen.EffectButtonWidget var9 = new BeaconScreen.EffectButtonWidget(
                  var4 << 8 | var8, this.x + 76 + var7 * 24 - var6 / 2, this.y + 22 + var4 * 25, var8, var4
               );
               this.buttons.add(var9);
               if (var4 >= var1) {
                  var9.active = false;
               } else if (var8 == var2) {
                  var9.setDisabled(true);
               }
            }
         }

         byte var10 = 3;
         int var11 = BeaconBlockEntity.EFFECTS[var10].length + 1;
         int var12 = var11 * 22 + (var11 - 1) * 2;

         for(int var13 = 0; var13 < var11 - 1; ++var13) {
            int var15 = BeaconBlockEntity.EFFECTS[var10][var13].id;
            BeaconScreen.EffectButtonWidget var16 = new BeaconScreen.EffectButtonWidget(
               var10 << 8 | var15, this.x + 167 + var13 * 24 - var12 / 2, this.y + 47, var15, var10
            );
            this.buttons.add(var16);
            if (var10 >= var1) {
               var16.active = false;
            } else if (var15 == var3) {
               var16.setDisabled(true);
            }
         }

         if (var2 > 0) {
            BeaconScreen.EffectButtonWidget var14 = new BeaconScreen.EffectButtonWidget(
               var10 << 8 | var2, this.x + 167 + (var11 - 1) * 24 - var12 / 2, this.y + 47, var2, var10
            );
            this.buttons.add(var14);
            if (var10 >= var1) {
               var14.active = false;
            } else if (var2 == var3) {
               var14.setDisabled(true);
            }
         }
      }

      this.doneButton.active = this.inventory.getStack(0) != null && var2 > 0;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == -2) {
         this.client.openScreen(null);
      } else if (buttonWidget.id == -1) {
         String var2 = "MC|Beacon";
         PacketByteBuf var3 = new PacketByteBuf(Unpooled.buffer());

         try {
            var3.writeInt(this.inventory.getData(1));
            var3.writeInt(this.inventory.getData(2));
            this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(var2, var3));
         } catch (Exception var8) {
            LOGGER.error("Couldn't send beacon info", var8);
         } finally {
            var3.release();
         }

         this.client.openScreen(null);
      } else if (buttonWidget instanceof BeaconScreen.EffectButtonWidget) {
         if (((BeaconScreen.EffectButtonWidget)buttonWidget).isDisabled()) {
            return;
         }

         int var10 = buttonWidget.id;
         int var11 = var10 & 0xFF;
         int var4 = var10 >> 8;
         if (var4 < 3) {
            this.inventory.setData(1, var11);
         } else {
            this.inventory.setData(2, var11);
         }

         this.buttons.clear();
         this.init();
         this.tick();
      }
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      Lighting.turnOff();
      this.drawCenteredString(this.textRenderer, I18n.translate("tile.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.textRenderer, I18n.translate("tile.beacon.secondary"), 169, 10, 14737632);

      for(ButtonWidget var4 : this.buttons) {
         if (var4.isHovered()) {
            var4.renderToolTip(mouseX - this.x, mouseY - this.y);
            break;
         }
      }

      Lighting.turnOnGui();
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      this.itemRenderer.zOffset = 100.0F;
      this.itemRenderer.renderGuiItem(new ItemStack(Items.EMERALD), var4 + 42, var5 + 109);
      this.itemRenderer.renderGuiItem(new ItemStack(Items.DIAMOND), var4 + 42 + 22, var5 + 109);
      this.itemRenderer.renderGuiItem(new ItemStack(Items.GOLD_INGOT), var4 + 42 + 44, var5 + 109);
      this.itemRenderer.renderGuiItem(new ItemStack(Items.IRON_INGOT), var4 + 42 + 66, var5 + 109);
      this.itemRenderer.zOffset = 0.0F;
   }

   @Environment(EnvType.CLIENT)
   static class BeaconButtonWidget extends ButtonWidget {
      private final Identifier texture;
      private final int xPos;
      private final int yPos;
      private boolean disabled;

      protected BeaconButtonWidget(int x, int y, int id, Identifier texture, int xPos, int yPos) {
         super(x, y, id, 22, 22, "");
         this.texture = texture;
         this.xPos = xPos;
         this.yPos = yPos;
      }

      @Override
      public void render(MinecraftClient client, int mouseX, int mouseY) {
         if (this.visible) {
            client.getTextureManager().bind(BeaconScreen.TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            short var4 = 219;
            int var5 = 0;
            if (!this.active) {
               var5 += this.width * 2;
            } else if (this.disabled) {
               var5 += this.width * 1;
            } else if (this.hovered) {
               var5 += this.width * 3;
            }

            this.drawTexture(this.x, this.y, var5, var4, this.width, this.height);
            if (!BeaconScreen.TEXTURE.equals(this.texture)) {
               client.getTextureManager().bind(this.texture);
            }

            this.drawTexture(this.x + 2, this.y + 2, this.xPos, this.yPos, 18, 18);
         }
      }

      public boolean isDisabled() {
         return this.disabled;
      }

      public void setDisabled(boolean disabled) {
         this.disabled = disabled;
      }
   }

   @Environment(EnvType.CLIENT)
   class CancelButtonWidget extends BeaconScreen.BeaconButtonWidget {
      public CancelButtonWidget(int x, int y, int id) {
         super(x, y, id, BeaconScreen.TEXTURE, 112, 220);
      }

      @Override
      public void renderToolTip(int mouseX, int mouseY) {
         BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel"), mouseX, mouseY);
      }
   }

   @Environment(EnvType.CLIENT)
   class DoneButtonWidget extends BeaconScreen.BeaconButtonWidget {
      public DoneButtonWidget(int x, int y, int id) {
         super(x, y, id, BeaconScreen.TEXTURE, 90, 220);
      }

      @Override
      public void renderToolTip(int mouseX, int mouseY) {
         BeaconScreen.this.renderTooltip(I18n.translate("gui.done"), mouseX, mouseY);
      }
   }

   @Environment(EnvType.CLIENT)
   class EffectButtonWidget extends BeaconScreen.BeaconButtonWidget {
      private final int effectId;
      private final int effectStrength;

      public EffectButtonWidget(int x, int y, int id, int effectId, int effectStrength) {
         super(
            x,
            y,
            id,
            InventoryMenuScreen.INVENTORY_TEXTURE,
            0 + StatusEffect.BY_ID[effectId].getIconIndex() % 8 * 18,
            198 + StatusEffect.BY_ID[effectId].getIconIndex() / 8 * 18
         );
         this.effectId = effectId;
         this.effectStrength = effectStrength;
      }

      @Override
      public void renderToolTip(int mouseX, int mouseY) {
         String var3 = I18n.translate(StatusEffect.BY_ID[this.effectId].getName());
         if (this.effectStrength >= 3 && this.effectId != StatusEffect.REGENERATION.id) {
            var3 = var3 + " II";
         }

         BeaconScreen.this.renderTooltip(var3, mouseX, mouseY);
      }
   }
}
