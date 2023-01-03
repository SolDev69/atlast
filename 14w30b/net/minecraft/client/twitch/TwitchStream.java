package net.minecraft.client.twitch;

import net.minecraft.C_02ymikian;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

@Environment(EnvType.CLIENT)
public interface TwitchStream {
   void m_08tceactq();

   void m_26lsqwmff();

   void m_81hdmzdjh();

   boolean m_01cdoylst();

   boolean m_10yutarck();

   boolean m_99rcqogzt();

   void m_27hgmbctc(StreamMetadata c_29pxubhlq, long l);

   void m_75vuvosit(StreamMetadata c_29pxubhlq, long l, long m);

   boolean m_59ybwvnxm();

   void m_67lgjitba();

   void m_62rfzgdrt();

   void m_59fglhcmk();

   void m_19ohgzkkn();

   void m_48nqrofgp();

   void stopStream();

   IngestServer[] m_96xxepobn();

   void m_81qbezqee();

   C_02ymikian m_39mbbqlvd();

   boolean m_70uizoygc();

   int m_23mxopcac();

   boolean m_31gtetblr();

   String m_93llpijut();

   ChatUserInfo m_11dhevjwi(String string);

   void m_37rrqnjgy(String string);

   boolean m_78qjaxsih();

   ErrorCode m_69ungrimn();

   boolean m_84uftderi();

   void m_39rjuiusz(boolean bl);

   boolean m_81cvlsrmx();

   TwitchStream.C_76cprrnwi m_56qzkeokj();

   @Environment(EnvType.CLIENT)
   public static enum C_76cprrnwi {
      ERROR,
      INVALID_TOKEN;
   }
}
