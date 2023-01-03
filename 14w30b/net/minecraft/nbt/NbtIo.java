package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class NbtIo {
   public static NbtCompound read(InputStream is) {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));

      NbtCompound var2;
      try {
         var2 = read(var1, NbtReadLimiter.UNLIMITED);
      } finally {
         var1.close();
      }

      return var2;
   }

   public static void write(NbtCompound nbt, OutputStream os) {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(os)));

      try {
         write(nbt, (DataOutput)var2);
      } finally {
         var2.close();
      }
   }

   @Environment(EnvType.CLIENT)
   public static void writeSafe(NbtCompound nbt, File file) {
      File var2 = new File(file.getAbsolutePath() + "_tmp");
      if (var2.exists()) {
         var2.delete();
      }

      write(nbt, var2);
      if (file.exists()) {
         file.delete();
      }

      if (file.exists()) {
         throw new IOException("Failed to delete " + file);
      } else {
         var2.renameTo(file);
      }
   }

   @Environment(EnvType.CLIENT)
   public static void write(NbtCompound nbt, File file) {
      DataOutputStream var2 = new DataOutputStream(new FileOutputStream(file));

      try {
         write(nbt, (DataOutput)var2);
      } finally {
         var2.close();
      }
   }

   @Environment(EnvType.CLIENT)
   public static NbtCompound read(File file) {
      if (!file.exists()) {
         return null;
      } else {
         DataInputStream var1 = new DataInputStream(new FileInputStream(file));

         NbtCompound var2;
         try {
            var2 = read(var1, NbtReadLimiter.UNLIMITED);
         } finally {
            var1.close();
         }

         return var2;
      }
   }

   public static NbtCompound read(DataInputStream is) {
      return read(is, NbtReadLimiter.UNLIMITED);
   }

   public static NbtCompound read(DataInput input, NbtReadLimiter limiter) {
      NbtElement var2 = read(input, 0, limiter);
      if (var2 instanceof NbtCompound) {
         return (NbtCompound)var2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(NbtCompound nbt, DataOutput output) {
      write((NbtElement)nbt, output);
   }

   private static void write(NbtElement nbt, DataOutput output) {
      output.writeByte(nbt.getType());
      if (nbt.getType() != 0) {
         output.writeUTF("");
         nbt.write(output);
      }
   }

   private static NbtElement read(DataInput input, int depth, NbtReadLimiter limiter) {
      byte var3 = input.readByte();
      if (var3 == 0) {
         return new NbtEnd();
      } else {
         input.readUTF();
         NbtElement var4 = NbtElement.create(var3);

         try {
            var4.read(input, depth, limiter);
            return var4;
         } catch (IOException var8) {
            CrashReport var6 = CrashReport.of(var8, "Loading NBT data");
            CashReportCategory var7 = var6.addCategory("NBT Tag");
            var7.add("Tag name", "[UNNAMED TAG]");
            var7.add("Tag type", var3);
            throw new CrashException(var6);
         }
      }
   }
}
