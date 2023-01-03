package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class RemoveEntitiesS2CPacket implements Packet {
   private int[] ids;

   public RemoveEntitiesS2CPacket() {
   }

   public RemoveEntitiesS2CPacket(int... ids) {
      this.ids = ids;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.ids = new int[buffer.readVarInt()];

      for(int var2 = 0; var2 < this.ids.length; ++var2) {
         this.ids[var2] = buffer.readVarInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.ids.length);

      for(int var2 = 0; var2 < this.ids.length; ++var2) {
         buffer.writeVarInt(this.ids[var2]);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleRemoveEntities(this);
   }

   @Environment(EnvType.CLIENT)
   public int[] getIds() {
      return this.ids;
   }
}
