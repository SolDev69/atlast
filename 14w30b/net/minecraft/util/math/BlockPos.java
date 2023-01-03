package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockPos extends Vec3i {
   public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
   private static final int OFFSET_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int OFFSET_Z = OFFSET_X;
   private static final int OFFSET_Y = 64 - OFFSET_X - OFFSET_Z;
   private static final int SIZE_Y = 0 + OFFSET_Z;
   private static final int SIZE_X = SIZE_Y + OFFSET_Y;
   private static final long MASK_X = (1L << OFFSET_X) - 1L;
   private static final long MASK_Y = (1L << OFFSET_Y) - 1L;
   private static final long MASK_Z = (1L << OFFSET_Z) - 1L;

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos(Entity pos) {
      this(pos.x, pos.y, pos.z);
   }

   public BlockPos(Vec3d vec) {
      this(vec.x, vec.y, vec.z);
   }

   public BlockPos(Vec3i vec) {
      this(vec.getX(), vec.getY(), vec.getZ());
   }

   public BlockPos add(double x, double y, double z) {
      return new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
   }

   public BlockPos add(int x, int y, int z) {
      return new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public BlockPos add(Vec3i vec) {
      return new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
   }

   @Environment(EnvType.CLIENT)
   public BlockPos subtract(Vec3i vec) {
      return new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
   }

   public BlockPos scale(int scale) {
      return new BlockPos(this.getX() * scale, this.getY() * scale, this.getZ() * scale);
   }

   public BlockPos up() {
      return this.up(1);
   }

   public BlockPos up(int d) {
      return this.offset(Direction.UP, d);
   }

   public BlockPos down() {
      return this.down(1);
   }

   public BlockPos down(int d) {
      return this.offset(Direction.DOWN, d);
   }

   public BlockPos north() {
      return this.north(1);
   }

   public BlockPos north(int d) {
      return this.offset(Direction.NORTH, d);
   }

   public BlockPos south() {
      return this.south(1);
   }

   public BlockPos south(int d) {
      return this.offset(Direction.SOUTH, d);
   }

   public BlockPos west() {
      return this.west(1);
   }

   public BlockPos west(int d) {
      return this.offset(Direction.WEST, d);
   }

   public BlockPos east() {
      return this.east(1);
   }

   public BlockPos east(int d) {
      return this.offset(Direction.EAST, d);
   }

   public BlockPos offset(Direction dir) {
      return this.offset(dir, 1);
   }

   public BlockPos offset(Direction dir, int d) {
      return new BlockPos(this.getX() + dir.getOffsetX() * d, this.getY() + dir.getOffsetY() * d, this.getZ() + dir.getOffsetZ() * d);
   }

   public BlockPos cross(Vec3i vec) {
      return new BlockPos(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }

   public long toLong() {
      return ((long)this.getX() & MASK_X) << SIZE_X | ((long)this.getY() & MASK_Y) << SIZE_Y | ((long)this.getZ() & MASK_Z) << 0;
   }

   public static BlockPos fromLong(long l) {
      int var2 = (int)(l << 64 - SIZE_X - OFFSET_X >> 64 - OFFSET_X);
      int var3 = (int)(l << 64 - SIZE_Y - OFFSET_Y >> 64 - OFFSET_Y);
      int var4 = (int)(l << 64 - OFFSET_Z >> 64 - OFFSET_Z);
      return new BlockPos(var2, var3, var4);
   }

   public static Iterable iterateRegion(BlockPos pos1, BlockPos pos2) {
      final BlockPos var2 = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
      final BlockPos var3 = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
      return new Iterable() {
         @Override
         public Iterator iterator() {
            return new AbstractIterator() {
               private BlockPos pos = null;

               protected BlockPos computeNext() {
                  if (this.pos == null) {
                     this.pos = var2;
                     return this.pos;
                  } else if (this.pos.equals(var3)) {
                     return (BlockPos)this.endOfData();
                  } else {
                     int var1 = this.pos.getX();
                     int var2x = this.pos.getY();
                     int var3x = this.pos.getZ();
                     if (var1 < var3.getX()) {
                        ++var1;
                     } else if (var2x < var3.getY()) {
                        var1 = var2.getX();
                        ++var2x;
                     } else if (var3x < var3.getZ()) {
                        var1 = var2.getX();
                        var2x = var2.getY();
                        ++var3x;
                     }

                     this.pos = new BlockPos(var1, var2x, var3x);
                     return this.pos;
                  }
               }
            };
         }
      };
   }

   public static Iterable iterateRegionMutable(BlockPos pos1, BlockPos pos2) {
      final BlockPos var2 = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
      final BlockPos var3 = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
      return new Iterable() {
         @Override
         public Iterator iterator() {
            return new AbstractIterator() {
               private BlockPos.Mutable pos = null;

               protected BlockPos.Mutable computeNext() {
                  if (this.pos == null) {
                     this.pos = new BlockPos.Mutable(var2.getX(), var2.getY(), var2.getZ());
                     return this.pos;
                  } else if (this.pos.equals(var3)) {
                     return (BlockPos.Mutable)this.endOfData();
                  } else {
                     int var1 = this.pos.getX();
                     int var2x = this.pos.getY();
                     int var3x = this.pos.getZ();
                     if (var1 < var3.getX()) {
                        ++var1;
                     } else if (var2x < var3.getY()) {
                        var1 = var2.getX();
                        ++var2x;
                     } else if (var3x < var3.getZ()) {
                        var1 = var2.getX();
                        var2x = var2.getY();
                        ++var3x;
                     }

                     this.pos.posX = var1;
                     this.pos.posY = var2x;
                     this.pos.posZ = var3x;
                     return this.pos;
                  }
               }
            };
         }
      };
   }

   public static final class Mutable extends BlockPos {
      public int posX;
      public int posY;
      public int posZ;

      private Mutable(int x, int y, int z) {
         super(0, 0, 0);
         this.posX = x;
         this.posY = y;
         this.posZ = z;
      }

      @Override
      public int getX() {
         return this.posX;
      }

      @Override
      public int getY() {
         return this.posY;
      }

      @Override
      public int getZ() {
         return this.posZ;
      }
   }
}
