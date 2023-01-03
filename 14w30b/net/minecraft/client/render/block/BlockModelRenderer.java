package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer {
   public boolean render(IWorld world, BakedModel model, BlockState state, BlockPos pos, BufferBuilder bufferBuilder) {
      Block var6 = state.getBlock();
      var6.updateShape(world, pos);
      return this.render(world, model, state, pos, bufferBuilder, true);
   }

   public boolean render(IWorld world, BakedModel model, BlockState state, BlockPos pos, BufferBuilder bufferBuilder, boolean checkShouldRenderFace) {
      boolean var7 = MinecraftClient.isAmbientOcclusionEnabled() && state.getBlock().getLightLevel() == 0 && model.useAmbientOcclusion();

      try {
         Block var8 = state.getBlock();
         return var7
            ? this.renderSmooth(world, model, var8, pos, bufferBuilder, checkShouldRenderFace)
            : this.renderFlat(world, model, var8, pos, bufferBuilder, checkShouldRenderFace);
      } catch (Throwable var11) {
         CrashReport var9 = CrashReport.of(var11, "Tesselating block model");
         CashReportCategory var10 = var9.addCategory("Block model being tesselated");
         CashReportCategory.addBlockDetails(var10, pos, state);
         var10.add("Using AO", var7);
         throw new CrashException(var9);
      }
   }

   public boolean renderSmooth(IWorld world, BakedModel model, Block block, BlockPos pos, BufferBuilder bufferBuilder, boolean checkShouldRenderFace) {
      boolean var7 = false;
      bufferBuilder.brightness(983055);
      float[] var8 = new float[Direction.values().length * 2];
      BitSet var9 = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace var10 = new BlockModelRenderer.AmbientOcclusionFace();

      for(Direction var14 : Direction.values()) {
         List var15 = model.getQuads(var14);
         if (!var15.isEmpty()) {
            BlockPos var16 = pos.offset(var14);
            if (!checkShouldRenderFace || block.shouldRenderFace(world, var16, var14)) {
               this.renderFaceSmooth(world, block, pos, bufferBuilder, var15, var8, var9, var10);
               var7 = true;
            }
         }
      }

      List var17 = model.getQuads();
      if (var17.size() > 0) {
         this.renderFaceSmooth(world, block, pos, bufferBuilder, var17, var8, var9, var10);
         var7 = true;
      }

      return var7;
   }

   public boolean renderFlat(IWorld world, BakedModel model, Block block, BlockPos pos, BufferBuilder bufferBuilder, boolean checkShouldRenderFace) {
      boolean var7 = false;
      BitSet var8 = new BitSet(3);

      for(Direction var12 : Direction.values()) {
         List var13 = model.getQuads(var12);
         if (!var13.isEmpty()) {
            BlockPos var14 = pos.offset(var12);
            if (!checkShouldRenderFace || block.shouldRenderFace(world, var14, var12)) {
               int var15 = block.getLightColor(world, var14);
               this.renderFaceFlat(world, block, pos, var12, var15, false, bufferBuilder, var13, var8);
               var7 = true;
            }
         }
      }

      List var16 = model.getQuads();
      if (var16.size() > 0) {
         this.renderFaceFlat(world, block, pos, null, -1, true, bufferBuilder, var16, var8);
         var7 = true;
      }

      return var7;
   }

   private void renderFaceSmooth(
      IWorld world,
      Block block,
      BlockPos pos,
      BufferBuilder bufferBuilder,
      List quads,
      float[] faceShape,
      BitSet shapeState,
      BlockModelRenderer.AmbientOcclusionFace face
   ) {
      double var9 = (double)pos.getX();
      double var11 = (double)pos.getY();
      double var13 = (double)pos.getZ();
      Block.OffsetType var15 = block.getOffsetType();
      if (var15 != Block.OffsetType.NONE) {
         long var16 = MathHelper.hashCode(pos);
         var9 += ((double)((float)(var16 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
         var13 += ((double)((float)(var16 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
         if (var15 == Block.OffsetType.XYZ) {
            var11 += ((double)((float)(var16 >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
         }
      }

      for(BakedQuad var17 : quads) {
         this.computeFaceShape(block, var17.getVertices(), var17.getFace(), faceShape, shapeState);
         face.compute(world, block, pos, var17.getFace(), faceShape, shapeState);
         bufferBuilder.vertices(var17.getVertices());
         bufferBuilder.position(face.lightMap[0], face.lightMap[1], face.lightMap[2], face.lightMap[3]);
         if (var17.hasTint()) {
            int var18 = block.getColor(world, pos, var17.getTintIndex());
            if (GameRenderer.anaglyphEnabled) {
               var18 = TextureUtil.getAnaglyphColor(var18);
            }

            float var19 = (float)(var18 >> 16 & 0xFF) / 255.0F;
            float var20 = (float)(var18 >> 8 & 0xFF) / 255.0F;
            float var21 = (float)(var18 & 0xFF) / 255.0F;
            bufferBuilder.multiplyColor(face.brightness[0] * var19, face.brightness[0] * var20, face.brightness[0] * var21, 4);
            bufferBuilder.multiplyColor(face.brightness[1] * var19, face.brightness[1] * var20, face.brightness[1] * var21, 3);
            bufferBuilder.multiplyColor(face.brightness[2] * var19, face.brightness[2] * var20, face.brightness[2] * var21, 2);
            bufferBuilder.multiplyColor(face.brightness[3] * var19, face.brightness[3] * var20, face.brightness[3] * var21, 1);
         } else {
            bufferBuilder.multiplyColor(face.brightness[0], face.brightness[0], face.brightness[0], 4);
            bufferBuilder.multiplyColor(face.brightness[1], face.brightness[1], face.brightness[1], 3);
            bufferBuilder.multiplyColor(face.brightness[2], face.brightness[2], face.brightness[2], 2);
            bufferBuilder.multiplyColor(face.brightness[3], face.brightness[3], face.brightness[3], 1);
         }

         bufferBuilder.postPosition(var9, var11, var13);
      }
   }

   private void computeFaceShape(Block block, int[] vertices, Direction face, float[] faceShape, BitSet shapeState) {
      float var6 = 32.0F;
      float var7 = 32.0F;
      float var8 = 32.0F;
      float var9 = -32.0F;
      float var10 = -32.0F;
      float var11 = -32.0F;

      for(int var12 = 0; var12 < 4; ++var12) {
         float var13 = Float.intBitsToFloat(vertices[var12 * 7]);
         float var14 = Float.intBitsToFloat(vertices[var12 * 7 + 1]);
         float var15 = Float.intBitsToFloat(vertices[var12 * 7 + 2]);
         var6 = Math.min(var6, var13);
         var7 = Math.min(var7, var14);
         var8 = Math.min(var8, var15);
         var9 = Math.max(var9, var13);
         var10 = Math.max(var10, var14);
         var11 = Math.max(var11, var15);
      }

      if (faceShape != null) {
         faceShape[Direction.WEST.getId()] = var6;
         faceShape[Direction.EAST.getId()] = var9;
         faceShape[Direction.DOWN.getId()] = var7;
         faceShape[Direction.UP.getId()] = var10;
         faceShape[Direction.NORTH.getId()] = var8;
         faceShape[Direction.SOUTH.getId()] = var11;
         faceShape[Direction.WEST.getId() + Direction.values().length] = 1.0F - var6;
         faceShape[Direction.EAST.getId() + Direction.values().length] = 1.0F - var9;
         faceShape[Direction.DOWN.getId() + Direction.values().length] = 1.0F - var7;
         faceShape[Direction.UP.getId() + Direction.values().length] = 1.0F - var10;
         faceShape[Direction.NORTH.getId() + Direction.values().length] = 1.0F - var8;
         faceShape[Direction.SOUTH.getId() + Direction.values().length] = 1.0F - var11;
      }

      float var16 = 1.0E-4F;
      float var17 = 0.9999F;
      switch(face) {
         case DOWN:
            shapeState.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
            shapeState.set(0, (var7 < 1.0E-4F || block.isFullCube()) && var7 == var10);
            break;
         case UP:
            shapeState.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
            shapeState.set(0, (var10 > 0.9999F || block.isFullCube()) && var7 == var10);
            break;
         case NORTH:
            shapeState.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
            shapeState.set(0, (var8 < 1.0E-4F || block.isFullCube()) && var8 == var11);
            break;
         case SOUTH:
            shapeState.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
            shapeState.set(0, (var11 > 0.9999F || block.isFullCube()) && var8 == var11);
            break;
         case WEST:
            shapeState.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
            shapeState.set(0, (var6 < 1.0E-4F || block.isFullCube()) && var6 == var9);
            break;
         case EAST:
            shapeState.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
            shapeState.set(0, (var9 > 0.9999F || block.isFullCube()) && var6 == var9);
      }
   }

   private void renderFaceFlat(
      IWorld world, Block block, BlockPos pos, Direction face, int lightColor, boolean computeShape, BufferBuilder bufferBuilder, List quads, BitSet shapeState
   ) {
      double var10 = (double)pos.getX();
      double var12 = (double)pos.getY();
      double var14 = (double)pos.getZ();
      Block.OffsetType var16 = block.getOffsetType();
      if (var16 != Block.OffsetType.NONE) {
         int var17 = pos.getX();
         int var18 = pos.getZ();
         long var19 = (long)(var17 * 3129871) ^ (long)var18 * 116129781L;
         var19 = var19 * var19 * 42317861L + var19 * 11L;
         var10 += ((double)((float)(var19 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
         var14 += ((double)((float)(var19 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
         if (var16 == Block.OffsetType.XYZ) {
            var12 += ((double)((float)(var19 >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
         }
      }

      for(BakedQuad var24 : quads) {
         if (computeShape) {
            this.computeFaceShape(block, var24.getVertices(), var24.getFace(), null, shapeState);
            lightColor = shapeState.get(0) ? block.getLightColor(world, pos.offset(var24.getFace())) : block.getLightColor(world, pos);
         }

         bufferBuilder.vertices(var24.getVertices());
         bufferBuilder.position(lightColor, lightColor, lightColor, lightColor);
         if (var24.hasTint()) {
            int var26 = block.getColor(world, pos, var24.getTintIndex());
            if (GameRenderer.anaglyphEnabled) {
               var26 = TextureUtil.getAnaglyphColor(var26);
            }

            float var20 = (float)(var26 >> 16 & 0xFF) / 255.0F;
            float var21 = (float)(var26 >> 8 & 0xFF) / 255.0F;
            float var22 = (float)(var26 & 0xFF) / 255.0F;
            bufferBuilder.multiplyColor(var20, var21, var22, 4);
            bufferBuilder.multiplyColor(var20, var21, var22, 3);
            bufferBuilder.multiplyColor(var20, var21, var22, 2);
            bufferBuilder.multiplyColor(var20, var21, var22, 1);
         }

         bufferBuilder.postPosition(var10, var12, var14);
      }
   }

   public void render(BakedModel model, float brightness, float r, float g, float b) {
      for(Direction var9 : Direction.values()) {
         this.renderQuads(brightness, r, g, b, model.getQuads(var9));
      }

      this.renderQuads(brightness, r, g, b, model.getQuads());
   }

   public void render(BakedModel c_51yvnkdmo, Block c_68zcrzyxg, int i, float f, boolean bl) {
      c_68zcrzyxg.setBlockItemBounds();
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int var6 = c_68zcrzyxg.m_43rfjsapl(i);
      if (var6 != -1) {
         i = var6;
      }

      int var7 = c_68zcrzyxg.getColor(i);
      if (GameRenderer.anaglyphEnabled) {
         var7 = TextureUtil.getAnaglyphColor(var7);
      }

      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      if (!bl) {
         GlStateManager.color4f(f, f, f, 1.0F);
      }

      this.render(c_51yvnkdmo, f, var8, var9, var10);
   }

   private void renderQuads(float brightness, float r, float g, float b, List quads) {
      Tessellator var6 = Tessellator.getInstance();
      BufferBuilder var7 = var6.getBufferBuilder();

      for(BakedQuad var9 : quads) {
         var7.start();
         var7.format(DefaultVertexFormat.BLOCK_NORMALS);
         var7.vertices(var9.getVertices());
         if (var9.hasTint()) {
            var7.setColor(r * brightness, g * brightness, b * brightness, 1.0F, 4);
            var7.setColor(r * brightness, g * brightness, b * brightness, 1.0F, 3);
            var7.setColor(r * brightness, g * brightness, b * brightness, 1.0F, 2);
            var7.setColor(r * brightness, g * brightness, b * brightness, 1.0F, 1);
         } else {
            var7.setColor(brightness, brightness, brightness, 1.0F, 4);
            var7.setColor(brightness, brightness, brightness, 1.0F, 3);
            var7.setColor(brightness, brightness, brightness, 1.0F, 2);
            var7.setColor(brightness, brightness, brightness, 1.0F, 1);
         }

         Vec3i var10 = var9.getFace().getNormal();
         var7.postNormal((float)var10.getX(), (float)var10.getY(), (float)var10.getZ());
         var6.end();
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum AdjacencyData {
      DOWN(
         new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
         0.5F,
         false,
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0]
      ),
      UP(
         new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
         1.0F,
         false,
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0],
         new BlockModelRenderer.SizeData[0]
      ),
      NORTH(
         new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},
         0.8F,
         true,
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_WEST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_EAST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_EAST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_WEST
         }
      ),
      SOUTH(
         new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP},
         0.8F,
         true,
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.WEST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_WEST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.WEST,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.WEST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.EAST
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_EAST,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.EAST,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.EAST
         }
      ),
      WEST(
         new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.SOUTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.SOUTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.NORTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.NORTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.NORTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.NORTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.SOUTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.SOUTH
         }
      ),
      EAST(
         new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.SOUTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.SOUTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.NORTH,
            BlockModelRenderer.SizeData.FLIP_DOWN,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.DOWN,
            BlockModelRenderer.SizeData.NORTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.NORTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_NORTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.NORTH
         },
         new BlockModelRenderer.SizeData[]{
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.SOUTH,
            BlockModelRenderer.SizeData.FLIP_UP,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.FLIP_SOUTH,
            BlockModelRenderer.SizeData.UP,
            BlockModelRenderer.SizeData.SOUTH
         }
      );

      protected final Direction[] corners;
      protected final float weight;
      protected final boolean nonCubicWeight;
      protected final BlockModelRenderer.SizeData[] vertex1Weights;
      protected final BlockModelRenderer.SizeData[] vertex2Weights;
      protected final BlockModelRenderer.SizeData[] vertex3Weights;
      protected final BlockModelRenderer.SizeData[] vertex4Weights;
      private static final BlockModelRenderer.AdjacencyData[] ALL = new BlockModelRenderer.AdjacencyData[6];

      private AdjacencyData(
         Direction[] corners,
         float weight,
         boolean nonCubicWeight,
         BlockModelRenderer.SizeData[] vertex1Weights,
         BlockModelRenderer.SizeData[] vertex2Weights,
         BlockModelRenderer.SizeData[] vertex3Weights,
         BlockModelRenderer.SizeData[] vertex4Weights
      ) {
         this.corners = corners;
         this.weight = weight;
         this.nonCubicWeight = nonCubicWeight;
         this.vertex1Weights = vertex1Weights;
         this.vertex2Weights = vertex2Weights;
         this.vertex3Weights = vertex3Weights;
         this.vertex4Weights = vertex4Weights;
      }

      public static BlockModelRenderer.AdjacencyData byDirection(Direction dir) {
         return ALL[dir.getId()];
      }

      static {
         ALL[Direction.DOWN.getId()] = DOWN;
         ALL[Direction.UP.getId()] = UP;
         ALL[Direction.NORTH.getId()] = NORTH;
         ALL[Direction.SOUTH.getId()] = SOUTH;
         ALL[Direction.WEST.getId()] = WEST;
         ALL[Direction.EAST.getId()] = EAST;
      }
   }

   @Environment(EnvType.CLIENT)
   class AmbientOcclusionFace {
      private final float[] brightness = new float[4];
      private final int[] lightMap = new int[4];

      public AmbientOcclusionFace() {
      }

      public void compute(IWorld world, Block block, BlockPos pos, Direction face, float[] faceShape, BitSet shapeState) {
         BlockPos var7 = shapeState.get(0) ? pos.offset(face) : pos;
         BlockModelRenderer.AdjacencyData var8 = BlockModelRenderer.AdjacencyData.byDirection(face);
         BlockPos var9 = var7.offset(var8.corners[0]);
         BlockPos var10 = var7.offset(var8.corners[1]);
         BlockPos var11 = var7.offset(var8.corners[2]);
         BlockPos var12 = var7.offset(var8.corners[3]);
         int var13 = block.getLightColor(world, var9);
         int var14 = block.getLightColor(world, var10);
         int var15 = block.getLightColor(world, var11);
         int var16 = block.getLightColor(world, var12);
         float var17 = world.getBlockState(var9).getBlock().getAmbientOcclusionLight();
         float var18 = world.getBlockState(var10).getBlock().getAmbientOcclusionLight();
         float var19 = world.getBlockState(var11).getBlock().getAmbientOcclusionLight();
         float var20 = world.getBlockState(var12).getBlock().getAmbientOcclusionLight();
         boolean var21 = world.getBlockState(var9.offset(face)).getBlock().isTranslucent();
         boolean var22 = world.getBlockState(var10.offset(face)).getBlock().isTranslucent();
         boolean var23 = world.getBlockState(var11.offset(face)).getBlock().isTranslucent();
         boolean var24 = world.getBlockState(var12.offset(face)).getBlock().isTranslucent();
         float var25;
         int var29;
         if (!var23 && !var21) {
            var25 = var17;
            var29 = var13;
         } else {
            BlockPos var33 = var9.offset(var8.corners[2]);
            var25 = world.getBlockState(var33).getBlock().getAmbientOcclusionLight();
            var29 = block.getLightColor(world, var33);
         }

         float var26;
         int var30;
         if (!var24 && !var21) {
            var26 = var17;
            var30 = var13;
         } else {
            BlockPos var60 = var9.offset(var8.corners[3]);
            var26 = world.getBlockState(var60).getBlock().getAmbientOcclusionLight();
            var30 = block.getLightColor(world, var60);
         }

         float var27;
         int var31;
         if (!var23 && !var22) {
            var27 = var18;
            var31 = var14;
         } else {
            BlockPos var61 = var10.offset(var8.corners[2]);
            var27 = world.getBlockState(var61).getBlock().getAmbientOcclusionLight();
            var31 = block.getLightColor(world, var61);
         }

         float var28;
         int var32;
         if (!var24 && !var22) {
            var28 = var18;
            var32 = var14;
         } else {
            BlockPos var62 = var10.offset(var8.corners[3]);
            var28 = world.getBlockState(var62).getBlock().getAmbientOcclusionLight();
            var32 = block.getLightColor(world, var62);
         }

         int var63 = block.getLightColor(world, pos);
         if (shapeState.get(0) || !world.getBlockState(pos.offset(face)).getBlock().isOpaqueCube()) {
            var63 = block.getLightColor(world, pos.offset(face));
         }

         float var34 = shapeState.get(0)
            ? world.getBlockState(var7).getBlock().getAmbientOcclusionLight()
            : world.getBlockState(pos).getBlock().getAmbientOcclusionLight();
         BlockModelRenderer.AmbientVertexRemap var35 = BlockModelRenderer.AmbientVertexRemap.byDirection(face);
         if (shapeState.get(1) && var8.nonCubicWeight) {
            float var64 = (var20 + var17 + var26 + var34) * 0.25F;
            float var65 = (var19 + var17 + var25 + var34) * 0.25F;
            float var66 = (var19 + var18 + var27 + var34) * 0.25F;
            float var67 = (var20 + var18 + var28 + var34) * 0.25F;
            float var40 = faceShape[var8.vertex1Weights[0].shape] * faceShape[var8.vertex1Weights[1].shape];
            float var41 = faceShape[var8.vertex1Weights[2].shape] * faceShape[var8.vertex1Weights[3].shape];
            float var42 = faceShape[var8.vertex1Weights[4].shape] * faceShape[var8.vertex1Weights[5].shape];
            float var43 = faceShape[var8.vertex1Weights[6].shape] * faceShape[var8.vertex1Weights[7].shape];
            float var44 = faceShape[var8.vertex2Weights[0].shape] * faceShape[var8.vertex2Weights[1].shape];
            float var45 = faceShape[var8.vertex2Weights[2].shape] * faceShape[var8.vertex2Weights[3].shape];
            float var46 = faceShape[var8.vertex2Weights[4].shape] * faceShape[var8.vertex2Weights[5].shape];
            float var47 = faceShape[var8.vertex2Weights[6].shape] * faceShape[var8.vertex2Weights[7].shape];
            float var48 = faceShape[var8.vertex3Weights[0].shape] * faceShape[var8.vertex3Weights[1].shape];
            float var49 = faceShape[var8.vertex3Weights[2].shape] * faceShape[var8.vertex3Weights[3].shape];
            float var50 = faceShape[var8.vertex3Weights[4].shape] * faceShape[var8.vertex3Weights[5].shape];
            float var51 = faceShape[var8.vertex3Weights[6].shape] * faceShape[var8.vertex3Weights[7].shape];
            float var52 = faceShape[var8.vertex4Weights[0].shape] * faceShape[var8.vertex4Weights[1].shape];
            float var53 = faceShape[var8.vertex4Weights[2].shape] * faceShape[var8.vertex4Weights[3].shape];
            float var54 = faceShape[var8.vertex4Weights[4].shape] * faceShape[var8.vertex4Weights[5].shape];
            float var55 = faceShape[var8.vertex4Weights[6].shape] * faceShape[var8.vertex4Weights[7].shape];
            this.brightness[var35.vertex1] = var64 * var40 + var65 * var41 + var66 * var42 + var67 * var43;
            this.brightness[var35.vertex2] = var64 * var44 + var65 * var45 + var66 * var46 + var67 * var47;
            this.brightness[var35.vertex3] = var64 * var48 + var65 * var49 + var66 * var50 + var67 * var51;
            this.brightness[var35.vertex4] = var64 * var52 + var65 * var53 + var66 * var54 + var67 * var55;
            int var56 = this.blend(var16, var13, var30, var63);
            int var57 = this.blend(var15, var13, var29, var63);
            int var58 = this.blend(var15, var14, var31, var63);
            int var59 = this.blend(var16, var14, var32, var63);
            this.lightMap[var35.vertex1] = this.blend(var56, var57, var58, var59, var40, var41, var42, var43);
            this.lightMap[var35.vertex2] = this.blend(var56, var57, var58, var59, var44, var45, var46, var47);
            this.lightMap[var35.vertex3] = this.blend(var56, var57, var58, var59, var48, var49, var50, var51);
            this.lightMap[var35.vertex4] = this.blend(var56, var57, var58, var59, var52, var53, var54, var55);
         } else {
            float var36 = (var20 + var17 + var26 + var34) * 0.25F;
            float var37 = (var19 + var17 + var25 + var34) * 0.25F;
            float var38 = (var19 + var18 + var27 + var34) * 0.25F;
            float var39 = (var20 + var18 + var28 + var34) * 0.25F;
            this.lightMap[var35.vertex1] = this.blend(var16, var13, var30, var63);
            this.lightMap[var35.vertex2] = this.blend(var15, var13, var29, var63);
            this.lightMap[var35.vertex3] = this.blend(var15, var14, var31, var63);
            this.lightMap[var35.vertex4] = this.blend(var16, var14, var32, var63);
            this.brightness[var35.vertex1] = var36;
            this.brightness[var35.vertex2] = var37;
            this.brightness[var35.vertex3] = var38;
            this.brightness[var35.vertex4] = var39;
         }
      }

      private int blend(int i, int j, int k, int l) {
         if (i == 0) {
            i = l;
         }

         if (j == 0) {
            j = l;
         }

         if (k == 0) {
            k = l;
         }

         return i + j + k + l >> 2 & 16711935;
      }

      private int blend(int i, int j, int k, int l, float f, float g, float h, float m) {
         int var9 = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
         int var10 = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
         return var9 << 16 | var10;
      }
   }

   @Environment(EnvType.CLIENT)
   static enum AmbientVertexRemap {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vertex1;
      private final int vertex2;
      private final int vertex3;
      private final int vertex4;
      private static final BlockModelRenderer.AmbientVertexRemap[] ALL = new BlockModelRenderer.AmbientVertexRemap[6];

      private AmbientVertexRemap(int vertex1, int vertex2, int vertex3, int vertex4) {
         this.vertex1 = vertex1;
         this.vertex2 = vertex2;
         this.vertex3 = vertex3;
         this.vertex4 = vertex4;
      }

      public static BlockModelRenderer.AmbientVertexRemap byDirection(Direction dir) {
         return ALL[dir.getId()];
      }

      static {
         ALL[Direction.DOWN.getId()] = DOWN;
         ALL[Direction.UP.getId()] = UP;
         ALL[Direction.NORTH.getId()] = NORTH;
         ALL[Direction.SOUTH.getId()] = SOUTH;
         ALL[Direction.WEST.getId()] = WEST;
         ALL[Direction.EAST.getId()] = EAST;
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum SizeData {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      protected final int shape;

      private SizeData(Direction dir, boolean flip) {
         this.shape = dir.getId() + (flip ? Direction.values().length : 0);
      }
   }
}
