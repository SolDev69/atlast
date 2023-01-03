package net.minecraft.client.resource.model;

import java.util.List;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BakedModel {
   List getQuads(Direction face);

   List getQuads();

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean isCustomRenderer();

   TextureAtlasSprite getParticleIcon();

   ModelTransformations getTransformations();
}
