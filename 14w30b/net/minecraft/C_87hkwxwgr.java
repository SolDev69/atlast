package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class C_87hkwxwgr extends C_98pnhiglv {
   @Override
   public void m_73mzxhtxq(BlockLayer c_26szrsafr) {
      if (this.f_31tfqnrpy) {
         for(ChunkBlockRenderer var3 : this.f_46vsohjao) {
            C_54lbowdqm var4 = (C_54lbowdqm)var3;
            GlStateManager.pushMatrix();
            this.m_45abruuwy(var3);
            GL11.glCallList(var4.m_47yrwtrmw(c_26szrsafr, var4.m_86rgndiak()));
            GlStateManager.popMatrix();
         }

         GlStateManager.clearColor();
         this.f_46vsohjao.clear();
      }
   }
}
