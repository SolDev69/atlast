package net.minecraft.client.resource.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.render.model.block.BlockModel;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BasicBakedModel implements BakedModel {
   protected final List unculledFaces;
   protected final List culledFaces;
   protected final boolean ambientOcclusion;
   protected final boolean gui3d;
   protected final TextureAtlasSprite particleIcon;
   protected final ModelTransformations transforms;

   public BasicBakedModel(
      List unculledFaces, List culledFaces, boolean ambientOcclusion, boolean gui3d, TextureAtlasSprite particleIcon, ModelTransformations transforms
   ) {
      this.unculledFaces = unculledFaces;
      this.culledFaces = culledFaces;
      this.ambientOcclusion = ambientOcclusion;
      this.gui3d = gui3d;
      this.particleIcon = particleIcon;
      this.transforms = transforms;
   }

   @Override
   public List getQuads(Direction face) {
      return (List)this.culledFaces.get(face.ordinal());
   }

   @Override
   public List getQuads() {
      return this.unculledFaces;
   }

   @Override
   public boolean useAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   @Override
   public boolean isGui3d() {
      return this.gui3d;
   }

   @Override
   public boolean isCustomRenderer() {
      return false;
   }

   @Override
   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   @Override
   public ModelTransformations getTransformations() {
      return this.transforms;
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final List unculledFaces = Lists.newArrayList();
      private final List culledFaces = Lists.newArrayListWithCapacity(6);
      private final boolean ambientOcclusion;
      private TextureAtlasSprite particleIcon;
      private boolean gui3d;
      private ModelTransformations transforms;

      public Builder(BlockModel model) {
         this(
            model.usesAmbientOcclusion(),
            model.isGui3d(),
            new ModelTransformations(model.m_81pqtuasw(), model.m_10lvezxir(), model.m_09toxmbvv(), model.m_12mcmbtqy())
         );
      }

      public Builder(BakedModel model, TextureAtlasSprite miningSprite) {
         this(model.useAmbientOcclusion(), model.isGui3d(), model.getTransformations());
         this.particleIcon = model.getParticleIcon();

         for(Direction var6 : Direction.values()) {
            this.culledMiningFaces(model, miningSprite, var6);
         }

         this.unculledMiningFaces(model, miningSprite);
      }

      private void culledMiningFaces(BakedModel model, TextureAtlasSprite miningSprite, Direction face) {
         for(BakedQuad var5 : model.getQuads(face)) {
            this.culledFace(face, new MiningQuad(var5, miningSprite));
         }
      }

      private void unculledMiningFaces(BakedModel model, TextureAtlasSprite miningSprite) {
         for(BakedQuad var4 : model.getQuads()) {
            this.unculledFace(new MiningQuad(var4, miningSprite));
         }
      }

      private Builder(boolean ambientOcclusion, boolean gui3d, ModelTransformations transforms) {
         for(Direction var7 : Direction.values()) {
            this.culledFaces.add(Lists.newArrayList());
         }

         this.ambientOcclusion = ambientOcclusion;
         this.gui3d = gui3d;
         this.transforms = transforms;
      }

      public BasicBakedModel.Builder culledFace(Direction face, BakedQuad quad) {
         ((List)this.culledFaces.get(face.ordinal())).add(quad);
         return this;
      }

      public BasicBakedModel.Builder unculledFace(BakedQuad quad) {
         this.unculledFaces.add(quad);
         return this;
      }

      public BasicBakedModel.Builder particleIcon(TextureAtlasSprite particleIcon) {
         this.particleIcon = particleIcon;
         return this;
      }

      public BakedModel build() {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new BasicBakedModel(this.unculledFaces, this.culledFaces, this.ambientOcclusion, this.gui3d, this.particleIcon, this.transforms);
         }
      }
   }
}
