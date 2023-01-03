package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;

public class CashReportCategory {
   private final CrashReport report;
   private final String title;
   private final List entries = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CashReportCategory(CrashReport report, String title) {
      this.report = report;
      this.title = title;
   }

   public static String formatPosition(double x, double y, double z) {
      return String.format("%.2f,%.2f,%.2f - %s", x, y, z, formatPosition(new BlockPos(x, y, z)));
   }

   public static String formatPosition(BlockPos pos) {
      int var1 = pos.getX();
      int var2 = pos.getY();
      int var3 = pos.getZ();
      StringBuilder var4 = new StringBuilder();

      try {
         var4.append(String.format("World: (%d,%d,%d)", var1, var2, var3));
      } catch (Throwable var17) {
         var4.append("(Error finding world loc)");
      }

      var4.append(", ");

      try {
         int var5 = var1 >> 4;
         int var6 = var3 >> 4;
         int var7 = var1 & 15;
         int var8 = var2 >> 4;
         int var9 = var3 & 15;
         int var10 = var5 << 4;
         int var11 = var6 << 4;
         int var12 = (var5 + 1 << 4) - 1;
         int var13 = (var6 + 1 << 4) - 1;
         var4.append(
            String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var7, var8, var9, var5, var6, var10, var11, var12, var13)
         );
      } catch (Throwable var16) {
         var4.append("(Error finding chunk loc)");
      }

      var4.append(", ");

      try {
         int var18 = var1 >> 9;
         int var19 = var3 >> 9;
         int var20 = var18 << 5;
         int var21 = var19 << 5;
         int var22 = (var18 + 1 << 5) - 1;
         int var23 = (var19 + 1 << 5) - 1;
         int var24 = var18 << 9;
         int var25 = var19 << 9;
         int var26 = (var18 + 1 << 9) - 1;
         int var14 = (var19 + 1 << 9) - 1;
         var4.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               var18,
               var19,
               var20,
               var21,
               var22,
               var23,
               var24,
               var25,
               var26,
               var14
            )
         );
      } catch (Throwable var15) {
         var4.append("(Error finding world loc)");
      }

      return var4.toString();
   }

   public void add(String key, Callable value) {
      try {
         this.add(key, value.call());
      } catch (Throwable var4) {
         this.add(key, var4);
      }
   }

   public void add(String key, Object value) {
      this.entries.add(new CashReportCategory.Entry(key, value));
   }

   public void add(String key, Throwable value) {
      this.add(key, (Object)value);
   }

   public int getStackTrace(int ignoredCallCount) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      if (var2.length <= 0) {
         return 0;
      } else {
         this.stackTrace = new StackTraceElement[var2.length - 3 - ignoredCallCount];
         System.arraycopy(var2, 3 + ignoredCallCount, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   public boolean validateStackTrace(StackTraceElement lastIncludedElement, StackTraceElement firstIgnoredElement) {
      if (this.stackTrace.length != 0 && lastIncludedElement != null) {
         StackTraceElement var3 = this.stackTrace[0];
         if (var3.isNativeMethod() == lastIncludedElement.isNativeMethod()
            && var3.getClassName().equals(lastIncludedElement.getClassName())
            && var3.getFileName().equals(lastIncludedElement.getFileName())
            && var3.getMethodName().equals(lastIncludedElement.getMethodName())) {
            if (firstIgnoredElement != null != this.stackTrace.length > 1) {
               return false;
            } else if (firstIgnoredElement != null && !this.stackTrace[1].equals(firstIgnoredElement)) {
               return false;
            } else {
               this.stackTrace[0] = lastIncludedElement;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTrace(int amount) {
      StackTraceElement[] var2 = new StackTraceElement[this.stackTrace.length - amount];
      System.arraycopy(this.stackTrace, 0, var2, 0, var2.length);
      this.stackTrace = var2;
   }

   public void addDetails(StringBuilder stringBuilder) {
      stringBuilder.append("-- ").append(this.title).append(" --\n");
      stringBuilder.append("Details:");

      for(CashReportCategory.Entry var3 : this.entries) {
         stringBuilder.append("\n\t");
         stringBuilder.append(var3.getKey());
         stringBuilder.append(": ");
         stringBuilder.append(var3.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         stringBuilder.append("\nStacktrace:");

         for(StackTraceElement var5 : this.stackTrace) {
            stringBuilder.append("\n\tat ");
            stringBuilder.append(var5.toString());
         }
      }
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockDetails(CashReportCategory category, BlockPos pos, Block block, int metadata) {
      final int var4 = Block.getRawId(block);
      category.add("Block type", new Callable() {
         public String call() {
            try {
               return String.format("ID #%d (%s // %s)", var4, block.getTranslationKey(), block.getClass().getCanonicalName());
            } catch (Throwable var2) {
               return "ID #" + var4;
            }
         }
      });
      category.add("Block data value", new Callable() {
         public String call() {
            if (metadata < 0) {
               return "Unknown? (Got " + metadata + ")";
            } else {
               String var1 = String.format("%4s", Integer.toBinaryString(metadata)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", metadata, var1);
            }
         }
      });
      category.add("Block location", new Callable() {
         public String call() {
            return CashReportCategory.formatPosition(pos);
         }
      });
   }

   public static void addBlockDetails(CashReportCategory category, BlockPos pos, BlockState state) {
      category.add("Block", new Callable() {
         public String call() {
            return state.toString();
         }
      });
      category.add("Block location", new Callable() {
         public String call() {
            return CashReportCategory.formatPosition(pos);
         }
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String key, Object value) {
         this.key = key;
         if (value == null) {
            this.value = "~~NULL~~";
         } else if (value instanceof Throwable) {
            Throwable var3 = (Throwable)value;
            this.value = "~~ERROR~~ " + var3.getClass().getSimpleName() + ": " + var3.getMessage();
         } else {
            this.value = value.toString();
         }
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
