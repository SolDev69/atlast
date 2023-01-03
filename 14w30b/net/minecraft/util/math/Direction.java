package net.minecraft.util.math;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.StringRepresentable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   private final int id;
   private final int opposite;
   private final int idHorizontal;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDir;
   private final Vec3i normal;
   private static final Direction[] ALL = new Direction[6];
   private static final Direction[] HORIZONTAL = new Direction[4];
   private static final Map BY_NAME = Maps.newHashMap();

   private Direction(int id, int opposite, int idHorizontal, String name, Direction.AxisDirection axisDir, Direction.Axis axis, Vec3i normal) {
      this.id = id;
      this.idHorizontal = idHorizontal;
      this.opposite = opposite;
      this.name = name;
      this.axis = axis;
      this.axisDir = axisDir;
      this.normal = normal;
   }

   public int getId() {
      return this.id;
   }

   public int getIdHorizontal() {
      return this.idHorizontal;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDir;
   }

   public Direction getOpposite() {
      return byId(this.opposite);
   }

   @Environment(EnvType.CLIENT)
   public Direction clockwise(Direction.Axis axis) {
      switch(axis) {
         case X:
            if (this != WEST && this != EAST) {
               return this.clockwiseX();
            }

            return this;
         case Y:
            if (this != UP && this != DOWN) {
               return this.clockwiseY();
            }

            return this;
         case Z:
            if (this != NORTH && this != SOUTH) {
               return this.clockwiseZ();
            }

            return this;
         default:
            throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   public Direction clockwiseY() {
      switch(this) {
         case NORTH:
            return EAST;
         case EAST:
            return SOUTH;
         case SOUTH:
            return WEST;
         case WEST:
            return NORTH;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   @Environment(EnvType.CLIENT)
   private Direction clockwiseX() {
      switch(this) {
         case NORTH:
            return DOWN;
         case EAST:
         case WEST:
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
         case SOUTH:
            return UP;
         case UP:
            return NORTH;
         case DOWN:
            return SOUTH;
      }
   }

   @Environment(EnvType.CLIENT)
   private Direction clockwiseZ() {
      switch(this) {
         case EAST:
            return DOWN;
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            return UP;
         case UP:
            return EAST;
         case DOWN:
            return WEST;
      }
   }

   public Direction counterClockwiseY() {
      switch(this) {
         case NORTH:
            return WEST;
         case EAST:
            return NORTH;
         case SOUTH:
            return EAST;
         case WEST:
            return SOUTH;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getOffsetX() {
      return this.axis == Direction.Axis.X ? this.axisDir.getOffset() : 0;
   }

   public int getOffsetY() {
      return this.axis == Direction.Axis.Y ? this.axisDir.getOffset() : 0;
   }

   public int getOffsetZ() {
      return this.axis == Direction.Axis.Z ? this.axisDir.getOffset() : 0;
   }

   public String getName() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Environment(EnvType.CLIENT)
   public static Direction byName(String name) {
      return name == null ? null : (Direction)BY_NAME.get(name.toLowerCase());
   }

   public static Direction byId(int id) {
      return ALL[MathHelper.abs(id % ALL.length)];
   }

   public static Direction byIdHorizontal(int id) {
      return HORIZONTAL[MathHelper.abs(id % HORIZONTAL.length)];
   }

   public static Direction byRotation(double rotation) {
      return byIdHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
   }

   public static Direction pick(Random random) {
      return values()[random.nextInt(values().length)];
   }

   @Environment(EnvType.CLIENT)
   public static Direction getClosest(float dx, float dy, float dz) {
      Direction var3 = NORTH;
      float var4 = Float.MIN_VALUE;

      for(Direction var8 : values()) {
         float var9 = dx * (float)var8.normal.getX() + dy * (float)var8.normal.getY() + dz * (float)var8.normal.getZ();
         if (var9 > var4) {
            var4 = var9;
            var3 = var8;
         }
      }

      return var3;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getStringRepresentation() {
      return this.name;
   }

   @Environment(EnvType.CLIENT)
   public Vec3i getNormal() {
      return this.normal;
   }

   static {
      for(Direction var3 : values()) {
         ALL[var3.id] = var3;
         if (var3.getAxis().isHorizontal()) {
            HORIZONTAL[var3.idHorizontal] = var3;
         }

         BY_NAME.put(var3.getName().toLowerCase(), var3);
      }
   }

   public static enum Axis implements Predicate, StringRepresentable {
      X("x", Direction.Plane.HORIZONTAL),
      Y("y", Direction.Plane.VERTICAL),
      Z("z", Direction.Plane.HORIZONTAL);

      private static final Map BY_NAME = Maps.newHashMap();
      private final String name;
      private final Direction.Plane plane;

      private Axis(String name, Direction.Plane plane) {
         this.name = name;
         this.plane = plane;
      }

      @Environment(EnvType.CLIENT)
      public static Direction.Axis byName(String name) {
         return name == null ? null : (Direction.Axis)BY_NAME.get(name.toLowerCase());
      }

      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this.plane == Direction.Plane.VERTICAL;
      }

      public boolean isHorizontal() {
         return this.plane == Direction.Plane.HORIZONTAL;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean apply(Direction c_69garkogr) {
         return c_69garkogr != null && c_69garkogr.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         return this.plane;
      }

      @Override
      public String getStringRepresentation() {
         return this.name;
      }

      static {
         for(Direction.Axis var3 : values()) {
            BY_NAME.put(var3.getName().toLowerCase(), var3);
         }
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      public int getOffset() {
         return this.offset;
      }

      @Override
      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Predicate, Iterable {
      HORIZONTAL,
      VERTICAL;

      public Direction[] get() {
         switch(this) {
            case HORIZONTAL:
               return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            case VERTICAL:
               return new Direction[]{Direction.UP, Direction.DOWN};
            default:
               throw new Error("Someone's been tampering with the universe!");
         }
      }

      public Direction pick(Random random) {
         Direction[] var2 = this.get();
         return var2[random.nextInt(var2.length)];
      }

      public boolean apply(Direction c_69garkogr) {
         return c_69garkogr != null && c_69garkogr.getAxis().getPlane() == this;
      }

      @Override
      public Iterator iterator() {
         return Iterators.forArray(this.get());
      }
   }
}
