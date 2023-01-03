package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;
import tv.twitch.StandardCoreAPI;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.DesktopStreamAPI;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

@Environment(EnvType.CLIENT)
public class C_48uodvatu {
   private static final Logger f_24prkhgqu = LogManager.getLogger();
   protected final int f_63pjzimjv = 30;
   protected final int f_09ukizxmd = 3;
   private static final C_32uniyyqe f_03cbgmfos = new C_32uniyyqe(String.class, 50);
   private String f_11dsnsjbv = null;
   protected C_48uodvatu.C_46ljtchyh f_35pgnjiux = null;
   protected String f_42cktgeyb = "";
   protected String f_59syqnaoa = "";
   protected String f_57vkrbwqa = "";
   protected boolean f_17qcpbode = true;
   protected Core f_61rbmrtjd = null;
   protected Stream f_86pftnqnk = null;
   protected List f_79lbukpyk = Lists.newArrayList();
   protected List f_90axvaeap = Lists.newArrayList();
   protected boolean f_40vuryoyu = false;
   protected boolean f_59yztfpeu = false;
   protected boolean f_77lhhsfut = false;
   protected C_48uodvatu.C_49akbipsc f_71nospxzi = C_48uodvatu.C_49akbipsc.UNINITIALIZED;
   protected String f_03vkttchj = null;
   protected VideoParams f_25ugqyxst = null;
   protected AudioParams f_64rpallid = null;
   protected IngestList f_55abkvjwb = new IngestList(new IngestServer[0]);
   protected IngestServer f_25sdxaebj = null;
   protected AuthToken f_31fajdpmo = new AuthToken();
   protected ChannelInfo f_47vfndrge = new ChannelInfo();
   protected UserInfo f_69whebefm = new UserInfo();
   protected StreamInfo f_11wnznblq = new StreamInfo();
   protected ArchivingState f_19boazzat = new ArchivingState();
   protected long f_22hsihyqe = 0L;
   protected C_02ymikian f_36nuyfjon = null;
   private ErrorCode f_76aqqurbc;
   protected IStreamCallbacks f_37fswazxd = new IStreamCallbacks() {
      public void requestAuthTokenCallback(ErrorCode errorCode, AuthToken authToken) {
         if (ErrorCode.succeeded(errorCode)) {
            C_48uodvatu.this.f_31fajdpmo = authToken;
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.AUTHENTICATED);
         } else {
            C_48uodvatu.this.f_31fajdpmo.data = "";
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INITIALIZED);
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("RequestAuthTokenDoneCallback got failure: %s", var3));
         }

         try {
            if (C_48uodvatu.this.f_35pgnjiux != null) {
               C_48uodvatu.this.f_35pgnjiux.m_45lfkayus(errorCode, authToken);
            }
         } catch (Exception var4) {
            C_48uodvatu.this.m_36ghihwqj(var4.toString());
         }
      }

      public void loginCallback(ErrorCode errorCode, ChannelInfo channelInfo) {
         if (ErrorCode.succeeded(errorCode)) {
            C_48uodvatu.this.f_47vfndrge = channelInfo;
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.LOGGED_IN);
            C_48uodvatu.this.f_59yztfpeu = true;
         } else {
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INITIALIZED);
            C_48uodvatu.this.f_59yztfpeu = false;
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("LoginCallback got failure: %s", var3));
         }

         try {
            if (C_48uodvatu.this.f_35pgnjiux != null) {
               C_48uodvatu.this.f_35pgnjiux.m_36rztryrt(errorCode);
            }
         } catch (Exception var4) {
            C_48uodvatu.this.m_36ghihwqj(var4.toString());
         }
      }

      public void getIngestServersCallback(ErrorCode errorCode, IngestList ingestList) {
         if (ErrorCode.succeeded(errorCode)) {
            C_48uodvatu.this.f_55abkvjwb = ingestList;
            C_48uodvatu.this.f_25sdxaebj = C_48uodvatu.this.f_55abkvjwb.getDefaultServer();
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.RECEIVED_INGEST_SERVERS);

            try {
               if (C_48uodvatu.this.f_35pgnjiux != null) {
                  C_48uodvatu.this.f_35pgnjiux.m_36govrjva(ingestList);
               }
            } catch (Exception var4) {
               C_48uodvatu.this.m_36ghihwqj(var4.toString());
            }
         } else {
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("IngestListCallback got failure: %s", var3));
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.LOGGING_IN);
         }
      }

      public void getUserInfoCallback(ErrorCode errorCode, UserInfo userInfo) {
         C_48uodvatu.this.f_69whebefm = userInfo;
         if (ErrorCode.failed(errorCode)) {
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("UserInfoDoneCallback got failure: %s", var3));
         }
      }

      public void getStreamInfoCallback(ErrorCode errorCode, StreamInfo streamInfo) {
         if (ErrorCode.succeeded(errorCode)) {
            C_48uodvatu.this.f_11wnznblq = streamInfo;

            try {
               if (C_48uodvatu.this.f_35pgnjiux != null) {
                  C_48uodvatu.this.f_35pgnjiux.m_50zksmnbp(streamInfo);
               }
            } catch (Exception var4) {
               C_48uodvatu.this.m_36ghihwqj(var4.toString());
            }
         } else {
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_20ulkctit(String.format("StreamInfoDoneCallback got failure: %s", var3));
         }
      }

      public void getArchivingStateCallback(ErrorCode errorCode, ArchivingState archivingState) {
         C_48uodvatu.this.f_19boazzat = archivingState;
         if (ErrorCode.failed(errorCode)) {
         }
      }

      public void runCommercialCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_20ulkctit(String.format("RunCommercialCallback got failure: %s", var2));
         }
      }

      public void setStreamInfoCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_20ulkctit(String.format("SetStreamInfoCallback got failure: %s", var2));
         }
      }

      public void getGameNameListCallback(ErrorCode errorCode, GameInfoList gameInfoList) {
         if (ErrorCode.failed(errorCode)) {
            String var3 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("GameNameListCallback got failure: %s", var3));
         }

         try {
            if (C_48uodvatu.this.f_35pgnjiux != null) {
               C_48uodvatu.this.f_35pgnjiux.m_69hcyofdx(errorCode, gameInfoList == null ? new GameInfo[0] : gameInfoList.list);
            }
         } catch (Exception var4) {
            C_48uodvatu.this.m_36ghihwqj(var4.toString());
         }
      }

      public void bufferUnlockCallback(long l) {
         FrameBuffer var3 = FrameBuffer.lookupBuffer(l);
         C_48uodvatu.this.f_90axvaeap.add(var3);
      }

      public void startCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            try {
               if (C_48uodvatu.this.f_35pgnjiux != null) {
                  C_48uodvatu.this.f_35pgnjiux.m_64kitlxsr();
               }
            } catch (Exception var4) {
               C_48uodvatu.this.m_36ghihwqj(var4.toString());
            }

            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.BROADCASTING);
         } else {
            C_48uodvatu.this.f_25ugqyxst = null;
            C_48uodvatu.this.f_64rpallid = null;
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST);

            try {
               if (C_48uodvatu.this.f_35pgnjiux != null) {
                  C_48uodvatu.this.f_35pgnjiux.m_11xlfllrs(errorCode);
               }
            } catch (Exception var3) {
               C_48uodvatu.this.m_36ghihwqj(var3.toString());
            }

            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("startCallback got failure: %s", var2));
         }
      }

      public void stopCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            C_48uodvatu.this.f_25ugqyxst = null;
            C_48uodvatu.this.f_64rpallid = null;
            C_48uodvatu.this.m_54zulgqhq();

            try {
               if (C_48uodvatu.this.f_35pgnjiux != null) {
                  C_48uodvatu.this.f_35pgnjiux.m_22yefjwrm();
               }
            } catch (Exception var3) {
               C_48uodvatu.this.m_36ghihwqj(var3.toString());
            }

            if (C_48uodvatu.this.f_59yztfpeu) {
               C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST);
            } else {
               C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INITIALIZED);
            }
         } else {
            C_48uodvatu.this.m_09stwujcb(C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST);
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("stopCallback got failure: %s", var2));
         }
      }

      public void sendActionMetaDataCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("sendActionMetaDataCallback got failure: %s", var2));
         }
      }

      public void sendStartSpanMetaDataCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("sendStartSpanMetaDataCallback got failure: %s", var2));
         }
      }

      public void sendEndSpanMetaDataCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            String var2 = ErrorCode.getString(errorCode);
            C_48uodvatu.this.m_36ghihwqj(String.format("sendEndSpanMetaDataCallback got failure: %s", var2));
         }
      }
   };
   protected IStatCallbacks f_54sesqcpr = new IStatCallbacks() {
      public void statCallback(StatType statType, long l) {
      }
   };

   public void m_97gacbzce(C_48uodvatu.C_46ljtchyh c_46ljtchyh) {
      this.f_35pgnjiux = c_46ljtchyh;
   }

   public boolean m_90efyedhr() {
      return this.f_40vuryoyu;
   }

   public void m_45lxtuzmf(String string) {
      this.f_42cktgeyb = string;
   }

   public StreamInfo m_33fzouxhp() {
      return this.f_11wnznblq;
   }

   public ChannelInfo m_25mpxeyvu() {
      return this.f_47vfndrge;
   }

   public boolean m_98lkbozru() {
      return this.f_71nospxzi == C_48uodvatu.C_49akbipsc.BROADCASTING || this.f_71nospxzi == C_48uodvatu.C_49akbipsc.PAUSED;
   }

   public boolean m_92irlaqgq() {
      return this.f_71nospxzi == C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST;
   }

   public boolean m_05rgggjzj() {
      return this.f_71nospxzi == C_48uodvatu.C_49akbipsc.INGEST_TESTING;
   }

   public boolean m_99qghnojx() {
      return this.f_71nospxzi == C_48uodvatu.C_49akbipsc.PAUSED;
   }

   public boolean m_39vgpzmvw() {
      return this.f_59yztfpeu;
   }

   public IngestServer m_88esecany() {
      return this.f_25sdxaebj;
   }

   public void m_65jmctixg(IngestServer ingestServer) {
      this.f_25sdxaebj = ingestServer;
   }

   public IngestList m_18obowuxd() {
      return this.f_55abkvjwb;
   }

   public void m_46nqcttzh(float f) {
      this.f_86pftnqnk.setVolume(AudioDeviceType.TTV_RECORDER_DEVICE, f);
   }

   public void m_26puheqiy(float f) {
      this.f_86pftnqnk.setVolume(AudioDeviceType.TTV_PLAYBACK_DEVICE, f);
   }

   public C_02ymikian m_07hdsswup() {
      return this.f_36nuyfjon;
   }

   public long m_75jnytxwc() {
      return this.f_86pftnqnk.getStreamTime();
   }

   protected boolean m_61hnaqjmd() {
      return true;
   }

   public ErrorCode m_66thbsdcg() {
      return this.f_76aqqurbc;
   }

   public C_48uodvatu() {
      this.f_61rbmrtjd = Core.getInstance();
      if (Core.getInstance() == null) {
         this.f_61rbmrtjd = new Core(new StandardCoreAPI());
      }

      this.f_86pftnqnk = new Stream(new DesktopStreamAPI());
   }

   protected PixelFormat m_84yddkjja() {
      return PixelFormat.TTV_PF_RGBA;
   }

   public boolean m_65xvjefbd() {
      if (this.f_40vuryoyu) {
         return false;
      } else {
         this.f_86pftnqnk.setStreamCallbacks(this.f_37fswazxd);
         ErrorCode var1 = this.f_61rbmrtjd.initialize(this.f_42cktgeyb, System.getProperty("java.library.path"));
         if (!this.m_42qvtvthq(var1)) {
            this.f_86pftnqnk.setStreamCallbacks(null);
            this.f_76aqqurbc = var1;
            return false;
         } else {
            var1 = this.f_61rbmrtjd.setTraceLevel(MessageLevel.TTV_ML_ERROR);
            if (!this.m_42qvtvthq(var1)) {
               this.f_86pftnqnk.setStreamCallbacks(null);
               this.f_61rbmrtjd.shutdown();
               this.f_76aqqurbc = var1;
               return false;
            } else if (ErrorCode.succeeded(var1)) {
               this.f_40vuryoyu = true;
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INITIALIZED);
               return true;
            } else {
               this.f_76aqqurbc = var1;
               this.f_61rbmrtjd.shutdown();
               return false;
            }
         }
      }
   }

   public boolean m_84ocwbwik() {
      if (!this.f_40vuryoyu) {
         return true;
      } else if (this.m_05rgggjzj()) {
         return false;
      } else {
         this.f_77lhhsfut = true;
         this.m_28earwucl();
         this.f_86pftnqnk.setStreamCallbacks(null);
         this.f_86pftnqnk.setStatCallbacks(null);
         ErrorCode var1 = this.f_61rbmrtjd.shutdown();
         this.m_42qvtvthq(var1);
         this.f_40vuryoyu = false;
         this.f_77lhhsfut = false;
         this.m_09stwujcb(C_48uodvatu.C_49akbipsc.UNINITIALIZED);
         return true;
      }
   }

   public void m_11kkkddtg() {
      if (this.f_71nospxzi != C_48uodvatu.C_49akbipsc.UNINITIALIZED) {
         if (this.f_36nuyfjon != null) {
            this.f_36nuyfjon.m_27plsqiyq();
         }

         for(; this.f_36nuyfjon != null; this.m_65tmpsvnh()) {
            try {
               Thread.sleep(200L);
            } catch (Exception var2) {
               this.m_36ghihwqj(var2.toString());
            }
         }

         this.m_84ocwbwik();
      }
   }

   public boolean m_86dgajljz(String string, AuthToken authToken) {
      if (this.m_05rgggjzj()) {
         return false;
      } else {
         this.m_28earwucl();
         if (string == null || string.isEmpty()) {
            this.m_36ghihwqj("Username must be valid");
            return false;
         } else if (authToken != null && authToken.data != null && !authToken.data.isEmpty()) {
            this.f_03vkttchj = string;
            this.f_31fajdpmo = authToken;
            if (this.m_90efyedhr()) {
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.AUTHENTICATED);
            }

            return true;
         } else {
            this.m_36ghihwqj("Auth token must be valid");
            return false;
         }
      }
   }

   public boolean m_28earwucl() {
      if (this.m_05rgggjzj()) {
         return false;
      } else {
         if (this.m_98lkbozru()) {
            this.f_86pftnqnk.stop(false);
         }

         this.f_03vkttchj = "";
         this.f_31fajdpmo = new AuthToken();
         if (!this.f_59yztfpeu) {
            return false;
         } else {
            this.f_59yztfpeu = false;
            if (!this.f_77lhhsfut) {
               try {
                  if (this.f_35pgnjiux != null) {
                     this.f_35pgnjiux.m_42btkswwl();
                  }
               } catch (Exception var2) {
                  this.m_36ghihwqj(var2.toString());
               }
            }

            this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INITIALIZED);
            return true;
         }
      }
   }

   public boolean m_84wcgbjlk(String string, String string2, String string3) {
      if (!this.f_59yztfpeu) {
         return false;
      } else {
         if (string == null || string.equals("")) {
            string = this.f_03vkttchj;
         }

         if (string2 == null) {
            string2 = "";
         }

         if (string3 == null) {
            string3 = "";
         }

         StreamInfoForSetting var4 = new StreamInfoForSetting();
         var4.streamTitle = string3;
         var4.gameName = string2;
         ErrorCode var5 = this.f_86pftnqnk.setStreamInfo(this.f_31fajdpmo, string, var4);
         this.m_42qvtvthq(var5);
         return ErrorCode.succeeded(var5);
      }
   }

   public boolean m_15aerwprk() {
      if (!this.m_98lkbozru()) {
         return false;
      } else {
         ErrorCode var1 = this.f_86pftnqnk.runCommercial(this.f_31fajdpmo);
         this.m_42qvtvthq(var1);
         return ErrorCode.succeeded(var1);
      }
   }

   public VideoParams m_20ckpkrpu(int i, int j, float f, float g) {
      int[] var5 = this.f_86pftnqnk.getMaxResolution(i, j, f, g);
      VideoParams var6 = new VideoParams();
      var6.maxKbps = i;
      var6.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
      var6.pixelFormat = this.m_84yddkjja();
      var6.targetFps = j;
      var6.outputWidth = var5[0];
      var6.outputHeight = var5[1];
      var6.disableAdaptiveBitrate = false;
      var6.verticalFlip = false;
      return var6;
   }

   public boolean m_83cpsfukk(VideoParams videoParams) {
      if (videoParams != null && this.m_92irlaqgq()) {
         this.f_25ugqyxst = videoParams.clone();
         this.f_64rpallid = new AudioParams();
         this.f_64rpallid.audioEnabled = this.f_17qcpbode && this.m_61hnaqjmd();
         this.f_64rpallid.enableMicCapture = this.f_64rpallid.audioEnabled;
         this.f_64rpallid.enablePlaybackCapture = this.f_64rpallid.audioEnabled;
         this.f_64rpallid.enablePassthroughAudio = false;
         if (!this.m_68iqskvgg()) {
            this.f_25ugqyxst = null;
            this.f_64rpallid = null;
            return false;
         } else {
            ErrorCode var2 = this.f_86pftnqnk.start(videoParams, this.f_64rpallid, this.f_25sdxaebj, StartFlags.None, true);
            if (ErrorCode.failed(var2)) {
               this.m_54zulgqhq();
               String var3 = ErrorCode.getString(var2);
               this.m_36ghihwqj(String.format("Error while starting to broadcast: %s", var3));
               this.f_25ugqyxst = null;
               this.f_64rpallid = null;
               return false;
            } else {
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.STARTING);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean m_72fnodvaw() {
      if (!this.m_98lkbozru()) {
         return false;
      } else {
         ErrorCode var1 = this.f_86pftnqnk.stop(true);
         if (ErrorCode.failed(var1)) {
            String var2 = ErrorCode.getString(var1);
            this.m_36ghihwqj(String.format("Error while stopping the broadcast: %s", var2));
            return false;
         } else {
            this.m_09stwujcb(C_48uodvatu.C_49akbipsc.STOPPING);
            return ErrorCode.succeeded(var1);
         }
      }
   }

   public boolean m_95egnealx() {
      if (!this.m_98lkbozru()) {
         return false;
      } else {
         ErrorCode var1 = this.f_86pftnqnk.pauseVideo();
         if (ErrorCode.failed(var1)) {
            this.m_72fnodvaw();
            String var2 = ErrorCode.getString(var1);
            this.m_36ghihwqj(String.format("Error pausing stream: %s\n", var2));
         } else {
            this.m_09stwujcb(C_48uodvatu.C_49akbipsc.PAUSED);
         }

         return ErrorCode.succeeded(var1);
      }
   }

   public boolean m_18szvwtzn() {
      if (!this.m_99qghnojx()) {
         return false;
      } else {
         this.m_09stwujcb(C_48uodvatu.C_49akbipsc.BROADCASTING);
         return true;
      }
   }

   public boolean m_22catrsfj(String string, long l, String string2, String string3) {
      ErrorCode var6 = this.f_86pftnqnk.sendActionMetaData(this.f_31fajdpmo, string, l, string2, string3);
      if (ErrorCode.failed(var6)) {
         String var7 = ErrorCode.getString(var6);
         this.m_36ghihwqj(String.format("Error while sending meta data: %s\n", var7));
         return false;
      } else {
         return true;
      }
   }

   public long m_47leaclky(String string, long l, String string2, String string3) {
      long var6 = this.f_86pftnqnk.sendStartSpanMetaData(this.f_31fajdpmo, string, l, string2, string3);
      if (var6 == -1L) {
         this.m_36ghihwqj(String.format("Error in SendStartSpanMetaData\n"));
      }

      return var6;
   }

   public boolean m_23rqjmuhg(String string, long l, long m, String string2, String string3) {
      if (m == -1L) {
         this.m_36ghihwqj(String.format("Invalid sequence id: %d\n", m));
         return false;
      } else {
         ErrorCode var8 = this.f_86pftnqnk.sendEndSpanMetaData(this.f_31fajdpmo, string, l, m, string2, string3);
         if (ErrorCode.failed(var8)) {
            String var9 = ErrorCode.getString(var8);
            this.m_36ghihwqj(String.format("Error in SendStopSpanMetaData: %s\n", var9));
            return false;
         } else {
            return true;
         }
      }
   }

   protected void m_09stwujcb(C_48uodvatu.C_49akbipsc c_49akbipsc) {
      if (c_49akbipsc != this.f_71nospxzi) {
         this.f_71nospxzi = c_49akbipsc;

         try {
            if (this.f_35pgnjiux != null) {
               this.f_35pgnjiux.m_75cmyvfkk(c_49akbipsc);
            }
         } catch (Exception var3) {
            this.m_36ghihwqj(var3.toString());
         }
      }
   }

   public void m_65tmpsvnh() {
      if (this.f_86pftnqnk != null && this.f_40vuryoyu) {
         ErrorCode var1 = this.f_86pftnqnk.pollTasks();
         this.m_42qvtvthq(var1);
         if (this.m_05rgggjzj()) {
            this.f_36nuyfjon.m_89urqscot();
            if (this.f_36nuyfjon.m_99uncfekt()) {
               this.f_36nuyfjon = null;
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST);
            }
         }

         switch(this.f_71nospxzi) {
            case AUTHENTICATED:
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.LOGGING_IN);
               var1 = this.f_86pftnqnk.login(this.f_31fajdpmo);
               if (ErrorCode.failed(var1)) {
                  String var9 = ErrorCode.getString(var1);
                  this.m_36ghihwqj(String.format("Error in TTV_Login: %s\n", var9));
               }
               break;
            case LOGGED_IN:
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.FINDING_INGEST_SERVER);
               var1 = this.f_86pftnqnk.getIngestServers(this.f_31fajdpmo);
               if (ErrorCode.failed(var1)) {
                  this.m_09stwujcb(C_48uodvatu.C_49akbipsc.LOGGED_IN);
                  String var8 = ErrorCode.getString(var1);
                  this.m_36ghihwqj(String.format("Error in TTV_GetIngestServers: %s\n", var8));
               }
               break;
            case RECEIVED_INGEST_SERVERS:
               this.m_09stwujcb(C_48uodvatu.C_49akbipsc.READY_TO_BROADCAST);
               var1 = this.f_86pftnqnk.getUserInfo(this.f_31fajdpmo);
               if (ErrorCode.failed(var1)) {
                  String var2 = ErrorCode.getString(var1);
                  this.m_36ghihwqj(String.format("Error in TTV_GetUserInfo: %s\n", var2));
               }

               this.m_34ifiqgjh();
               var1 = this.f_86pftnqnk.getArchivingState(this.f_31fajdpmo);
               if (ErrorCode.failed(var1)) {
                  String var7 = ErrorCode.getString(var1);
                  this.m_36ghihwqj(String.format("Error in TTV_GetArchivingState: %s\n", var7));
               }
            case STARTING:
            case STOPPING:
            case FINDING_INGEST_SERVER:
            case AUTHENTICATING:
            case INITIALIZED:
            case UNINITIALIZED:
            case INGEST_TESTING:
            default:
               break;
            case PAUSED:
            case BROADCASTING:
               this.m_34ifiqgjh();
         }
      }
   }

   protected void m_34ifiqgjh() {
      long var1 = System.nanoTime();
      long var3 = (var1 - this.f_22hsihyqe) / 1000000000L;
      if (var3 >= 30L) {
         this.f_22hsihyqe = var1;
         ErrorCode var5 = this.f_86pftnqnk.getStreamInfo(this.f_31fajdpmo, this.f_03vkttchj);
         if (ErrorCode.failed(var5)) {
            String var6 = ErrorCode.getString(var5);
            this.m_36ghihwqj(String.format("Error in TTV_GetStreamInfo: %s", var6));
         }
      }
   }

   public C_02ymikian m_78mbabeno() {
      if (!this.m_92irlaqgq() || this.f_55abkvjwb == null) {
         return null;
      } else if (this.m_05rgggjzj()) {
         return null;
      } else {
         this.f_36nuyfjon = new C_02ymikian(this.f_86pftnqnk, this.f_55abkvjwb);
         this.f_36nuyfjon.m_37wjdtcsk();
         this.m_09stwujcb(C_48uodvatu.C_49akbipsc.INGEST_TESTING);
         return this.f_36nuyfjon;
      }
   }

   protected boolean m_68iqskvgg() {
      for(int var1 = 0; var1 < 3; ++var1) {
         FrameBuffer var2 = this.f_86pftnqnk.allocateFrameBuffer(this.f_25ugqyxst.outputWidth * this.f_25ugqyxst.outputHeight * 4);
         if (!var2.getIsValid()) {
            this.m_36ghihwqj(String.format("Error while allocating frame buffer"));
            return false;
         }

         this.f_79lbukpyk.add(var2);
         this.f_90axvaeap.add(var2);
      }

      return true;
   }

   protected void m_54zulgqhq() {
      for(int var1 = 0; var1 < this.f_79lbukpyk.size(); ++var1) {
         FrameBuffer var2 = (FrameBuffer)this.f_79lbukpyk.get(var1);
         var2.free();
      }

      this.f_90axvaeap.clear();
      this.f_79lbukpyk.clear();
   }

   public FrameBuffer m_88cciqyzd() {
      if (this.f_90axvaeap.size() == 0) {
         this.m_36ghihwqj(String.format("Out of free buffers, this should never happen"));
         return null;
      } else {
         FrameBuffer var1 = (FrameBuffer)this.f_90axvaeap.get(this.f_90axvaeap.size() - 1);
         this.f_90axvaeap.remove(this.f_90axvaeap.size() - 1);
         return var1;
      }
   }

   public void m_42lihkxbq(FrameBuffer frameBuffer) {
      try {
         this.f_86pftnqnk.captureFrameBuffer_ReadPixels(frameBuffer);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.of(var5, "Trying to submit a frame to Twitch");
         CashReportCategory var4 = var3.addCategory("Broadcast State");
         var4.add("Last reported errors", Arrays.toString(f_03cbgmfos.m_06dcduyhp()));
         var4.add("Buffer", frameBuffer);
         var4.add("Free buffer count", this.f_90axvaeap.size());
         var4.add("Capture buffer count", this.f_79lbukpyk.size());
         throw new CrashException(var3);
      }
   }

   public ErrorCode m_68lbgyafe(FrameBuffer frameBuffer) {
      if (this.m_99qghnojx()) {
         this.m_18szvwtzn();
      } else if (!this.m_98lkbozru()) {
         return ErrorCode.TTV_EC_STREAM_NOT_STARTED;
      }

      ErrorCode var2 = this.f_86pftnqnk.submitVideoFrame(frameBuffer);
      if (var2 != ErrorCode.TTV_EC_SUCCESS) {
         String var3 = ErrorCode.getString(var2);
         if (ErrorCode.succeeded(var2)) {
            this.m_20ulkctit(String.format("Warning in SubmitTexturePointer: %s\n", var3));
         } else {
            this.m_36ghihwqj(String.format("Error in SubmitTexturePointer: %s\n", var3));
            this.m_72fnodvaw();
         }

         if (this.f_35pgnjiux != null) {
            this.f_35pgnjiux.m_33yockmto(var2);
         }
      }

      return var2;
   }

   protected boolean m_42qvtvthq(ErrorCode errorCode) {
      if (ErrorCode.failed(errorCode)) {
         this.m_36ghihwqj(ErrorCode.getString(errorCode));
         return false;
      } else {
         return true;
      }
   }

   protected void m_36ghihwqj(String string) {
      this.f_11dsnsjbv = string;
      f_03cbgmfos.m_38clnhxnq("<Error> " + string);
      f_24prkhgqu.error(C_48kamxasz.f_44ugpmdwg, "[Broadcast controller] {}", new Object[]{string});
   }

   protected void m_20ulkctit(String string) {
      f_03cbgmfos.m_38clnhxnq("<Warning> " + string);
      f_24prkhgqu.warn(C_48kamxasz.f_44ugpmdwg, "[Broadcast controller] {}", new Object[]{string});
   }

   @Environment(EnvType.CLIENT)
   public interface C_46ljtchyh {
      void m_45lfkayus(ErrorCode errorCode, AuthToken authToken);

      void m_36rztryrt(ErrorCode errorCode);

      void m_69hcyofdx(ErrorCode errorCode, GameInfo[] gameInfos);

      void m_75cmyvfkk(C_48uodvatu.C_49akbipsc c_49akbipsc);

      void m_42btkswwl();

      void m_50zksmnbp(StreamInfo streamInfo);

      void m_36govrjva(IngestList ingestList);

      void m_33yockmto(ErrorCode errorCode);

      void m_64kitlxsr();

      void m_22yefjwrm();

      void m_11xlfllrs(ErrorCode errorCode);
   }

   @Environment(EnvType.CLIENT)
   public static enum C_49akbipsc {
      UNINITIALIZED,
      INITIALIZED,
      AUTHENTICATING,
      AUTHENTICATED,
      LOGGING_IN,
      LOGGED_IN,
      FINDING_INGEST_SERVER,
      RECEIVED_INGEST_SERVERS,
      READY_TO_BROADCAST,
      STARTING,
      BROADCASTING,
      STOPPING,
      PAUSED,
      INGEST_TESTING;
   }
}
