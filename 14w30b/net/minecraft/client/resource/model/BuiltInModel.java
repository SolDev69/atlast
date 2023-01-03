package net.minecraft.client.resource.model;

import java.util.List;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BuiltInModel implements BakedModel {
   private ModelTransformations transformations;

   public BuiltInModel(ModelTransformations transformations) {
      this.transformations = transformations;
   }

   @Override
   public List getQuads(Direction face) {
      return null;
   }

   @Override
   public List getQuads() {
      return null;
   }

   @Override
   public boolean useAmbientOcclusion() {
      return false;
   }

   @Override
   public boolean isGui3d() {
      return true;
   }

   @Override
   public boolean isCustomRenderer() {
      return true;
   }

   @Override
   public TextureAtlasSprite getParticleIcon() {
      return null;
   }

   @Override
   public ModelTransformations getTransformations() {
      return this.transformations;
   }
}
