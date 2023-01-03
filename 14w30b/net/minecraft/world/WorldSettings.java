package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public final class WorldSettings {
   private final long seed;
   private final WorldSettings.GameMode gameMode;
   private final boolean allowStructures;
   private final boolean hardcore;
   private final WorldGeneratorType generatorType;
   private boolean allowCommands;
   private boolean hasBonusChest;
   private String generatorOptions = "";

   public WorldSettings(long seed, WorldSettings.GameMode gameMode, boolean allowStructures, boolean hardcore, WorldGeneratorType generatorType) {
      this.seed = seed;
      this.gameMode = gameMode;
      this.allowStructures = allowStructures;
      this.hardcore = hardcore;
      this.generatorType = generatorType;
   }

   public WorldSettings(WorldData data) {
      this(data.getSeed(), data.getDefaultGamemode(), data.allowStructures(), data.isHardcore(), data.getGeneratorType());
   }

   public WorldSettings enableBonusChest() {
      this.hasBonusChest = true;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public WorldSettings enableCommands() {
      this.allowCommands = true;
      return this;
   }

   public WorldSettings setGeneratorOptions(String generatorOptions) {
      this.generatorOptions = generatorOptions;
      return this;
   }

   public boolean hasBonusChest() {
      return this.hasBonusChest;
   }

   public long getSeed() {
      return this.seed;
   }

   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean allowStructures() {
      return this.allowStructures;
   }

   public WorldGeneratorType getGeneratorType() {
      return this.generatorType;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public static WorldSettings.GameMode getGameModeById(int id) {
      return WorldSettings.GameMode.byIndex(id);
   }

   public String getGeneratorOptions() {
      return this.generatorOptions;
   }

   public static enum GameMode {
      NOT_SET(-1, ""),
      SURVIVAL(0, "survival"),
      CREATIVE(1, "creative"),
      ADVENTURE(2, "adventure"),
      SPECTATOR(3, "spectator");

      int index;
      String id;

      private GameMode(int index, String id) {
         this.index = index;
         this.id = id;
      }

      public int getIndex() {
         return this.index;
      }

      public String getId() {
         return this.id;
      }

      public void apply(PlayerAbilities abilities) {
         if (this == CREATIVE) {
            abilities.canFly = true;
            abilities.creativeMode = true;
            abilities.invulnerable = true;
         } else if (this == SPECTATOR) {
            abilities.canFly = true;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = true;
         } else {
            abilities.canFly = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
         }

         abilities.canModifyWorld = !this.restrictsWorldModification();
      }

      public boolean restrictsWorldModification() {
         return this == ADVENTURE || this == SPECTATOR;
      }

      public boolean isCreative() {
         return this == CREATIVE;
      }

      public boolean isSurvival() {
         return this == SURVIVAL || this == ADVENTURE;
      }

      public static WorldSettings.GameMode byIndex(int index) {
         for(WorldSettings.GameMode var4 : values()) {
            if (var4.index == index) {
               return var4;
            }
         }

         return SURVIVAL;
      }

      @Environment(EnvType.CLIENT)
      public static WorldSettings.GameMode byId(String id) {
         for(WorldSettings.GameMode var4 : values()) {
            if (var4.id.equals(id)) {
               return var4;
            }
         }

         return SURVIVAL;
      }
   }
}
