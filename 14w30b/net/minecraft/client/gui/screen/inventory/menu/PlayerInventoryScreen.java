package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.inventory.menu.InventoryMenu;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class PlayerInventoryScreen extends InventoryMenuScreen {
   private boolean hasStatusEffect;

   public PlayerInventoryScreen(InventoryMenu c_38uldnvgv) {
      super(c_38uldnvgv);
   }

   @Override
   public void init() {
      super.init();
      this.m_94rxjojap();
   }

   protected void m_94rxjojap() {
      if (!this.client.player.getStatusEffects().isEmpty()) {
         this.x = 160 + (this.titleWidth - this.backgroundWidth - 200) / 2;
         this.hasStatusEffect = true;
      } else {
         this.x = (this.titleWidth - this.backgroundWidth) / 2;
         this.hasStatusEffect = false;
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      super.render(mouseX, mouseY, tickDelta);
      if (this.hasStatusEffect) {
         this.drawStatusEffects();
      }
   }

   private void drawStatusEffects() {
      int var1 = this.x - 124;
      int var2 = this.y;
      boolean var3 = true;
      Collection var4 = this.client.player.getStatusEffects();
      if (!var4.isEmpty()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int var5 = 33;
         if (var4.size() > 5) {
            var5 = 132 / (var4.size() - 1);
         }

         for(StatusEffectInstance var7 : this.client.player.getStatusEffects()) {
            StatusEffect var8 = StatusEffect.BY_ID[var7.getId()];
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.client.getTextureManager().bind(INVENTORY_TEXTURE);
            this.drawTexture(var1, var2, 0, 166, 140, 32);
            if (var8.hasIcon()) {
               int var9 = var8.getIconIndex();
               this.drawTexture(var1 + 6, var2 + 7, 0 + var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
            }

            String var11 = I18n.translate(var8.getName());
            if (var7.getAmplifier() == 1) {
               var11 = var11 + " " + I18n.translate("enchantment.level.2");
            } else if (var7.getAmplifier() == 2) {
               var11 = var11 + " " + I18n.translate("enchantment.level.3");
            } else if (var7.getAmplifier() == 3) {
               var11 = var11 + " " + I18n.translate("enchantment.level.4");
            }

            this.textRenderer.drawWithShadow(var11, (float)(var1 + 10 + 18), (float)(var2 + 6), 16777215);
            String var10 = StatusEffect.getDurationString(var7);
            this.textRenderer.drawWithShadow(var10, (float)(var1 + 10 + 18), (float)(var2 + 6 + 10), 8355711);
            var2 += var5;
         }
      }
   }
}
