package net.minecraft.client.resource.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class VariantBlockModelProvider extends AbstractBlockModelProvider {
   private final Property property;
   private final String variant;
   private final List unusedProperties;

   private VariantBlockModelProvider(Property property, String variant, List unusedProperties) {
      this.property = property;
      this.variant = variant;
      this.unusedProperties = unusedProperties;
   }

   @Override
   protected ModelIdentifier provide(BlockState state) {
      LinkedHashMap var2 = Maps.newLinkedHashMap(state.values());
      String var3;
      if (this.property == null) {
         var3 = ((Identifier)Block.REGISTRY.getKey(state.getBlock())).toString();
      } else {
         var3 = this.property.getName((Comparable)var2.remove(this.property));
      }

      if (this.variant != null) {
         var3 = var3 + this.variant;
      }

      for(Property var5 : this.unusedProperties) {
         var2.remove(var5);
      }

      return new ModelIdentifier(var3, this.propertiesAsString(var2));
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private Property property;
      private String variant;
      private final List unusedProperties = Lists.newArrayList();

      public VariantBlockModelProvider.Builder setProperty(Property property) {
         this.property = property;
         return this;
      }

      public VariantBlockModelProvider.Builder setVariant(String variant) {
         this.variant = variant;
         return this;
      }

      public VariantBlockModelProvider.Builder setUnusedProperties(Property... properties) {
         Collections.addAll(this.unusedProperties, properties);
         return this;
      }

      public VariantBlockModelProvider build() {
         return new VariantBlockModelProvider(this.property, this.variant, this.unusedProperties);
      }
   }
}
