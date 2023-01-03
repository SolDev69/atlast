package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SignBlockEntityUpdateS2CPacket implements Packet {
   private World world;
   private BlockPos pos;
   private Text[] lines;

   public SignBlockEntityUpdateS2CPacket() {
   }

   public SignBlockEntityUpdateS2CPacket(World world, BlockPos pos, Text[] lines) {
      this.world = world;
      this.pos = pos;
      this.lines = new Text[]{lines[0], lines[1], lines[2], lines[3]};
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.lines = new Text[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.lines[var2] = buffer.readText();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);

      for(int var2 = 0; var2 < 4; ++var2) {
         buffer.writeText(this.lines[var2]);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleSignBlockEntityUpdate(this);
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @Environment(EnvType.CLIENT)
   public Text[] getLines() {
      return this.lines;
   }
}
