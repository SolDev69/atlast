package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_53fhzsins {
   public static final C_53fhzsins f_89lpuxuhd = new C_53fhzsins() {
      @Override
      protected void m_55pxkkajr(BlockLayer c_26szrsafr) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void m_45hmylvpy(BlockLayer c_26szrsafr) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean m_58vwhpzov(Direction c_69garkogr, Direction c_69garkogr2) {
         return false;
      }
   };
   private final boolean[] f_39pdahkwv = new boolean[BlockLayer.values().length];
   private final boolean[] f_11igdzvya = new boolean[BlockLayer.values().length];
   private boolean f_51cswhkzg = true;
   private final List f_80dopzidd = Lists.newArrayList();
   private C_14adyejqf f_92ifhchyr = new C_14adyejqf();
   private BufferBuilder.State f_67hgtljfl;

   public boolean m_29jrzqbxt() {
      return this.f_51cswhkzg;
   }

   protected void m_55pxkkajr(BlockLayer c_26szrsafr) {
      this.f_51cswhkzg = false;
      this.f_39pdahkwv[c_26szrsafr.ordinal()] = true;
   }

   public boolean m_66hqbsyfn(BlockLayer c_26szrsafr) {
      return !this.f_39pdahkwv[c_26szrsafr.ordinal()];
   }

   public void m_45hmylvpy(BlockLayer c_26szrsafr) {
      this.f_11igdzvya[c_26szrsafr.ordinal()] = true;
   }

   public boolean m_06fysprsm(BlockLayer c_26szrsafr) {
      return this.f_11igdzvya[c_26szrsafr.ordinal()];
   }

   public List m_64xgzhpzi() {
      return this.f_80dopzidd;
   }

   public void m_93lexfuxc(BlockEntity c_87eagbyjs) {
      this.f_80dopzidd.add(c_87eagbyjs);
   }

   public boolean m_58vwhpzov(Direction c_69garkogr, Direction c_69garkogr2) {
      return this.f_92ifhchyr.m_77sbrlvvi(c_69garkogr, c_69garkogr2);
   }

   public void m_36mfoqryn(C_14adyejqf c_32chznyli) {
      this.f_92ifhchyr = c_32chznyli;
   }

   public BufferBuilder.State m_57mivhtew() {
      return this.f_67hgtljfl;
   }

   public void m_87tamwlry(BufferBuilder.State c_16gttdmcd) {
      this.f_67hgtljfl = c_16gttdmcd;
   }
}
