package net.minecraft.client.resource.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel implements BakedModel {
   private final int totalWeight;
   private final List entries;
   private final BakedModel defaultModel;

   public WeightedBakedModel(List entries) {
      this.entries = entries;
      this.totalWeight = WeightedPicker.getTotalWeight(entries);
      this.defaultModel = ((WeightedBakedModel.ModelEntry)entries.get(0)).model;
   }

   @Override
   public List getQuads(Direction face) {
      return this.defaultModel.getQuads(face);
   }

   @Override
   public List getQuads() {
      return this.defaultModel.getQuads();
   }

   @Override
   public boolean useAmbientOcclusion() {
      return this.defaultModel.useAmbientOcclusion();
   }

   @Override
   public boolean isGui3d() {
      return this.defaultModel.isGui3d();
   }

   @Override
   public boolean isCustomRenderer() {
      return this.defaultModel.isCustomRenderer();
   }

   @Override
   public TextureAtlasSprite getParticleIcon() {
      return this.defaultModel.getParticleIcon();
   }

   @Override
   public ModelTransformations getTransformations() {
      return this.defaultModel.getTransformations();
   }

   public BakedModel pick(long weight) {
      return ((WeightedBakedModel.ModelEntry)WeightedPicker.pick(this.entries, Math.abs((int)weight >> 16) % this.totalWeight)).model;
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private List entries = Lists.newArrayList();

      public WeightedBakedModel.Builder add(BakedModel model, int weight) {
         this.entries.add(new WeightedBakedModel.ModelEntry(model, weight));
         return this;
      }

      public WeightedBakedModel build() {
         Collections.sort(this.entries);
         return new WeightedBakedModel(this.entries);
      }

      public BakedModel first() {
         return ((WeightedBakedModel.ModelEntry)this.entries.get(0)).model;
      }
   }

   @Environment(EnvType.CLIENT)
   static class ModelEntry extends WeightedPicker.Entry implements Comparable {
      protected final BakedModel model;

      public ModelEntry(BakedModel model, int weight) {
         super(weight);
         this.model = model;
      }

      public int compareTo(WeightedBakedModel.ModelEntry c_55aovqlfl) {
         return ComparisonChain.start().compare(c_55aovqlfl.weight, this.weight).compare(this.size(), c_55aovqlfl.size()).result();
      }

      protected int size() {
         int var1 = this.model.getQuads().size();

         for(Direction var5 : Direction.values()) {
            var1 += this.model.getQuads(var5).size();
         }

         return var1;
      }

      @Override
      public String toString() {
         return "MyWeighedRandomItem{weight=" + this.weight + ", model=" + this.model + '}';
      }
   }
}
