package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerAbilitiesS2CPacket implements Packet {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;

   public PlayerAbilitiesS2CPacket() {
   }

   public PlayerAbilitiesS2CPacket(PlayerAbilities abilities) {
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

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerAbilities(this);
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

   @Environment(EnvType.CLIENT)
   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float flySpeed) {
      this.flySpeed = flySpeed;
   }

   @Environment(EnvType.CLIENT)
   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float walkSpeed) {
      this.walkSpeed = walkSpeed;
   }
}
