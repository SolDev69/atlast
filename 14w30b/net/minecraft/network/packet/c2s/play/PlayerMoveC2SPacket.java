package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerMoveC2SPacket implements Packet {
   protected double x;
   protected double y;
   protected double z;
   protected float yaw;
   protected float pitch;
   protected boolean onGround;
   protected boolean hasPos;
   protected boolean hasAngles;

   public PlayerMoveC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PlayerMoveC2SPacket(boolean onGround) {
      this.onGround = onGround;
   }

   public void handle(ServerPlayPacketHandler listener) {
      listener.handlePlayerMove(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.onGround = buffer.readUnsignedByte() != 0;
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.onGround ? 1 : 0);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public boolean getOnGround() {
      return this.onGround;
   }

   public boolean hasPos() {
      return this.hasPos;
   }

   public boolean hasAngles() {
      return this.hasAngles;
   }

   public void setHasPos(boolean hasPos) {
      this.hasPos = hasPos;
   }

   public static class Angles extends PlayerMoveC2SPacket {
      public Angles() {
         this.hasAngles = true;
      }

      @Environment(EnvType.CLIENT)
      public Angles(float yaw, float pitch, boolean onGround) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.onGround = onGround;
         this.hasAngles = true;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         this.yaw = buffer.readFloat();
         this.pitch = buffer.readFloat();
         super.write(buffer);
      }

      @Override
      public void read(PacketByteBuf buffer) {
         buffer.writeFloat(this.yaw);
         buffer.writeFloat(this.pitch);
         super.read(buffer);
      }
   }

   public static class Position extends PlayerMoveC2SPacket {
      public Position() {
         this.hasPos = true;
      }

      @Environment(EnvType.CLIENT)
      public Position(double x, double y, double z, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.onGround = onGround;
         this.hasPos = true;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         this.x = buffer.readDouble();
         this.y = buffer.readDouble();
         this.z = buffer.readDouble();
         super.write(buffer);
      }

      @Override
      public void read(PacketByteBuf buffer) {
         buffer.writeDouble(this.x);
         buffer.writeDouble(this.y);
         buffer.writeDouble(this.z);
         super.read(buffer);
      }
   }

   public static class PositionAndAngles extends PlayerMoveC2SPacket {
      public PositionAndAngles() {
         this.hasPos = true;
         this.hasAngles = true;
      }

      @Environment(EnvType.CLIENT)
      public PositionAndAngles(double x, double y, double z, float yaw, float pitch, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
         this.onGround = onGround;
         this.hasAngles = true;
         this.hasPos = true;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         this.x = buffer.readDouble();
         this.y = buffer.readDouble();
         this.z = buffer.readDouble();
         this.yaw = buffer.readFloat();
         this.pitch = buffer.readFloat();
         super.write(buffer);
      }

      @Override
      public void read(PacketByteBuf buffer) {
         buffer.writeDouble(this.x);
         buffer.writeDouble(this.y);
         buffer.writeDouble(this.z);
         buffer.writeFloat(this.yaw);
         buffer.writeFloat(this.pitch);
         super.read(buffer);
      }
   }
}
