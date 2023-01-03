package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CameraS2CPacket implements Packet {
   public int id;

   public CameraS2CPacket() {
   }

   public CameraS2CPacket(Entity camera) {
      this.id = camera.getNetworkId();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleCamera(this);
   }

   @Environment(EnvType.CLIENT)
   public Entity getCamera(World world) {
      return world.getEntity(this.id);
   }
}
