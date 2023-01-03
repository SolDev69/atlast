package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.RTMPState;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

@Environment(EnvType.CLIENT)
public class C_02ymikian {
   protected C_02ymikian.C_91iqijbyp f_68ucdxkik = null;
   protected Stream f_42yifarja = null;
   protected IngestList f_25dfzvdev = null;
   protected C_02ymikian.C_57nyknvyx f_04bkinvex = C_02ymikian.C_57nyknvyx.UNINITALIZED;
   protected long f_86blioxmg = 8000L;
   protected long f_21rsompha = 2000L;
   protected long f_04ngnvcvm = 0L;
   protected RTMPState f_86kxgjvkz = RTMPState.Invalid;
   protected VideoParams f_37wynbbca = null;
   protected AudioParams f_49kmwhvfw = null;
   protected long f_18srhuzpg = 0L;
   protected List f_40dotwdou = null;
   protected boolean f_31qlmtyvc = false;
   protected IStreamCallbacks f_72xnvuwfl = null;
   protected IStatCallbacks f_06dsmnnnn = null;
   protected IngestServer f_13zejwgom = null;
   protected boolean f_54oytpxij = false;
   protected boolean f_91zllddpy = false;
   protected int f_70hldhmbo = -1;
   protected int f_69eriloge = 0;
   protected long f_86fwvnsys = 0L;
   protected float f_65cystlax = 0.0F;
   protected float f_66ywogcxf = 0.0F;
   protected boolean f_32gmcemej = false;
   protected boolean f_56bjkacnq = false;
   protected boolean f_16dolpsuz = false;
   protected IStreamCallbacks f_01lvoohuk = new IStreamCallbacks() {
      public void requestAuthTokenCallback(ErrorCode errorCode, AuthToken authToken) {
      }

      public void loginCallback(ErrorCode errorCode, ChannelInfo channelInfo) {
      }

      public void getIngestServersCallback(ErrorCode errorCode, IngestList ingestList) {
      }

      public void getUserInfoCallback(ErrorCode errorCode, UserInfo userInfo) {
      }

      public void getStreamInfoCallback(ErrorCode errorCode, StreamInfo streamInfo) {
      }

      public void getArchivingStateCallback(ErrorCode errorCode, ArchivingState archivingState) {
      }

      public void runCommercialCallback(ErrorCode errorCode) {
      }

      public void setStreamInfoCallback(ErrorCode errorCode) {
      }

      public void getGameNameListCallback(ErrorCode errorCode, GameInfoList gameInfoList) {
      }

      public void bufferUnlockCallback(long l) {
      }

      public void startCallback(ErrorCode errorCode) {
         C_02ymikian.this.f_56bjkacnq = false;
         if (ErrorCode.succeeded(errorCode)) {
            C_02ymikian.this.f_32gmcemej = true;
            C_02ymikian.this.f_18srhuzpg = System.currentTimeMillis();
            C_02ymikian.this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.CONNECTING_TO_SERVER);
         } else {
            C_02ymikian.this.f_31qlmtyvc = false;
            C_02ymikian.this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.DONE_TESTING_SERVER);
         }
      }

      public void stopCallback(ErrorCode errorCode) {
         if (ErrorCode.failed(errorCode)) {
            System.out.println("IngestTester.stopCallback failed to stop - " + C_02ymikian.this.f_13zejwgom.serverName + ": " + errorCode.toString());
         }

         C_02ymikian.this.f_16dolpsuz = false;
         C_02ymikian.this.f_32gmcemej = false;
         C_02ymikian.this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.DONE_TESTING_SERVER);
         C_02ymikian.this.f_13zejwgom = null;
         if (C_02ymikian.this.f_54oytpxij) {
            C_02ymikian.this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.CANCELLING);
         }
      }

      public void sendActionMetaDataCallback(ErrorCode errorCode) {
      }

      public void sendStartSpanMetaDataCallback(ErrorCode errorCode) {
      }

      public void sendEndSpanMetaDataCallback(ErrorCode errorCode) {
      }
   };
   protected IStatCallbacks f_19lmumztu = new IStatCallbacks() {
      public void statCallback(StatType statType, long l) {
         switch(statType) {
            case TTV_ST_RTMPSTATE:
               C_02ymikian.this.f_86kxgjvkz = RTMPState.lookupValue((int)l);
               break;
            case TTV_ST_RTMPDATASENT:
               C_02ymikian.this.f_04ngnvcvm = l;
         }
      }
   };

   public void m_01aumvjgp(C_02ymikian.C_91iqijbyp c_91iqijbyp) {
      this.f_68ucdxkik = c_91iqijbyp;
   }

   public IngestServer m_96wppvscx() {
      return this.f_13zejwgom;
   }

   public int m_92efldgvl() {
      return this.f_70hldhmbo;
   }

   public boolean m_99uncfekt() {
      return this.f_04bkinvex == C_02ymikian.C_57nyknvyx.FINISHED
         || this.f_04bkinvex == C_02ymikian.C_57nyknvyx.CANCELLED
         || this.f_04bkinvex == C_02ymikian.C_57nyknvyx.FAILED;
   }

   public float m_75yqgekyx() {
      return this.f_66ywogcxf;
   }

   public C_02ymikian(Stream stream, IngestList ingestList) {
      this.f_42yifarja = stream;
      this.f_25dfzvdev = ingestList;
   }

   public void m_37wjdtcsk() {
      if (this.f_04bkinvex == C_02ymikian.C_57nyknvyx.UNINITALIZED) {
         this.f_70hldhmbo = 0;
         this.f_54oytpxij = false;
         this.f_91zllddpy = false;
         this.f_32gmcemej = false;
         this.f_56bjkacnq = false;
         this.f_16dolpsuz = false;
         this.f_06dsmnnnn = this.f_42yifarja.getStatCallbacks();
         this.f_42yifarja.setStatCallbacks(this.f_19lmumztu);
         this.f_72xnvuwfl = this.f_42yifarja.getStreamCallbacks();
         this.f_42yifarja.setStreamCallbacks(this.f_01lvoohuk);
         this.f_37wynbbca = new VideoParams();
         this.f_37wynbbca.targetFps = 60;
         this.f_37wynbbca.maxKbps = 3500;
         this.f_37wynbbca.outputWidth = 1280;
         this.f_37wynbbca.outputHeight = 720;
         this.f_37wynbbca.pixelFormat = PixelFormat.TTV_PF_BGRA;
         this.f_37wynbbca.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
         this.f_37wynbbca.disableAdaptiveBitrate = true;
         this.f_37wynbbca.verticalFlip = false;
         this.f_42yifarja.getDefaultParams(this.f_37wynbbca);
         this.f_49kmwhvfw = new AudioParams();
         this.f_49kmwhvfw.audioEnabled = false;
         this.f_49kmwhvfw.enableMicCapture = false;
         this.f_49kmwhvfw.enablePlaybackCapture = false;
         this.f_49kmwhvfw.enablePassthroughAudio = false;
         this.f_40dotwdou = Lists.newArrayList();
         byte var1 = 3;

         for(int var2 = 0; var2 < var1; ++var2) {
            FrameBuffer var3 = this.f_42yifarja.allocateFrameBuffer(this.f_37wynbbca.outputWidth * this.f_37wynbbca.outputHeight * 4);
            if (!var3.getIsValid()) {
               this.m_26sxaslvl();
               this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.FAILED);
               return;
            }

            this.f_40dotwdou.add(var3);
            this.f_42yifarja.randomizeFrameBuffer(var3);
         }

         this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.STARTING);
         this.f_18srhuzpg = System.currentTimeMillis();
      }
   }

   public void m_89urqscot() {
      if (!this.m_99uncfekt() && this.f_04bkinvex != C_02ymikian.C_57nyknvyx.UNINITALIZED) {
         if (!this.f_56bjkacnq && !this.f_16dolpsuz) {
            switch(this.f_04bkinvex) {
               case STARTING:
               case DONE_TESTING_SERVER:
                  if (this.f_13zejwgom != null) {
                     if (this.f_91zllddpy || !this.f_31qlmtyvc) {
                        this.f_13zejwgom.bitrateKbps = 0.0F;
                     }

                     this.m_59rqhxsjd(this.f_13zejwgom);
                  } else {
                     this.f_18srhuzpg = 0L;
                     this.f_91zllddpy = false;
                     this.f_31qlmtyvc = true;
                     if (this.f_04bkinvex != C_02ymikian.C_57nyknvyx.STARTING) {
                        ++this.f_70hldhmbo;
                     }

                     if (this.f_70hldhmbo < this.f_25dfzvdev.getServers().length) {
                        this.f_13zejwgom = this.f_25dfzvdev.getServers()[this.f_70hldhmbo];
                        this.m_46mhxrxgw(this.f_13zejwgom);
                     } else {
                        this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.FINISHED);
                     }
                  }
                  break;
               case CONNECTING_TO_SERVER:
               case TESTING_SERVER:
                  this.m_32bnmrgxk(this.f_13zejwgom);
                  break;
               case CANCELLING:
                  this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.CANCELLED);
            }

            this.m_61abyusau();
            if (this.f_04bkinvex == C_02ymikian.C_57nyknvyx.CANCELLED || this.f_04bkinvex == C_02ymikian.C_57nyknvyx.FINISHED) {
               this.m_26sxaslvl();
            }
         }
      }
   }

   public void m_27plsqiyq() {
      if (!this.m_99uncfekt() && !this.f_54oytpxij) {
         this.f_54oytpxij = true;
         if (this.f_13zejwgom != null) {
            this.f_13zejwgom.bitrateKbps = 0.0F;
         }
      }
   }

   protected boolean m_46mhxrxgw(IngestServer ingestServer) {
      this.f_31qlmtyvc = true;
      this.f_04ngnvcvm = 0L;
      this.f_86kxgjvkz = RTMPState.Idle;
      this.f_13zejwgom = ingestServer;
      this.f_56bjkacnq = true;
      this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.CONNECTING_TO_SERVER);
      ErrorCode var2 = this.f_42yifarja.start(this.f_37wynbbca, this.f_49kmwhvfw, ingestServer, StartFlags.TTV_Start_BandwidthTest, true);
      if (ErrorCode.failed(var2)) {
         this.f_56bjkacnq = false;
         this.f_31qlmtyvc = false;
         this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.DONE_TESTING_SERVER);
         return false;
      } else {
         this.f_86fwvnsys = this.f_04ngnvcvm;
         ingestServer.bitrateKbps = 0.0F;
         this.f_69eriloge = 0;
         return true;
      }
   }

   protected void m_59rqhxsjd(IngestServer ingestServer) {
      if (this.f_56bjkacnq) {
         this.f_91zllddpy = true;
      } else if (this.f_32gmcemej) {
         this.f_16dolpsuz = true;
         ErrorCode var2 = this.f_42yifarja.stop(true);
         if (ErrorCode.failed(var2)) {
            this.f_01lvoohuk.stopCallback(ErrorCode.TTV_EC_SUCCESS);
            System.out.println("Stop failed: " + var2.toString());
         }

         this.f_42yifarja.pollStats();
      } else {
         this.f_01lvoohuk.stopCallback(ErrorCode.TTV_EC_SUCCESS);
      }
   }

   protected long m_61slysrav() {
      return System.currentTimeMillis() - this.f_18srhuzpg;
   }

   protected void m_61abyusau() {
      float var1 = (float)this.m_61slysrav();
      switch(this.f_04bkinvex) {
         case STARTING:
         case CONNECTING_TO_SERVER:
         case UNINITALIZED:
         case FINISHED:
         case CANCELLED:
         case FAILED:
            this.f_66ywogcxf = 0.0F;
            break;
         case DONE_TESTING_SERVER:
            this.f_66ywogcxf = 1.0F;
            break;
         case TESTING_SERVER:
         case CANCELLING:
         default:
            this.f_66ywogcxf = var1 / (float)this.f_86blioxmg;
      }

      switch(this.f_04bkinvex) {
         case FINISHED:
         case CANCELLED:
         case FAILED:
            this.f_65cystlax = 1.0F;
            break;
         default:
            this.f_65cystlax = (float)this.f_70hldhmbo / (float)this.f_25dfzvdev.getServers().length;
            this.f_65cystlax += this.f_66ywogcxf / (float)this.f_25dfzvdev.getServers().length;
      }
   }

   protected boolean m_32bnmrgxk(IngestServer ingestServer) {
      if (this.f_91zllddpy || this.f_54oytpxij || this.m_61slysrav() >= this.f_86blioxmg) {
         this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.DONE_TESTING_SERVER);
         return true;
      } else if (!this.f_56bjkacnq && !this.f_16dolpsuz) {
         ErrorCode var2 = this.f_42yifarja.submitVideoFrame((FrameBuffer)this.f_40dotwdou.get(this.f_69eriloge));
         if (ErrorCode.failed(var2)) {
            this.f_31qlmtyvc = false;
            this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.DONE_TESTING_SERVER);
            return false;
         } else {
            this.f_69eriloge = (this.f_69eriloge + 1) % this.f_40dotwdou.size();
            this.f_42yifarja.pollStats();
            if (this.f_86kxgjvkz == RTMPState.SendVideo) {
               this.m_52xslbfqv(C_02ymikian.C_57nyknvyx.TESTING_SERVER);
               long var3 = this.m_61slysrav();
               if (var3 > 0L && this.f_04ngnvcvm > this.f_86fwvnsys) {
                  ingestServer.bitrateKbps = (float)(this.f_04ngnvcvm * 8L) / (float)this.m_61slysrav();
                  this.f_86fwvnsys = this.f_04ngnvcvm;
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   protected void m_26sxaslvl() {
      this.f_13zejwgom = null;
      if (this.f_40dotwdou != null) {
         for(int var1 = 0; var1 < this.f_40dotwdou.size(); ++var1) {
            ((FrameBuffer)this.f_40dotwdou.get(var1)).free();
         }

         this.f_40dotwdou = null;
      }

      if (this.f_42yifarja.getStatCallbacks() == this.f_19lmumztu) {
         this.f_42yifarja.setStatCallbacks(this.f_06dsmnnnn);
         this.f_06dsmnnnn = null;
      }

      if (this.f_42yifarja.getStreamCallbacks() == this.f_01lvoohuk) {
         this.f_42yifarja.setStreamCallbacks(this.f_72xnvuwfl);
         this.f_72xnvuwfl = null;
      }
   }

   protected void m_52xslbfqv(C_02ymikian.C_57nyknvyx c_57nyknvyx) {
      if (c_57nyknvyx != this.f_04bkinvex) {
         this.f_04bkinvex = c_57nyknvyx;
         if (this.f_68ucdxkik != null) {
            this.f_68ucdxkik.m_99ftllcql(this, c_57nyknvyx);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum C_57nyknvyx {
      UNINITALIZED,
      STARTING,
      CONNECTING_TO_SERVER,
      TESTING_SERVER,
      DONE_TESTING_SERVER,
      FINISHED,
      CANCELLING,
      CANCELLED,
      FAILED;
   }

   @Environment(EnvType.CLIENT)
   public interface C_91iqijbyp {
      void m_99ftllcql(C_02ymikian c_80yeuxqdq, C_02ymikian.C_57nyknvyx c_57nyknvyx);
   }
}
