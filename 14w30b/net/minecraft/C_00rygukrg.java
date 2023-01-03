package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class C_00rygukrg extends C_98pnhiglv {
   @Override
   public void m_73mzxhtxq(BlockLayer c_26szrsafr) {
      if (this.f_31tfqnrpy) {
         for(ChunkBlockRenderer var3 : this.f_46vsohjao) {
            VertexBuffer var4 = var3.m_13kjyoqhl(c_26szrsafr.ordinal());
            GlStateManager.pushMatrix();
            this.m_45abruuwy(var3);
            var3.m_32thpmjwk();
            var4.bind();
            this.m_65rxwrnkj();
            var4.draw(7);
            GlStateManager.popMatrix();
         }

         GLX.bindBuffer(GLX.GL_ARRAY_BUFFER, 0);
         GlStateManager.clearColor();
         this.f_46vsohjao.clear();
      }
   }

   private void m_65rxwrnkj() {
      GL11.glVertexPointer(3, 5126, 28, 0L);
      GL11.glColorPointer(4, 5121, 28, 12L);
      GL11.glTexCoordPointer(2, 5126, 28, 16L);
      GLX.clientActiveTexture(GLX.GL_TEXTURE1);
      GL11.glTexCoordPointer(2, 5122, 28, 24L);
      GLX.clientActiveTexture(GLX.GL_TEXTURE0);
   }
}
