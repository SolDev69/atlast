package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EmptyChunk extends WorldChunk {
   public EmptyChunk(World c_54ruxjwzt, int i, int j) {
      super(c_54ruxjwzt, i, j);
   }

   @Override
   public boolean isAt(int chunkX, int chunkZ) {
      return chunkX == this.chunkX && chunkZ == this.chunkZ;
   }

   @Override
   public int getHeight(int sectionX, int sectionZ) {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void populateHeightmap() {
   }

   @Override
   public void populateSkylight() {
   }

   @Override
   public Block getBlock(BlockPos pos) {
      return Blocks.AIR;
   }

   @Override
   public int getOpacity(BlockPos pos) {
      return 255;
   }

   @Override
   public int getBlockMetadata(BlockPos pos) {
      return 0;
   }

   @Override
   public int getLight(LightType type, BlockPos pos) {
      return type.defaultValue;
   }

   @Override
   public void setLight(LightType type, BlockPos pos, int light) {
   }

   @Override
   public int getLight(BlockPos pos, int ambientDarkness) {
      return 0;
   }

   @Override
   public void addEntity(Entity entity) {
   }

   @Override
   public void removeEntity(Entity entity) {
   }

   @Override
   public void removeEntity(Entity entity, int chunkY) {
   }

   @Override
   public boolean hasSkyAccess(BlockPos pos) {
      return false;
   }

   @Override
   public BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }

   @Override
   public void addBlockEntity(BlockEntity blockEntity) {
   }

   @Override
   public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
   }

   @Override
   public void removeBlockEntity(BlockPos pos) {
   }

   @Override
   public void load() {
   }

   @Override
   public void unload() {
   }

   @Override
   public void markDirty() {
   }

   @Override
   public void getEntities(Entity exclude, Box box, List entities, Predicate filter) {
   }

   @Override
   public void getEntities(Class type, Box box, List entities, Predicate filter) {
   }

   @Override
   public boolean shouldSave(boolean saveEntities) {
      return false;
   }

   @Override
   public Random getRandomForSlime(long seed) {
      return new Random(
         this.getWorld().getSeed()
               + (long)(this.chunkX * this.chunkX * 4987142)
               + (long)(this.chunkX * 5947611)
               + (long)(this.chunkZ * this.chunkZ) * 4392871L
               + (long)(this.chunkZ * 389711)
            ^ seed
      );
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean isEmpty(int minY, int maxY) {
      return true;
   }
}
