package net.minecraft;

import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_00ekfeucz implements C_42nteilvc {
   @Override
   public ChunkBlockRenderer m_84vrqgyim(World c_54ruxjwzt, WorldRenderer c_07tveanoq, BlockPos c_76varpwca, int i) {
      return new ChunkBlockRenderer(c_54ruxjwzt, c_07tveanoq, c_76varpwca, i);
   }
}
