package net.minecraft.client.render.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Quad {
   public Vertex[] vertices;
   public int vertexCount;
   private boolean flipNormal;

   public Quad(Vertex[] vertices) {
      this.vertices = vertices;
      this.vertexCount = vertices.length;
   }

   public Quad(Vertex[] vertices, int maxX, int minY, int minX, int maxY, float textureWidth, float textureHeight) {
      this(vertices);
      float var8 = 0.0F / textureWidth;
      float var9 = 0.0F / textureHeight;
      vertices[0] = vertices[0].withTextureCoords((float)minX / textureWidth - var8, (float)minY / textureHeight + var9);
      vertices[1] = vertices[1].withTextureCoords((float)maxX / textureWidth + var8, (float)minY / textureHeight + var9);
      vertices[2] = vertices[2].withTextureCoords((float)maxX / textureWidth + var8, (float)maxY / textureHeight - var9);
      vertices[3] = vertices[3].withTextureCoords((float)minX / textureWidth - var8, (float)maxY / textureHeight - var9);
   }

   public void flip() {
      Vertex[] var1 = new Vertex[this.vertices.length];

      for(int var2 = 0; var2 < this.vertices.length; ++var2) {
         var1[var2] = this.vertices[this.vertices.length - var2 - 1];
      }

      this.vertices = var1;
   }

   public void render(BufferBuilder bufferBuilder, float scale) {
      Vec3d var3 = this.vertices[1].pos.subtractFrom(this.vertices[0].pos);
      Vec3d var4 = this.vertices[1].pos.subtractFrom(this.vertices[2].pos);
      Vec3d var5 = var4.cross(var3).normalize();
      bufferBuilder.start();
      if (this.flipNormal) {
         bufferBuilder.normal(-((float)var5.x), -((float)var5.y), -((float)var5.z));
      } else {
         bufferBuilder.normal((float)var5.x, (float)var5.y, (float)var5.z);
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         Vertex var7 = this.vertices[var6];
         bufferBuilder.vertex(var7.pos.x * (double)scale, var7.pos.y * (double)scale, var7.pos.z * (double)scale, (double)var7.u, (double)var7.v);
      }

      Tessellator.getInstance().end();
   }
}
