package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtReadLimiter;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class PacketByteBuf extends ByteBuf {
   private final ByteBuf delegate;

   public PacketByteBuf(ByteBuf delegate) {
      this.delegate = delegate;
   }

   public static int getVarIntSizeBytes(int size) {
      if ((size & -128) == 0) {
         return 1;
      } else if ((size & -16384) == 0) {
         return 2;
      } else if ((size & -2097152) == 0) {
         return 3;
      } else {
         return (size & -268435456) == 0 ? 4 : 5;
      }
   }

   public void writeByteArray(byte[] bytes) {
      this.writeVarInt(bytes.length);
      this.writeBytes(bytes);
   }

   public byte[] readByteArray() {
      byte[] var1 = new byte[this.readVarInt()];
      this.readBytes(var1);
      return var1;
   }

   public byte[] getBytes() {
      int var1 = this.writerIndex();
      byte[] var2 = new byte[var1];
      this.getBytes(0, var2);
      return var2;
   }

   public BlockPos readBlockPos() {
      return BlockPos.fromLong(this.readLong());
   }

   public void writeBlockPos(BlockPos pos) {
      this.writeLong(pos.toLong());
   }

   public Text readText() {
      return Text.Serializer.fromJson(this.readString(32767));
   }

   public void writeText(Text text) {
      this.writeString(Text.Serializer.toJson(text));
   }

   public Enum readEnum(Class type) {
      return ((Enum[])type.getEnumConstants())[this.readVarInt()];
   }

   public void writeEnum(Enum value) {
      this.writeVarInt(value.ordinal());
   }

   public int readVarInt() {
      int var1 = 0;
      int var2 = 0;

      byte var3;
      do {
         var3 = this.readByte();
         var1 |= (var3 & 127) << var2++ * 7;
         if (var2 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((var3 & 128) == 128);

      return var1;
   }

   public void writeUuid(UUID uuid) {
      this.writeLong(uuid.getMostSignificantBits());
      this.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID readUuid() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void writeVarInt(int i) {
      while((i & -128) != 0) {
         this.writeByte(i & 127 | 128);
         i >>>= 7;
      }

      this.writeByte(i);
   }

   public void writeNbtCompound(NbtCompound nbt) {
      if (nbt == null) {
         this.writeByte(0);
      } else {
         NbtIo.write(nbt, new ByteBufOutputStream(this));
      }
   }

   public NbtCompound readNbtCompound() {
      int var1 = this.readerIndex();
      byte var2 = this.readByte();
      if (var2 == 0) {
         return null;
      } else {
         this.readerIndex(var1);
         return NbtIo.read(new ByteBufInputStream(this), new NbtReadLimiter(2097152L));
      }
   }

   public void writeItemStack(ItemStack stack) {
      if (stack == null) {
         this.writeShort(-1);
      } else {
         this.writeShort(Item.getRawId(stack.getItem()));
         this.writeByte(stack.size);
         this.writeShort(stack.getMetadata());
         NbtCompound var2 = null;
         if (stack.getItem().isDamageable() || stack.getItem().shouldSyncNbt()) {
            var2 = stack.getNbt();
         }

         this.writeNbtCompound(var2);
      }
   }

   public ItemStack readItemStack() {
      ItemStack var1 = null;
      short var2 = this.readShort();
      if (var2 >= 0) {
         byte var3 = this.readByte();
         short var4 = this.readShort();
         var1 = new ItemStack(Item.byRawId(var2), var3, var4);
         var1.setNbt(this.readNbtCompound());
      }

      return var1;
   }

   public String readString(int maxLength) {
      int var2 = this.readVarInt();
      if (var2 > maxLength * 4) {
         throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + var2 + " > " + maxLength * 4 + ")");
      } else if (var2 < 0) {
         throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String var3 = new String(this.readBytes(var2).array(), Charsets.UTF_8);
         if (var3.length() > maxLength) {
            throw new IOException("The received string length is longer than maximum allowed (" + var2 + " > " + maxLength + ")");
         } else {
            return var3;
         }
      }
   }

   public void writeString(String string) {
      byte[] var2 = string.getBytes(Charsets.UTF_8);
      if (var2.length > 32767) {
         throw new IOException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
      } else {
         this.writeVarInt(var2.length);
         this.writeBytes(var2);
      }
   }

   public int capacity() {
      return this.delegate.capacity();
   }

   public ByteBuf capacity(int newCapacity) {
      return this.delegate.capacity(newCapacity);
   }

   public int maxCapacity() {
      return this.delegate.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.delegate.alloc();
   }

   public ByteOrder order() {
      return this.delegate.order();
   }

   public ByteBuf order(ByteOrder endianness) {
      return this.delegate.order(endianness);
   }

   public ByteBuf unwrap() {
      return this.delegate.unwrap();
   }

   public boolean isDirect() {
      return this.delegate.isDirect();
   }

   public int readerIndex() {
      return this.delegate.readerIndex();
   }

   public ByteBuf readerIndex(int readerIndex) {
      return this.delegate.readerIndex(readerIndex);
   }

   public int writerIndex() {
      return this.delegate.writerIndex();
   }

   public ByteBuf writerIndex(int writerIndex) {
      return this.delegate.writerIndex(writerIndex);
   }

   public ByteBuf setIndex(int readerIndex, int writerIndex) {
      return this.delegate.setIndex(readerIndex, writerIndex);
   }

   public int readableBytes() {
      return this.delegate.readableBytes();
   }

   public int writableBytes() {
      return this.delegate.writableBytes();
   }

   public int maxWritableBytes() {
      return this.delegate.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.delegate.isReadable();
   }

   public boolean isReadable(int size) {
      return this.delegate.isReadable(size);
   }

   public boolean isWritable() {
      return this.delegate.isWritable();
   }

   public boolean isWritable(int size) {
      return this.delegate.isWritable(size);
   }

   public ByteBuf clear() {
      return this.delegate.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.delegate.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.delegate.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.delegate.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.delegate.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.delegate.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.delegate.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int minWritableBytes) {
      return this.delegate.ensureWritable(minWritableBytes);
   }

   public int ensureWritable(int minWritableBytes, boolean force) {
      return this.delegate.ensureWritable(minWritableBytes, force);
   }

   public boolean getBoolean(int index) {
      return this.delegate.getBoolean(index);
   }

   public byte getByte(int index) {
      return this.delegate.getByte(index);
   }

   public short getUnsignedByte(int index) {
      return this.delegate.getUnsignedByte(index);
   }

   public short getShort(int index) {
      return this.delegate.getShort(index);
   }

   public int getUnsignedShort(int index) {
      return this.delegate.getUnsignedShort(index);
   }

   public int getMedium(int index) {
      return this.delegate.getMedium(index);
   }

   public int getUnsignedMedium(int index) {
      return this.delegate.getUnsignedMedium(index);
   }

   public int getInt(int index) {
      return this.delegate.getInt(index);
   }

   public long getUnsignedInt(int index) {
      return this.delegate.getUnsignedInt(index);
   }

   public long getLong(int index) {
      return this.delegate.getLong(index);
   }

   public char getChar(int index) {
      return this.delegate.getChar(index);
   }

   public float getFloat(int index) {
      return this.delegate.getFloat(index);
   }

   public double getDouble(int index) {
      return this.delegate.getDouble(index);
   }

   public ByteBuf getBytes(int index, ByteBuf dst) {
      return this.delegate.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
      return this.delegate.getBytes(index, dst, length);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      return this.delegate.getBytes(index, dst, dstIndex, length);
   }

   public ByteBuf getBytes(int index, byte[] dst) {
      return this.delegate.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      return this.delegate.getBytes(index, dst, dstIndex, length);
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      return this.delegate.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) {
      return this.delegate.getBytes(index, out, length);
   }

   public int getBytes(int index, GatheringByteChannel out, int length) {
      return this.delegate.getBytes(index, out, length);
   }

   public ByteBuf setBoolean(int index, boolean value) {
      return this.delegate.setBoolean(index, value);
   }

   public ByteBuf setByte(int index, int value) {
      return this.delegate.setByte(index, value);
   }

   public ByteBuf setShort(int index, int value) {
      return this.delegate.setShort(index, value);
   }

   public ByteBuf setMedium(int index, int value) {
      return this.delegate.setMedium(index, value);
   }

   public ByteBuf setInt(int index, int value) {
      return this.delegate.setInt(index, value);
   }

   public ByteBuf setLong(int index, long value) {
      return this.delegate.setLong(index, value);
   }

   public ByteBuf setChar(int index, int value) {
      return this.delegate.setChar(index, value);
   }

   public ByteBuf setFloat(int index, float value) {
      return this.delegate.setFloat(index, value);
   }

   public ByteBuf setDouble(int index, double value) {
      return this.delegate.setDouble(index, value);
   }

   public ByteBuf setBytes(int index, ByteBuf src) {
      return this.delegate.setBytes(index, src);
   }

   public ByteBuf setBytes(int index, ByteBuf src, int length) {
      return this.delegate.setBytes(index, src, length);
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      return this.delegate.setBytes(index, src, srcIndex, length);
   }

   public ByteBuf setBytes(int index, byte[] src) {
      return this.delegate.setBytes(index, src);
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      return this.delegate.setBytes(index, src, srcIndex, length);
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      return this.delegate.setBytes(index, src);
   }

   public int setBytes(int index, InputStream in, int length) {
      return this.delegate.setBytes(index, in, length);
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) {
      return this.delegate.setBytes(index, in, length);
   }

   public ByteBuf setZero(int index, int length) {
      return this.delegate.setZero(index, length);
   }

   public boolean readBoolean() {
      return this.delegate.readBoolean();
   }

   public byte readByte() {
      return this.delegate.readByte();
   }

   public short readUnsignedByte() {
      return this.delegate.readUnsignedByte();
   }

   public short readShort() {
      return this.delegate.readShort();
   }

   public int readUnsignedShort() {
      return this.delegate.readUnsignedShort();
   }

   public int readMedium() {
      return this.delegate.readMedium();
   }

   public int readUnsignedMedium() {
      return this.delegate.readUnsignedMedium();
   }

   public int readInt() {
      return this.delegate.readInt();
   }

   public long readUnsignedInt() {
      return this.delegate.readUnsignedInt();
   }

   public long readLong() {
      return this.delegate.readLong();
   }

   public char readChar() {
      return this.delegate.readChar();
   }

   public float readFloat() {
      return this.delegate.readFloat();
   }

   public double readDouble() {
      return this.delegate.readDouble();
   }

   public ByteBuf readBytes(int length) {
      return this.delegate.readBytes(length);
   }

   public ByteBuf readSlice(int length) {
      return this.delegate.readSlice(length);
   }

   public ByteBuf readBytes(ByteBuf dst) {
      return this.delegate.readBytes(dst);
   }

   public ByteBuf readBytes(ByteBuf dst, int length) {
      return this.delegate.readBytes(dst, length);
   }

   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      return this.delegate.readBytes(dst, dstIndex, length);
   }

   public ByteBuf readBytes(byte[] dst) {
      return this.delegate.readBytes(dst);
   }

   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      return this.delegate.readBytes(dst, dstIndex, length);
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      return this.delegate.readBytes(dst);
   }

   public ByteBuf readBytes(OutputStream out, int length) {
      return this.delegate.readBytes(out, length);
   }

   public int readBytes(GatheringByteChannel out, int length) {
      return this.delegate.readBytes(out, length);
   }

   public ByteBuf skipBytes(int length) {
      return this.delegate.skipBytes(length);
   }

   public ByteBuf writeBoolean(boolean value) {
      return this.delegate.writeBoolean(value);
   }

   public ByteBuf writeByte(int value) {
      return this.delegate.writeByte(value);
   }

   public ByteBuf writeShort(int value) {
      return this.delegate.writeShort(value);
   }

   public ByteBuf writeMedium(int value) {
      return this.delegate.writeMedium(value);
   }

   public ByteBuf writeInt(int value) {
      return this.delegate.writeInt(value);
   }

   public ByteBuf writeLong(long value) {
      return this.delegate.writeLong(value);
   }

   public ByteBuf writeChar(int value) {
      return this.delegate.writeChar(value);
   }

   public ByteBuf writeFloat(float value) {
      return this.delegate.writeFloat(value);
   }

   public ByteBuf writeDouble(double value) {
      return this.delegate.writeDouble(value);
   }

   public ByteBuf writeBytes(ByteBuf src) {
      return this.delegate.writeBytes(src);
   }

   public ByteBuf writeBytes(ByteBuf src, int length) {
      return this.delegate.writeBytes(src, length);
   }

   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      return this.delegate.writeBytes(src, srcIndex, length);
   }

   public ByteBuf writeBytes(byte[] src) {
      return this.delegate.writeBytes(src);
   }

   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      return this.delegate.writeBytes(src, srcIndex, length);
   }

   public ByteBuf writeBytes(ByteBuffer src) {
      return this.delegate.writeBytes(src);
   }

   public int writeBytes(InputStream in, int length) {
      return this.delegate.writeBytes(in, length);
   }

   public int writeBytes(ScatteringByteChannel in, int length) {
      return this.delegate.writeBytes(in, length);
   }

   public ByteBuf writeZero(int length) {
      return this.delegate.writeZero(length);
   }

   public int indexOf(int fromIndex, int toIndex, byte value) {
      return this.delegate.indexOf(fromIndex, toIndex, value);
   }

   public int bytesBefore(byte value) {
      return this.delegate.bytesBefore(value);
   }

   public int bytesBefore(int length, byte value) {
      return this.delegate.bytesBefore(length, value);
   }

   public int bytesBefore(int index, int length, byte value) {
      return this.delegate.bytesBefore(index, length, value);
   }

   public int forEachByte(ByteBufProcessor processor) {
      return this.delegate.forEachByte(processor);
   }

   public int forEachByte(int index, int length, ByteBufProcessor processor) {
      return this.delegate.forEachByte(index, length, processor);
   }

   public int forEachByteDesc(ByteBufProcessor processor) {
      return this.delegate.forEachByteDesc(processor);
   }

   public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
      return this.delegate.forEachByteDesc(index, length, processor);
   }

   public ByteBuf copy() {
      return this.delegate.copy();
   }

   public ByteBuf copy(int index, int length) {
      return this.delegate.copy(index, length);
   }

   public ByteBuf slice() {
      return this.delegate.slice();
   }

   public ByteBuf slice(int index, int length) {
      return this.delegate.slice(index, length);
   }

   public ByteBuf duplicate() {
      return this.delegate.duplicate();
   }

   public int nioBufferCount() {
      return this.delegate.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.delegate.nioBuffer();
   }

   public ByteBuffer nioBuffer(int index, int length) {
      return this.delegate.nioBuffer(index, length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      return this.delegate.internalNioBuffer(index, length);
   }

   public ByteBuffer[] nioBuffers() {
      return this.delegate.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return this.delegate.nioBuffers(index, length);
   }

   public boolean hasArray() {
      return this.delegate.hasArray();
   }

   public byte[] array() {
      return this.delegate.array();
   }

   public int arrayOffset() {
      return this.delegate.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.delegate.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.delegate.memoryAddress();
   }

   public String toString(Charset charset) {
      return this.delegate.toString(charset);
   }

   public String toString(int index, int length, Charset charset) {
      return this.delegate.toString(index, length, charset);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public boolean equals(Object obj) {
      return this.delegate.equals(obj);
   }

   public int compareTo(ByteBuf byteBuf) {
      return this.delegate.compareTo(byteBuf);
   }

   public String toString() {
      return this.delegate.toString();
   }

   public ByteBuf retain(int i) {
      return this.delegate.retain(i);
   }

   public ByteBuf retain() {
      return this.delegate.retain();
   }

   public int refCnt() {
      return this.delegate.refCnt();
   }

   public boolean release() {
      return this.delegate.release();
   }

   public boolean release(int decrement) {
      return this.delegate.release(decrement);
   }
}
