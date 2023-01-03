package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ParticleS2CPacket implements Packet {
   private ParticleType type;
   private float x;
   private float y;
   private float z;
   private float velocityX;
   private float velocityY;
   private float velocityZ;
   private float velocityScale;
   private int count;
   private boolean ignoreDistance;
   private int[] parameters;

   public ParticleS2CPacket() {
   }

   public ParticleS2CPacket(
      ParticleType type,
      boolean ignoreDistance,
      float x,
      float y,
      float z,
      float velocityX,
      float velocityY,
      float velocityZ,
      float velocityScale,
      int count,
      int... parameters
   ) {
      this.type = type;
      this.ignoreDistance = ignoreDistance;
      this.x = x;
      this.y = y;
      this.z = z;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      this.velocityScale = velocityScale;
      this.count = count;
      this.parameters = parameters;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.type = ParticleType.byId(buffer.readInt());
      if (this.type == null) {
         this.type = ParticleType.BARRIER;
      }

      this.ignoreDistance = buffer.readBoolean();
      this.x = buffer.readFloat();
      this.y = buffer.readFloat();
      this.z = buffer.readFloat();
      this.velocityX = buffer.readFloat();
      this.velocityY = buffer.readFloat();
      this.velocityZ = buffer.readFloat();
      this.velocityScale = buffer.readFloat();
      this.count = buffer.readInt();
      int var2 = this.type.getIdForCommands();
      this.parameters = new int[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.parameters[var3] = buffer.readVarInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.type.getId());
      buffer.writeBoolean(this.ignoreDistance);
      buffer.writeFloat(this.x);
      buffer.writeFloat(this.y);
      buffer.writeFloat(this.z);
      buffer.writeFloat(this.velocityX);
      buffer.writeFloat(this.velocityY);
      buffer.writeFloat(this.velocityZ);
      buffer.writeFloat(this.velocityScale);
      buffer.writeInt(this.count);
      int var2 = this.type.getIdForCommands();

      for(int var3 = 0; var3 < var2; ++var3) {
         buffer.writeVarInt(this.parameters[var3]);
      }
   }

   @Environment(EnvType.CLIENT)
   public ParticleType getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public boolean getIgnoreDistance() {
      return this.ignoreDistance;
   }

   @Environment(EnvType.CLIENT)
   public double getX() {
      return (double)this.x;
   }

   @Environment(EnvType.CLIENT)
   public double getY() {
      return (double)this.y;
   }

   @Environment(EnvType.CLIENT)
   public double getZ() {
      return (double)this.z;
   }

   @Environment(EnvType.CLIENT)
   public float getVelocityX() {
      return this.velocityX;
   }

   @Environment(EnvType.CLIENT)
   public float getVelocityY() {
      return this.velocityY;
   }

   @Environment(EnvType.CLIENT)
   public float getVelocityZ() {
      return this.velocityZ;
   }

   @Environment(EnvType.CLIENT)
   public float getVelocityScale() {
      return this.velocityScale;
   }

   @Environment(EnvType.CLIENT)
   public int getCount() {
      return this.count;
   }

   @Environment(EnvType.CLIENT)
   public int[] getParameters() {
      return this.parameters;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleParticle(this);
   }
}
