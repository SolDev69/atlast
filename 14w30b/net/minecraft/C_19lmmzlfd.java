package net.minecraft;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.render.block.BlockLayer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_19lmmzlfd {
   private final BufferBuilder[] f_93vdpijos = new BufferBuilder[BlockLayer.values().length];

   public C_19lmmzlfd() {
      this.f_93vdpijos[BlockLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
      this.f_93vdpijos[BlockLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
      this.f_93vdpijos[BlockLayer.CUTOUT_MIPPED.ordinal()] = new BufferBuilder(131072);
      this.f_93vdpijos[BlockLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
   }

   public BufferBuilder m_12wnljgwg(BlockLayer c_26szrsafr) {
      return this.f_93vdpijos[c_26szrsafr.ordinal()];
   }

   public BufferBuilder m_45etpyycn(int i) {
      return this.f_93vdpijos[i];
   }
}
