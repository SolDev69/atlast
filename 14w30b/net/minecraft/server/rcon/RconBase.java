package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.dedicated.IDedicatedServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public abstract class RconBase implements Runnable {
   private static final AtomicInteger count = new AtomicInteger(0);
   protected boolean running;
   protected IDedicatedServer server;
   protected final String description;
   protected Thread rconThread;
   protected int five = 5;
   protected List sockets = Lists.newArrayList();
   protected List closeableSockets = Lists.newArrayList();

   protected RconBase(IDedicatedServer server, String description) {
      this.server = server;
      this.description = description;
      if (this.server.isDebuggingEnabled()) {
         this.warn("Debugging is enabled, performance maybe reduced!");
      }
   }

   public synchronized void start() {
      this.rconThread = new Thread(this, this.description + " #" + count.incrementAndGet());
      this.rconThread.start();
      this.running = true;
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void log(String message) {
      this.server.log(message);
   }

   protected void info(String message) {
      this.server.info(message);
   }

   protected void warn(String message) {
      this.server.warn(message);
   }

   protected void logError(String essage) {
      this.server.error(essage);
   }

   protected int getCurrentPlayerCount() {
      return this.server.getPlayerCount();
   }

   protected void registerSocket(DatagramSocket datagramSocket) {
      this.log("registerSocket: " + datagramSocket);
      this.sockets.add(datagramSocket);
   }

   protected boolean closeSocket(DatagramSocket socket, boolean remove) {
      this.log("closeSocket: " + socket);
      if (null == socket) {
         return false;
      } else {
         boolean var3 = false;
         if (!socket.isClosed()) {
            socket.close();
            var3 = true;
         }

         if (remove) {
            this.sockets.remove(socket);
         }

         return var3;
      }
   }

   protected boolean closeSocket(ServerSocket socket) {
      return this.closeSocket(socket, true);
   }

   protected boolean closeSocket(ServerSocket socket, boolean remove) {
      this.log("closeSocket: " + socket);
      if (null == socket) {
         return false;
      } else {
         boolean var3 = false;

         try {
            if (!socket.isClosed()) {
               socket.close();
               var3 = true;
            }
         } catch (IOException var5) {
            this.warn("IO: " + var5.getMessage());
         }

         if (remove) {
            this.closeableSockets.remove(socket);
         }

         return var3;
      }
   }

   protected void forceClose() {
      this.forceClose(false);
   }

   protected void forceClose(boolean warn) {
      int var2 = 0;

      for(DatagramSocket var4 : this.sockets) {
         if (this.closeSocket(var4, false)) {
            ++var2;
         }
      }

      this.sockets.clear();

      for(ServerSocket var6 : this.closeableSockets) {
         if (this.closeSocket(var6, false)) {
            ++var2;
         }
      }

      this.closeableSockets.clear();
      if (warn && 0 < var2) {
         this.warn("Force closed " + var2 + " sockets");
      }
   }
}
