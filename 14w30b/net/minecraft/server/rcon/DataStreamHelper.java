package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class DataStreamHelper {
   private ByteArrayOutputStream byteArrayOutputStream;
   private DataOutputStream dataOutputStream;

   public DataStreamHelper(int size) {
      this.byteArrayOutputStream = new ByteArrayOutputStream(size);
      this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
   }

   public void write(byte[] writable) {
      this.dataOutputStream.write(writable, 0, writable.length);
   }

   public void writeBytes(String name) {
      this.dataOutputStream.writeBytes(name);
      this.dataOutputStream.write(0);
   }

   public void write(int writable) {
      this.dataOutputStream.write(writable);
   }

   public void writeShort(short writable) {
      this.dataOutputStream.writeShort(Short.reverseBytes(writable));
   }

   public byte[] bytes() {
      return this.byteArrayOutputStream.toByteArray();
   }

   public void reset() {
      this.byteArrayOutputStream.reset();
   }
}
