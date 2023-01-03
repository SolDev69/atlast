package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerRespawnS2CPacket implements Packet {
   private int worldHeight;
   private Difficulty difficulty;
   private WorldSettings.GameMode gameMode;
   private WorldGeneratorType generatorType;

   public PlayerRespawnS2CPacket() {
   }

   public PlayerRespawnS2CPacket(int dimensionId, Difficulty difficulty, WorldGeneratorType generatorType, WorldSettings.GameMode gameMode) {
      this.worldHeight = dimensionId;
      this.difficulty = difficulty;
      this.gameMode = gameMode;
      this.generatorType = generatorType;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerRespawn(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.worldHeight = buffer.readInt();
      this.difficulty = Difficulty.byIndex(buffer.readUnsignedByte());
      this.gameMode = WorldSettings.GameMode.byIndex(buffer.readUnsignedByte());
      this.generatorType = WorldGeneratorType.byId(buffer.readString(16));
      if (this.generatorType == null) {
         this.generatorType = WorldGeneratorType.DEFAULT;
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.worldHeight);
      buffer.writeByte(this.difficulty.getIndex());
      buffer.writeByte(this.gameMode.getIndex());
      buffer.writeString(this.generatorType.getId());
   }

   @Environment(EnvType.CLIENT)
   public int getDimensionId() {
      return this.worldHeight;
   }

   @Environment(EnvType.CLIENT)
   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   @Environment(EnvType.CLIENT)
   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   @Environment(EnvType.CLIENT)
   public WorldGeneratorType getGeneratorType() {
      return this.generatorType;
   }
}
