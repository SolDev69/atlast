package net.minecraft.client.resource.model;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SimpleBlockModelProvider extends AbstractBlockModelProvider {
   @Override
   protected ModelIdentifier provide(BlockState state) {
      return new ModelIdentifier((Identifier)Block.REGISTRY.getKey(state.getBlock()), this.propertiesAsString(state.values()));
   }
}
