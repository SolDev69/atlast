package net.minecraft.util.math;

import com.google.common.base.Objects;

public class Vec3i implements Comparable {
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3i(double x, double y, double z) {
      this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Vec3i)) {
         return false;
      } else {
         Vec3i var2 = (Vec3i)obj;
         if (this.getX() != var2.getX()) {
            return false;
         } else if (this.getY() != var2.getY()) {
            return false;
         } else {
            return this.getZ() == var2.getZ();
         }
      }
   }

   @Override
   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i c_05etfdlgd) {
      if (this.getY() == c_05etfdlgd.getY()) {
         return this.getZ() == c_05etfdlgd.getZ() ? this.getX() - c_05etfdlgd.getX() : this.getZ() - c_05etfdlgd.getZ();
      } else {
         return this.getY() - c_05etfdlgd.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i cross(Vec3i vec) {
      return new Vec3i(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }

   public double squaredDistanceTo(double x, double y, double z) {
      double var7 = (double)this.getX() - x;
      double var9 = (double)this.getY() - y;
      double var11 = (double)this.getZ() - z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double squaredDistanceToCenter(double x, double y, double z) {
      double var7 = (double)this.getX() + 0.5 - x;
      double var9 = (double)this.getY() + 0.5 - y;
      double var11 = (double)this.getZ() + 0.5 - z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double squaredDistanceTo(Vec3i vec) {
      return this.squaredDistanceTo((double)vec.getX(), (double)vec.getY(), (double)vec.getZ());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}
