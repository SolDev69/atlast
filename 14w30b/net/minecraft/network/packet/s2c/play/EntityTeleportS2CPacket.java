package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityTeleportS2CPacket implements Packet {
   private int id;
   private int x;
   private int y;
   private int z;
   private byte yaw;
   private byte pitch;
   private boolean onGround;

   public EntityTeleportS2CPacket() {
   }

   public EntityTeleportS2CPacket(Entity entity) {
      this.id = entity.getNetworkId();
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
      this.onGround = entity.onGround;
   }

   public EntityTeleportS2CPacket(int id, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.onGround = onGround;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
      this.yaw = buffer.readByte();
      this.pitch = buffer.readByte();
      this.onGround = buffer.readBoolean();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
      buffer.writeByte(this.yaw);
      buffer.writeByte(this.pitch);
      buffer.writeBoolean(this.onGround);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityTeleport(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getX() {
      return this.x;
   }

   @Environment(EnvType.CLIENT)
   public int getY() {
      return this.y;
   }

   @Environment(EnvType.CLIENT)
   public int getZ() {
      return this.z;
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
   public boolean getOnGround() {
      return this.onGround;
   }
}
