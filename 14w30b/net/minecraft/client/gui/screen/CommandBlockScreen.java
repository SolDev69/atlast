package net.minecraft.client.gui.screen;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.command.source.CommandExecutor;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class CommandBlockScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private TextFieldWidget input;
   private TextFieldWidget output;
   private final CommandExecutor executor;
   private ButtonWidget doneButton;
   private ButtonWidget cancelButton;
   private ButtonWidget f_37mybsplm;
   private boolean f_66agqddsx;

   public CommandBlockScreen(CommandExecutor executor) {
      this.executor = executor;
   }

   @Override
   public void tick() {
      this.input.tick();
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttons.clear();
      this.buttons.add(this.doneButton = new ButtonWidget(0, this.titleWidth / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done")));
      this.buttons.add(this.cancelButton = new ButtonWidget(1, this.titleWidth / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel")));
      this.buttons.add(this.f_37mybsplm = new ButtonWidget(4, this.titleWidth / 2 + 150 - 20, 150, 20, 20, "O"));
      this.input = new TextFieldWidget(2, this.textRenderer, this.titleWidth / 2 - 150, 50, 300, 20);
      this.input.setMaxLength(32767);
      this.input.setFocused(true);
      this.input.setText(this.executor.getCommand());
      this.output = new TextFieldWidget(3, this.textRenderer, this.titleWidth / 2 - 150, 150, 276, 20);
      this.output.setMaxLength(32767);
      this.output.setEditable(false);
      this.output.setText("-");
      this.f_66agqddsx = this.executor.trackOutput();
      this.m_31mdpyfqp();
      this.doneButton.active = this.input.getText().trim().length() > 0;
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 1) {
            this.executor.setTrackOutput(this.f_66agqddsx);
            this.client.openScreen(null);
         } else if (buttonWidget.id == 0) {
            PacketByteBuf var2 = new PacketByteBuf(Unpooled.buffer());

            try {
               var2.writeByte(this.executor.getType());
               this.executor.writeEntityId(var2);
               var2.writeString(this.input.getText());
               var2.writeBoolean(this.executor.trackOutput());
               this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|AdvCdm", var2));
            } catch (Exception var7) {
               LOGGER.error("Couldn't send command block info", var7);
            } finally {
               var2.release();
            }

            if (!this.executor.trackOutput()) {
               this.executor.setLastOutput(null);
            }

            this.client.openScreen(null);
         } else if (buttonWidget.id == 4) {
            this.executor.setTrackOutput(!this.executor.trackOutput());
            this.m_31mdpyfqp();
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      this.input.keyPressed(chr, key);
      this.output.keyPressed(chr, key);
      this.doneButton.active = this.input.getText().trim().length() > 0;
      if (key == 28 || key == 156) {
         this.buttonClicked(this.doneButton);
      } else if (key == 1) {
         this.buttonClicked(this.cancelButton);
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.input.mouseClicked(mouseX, mouseY, mouseButton);
      this.output.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, I18n.translate("advMode.setCommand"), this.titleWidth / 2, 20, 16777215);
      this.drawString(this.textRenderer, I18n.translate("advMode.command"), this.titleWidth / 2 - 150, 37, 10526880);
      this.input.render();
      int var4 = 75;
      int var5 = 0;
      this.drawString(
         this.textRenderer, I18n.translate("advMode.nearestPlayer"), this.titleWidth / 2 - 150, var4 + var5++ * this.textRenderer.fontHeight, 10526880
      );
      this.drawString(
         this.textRenderer, I18n.translate("advMode.randomPlayer"), this.titleWidth / 2 - 150, var4 + var5++ * this.textRenderer.fontHeight, 10526880
      );
      this.drawString(
         this.textRenderer, I18n.translate("advMode.allPlayers"), this.titleWidth / 2 - 150, var4 + var5++ * this.textRenderer.fontHeight, 10526880
      );
      this.drawString(
         this.textRenderer, I18n.translate("advMode.allEntities"), this.titleWidth / 2 - 150, var4 + var5++ * this.textRenderer.fontHeight, 10526880
      );
      this.drawString(this.textRenderer, "", this.titleWidth / 2 - 150, var4 + var5++ * this.textRenderer.fontHeight, 10526880);
      if (this.output.getText().length() > 0) {
         var4 += var5 * this.textRenderer.fontHeight + 16;
         this.drawString(this.textRenderer, I18n.translate("advMode.previousOutput"), this.titleWidth / 2 - 150, var4, 10526880);
         this.output.render();
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   private void m_31mdpyfqp() {
      if (this.executor.trackOutput()) {
         this.f_37mybsplm.message = "O";
         if (this.executor.getLastOutput() != null) {
            this.output.setText(this.executor.getLastOutput().buildString());
         }
      } else {
         this.f_37mybsplm.message = "X";
         this.output.setText("-");
      }
   }
}
