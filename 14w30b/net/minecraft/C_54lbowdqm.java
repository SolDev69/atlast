package net.minecraft;

import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_54lbowdqm extends ChunkBlockRenderer {
   private int f_22qhftoly = -1;

   public C_54lbowdqm(World c_54ruxjwzt, WorldRenderer c_07tveanoq, BlockPos c_76varpwca, int i) {
      super(c_54ruxjwzt, c_07tveanoq, c_76varpwca, i);
      this.f_22qhftoly = MemoryTracker.getLists(BlockLayer.values().length);
   }

   public int m_47yrwtrmw(BlockLayer c_26szrsafr, C_53fhzsins c_53fhzsins) {
      return !c_53fhzsins.m_66hqbsyfn(c_26szrsafr) ? this.f_22qhftoly + c_26szrsafr.ordinal() : -1;
   }

   @Override
   public void resetRenderStagesAndWorld() {
      super.resetRenderStagesAndWorld();
      if (this.f_22qhftoly != -1) {
         MemoryTracker.releaseLists(this.f_22qhftoly, BlockLayer.values().length);
         this.f_22qhftoly = -1;
      }
   }
}
