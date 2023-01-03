package com.mojang.blaze3d.vertex;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private int index;
   private int count;
   private int formatSize;

   public VertexFormatElement(int index, VertexFormatElement.Type type, VertexFormatElement.Usage usage, int count) {
      if (!this.supportsUsage(index, usage)) {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.Usage.UV;
      } else {
         this.usage = usage;
      }

      this.type = type;
      this.index = index;
      this.count = count;
      this.formatSize = 0;
   }

   public void setFormatSize(int size) {
      this.formatSize = size;
   }

   public int getFormatSize() {
      return this.formatSize;
   }

   private final boolean supportsUsage(int index, VertexFormatElement.Usage usage) {
      return index == 0 || usage == VertexFormatElement.Usage.UV;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getCount() {
      return this.count;
   }

   public final int getIndex() {
      return this.index;
   }

   @Override
   public String toString() {
      return this.count + "," + this.usage.getName() + "," + this.type.getName();
   }

   public final int getByteSize() {
      return this.type.getSize() * this.count;
   }

   public final boolean isPosition() {
      return this.usage == VertexFormatElement.Usage.POSITION;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         VertexFormatElement var2 = (VertexFormatElement)obj;
         if (this.count != var2.count) {
            return false;
         } else if (this.index != var2.index) {
            return false;
         } else if (this.formatSize != var2.formatSize) {
            return false;
         } else if (this.type != var2.type) {
            return false;
         } else {
            return this.usage == var2.usage;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = 31 * var1 + this.usage.hashCode();
      var1 = 31 * var1 + this.index;
      var1 = 31 * var1 + this.count;
      return 31 * var1 + this.formatSize;
   }

   @Environment(EnvType.CLIENT)
   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glCode;

      private Type(int size, String name, int glCode) {
         this.size = size;
         this.name = name;
         this.glCode = glCode;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlCode() {
         return this.glCode;
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum Usage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String name;

      private Usage(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}
