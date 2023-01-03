package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityRemoveStatusEffectS2CPacket implements Packet {
   private int id;
   private int effect;

   public EntityRemoveStatusEffectS2CPacket() {
   }

   public EntityRemoveStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
      this.id = entityId;
      this.effect = effect.getId();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.effect = buffer.readUnsignedByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.effect);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityRemoveStatusEffect(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getEffect() {
      return this.effect;
   }
}
