package net.minecraft.client.resource.model;

import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ItemModelProvider {
   ModelIdentifier provide(ItemStack stack);
}
