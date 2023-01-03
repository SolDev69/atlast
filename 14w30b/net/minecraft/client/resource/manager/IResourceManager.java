package net.minecraft.client.resource.manager;

import java.util.List;
import java.util.Set;
import net.minecraft.client.resource.IResource;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IResourceManager {
   Set getNamespaces();

   IResource getResource(Identifier id);

   List getResources(Identifier id);
}
