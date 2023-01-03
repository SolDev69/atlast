package net.minecraft.client.sound.event;

import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmptyMinecartSoundEvent extends TickableSoundEvent {
   private final MinecartEntity minecart;
   private float f_49vzqvtew = 0.0F;

   public EmptyMinecartSoundEvent(MinecartEntity minecart) {
      super(new Identifier("minecraft:minecart.base"));
      this.minecart = minecart;
      this.repeat = true;
      this.period = 0;
   }

   @Override
   public void tick() {
      if (this.minecart.removed) {
         this.done = true;
      } else {
         this.x = (float)this.minecart.x;
         this.y = (float)this.minecart.y;
         this.z = (float)this.minecart.z;
         float var1 = MathHelper.sqrt(this.minecart.velocityX * this.minecart.velocityX + this.minecart.velocityZ * this.minecart.velocityZ);
         if ((double)var1 >= 0.01) {
            this.f_49vzqvtew = MathHelper.clamp(this.f_49vzqvtew + 0.0025F, 0.0F, 1.0F);
            this.volume = 0.0F + MathHelper.clamp(var1, 0.0F, 0.5F) * 0.7F;
         } else {
            this.f_49vzqvtew = 0.0F;
            this.volume = 0.0F;
         }
      }
   }
}
