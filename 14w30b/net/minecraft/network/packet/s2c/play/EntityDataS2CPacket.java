package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.DataTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityDataS2CPacket implements Packet {
   private int id;
   private List dataEntries;

   public EntityDataS2CPacket() {
   }

   public EntityDataS2CPacket(int id, DataTracker tracker, boolean forceUpdateAll) {
      this.id = id;
      if (forceUpdateAll) {
         this.dataEntries = tracker.collectEntries();
      } else {
         this.dataEntries = tracker.collectModifiedEntries();
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.dataEntries = DataTracker.read(buffer);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      DataTracker.write(this.dataEntries, buffer);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityData(this);
   }

   @Environment(EnvType.CLIENT)
   public List getDataEntries() {
      return this.dataEntries;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }
}
