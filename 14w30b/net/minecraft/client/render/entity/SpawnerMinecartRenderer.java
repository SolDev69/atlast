package net.minecraft.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.entity.MobSpawnerRenderer;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpawnerMinecartRenderer extends MinecartRenderer {
   public SpawnerMinecartRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   protected void renderSpawnerMinecart(SpawnerMinecartEntity c_11wbeqqmq, float f, Block c_68zcrzyxg, int i) {
      super.renderSpawnerMinecart(c_11wbeqqmq, f, c_68zcrzyxg, i);
      if (c_68zcrzyxg == Blocks.MOB_SPAWNER) {
         MobSpawnerRenderer.renderMobSpawner(c_11wbeqqmq.getSpawnerBehavior(), c_11wbeqqmq.x, c_11wbeqqmq.y, c_11wbeqqmq.z, f);
      }
   }
}
