package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerInputC2SPacket implements Packet {
   private float sidewaysSpeed;
   private float forwardSpeed;
   private boolean jumping;
   private boolean sneaking;

   public PlayerInputC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PlayerInputC2SPacket(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
      this.sidewaysSpeed = sidewaysSpeed;
      this.forwardSpeed = forwardSpeed;
      this.jumping = jumping;
      this.sneaking = sneaking;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.sidewaysSpeed = buffer.readFloat();
      this.forwardSpeed = buffer.readFloat();
      byte var2 = buffer.readByte();
      this.jumping = (var2 & 1) > 0;
      this.sneaking = (var2 & 2) > 0;
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeFloat(this.sidewaysSpeed);
      buffer.writeFloat(this.forwardSpeed);
      byte var2 = 0;
      if (this.jumping) {
         var2 = (byte)(var2 | 1);
      }

      if (this.sneaking) {
         var2 = (byte)(var2 | 2);
      }

      buffer.writeByte(var2);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerInput(this);
   }

   public float getSidewaysSpeed() {
      return this.sidewaysSpeed;
   }

   public float getForwardSpeed() {
      return this.forwardSpeed;
   }

   public boolean getJumping() {
      return this.jumping;
   }

   public boolean getSneaking() {
      return this.sneaking;
   }
}
