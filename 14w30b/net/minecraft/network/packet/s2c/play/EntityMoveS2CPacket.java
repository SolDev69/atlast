package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityMoveS2CPacket implements Packet {
   protected int id;
   protected byte dx;
   protected byte dy;
   protected byte dz;
   protected byte yaw;
   protected byte pitch;
   protected boolean onGround;
   protected boolean hasAngles;

   public EntityMoveS2CPacket() {
   }

   public EntityMoveS2CPacket(int id) {
      this.id = id;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
   }

   public void handle(ClientPlayPacketHandler listener) {
      listener.handleEntityMove(this);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   @Environment(EnvType.CLIENT)
   public Entity getEntity(World world) {
      return world.getEntity(this.id);
   }

   @Environment(EnvType.CLIENT)
   public byte getDx() {
      return this.dx;
   }

   @Environment(EnvType.CLIENT)
   public byte getDy() {
      return this.dy;
   }

   @Environment(EnvType.CLIENT)
   public byte getDz() {
      return this.dz;
   }

   @Environment(EnvType.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @Environment(EnvType.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasAngles() {
      return this.hasAngles;
   }

   @Environment(EnvType.CLIENT)
   public boolean getOnGround() {
      return this.onGround;
   }

   public static class Angles extends EntityMoveS2CPacket {
      public Angles() {
         this.hasAngles = true;
      }

      public Angles(int id, byte yaw, byte pitch, boolean onGround) {
         super(id);
         this.yaw = yaw;
         this.pitch = pitch;
         this.hasAngles = true;
         this.onGround = onGround;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         super.write(buffer);
         this.yaw = buffer.readByte();
         this.pitch = buffer.readByte();
         this.onGround = buffer.readBoolean();
      }

      @Override
      public void read(PacketByteBuf buffer) {
         super.read(buffer);
         buffer.writeByte(this.yaw);
         buffer.writeByte(this.pitch);
         buffer.writeBoolean(this.onGround);
      }
   }

   public static class Position extends EntityMoveS2CPacket {
      public Position() {
      }

      public Position(int id, byte dx, byte dy, byte dz, boolean onGround) {
         super(id);
         this.dx = dx;
         this.dy = dy;
         this.dz = dz;
         this.onGround = onGround;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         super.write(buffer);
         this.dx = buffer.readByte();
         this.dy = buffer.readByte();
         this.dz = buffer.readByte();
         this.onGround = buffer.readBoolean();
      }

      @Override
      public void read(PacketByteBuf buffer) {
         super.read(buffer);
         buffer.writeByte(this.dx);
         buffer.writeByte(this.dy);
         buffer.writeByte(this.dz);
         buffer.writeBoolean(this.onGround);
      }
   }

   public static class PositionAndAngles extends EntityMoveS2CPacket {
      public PositionAndAngles() {
         this.hasAngles = true;
      }

      public PositionAndAngles(int id, byte dx, byte dy, byte dz, byte yaw, byte pitch, boolean onGround) {
         super(id);
         this.dx = dx;
         this.dy = dy;
         this.dz = dz;
         this.yaw = yaw;
         this.pitch = pitch;
         this.onGround = onGround;
         this.hasAngles = true;
      }

      @Override
      public void write(PacketByteBuf buffer) {
         super.write(buffer);
         this.dx = buffer.readByte();
         this.dy = buffer.readByte();
         this.dz = buffer.readByte();
         this.yaw = buffer.readByte();
         this.pitch = buffer.readByte();
         this.onGround = buffer.readBoolean();
      }

      @Override
      public void read(PacketByteBuf buffer) {
         super.read(buffer);
         buffer.writeByte(this.dx);
         buffer.writeByte(this.dy);
         buffer.writeByte(this.dz);
         buffer.writeByte(this.yaw);
         buffer.writeByte(this.pitch);
         buffer.writeBoolean(this.onGround);
      }
   }
}
