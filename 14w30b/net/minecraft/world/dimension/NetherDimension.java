package net.minecraft.world.dimension;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingleBiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.chunk.NetherChunkGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class NetherDimension extends Dimension {
   @Override
   public void createBiomeSource() {
      this.biomeSource = new SingleBiomeSource(Biome.HELL, 0.0F);
      this.yeetsWater = true;
      this.isDark = true;
      this.id = -1;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Vec3d getFogColor(float timeOfDay, float tickDelta) {
      return new Vec3d(0.2F, 0.03F, 0.03F);
   }

   @Override
   protected void populateBrightnessTable() {
      float var1 = 0.1F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.brightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
      }
   }

   @Override
   public ChunkSource createChunkGenerator() {
      return new NetherChunkGenerator(this.world, this.world.getData().allowStructures(), this.world.getSeed());
   }

   @Override
   public boolean isOverworld() {
      return false;
   }

   @Override
   public boolean isValidSpawnPos(int x, int z) {
      return false;
   }

   @Override
   public float getTimeOfDay(long time, float tickDelta) {
      return 0.5F;
   }

   @Override
   public boolean hasWorldSpawn() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isFogThick(int x, int z) {
      return true;
   }

   @Override
   public String getName() {
      return "Nether";
   }

   @Override
   public String getDataSuffix() {
      return "_nether";
   }

   @Override
   public WorldBorder getWorldBorder() {
      return new WorldBorder() {
         @Override
         public double getCenterX() {
            return super.getCenterX() / 8.0;
         }

         @Override
         public double getCenterZ() {
            return super.getCenterZ() / 8.0;
         }
      };
   }
}
