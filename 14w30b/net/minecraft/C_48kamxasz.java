package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.twitch.StreamMetadata;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NetworkUtils;
import net.minecraft.util.Utils;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.VideoParams;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

@Environment(EnvType.CLIENT)
public class C_48kamxasz implements C_48uodvatu.C_46ljtchyh, C_55vyyvcbb.C_21ughtocu, C_02ymikian.C_91iqijbyp, TwitchStream {
   private static final Logger f_46iadcgpz = LogManager.getLogger();
   public static final Marker f_44ugpmdwg = MarkerManager.getMarker("STREAM");
   private final C_48uodvatu f_41kmeqlyf;
   private final C_55vyyvcbb f_76hmlkikd;
   private String f_91sitixin;
   private final MinecraftClient client;
   private final Text f_06kvaaweb = new LiteralText("Twitch");
   private final Map f_64oyhkirw = Maps.newHashMap();
   private RenderTarget f_43zlxxjri;
   private boolean f_63ypznbcc;
   private int f_31ohnedcc = 30;
   private long f_40orwuofq = 0L;
   private boolean f_97ehhrbhc = false;
   private boolean f_28onvmrhu;
   private boolean f_05mrrqpjk;
   private boolean f_83ogtloxv;
   private TwitchStream.C_76cprrnwi f_44ugtyatv = TwitchStream.C_76cprrnwi.ERROR;
   private static boolean f_02dscwenx;

