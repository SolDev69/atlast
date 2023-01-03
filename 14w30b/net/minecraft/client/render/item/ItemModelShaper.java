package net.minecraft.client.render.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.ItemModelProvider;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelShaper {
   private final Map models = Maps.newHashMap();
   private final Map modelCache = Maps.newHashMap();
   private final Map modelProviders = Maps.newHashMap();
   private final ModelManager manager;

   public ItemModelShaper(ModelManager manager) {
      this.manager = manager;
   }

   public TextureAtlasSprite getParticleIcon(Item item) {
      return this.getParticleIcon(item, 0);
   }

   public TextureAtlasSprite getParticleIcon(Item item, int metadata) {
      return this.getModel(new ItemStack(item, 1, metadata)).getParticleIcon();
   }

   public BakedModel getModel(ItemStack stack) {
      Item var2 = stack.getItem();
      BakedModel var3 = this.getModel(var2, this.getModelMetadata(stack));
      if (var3 == null) {
         ItemModelProvider var4 = (ItemModelProvider)this.modelProviders.get(var2);
         if (var4 != null) {
            var3 = this.manager.getModel(var4.provide(stack));
         }
      }

      if (var3 == null) {
         var3 = this.manager.getMissingModel();
      }

      return var3;
   }

   protected int getModelMetadata(ItemStack stack) {
      return stack.isDamageable() ? 0 : stack.getMetadata();
   }

   protected BakedModel getModel(Item item, int metadata) {
      return (BakedModel)this.modelCache.get(this.index(item, metadata));
   }

   private int index(Item item, int metadata) {
      return Item.getRawId(item) << 16 | metadata;
   }

   public void register(Item item, int metadata, ModelIdentifier model) {
      this.models.put(this.index(item, metadata), model);
      this.modelCache.put(this.index(item, metadata), this.manager.getModel(model));
   }

   public void register(Item item, ItemModelProvider provider) {
      this.modelProviders.put(item, provider);
   }

   public ModelManager getManager() {
      return this.manager;
   }

   public void rebuildCache() {
      this.modelCache.clear();

      for(Entry var2 : this.models.entrySet()) {
         this.modelCache.put(var2.getKey(), this.manager.getModel((ModelIdentifier)var2.getValue()));
      }
   }
}
