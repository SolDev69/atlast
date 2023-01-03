package net.minecraft.client.player.input;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerInput {
   public float movementSideways;
   public float movementForward;
   public boolean jumping;
   public boolean sneaking;

   public void tick() {
   }
}
