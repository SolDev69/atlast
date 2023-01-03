package com.mojang.blaze3d.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List elements = Lists.newArrayList();
   private final List offsets = Lists.newArrayList();
   private int size = 0;
   private int colorOffset = -1;
   private List offsetsUv = Lists.newArrayList();
   private int normalOffset = -1;

   public VertexFormat(VertexFormat format) {
      this();

      for(int var2 = 0; var2 < format.getElementCount(); ++var2) {
         this.add(format.getElement(var2));
      }

      this.size = format.getVertexSize();
   }

   public VertexFormat() {
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorOffset = -1;
      this.offsetsUv.clear();
      this.normalOffset = -1;
      this.size = 0;
   }

   public void add(VertexFormatElement element) {
      if (element.isPosition() && this.hasPositionElement()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
      } else {
         this.elements.add(element);
         this.offsets.add(this.size);
         element.setFormatSize(this.size);
         this.size += element.getByteSize();
         switch(element.getUsage()) {
            case NORMAL:
               this.normalOffset = element.getFormatSize();
               break;
            case COLOR:
               this.colorOffset = element.getFormatSize();
               break;
            case UV:
               this.offsetsUv.add(element.getIndex(), element.getFormatSize());
         }
      }
   }

   public boolean hasNormal() {
      return this.normalOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalOffset;
   }

   public boolean hasColor() {
      return this.colorOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorOffset;
   }

   public boolean hasUv(int index) {
      return this.offsetsUv.size() - 1 >= index;
   }

   public int getUvOffset(int index) {
      return this.offsetsUv.get(index);
   }

   @Override
   public String toString() {
      String var1 = "format: " + this.elements.size() + " elements: ";

      for(int var2 = 0; var2 < this.elements.size(); ++var2) {
         var1 = var1 + ((VertexFormatElement)this.elements.get(var2)).toString();
         if (var2 != this.elements.size() - 1) {
            var1 = var1 + " ";
         }
      }

      return var1;
   }

   private boolean hasPositionElement() {
      for(VertexFormatElement var2 : this.elements) {
         if (var2.isPosition()) {
            return true;
         }
      }

      return false;
   }

   public int getVertexSize() {
      return this.size;
   }

   public List getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int index) {
      return (VertexFormatElement)this.elements.get(index);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         VertexFormat var2 = (VertexFormat)obj;
         if (this.size != var2.size) {
            return false;
         } else if (!this.elements.equals(var2.elements)) {
            return false;
         } else {
            return this.offsets.equals(var2.offsets);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.elements.hashCode();
      var1 = 31 * var1 + this.offsets.hashCode();
      return 31 * var1 + this.size;
   }
}
