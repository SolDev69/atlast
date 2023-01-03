package net.minecraft.server.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.IDedicatedServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class QueryResponseHandler extends RconBase {
   private long lastQueryTime;
   private int queryPort;
   private int port;
   private int maxPlayerCount;
   private String motd;
   private String levelName;
   private DatagramSocket socket;
   private byte[] packetBuffer = new byte[1460];
   private DatagramPacket currentPacket;
   private Map map;
   private String ip;
   private String hostname;
   private Map queryByPacket;
   private long creationTime;
   private DataStreamHelper dataStreamHelper;
   private long lastResponseTime;

   public QueryResponseHandler(IDedicatedServer server) {
      super(server, "Query Listener");
      this.queryPort = server.getPropertyOrDefault("query.port", 0);
      this.hostname = server.getIp();
      this.port = server.getPort();
      this.motd = server.getMotd();
      this.maxPlayerCount = server.getMaxPlayerCount();
      this.levelName = server.getWorldName();
      this.lastResponseTime = 0L;
      this.ip = "0.0.0.0";
      if (0 != this.hostname.length() && !this.ip.equals(this.hostname)) {
         this.ip = this.hostname;
      } else {
         this.hostname = "0.0.0.0";

         try {
            InetAddress var2 = InetAddress.getLocalHost();
            this.ip = var2.getHostAddress();
         } catch (UnknownHostException var3) {
            this.warn("Unable to determine local host IP, please set server-ip in '" + server.getPropertiesFilePath() + "' : " + var3.getMessage());
         }
      }

      if (0 == this.queryPort) {
         this.queryPort = this.port;
         this.info("Setting default query port to " + this.queryPort);
         server.setProperty("query.port", this.queryPort);
         server.setProperty("debug", false);
         server.saveProperties();
      }

      this.map = Maps.newHashMap();
      this.dataStreamHelper = new DataStreamHelper(1460);
      this.queryByPacket = Maps.newHashMap();
      this.creationTime = new Date().getTime();
   }

   private void reply(byte[] buf, DatagramPacket packet) {
      this.socket.send(new DatagramPacket(buf, buf.length, packet.getSocketAddress()));
   }

   private boolean handle(DatagramPacket packet) {
      byte[] var2 = packet.getData();
      int var3 = packet.getLength();
      SocketAddress var4 = packet.getSocketAddress();
      this.log("Packet len " + var3 + " [" + var4 + "]");
      if (3 <= var3 && -2 == var2[0] && -3 == var2[1]) {
         this.log("Packet '" + BufferHelper.toHex(var2[2]) + "' [" + var4 + "]");
         switch(var2[2]) {
            case 0:
               if (!this.isValidQuery(packet)) {
                  this.log("Invalid challenge [" + var4 + "]");
                  return false;
               } else if (15 == var3) {
                  this.reply(this.createRulesReply(packet), packet);
                  this.log("Rules [" + var4 + "]");
               } else {
                  DataStreamHelper var5 = new DataStreamHelper(1460);
                  var5.write(0);
                  var5.write(this.getMessageBytes(packet.getSocketAddress()));
                  var5.writeBytes(this.motd);
                  var5.writeBytes("SMP");
                  var5.writeBytes(this.levelName);
                  var5.writeBytes(Integer.toString(this.getCurrentPlayerCount()));
                  var5.writeBytes(Integer.toString(this.maxPlayerCount));
                  var5.writeShort((short)this.port);
                  var5.writeBytes(this.ip);
                  this.reply(var5.bytes(), packet);
                  this.log("Status [" + var4 + "]");
               }
            default:
               return true;
            case 9:
               this.createQuery(packet);
               this.log("Challenge [" + var4 + "]");
               return true;
         }
      } else {
         this.log("Invalid packet [" + var4 + "]");
         return false;
      }
   }

   private byte[] createRulesReply(DatagramPacket packet) {
      long var2 = MinecraftServer.getTimeMillis();
      if (var2 < this.lastResponseTime + 5000L) {
         byte[] var9 = this.dataStreamHelper.bytes();
         byte[] var10 = this.getMessageBytes(packet.getSocketAddress());
         var9[1] = var10[0];
         var9[2] = var10[1];
         var9[3] = var10[2];
         var9[4] = var10[3];
         return var9;
      } else {
         this.lastResponseTime = var2;
         this.dataStreamHelper.reset();
         this.dataStreamHelper.write(0);
         this.dataStreamHelper.write(this.getMessageBytes(packet.getSocketAddress()));
         this.dataStreamHelper.writeBytes("splitnum");
         this.dataStreamHelper.write(128);
         this.dataStreamHelper.write(0);
         this.dataStreamHelper.writeBytes("hostname");
         this.dataStreamHelper.writeBytes(this.motd);
         this.dataStreamHelper.writeBytes("gametype");
         this.dataStreamHelper.writeBytes("SMP");
         this.dataStreamHelper.writeBytes("game_id");
         this.dataStreamHelper.writeBytes("MINECRAFT");
         this.dataStreamHelper.writeBytes("version");
         this.dataStreamHelper.writeBytes(this.server.getGameVersion());
         this.dataStreamHelper.writeBytes("plugins");
         this.dataStreamHelper.writeBytes(this.server.getPlugins());
         this.dataStreamHelper.writeBytes("map");
         this.dataStreamHelper.writeBytes(this.levelName);
         this.dataStreamHelper.writeBytes("numplayers");
         this.dataStreamHelper.writeBytes("" + this.getCurrentPlayerCount());
         this.dataStreamHelper.writeBytes("maxplayers");
         this.dataStreamHelper.writeBytes("" + this.maxPlayerCount);
         this.dataStreamHelper.writeBytes("hostport");
         this.dataStreamHelper.writeBytes("" + this.port);
         this.dataStreamHelper.writeBytes("hostip");
         this.dataStreamHelper.writeBytes(this.ip);
         this.dataStreamHelper.write(0);
         this.dataStreamHelper.write(1);
         this.dataStreamHelper.writeBytes("player_");
         this.dataStreamHelper.write(0);
         String[] var4 = this.server.getPlayerNames();

         for(String var8 : var4) {
            this.dataStreamHelper.writeBytes(var8);
         }

         this.dataStreamHelper.write(0);
         return this.dataStreamHelper.bytes();
      }
   }

   private byte[] getMessageBytes(SocketAddress socketAddress) {
      return ((QueryResponseHandler.Query)this.queryByPacket.get(socketAddress)).getMessageBytes();
   }

   private Boolean isValidQuery(DatagramPacket datagramPacket) {
      SocketAddress var2 = datagramPacket.getSocketAddress();
      if (!this.queryByPacket.containsKey(var2)) {
         return false;
      } else {
         byte[] var3 = datagramPacket.getData();
         return ((QueryResponseHandler.Query)this.queryByPacket.get(var2)).getId() != BufferHelper.getIntBE(var3, 7, datagramPacket.getLength()) ? false : true;
      }
   }

   private void createQuery(DatagramPacket datagramPacket) {
      QueryResponseHandler.Query var2 = new QueryResponseHandler.Query(datagramPacket);
      this.queryByPacket.put(datagramPacket.getSocketAddress(), var2);
      this.reply(var2.getReplyBuf(), datagramPacket);
   }

   private void cleanUp() {
      if (this.running) {
         long var1 = MinecraftServer.getTimeMillis();
         if (var1 >= this.lastQueryTime + 30000L) {
            this.lastQueryTime = var1;
            Iterator var3 = this.queryByPacket.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               if (((QueryResponseHandler.Query)var4.getValue()).startedBefore(var1)) {
                  var3.remove();
               }
            }
         }
      }
   }

   @Override
   public void run() {
      this.info("Query running on " + this.hostname + ":" + this.queryPort);
      this.lastQueryTime = MinecraftServer.getTimeMillis();
      this.currentPacket = new DatagramPacket(this.packetBuffer, this.packetBuffer.length);

      try {
         while(this.running) {
            try {
               this.socket.receive(this.currentPacket);
               this.cleanUp();
               this.handle(this.currentPacket);
            } catch (SocketTimeoutException var7) {
               this.cleanUp();
            } catch (PortUnreachableException var8) {
            } catch (IOException var9) {
               this.handleIoException(var9);
            }
         }
      } finally {
         this.forceClose();
      }
   }

   @Override
   public void start() {
      if (!this.running) {
         if (0 < this.queryPort && 65535 >= this.queryPort) {
            if (this.initialize()) {
               super.start();
            }
         } else {
            this.warn("Invalid query port " + this.queryPort + " found in '" + this.server.getPropertiesFilePath() + "' (queries disabled)");
         }
      }
   }

   private void handleIoException(Exception e) {
      if (this.running) {
         this.warn("Unexpected exception, buggy JRE? (" + e.toString() + ")");
         if (!this.initialize()) {
            this.logError("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }
      }
   }

   private boolean initialize() {
      try {
         this.socket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.hostname));
         this.registerSocket(this.socket);
         this.socket.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.warn("Unable to initialise query system on " + this.hostname + ":" + this.queryPort + " (E): " + var4.getMessage());
      }

      return false;
   }

   @Environment(EnvType.SERVER)
   class Query {
      private long startTime = new Date().getTime();
      private int id;
      private byte[] messageBytes;
      private byte[] replyBuf;
      private String message;

      public Query(DatagramPacket packet) {
         byte[] var3 = packet.getData();
         this.messageBytes = new byte[4];
         this.messageBytes[0] = var3[3];
         this.messageBytes[1] = var3[4];
         this.messageBytes[2] = var3[5];
         this.messageBytes[3] = var3[6];
         this.message = new String(this.messageBytes);
         this.id = new Random().nextInt(16777216);
         this.replyBuf = String.format("\t%s%d\u0000", this.message, this.id).getBytes();
      }

      public Boolean startedBefore(long lastQueryTime) {
         return this.startTime < lastQueryTime;
      }

      public int getId() {
         return this.id;
      }

      public byte[] getReplyBuf() {
         return this.replyBuf;
      }

      public byte[] getMessageBytes() {
         return this.messageBytes;
      }
   }
}
