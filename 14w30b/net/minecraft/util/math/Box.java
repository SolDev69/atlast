package net.minecraft.util.math;

import net.minecraft.util.HitResult;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Box {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public Box(BlockPos min, BlockPos max) {
      this.minX = (double)min.getX();
      this.minY = (double)min.getY();
      this.minZ = (double)min.getZ();
      this.maxX = (double)max.getX();
      this.maxY = (double)max.getY();
      this.maxZ = (double)max.getZ();
   }

   public Box grow(double dx, double dy, double dz) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if (dx < 0.0) {
         var7 += dx;
      } else if (dx > 0.0) {
         var13 += dx;
      }

      if (dy < 0.0) {
         var9 += dy;
      } else if (dy > 0.0) {
         var15 += dy;
      }

      if (dz < 0.0) {
         var11 += dz;
      } else if (dz > 0.0) {
         var17 += dz;
      }

      return new Box(var7, var9, var11, var13, var15, var17);
   }

   public Box expand(double dx, double dy, double dz) {
      double var7 = this.minX - dx;
      double var9 = this.minY - dy;
      double var11 = this.minZ - dz;
      double var13 = this.maxX + dx;
      double var15 = this.maxY + dy;
      double var17 = this.maxZ + dz;
      return new Box(var7, var9, var11, var13, var15, var17);
   }

   public Box union(Box box) {
      double var2 = Math.min(this.minX, box.minX);
      double var4 = Math.min(this.minY, box.minY);
      double var6 = Math.min(this.minZ, box.minZ);
      double var8 = Math.max(this.maxX, box.maxX);
      double var10 = Math.max(this.maxY, box.maxY);
      double var12 = Math.max(this.maxZ, box.maxZ);
      return new Box(var2, var4, var6, var8, var10, var12);
   }

   @Environment(EnvType.CLIENT)
   public static Box of(double x1, double y1, double z1, double x2, double y2, double z2) {
      double var12 = Math.min(x1, x2);
      double var14 = Math.min(y1, y2);
      double var16 = Math.min(z1, z2);
      double var18 = Math.max(x1, x2);
      double var20 = Math.max(y1, y2);
      double var22 = Math.max(z1, z2);
      return new Box(var12, var14, var16, var18, var20, var22);
   }

   public Box move(double dx, double dy, double dz) {
      return new Box(this.minX + dx, this.minY + dy, this.minZ + dz, this.maxX + dx, this.maxY + dy, this.maxZ + dz);
   }

   public double intersectX(Box box, double limit) {
      if (!(box.maxY <= this.minY) && !(box.minY >= this.maxY) && !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ)) {
         if (limit > 0.0 && box.maxX <= this.minX) {
            double var6 = this.minX - box.maxX;
            if (var6 < limit) {
               limit = var6;
            }
         } else if (limit < 0.0 && box.minX >= this.maxX) {
            double var4 = this.maxX - box.minX;
            if (var4 > limit) {
               limit = var4;
            }
         }

         return limit;
      } else {
         return limit;
      }
   }

   public double intersectY(Box box, double limit) {
      if (!(box.maxX <= this.minX) && !(box.minX >= this.maxX) && !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ)) {
         if (limit > 0.0 && box.maxY <= this.minY) {
            double var6 = this.minY - box.maxY;
            if (var6 < limit) {
               limit = var6;
            }
         } else if (limit < 0.0 && box.minY >= this.maxY) {
            double var4 = this.maxY - box.minY;
            if (var4 > limit) {
               limit = var4;
            }
         }

         return limit;
      } else {
         return limit;
      }
   }

   public double intersectZ(Box box, double limit) {
      if (!(box.maxX <= this.minX) && !(box.minX >= this.maxX) && !(box.maxY <= this.minY) && !(box.minY >= this.maxY)) {
         if (limit > 0.0 && box.maxZ <= this.minZ) {
            double var6 = this.minZ - box.maxZ;
            if (var6 < limit) {
               limit = var6;
            }
         } else if (limit < 0.0 && box.minZ >= this.maxZ) {
            double var4 = this.maxZ - box.minZ;
            if (var4 > limit) {
               limit = var4;
            }
         }

         return limit;
      } else {
         return limit;
      }
   }

   public boolean intersects(Box box) {
      if (box.maxX <= this.minX || box.minX >= this.maxX) {
         return false;
      } else if (box.maxY <= this.minY || box.minY >= this.maxY) {
         return false;
      } else {
         return !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ);
      }
   }

   public boolean contains(Vec3d vec) {
      if (vec.x <= this.minX || vec.x >= this.maxX) {
         return false;
      } else if (vec.y <= this.minY || vec.y >= this.maxY) {
         return false;
      } else {
         return !(vec.z <= this.minZ) && !(vec.z >= this.maxZ);
      }
   }

   public double getAverageSideLength() {
      double var1 = this.maxX - this.minX;
      double var3 = this.maxY - this.minY;
      double var5 = this.maxZ - this.minZ;
      return (var1 + var3 + var5) / 3.0;
   }

   public Box contract(double dx, double dy, double dz) {
      double var7 = this.minX + dx;
      double var9 = this.minY + dy;
      double var11 = this.minZ + dz;
      double var13 = this.maxX - dx;
      double var15 = this.maxY - dy;
      double var17 = this.maxZ - dz;
      return new Box(var7, var9, var11, var13, var15, var17);
   }

   public HitResult clip(Vec3d from, Vec3d to) {
      Vec3d var3 = from.intermediateWithX(to, this.minX);
      Vec3d var4 = from.intermediateWithX(to, this.maxX);
      Vec3d var5 = from.intermediateWithY(to, this.minY);
      Vec3d var6 = from.intermediateWithY(to, this.maxY);
      Vec3d var7 = from.intermediateWithZ(to, this.minZ);
      Vec3d var8 = from.intermediateWithZ(to, this.maxZ);
      if (!this.containsYZ(var3)) {
         var3 = null;
      }

      if (!this.containsYZ(var4)) {
         var4 = null;
      }

      if (!this.containsXZ(var5)) {
         var5 = null;
      }

      if (!this.containsXZ(var6)) {
         var6 = null;
      }

      if (!this.containsXY(var7)) {
         var7 = null;
      }

      if (!this.containsXY(var8)) {
         var8 = null;
      }

      Vec3d var9 = null;
      if (var3 != null) {
         var9 = var3;
      }

      if (var4 != null && (var9 == null || from.squaredDistanceTo(var4) < from.squaredDistanceTo(var9))) {
         var9 = var4;
      }

      if (var5 != null && (var9 == null || from.squaredDistanceTo(var5) < from.squaredDistanceTo(var9))) {
         var9 = var5;
      }

      if (var6 != null && (var9 == null || from.squaredDistanceTo(var6) < from.squaredDistanceTo(var9))) {
         var9 = var6;
      }

      if (var7 != null && (var9 == null || from.squaredDistanceTo(var7) < from.squaredDistanceTo(var9))) {
         var9 = var7;
      }

      if (var8 != null && (var9 == null || from.squaredDistanceTo(var8) < from.squaredDistanceTo(var9))) {
         var9 = var8;
      }

      if (var9 == null) {
         return null;
      } else {
         Object var10 = null;
         Direction var11;
         if (var9 == var3) {
            var11 = Direction.WEST;
         } else if (var9 == var4) {
            var11 = Direction.EAST;
         } else if (var9 == var5) {
            var11 = Direction.DOWN;
         } else if (var9 == var6) {
            var11 = Direction.UP;
         } else if (var9 == var7) {
            var11 = Direction.NORTH;
         } else {
            var11 = Direction.SOUTH;
         }

         return new HitResult(var9, var11);
      }
   }

   private boolean containsYZ(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
      }
   }

   private boolean containsXZ(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
      }
   }

   private boolean containsXY(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
      }
   }

   @Override
   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }
}
