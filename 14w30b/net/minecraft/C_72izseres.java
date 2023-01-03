package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class C_72izseres implements Runnable {
   private static final Logger f_78tsqvotj = LogManager.getLogger();
   private final C_34czhuwfp f_96tcskvnk;

   public C_72izseres(C_34czhuwfp c_65xzluxwk) {
      this.f_96tcskvnk = c_65xzluxwk;
   }

   @Override
   public void run() {
      while(true) {
         try {
            this.m_08vxnxlts(this.f_96tcskvnk.m_52lzqahkn());
         } catch (InterruptedException var3) {
            f_78tsqvotj.debug("Stopping due to interrupt");
            return;
         } catch (Throwable var4) {
            CrashReport var2 = CrashReport.of(var4, "Batching chunks");
            MinecraftClient.getInstance().crash(MinecraftClient.getInstance().populateCrashReport(var2));
            return;
         }
      }
   }

   protected void m_08vxnxlts(C_06pcqnnli c_60jrwydru) {
      c_60jrwydru.m_89robiyro().lock();

      try {
         if (c_60jrwydru.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.PENDING) {
            if (!c_60jrwydru.m_40fnnmaiq()) {
               f_78tsqvotj.warn("Chunk render task was " + c_60jrwydru.m_23eezatmj() + " when I expected it to be pending; ignoring task");
            }

            return;
         }

         c_60jrwydru.m_12hzuasgn(C_06pcqnnli.C_45bvstooc.COMPILING);
      } finally {
         c_60jrwydru.m_89robiyro().unlock();
      }

      Entity var2 = MinecraftClient.getInstance().getCamera();
      if (var2 == null) {
         c_60jrwydru.m_58dhnfokw();
      } else {
         c_60jrwydru.m_89cgbjvgy(this.f_96tcskvnk.m_11ghcjbgi());
         float var3 = (float)var2.x;
         float var4 = (float)var2.y + var2.getEyeHeight();
         float var5 = (float)var2.z;
         C_06pcqnnli.C_77ddrvmit var6 = c_60jrwydru.m_31kutqhid();
         if (var6 == C_06pcqnnli.C_77ddrvmit.REBUILD_CHUNK) {
            c_60jrwydru.m_75lssdqlu().m_87gkfktay(var3, var4, var5, c_60jrwydru);
         } else if (var6 == C_06pcqnnli.C_77ddrvmit.RESORT_TRANSPARENCY) {
            c_60jrwydru.m_75lssdqlu().m_71cycciik(var3, var4, var5, c_60jrwydru);
         }

         c_60jrwydru.m_89robiyro().lock();

         try {
            if (c_60jrwydru.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.COMPILING) {
               if (!c_60jrwydru.m_40fnnmaiq()) {
                  f_78tsqvotj.warn("Chunk render task was " + c_60jrwydru.m_23eezatmj() + " when I expected it to be compiling; aborting task");
               }

               this.m_75xsrkvqo(c_60jrwydru);
               return;
            }

            c_60jrwydru.m_12hzuasgn(C_06pcqnnli.C_45bvstooc.UPLOADING);
         } finally {
            c_60jrwydru.m_89robiyro().unlock();
         }

         final C_53fhzsins var7 = c_60jrwydru.m_44zqxragh();
         ArrayList var8 = Lists.newArrayList();
         if (var6 == C_06pcqnnli.C_77ddrvmit.REBUILD_CHUNK) {
            for(BlockLayer var12 : BlockLayer.values()) {
               if (var7.m_06fysprsm(var12)) {
                  var8.add(this.f_96tcskvnk.m_61zmndvvh(var12, c_60jrwydru.m_52lxfshlo().m_12wnljgwg(var12), c_60jrwydru.m_75lssdqlu(), var7));
               }
            }
         } else if (var6 == C_06pcqnnli.C_77ddrvmit.RESORT_TRANSPARENCY) {
            var8.add(
               this.f_96tcskvnk
                  .m_61zmndvvh(BlockLayer.TRANSLUCENT, c_60jrwydru.m_52lxfshlo().m_12wnljgwg(BlockLayer.TRANSLUCENT), c_60jrwydru.m_75lssdqlu(), var7)
            );
         }

         final ListenableFuture var19 = Futures.allAsList(var8);
         c_60jrwydru.m_50mpmyuzs(new Runnable() {
            @Override
            public void run() {
               var19.cancel(false);
            }
         });
         Futures.addCallback(
            var19,
            new FutureCallback() {
               public void onSuccess(List list) {
                  C_72izseres.this.m_75xsrkvqo(c_60jrwydru);
                  c_60jrwydru.m_89robiyro().lock();
   
                  label42: {
                     try {
                        if (c_60jrwydru.m_23eezatmj() == C_06pcqnnli.C_45bvstooc.UPLOADING) {
                           c_60jrwydru.m_12hzuasgn(C_06pcqnnli.C_45bvstooc.DONE);
                           break label42;
                        }
   
                        if (!c_60jrwydru.m_40fnnmaiq()) {
                           C_72izseres.f_78tsqvotj
                              .warn("Chunk render task was " + c_60jrwydru.m_23eezatmj() + " when I expected it to be uploading; aborting task");
                        }
                     } finally {
                        c_60jrwydru.m_89robiyro().unlock();
                     }
   
                     return;
                  }
   
                  c_60jrwydru.m_75lssdqlu().m_03gmrikwy(var7);
               }
   
               public void onFailure(Throwable throwable) {
                  C_72izseres.this.m_75xsrkvqo(c_60jrwydru);
                  if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                     MinecraftClient.getInstance().crash(CrashReport.of(throwable, "Rendering chunk"));
                  }
               }
            }
         );
      }
   }

   private void m_75xsrkvqo(C_06pcqnnli c_60jrwydru) {
      this.f_96tcskvnk.m_25avnwtaz(c_60jrwydru.m_52lxfshlo());
   }
}
