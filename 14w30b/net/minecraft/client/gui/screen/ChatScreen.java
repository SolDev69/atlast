package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.CommandSuggestionsC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private String lastChatMessage = "";
   private int messageHistorySize = -1;
   private boolean reset;
   private boolean completed;
   private int currentMessageId;
   private List messageHistory = Lists.newArrayList();
   protected TextFieldWidget chatField;
   private String initialChatText = "";

   public ChatScreen() {
   }

   public ChatScreen(String initialChatText) {
      this.initialChatText = initialChatText;
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.messageHistorySize = this.client.gui.getChat().getRecentMessages().size();
      this.chatField = new TextFieldWidget(0, this.textRenderer, 4, this.height - 12, this.titleWidth - 4, 12);
      this.chatField.setMaxLength(100);
      this.chatField.setHasBorder(false);
      this.chatField.setFocused(true);
      this.chatField.setText(this.initialChatText);
      this.chatField.setFocusUnlocked(false);
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
      this.client.gui.getChat().resetScroll();
   }

   @Override
   public void tick() {
      this.chatField.tick();
   }

   @Override
   protected void keyPressed(char chr, int key) {
      this.completed = false;
      if (key == 15) {
         this.reset();
      } else {
         this.reset = false;
      }

      if (key == 1) {
         this.client.openScreen(null);
      } else if (key == 28 || key == 156) {
         String var3 = this.chatField.getText().trim();
         if (var3.length() > 0) {
            this.m_03juvdrce(var3);
         }

         this.client.openScreen(null);
      } else if (key == 200) {
         this.goThroughHistory(-1);
      } else if (key == 208) {
         this.goThroughHistory(1);
      } else if (key == 201) {
         this.client.gui.getChat().scroll(this.client.gui.getChat().getVisibleLineCount() - 1);
      } else if (key == 209) {
         this.client.gui.getChat().scroll(-this.client.gui.getChat().getVisibleLineCount() + 1);
      } else {
         this.chatField.keyPressed(chr, key);
      }
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      int var1 = Mouse.getEventDWheel();
      if (var1 != 0) {
         if (var1 > 1) {
            var1 = 1;
         }

         if (var1 < -1) {
            var1 = -1;
         }

         if (!isShiftDown()) {
            var1 *= 7;
         }

         this.client.gui.getChat().scroll(var1);
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         Text var4 = this.client.gui.getChat().getMessageAt(Mouse.getX(), Mouse.getY());
         if (this.m_35nuugpna(var4)) {
            return;
         }
      }

      this.chatField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void m_01hkthhvu(String string, boolean bl) {
      if (bl) {
         this.chatField.setText(string);
      } else {
         this.chatField.write(string);
      }
   }

   public void reset() {
      if (this.reset) {
         this.chatField.eraseCharacters(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false) - this.chatField.getCursor());
         if (this.currentMessageId >= this.messageHistory.size()) {
            this.currentMessageId = 0;
         }
      } else {
         int var1 = this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false);
         this.messageHistory.clear();
         this.currentMessageId = 0;
         String var2 = this.chatField.getText().substring(var1).toLowerCase();
         String var3 = this.chatField.getText().substring(0, this.chatField.getCursor());
         this.goThroughHistory(var3, var2);
         if (this.messageHistory.isEmpty()) {
            return;
         }

         this.reset = true;
         this.chatField.eraseCharacters(var1 - this.chatField.getCursor());
      }

      if (this.messageHistory.size() > 1) {
         StringBuilder var4 = new StringBuilder();

         for(String var6 : this.messageHistory) {
            if (var4.length() > 0) {
               var4.append(", ");
            }

            var4.append(var6);
         }

         this.client.gui.getChat().addMessage(new LiteralText(var4.toString()), 1);
      }

      this.chatField.write((String)this.messageHistory.get(this.currentMessageId++));
   }

   private void goThroughHistory(String text, String cursor) {
      if (text.length() >= 1) {
         this.client.player.networkHandler.sendPacket(new CommandSuggestionsC2SPacket(text));
         this.completed = true;
      }
   }

   public void goThroughHistory(int numberOfNewMessages) {
      int var2 = this.messageHistorySize + numberOfNewMessages;
      int var3 = this.client.gui.getChat().getRecentMessages().size();
      var2 = MathHelper.clamp(var2, 0, var3);
      if (var2 != this.messageHistorySize) {
         if (var2 == var3) {
            this.messageHistorySize = var3;
            this.chatField.setText(this.lastChatMessage);
         } else {
            if (this.messageHistorySize == var3) {
               this.lastChatMessage = this.chatField.getText();
            }

            this.chatField.setText((String)this.client.gui.getChat().getRecentMessages().get(var2));
            this.messageHistorySize = var2;
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      fill(2, this.height - 14, this.titleWidth - 2, this.height - 2, Integer.MIN_VALUE);
      this.chatField.render();
      Text var4 = this.client.gui.getChat().getMessageAt(Mouse.getX(), Mouse.getY());
      if (var4 != null && var4.getStyle().getHoverEvent() != null) {
         this.m_44svopzkj(var4, mouseX, mouseY);
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   public void setMessageHistory(String[] messages) {
      if (this.completed) {
         this.reset = false;
         this.messageHistory.clear();

         for(String var5 : messages) {
            if (var5.length() > 0) {
               this.messageHistory.add(var5);
            }
         }

         String var6 = this.chatField.getText().substring(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false));
         String var7 = StringUtils.getCommonPrefix(messages);
         if (var7.length() > 0 && !var6.equalsIgnoreCase(var7)) {
            this.chatField.eraseCharacters(this.chatField.getWordSkipPosition(-1, this.chatField.getCursor(), false) - this.chatField.getCursor());
            this.chatField.write(var7);
         } else if (this.messageHistory.size() > 0) {
            this.reset = true;
            this.reset();
         }
      }
   }

   @Override
   public boolean shouldPauseGame() {
      return false;
   }
}
