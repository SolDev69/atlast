package net.minecraft.client.texture;

import net.minecraft.client.resource.manager.IResourceManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Texture {
   void m_60hztdglb(boolean bl, boolean bl2);

   void m_42jngdvts();

   void load(IResourceManager resourceManager);

   int getGlId();
}
