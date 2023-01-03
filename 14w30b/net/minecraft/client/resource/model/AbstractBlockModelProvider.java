package net.minecraft.client.resource.model;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.client.resource.ModelIdentifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractBlockModelProvider implements BlockModelProvider {
   protected Map models = Maps.newLinkedHashMap();

   public String propertiesAsString(Map properties) {
      StringBuilder var2 = new StringBuilder();

      for(Entry var4 : properties.entrySet()) {
         if (var2.length() != 0) {
            var2.append(",");
         }

         Property var5 = (Property)var4.getKey();
         Comparable var6 = (Comparable)var4.getValue();
         var2.append(var5.getName());
         var2.append("=");
         var2.append(var5.getName(var6));
      }

      if (var2.length() == 0) {
         var2.append("normal");
      }

      return var2.toString();
   }

   @Override
   public Map provide(Block block) {
      for(BlockState var3 : block.stateDefinition().all()) {
         this.models.put(var3, this.provide(var3));
      }

      return this.models;
   }

   protected abstract ModelIdentifier provide(BlockState state);
}
