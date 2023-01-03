package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class StatisticsS2CPacket implements Packet {
   private Map stats;

   public StatisticsS2CPacket() {
   }

   public StatisticsS2CPacket(Map stats) {
      this.stats = stats;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleStatistics(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      int var2 = buffer.readVarInt();
      this.stats = Maps.newHashMap();

      for(int var3 = 0; var3 < var2; ++var3) {
         Stat var4 = Stats.get(buffer.readString(32767));
         int var5 = buffer.readVarInt();
         if (var4 != null) {
            this.stats.put(var4, var5);
         }
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.stats.size());

      for(Entry var3 : this.stats.entrySet()) {
         buffer.writeString(((Stat)var3.getKey()).id);
         buffer.writeVarInt(var3.getValue());
      }
   }

   @Environment(EnvType.CLIENT)
   public Map getStats() {
      return this.stats;
   }
}
