package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.LanServerPinger;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LanServerQueryManager {
   private static final AtomicInteger threadId = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   @Environment(EnvType.CLIENT)
   public static class LanServerDetector extends Thread {
      private final LanServerQueryManager.LanServerList serverList;
      private final InetAddress address;
      private final MulticastSocket socket;

      public LanServerDetector(LanServerQueryManager.LanServerList serverList) {
         super("LanServerDetector #" + LanServerQueryManager.threadId.incrementAndGet());
         this.serverList = serverList;
         this.setDaemon(true);
         this.socket = new MulticastSocket(4445);
         this.address = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.address);
      }

      @Override
      public void run() {
         byte[] var2 = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket var1 = new DatagramPacket(var2, var2.length);

            try {
               this.socket.receive(var1);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException var6) {
               LanServerQueryManager.LOGGER.error("Couldn't ping server", var6);
               break;
            }

            String var3 = new String(var1.getData(), var1.getOffset(), var1.getLength());
            LanServerQueryManager.LOGGER.debug(var1.getAddress() + ": " + var3);
            this.serverList.addServer(var3, var1.getAddress());
         }

         try {
            this.socket.leaveGroup(this.address);
         } catch (IOException var4) {
         }

         this.socket.close();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LanServerInfo {
      private String motd;
      private String port;
      private long lastTickTime;

      public LanServerInfo(String motd, String port) {
         this.motd = motd;
         this.port = port;
         this.lastTickTime = MinecraftClient.getTime();
      }

      public String getMotd() {
         return this.motd;
      }

      public String getPort() {
         return this.port;
      }

      public void tick() {
         this.lastTickTime = MinecraftClient.getTime();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LanServerList {
      private List servers = Lists.newArrayList();
      boolean dirty;

      public synchronized boolean needsUpdate() {
         return this.dirty;
      }

      public synchronized void markClean() {
         this.dirty = false;
      }

      public synchronized List getServers() {
         return Collections.unmodifiableList(this.servers);
      }

      public synchronized void addServer(String metadata, InetAddress address) {
         String var3 = LanServerPinger.parseMotd(metadata);
         String var4 = LanServerPinger.parsePort(metadata);
         if (var4 != null) {
            var4 = address.getHostAddress() + ":" + var4;
            boolean var5 = false;

            for(LanServerQueryManager.LanServerInfo var7 : this.servers) {
               if (var7.getPort().equals(var4)) {
                  var7.tick();
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               this.servers.add(new LanServerQueryManager.LanServerInfo(var3, var4));
               this.dirty = true;
            }
         }
      }
   }
}
