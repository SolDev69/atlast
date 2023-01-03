package net.minecraft.world;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface IWorld {
   BlockEntity getBlockEntity(BlockPos pos);

   @Environment(EnvType.CLIENT)
   int getLightColor(BlockPos pos, int blockLight);

   BlockState getBlockState(BlockPos pos);

   boolean isAir(BlockPos pos);

   @Environment(EnvType.CLIENT)
   Biome getBiome(BlockPos pos);

   @Environment(EnvType.CLIENT)
   boolean isSaved();

   int getEmittedStrongPower(BlockPos pos, Direction dir);

   @Environment(EnvType.CLIENT)
   WorldGeneratorType getGeneratorType();
}
