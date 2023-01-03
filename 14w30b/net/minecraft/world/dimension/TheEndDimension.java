package net.minecraft.world.dimension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingleBiomeSource;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.chunk.TheEndChunkGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TheEndDimension extends Dimension {
   @Override
   public void createBiomeSource() {
      this.biomeSource = new SingleBiomeSource(Biome.THE_END, 0.0F);
      this.id = 1;
      this.isDark = true;
   }

   @Override
   public ChunkSource createChunkGenerator() {
      return new TheEndChunkGenerator(this.world, this.world.getSeed());
   }

   @Override
   public float getTimeOfDay(long time, float tickDelta) {
      return 0.0F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public float[] getBackgroundColor(float timeOfDay, float tickDelta) {
      return null;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Vec3d getFogColor(float timeOfDay, float tickDelta) {
      int var3 = 10518688;
      float var4 = MathHelper.cos(timeOfDay * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var4 = MathHelper.clamp(var4, 0.0F, 1.0F);
      float var5 = (float)(var3 >> 16 & 0xFF) / 255.0F;
      float var6 = (float)(var3 >> 8 & 0xFF) / 255.0F;
      float var7 = (float)(var3 & 0xFF) / 255.0F;
      var5 *= var4 * 0.0F + 0.15F;
      var6 *= var4 * 0.0F + 0.15F;
      var7 *= var4 * 0.0F + 0.15F;
      return new Vec3d((double)var5, (double)var6, (double)var7);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasGround() {
      return false;
   }

   @Override
   public boolean hasWorldSpawn() {
      return false;
   }

   @Override
   public boolean isOverworld() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public float getCloudHeight() {
      return 8.0F;
   }

   @Override
   public boolean isValidSpawnPos(int x, int z) {
      return this.world.getSurfaceBlock(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
   }

   @Override
   public BlockPos getForcedSpawnPoint() {
      return new BlockPos(100, 50, 0);
   }

   @Override
   public int getMinSpawnY() {
      return 50;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isFogThick(int x, int z) {
      return true;
   }

   @Override
   public String getName() {
      return "The End";
   }

   @Override
   public String getDataSuffix() {
      return "_end";
   }
}
