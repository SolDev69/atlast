package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BlockEntityItemRenderer;
import net.minecraft.item.ItemStack;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderer {
   public void render(Block block, float color) {
      GlStateManager.color4f(color, color, color, 1.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      BlockEntityItemRenderer.INSTANCE.render(new ItemStack(block));
   }
}
