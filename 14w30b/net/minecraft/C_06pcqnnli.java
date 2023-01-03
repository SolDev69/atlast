package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_06pcqnnli {
   private final ChunkBlockRenderer f_47cevtnsy;
   private final ReentrantLock f_12arsqlfh = new ReentrantLock();
   private final List f_93kxdmwbi = Lists.newArrayList();
   private final C_06pcqnnli.C_77ddrvmit f_22kuaborg;
   private C_19lmmzlfd f_21iswtkzj;
   private C_53fhzsins f_12ugtzcia;
   private C_06pcqnnli.C_45bvstooc f_24jictwlp = C_06pcqnnli.C_45bvstooc.PENDING;
   private boolean f_14pdpofkx;

   public C_06pcqnnli(ChunkBlockRenderer c_20vbkqxvz, C_06pcqnnli.C_77ddrvmit c_77ddrvmit) {
      this.f_47cevtnsy = c_20vbkqxvz;
      this.f_22kuaborg = c_77ddrvmit;
   }

   public C_06pcqnnli.C_45bvstooc m_23eezatmj() {
      return this.f_24jictwlp;
   }

   public ChunkBlockRenderer m_75lssdqlu() {
      return this.f_47cevtnsy;
   }

   public C_53fhzsins m_44zqxragh() {
      return this.f_12ugtzcia;
   }

   public void m_73swxuixb(C_53fhzsins c_53fhzsins) {
      this.f_12ugtzcia = c_53fhzsins;
   }

   public C_19lmmzlfd m_52lxfshlo() {
      return this.f_21iswtkzj;
   }

   public void m_89cgbjvgy(C_19lmmzlfd c_19lmmzlfd) {
      this.f_21iswtkzj = c_19lmmzlfd;
   }

   public void m_12hzuasgn(C_06pcqnnli.C_45bvstooc c_45bvstooc) {
      this.f_12arsqlfh.lock();

      try {
         this.f_24jictwlp = c_45bvstooc;
      } finally {
         this.f_12arsqlfh.unlock();
      }
   }

   public void m_58dhnfokw() {
      this.f_12arsqlfh.lock();

      try {
         this.f_14pdpofkx = true;
         this.f_24jictwlp = C_06pcqnnli.C_45bvstooc.DONE;

         for(Runnable var2 : this.f_93kxdmwbi) {
            var2.run();
         }
      } finally {
         this.f_12arsqlfh.unlock();
      }
   }

   public void m_50mpmyuzs(Runnable runnable) {
      this.f_12arsqlfh.lock();

      try {
         this.f_93kxdmwbi.add(runnable);
         if (this.f_14pdpofkx) {
            runnable.run();
         }
      } finally {
         this.f_12arsqlfh.unlock();
      }
   }

   public ReentrantLock m_89robiyro() {
      return this.f_12arsqlfh;
   }

   public C_06pcqnnli.C_77ddrvmit m_31kutqhid() {
      return this.f_22kuaborg;
   }

   public boolean m_40fnnmaiq() {
      return this.f_14pdpofkx;
   }

   @Environment(EnvType.CLIENT)
   public static enum C_45bvstooc {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   @Environment(EnvType.CLIENT)
   public static enum C_77ddrvmit {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
