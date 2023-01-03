package net.minecraft.client.render.model.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Vector3f;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelGenerator {
   public static final List LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public BlockModel generate(SpriteAtlasTexture blockAtlas, BlockModel model) {
      HashMap var3 = Maps.newHashMap();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < LAYERS.size(); ++var5) {
         String var6 = (String)LAYERS.get(var5);
         if (!model.hasTexture(var6)) {
            break;
         }

         String var7 = model.getTexture(var6);
         var3.put(var6, var7);
         TextureAtlasSprite var8 = blockAtlas.getSprite(new Identifier(var7).toString());
         var4.addAll(this.processFrames(var5, var6, var8));
      }

      if (var4.isEmpty()) {
         return null;
      } else {
         var3.put("particle", model.hasTexture("particle") ? model.getTexture("particle") : (String)var3.get("layer0"));
         return new BlockModel(
            var4, var3, false, false, new ModelTransformations(model.m_81pqtuasw(), model.m_10lvezxir(), model.m_09toxmbvv(), model.m_12mcmbtqy())
         );
      }
   }

   private List processFrames(int layer, String key, TextureAtlasSprite blockSprite) {
      HashMap var4 = Maps.newHashMap();
      var4.put(Direction.SOUTH, new BlockElementFace(null, layer, key, new BlockElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      var4.put(Direction.NORTH, new BlockElementFace(null, layer, key, new BlockElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList var5 = Lists.newArrayList();
      var5.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4, null, true));
      var5.addAll(this.addSideElements(blockSprite, key, layer));
      return var5;
   }

   private List addSideElements(TextureAtlasSprite blockAtlas, String key, int layer) {
      float var4 = (float)blockAtlas.getWidth();
      float var5 = (float)blockAtlas.getHeight();
      ArrayList var6 = Lists.newArrayList();

      for(ItemModelGenerator.Span var8 : this.getSpans(blockAtlas)) {
         float var9 = 0.0F;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         float var17 = 0.0F;
         float var18 = 0.0F;
         float var19 = (float)var8.getMin();
         float var20 = (float)var8.getMax();
         float var21 = (float)var8.getAnchor();
         ItemModelGenerator.Facing var22 = var8.getFacing();
         switch(var22) {
            case UP:
               var13 = var19;
               var9 = var19;
               var11 = var14 = var20 + 1.0F;
               var15 = var21;
               var10 = var21;
               var16 = var21;
               var12 = var21;
               var17 = 16.0F / var4;
               var18 = 16.0F / (var5 - 1.0F);
               break;
            case DOWN:
               var16 = var21;
               var15 = var21;
               var13 = var19;
               var9 = var19;
               var11 = var14 = var20 + 1.0F;
               var10 = var21 + 1.0F;
               var12 = var21 + 1.0F;
               var17 = 16.0F / var4;
               var18 = 16.0F / (var5 - 1.0F);
               break;
            case LEFT:
               var13 = var21;
               var9 = var21;
               var14 = var21;
               var11 = var21;
               var16 = var19;
               var10 = var19;
               var12 = var15 = var20 + 1.0F;
               var17 = 16.0F / (var4 - 1.0F);
               var18 = 16.0F / var5;
               break;
            case RIGHT:
               var14 = var21;
               var13 = var21;
               var9 = var21 + 1.0F;
               var11 = var21 + 1.0F;
               var16 = var19;
               var10 = var19;
               var12 = var15 = var20 + 1.0F;
               var17 = 16.0F / (var4 - 1.0F);
               var18 = 16.0F / var5;
         }

         float var23 = 16.0F / var4;
         float var24 = 16.0F / var5;
         var9 *= var23;
         var11 *= var23;
         var10 *= var24;
         var12 *= var24;
         var10 = 16.0F - var10;
         var12 = 16.0F - var12;
         var13 *= var17;
         var14 *= var17;
         var15 *= var18;
         var16 *= var18;
         HashMap var25 = Maps.newHashMap();
         var25.put(var22.asDirection(), new BlockElementFace(null, layer, key, new BlockElementTexture(new float[]{var13, var15, var14, var16}, 0)));
         switch(var22) {
            case UP:
               var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var11, var10, 8.5F), var25, null, true));
               break;
            case DOWN:
               var6.add(new BlockElement(new Vector3f(var9, var12, 7.5F), new Vector3f(var11, var12, 8.5F), var25, null, true));
               break;
            case LEFT:
               var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var9, var12, 8.5F), var25, null, true));
               break;
            case RIGHT:
               var6.add(new BlockElement(new Vector3f(var11, var10, 7.5F), new Vector3f(var11, var12, 8.5F), var25, null, true));
         }
      }

      return var6;
   }

   private List getSpans(TextureAtlasSprite blockAtlas) {
      int var2 = blockAtlas.getWidth();
      int var3 = blockAtlas.getHeight();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < blockAtlas.getFrameSize(); ++var5) {
         int[] var6 = blockAtlas.getFrame(var5)[0];

         for(int var7 = 0; var7 < var3; ++var7) {
            for(int var8 = 0; var8 < var2; ++var8) {
               boolean var9 = !this.isTransparent(var6, var8, var7, var2, var3);
               this.checkTransition(ItemModelGenerator.Facing.UP, var4, var6, var8, var7, var2, var3, var9);
               this.checkTransition(ItemModelGenerator.Facing.DOWN, var4, var6, var8, var7, var2, var3, var9);
               this.checkTransition(ItemModelGenerator.Facing.LEFT, var4, var6, var8, var7, var2, var3, var9);
               this.checkTransition(ItemModelGenerator.Facing.RIGHT, var4, var6, var8, var7, var2, var3, var9);
            }
         }
      }

      return var4;
   }

   private void checkTransition(ItemModelGenerator.Facing facing, List spans, int[] frame, int x, int y, int width, int height, boolean expandWhenTransparent) {
      boolean var9 = this.isTransparent(frame, x + facing.getOffsetX(), y + facing.getOffsetY(), width, height) && expandWhenTransparent;
      if (var9) {
         this.expandSpan(spans, facing, x, y);
      }
   }

   private void expandSpan(List spans, ItemModelGenerator.Facing facing, int x, int y) {
      ItemModelGenerator.Span var5 = null;

      for(ItemModelGenerator.Span var7 : spans) {
         if (var7.getFacing() == facing) {
            int var8 = facing.isVertical() ? y : x;
            if (var7.getAnchor() == var8) {
               var5 = var7;
               break;
            }
         }
      }

      int var9 = facing.isVertical() ? y : x;
      int var10 = facing.isVertical() ? x : y;
      if (var5 == null) {
         spans.add(new ItemModelGenerator.Span(facing, var10, var9));
      } else {
         var5.expand(var10);
      }
   }

   private boolean isTransparent(int[] frame, int x, int y, int width, int height) {
      if (x >= 0 && y >= 0 && x < width && y < height) {
         return (frame[y * width + x] >> 24 & 0xFF) == 0;
      } else {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   static enum Facing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction dir;
      private final int offsetX;
      private final int offsetY;

      private Facing(Direction dir, int offsetX, int offsetY) {
         this.dir = dir;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
      }

      public Direction asDirection() {
         return this.dir;
      }

      public int getOffsetX() {
         return this.offsetX;
      }

      public int getOffsetY() {
         return this.offsetY;
      }

      private boolean isVertical() {
         return this == DOWN || this == UP;
      }
   }

   @Environment(EnvType.CLIENT)
   static class Span {
      private final ItemModelGenerator.Facing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.Facing facing, int value, int anchor) {
         this.facing = facing;
         this.min = value;
         this.max = value;
         this.anchor = anchor;
      }

      public void expand(int amount) {
         if (amount < this.min) {
            this.min = amount;
         } else if (amount > this.max) {
            this.max = amount;
         }
      }

      public ItemModelGenerator.Facing getFacing() {
         return this.facing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }
}
