package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerHealthS2CPacket implements Packet {
   private float health;
   private int hunger;
   private float saturation;

   public PlayerHealthS2CPacket() {
   }

   public PlayerHealthS2CPacket(float health, int hunger, float saturation) {
      this.health = health;
      this.hunger = hunger;
      this.saturation = saturation;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.health = buffer.readFloat();
      this.hunger = buffer.readVarInt();
      this.saturation = buffer.readFloat();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeFloat(this.health);
      buffer.writeVarInt(this.hunger);
      buffer.writeFloat(this.saturation);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerHealth(this);
   }

   @Environment(EnvType.CLIENT)
   public float getHealth() {
      return this.health;
   }

   @Environment(EnvType.CLIENT)
   public int getHunger() {
      return this.hunger;
   }

   @Environment(EnvType.CLIENT)
   public float getSaturation() {
      return this.saturation;
   }
}
