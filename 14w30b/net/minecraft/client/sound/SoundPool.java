package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SoundPool implements SoundContainer {
   private final List containers = Lists.newArrayList();
   private final Random random = new Random();
   private final Identifier id;
   private final SoundCategory category;
   private double volumeMultiplier;
   private double pitchMultiplier;

   public SoundPool(Identifier id, double volumeMultiplier, double pitchMultiplier, SoundCategory category) {
      this.id = id;
      this.pitchMultiplier = pitchMultiplier;
      this.volumeMultiplier = volumeMultiplier;
      this.category = category;
   }

   @Override
   public int getWeight() {
      int var1 = 0;

      for(SoundContainer var3 : this.containers) {
         var1 += var3.getWeight();
      }

      return var1;
   }

   public Sound get() {
      int var1 = this.getWeight();
      if (!this.containers.isEmpty() && var1 != 0) {
         int var2 = this.random.nextInt(var1);

         for(SoundContainer var4 : this.containers) {
            var2 -= var4.getWeight();
            if (var2 < 0) {
               Sound var5 = (Sound)var4.get();
               var5.setVolume(var5.getVolume() * this.volumeMultiplier);
               var5.setPitch(var5.getPitch() * this.pitchMultiplier);
               return var5;
            }
         }

         return SoundManager.MISSING_SOUND;
      } else {
         return SoundManager.MISSING_SOUND;
      }
   }

   public void add(SoundContainer sound) {
      this.containers.add(sound);
   }

   public Identifier getId() {
      return this.id;
   }

   public SoundCategory getCategory() {
      return this.category;
   }
}
