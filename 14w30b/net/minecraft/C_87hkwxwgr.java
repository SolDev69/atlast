package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.Buffer;
import java.nio.IntBuffer;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class C_87hkwxwgr extends C_98pnhiglv {
   private IntBuffer f_07plckcbq = MemoryTracker.createIntBuffer(65536);

   @Override
   public void m_04zpydvcx(double d, double e, double f) {
      super.m_04zpydvcx(d, e, f);
      ((Buffer)this.f_07plckcbq).clear();
   }

   @Override
   public void m_59lctdfif(ChunkBlockRenderer c_20vbkqxvz, BlockLayer c_26szrsafr) {
      super.m_59lctdfif(c_20vbkqxvz, c_26szrsafr);
      this.f_07plckcbq.put(((C_54lbowdqm)c_20vbkqxvz).m_47yrwtrmw(c_26szrsafr, c_20vbkqxvz.m_86rgndiak()));
      if (this.f_07plckcbq.remaining() == 0) {
         this.m_73mzxhtxq(c_26szrsafr);
      }
   }

   @Override
   public void m_73mzxhtxq(BlockLayer c_26szrsafr) {
      if (this.f_31tfqnrpy) {
         ((Buffer)this.f_07plckcbq).flip();
         if (this.f_07plckcbq.remaining() > 0) {
            GlStateManager.pushMatrix();
            this.m_19hvyktba();
            GL11.glCallLists(this.f_07plckcbq);
            GlStateManager.clearColor();
            GlStateManager.popMatrix();
         }

         ((Buffer)this.f_07plckcbq).clear();
      }
   }
}
