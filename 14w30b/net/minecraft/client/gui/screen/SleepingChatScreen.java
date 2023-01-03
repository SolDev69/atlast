package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.PlayerMovementActionC2SPacket;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen {
   @Override
   public void init() {
      super.init();
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, this.height - 40, I18n.translate("multiplayer.stopSleeping")));
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (key == 1) {
         this.stopSleeping();
      } else if (key != 28 && key != 156) {
         super.keyPressed(chr, key);
      } else {
         String var3 = this.chatField.getText().trim();
         if (!var3.isEmpty()) {
            this.client.player.sendChat(var3);
         }

         this.chatField.setText("");
         this.client.gui.getChat().resetScroll();
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 1) {
         this.stopSleeping();
      } else {
         super.buttonClicked(buttonWidget);
      }
   }

   private void stopSleeping() {
      ClientPlayNetworkHandler var1 = this.client.player.networkHandler;
      var1.sendPacket(new PlayerMovementActionC2SPacket(this.client.player, PlayerMovementActionC2SPacket.Action.STOP_SLEEPING));
   }
}
