package net.minecraft.block.pattern;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
   private final Predicate[][][] pattern;
   private final int depth;
   private final int width;
   private final int height;

   public BlockPattern(Predicate[][][] pattern) {
      this.pattern = pattern;
      this.depth = pattern.length;
      if (this.depth > 0) {
         this.width = pattern[0].length;
         if (this.width > 0) {
            this.height = pattern[0][0].length;
         } else {
            this.height = 0;
         }
      } else {
         this.width = 0;
         this.height = 0;
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private BlockPattern.Match matches(BlockPos pos, Direction forward, Direction up, LoadingCache cache) {
      for(int var5 = 0; var5 < this.height; ++var5) {
         for(int var6 = 0; var6 < this.width; ++var6) {
            for(int var7 = 0; var7 < this.depth; ++var7) {
               if (!this.pattern[var7][var6][var5].apply(cache.getUnchecked(transform(pos, forward, up, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.Match(pos, forward, up, cache);
   }

   public BlockPattern.Match find(World world, BlockPos pos) {
      LoadingCache var3 = CacheBuilder.newBuilder().build(new BlockPattern.BlockCacheLoader(world));
      int var4 = Math.max(Math.max(this.height, this.width), this.depth);

      for(BlockPos var6 : BlockPos.iterateRegion(pos, pos.add(var4 - 1, var4 - 1, var4 - 1))) {
         for(Direction var10 : Direction.values()) {
            for(Direction var14 : Direction.values()) {
               if (var14 != var10 && var14 != var10.getOpposite()) {
                  BlockPattern.Match var15 = this.matches(var6, var10, var14, var3);
                  if (var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   protected static BlockPos transform(BlockPos pos, Direction forward, Direction up, int dx, int dy, int dz) {
      if (forward != up && forward != up.getOpposite()) {
         Vec3i var6 = new Vec3i(forward.getOffsetX(), forward.getOffsetY(), forward.getOffsetZ());
         Vec3i var7 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
         Vec3i var8 = var6.cross(var7);
         return pos.add(
            var7.getX() * -dy + var8.getX() * dx + var6.getX() * dz,
            var7.getY() * -dy + var8.getY() * dx + var6.getY() * dz,
            var7.getZ() * -dy + var8.getZ() * dx + var6.getZ() * dz
         );
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class BlockCacheLoader extends CacheLoader {
      private final World world;

      public BlockCacheLoader(World world) {
         this.world = world;
      }

      public BlockPointer load(BlockPos c_76varpwca) {
         return new BlockPointer(this.world, c_76varpwca);
      }
   }

   public static class Match {
      private final BlockPos topLeftFront;
      private final Direction forward;
      private final Direction up;
      private final LoadingCache cache;

      public Match(BlockPos topLeftFront, Direction forward, Direction up, LoadingCache cache) {
         this.topLeftFront = topLeftFront;
         this.forward = forward;
         this.up = up;
         this.cache = cache;
      }

      public Direction getForward() {
         return this.forward;
      }

      public Direction getUp() {
         return this.up;
      }

      public BlockPointer getBlock(int dx, int dy, int dz) {
         return (BlockPointer)this.cache.getUnchecked(BlockPattern.transform(this.topLeftFront, this.getForward(), this.getUp(), dx, dy, dz));
      }
   }
}
