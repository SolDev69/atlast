package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Session;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.twitch.ErrorTwitchStream;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Utils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import tv.twitch.ErrorCode;

@Environment(EnvType.CLIENT)
public class C_39cmizuwc extends Screen {
   private static final Logger f_14dovqamj = LogManager.getLogger();
   private final Text f_98smkrxqv = new TranslatableText("stream.unavailable.title");
   private final Screen f_30xnzehfq;
   private final C_39cmizuwc.C_90xhaejka f_34lhqvevy;
   private final List f_64hmayzaz;
   private final List f_97nbdgfvd = Lists.newArrayList();

   public C_39cmizuwc(Screen c_31rdgoemj, C_39cmizuwc.C_90xhaejka c_90xhaejka) {
      this(c_31rdgoemj, c_90xhaejka, null);
   }

   public C_39cmizuwc(Screen c_31rdgoemj, C_39cmizuwc.C_90xhaejka c_90xhaejka, List list) {
      this.f_30xnzehfq = c_31rdgoemj;
      this.f_34lhqvevy = c_90xhaejka;
      this.f_64hmayzaz = list;
   }

   @Override
   public void init() {
      if (this.f_97nbdgfvd.isEmpty()) {
         this.f_97nbdgfvd.addAll(this.textRenderer.wrapLines(this.f_34lhqvevy.m_74snqexks().buildFormattedString(), (int)((float)this.titleWidth * 0.75F)));
         if (this.f_64hmayzaz != null) {
            this.f_97nbdgfvd.add("");

            for(TranslatableText var2 : this.f_64hmayzaz) {
               this.f_97nbdgfvd.add(var2.getString());
            }
         }
      }

      if (this.f_34lhqvevy.m_89aksneee() != null) {
         this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 155, this.height - 50, 150, 20, I18n.translate("gui.cancel")));
         this.buttons
            .add(
               new ButtonWidget(
                  1, this.titleWidth / 2 - 155 + 160, this.height - 50, 150, 20, I18n.translate(this.f_34lhqvevy.m_89aksneee().buildFormattedString())
               )
            );
      } else {
         this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 75, this.height - 50, 150, 20, I18n.translate("gui.cancel")));
      }
   }

   @Override
   public void removed() {
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      int var4 = Math.max((int)((double)this.height * 0.85 / 2.0 - (double)((float)(this.f_97nbdgfvd.size() * this.textRenderer.fontHeight) / 2.0F)), 50);
      this.drawCenteredString(
         this.textRenderer, this.f_98smkrxqv.buildFormattedString(), this.titleWidth / 2, var4 - this.textRenderer.fontHeight * 2, 16777215
      );

      for(String var6 : this.f_97nbdgfvd) {
         this.drawCenteredString(this.textRenderer, var6, this.titleWidth / 2, var4, 10526880);
         var4 += this.textRenderer.fontHeight;
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 1) {
            switch(this.f_34lhqvevy) {
               case ACCOUNT_NOT_BOUND:
               case FAILED_TWITCH_AUTH:
                  this.m_76uurfhvy("https://account.mojang.com/me/settings");
                  break;
               case ACCOUNT_NOT_MIGRATED:
                  this.m_76uurfhvy("https://account.mojang.com/migrate");
                  break;
               case UNSUPPORTED_OS_MAC:
                  this.m_76uurfhvy("http://www.apple.com/osx/");
                  break;
               case UNKNOWN:
               case LIBRARY_FAILURE:
               case INITIALIZATION_FAILURE:
                  this.m_76uurfhvy("http://bugs.mojang.com/browse/MC");
            }
         }

         this.client.openScreen(this.f_30xnzehfq);
      }
   }

   private void m_76uurfhvy(String string) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop").invoke(null);
         var2.getMethod("browse", URI.class).invoke(var3, new URI(string));
      } catch (Throwable var4) {
         f_14dovqamj.error("Couldn't open link", var4);
      }
   }

   public static void m_95brgjnxo(Screen c_31rdgoemj) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      TwitchStream var2 = var1.getTwitchStream();
      if (!GLX.useFramebufferObjects) {
         ArrayList var3 = Lists.newArrayList();
         var3.add(new TranslatableText("stream.unavailable.no_fbo.version", GL11.glGetString(7938)));
         var3.add(new TranslatableText("stream.unavailable.no_fbo.blend", GLContext.getCapabilities().GL_EXT_blend_func_separate));
         var3.add(new TranslatableText("stream.unavailable.no_fbo.arb", GLContext.getCapabilities().GL_ARB_framebuffer_object));
         var3.add(new TranslatableText("stream.unavailable.no_fbo.ext", GLContext.getCapabilities().GL_EXT_framebuffer_object));
         var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.NO_FBO, var3));
      } else if (var2 instanceof ErrorTwitchStream) {
         if (((ErrorTwitchStream)var2).getError().getMessage().contains("Can't load AMD 64-bit .dll on a IA 32-bit platform")) {
            var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.LIBRARY_ARCH_MISMATCH));
         } else {
            var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.LIBRARY_FAILURE));
         }
      } else if (!var2.m_78qjaxsih() && var2.m_69ungrimn() == ErrorCode.TTV_EC_OS_TOO_OLD) {
         switch(Utils.getOS()) {
            case WINDOWS:
               var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.UNSUPPORTED_OS_WINDOWS));
               break;
            case MACOS:
               var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.UNSUPPORTED_OS_MAC));
               break;
            default:
               var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.UNSUPPORTED_OS_OTHER));
         }
      } else if (!var1.getUserProperties().containsKey("twitch_access_token")) {
         if (var1.getSession().getType() == Session.Type.LEGACY) {
            var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.ACCOUNT_NOT_MIGRATED));
         } else {
            var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.ACCOUNT_NOT_BOUND));
         }
      } else if (!var2.m_84uftderi()) {
         switch(var2.m_56qzkeokj()) {
            case INVALID_TOKEN:
               var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.FAILED_TWITCH_AUTH));
               break;
            case ERROR:
            default:
               var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.FAILED_TWITCH_AUTH_ERROR));
         }
      } else if (var2.m_69ungrimn() != null) {
         List var4 = Arrays.asList(new TranslatableText("stream.unavailable.initialization_failure.extra", ErrorCode.getString(var2.m_69ungrimn())));
         var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.INITIALIZATION_FAILURE, var4));
      } else {
         var1.openScreen(new C_39cmizuwc(c_31rdgoemj, C_39cmizuwc.C_90xhaejka.UNKNOWN));
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum C_90xhaejka {
      NO_FBO(new TranslatableText("stream.unavailable.no_fbo")),
      LIBRARY_ARCH_MISMATCH(new TranslatableText("stream.unavailable.library_arch_mismatch")),
      LIBRARY_FAILURE(new TranslatableText("stream.unavailable.library_failure"), new TranslatableText("stream.unavailable.report_to_mojang")),
      UNSUPPORTED_OS_WINDOWS(new TranslatableText("stream.unavailable.not_supported.windows")),
      UNSUPPORTED_OS_MAC(new TranslatableText("stream.unavailable.not_supported.mac"), new TranslatableText("stream.unavailable.not_supported.mac.okay")),
      UNSUPPORTED_OS_OTHER(new TranslatableText("stream.unavailable.not_supported.other")),
      ACCOUNT_NOT_MIGRATED(
         new TranslatableText("stream.unavailable.account_not_migrated"), new TranslatableText("stream.unavailable.account_not_migrated.okay")
      ),
      ACCOUNT_NOT_BOUND(new TranslatableText("stream.unavailable.account_not_bound"), new TranslatableText("stream.unavailable.account_not_bound.okay")),
      FAILED_TWITCH_AUTH(new TranslatableText("stream.unavailable.failed_auth"), new TranslatableText("stream.unavailable.failed_auth.okay")),
      FAILED_TWITCH_AUTH_ERROR(new TranslatableText("stream.unavailable.failed_auth_error")),
      INITIALIZATION_FAILURE(new TranslatableText("stream.unavailable.initialization_failure"), new TranslatableText("stream.unavailable.report_to_mojang")),
      UNKNOWN(new TranslatableText("stream.unavailable.unknown"), new TranslatableText("stream.unavailable.report_to_mojang"));

      private final Text f_42witrzbj;
      private final Text f_71kbnzpzm;

      private C_90xhaejka(Text c_21uoltggz) {
         this(c_21uoltggz, null);
      }

      private C_90xhaejka(Text c_21uoltggz, Text c_21uoltggz2) {
         this.f_42witrzbj = c_21uoltggz;
         this.f_71kbnzpzm = c_21uoltggz2;
      }

      public Text m_74snqexks() {
         return this.f_42witrzbj;
      }

      public Text m_89aksneee() {
         return this.f_71kbnzpzm;
      }
   }
}
