package net.minecraft.client.twitch;

import net.minecraft.C_02ymikian;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

@Environment(EnvType.CLIENT)
public class ErrorTwitchStream implements TwitchStream {
   private final Throwable error;

   public ErrorTwitchStream(Throwable error) {
      this.error = error;
   }

   @Override
   public void m_08tceactq() {
   }

   @Override
   public void m_26lsqwmff() {
   }

   @Override
   public void m_81hdmzdjh() {
   }

   @Override
   public boolean m_01cdoylst() {
      return false;
   }

   @Override
   public boolean m_10yutarck() {
      return false;
   }

   @Override
   public boolean m_99rcqogzt() {
      return false;
   }

   @Override
   public void m_27hgmbctc(StreamMetadata c_29pxubhlq, long l) {
   }

   @Override
   public void m_75vuvosit(StreamMetadata c_29pxubhlq, long l, long m) {
   }

   @Override
   public boolean m_59ybwvnxm() {
      return false;
   }

   @Override
   public void m_67lgjitba() {
   }

   @Override
   public void m_62rfzgdrt() {
   }

   @Override
   public void m_59fglhcmk() {
   }

   @Override
   public void m_19ohgzkkn() {
   }

   @Override
   public void m_48nqrofgp() {
   }

   @Override
   public void stopStream() {
   }

   @Override
   public IngestServer[] m_96xxepobn() {
      return new IngestServer[0];
   }

   @Override
   public void m_81qbezqee() {
   }

   @Override
   public C_02ymikian m_39mbbqlvd() {
      return null;
   }

   @Override
   public boolean m_70uizoygc() {
      return false;
   }

   @Override
   public int m_23mxopcac() {
      return 0;
   }

   @Override
   public boolean m_31gtetblr() {
      return false;
   }

   @Override
   public String m_93llpijut() {
      return null;
   }

   @Override
   public ChatUserInfo m_11dhevjwi(String string) {
      return null;
   }

   @Override
   public void m_37rrqnjgy(String string) {
   }

   @Override
   public boolean m_78qjaxsih() {
      return false;
   }

   @Override
   public ErrorCode m_69ungrimn() {
      return null;
   }

   @Override
   public boolean m_84uftderi() {
      return false;
   }

   @Override
   public void m_39rjuiusz(boolean bl) {
   }

   @Override
   public boolean m_81cvlsrmx() {
      return false;
   }

   @Override
   public TwitchStream.C_76cprrnwi m_56qzkeokj() {
      return TwitchStream.C_76cprrnwi.ERROR;
   }

   public Throwable getError() {
      return this.error;
   }
}
