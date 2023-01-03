package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class C_98pnhiglv {
   private double x;
   private double y;
   private double z;
   protected List f_46vsohjao = Lists.newArrayListWithCapacity(17424);
   protected boolean f_31tfqnrpy;

   public void m_04zpydvcx(double x, double y, double z) {
      this.f_31tfqnrpy = true;
      this.f_46vsohjao.clear();
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void m_45abruuwy(ChunkBlockRenderer c_20vbkqxvz) {
      BlockPos var2 = c_20vbkqxvz.m_97olwzrjj();
      GlStateManager.translatef((float)((double)var2.getX() - this.x), (float)((double)var2.getY() - this.y), (float)((double)var2.getZ() - this.z));
   }

   public void m_59lctdfif(ChunkBlockRenderer c_20vbkqxvz, BlockLayer c_26szrsafr) {
      this.f_46vsohjao.add(c_20vbkqxvz);
   }

   public abstract void m_73mzxhtxq(BlockLayer c_26szrsafr);
}
