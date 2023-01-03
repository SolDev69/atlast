package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;

public class PlayerAbilitiesC2SPacket implements Packet {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;

   public PlayerAbilitiesC2SPacket() {
   }

   public PlayerAbilitiesC2SPacket(PlayerAbilities abilities) {
      this.setInvulnerable(abilities.invulnerable);
      this.setFlying(abilities.flying);
      this.setAllowFlying(abilities.canFly);
      this.setCreativeMode(abilities.creativeMode);
      this.setFlySpeed(abilities.getFlySpeed());
      this.setWalkSpeed(abilities.getWalkSpeed());
   }

   @Override
   public void write(PacketByteBuf buffer) {
      byte var2 = buffer.readByte();
      this.setInvulnerable((var2 & 1) > 0);
      this.setFlying((var2 & 2) > 0);
      this.setAllowFlying((var2 & 4) > 0);
      this.setCreativeMode((var2 & 8) > 0);
      this.setFlySpeed(buffer.readFloat());
      this.setWalkSpeed(buffer.readFloat());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      byte var2 = 0;
      if (this.isInvulnerable()) {
         var2 = (byte)(var2 | 1);
      }

      if (this.isFlying()) {
         var2 = (byte)(var2 | 2);
      }

      if (this.allowsFlying()) {
         var2 = (byte)(var2 | 4);
      }

      if (this.isCreativeMode()) {
         var2 = (byte)(var2 | 8);
      }

      buffer.writeByte(var2);
      buffer.writeFloat(this.flySpeed);
      buffer.writeFloat(this.walkSpeed);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean invulnerable) {
      this.invulnerable = invulnerable;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean flying) {
      this.flying = flying;
   }

   public boolean allowsFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean allowFlying) {
      this.allowFlying = allowFlying;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean creativeMode) {
      this.creativeMode = creativeMode;
   }

   public void setFlySpeed(float flySpeed) {
      this.flySpeed = flySpeed;
   }

   public void setWalkSpeed(float walkSpeed) {
      this.walkSpeed = walkSpeed;
   }
}
