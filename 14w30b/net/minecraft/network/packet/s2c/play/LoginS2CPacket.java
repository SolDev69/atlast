package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LoginS2CPacket implements Packet {
   private int id;
   private boolean hardcore;
   private WorldSettings.GameMode gameMode;
   private int dimensionId;
   private Difficulty difficulty;
   private int maxPlayerCount;
   private WorldGeneratorType generatorType;
   private boolean reducedDebugInfo;

   public LoginS2CPacket() {
   }

   public LoginS2CPacket(
      int entityId,
      WorldSettings.GameMode gameMode,
      boolean hardcore,
      int dimensionId,
      Difficulty difficulty,
      int maxPlayerCount,
      WorldGeneratorType generatorType,
      boolean reducedDebugInfo
   ) {
      this.id = entityId;
      this.dimensionId = dimensionId;
      this.difficulty = difficulty;
      this.gameMode = gameMode;
      this.maxPlayerCount = maxPlayerCount;
      this.hardcore = hardcore;
      this.generatorType = generatorType;
      this.reducedDebugInfo = reducedDebugInfo;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readInt();
      int var2 = buffer.readUnsignedByte();
      this.hardcore = (var2 & 8) == 8;
      var2 &= -9;
      this.gameMode = WorldSettings.GameMode.byIndex(var2);
      this.dimensionId = buffer.readByte();
      this.difficulty = Difficulty.byIndex(buffer.readUnsignedByte());
      this.maxPlayerCount = buffer.readUnsignedByte();
      this.generatorType = WorldGeneratorType.byId(buffer.readString(16));
      if (this.generatorType == null) {
         this.generatorType = WorldGeneratorType.DEFAULT;
      }

      this.reducedDebugInfo = buffer.readBoolean();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.id);
      int var2 = this.gameMode.getIndex();
      if (this.hardcore) {
         var2 |= 8;
      }

      buffer.writeByte(var2);
      buffer.writeByte(this.dimensionId);
      buffer.writeByte(this.difficulty.getIndex());
      buffer.writeByte(this.maxPlayerCount);
      buffer.writeString(this.generatorType.getId());
      buffer.writeBoolean(this.reducedDebugInfo);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleLogin(this);
   }

   @Environment(EnvType.CLIENT)
   public int getEntityId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public boolean getHardcore() {
      return this.hardcore;
   }

   @Environment(EnvType.CLIENT)
   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   @Environment(EnvType.CLIENT)
   public int getDimensionId() {
      return this.dimensionId;
   }

   @Environment(EnvType.CLIENT)
   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   @Environment(EnvType.CLIENT)
   public int getMaxPlayerCount() {
      return this.maxPlayerCount;
   }

   @Environment(EnvType.CLIENT)
   public WorldGeneratorType getGeneratorType() {
      return this.generatorType;
   }

   @Environment(EnvType.CLIENT)
   public boolean getReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}
