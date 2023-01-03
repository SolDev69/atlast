package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.border.WorldBorder;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldBorderS2CPacket implements Packet {
   private WorldBorderS2CPacket.Type type;
   private int maxSize;
   private double centerX;
   private double centeZ;
   private double sizeLerpTarget;
   private double lerpSize;
   private int f_92ehixagx;
   private int warningTime;
   private int warningBlocks;

   public WorldBorderS2CPacket() {
   }

   public WorldBorderS2CPacket(WorldBorder worldBorder, WorldBorderS2CPacket.Type type) {
      this.type = type;
      this.centerX = worldBorder.getCenterX();
      this.centeZ = worldBorder.getCenterZ();
      this.lerpSize = worldBorder.getLerpSize();
      this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
      this.f_92ehixagx = worldBorder.getLerpTime();
      this.maxSize = worldBorder.getMaxSize();
      this.warningBlocks = worldBorder.getWarningBlocks();
      this.warningTime = worldBorder.getWarningTime();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.type = (WorldBorderS2CPacket.Type)buffer.readEnum(WorldBorderS2CPacket.Type.class);
      switch(this.type) {
         case SET_SIZE:
            this.sizeLerpTarget = buffer.readDouble();
            break;
         case LERP_SIZE:
            this.lerpSize = buffer.readDouble();
            this.sizeLerpTarget = buffer.readDouble();
            this.f_92ehixagx = buffer.readVarInt();
            break;
         case SET_CENTER:
            this.centerX = buffer.readDouble();
            this.centeZ = buffer.readDouble();
            break;
         case SET_WARNING_BLOCKS:
            this.warningBlocks = buffer.readVarInt();
            break;
         case SET_WARNING_TIME:
            this.warningTime = buffer.readVarInt();
            break;
         case INITIALIZE:
            this.centerX = buffer.readDouble();
            this.centeZ = buffer.readDouble();
            this.lerpSize = buffer.readDouble();
            this.sizeLerpTarget = buffer.readDouble();
            this.f_92ehixagx = buffer.readVarInt();
            this.maxSize = buffer.readVarInt();
            this.warningBlocks = buffer.readVarInt();
            this.warningTime = buffer.readVarInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeEnum(this.type);
      switch(this.type) {
         case SET_SIZE:
            buffer.writeDouble(this.sizeLerpTarget);
            break;
         case LERP_SIZE:
            buffer.writeDouble(this.lerpSize);
            buffer.writeDouble(this.sizeLerpTarget);
            buffer.writeVarInt(this.f_92ehixagx);
            break;
         case SET_CENTER:
            buffer.writeDouble(this.centerX);
            buffer.writeDouble(this.centeZ);
            break;
         case SET_WARNING_BLOCKS:
            buffer.writeVarInt(this.warningBlocks);
            break;
         case SET_WARNING_TIME:
            buffer.writeVarInt(this.warningTime);
            break;
         case INITIALIZE:
            buffer.writeDouble(this.centerX);
            buffer.writeDouble(this.centeZ);
            buffer.writeDouble(this.lerpSize);
            buffer.writeDouble(this.sizeLerpTarget);
            buffer.writeVarInt(this.f_92ehixagx);
            buffer.writeVarInt(this.maxSize);
            buffer.writeVarInt(this.warningBlocks);
            buffer.writeVarInt(this.warningTime);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleWorldBorder(this);
   }

   @Environment(EnvType.CLIENT)
   public void apply(WorldBorder worldBorder) {
      switch(this.type) {
         case SET_SIZE:
            worldBorder.setSize(this.sizeLerpTarget);
            break;
         case LERP_SIZE:
            worldBorder.setSize(this.lerpSize, this.sizeLerpTarget, this.f_92ehixagx);
            break;
         case SET_CENTER:
            worldBorder.setCenter(this.centerX, this.centeZ);
            break;
         case SET_WARNING_BLOCKS:
            worldBorder.setWarningBlocks(this.warningBlocks);
            break;
         case SET_WARNING_TIME:
            worldBorder.setWarningTime(this.warningTime);
            break;
         case INITIALIZE:
            worldBorder.setCenter(this.centerX, this.centeZ);
            if (this.f_92ehixagx > 0) {
               worldBorder.setSize(this.lerpSize, this.sizeLerpTarget, this.f_92ehixagx);
            } else {
               worldBorder.setSize(this.sizeLerpTarget);
            }

            worldBorder.setMaxSize(this.maxSize);
            worldBorder.setWarningBlocks(this.warningBlocks);
            worldBorder.setWarningTime(this.warningTime);
      }
   }

   public static enum Type {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
