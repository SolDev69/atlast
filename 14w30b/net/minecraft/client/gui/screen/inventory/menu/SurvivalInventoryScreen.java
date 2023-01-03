package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.menu.AchievementsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SurvivalInventoryScreen extends PlayerInventoryScreen {
   private float mouseX;
   private float mouseY;

   public SurvivalInventoryScreen(PlayerEntity player) {
      super(player.playerMenu);
      this.passEvents = true;
   }

   @Override
   public void tick() {
      if (this.client.interactionManager.hasCreativeInventory()) {
         this.client.openScreen(new CreativeInventoryScreen(this.client.player));
      }

      this.m_94rxjojap();
   }

   @Override
   public void init() {
      this.buttons.clear();
      if (this.client.interactionManager.hasCreativeInventory()) {
         this.client.openScreen(new CreativeInventoryScreen(this.client.player));
      } else {
         super.init();
      }
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      this.textRenderer.drawWithoutShadow(I18n.translate("container.crafting"), 86, 16, 4210752);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      super.render(mouseX, mouseY, tickDelta);
      this.mouseX = (float)mouseX;
      this.mouseY = (float)mouseY;
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(INVENTORY_TEXTURE);
      int var4 = this.x;
      int var5 = this.y;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      renderEntity(var4 + 51, var5 + 75, 30, (float)(var4 + 51) - this.mouseX, (float)(var5 + 75 - 50) - this.mouseY, this.client.player);
   }

   public static void renderEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)x, (float)y, 50.0F);
      GlStateManager.scalef((float)(-size), (float)size, (float)size);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      float var6 = entity.bodyYaw;
      float var7 = entity.yaw;
      float var8 = entity.pitch;
      float var9 = entity.prevHeadYaw;
      float var10 = entity.headYaw;
      GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
      Lighting.turnOn();
      GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      entity.bodyYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
      entity.yaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
      entity.pitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
      entity.headYaw = entity.yaw;
      entity.prevHeadYaw = entity.yaw;
      GlStateManager.translatef(0.0F, 0.0F, 0.0F);
      EntityRenderDispatcher var11 = MinecraftClient.getInstance().getEntityRenderDispatcher();
      var11.m_59erzayop(180.0F);
      var11.m_01bqsgyjd(false);
      var11.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      var11.m_01bqsgyjd(true);
      entity.bodyYaw = var6;
      entity.yaw = var7;
      entity.pitch = var8;
      entity.prevHeadYaw = var9;
      entity.headYaw = var10;
      GlStateManager.popMatrix();
      Lighting.turnOff();
      GlStateManager.disableRescaleNormal();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
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
}
