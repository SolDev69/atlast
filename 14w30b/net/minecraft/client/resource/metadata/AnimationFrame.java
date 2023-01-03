package net.minecraft.client.resource.metadata;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationFrame {
   private final int index;
   private final int time;

   public AnimationFrame(int index) {
      this(index, -1);
   }

   public AnimationFrame(int index, int time) {
      this.index = index;
      this.time = time;
   }

   public boolean usesDefaultFrameTime() {
      return this.time == -1;
   }

   public int getTime() {
      return this.time;
   }

   public int getIndex() {
      return this.index;
   }
}
