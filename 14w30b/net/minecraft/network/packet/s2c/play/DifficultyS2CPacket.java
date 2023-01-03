package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.Difficulty;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DifficultyS2CPacket implements Packet {
   private Difficulty difficulty;
   private boolean hasCustomName;

   public DifficultyS2CPacket() {
   }

   public DifficultyS2CPacket(Difficulty difficulty, boolean locked) {
      this.difficulty = difficulty;
      this.hasCustomName = locked;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleDifficulty(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.difficulty = Difficulty.byIndex(buffer.readUnsignedByte());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.difficulty.getIndex());
   }

   @Environment(EnvType.CLIENT)
   public boolean getLocked() {
      return this.hasCustomName;
   }

   @Environment(EnvType.CLIENT)
   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
