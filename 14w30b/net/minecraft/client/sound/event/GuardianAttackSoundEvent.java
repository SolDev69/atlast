package net.minecraft.client.sound.event;

import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuardianAttackSoundEvent extends TickableSoundEvent {
   private final GuardianEntity guardian;

   public GuardianAttackSoundEvent(GuardianEntity guardian) {
      super(new Identifier("minecraft:mob.guardian.attack"));
      this.guardian = guardian;
      this.attenuation = ISoundEvent.Attenuation.NONE;
      this.repeat = true;
      this.period = 0;
   }

   @Override
   public void tick() {
      if (!this.guardian.removed && this.guardian.m_16dqbnsqq()) {
         this.x = (float)this.guardian.x;
         this.y = (float)this.guardian.y;
         this.z = (float)this.guardian.z;
         float var1 = this.guardian.m_76qczvcqr(0.0F);
         this.volume = 0.0F + 1.0F * var1 * var1;
         this.pitch = 0.7F + 0.5F * var1;
      } else {
         this.done = true;
      }
   }
}
