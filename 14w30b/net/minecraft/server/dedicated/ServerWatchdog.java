package net.minecraft.server.dedicated;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class ServerWatchdog implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer f_10kbhtpwt;

   public ServerWatchdog(MinecraftServer minecraftServer) {
      this.f_10kbhtpwt = minecraftServer;
   }

   @Override
   public void run() {
      while(this.f_10kbhtpwt.isRunning()) {
         long var1 = MinecraftServer.getTimeMillis() - this.f_10kbhtpwt.getNextTickTime();
         if (var1 > 30000L) {
            LOGGER.fatal(
               "A single server tick took " + String.format("%.2f", (float)var1 / 1000.0F) + " seconds (should be max " + String.format("%.2f", 0.05F) + ")"
            );
            LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean var3 = ManagementFactory.getThreadMXBean();
            ThreadInfo[] var4 = var3.dumpAllThreads(true, true);
            StringBuilder var5 = new StringBuilder();
            Error var6 = new Error();

            for(ThreadInfo var10 : var4) {
               if (var10.getThreadId() == this.f_10kbhtpwt.getThread().getId()) {
                  var6.setStackTrace(var10.getStackTrace());
               }

               var5.append(var10);
               var5.append("\n");
            }

            CrashReport var12 = new CrashReport("Watching Server", var6);
            this.f_10kbhtpwt.populateCrashReport(var12);
            CashReportCategory var13 = var12.addCategory("Thread Dump");
            var13.add("Threads", var5);
            File var14 = new File(
               new File(this.f_10kbhtpwt.getRunDir(), "crash-reports"),
               "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
            );
            if (var12.writeToFile(var14)) {
               LOGGER.error("This crash report has been saved to: " + var14.getAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            System.exit(1);
         }

         try {
            Thread.sleep(30000L);
         } catch (InterruptedException var11) {
         }
      }
   }
}
