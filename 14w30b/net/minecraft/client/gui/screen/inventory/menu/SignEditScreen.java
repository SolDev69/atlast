package net.minecraft.client.gui.screen.inventory.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.SignUpdateC2SPacket;
import net.minecraft.text.LiteralText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class SignEditScreen extends Screen {
   private SignBlockEntity sign;
   private int ticksSinceOpened;
   private int currentRow;
   private ButtonWidget doneButton;

   public SignEditScreen(SignBlockEntity sign) {
      this.sign = sign;
   }

   @Override
   public void init() {
      this.buttons.clear();
      Keyboard.enableRepeatEvents(true);
      this.buttons.add(this.doneButton = new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 120, I18n.translate("gui.done")));
      this.sign.setEditable(false);
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
      ClientPlayNetworkHandler var1 = this.client.getNetworkHandler();
      if (var1 != null) {
         var1.sendPacket(new SignUpdateC2SPacket(this.sign.getPos(), this.sign.lines));
      }

      this.sign.setEditable(true);
   }

   @Override
   public void tick() {
      ++this.ticksSinceOpened;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 0) {
            this.sign.markDirty();
            this.client.openScreen(null);
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (key == 200) {
         this.currentRow = this.currentRow - 1 & 3;
      }

      if (key == 208 || key == 28 || key == 156) {
         this.currentRow = this.currentRow + 1 & 3;
      }

      String var3 = this.sign.lines[this.currentRow].buildString();
      if (key == 14 && var3.length() > 0) {
         var3 = var3.substring(0, var3.length() - 1);
      }

      if (SharedConstants.isValidChatChar(chr) && this.textRenderer.getStringWidth(var3 + chr) <= 90) {
         var3 = var3 + chr;
      }

      this.sign.lines[this.currentRow] = new LiteralText(var3);
      if (key == 1) {
         this.buttonClicked(this.doneButton);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("sign.edit"), this.titleWidth / 2, 40, 16777215);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.titleWidth / 2), 0.0F, 50.0F);
      float var4 = 93.75F;
      GlStateManager.scalef(-var4, -var4, -var4);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      Block var5 = this.sign.getCachedBlock();
      if (var5 == Blocks.STANDING_SIGN) {
         float var6 = (float)(this.sign.getCachedMetadata() * 360) / 16.0F;
         GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
      } else {
         int var8 = this.sign.getCachedMetadata();
         float var7 = 0.0F;
         if (var8 == 2) {
            var7 = 180.0F;
         }

         if (var8 == 4) {
            var7 = 90.0F;
         }

         if (var8 == 5) {
            var7 = -90.0F;
         }

         GlStateManager.rotatef(var7, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
      }

      if (this.ticksSinceOpened / 6 % 2 == 0) {
         this.sign.currentRow = this.currentRow;
      }

      BlockEntityRenderDispatcher.INSTANCE.render(this.sign, -0.5, -0.75, -0.5, 0.0F);
      this.sign.currentRow = -1;
      GlStateManager.popMatrix();
      super.render(mouseX, mouseY, tickDelta);
   }
}
