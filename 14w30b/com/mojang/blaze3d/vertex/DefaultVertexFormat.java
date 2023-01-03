package com.mojang.blaze3d.vertex;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DefaultVertexFormat {
   public static final VertexFormat BLOCK = new VertexFormat();
   public static final VertexFormat BLOCK_NORMALS = new VertexFormat();

   static {
      BLOCK.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
      BLOCK.add(new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4));
      BLOCK.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2));
      BLOCK.add(new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2));
      BLOCK_NORMALS.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
      BLOCK_NORMALS.add(new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4));
      BLOCK_NORMALS.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2));
      BLOCK_NORMALS.add(new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3));
      BLOCK_NORMALS.add(new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1));
   }
}
