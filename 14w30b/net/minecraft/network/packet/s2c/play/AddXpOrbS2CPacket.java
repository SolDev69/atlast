package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddXpOrbS2CPacket implements Packet {
   private int id;
   private int x;
   private int y;
   private int z;
   private int xp;

   public AddXpOrbS2CPacket() {
   }

   public AddXpOrbS2CPacket(XpOrbEntity entity) {
      this.id = entity.getNetworkId();
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      this.xp = entity.getXp();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
      this.xp = buffer.readShort();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
      buffer.writeShort(this.xp);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddXpOrb(this);
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
   public int getXp() {
      return this.xp;
   }
}
