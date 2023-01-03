package net.minecraft.client.render.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Box {
   private Vertex[] vertices;
   private Quad[] faces;
   public final float minX;
   public final float minY;
   public final float minZ;
   public final float maxX;
   public final float maxY;
   public final float maxZ;
   public String id;

   public Box(ModelPart part, int textureU, int textureV, float minX, float minY, float minZ, int sizeX, int sizeY, int sizeZ, float increase) {
      this(part, textureU, textureV, minX, minY, minZ, sizeX, sizeY, sizeZ, increase, part.flipped);
   }

   public Box(ModelPart part, int textureU, int textureV, float minX, float minY, float minZ, int sizeX, int sizeY, int sizeZ, float increase, boolean flipped) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = minX + (float)sizeX;
      this.maxY = minY + (float)sizeY;
      this.maxZ = minZ + (float)sizeZ;
      this.vertices = new Vertex[8];
      this.faces = new Quad[6];
      float var12 = minX + (float)sizeX;
      float var13 = minY + (float)sizeY;
      float var14 = minZ + (float)sizeZ;
      minX -= increase;
      minY -= increase;
      minZ -= increase;
      var12 += increase;
      var13 += increase;
      var14 += increase;
      if (flipped) {
         float var15 = var12;
         var12 = minX;
         minX = var15;
      }

      Vertex var30 = new Vertex(minX, minY, minZ, 0.0F, 0.0F);
      Vertex var16 = new Vertex(var12, minY, minZ, 0.0F, 8.0F);
      Vertex var17 = new Vertex(var12, var13, minZ, 8.0F, 8.0F);
      Vertex var18 = new Vertex(minX, var13, minZ, 8.0F, 0.0F);
      Vertex var19 = new Vertex(minX, minY, var14, 0.0F, 0.0F);
      Vertex var20 = new Vertex(var12, minY, var14, 0.0F, 8.0F);
      Vertex var21 = new Vertex(var12, var13, var14, 8.0F, 8.0F);
      Vertex var22 = new Vertex(minX, var13, var14, 8.0F, 0.0F);
      this.vertices[0] = var30;
      this.vertices[1] = var16;
      this.vertices[2] = var17;
      this.vertices[3] = var18;
      this.vertices[4] = var19;
      this.vertices[5] = var20;
      this.vertices[6] = var21;
      this.vertices[7] = var22;
      this.faces[0] = new Quad(
         new Vertex[]{var20, var16, var17, var21},
         textureU + sizeZ + sizeX,
         textureV + sizeZ,
         textureU + sizeZ + sizeX + sizeZ,
         textureV + sizeZ + sizeY,
         part.textureWidth,
         part.textureHeight
      );
      this.faces[1] = new Quad(
         new Vertex[]{var30, var19, var22, var18},
         textureU,
         textureV + sizeZ,
         textureU + sizeZ,
         textureV + sizeZ + sizeY,
         part.textureWidth,
         part.textureHeight
      );
      this.faces[2] = new Quad(
         new Vertex[]{var20, var19, var30, var16},
         textureU + sizeZ,
         textureV,
         textureU + sizeZ + sizeX,
         textureV + sizeZ,
         part.textureWidth,
         part.textureHeight
      );
      this.faces[3] = new Quad(
         new Vertex[]{var17, var18, var22, var21},
         textureU + sizeZ + sizeX,
         textureV + sizeZ,
         textureU + sizeZ + sizeX + sizeX,
         textureV,
         part.textureWidth,
         part.textureHeight
      );
      this.faces[4] = new Quad(
         new Vertex[]{var16, var30, var18, var17},
         textureU + sizeZ,
         textureV + sizeZ,
         textureU + sizeZ + sizeX,
         textureV + sizeZ + sizeY,
         part.textureWidth,
         part.textureHeight
      );
      this.faces[5] = new Quad(
         new Vertex[]{var19, var20, var21, var22},
         textureU + sizeZ + sizeX + sizeZ,
         textureV + sizeZ,
         textureU + sizeZ + sizeX + sizeZ + sizeX,
         textureV + sizeZ + sizeY,
         part.textureWidth,
         part.textureHeight
      );
      if (flipped) {
         for(int var23 = 0; var23 < this.faces.length; ++var23) {
            this.faces[var23].flip();
         }
      }
   }

   public void render(BufferBuilder bufferBuilder, float scale) {
      for(int var3 = 0; var3 < this.faces.length; ++var3) {
         this.faces[var3].render(bufferBuilder, scale);
      }
   }

   public Box setId(String id) {
      this.id = id;
      return this;
   }
}
