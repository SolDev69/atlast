package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import tv.twitch.broadcast.IngestServer;

@Environment(EnvType.CLIENT)
public class C_84olcqpef extends Screen {
   private final Screen f_23zixhcha;
   private String f_44kmhafns;
   private C_84olcqpef.C_83iydwvjg f_16labjtpv;

   public C_84olcqpef(Screen c_31rdgoemj) {
      this.f_23zixhcha = c_31rdgoemj;
   }

   @Override
   public void init() {
      this.f_44kmhafns = I18n.translate("options.stream.ingest.title");
      this.f_16labjtpv = new C_84olcqpef.C_83iydwvjg(this.client);
      if (!this.client.getTwitchStream().m_70uizoygc()) {
         this.client.getTwitchStream().m_81qbezqee();
      }

      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 155, this.height - 24 - 6, 150, 20, I18n.translate("gui.done")));
      this.buttons.add(new ButtonWidget(2, this.titleWidth / 2 + 5, this.height - 24 - 6, 150, 20, I18n.translate("options.stream.ingest.reset")));
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.f_16labjtpv.m_94jnhyuiz();
   }

   @Override
   public void removed() {
      if (this.client.getTwitchStream().m_70uizoygc()) {
         this.client.getTwitchStream().m_39mbbqlvd().m_27plsqiyq();
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 1) {
            this.client.openScreen(this.f_23zixhcha);
         } else {
            this.client.options.streamPreferredServer = "";
            this.client.options.save();
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.f_16labjtpv.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.f_44kmhafns, this.titleWidth / 2, 20, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   @Environment(EnvType.CLIENT)
   class C_83iydwvjg extends ListWidget {
      public C_83iydwvjg(MinecraftClient c_13piauvdk) {
         super(
            c_13piauvdk,
            C_84olcqpef.this.titleWidth,
            C_84olcqpef.this.height,
            32,
            C_84olcqpef.this.height - 35,
            (int)((double)c_13piauvdk.textRenderer.fontHeight * 3.5)
         );
         this.setRenderSelection(false);
      }

      @Override
      protected int getEntriesSize() {
         return this.client.getTwitchStream().m_96xxepobn().length;
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         this.client.options.streamPreferredServer = this.client.getTwitchStream().m_96xxepobn()[y].serverUrl;
         this.client.options.save();
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return this.client.getTwitchStream().m_96xxepobn()[index].serverUrl.equals(this.client.options.streamPreferredServer);
      }

      @Override
      protected void renderBackground() {
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         IngestServer var7 = this.client.getTwitchStream().m_96xxepobn()[index];
         String var8 = var7.serverUrl.replaceAll("\\{stream_key\\}", "");
         String var9 = (int)var7.bitrateKbps + " kbps";
         String var10 = null;
         C_02ymikian var11 = this.client.getTwitchStream().m_39mbbqlvd();
         if (var11 != null) {
            if (var7 == var11.m_96wppvscx()) {
               var8 = Formatting.GREEN + var8;
               var9 = (int)(var11.m_75yqgekyx() * 100.0F) + "%";
            } else if (index < var11.m_92efldgvl()) {
               if (var7.bitrateKbps == 0.0F) {
                  var9 = Formatting.RED + "Down!";
               }
            } else {
               var9 = Formatting.OBFUSCATED + "1234" + Formatting.RESET + " kbps";
            }
         } else if (var7.bitrateKbps == 0.0F) {
            var9 = Formatting.RED + "Down!";
         }

         x -= 15;
         if (this.isEntrySelected(index)) {
            var10 = Formatting.BLUE + "(Preferred)";
         } else if (var7.defaultServer) {
            var10 = Formatting.GREEN + "(Default)";
         }

         C_84olcqpef.this.drawString(C_84olcqpef.this.textRenderer, var7.serverName, x + 2, y + 5, 16777215);
         C_84olcqpef.this.drawString(C_84olcqpef.this.textRenderer, var8, x + 2, y + C_84olcqpef.this.textRenderer.fontHeight + 5 + 3, 3158064);
         C_84olcqpef.this.drawString(
            C_84olcqpef.this.textRenderer, var9, this.getScrollbarPosition() - 5 - C_84olcqpef.this.textRenderer.getStringWidth(var9), y + 5, 8421504
         );
         if (var10 != null) {
            C_84olcqpef.this.drawString(
               C_84olcqpef.this.textRenderer,
               var10,
               this.getScrollbarPosition() - 5 - C_84olcqpef.this.textRenderer.getStringWidth(var10),
               y + 5 + 3 + C_84olcqpef.this.textRenderer.fontHeight,
               8421504
            );
         }
      }

      @Override
      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 15;
      }
   }
}
