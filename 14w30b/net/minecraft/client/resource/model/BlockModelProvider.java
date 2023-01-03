package net.minecraft.client.resource.model;

import java.util.Map;
import net.minecraft.block.Block;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BlockModelProvider {
   Map provide(Block block);
}
