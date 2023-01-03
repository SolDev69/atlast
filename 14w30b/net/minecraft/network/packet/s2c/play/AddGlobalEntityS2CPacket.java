package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddGlobalEntityS2CPacket implements Packet {
   private int id;
   private int x;
   private int y;
   private int z;
   private int type;

   public AddGlobalEntityS2CPacket() {
   }

   public AddGlobalEntityS2CPacket(Entity entity) {
      this.id = entity.getNetworkId();
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      if (entity instanceof LightningBoltEntity) {
         this.type = 1;
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.type = buffer.readByte();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.type);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddGlobalEntity(this);
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
   public int getType() {
      return this.type;
   }
}
