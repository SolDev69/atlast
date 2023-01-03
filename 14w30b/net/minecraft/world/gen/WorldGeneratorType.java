package net.minecraft.world.gen;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldGeneratorType {
   public static final WorldGeneratorType[] ALL = new WorldGeneratorType[16];
   public static final WorldGeneratorType DEFAULT = new WorldGeneratorType(0, "default", 1).setVersioned();
   public static final WorldGeneratorType FLAT = new WorldGeneratorType(1, "flat");
   public static final WorldGeneratorType LARGE_BIOMES = new WorldGeneratorType(2, "largeBiomes");
   public static final WorldGeneratorType AMPLIFIED = new WorldGeneratorType(3, "amplified").setHasInfo();
   public static final WorldGeneratorType CUSTOMIZED = new WorldGeneratorType(4, "customized");
   public static final WorldGeneratorType DEBUG_ALL_BLOCK_STATES = new WorldGeneratorType(5, "debug_all_block_states");
   public static final WorldGeneratorType DEFAULT_1_1 = new WorldGeneratorType(8, "default_1_1", 0).setVisible(false);
   private final int index;
   private final String id;
   private final int version;
   private boolean visible;
   private boolean versioned;
   private boolean info;

   private WorldGeneratorType(int index, String id) {
      this(index, id, 0);
   }

   private WorldGeneratorType(int index, String id, int version) {
      this.id = id;
      this.version = version;
      this.visible = true;
      this.index = index;
      ALL[index] = this;
   }

   public String getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public String getName() {
      return "generator." + this.id;
   }

   @Environment(EnvType.CLIENT)
   public String getInfoTranslationKey() {
      return this.getName() + ".info";
   }

   public int getVersion() {
      return this.version;
   }

   public WorldGeneratorType getTypeForVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   private WorldGeneratorType setVisible(boolean visible) {
      this.visible = visible;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public boolean isVisible() {
      return this.visible;
   }

   private WorldGeneratorType setVersioned() {
      this.versioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.versioned;
   }

   public static WorldGeneratorType byId(String id) {
      for(int var1 = 0; var1 < ALL.length; ++var1) {
         if (ALL[var1] != null && ALL[var1].id.equalsIgnoreCase(id)) {
            return ALL[var1];
         }
      }

      return null;
   }

   public int getIndex() {
      return this.index;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasInfo() {
      return this.info;
   }

   private WorldGeneratorType setHasInfo() {
      this.info = true;
      return this;
   }
}
