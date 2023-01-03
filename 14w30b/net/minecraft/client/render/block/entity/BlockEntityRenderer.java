package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer {
   protected static final Identifier[] MINING_PROGRESS_TEXTURES = new Identifier[]{
      new Identifier("textures/blocks/destroy_stage_0.png"),
      new Identifier("textures/blocks/destroy_stage_1.png"),
      new Identifier("textures/blocks/destroy_stage_2.png"),
      new Identifier("textures/blocks/destroy_stage_3.png"),
      new Identifier("textures/blocks/destroy_stage_4.png"),
      new Identifier("textures/blocks/destroy_stage_5.png"),
      new Identifier("textures/blocks/destroy_stage_6.png"),
      new Identifier("textures/blocks/destroy_stage_7.png"),
      new Identifier("textures/blocks/destroy_stage_8.png"),
      new Identifier("textures/blocks/destroy_stage_9.png")
   };
   protected BlockEntityRenderDispatcher dispatcher;

   public abstract void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta, int blockMiningProgress);

   protected void bindTexture(Identifier texture) {
      TextureManager var2 = this.dispatcher.textureManager;
      if (var2 != null) {
         var2.bind(texture);
      }
   }

   protected World getWorld() {
      return this.dispatcher.world;
   }

   public void init(BlockEntityRenderDispatcher dispatcher) {
      this.dispatcher = dispatcher;
   }

   public void setWorld(World world) {
   }

   public TextRenderer getTextRenderer() {
      return this.dispatcher.getTextRenderer();
   }
}
