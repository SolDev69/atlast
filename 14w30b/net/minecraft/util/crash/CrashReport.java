package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.world.biome.IntArrays;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String description;
   private final Throwable exception;
   private final CashReportCategory systemDetails = new CashReportCategory(this, "System Details");
   private final List details = Lists.newArrayList();
   private File file;
   private boolean hasStackTrace = true;
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReport(String description, Throwable cause) {
      this.description = description;
      this.exception = cause;
      this.fillSystemDetails();
   }

   private void fillSystemDetails() {
      this.systemDetails.add("Minecraft Version", new Callable() {
         public String call() {
            return "14w30b";
         }
      });
      this.systemDetails.add("Operating System", new Callable() {
         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }
      });
      this.systemDetails.add("Java Version", new Callable() {
         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }
      });
      this.systemDetails.add("Java VM Version", new Callable() {
         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }
      });
      this.systemDetails.add("Memory", new Callable() {
         public String call() {
            Runtime var1 = Runtime.getRuntime();
            long var2 = var1.maxMemory();
            long var4 = var1.totalMemory();
            long var6 = var1.freeMemory();
            long var8 = var2 / 1024L / 1024L;
            long var10 = var4 / 1024L / 1024L;
            long var12 = var6 / 1024L / 1024L;
            return var6 + " bytes (" + var12 + " MB) / " + var4 + " bytes (" + var10 + " MB) up to " + var2 + " bytes (" + var8 + " MB)";
         }
      });
      this.systemDetails.add("JVM Flags", new Callable() {
         public String call() {
            RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
            List var2 = var1.getInputArguments();
            int var3 = 0;
            StringBuilder var4 = new StringBuilder();

            for(String var6 : var2) {
               if (var6.startsWith("-X")) {
                  if (var3++ > 0) {
                     var4.append(" ");
                  }

                  var4.append(var6);
               }
            }

            return String.format("%d total; %s", var3, var4.toString());
         }
      });
      this.systemDetails.add("IntCache", new Callable() {
         public String call() {
            return IntArrays.toString();
         }
      });
   }

   public String getDescription() {
      return this.description;
   }

   public Throwable getException() {
      return this.exception;
   }

   public void addDetails(StringBuilder sb) {
      if ((this.stackTrace == null || this.stackTrace.length <= 0) && this.details.size() > 0) {
         this.stackTrace = (StackTraceElement[])ArrayUtils.subarray(((CashReportCategory)this.details.get(0)).getStackTrace(), 0, 1);
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         sb.append("-- Head --\n");
         sb.append("Stacktrace:\n");

         for(StackTraceElement var5 : this.stackTrace) {
            sb.append("\t").append("at ").append(var5.toString());
            sb.append("\n");
         }

         sb.append("\n");
      }

      for(CashReportCategory var7 : this.details) {
         var7.addDetails(sb);
         sb.append("\n\n");
      }

      this.systemDetails.addDetails(sb);
   }

   public String getExceptionMessage() {
      StringWriter var1 = null;
      PrintWriter var2 = null;
      Object var3 = this.exception;
      if (var3.getMessage() == null) {
         if (var3 instanceof NullPointerException) {
            var3 = new NullPointerException(this.description);
         } else if (var3 instanceof StackOverflowError) {
            var3 = new StackOverflowError(this.description);
         } else if (var3 instanceof OutOfMemoryError) {
            var3 = new OutOfMemoryError(this.description);
         }

         var3.setStackTrace(this.exception.getStackTrace());
      }

      String var4 = var3.toString();

      try {
         var1 = new StringWriter();
         var2 = new PrintWriter(var1);
         var3.printStackTrace(var2);
         var4 = var1.toString();
      } finally {
         IOUtils.closeQuietly(var1);
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   public String buildReport() {
      StringBuilder var1 = new StringBuilder();
      var1.append("---- Minecraft Crash Report ----\n");
      var1.append("// ");
      var1.append(getWittyComment());
      var1.append("\n\n");
      var1.append("Time: ");
      var1.append(new SimpleDateFormat().format(new Date()));
      var1.append("\n");
      var1.append("Description: ");
      var1.append(this.description);
      var1.append("\n\n");
      var1.append(this.getExceptionMessage());
      var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int var2 = 0; var2 < 87; ++var2) {
         var1.append("-");
      }

      var1.append("\n\n");
      this.addDetails(var1);
      return var1.toString();
   }

   @Environment(EnvType.CLIENT)
   public File getFile() {
      return this.file;
   }

   public boolean writeToFile(File file) {
      if (this.file != null) {
         return false;
      } else {
         if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
         }

         try {
            FileWriter var2 = new FileWriter(file);
            var2.write(this.buildReport());
            var2.close();
            this.file = file;
            return true;
         } catch (Throwable var3) {
            LOGGER.error("Could not save crash report to " + file, var3);
            return false;
         }
      }
   }

   public CashReportCategory getSystemDetails() {
      return this.systemDetails;
   }

   public CashReportCategory addCategory(String title) {
      return this.addCategory(title, 1);
   }

   public CashReportCategory addCategory(String title, int ignoredStackTraceCallCount) {
      CashReportCategory var3 = new CashReportCategory(this, title);
      if (this.hasStackTrace) {
         int var4 = var3.getStackTrace(ignoredStackTraceCallCount);
         StackTraceElement[] var5 = this.exception.getStackTrace();
         StackTraceElement var6 = null;
         StackTraceElement var7 = null;
         int var8 = var5.length - var4;
         if (var8 < 0) {
            System.out.println("Negative index in crash report handler (" + var5.length + "/" + var4 + ")");
         }

         if (var5 != null && 0 <= var8 && var8 < var5.length) {
            var6 = var5[var8];
            if (var5.length + 1 - var4 < var5.length) {
               var7 = var5[var5.length + 1 - var4];
            }
         }

         this.hasStackTrace = var3.validateStackTrace(var6, var7);
         if (var4 > 0 && !this.details.isEmpty()) {
            CashReportCategory var9 = (CashReportCategory)this.details.get(this.details.size() - 1);
            var9.trimStackTrace(var4);
         } else if (var5 != null && var5.length >= var4 && 0 <= var8 && var8 < var5.length) {
            this.stackTrace = new StackTraceElement[var8];
            System.arraycopy(var5, 0, this.stackTrace, 0, this.stackTrace.length);
         } else {
            this.hasStackTrace = false;
         }
      }

      this.details.add(var3);
      return var3;
   }

   private static String getWittyComment() {
      String[] var0 = new String[]{
         "Who set us up the TNT?",
         "Everything's going to plan. No, really, that was supposed to happen.",
         "Uh... Did I do that?",
         "Oops.",
         "Why did you do that?",
         "I feel sad now :(",
         "My bad.",
         "I'm sorry, Dave.",
         "I let you down. Sorry :(",
         "On the bright side, I bought you a teddy bear!",
         "Daisy, daisy...",
         "Oh - I know what I did wrong!",
         "Hey, that tickles! Hehehe!",
         "I blame Dinnerbone.",
         "You should try our sister game, Minceraft!",
         "Don't be sad. I'll do better next time, I promise!",
         "Don't be sad, have a hug! <3",
         "I just don't know what went wrong :(",
         "Shall we play a game?",
         "Quite honestly, I wouldn't worry myself about that.",
         "I bet Cylons wouldn't have this problem.",
         "Sorry :(",
         "Surprise! Haha. Well, this is awkward.",
         "Would you like a cupcake?",
         "Hi. I'm Minecraft, and I'm a crashaholic.",
         "Ooh. Shiny.",
         "This doesn't make any sense!",
         "Why is it breaking :(",
         "Don't do that.",
         "Ouch. That hurt :(",
         "You're mean.",
         "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
         "There are four lights!",
         "But it works on my machine."
      };

      try {
         return var0[(int)(System.nanoTime() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport of(Throwable exception, String title) {
      CrashReport var2;
      if (exception instanceof CrashException) {
         var2 = ((CrashException)exception).getReport();
      } else {
         var2 = new CrashReport(title, exception);
      }

      return var2;
   }
}
