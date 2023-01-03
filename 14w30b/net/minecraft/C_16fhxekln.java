package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

@Environment(EnvType.CLIENT)
public class C_16fhxekln extends Screen {
   private static final Formatting f_57rmxyaqh = Formatting.DARK_GREEN;
   private static final Formatting f_74xdlhavs = Formatting.RED;
   private static final Formatting f_53tofmwls = Formatting.DARK_PURPLE;
   private final ChatUserInfo f_38axfdljk;
   private final Text f_83gbfonjh;
   private final List f_98hzkcgxk = Lists.newArrayList();
   private final TwitchStream f_87rmjyfad;
   private int f_21mrmnqzu;

   public C_16fhxekln(TwitchStream c_10cpfpsju, ChatUserInfo chatUserInfo) {
      this.f_87rmjyfad = c_10cpfpsju;
      this.f_38axfdljk = chatUserInfo;
      this.f_83gbfonjh = new LiteralText(chatUserInfo.displayName);
      this.f_98hzkcgxk.addAll(m_67zrekjkv(chatUserInfo.modes, chatUserInfo.subscriptions, c_10cpfpsju));
   }

   public static List m_67zrekjkv(Set set, Set set2, TwitchStream c_10cpfpsju) {
      String var3 = c_10cpfpsju == null ? null : c_10cpfpsju.m_93llpijut();
      boolean var4 = c_10cpfpsju != null && c_10cpfpsju.m_31gtetblr();
      ArrayList var5 = Lists.newArrayList();

      for(ChatUserMode var7 : set) {
         Text var8 = m_56hdagcll(var7, var3, var4);
         if (var8 != null) {
            LiteralText var9 = new LiteralText("- ");
            var9.append(var8);
            var5.add(var9);
         }
      }

      for(ChatUserSubscription var11 : set2) {
         Text var12 = m_62imnlqxm(var11, var3, var4);
         if (var12 != null) {
            LiteralText var13 = new LiteralText("- ");
            var13.append(var12);
            var5.add(var13);
         }
      }

      return var5;
   }

   public static Text m_62imnlqxm(ChatUserSubscription chatUserSubscription, String string, boolean bl) {
      TranslatableText var3 = null;
      if (chatUserSubscription == ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) {
         if (string == null) {
            var3 = new TranslatableText("stream.user.subscription.subscriber");
         } else if (bl) {
            var3 = new TranslatableText("stream.user.subscription.subscriber.self");
         } else {
            var3 = new TranslatableText("stream.user.subscription.subscriber.other", string);
         }

         var3.getStyle().setColor(f_57rmxyaqh);
      } else if (chatUserSubscription == ChatUserSubscription.TTV_CHAT_USERSUB_TURBO) {
         var3 = new TranslatableText("stream.user.subscription.turbo");
         var3.getStyle().setColor(f_53tofmwls);
      }

      return var3;
   }

   public static Text m_56hdagcll(ChatUserMode chatUserMode, String string, boolean bl) {
      TranslatableText var3 = null;
      if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR) {
         var3 = new TranslatableText("stream.user.mode.administrator");
         var3.getStyle().setColor(f_53tofmwls);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_BANNED) {
         if (string == null) {
            var3 = new TranslatableText("stream.user.mode.banned");
         } else if (bl) {
            var3 = new TranslatableText("stream.user.mode.banned.self");
         } else {
            var3 = new TranslatableText("stream.user.mode.banned.other", string);
         }

         var3.getStyle().setColor(f_74xdlhavs);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_BROADCASTER) {
         if (string == null) {
            var3 = new TranslatableText("stream.user.mode.broadcaster");
         } else if (bl) {
            var3 = new TranslatableText("stream.user.mode.broadcaster.self");
         } else {
            var3 = new TranslatableText("stream.user.mode.broadcaster.other");
         }

         var3.getStyle().setColor(f_57rmxyaqh);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_MODERATOR) {
         if (string == null) {
            var3 = new TranslatableText("stream.user.mode.moderator");
         } else if (bl) {
            var3 = new TranslatableText("stream.user.mode.moderator.self");
         } else {
            var3 = new TranslatableText("stream.user.mode.moderator.other", string);
         }

         var3.getStyle().setColor(f_57rmxyaqh);
      } else if (chatUserMode == ChatUserMode.TTV_CHAT_USERMODE_STAFF) {
         var3 = new TranslatableText("stream.user.mode.staff");
         var3.getStyle().setColor(f_53tofmwls);
      }

      return var3;
   }

   @Override
   public void init() {
      int var1 = this.titleWidth / 3;
      int var2 = var1 - 130;
      this.buttons.add(new ButtonWidget(1, var1 * 0 + var2 / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.timeout")));
      this.buttons.add(new ButtonWidget(0, var1 * 1 + var2 / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.ban")));
      this.buttons.add(new ButtonWidget(2, var1 * 2 + var2 / 2, this.height - 70, 130, 20, I18n.translate("stream.userinfo.mod")));
      this.buttons.add(new ButtonWidget(5, var1 * 0 + var2 / 2, this.height - 45, 130, 20, I18n.translate("gui.cancel")));
      this.buttons.add(new ButtonWidget(3, var1 * 1 + var2 / 2, this.height - 45, 130, 20, I18n.translate("stream.userinfo.unban")));
      this.buttons.add(new ButtonWidget(4, var1 * 2 + var2 / 2, this.height - 45, 130, 20, I18n.translate("stream.userinfo.unmod")));
      int var3 = 0;

      for(Text var5 : this.f_98hzkcgxk) {
         var3 = Math.max(var3, this.textRenderer.getStringWidth(var5.buildFormattedString()));
      }

      this.f_21mrmnqzu = this.titleWidth / 2 - var3 / 2;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 0) {
            this.f_87rmjyfad.m_37rrqnjgy("/ban " + this.f_38axfdljk.displayName);
         } else if (buttonWidget.id == 3) {
            this.f_87rmjyfad.m_37rrqnjgy("/unban " + this.f_38axfdljk.displayName);
         } else if (buttonWidget.id == 2) {
            this.f_87rmjyfad.m_37rrqnjgy("/mod " + this.f_38axfdljk.displayName);
         } else if (buttonWidget.id == 4) {
            this.f_87rmjyfad.m_37rrqnjgy("/unmod " + this.f_38axfdljk.displayName);
         } else if (buttonWidget.id == 1) {
            this.f_87rmjyfad.m_37rrqnjgy("/timeout " + this.f_38axfdljk.displayName);
         }

         this.client.openScreen(null);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.f_83gbfonjh.buildString(), this.titleWidth / 2, 70, 16777215);
      int var4 = 80;

      for(Text var6 : this.f_98hzkcgxk) {
         this.drawString(this.textRenderer, var6.buildFormattedString(), this.f_21mrmnqzu, var4, 16777215);
         var4 += this.textRenderer.fontHeight;
      }

      super.render(mouseX, mouseY, tickDelta);
   }
}
