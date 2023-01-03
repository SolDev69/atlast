package net.minecraft.client.render.model.block;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.minecraft.SharedConstants;
import net.minecraft.client.render.block.BlockFace;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.client.resource.model.ModelRotation;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FaceBakery {
   private static final double SCALE_22_5_DEGREES = 1.0 / Math.cos(Math.PI / 8) - 1.0;
   private static final double SCALE_45_DEGREES = 1.0 / Math.cos(Math.PI / 4) - 1.0;

   public BakedQuad bake(
      Vector3f vector3f,
      Vector3f vector3f2,
      BlockElementFace c_36wzeheeh,
      TextureAtlasSprite c_23kgsbsml,
      Direction c_69garkogr,
      ModelRotation c_00puhujbz,
      BlockElementRotation c_16uwxdqhy,
      boolean bl,
      boolean bl2
   ) {
      int[] var10 = this.packVertices(c_36wzeheeh, c_23kgsbsml, c_69garkogr, this.getShape(vector3f, vector3f2), c_00puhujbz, c_16uwxdqhy, bl, bl2);
      Direction var11 = getFacing(var10);
      if (bl) {
         this.calculateTextureCoords(var10, var11, c_36wzeheeh.textureCoords, c_23kgsbsml);
      }

      if (c_16uwxdqhy == null) {
         this.calculateWinding(var10, var11);
      }

      return new BakedQuad(var10, c_36wzeheeh.tintIndex, var11);
   }

   private int[] packVertices(
      BlockElementFace elementFace,
      TextureAtlasSprite blockSprite,
      Direction face,
      float[] shape,
      ModelRotation modelRotation,
      BlockElementRotation elementRotation,
      boolean uvLock,
      boolean shade
   ) {
      int[] var9 = new int[28];

      for(int var10 = 0; var10 < 4; ++var10) {
         this.bakeVertex(var9, var10, face, elementFace, shape, blockSprite, modelRotation, elementRotation, uvLock, shade);
      }

      return var9;
   }

   private int getShadeValue(Direction face) {
      float var2 = this.getShade(face);
      int var3 = MathHelper.clamp((int)(var2 * 255.0F), 0, 255);
      return 0xFF000000 | var3 << 16 | var3 << 8 | var3;
   }

   private float getShade(Direction face) {
      switch(face) {
         case DOWN:
            return 0.5F;
         case UP:
            return 1.0F;
         case NORTH:
         case SOUTH:
            return 0.8F;
         case WEST:
         case EAST:
            return 0.6F;
         default:
            return 1.0F;
      }
   }

   private float[] getShape(Vector3f vector3f, Vector3f vector3f2) {
      float[] var3 = new float[Direction.values().length];
      var3[SharedConstants.f_35cytruuz] = vector3f.x / 16.0F;
      var3[SharedConstants.f_65hjhkibg] = vector3f.y / 16.0F;
      var3[SharedConstants.f_80ionvmpr] = vector3f.z / 16.0F;
      var3[SharedConstants.f_12bhivnfu] = vector3f2.x / 16.0F;
      var3[SharedConstants.f_45rwuiagn] = vector3f2.y / 16.0F;
      var3[SharedConstants.f_11horuacr] = vector3f2.z / 16.0F;
      return var3;
   }

   private void bakeVertex(
      int[] vertex,
      int index,
      Direction face,
      BlockElementFace elementFace,
      float[] shape,
      TextureAtlasSprite blockAtlas,
      ModelRotation modelRotation,
      BlockElementRotation elementRotation,
      boolean uvLock,
      boolean shade
   ) {
      Direction var11 = modelRotation.apply(face);
      int var12 = shade ? this.getShadeValue(var11) : -1;
      BlockFace.Vertex var13 = BlockFace.byDirection(face).getVertex(index);
      Vector3d var14 = new Vector3d((double)shape[var13.x], (double)shape[var13.y], (double)shape[var13.z]);
      this.applyElementRotation(var14, elementRotation);
      int var15 = this.applyModelRotation(var14, face, index, modelRotation, uvLock);
      this.fillVertex(vertex, var15, index, var14, var12, blockAtlas, elementFace.textureCoords);
   }

   private void fillVertex(int[] is, int i, int j, Vector3d vector3d, int k, TextureAtlasSprite c_23kgsbsml, BlockElementTexture c_38alyrbws) {
      int var8 = i * 7;
      is[var8] = Float.floatToRawIntBits((float)vector3d.x);
      is[var8 + 1] = Float.floatToRawIntBits((float)vector3d.y);
      is[var8 + 2] = Float.floatToRawIntBits((float)vector3d.z);
      is[var8 + 3] = k;
      is[var8 + 4] = Float.floatToRawIntBits(c_23kgsbsml.getU((double)c_38alyrbws.getU(j)));
      is[var8 + 4 + 1] = Float.floatToRawIntBits(c_23kgsbsml.getV((double)c_38alyrbws.getV(j)));
   }

   private void applyElementRotation(Vector3d vector3d, BlockElementRotation c_16uwxdqhy) {
      if (c_16uwxdqhy != null) {
         Matrix4d var3 = this.identityMatrix();
         Vector3d var4 = new Vector3d(0.0, 0.0, 0.0);
         switch(c_16uwxdqhy.axis) {
            case X:
               var3.mul(this.m_36dhwbbbl(new AxisAngle4d(1.0, 0.0, 0.0, (double)c_16uwxdqhy.angle * (Math.PI / 180.0))));
               var4.set(0.0, 1.0, 1.0);
               break;
            case Y:
               var3.mul(this.m_36dhwbbbl(new AxisAngle4d(0.0, 1.0, 0.0, (double)c_16uwxdqhy.angle * (Math.PI / 180.0))));
               var4.set(1.0, 0.0, 1.0);
               break;
            case Z:
               var3.mul(this.m_36dhwbbbl(new AxisAngle4d(0.0, 0.0, 1.0, (double)c_16uwxdqhy.angle * (Math.PI / 180.0))));
               var4.set(1.0, 1.0, 0.0);
         }

         if (c_16uwxdqhy.rescale) {
            if (Math.abs(c_16uwxdqhy.angle) == 22.5F) {
               var4.scale(SCALE_22_5_DEGREES);
            } else {
               var4.scale(SCALE_45_DEGREES);
            }

            var4.add(new Vector3d(1.0, 1.0, 1.0));
         } else {
            var4.set(new Vector3d(1.0, 1.0, 1.0));
         }

         this.rotateVertex(vector3d, new Vector3d(c_16uwxdqhy.origin), var3, var4);
      }
   }

   public int applyModelRotation(Vector3d vector3d, Direction c_69garkogr, int i, ModelRotation c_00puhujbz, boolean bl) {
      if (c_00puhujbz == ModelRotation.X0_Y0) {
         return i;
      } else {
         this.rotateVertex(vector3d, new Vector3d(0.5, 0.5, 0.5), c_00puhujbz.getRotation(), new Vector3d(1.0, 1.0, 1.0));
         return c_00puhujbz.apply(c_69garkogr, i);
      }
   }

   private void rotateVertex(Vector3d vector3d, Vector3d vector3d2, Matrix4d matrix4d, Vector3d vector3d3) {
      vector3d.sub(vector3d2);
      matrix4d.transform(vector3d);
      vector3d.x *= vector3d3.x;
      vector3d.y *= vector3d3.y;
      vector3d.z *= vector3d3.z;
      vector3d.add(vector3d2);
   }

   private Matrix4d m_36dhwbbbl(AxisAngle4d axisAngle4d) {
      Matrix4d var2 = this.identityMatrix();
      var2.setRotation(axisAngle4d);
      return var2;
   }

   private Matrix4d identityMatrix() {
      Matrix4d var1 = new Matrix4d();
      var1.setIdentity();
      return var1;
   }

   public static Direction getFacing(int[] vertices) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(vertices[0]), Float.intBitsToFloat(vertices[1]), Float.intBitsToFloat(vertices[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(vertices[7]), Float.intBitsToFloat(vertices[8]), Float.intBitsToFloat(vertices[9]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(vertices[14]), Float.intBitsToFloat(vertices[15]), Float.intBitsToFloat(vertices[16]));
      Vector3f var4 = new Vector3f();
      Vector3f var5 = new Vector3f();
      Vector3f var6 = new Vector3f();
      var4.sub(var1, var2);
      var5.sub(var3, var2);
      var6.cross(var5, var4);
      var6.normalize();
      Direction var7 = null;
      float var8 = 0.0F;

      for(Direction var12 : Direction.values()) {
         Vec3i var13 = var12.getNormal();
         Vector3f var14 = new Vector3f((float)var13.getX(), (float)var13.getY(), (float)var13.getZ());
         float var15 = var6.dot(var14);
         if (var15 >= 0.0F && var15 > var8) {
            var8 = var15;
            var7 = var12;
         }
      }

      return var7 == null ? Direction.UP : var7;
   }

   public void calculateTextureCoords(int[] vertices, Direction face, BlockElementTexture textureCoords, TextureAtlasSprite blockAtlas) {
      for(int var5 = 0; var5 < 4; ++var5) {
         this.calculateTextureCoords(var5, vertices, face, textureCoords, blockAtlas);
      }
   }

   private void calculateWinding(int[] vertices, Direction facing) {
      int[] var3 = new int[vertices.length];
      System.arraycopy(vertices, 0, var3, 0, vertices.length);
      float[] var4 = new float[Direction.values().length];
      var4[SharedConstants.f_35cytruuz] = 999.0F;
      var4[SharedConstants.f_65hjhkibg] = 999.0F;
      var4[SharedConstants.f_80ionvmpr] = 999.0F;
      var4[SharedConstants.f_12bhivnfu] = -999.0F;
      var4[SharedConstants.f_45rwuiagn] = -999.0F;
      var4[SharedConstants.f_11horuacr] = -999.0F;

      for(int var5 = 0; var5 < 4; ++var5) {
         int var6 = 7 * var5;
         float var7 = Float.intBitsToFloat(var3[var6]);
         float var8 = Float.intBitsToFloat(var3[var6 + 1]);
         float var9 = Float.intBitsToFloat(var3[var6 + 2]);
         if (var7 < var4[SharedConstants.f_35cytruuz]) {
            var4[SharedConstants.f_35cytruuz] = var7;
         }

         if (var8 < var4[SharedConstants.f_65hjhkibg]) {
            var4[SharedConstants.f_65hjhkibg] = var8;
         }

         if (var9 < var4[SharedConstants.f_80ionvmpr]) {
            var4[SharedConstants.f_80ionvmpr] = var9;
         }

         if (var7 > var4[SharedConstants.f_12bhivnfu]) {
            var4[SharedConstants.f_12bhivnfu] = var7;
         }

         if (var8 > var4[SharedConstants.f_45rwuiagn]) {
            var4[SharedConstants.f_45rwuiagn] = var8;
         }

         if (var9 > var4[SharedConstants.f_11horuacr]) {
            var4[SharedConstants.f_11horuacr] = var9;
         }
      }

      BlockFace var17 = BlockFace.byDirection(facing);

      for(int var18 = 0; var18 < 4; ++var18) {
         int var19 = 7 * var18;
         BlockFace.Vertex var20 = var17.getVertex(var18);
         float var21 = var4[var20.x];
         float var10 = var4[var20.y];
         float var11 = var4[var20.z];
         vertices[var19] = Float.floatToRawIntBits(var21);
         vertices[var19 + 1] = Float.floatToRawIntBits(var10);
         vertices[var19 + 2] = Float.floatToRawIntBits(var11);

         for(int var12 = 0; var12 < 4; ++var12) {
            int var13 = 7 * var12;
            float var14 = Float.intBitsToFloat(var3[var13]);
            float var15 = Float.intBitsToFloat(var3[var13 + 1]);
            float var16 = Float.intBitsToFloat(var3[var13 + 2]);
            if (MathHelper.equalsApproximate(var21, var14) && MathHelper.equalsApproximate(var10, var15) && MathHelper.equalsApproximate(var11, var16)) {
               vertices[var19 + 4] = var3[var13 + 4];
               vertices[var19 + 4 + 1] = var3[var13 + 4 + 1];
            }
         }
      }
   }

   private void calculateTextureCoords(int index, int[] vertices, Direction face, BlockElementTexture textureCoords, TextureAtlasSprite blockAtlas) {
      int var6 = 7 * index;
      float var7 = Float.intBitsToFloat(vertices[var6]);
      float var8 = Float.intBitsToFloat(vertices[var6 + 1]);
      float var9 = Float.intBitsToFloat(vertices[var6 + 2]);
      float var10 = 0.0F;
      float var11 = 0.0F;
      switch(face) {
         case DOWN:
            var10 = var7 * 16.0F;
            var11 = (1.0F - var9) * 16.0F;
            break;
         case UP:
            var10 = var7 * 16.0F;
            var11 = var9 * 16.0F;
            break;
         case NORTH:
            var10 = (1.0F - var7) * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case SOUTH:
            var10 = var7 * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case WEST:
            var10 = var9 * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
            break;
         case EAST:
            var10 = (1.0F - var9) * 16.0F;
            var11 = (1.0F - var8) * 16.0F;
      }

      int var12 = textureCoords.reverseIndex(index) * 7;
      vertices[var12 + 4] = Float.floatToRawIntBits(blockAtlas.getU((double)var10));
      vertices[var12 + 4 + 1] = Float.floatToRawIntBits(blockAtlas.getV((double)var11));
   }
}
