package net.minecraft.client.resource.manager;

import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IReloadableResourceManager extends IResourceManager {
   void reload(List resourcePacks);

   void addListener(ResourceReloadListener listener);
}
