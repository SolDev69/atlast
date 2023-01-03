package net.minecraft;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

@Environment(EnvType.CLIENT)
public class C_55vyyvcbb {
   private static final Logger f_21bkevnpu = LogManager.getLogger();
   protected C_55vyyvcbb.C_21ughtocu f_62odwctgn = null;
   protected String f_77klajvkg = "";
   protected String f_46qrayvhf = "";
   protected String f_35qnywpsv = "";
   protected Core f_18ztaorze = null;
   protected Chat f_74zuaatue = null;
   protected C_55vyyvcbb.C_63ocjvuuw f_56gpwgroz = C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED;
   protected AuthToken f_16mccznxv = new AuthToken();
   protected HashMap f_69gxvioov = new HashMap();
   protected int f_67kexpqek = 128;
   protected C_55vyyvcbb.C_78hfgphwi f_31ncbkjee = C_55vyyvcbb.C_78hfgphwi.NONE;
   protected C_55vyyvcbb.C_78hfgphwi f_66iokfkal = C_55vyyvcbb.C_78hfgphwi.NONE;
   protected ChatEmoticonData f_92ioftvzp = null;
   protected int f_15hkmyfso = 500;
   protected int f_61xqjgwri = 2000;
   protected IChatAPIListener f_30scxunht = new IChatAPIListener() {
      public void chatInitializationCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            C_55vyyvcbb.this.f_74zuaatue.setMessageFlushInterval(C_55vyyvcbb.this.f_15hkmyfso);
            C_55vyyvcbb.this.f_74zuaatue.setUserChangeEventInterval(C_55vyyvcbb.this.f_61xqjgwri);
            C_55vyyvcbb.this.m_18egdvvor();
            C_55vyyvcbb.this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.INITIALIZED);
         } else {
            C_55vyyvcbb.this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED);
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_94hoompvc(errorCode);
            }
         } catch (Exception var3) {
            C_55vyyvcbb.this.m_09soggvgu(var3.toString());
         }
      }

      public void chatShutdownCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            ErrorCode var2 = C_55vyyvcbb.this.f_18ztaorze.shutdown();
            if (ErrorCode.failed(var2)) {
               String var3 = ErrorCode.getString(var2);
               C_55vyyvcbb.this.m_09soggvgu(String.format("Error shutting down the Twitch sdk: %s", var3));
            }

            C_55vyyvcbb.this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED);
         } else {
            C_55vyyvcbb.this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.INITIALIZED);
            C_55vyyvcbb.this.m_09soggvgu(String.format("Error shutting down Twith chat: %s", errorCode));
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_95mdtnzem(errorCode);
            }
         } catch (Exception var4) {
            C_55vyyvcbb.this.m_09soggvgu(var4.toString());
         }
      }

      public void chatEmoticonDataDownloadCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            C_55vyyvcbb.this.m_49gnnpvto();
         }
      }
   };

   public void m_89waqnhfj(C_55vyyvcbb.C_21ughtocu c_21ughtocu) {
      this.f_62odwctgn = c_21ughtocu;
   }

   public void m_83aycowfd(AuthToken authToken) {
      this.f_16mccznxv = authToken;
   }

   public void m_50ioolqzh(String string) {
      this.f_46qrayvhf = string;
   }

   public void m_23ywwutzp(String string) {
      this.f_77klajvkg = string;
   }

   public C_55vyyvcbb.C_63ocjvuuw m_42slekjgq() {
      return this.f_56gpwgroz;
   }

   public boolean m_23bnorqlj(String string) {
      if (!this.f_69gxvioov.containsKey(string)) {
         return false;
      } else {
         C_55vyyvcbb.C_01qazdsjm var2 = (C_55vyyvcbb.C_01qazdsjm)this.f_69gxvioov.get(string);
         return var2.m_52ebgqjcg() == C_55vyyvcbb.C_66pbsbnsh.CONNECTED;
      }
   }

   public C_55vyyvcbb.C_66pbsbnsh m_90xvfyguu(String string) {
      if (!this.f_69gxvioov.containsKey(string)) {
         return C_55vyyvcbb.C_66pbsbnsh.DISCONNECTED;
      } else {
         C_55vyyvcbb.C_01qazdsjm var2 = (C_55vyyvcbb.C_01qazdsjm)this.f_69gxvioov.get(string);
         return var2.m_52ebgqjcg();
      }
   }

   public C_55vyyvcbb() {
      this.f_18ztaorze = Core.getInstance();
      if (this.f_18ztaorze == null) {
         this.f_18ztaorze = new Core(new StandardCoreAPI());
      }

      this.f_74zuaatue = new Chat(new StandardChatAPI());
   }

   public boolean m_00ktxmyoz() {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED) {
         return false;
      } else {
         this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.INITIALIZING);
         ErrorCode var1 = this.f_18ztaorze.initialize(this.f_46qrayvhf, null);
         if (ErrorCode.failed(var1)) {
            this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED);
            String var5 = ErrorCode.getString(var1);
            this.m_09soggvgu(String.format("Error initializing Twitch sdk: %s", var5));
            return false;
         } else {
            this.f_66iokfkal = this.f_31ncbkjee;
            HashSet var2 = new HashSet();
            switch(this.f_31ncbkjee) {
               case NONE:
                  var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
                  break;
               case URL:
                  var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
                  break;
               case TEXTURE_ATLAS:
                  var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
            }

            var1 = this.f_74zuaatue.initialize(var2, this.f_30scxunht);
            if (ErrorCode.failed(var1)) {
               this.f_18ztaorze.shutdown();
               this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED);
               String var3 = ErrorCode.getString(var1);
               this.m_09soggvgu(String.format("Error initializing Twitch chat: %s", var3));
               return false;
            } else {
               this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.INITIALIZED);
               return true;
            }
         }
      }
   }

   public boolean m_75aklpftj(String string) {
      return this.m_28gzfaeic(string, false);
   }

   protected boolean m_28gzfaeic(String string, boolean bl) {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.INITIALIZED) {
         return false;
      } else if (this.f_69gxvioov.containsKey(string)) {
         this.m_09soggvgu("Already in channel: " + string);
         return false;
      } else if (string != null && !string.equals("")) {
         C_55vyyvcbb.C_01qazdsjm var3 = new C_55vyyvcbb.C_01qazdsjm(string);
         this.f_69gxvioov.put(string, var3);
         boolean var4 = var3.m_68hicjarr(bl);
         if (!var4) {
            this.f_69gxvioov.remove(string);
         }

         return var4;
      } else {
         return false;
      }
   }

   public boolean m_77eyyxfhf(String string) {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.INITIALIZED) {
         return false;
      } else if (!this.f_69gxvioov.containsKey(string)) {
         this.m_09soggvgu("Not in channel: " + string);
         return false;
      } else {
         C_55vyyvcbb.C_01qazdsjm var2 = (C_55vyyvcbb.C_01qazdsjm)this.f_69gxvioov.get(string);
         return var2.m_21mxyuheg();
      }
   }

   public boolean m_55prwqizz() {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.INITIALIZED) {
         return false;
      } else {
         ErrorCode var1 = this.f_74zuaatue.shutdown();
         if (ErrorCode.failed(var1)) {
            String var2 = ErrorCode.getString(var1);
            this.m_09soggvgu(String.format("Error shutting down chat: %s", var2));
            return false;
         } else {
            this.m_08bmaospg();
            this.m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw.SHUTTING_DOWN);
            return true;
         }
      }
   }

   public void m_60ievhajv() {
      if (this.m_42slekjgq() != C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED) {
         this.m_55prwqizz();
         if (this.m_42slekjgq() == C_55vyyvcbb.C_63ocjvuuw.SHUTTING_DOWN) {
            while(this.m_42slekjgq() != C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED) {
               try {
                  Thread.sleep(200L);
                  this.m_12grkirzd();
               } catch (InterruptedException var2) {
               }
            }
         }
      }
   }

   public void m_12grkirzd() {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.UNINITIALIZED) {
         ErrorCode var1 = this.f_74zuaatue.flushEvents();
         if (ErrorCode.failed(var1)) {
            String var2 = ErrorCode.getString(var1);
            this.m_09soggvgu(String.format("Error flushing chat events: %s", var2));
         }
      }
   }

   public boolean m_65ylxsqcj(String string, String string2) {
      if (this.f_56gpwgroz != C_55vyyvcbb.C_63ocjvuuw.INITIALIZED) {
         return false;
      } else if (!this.f_69gxvioov.containsKey(string)) {
         this.m_09soggvgu("Not in channel: " + string);
         return false;
      } else {
         C_55vyyvcbb.C_01qazdsjm var3 = (C_55vyyvcbb.C_01qazdsjm)this.f_69gxvioov.get(string);
         return var3.m_44aoymyso(string2);
      }
   }

   protected void m_92shypzlz(C_55vyyvcbb.C_63ocjvuuw c_63ocjvuuw) {
      if (c_63ocjvuuw != this.f_56gpwgroz) {
         this.f_56gpwgroz = c_63ocjvuuw;

         try {
            if (this.f_62odwctgn != null) {
               this.f_62odwctgn.m_88pocigdf(c_63ocjvuuw);
            }
         } catch (Exception var3) {
            this.m_09soggvgu(var3.toString());
         }
      }
   }

   protected void m_18egdvvor() {
      if (this.f_66iokfkal != C_55vyyvcbb.C_78hfgphwi.NONE) {
         if (this.f_92ioftvzp == null) {
            ErrorCode var1 = this.f_74zuaatue.downloadEmoticonData();
            if (ErrorCode.failed(var1)) {
               String var2 = ErrorCode.getString(var1);
               this.m_09soggvgu(String.format("Error trying to download emoticon data: %s", var2));
            }
         }
      }
   }

   protected void m_49gnnpvto() {
      if (this.f_92ioftvzp == null) {
         this.f_92ioftvzp = new ChatEmoticonData();
         ErrorCode var1 = this.f_74zuaatue.getEmoticonData(this.f_92ioftvzp);
         if (ErrorCode.succeeded(var1)) {
            try {
               if (this.f_62odwctgn != null) {
                  this.f_62odwctgn.m_12zkeyvhd();
               }
            } catch (Exception var3) {
               this.m_09soggvgu(var3.toString());
            }
         } else {
            this.m_09soggvgu("Error preparing emoticon data: " + ErrorCode.getString(var1));
         }
      }
   }

   protected void m_08bmaospg() {
      if (this.f_92ioftvzp != null) {
         ErrorCode var1 = this.f_74zuaatue.clearEmoticonData();
         if (ErrorCode.succeeded(var1)) {
            this.f_92ioftvzp = null;

            try {
               if (this.f_62odwctgn != null) {
                  this.f_62odwctgn.m_90fsxvyzy();
               }
            } catch (Exception var3) {
               this.m_09soggvgu(var3.toString());
            }
         } else {
            this.m_09soggvgu("Error clearing emoticon data: " + ErrorCode.getString(var1));
         }
      }
   }

   protected void m_09soggvgu(String string) {
      f_21bkevnpu.error(C_48kamxasz.f_44ugpmdwg, "[Chat controller] {}", new Object[]{string});
   }

   @Environment(EnvType.CLIENT)
   public class C_01qazdsjm implements IChatChannelListener {
      protected String f_15ngdfrne = null;
      protected boolean f_58alachnv = false;
      protected C_55vyyvcbb.C_66pbsbnsh f_56rvjmzat = C_55vyyvcbb.C_66pbsbnsh.CREATED;
      protected List f_70ibnziqh = Lists.newArrayList();
      protected LinkedList f_06xjfbbck = new LinkedList();
      protected LinkedList f_62ffzbwgl = new LinkedList();
      protected ChatBadgeData f_26dhwhsui = null;

      public C_01qazdsjm(String string) {
         this.f_15ngdfrne = string;
      }

      public C_55vyyvcbb.C_66pbsbnsh m_52ebgqjcg() {
         return this.f_56rvjmzat;
      }

      public boolean m_68hicjarr(boolean bl) {
         this.f_58alachnv = bl;
         ErrorCode var2 = ErrorCode.TTV_EC_SUCCESS;
         if (bl) {
            var2 = C_55vyyvcbb.this.f_74zuaatue.connectAnonymous(this.f_15ngdfrne, this);
         } else {
            var2 = C_55vyyvcbb.this.f_74zuaatue.connect(this.f_15ngdfrne, C_55vyyvcbb.this.f_77klajvkg, C_55vyyvcbb.this.f_16mccznxv.data, this);
         }

         if (ErrorCode.failed(var2)) {
            String var3 = ErrorCode.getString(var2);
            C_55vyyvcbb.this.m_09soggvgu(String.format("Error connecting: %s", var3));
            this.m_78pwliywo(this.f_15ngdfrne);
            return false;
         } else {
            this.m_96bidrvlq(C_55vyyvcbb.C_66pbsbnsh.CONNECTING);
            this.m_99ncejndj();
            return true;
         }
      }

      public boolean m_21mxyuheg() {
         switch(this.f_56rvjmzat) {
            case CONNECTED:
            case CONNECTING:
               ErrorCode var1 = C_55vyyvcbb.this.f_74zuaatue.disconnect(this.f_15ngdfrne);
               if (ErrorCode.failed(var1)) {
                  String var2 = ErrorCode.getString(var1);
                  C_55vyyvcbb.this.m_09soggvgu(String.format("Error disconnecting: %s", var2));
                  return false;
               }

               this.m_96bidrvlq(C_55vyyvcbb.C_66pbsbnsh.DISCONNECTING);
               return true;
            case CREATED:
            case DISCONNECTED:
            case DISCONNECTING:
            default:
               return false;
         }
      }

      protected void m_96bidrvlq(C_55vyyvcbb.C_66pbsbnsh c_66pbsbnsh) {
         if (c_66pbsbnsh != this.f_56rvjmzat) {
            this.f_56rvjmzat = c_66pbsbnsh;
         }
      }

      public void m_25jqyrdek(String string) {
         if (C_55vyyvcbb.this.f_66iokfkal == C_55vyyvcbb.C_78hfgphwi.NONE) {
            this.f_06xjfbbck.clear();
            this.f_62ffzbwgl.clear();
         } else {
            if (this.f_06xjfbbck.size() > 0) {
               ListIterator var2 = this.f_06xjfbbck.listIterator();

               while(var2.hasNext()) {
                  ChatRawMessage var3 = (ChatRawMessage)var2.next();
                  if (var3.userName.equals(string)) {
                     var2.remove();
                  }
               }
            }

            if (this.f_62ffzbwgl.size() > 0) {
               ListIterator var5 = this.f_62ffzbwgl.listIterator();

               while(var5.hasNext()) {
                  ChatTokenizedMessage var6 = (ChatTokenizedMessage)var5.next();
                  if (var6.displayName.equals(string)) {
                     var5.remove();
                  }
               }
            }
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_07cazmsoa(this.f_15ngdfrne, string);
            }
         } catch (Exception var4) {
            C_55vyyvcbb.this.m_09soggvgu(var4.toString());
         }
      }

      public boolean m_44aoymyso(String string) {
         if (this.f_56rvjmzat != C_55vyyvcbb.C_66pbsbnsh.CONNECTED) {
            return false;
         } else {
            ErrorCode var2 = C_55vyyvcbb.this.f_74zuaatue.sendMessage(this.f_15ngdfrne, string);
            if (ErrorCode.failed(var2)) {
               String var3 = ErrorCode.getString(var2);
               C_55vyyvcbb.this.m_09soggvgu(String.format("Error sending chat message: %s", var3));
               return false;
            } else {
               return true;
            }
         }
      }

      protected void m_99ncejndj() {
         if (C_55vyyvcbb.this.f_66iokfkal != C_55vyyvcbb.C_78hfgphwi.NONE) {
            if (this.f_26dhwhsui == null) {
               ErrorCode var1 = C_55vyyvcbb.this.f_74zuaatue.downloadBadgeData(this.f_15ngdfrne);
               if (ErrorCode.failed(var1)) {
                  String var2 = ErrorCode.getString(var1);
                  C_55vyyvcbb.this.m_09soggvgu(String.format("Error trying to download badge data: %s", var2));
               }
            }
         }
      }

      protected void m_82ndjcpwt() {
         if (this.f_26dhwhsui == null) {
            this.f_26dhwhsui = new ChatBadgeData();
            ErrorCode var1 = C_55vyyvcbb.this.f_74zuaatue.getBadgeData(this.f_15ngdfrne, this.f_26dhwhsui);
            if (ErrorCode.succeeded(var1)) {
               try {
                  if (C_55vyyvcbb.this.f_62odwctgn != null) {
                     C_55vyyvcbb.this.f_62odwctgn.m_23iyixmle(this.f_15ngdfrne);
                  }
               } catch (Exception var3) {
                  C_55vyyvcbb.this.m_09soggvgu(var3.toString());
               }
            } else {
               C_55vyyvcbb.this.m_09soggvgu("Error preparing badge data: " + ErrorCode.getString(var1));
            }
         }
      }

      protected void m_20honxogh() {
         if (this.f_26dhwhsui != null) {
            ErrorCode var1 = C_55vyyvcbb.this.f_74zuaatue.clearBadgeData(this.f_15ngdfrne);
            if (ErrorCode.succeeded(var1)) {
               this.f_26dhwhsui = null;

               try {
                  if (C_55vyyvcbb.this.f_62odwctgn != null) {
                     C_55vyyvcbb.this.f_62odwctgn.m_14zuefaqv(this.f_15ngdfrne);
                  }
               } catch (Exception var3) {
                  C_55vyyvcbb.this.m_09soggvgu(var3.toString());
               }
            } else {
               C_55vyyvcbb.this.m_09soggvgu("Error releasing badge data: " + ErrorCode.getString(var1));
            }
         }
      }

      protected void m_30isnhoyy(String string) {
         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_31febjeqh(string);
            }
         } catch (Exception var3) {
            C_55vyyvcbb.this.m_09soggvgu(var3.toString());
         }
      }

      protected void m_78pwliywo(String string) {
         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_85flcyxgx(string);
            }
         } catch (Exception var3) {
            C_55vyyvcbb.this.m_09soggvgu(var3.toString());
         }
      }

      private void m_19sohtgpe() {
         if (this.f_56rvjmzat != C_55vyyvcbb.C_66pbsbnsh.DISCONNECTED) {
            this.m_96bidrvlq(C_55vyyvcbb.C_66pbsbnsh.DISCONNECTED);
            this.m_78pwliywo(this.f_15ngdfrne);
            this.m_20honxogh();
         }
      }

      public void chatStatusCallback(String string, ErrorCode errorCode) {
         if (!ErrorCode.succeeded(errorCode)) {
            C_55vyyvcbb.this.f_69gxvioov.remove(string);
            this.m_19sohtgpe();
         }
      }

      public void chatChannelMembershipCallback(String string, ChatEvent chatEvent, ChatChannelInfo chatChannelInfo) {
         switch(chatEvent) {
            case TTV_CHAT_JOINED_CHANNEL:
               this.m_96bidrvlq(C_55vyyvcbb.C_66pbsbnsh.CONNECTED);
               this.m_30isnhoyy(string);
               break;
            case TTV_CHAT_LEFT_CHANNEL:
               this.m_19sohtgpe();
         }
      }

      public void chatChannelUserChangeCallback(String string, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3) {
         for(int var5 = 0; var5 < chatUserInfos2.length; ++var5) {
            int var6 = this.f_70ibnziqh.indexOf(chatUserInfos2[var5]);
            if (var6 >= 0) {
               this.f_70ibnziqh.remove(var6);
            }
         }

         for(int var8 = 0; var8 < chatUserInfos3.length; ++var8) {
            int var10 = this.f_70ibnziqh.indexOf(chatUserInfos3[var8]);
            if (var10 >= 0) {
               this.f_70ibnziqh.remove(var10);
            }

            this.f_70ibnziqh.add(chatUserInfos3[var8]);
         }

         for(int var9 = 0; var9 < chatUserInfos.length; ++var9) {
            this.f_70ibnziqh.add(chatUserInfos[var9]);
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_69xctqpwe(this.f_15ngdfrne, chatUserInfos, chatUserInfos2, chatUserInfos3);
            }
         } catch (Exception var7) {
            C_55vyyvcbb.this.m_09soggvgu(var7.toString());
         }
      }

      public void chatChannelRawMessageCallback(String string, ChatRawMessage[] chatRawMessages) {
         for(int var3 = 0; var3 < chatRawMessages.length; ++var3) {
            this.f_06xjfbbck.addLast(chatRawMessages[var3]);
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_04qaurtal(this.f_15ngdfrne, chatRawMessages);
            }
         } catch (Exception var4) {
            C_55vyyvcbb.this.m_09soggvgu(var4.toString());
         }

         while(this.f_06xjfbbck.size() > C_55vyyvcbb.this.f_67kexpqek) {
            this.f_06xjfbbck.removeFirst();
         }
      }

      public void chatChannelTokenizedMessageCallback(String string, ChatTokenizedMessage[] chatTokenizedMessages) {
         for(int var3 = 0; var3 < chatTokenizedMessages.length; ++var3) {
            this.f_62ffzbwgl.addLast(chatTokenizedMessages[var3]);
         }

         try {
            if (C_55vyyvcbb.this.f_62odwctgn != null) {
               C_55vyyvcbb.this.f_62odwctgn.m_55qbwjsme(this.f_15ngdfrne, chatTokenizedMessages);
            }
         } catch (Exception var4) {
            C_55vyyvcbb.this.m_09soggvgu(var4.toString());
         }

         while(this.f_62ffzbwgl.size() > C_55vyyvcbb.this.f_67kexpqek) {
            this.f_62ffzbwgl.removeFirst();
         }
      }

      public void chatClearCallback(String string, String string2) {
         this.m_25jqyrdek(string2);
      }

      public void chatBadgeDataDownloadCallback(String string, ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            this.m_82ndjcpwt();
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public interface C_21ughtocu {
      void m_94hoompvc(ErrorCode errorCode);

      void m_95mdtnzem(ErrorCode errorCode);

      void m_12zkeyvhd();

      void m_90fsxvyzy();

      void m_88pocigdf(C_55vyyvcbb.C_63ocjvuuw c_63ocjvuuw);

      void m_55qbwjsme(String string, ChatTokenizedMessage[] chatTokenizedMessages);

      void m_04qaurtal(String string, ChatRawMessage[] chatRawMessages);

      void m_69xctqpwe(String string, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3);

      void m_31febjeqh(String string);

      void m_85flcyxgx(String string);

      void m_07cazmsoa(String string, String string2);

      void m_23iyixmle(String string);

      void m_14zuefaqv(String string);
   }

   @Environment(EnvType.CLIENT)
   public static enum C_63ocjvuuw {
      UNINITIALIZED,
      INITIALIZING,
      INITIALIZED,
      SHUTTING_DOWN;
   }

   @Environment(EnvType.CLIENT)
   public static enum C_66pbsbnsh {
      CREATED,
      CONNECTING,
      CONNECTED,
      DISCONNECTING,
      DISCONNECTED;
   }

   @Environment(EnvType.CLIENT)
   public static enum C_78hfgphwi {
      NONE,
      URL,
      TEXTURE_ATLAS;
   }
}
