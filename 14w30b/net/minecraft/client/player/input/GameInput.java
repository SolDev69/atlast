package net.minecraft.client.player.input;

import net.minecraft.client.options.GameOptions;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameInput extends PlayerInput {
   private final GameOptions options;

   public GameInput(GameOptions options) {
      this.options = options;
   }

   @Override
   public void tick() {
      this.movementSideways = 0.0F;
      this.movementForward = 0.0F;
      if (this.options.forwardKey.isPressed()) {
         ++this.movementForward;
      }

      if (this.options.backKey.isPressed()) {
         --this.movementForward;
      }

      if (this.options.leftKey.isPressed()) {
         ++this.movementSideways;
      }

      if (this.options.rightKey.isPressed()) {
         --this.movementSideways;
      }

      this.jumping = this.options.jumpKey.isPressed();
      this.sneaking = this.options.sneakKey.isPressed();
      if (this.sneaking) {
         this.movementSideways = (float)((double)this.movementSideways * 0.3);
         this.movementForward = (float)((double)this.movementForward * 0.3);
      }
   }
}
