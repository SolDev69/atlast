package net.minecraft.client.sound.music;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.event.ISoundEvent;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.resource.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;

@net.ornithemc.api.Environment(EnvType.CLIENT)
public class MusicManager implements Tickable {
   private final Random random = new Random();
   private final MinecraftClient client;
   private ISoundEvent event;
   private int timeUntilNextSong = 100;

   public MusicManager(MinecraftClient client) {
      this.client = client;
   }

   @Override
   public void tick() {
      MusicManager.Environment var1 = this.client.getMusicEnvironment();
      if (this.event != null) {
         if (!var1.getId().equals(this.event.getId())) {
            this.client.getSoundManager().stop(this.event);
            this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, var1.getMinWaitTime() / 2);
         }

         if (!this.client.getSoundManager().isPlaying(this.event)) {
            this.event = null;
            this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, var1.getMinWaitTime(), var1.getMaxWaitTime()), this.timeUntilNextSong);
         }
      }

      if (this.event == null && this.timeUntilNextSong-- <= 0) {
         this.event = SimpleSoundEvent.of(var1.getId());
         this.client.getSoundManager().play(this.event);
         this.timeUntilNextSong = Integer.MAX_VALUE;
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public static enum Environment {
      MENU(new Identifier("minecraft:music.menu"), 20, 600),
      GAME(new Identifier("minecraft:music.game"), 12000, 24000),
      CREATIVE(new Identifier("minecraft:music.game.creative"), 1200, 3600),
      CREDITS(new Identifier("minecraft:music.game.end.credits"), Integer.MAX_VALUE, Integer.MAX_VALUE),
      NETHER(new Identifier("minecraft:music.game.nether"), 1200, 3600),
      END_BOSS(new Identifier("minecraft:music.game.end.dragon"), 0, 0),
      END(new Identifier("minecraft:music.game.end"), 6000, 24000);

      private final Identifier id;
      private final int minWaitTime;
      private final int maxWaitTime;

      private Environment(Identifier id, int minWaitTime, int maxWaitTime) {
         this.id = id;
         this.minWaitTime = minWaitTime;
         this.maxWaitTime = maxWaitTime;
      }

      public Identifier getId() {
         return this.id;
      }

      public int getMinWaitTime() {
         return this.minWaitTime;
      }

      public int getMaxWaitTime() {
         return this.maxWaitTime;
      }
   }
}
