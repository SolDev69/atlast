package net.minecraft.client.sound.event;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MinecartWithPlayerSoundEvent extends TickableSoundEvent {
   private final PlayerEntity player;
   private final MinecartEntity minecart;

   public MinecartWithPlayerSoundEvent(PlayerEntity player, MinecartEntity minecart) {
      super(new Identifier("minecraft:minecart.inside"));
      this.player = player;
      this.minecart = minecart;
      this.attenuation = ISoundEvent.Attenuation.NONE;
      this.repeat = true;
      this.period = 0;
   }

   @Override
   public void tick() {
      if (!this.minecart.removed && this.player.hasVehicle() && this.player.vehicle == this.minecart) {
         float var1 = MathHelper.sqrt(this.minecart.velocityX * this.minecart.velocityX + this.minecart.velocityZ * this.minecart.velocityZ);
         if ((double)var1 >= 0.01) {
            this.volume = 0.0F + MathHelper.clamp(var1, 0.0F, 1.0F) * 0.75F;
         } else {
            this.volume = 0.0F;
         }
      } else {
         this.done = true;
      }
   }
}
