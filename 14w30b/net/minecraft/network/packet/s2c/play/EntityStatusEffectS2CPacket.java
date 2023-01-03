package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityStatusEffectS2CPacket implements Packet {
   private int id;
   private byte effect;
   private byte amplifier;
   private int duration;
   private byte flags;

   public EntityStatusEffectS2CPacket() {
   }

   public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
      this.id = entityId;
      this.effect = (byte)(effect.getId() & 0xFF);
      this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
      if (effect.getDuration() > 32767) {
         this.duration = 32767;
      } else {
         this.duration = effect.getDuration();
      }

      this.flags = (byte)(effect.hasParticles() ? 1 : 0);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.effect = buffer.readByte();
      this.amplifier = buffer.readByte();
      this.duration = buffer.readVarInt();
      this.flags = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.effect);
      buffer.writeByte(this.amplifier);
      buffer.writeVarInt(this.duration);
      buffer.writeByte(this.flags);
   }

   @Environment(EnvType.CLIENT)
   public boolean isPermanent() {
      return this.duration == 32767;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityStatusEffect(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public byte getEffect() {
      return this.effect;
   }

   @Environment(EnvType.CLIENT)
   public byte getAmplifier() {
      return this.amplifier;
   }

   @Environment(EnvType.CLIENT)
   public int getDuration() {
      return this.duration;
   }

   @Environment(EnvType.CLIENT)
   public boolean getParticles() {
      return this.flags != 0;
   }
}
