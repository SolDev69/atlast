package net.minecraft.client.resource;

import java.io.InputStream;
import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IResource {
   Identifier getId();

   InputStream asStream();

   boolean hasMetadata();

   ResourceMetadataSection getMetadata(String name);

   String getSourceName();
}
