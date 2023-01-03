package net.minecraft.client.resource.model;

import net.minecraft.client.render.block.BlockModelShaper;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.registry.Registry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelManager implements ResourceReloadListener {
   private Registry registry;
   private final SpriteAtlasTexture blocksSprite;
   private final BlockModelShaper modelShaper;
   private BakedModel missing;

   public ModelManager(SpriteAtlasTexture blocksSprite) {
      this.blocksSprite = blocksSprite;
      this.modelShaper = new BlockModelShaper(this);
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      ModelBakery var2 = new ModelBakery(resourceManager, this.blocksSprite, this.modelShaper);
      this.registry = var2.getBakedModels();
      this.missing = (BakedModel)this.registry.get(ModelBakery.MISSING);
      this.modelShaper.rebuildCache();
   }

   public BakedModel getModel(ModelIdentifier id) {
      if (id == null) {
         return this.missing;
      } else {
         BakedModel var2 = (BakedModel)this.registry.get(id);
         return var2 == null ? this.missing : var2;
      }
   }

   public BakedModel getMissingModel() {
      return this.missing;
   }

   public SpriteAtlasTexture getBlocksSprite() {
      return this.blocksSprite;
   }

   public BlockModelShaper getModelShaper() {
      return this.modelShaper;
   }
}
