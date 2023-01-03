package net.minecraft.network.packet.s2c.play;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerMoveS2CPacket implements Packet {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private Set relativeArgs;

   public PlayerMoveS2CPacket() {
   }

   public PlayerMoveS2CPacket(double x, double y, double z, float yaw, float pitch, Set relativeArgs) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.relativeArgs = relativeArgs;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.x = buffer.readDouble();
      this.y = buffer.readDouble();
      this.z = buffer.readDouble();
      this.yaw = buffer.readFloat();
      this.pitch = buffer.readFloat();
      this.relativeArgs = PlayerMoveS2CPacket.Argument.byFlags(buffer.readUnsignedByte());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeDouble(this.x);
      buffer.writeDouble(this.y);
      buffer.writeDouble(this.z);
      buffer.writeFloat(this.yaw);
      buffer.writeFloat(this.pitch);
      buffer.writeByte(PlayerMoveS2CPacket.Argument.getFlags(this.relativeArgs));
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerMove(this);
   }

   @Environment(EnvType.CLIENT)
   public double getX() {
      return this.x;
   }

   @Environment(EnvType.CLIENT)
   public double getY() {
      return this.y;
   }

   @Environment(EnvType.CLIENT)
   public double getZ() {
      return this.z;
   }

   @Environment(EnvType.CLIENT)
   public float getYaw() {
      return this.yaw;
   }

   @Environment(EnvType.CLIENT)
   public float getPitch() {
      return this.pitch;
   }

   @Environment(EnvType.CLIENT)
   public Set getRelativeArgs() {
      return this.relativeArgs;
   }

   public static enum Argument {
      X(0),
      Y(1),
      Z(2),
      YAW(3),
      PITCH(4);

      private int index;

      private Argument(int index) {
         this.index = index;
      }

      private int getFlag() {
         return 1 << this.index;
      }

      private boolean isSet(int flags) {
         return (flags & this.getFlag()) == this.getFlag();
      }

      public static Set byFlags(int flags) {
         EnumSet var1 = EnumSet.noneOf(PlayerMoveS2CPacket.Argument.class);

         for(PlayerMoveS2CPacket.Argument var5 : values()) {
            if (var5.isSet(flags)) {
               var1.add(var5);
            }
         }

         return var1;
      }

      public static int getFlags(Set args) {
         int var1 = 0;

         for(PlayerMoveS2CPacket.Argument var3 : args) {
            var1 |= var3.getFlag();
         }

         return var1;
      }
   }
}
