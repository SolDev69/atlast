package net.minecraft.server.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.storage.WorldStorage;

public class DemoServerWorld extends ServerWorld {
   private static final long SEED = (long)"North Carolina".hashCode();
   public static final WorldSettings SETTINGS = new WorldSettings(SEED, WorldSettings.GameMode.SURVIVAL, true, false, WorldGeneratorType.DEFAULT)
      .enableBonusChest();

   public DemoServerWorld(MinecraftServer minecraftServer, WorldStorage c_53xzgymck, WorldData c_30zdgghms, int i, Profiler c_35cxspgmp) {
      super(minecraftServer, c_53xzgymck, c_30zdgghms, i, c_35cxspgmp);
      this.data.setSettings(SETTINGS);
   }
}
