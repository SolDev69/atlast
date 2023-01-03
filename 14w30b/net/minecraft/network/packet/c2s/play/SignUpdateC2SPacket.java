package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SignUpdateC2SPacket implements Packet {
   private BlockPos pos;
   private Text[] lines;

   public SignUpdateC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public SignUpdateC2SPacket(BlockPos pos, Text[] lines) {
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

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Text[] getLines() {
      return this.lines;
   }
}
