package net.minecraft.client.resource.metadata.serializer;

import com.google.gson.JsonDeserializer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IResourceMetadataSerializer extends JsonDeserializer {
   String getName();
}
