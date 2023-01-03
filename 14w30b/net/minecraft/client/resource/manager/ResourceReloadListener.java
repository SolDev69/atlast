package net.minecraft.client.resource.manager;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ResourceReloadListener {
   void reload(IResourceManager resourceManager);
}
