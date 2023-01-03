package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlocksUpdateS2CPacket implements Packet {
   private ChunkPos chunkPos;
   private BlocksUpdateS2CPacket.BlockUpdate[] updates;

   public BlocksUpdateS2CPacket() {
   }

   public BlocksUpdateS2CPacket(int blockChangeCount, short[] positions, WorldChunk chunk) {
      this.chunkPos = new ChunkPos(chunk.chunkX, chunk.chunkZ);
      this.updates = new BlocksUpdateS2CPacket.BlockUpdate[blockChangeCount];

      for(int var4 = 0; var4 < this.updates.length; ++var4) {
         this.updates[var4] = new BlocksUpdateS2CPacket.BlockUpdate(positions[var4], chunk);
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.chunkPos = new ChunkPos(buffer.readInt(), buffer.readInt());
      this.updates = new BlocksUpdateS2CPacket.BlockUpdate[buffer.readVarInt()];

      for(int var2 = 0; var2 < this.updates.length; ++var2) {
         this.updates[var2] = new BlocksUpdateS2CPacket.BlockUpdate(buffer.readShort(), (BlockState)Block.STATE_REGISTRY.get(buffer.readVarInt()));
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.chunkPos.x);
      buffer.writeInt(this.chunkPos.z);
      buffer.writeVarInt(this.updates.length);

      for(BlocksUpdateS2CPacket.BlockUpdate var5 : this.updates) {
         buffer.writeShort(var5.getPosition());
         buffer.writeVarInt(Block.STATE_REGISTRY.getId(var5.getBlockState()));
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleBlocksUpdate(this);
   }

   @Environment(EnvType.CLIENT)
   public BlocksUpdateS2CPacket.BlockUpdate[] getUpdates() {
      return this.updates;
   }

   public class BlockUpdate {
      private final short position;
      private final BlockState state;

      public BlockUpdate(short position, BlockState state) {
         this.position = position;
         this.state = state;
      }

      public BlockUpdate(short position, WorldChunk chunk) {
         this.position = position;
         this.state = chunk.getBlockState(this.getBlockPos());
      }

      public BlockPos getBlockPos() {
         return new BlockPos(BlocksUpdateS2CPacket.this.chunkPos.getBlockPos(this.position >> 12 & 15, this.position & 255, this.position >> 8 & 15));
      }

      public short getPosition() {
         return this.position;
      }

      public BlockState getBlockState() {
         return this.state;
      }
   }
}
