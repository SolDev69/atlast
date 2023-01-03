package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityPickupS2CPacket implements Packet {
   private int id;
   private int collectorId;

   public EntityPickupS2CPacket() {
   }

   public EntityPickupS2CPacket(int id, int collectorId) {
      this.id = id;
      this.collectorId = collectorId;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.collectorId = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeVarInt(this.collectorId);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityPickup(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getCollectorId() {
      return this.collectorId;
   }
}
