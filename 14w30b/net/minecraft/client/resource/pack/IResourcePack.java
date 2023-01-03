package net.minecraft.client.resource.pack;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resource.metadata.ResourceMetadataSection;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IResourcePack {
   InputStream getResource(Identifier id);

   boolean hasResource(Identifier id);

   Set getNamespaces();

   ResourceMetadataSection getMetadataSection(ResourceMetadataSerializerRegistry metadataSerializers, String name);

   BufferedImage getIcon();

   String getName();
}
