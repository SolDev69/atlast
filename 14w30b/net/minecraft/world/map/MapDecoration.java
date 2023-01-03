package net.minecraft.world.map;

public class MapDecoration {
   private byte type;
   private byte x;
   private byte y;
   private byte rotation;

   public MapDecoration(byte type, byte x, byte y, byte rotation) {
      this.type = type;
      this.x = x;
      this.y = y;
      this.rotation = rotation;
   }

   public MapDecoration(MapDecoration decoration) {
      this.type = decoration.type;
      this.x = decoration.x;
      this.y = decoration.y;
      this.rotation = decoration.rotation;
   }

   public byte getType() {
      return this.type;
   }

   public byte getX() {
      return this.x;
   }

   public byte getY() {
      return this.y;
   }

   public byte getRotation() {
      return this.rotation;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof MapDecoration)) {
         return false;
      } else {
         MapDecoration var2 = (MapDecoration)obj;
         if (this.type != var2.type) {
            return false;
         } else if (this.rotation != var2.rotation) {
            return false;
         } else if (this.x != var2.x) {
            return false;
         } else {
            return this.y == var2.y;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type;
      var1 = 31 * var1 + this.x;
      var1 = 31 * var1 + this.y;
      return 31 * var1 + this.rotation;
   }
}
