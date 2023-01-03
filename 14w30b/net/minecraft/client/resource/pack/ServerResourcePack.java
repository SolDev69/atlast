package net.minecraft.client.resource.pack;

import java.io.File;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ServerResourcePack {
   void apply(File file);
}