   public C_48kamxasz(MinecraftClient client, Property property) {
      this.client = client;
      this.f_41kmeqlyf = new C_48uodvatu();
      this.f_76hmlkikd = new C_55vyyvcbb();
      this.f_41kmeqlyf.m_97gacbzce(this);
      this.f_76hmlkikd.m_89waqnhfj(this);
      this.f_41kmeqlyf.m_45lxtuzmf("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.f_76hmlkikd.m_50ioolqzh("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.f_06kvaaweb.getStyle().setColor(Formatting.DARK_PURPLE);
      if (property != null && !Strings.isNullOrEmpty(property.getValue()) && GLX.useFramebufferObjects) {
         Thread var3 = new Thread("Twitch authenticator") {
            @Override
            public void run() {
               try {
                  URL var1 = new URL("https://api.twitch.tv/kraken?oauth_token=" + URLEncoder.encode(property.getValue(), "UTF-8"));
                  String var2 = NetworkUtils.getUrlContents(var1);
                  JsonObject var3 = JsonUtils.asJsonObject(new JsonParser().parse(var2), "Response");
                  JsonObject var4 = JsonUtils.getJsonObject(var3, "token");
                  if (JsonUtils.getBoolean(var4, "valid")) {
                     String var5 = JsonUtils.getString(var4, "user_name");
                     C_48kamxasz.f_46iadcgpz.debug(C_48kamxasz.f_44ugpmdwg, "Authenticated with twitch; username is {}", new Object[]{var5});
                     AuthToken var6 = new AuthToken();
                     var6.data = property.getValue();
                     C_48kamxasz.this.f_41kmeqlyf.m_86dgajljz(var5, var6);
                     C_48kamxasz.this.f_76hmlkikd.m_23ywwutzp(var5);
                     C_48kamxasz.this.f_76hmlkikd.m_83aycowfd(var6);
                     Runtime.getRuntime().addShutdownHook(new Thread("Twitch shutdown hook") {
                        @Override
                        public void run() {
                           C_48kamxasz.this.m_08tceactq();
                        }
                     });
                     C_48kamxasz.this.f_41kmeqlyf.m_65xvjefbd();
                     C_48kamxasz.this.f_76hmlkikd.m_00ktxmyoz();
                  } else {
                     C_48kamxasz.this.f_44ugtyatv = TwitchStream.C_76cprrnwi.INVALID_TOKEN;
                     C_48kamxasz.f_46iadcgpz.error(C_48kamxasz.f_44ugpmdwg, "Given twitch access token is invalid");
                  }
               } catch (IOException var7) {
                  C_48kamxasz.this.f_44ugtyatv = TwitchStream.C_76cprrnwi.ERROR;
                  C_48kamxasz.f_46iadcgpz.error(C_48kamxasz.f_44ugpmdwg, "Could not authenticate with twitch", var7);
               }
            }
         };
         var3.setDaemon(true);
         var3.start();
      }
   }

   @Override
   public void m_08tceactq() {
      f_46iadcgpz.debug(f_44ugpmdwg, "Shutdown streaming");
      this.f_41kmeqlyf.m_11kkkddtg();
      this.f_76hmlkikd.m_60ievhajv();
   }

   @Override
   public void m_26lsqwmff() {
      int var1 = this.client.options.streamChatEnabled;
      boolean var2 = this.f_91sitixin != null && this.f_76hmlkikd.m_23bnorqlj(this.f_91sitixin);
      boolean var3 = this.f_76hmlkikd.m_42slekjgq() == C_55vyyvcbb.C_63ocjvuuw.INITIALIZED
         && (this.f_91sitixin == null || this.f_76hmlkikd.m_90xvfyguu(this.f_91sitixin) == C_55vyyvcbb.C_66pbsbnsh.DISCONNECTED);
      if (var1 == 2) {
         if (var2) {
            f_46iadcgpz.debug(f_44ugpmdwg, "Disconnecting from twitch chat per user options");
            this.f_76hmlkikd.m_77eyyxfhf(this.f_91sitixin);
         }
      } else if (var1 == 1) {
         if (var3 && this.f_41kmeqlyf.m_39vgpzmvw()) {
            f_46iadcgpz.debug(f_44ugpmdwg, "Connecting to twitch chat per user options");
            this.m_27wdqnuie();
         }
      } else if (var1 == 0) {
         if (var2 && !this.m_99rcqogzt()) {
            f_46iadcgpz.debug(f_44ugpmdwg, "Disconnecting from twitch chat as user is no longer streaming");
            this.f_76hmlkikd.m_77eyyxfhf(this.f_91sitixin);
         } else if (var3 && this.m_99rcqogzt()) {
            f_46iadcgpz.debug(f_44ugpmdwg, "Connecting to twitch chat as user is streaming");
            this.m_27wdqnuie();
         }
      }

      this.f_41kmeqlyf.m_65tmpsvnh();
      this.f_76hmlkikd.m_12grkirzd();
   }

   protected void m_27wdqnuie() {
      C_55vyyvcbb.C_63ocjvuuw var1 = this.f_76hmlkikd.m_42slekjgq();
      String var2 = this.f_41kmeqlyf.m_25mpxeyvu().name;
      this.f_91sitixin = var2;
      if (var1 != C_55vyyvcbb.C_63ocjvuuw.INITIALIZED) {
         f_46iadcgpz.warn("Invalid twitch chat state {}", new Object[]{var1});
      } else if (this.f_76hmlkikd.m_90xvfyguu(this.f_91sitixin) == C_55vyyvcbb.C_66pbsbnsh.DISCONNECTED) {
         this.f_76hmlkikd.m_75aklpftj(var2);
      } else {
         f_46iadcgpz.warn("Invalid twitch chat state {}", new Object[]{var1});
      }
   }

   @Override
   public void m_81hdmzdjh() {
      if (this.f_41kmeqlyf.m_98lkbozru() && !this.f_41kmeqlyf.m_99qghnojx()) {
         long var1 = System.nanoTime();
         long var3 = (long)(1000000000 / this.f_31ohnedcc);
         long var5 = var1 - this.f_40orwuofq;
         boolean var7 = var5 >= var3;
         if (var7) {
            FrameBuffer var8 = this.f_41kmeqlyf.m_88cciqyzd();
            RenderTarget var9 = this.client.getRenderTarget();
            this.f_43zlxxjri.bindWrite(true);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, (double)this.f_43zlxxjri.viewWidth, (double)this.f_43zlxxjri.viewHeight, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.viewport(0, 0, this.f_43zlxxjri.viewWidth, this.f_43zlxxjri.viewHeight);
            GlStateManager.enableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            float var10 = (float)this.f_43zlxxjri.viewWidth;
            float var11 = (float)this.f_43zlxxjri.viewHeight;
            float var12 = (float)var9.viewWidth / (float)var9.width;
            float var13 = (float)var9.viewHeight / (float)var9.height;
            var9.bindRead();
            GL11.glTexParameterf(3553, 10241, 9729.0F);
            GL11.glTexParameterf(3553, 10240, 9729.0F);
            Tessellator var14 = Tessellator.getInstance();
            BufferBuilder var15 = var14.getBufferBuilder();
            var15.start();
            var15.vertex(0.0, (double)var11, 0.0, 0.0, (double)var13);
            var15.vertex((double)var10, (double)var11, 0.0, (double)var12, (double)var13);
            var15.vertex((double)var10, 0.0, 0.0, (double)var12, 0.0);
            var15.vertex(0.0, 0.0, 0.0, 0.0, 0.0);
            var14.end();
            var9.unbindRead();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.f_41kmeqlyf.m_42lihkxbq(var8);
            this.f_43zlxxjri.unbindWrite();
            this.f_41kmeqlyf.m_68lbgyafe(var8);
            this.f_40orwuofq = var1;
         }
      }
   }

   @Override
   public boolean m_01cdoylst() {
      return this.f_41kmeqlyf.m_39vgpzmvw();
   }

   @Override
   public boolean m_10yutarck() {
      return this.f_41kmeqlyf.m_92irlaqgq();
   }

   @Override
   public boolean m_99rcqogzt() {
      return this.f_41kmeqlyf.m_98lkbozru();
   }

   @Override
   public void m_27hgmbctc(StreamMetadata c_29pxubhlq, long l) {
      if (this.m_99rcqogzt() && this.f_63ypznbcc) {
         long var4 = this.f_41kmeqlyf.m_75jnytxwc();
         if (!this.f_41kmeqlyf.m_22catrsfj(c_29pxubhlq.getId(), var4 + l, c_29pxubhlq.getMessage(), c_29pxubhlq.serialize())) {
            f_46iadcgpz.warn(f_44ugpmdwg, "Couldn't send stream metadata action at {}: {}", new Object[]{var4 + l, c_29pxubhlq});
         } else {
            f_46iadcgpz.debug(f_44ugpmdwg, "Sent stream metadata action at {}: {}", new Object[]{var4 + l, c_29pxubhlq});
         }
      }
   }

   @Override
   public void m_75vuvosit(StreamMetadata c_29pxubhlq, long l, long m) {
      if (this.m_99rcqogzt() && this.f_63ypznbcc) {
         long var6 = this.f_41kmeqlyf.m_75jnytxwc();
         String var8 = c_29pxubhlq.getMessage();
         String var9 = c_29pxubhlq.serialize();
         long var10 = this.f_41kmeqlyf.m_47leaclky(c_29pxubhlq.getId(), var6 + l, var8, var9);
         if (var10 < 0L) {
            f_46iadcgpz.warn(f_44ugpmdwg, "Could not send stream metadata sequence from {} to {}: {}", new Object[]{var6 + l, var6 + m, c_29pxubhlq});
         } else if (this.f_41kmeqlyf.m_23rqjmuhg(c_29pxubhlq.getId(), var6 + m, var10, var8, var9)) {
            f_46iadcgpz.debug(f_44ugpmdwg, "Sent stream metadata sequence from {} to {}: {}", new Object[]{var6 + l, var6 + m, c_29pxubhlq});
         } else {
            f_46iadcgpz.warn(f_44ugpmdwg, "Half-sent stream metadata sequence from {} to {}: {}", new Object[]{var6 + l, var6 + m, c_29pxubhlq});
         }
      }
   }

   @Override
   public boolean m_59ybwvnxm() {
      return this.f_41kmeqlyf.m_99qghnojx();
   }

   @Override
   public void m_67lgjitba() {
      if (this.f_41kmeqlyf.m_15aerwprk()) {
         f_46iadcgpz.debug(f_44ugpmdwg, "Requested commercial from Twitch");
      } else {
         f_46iadcgpz.warn(f_44ugpmdwg, "Could not request commercial from Twitch");
      }
   }

   @Override
   public void m_62rfzgdrt() {
      this.f_41kmeqlyf.m_95egnealx();
      this.f_05mrrqpjk = true;
      this.m_19ohgzkkn();
   }

   @Override
   public void m_59fglhcmk() {
      this.f_41kmeqlyf.m_18szvwtzn();
      this.f_05mrrqpjk = false;
      this.m_19ohgzkkn();
   }

   @Override
   public void m_19ohgzkkn() {
      if (this.m_99rcqogzt()) {
         float var1 = this.client.options.streamSystemVolume;
         boolean var2 = this.f_05mrrqpjk || var1 <= 0.0F;
         this.f_41kmeqlyf.m_26puheqiy(var2 ? 0.0F : var1);
         this.f_41kmeqlyf.m_46nqcttzh(this.m_81cvlsrmx() ? 0.0F : this.client.options.streamMicVolume);
      }
   }

   @Override
   public void m_48nqrofgp() {
      GameOptions var1 = this.client.options;
      VideoParams var2 = this.f_41kmeqlyf
         .m_20ckpkrpu(
            m_24jzkvtzu(var1.streamKbps),
            m_41sqvmjqh(var1.streamFps),
            m_67xemlvwq(var1.streamBytesPerPixel),
            (float)this.client.width / (float)this.client.height
         );
      switch(var1.streamCompression) {
         case 0:
            var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_LOW;
            break;
         case 1:
            var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_MEDIUM;
            break;
         case 2:
            var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
      }

      if (this.f_43zlxxjri == null) {
         this.f_43zlxxjri = new RenderTarget(var2.outputWidth, var2.outputHeight, false);
      } else {
         this.f_43zlxxjri.resize(var2.outputWidth, var2.outputHeight);
      }

      if (var1.streamPreferredServer != null && var1.streamPreferredServer.length() > 0) {
         for(IngestServer var6 : this.m_96xxepobn()) {
            if (var6.serverUrl.equals(var1.streamPreferredServer)) {
               this.f_41kmeqlyf.m_65jmctixg(var6);
               break;
            }
         }
      }

      this.f_31ohnedcc = var2.targetFps;
      this.f_63ypznbcc = var1.streamSendMetadata;
      this.f_41kmeqlyf.m_83cpsfukk(var2);
      f_46iadcgpz.info(
         f_44ugpmdwg,
         "Streaming at {}/{} at {} kbps to {}",
         new Object[]{var2.outputWidth, var2.outputHeight, var2.maxKbps, this.f_41kmeqlyf.m_88esecany().serverUrl}
      );
      this.f_41kmeqlyf.m_84wcgbjlk(null, "Minecraft", null);
   }

   @Override
   public void stopStream() {
      if (this.f_41kmeqlyf.m_72fnodvaw()) {
         f_46iadcgpz.info(f_44ugpmdwg, "Stopped streaming to Twitch");
      } else {
         f_46iadcgpz.warn(f_44ugpmdwg, "Could not stop streaming to Twitch");
      }
   }

   @Override
   public void m_45lfkayus(ErrorCode errorCode, AuthToken authToken) {
   }

   @Override
   public void m_36rztryrt(ErrorCode errorCode) {
      if (ErrorCode.succeeded(errorCode)) {
         f_46iadcgpz.debug(f_44ugpmdwg, "Login attempt successful");
         this.f_28onvmrhu = true;
      } else {
         f_46iadcgpz.warn(f_44ugpmdwg, "Login attempt unsuccessful: {} (error code {})", new Object[]{ErrorCode.getString(errorCode), errorCode.getValue()});
         this.f_28onvmrhu = false;
      }
   }

   @Override
   public void m_69hcyofdx(ErrorCode errorCode, GameInfo[] gameInfos) {
   }

   @Override
   public void m_75cmyvfkk(C_48uodvatu.C_49akbipsc c_49akbipsc) {
      f_46iadcgpz.debug(f_44ugpmdwg, "Broadcast state changed to {}", new Object[]{c_49akbipsc});
      if (c_49akbipsc == C_48uodvatu.C_49akbipsc.INITIALIZED) {
         this.f_41kmeqlyf.m_09stwujcb(C_48uodvatu.C_49akbipsc.AUTHENTICATED);
      }
   }

   @Override
   public void m_42btkswwl() {
      f_46iadcgpz.info(f_44ugpmdwg, "Logged out of twitch");
   }

   @Override
   public void m_50zksmnbp(StreamInfo streamInfo) {
      f_46iadcgpz.debug(f_44ugpmdwg, "Stream info updated; {} viewers on stream ID {}", new Object[]{streamInfo.viewers, streamInfo.streamId});
   }

   @Override
   public void m_36govrjva(IngestList ingestList) {
   }

   @Override
   public void m_33yockmto(ErrorCode errorCode) {
      f_46iadcgpz.warn(f_44ugpmdwg, "Issue submitting frame: {} (Error code {})", new Object[]{ErrorCode.getString(errorCode), errorCode.getValue()});
      this.client.gui.getChat().addMessage(new LiteralText("Issue streaming frame: " + errorCode + " (" + ErrorCode.getString(errorCode) + ")"), 2);
   }

   @Override
   public void m_64kitlxsr() {
      this.m_19ohgzkkn();
      f_46iadcgpz.info(f_44ugpmdwg, "Broadcast to Twitch has started");
   }

   @Override
   public void m_22yefjwrm() {
      f_46iadcgpz.info(f_44ugpmdwg, "Broadcast to Twitch has stopped");
   }

   @Override
   public void m_11xlfllrs(ErrorCode errorCode) {
      if (errorCode == ErrorCode.TTV_EC_SOUNDFLOWER_NOT_INSTALLED) {
         TranslatableText var2 = new TranslatableText("stream.unavailable.soundflower.chat.link");
         var2.getStyle()
            .setClickEvent(
               new ClickEvent(
                  ClickEvent.Action.OPEN_URL,
                  "https://help.mojang.com/customer/portal/articles/1374877-configuring-soundflower-for-streaming-on-apple-computers"
               )
            );
         var2.getStyle().setUnderlined(true);
         TranslatableText var3 = new TranslatableText("stream.unavailable.soundflower.chat", var2);
         var3.getStyle().setColor(Formatting.DARK_RED);
         this.client.gui.getChat().addMessage(var3);
      } else {
         TranslatableText var4 = new TranslatableText("stream.unavailable.unknown.chat", ErrorCode.getString(errorCode));
         var4.getStyle().setColor(Formatting.DARK_RED);
         this.client.gui.getChat().addMessage(var4);
      }
   }

   @Override
   public void m_99ftllcql(C_02ymikian c_80yeuxqdq, C_02ymikian.C_57nyknvyx c_57nyknvyx) {
      f_46iadcgpz.debug(f_44ugpmdwg, "Ingest test state changed to {}", new Object[]{c_57nyknvyx});
      if (c_57nyknvyx == C_02ymikian.C_57nyknvyx.FINISHED) {
         this.f_97ehhrbhc = true;
      }
   }

   public static int m_41sqvmjqh(float f) {
      return MathHelper.floor(10.0F + f * 50.0F);
   }

   public static int m_24jzkvtzu(float f) {
      return MathHelper.floor(230.0F + f * 3270.0F);
   }

   public static float m_67xemlvwq(float f) {
      return 0.1F + f * 0.1F;
   }

   @Override
   public IngestServer[] m_96xxepobn() {
      return this.f_41kmeqlyf.m_18obowuxd().getServers();
   }

   @Override
   public void m_81qbezqee() {
      C_02ymikian var1 = this.f_41kmeqlyf.m_78mbabeno();
      if (var1 != null) {
         var1.m_01aumvjgp(this);
      }
   }

   @Override
   public C_02ymikian m_39mbbqlvd() {
      return this.f_41kmeqlyf.m_07hdsswup();
   }

   @Override
   public boolean m_70uizoygc() {
      return this.f_41kmeqlyf.m_05rgggjzj();
   }

   @Override
   public int m_23mxopcac() {
      return this.m_99rcqogzt() ? this.f_41kmeqlyf.m_33fzouxhp().viewers : 0;
   }

   @Override
   public void m_94hoompvc(ErrorCode errorCode) {
      if (ErrorCode.failed(errorCode)) {
         f_46iadcgpz.error(f_44ugpmdwg, "Chat failed to initialize");
      }
   }

   @Override
   public void m_95mdtnzem(ErrorCode errorCode) {
      if (ErrorCode.failed(errorCode)) {
         f_46iadcgpz.error(f_44ugpmdwg, "Chat failed to shutdown");
      }
   }

   @Override
   public void m_88pocigdf(C_55vyyvcbb.C_63ocjvuuw c_63ocjvuuw) {
   }

   @Override
   public void m_04qaurtal(String string, ChatRawMessage[] chatRawMessages) {
      for(ChatRawMessage var6 : chatRawMessages) {
         this.m_57fefqszi(var6.userName, var6);
         if (this.m_78fenssfk(var6.modes, var6.subscriptions, this.client.options.streamChatUserFilter)) {
            LiteralText var7 = new LiteralText(var6.userName);
            TranslatableText var8 = new TranslatableText(
               "chat.stream." + (var6.action ? "emote" : "text"), this.f_06kvaaweb, var7, Formatting.strip(var6.message)
            );
            if (var6.action) {
               var8.getStyle().setItalic(true);
            }

            LiteralText var9 = new LiteralText("");
            var9.append(new TranslatableText("stream.userinfo.chatTooltip"));

            for(Text var11 : C_16fhxekln.m_67zrekjkv(var6.modes, var6.subscriptions, null)) {
               var9.append("\n");
               var9.append(var11);
            }

            var7.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var9));
            var7.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.TWITCH_USER_INFO, var6.userName));
            this.client.gui.getChat().addMessage(var8);
         }
      }
   }

   @Override
   public void m_55qbwjsme(String string, ChatTokenizedMessage[] chatTokenizedMessages) {
   }

   private void m_57fefqszi(String string, ChatRawMessage chatRawMessage) {
      ChatUserInfo var3 = (ChatUserInfo)this.f_64oyhkirw.get(string);
      if (var3 == null) {
         var3 = new ChatUserInfo();
         var3.displayName = string;
         this.f_64oyhkirw.put(string, var3);
      }

      var3.subscriptions = chatRawMessage.subscriptions;
      var3.modes = chatRawMessage.modes;
      var3.nameColorARGB = chatRawMessage.nameColorARGB;
   }

   private boolean m_78fenssfk(Set set, Set set2, int i) {
      if (set.contains(ChatUserMode.TTV_CHAT_USERMODE_BANNED)) {
         return false;
      } else if (set.contains(ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR)) {
         return true;
      } else if (set.contains(ChatUserMode.TTV_CHAT_USERMODE_MODERATOR)) {
         return true;
      } else if (set.contains(ChatUserMode.TTV_CHAT_USERMODE_STAFF)) {
         return true;
      } else if (i == 0) {
         return true;
      } else {
         return i == 1 ? set2.contains(ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) : false;
      }
   }

   @Override
   public void m_69xctqpwe(String string, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3) {
      for(ChatUserInfo var8 : chatUserInfos2) {
         this.f_64oyhkirw.remove(var8.displayName);
      }

      for(ChatUserInfo var15 : chatUserInfos3) {
         this.f_64oyhkirw.put(var15.displayName, var15);
      }

      for(ChatUserInfo var16 : chatUserInfos) {
         this.f_64oyhkirw.put(var16.displayName, var16);
      }
   }

   @Override
   public void m_31febjeqh(String string) {
      f_46iadcgpz.debug(f_44ugpmdwg, "Chat connected");
   }

   @Override
   public void m_85flcyxgx(String string) {
      f_46iadcgpz.debug(f_44ugpmdwg, "Chat disconnected");
      this.f_64oyhkirw.clear();
   }

   @Override
   public void m_07cazmsoa(String string, String string2) {
   }

   @Override
   public void m_12zkeyvhd() {
   }

   @Override
   public void m_90fsxvyzy() {
   }

   @Override
   public void m_23iyixmle(String string) {
   }

   @Override
   public void m_14zuefaqv(String string) {
   }

   @Override
   public boolean m_31gtetblr() {
      return this.f_91sitixin != null && this.f_91sitixin.equals(this.f_41kmeqlyf.m_25mpxeyvu().name);
   }

   @Override
   public String m_93llpijut() {
      return this.f_91sitixin;
   }

   @Override
   public ChatUserInfo m_11dhevjwi(String string) {
      return (ChatUserInfo)this.f_64oyhkirw.get(string);
   }

   @Override
   public void m_37rrqnjgy(String string) {
      this.f_76hmlkikd.m_65ylxsqcj(this.f_91sitixin, string);
   }

   @Override
   public boolean m_78qjaxsih() {
      return f_02dscwenx && this.f_41kmeqlyf.m_90efyedhr();
   }

   @Override
   public ErrorCode m_69ungrimn() {
      return !f_02dscwenx ? ErrorCode.TTV_EC_OS_TOO_OLD : this.f_41kmeqlyf.m_66thbsdcg();
   }

   @Override
   public boolean m_84uftderi() {
      return this.f_28onvmrhu;
   }

   @Override
   public void m_39rjuiusz(boolean bl) {
      this.f_83ogtloxv = bl;
      this.m_19ohgzkkn();
   }

   @Override
   public boolean m_81cvlsrmx() {
      boolean var1 = this.client.options.streamMicToggleBehavior == 1;
      return this.f_05mrrqpjk || this.client.options.streamMicVolume <= 0.0F || var1 != this.f_83ogtloxv;
   }

   @Override
   public TwitchStream.C_76cprrnwi m_56qzkeokj() {
      return this.f_44ugtyatv;
   }

   static {
      try {
         if (Utils.getOS() == Utils.OS.WINDOWS) {
            System.loadLibrary("avutil-ttv-51");
            System.loadLibrary("swresample-ttv-0");
            System.loadLibrary("libmp3lame-ttv");
            if (System.getProperty("os.arch").contains("64")) {
               System.loadLibrary("libmfxsw64");
            } else {
               System.loadLibrary("libmfxsw32");
            }
         }

         f_02dscwenx = true;
      } catch (Throwable var1) {
         f_02dscwenx = false;
      }
   }
}
