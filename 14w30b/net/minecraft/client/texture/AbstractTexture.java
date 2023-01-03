package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements Texture {
   protected int glId = -1;
   protected boolean f_85sdwrwxy;
   protected boolean f_72xljrrqt;
   protected boolean f_32nmtqchk;
   protected boolean f_60ngrzkhb;

   public void m_21vhhelxf(boolean bl, boolean bl2) {
      this.f_85sdwrwxy = bl;
      this.f_72xljrrqt = bl2;
      int var3 = -1;
      short var4 = -1;
      if (bl) {
         var3 = bl2 ? 9987 : 9729;
         var4 = 9729;
      } else {
         var3 = bl2 ? 9986 : 9728;
         var4 = 9728;
      }

      GL11.glTexParameteri(3553, 10241, var3);
      GL11.glTexParameteri(3553, 10240, var4);
   }

   @Override
   public void m_60hztdglb(boolean bl, boolean bl2) {
      this.f_32nmtqchk = this.f_85sdwrwxy;
      this.f_60ngrzkhb = this.f_72xljrrqt;
      this.m_21vhhelxf(bl, bl2);
   }

   @Override
   public void m_42jngdvts() {
      this.m_21vhhelxf(this.f_32nmtqchk, this.f_60ngrzkhb);
   }

   @Override
   public int getGlId() {
      if (this.glId == -1) {
         this.glId = TextureUtil.genTextures();
      }

      return this.glId;
   }

   public void clearGlId() {
      if (this.glId != -1) {
         TextureUtil.deleteTexture(this.glId);
         this.glId = -1;
      }
   }
}
