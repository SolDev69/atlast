package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBufferUploader;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class C_34czhuwfp {
   private static final Logger f_24kbluhpv = LogManager.getLogger();
   private static final ThreadFactory CHUNK_BATCHER_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
   private final List f_74bqwczwp = Lists.newArrayList();
   private final BlockingQueue f_74paoaycu = Queues.newArrayBlockingQueue(100);
   private final BlockingQueue f_60gegzcnc = Queues.newArrayBlockingQueue(5);
   private final BufferUploader bufferUploader = new BufferUploader();
   private final VertexBufferUploader vertexBufferUploader = new VertexBufferUploader();
   private final Queue f_09ipkrjfp = Queues.newArrayDeque();
   private final C_72izseres f_80pffmewb;

   public C_34czhuwfp() {
      for(int var1 = 0; var1 < 2; ++var1) {
         C_72izseres var2 = new C_72izseres(this);
         Thread var3 = CHUNK_BATCHER_FACTORY.newThread(var2);
         var3.start();
         this.f_74bqwczwp.add(var2);
      }

      for(int var4 = 0; var4 < 5; ++var4) {
         this.f_60gegzcnc.add(new C_19lmmzlfd());
      }

      this.f_80pffmewb = null;
   }

   public String m_43nzrlwev() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.f_74paoaycu.size(), this.f_09ipkrjfp.size(), this.f_60gegzcnc.size());
   }

   public boolean m_77oufjbaz(long l) {
      boolean var3 = false;

      long var8;
      do {
         boolean var4 = false;
         synchronized(this.f_09ipkrjfp) {
            if (!this.f_09ipkrjfp.isEmpty()) {
               ((ListenableFutureTask)this.f_09ipkrjfp.poll()).run();
               var4 = true;
               var3 = true;
            }
         }

         if (l == 0L || !var4) {
            break;
         }

         var8 = l - System.nanoTime();
      } while(var8 >= 0L && var8 <= 1000000000L);

      return var3;
   }

   public boolean m_11mswwlvj(ChunkBlockRenderer c_20vbkqxvz) {
      c_20vbkqxvz.m_72zxisjbx().lock();

      boolean var4;
      try {
         final C_06pcqnnli var2 = c_20vbkqxvz.m_02ljsooog();
         var2.m_50mpmyuzs(new Runnable() {
            @Override
            public void run() {
               C_34czhuwfp.this.f_74paoaycu.remove(var2);
            }
         });
         boolean var3 = this.f_74paoaycu.offer(var2);
         if (!var3) {
            var2.m_58dhnfokw();
         }

         var4 = var3;
      } finally {
         c_20vbkqxvz.m_72zxisjbx().unlock();
      }

      return var4;
   }

   public void m_25avnwtaz(C_19lmmzlfd c_19lmmzlfd) {
      this.f_60gegzcnc.add(c_19lmmzlfd);
   }

   public C_19lmmzlfd m_11ghcjbgi() {
      return (C_19lmmzlfd)this.f_60gegzcnc.take();
   }

   public C_06pcqnnli m_52lzqahkn() {
      return (C_06pcqnnli)this.f_74paoaycu.take();
   }

   public boolean m_25bvfbldo(ChunkBlockRenderer c_20vbkqxvz) {
      c_20vbkqxvz.m_72zxisjbx().lock();

      boolean var3;
      try {
         final C_06pcqnnli var2 = c_20vbkqxvz.m_15lwncgpq();
         if (var2 == null) {
            return true;
         }

         var2.m_50mpmyuzs(new Runnable() {
            @Override
            public void run() {
               C_34czhuwfp.this.f_74paoaycu.remove(var2);
            }
         });
         var3 = this.f_74paoaycu.offer(var2);
      } finally {
         c_20vbkqxvz.m_72zxisjbx().unlock();
      }

      return var3;
   }

   public ListenableFuture m_61zmndvvh(BlockLayer c_26szrsafr, BufferBuilder c_36rbvvvbq, ChunkBlockRenderer c_20vbkqxvz, C_53fhzsins c_53fhzsins) {
      if (MinecraftClient.getInstance().isOnSameThread()) {
         if (GLX.useVbo()) {
            this.m_69jdkttkb(c_36rbvvvbq, c_20vbkqxvz.m_13kjyoqhl(c_26szrsafr.ordinal()));
         } else {
            this.m_87ttiuxau(c_36rbvvvbq, ((C_54lbowdqm)c_20vbkqxvz).m_47yrwtrmw(c_26szrsafr, c_53fhzsins), c_20vbkqxvz);
         }

         c_36rbvvvbq.offset(0.0, 0.0, 0.0);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask var5 = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
               C_34czhuwfp.this.m_61zmndvvh(c_26szrsafr, c_36rbvvvbq, c_20vbkqxvz, c_53fhzsins);
            }
         }, null);
         synchronized(this.f_09ipkrjfp) {
            this.f_09ipkrjfp.add(var5);
            return var5;
         }
      }
   }

   private void m_87ttiuxau(BufferBuilder c_36rbvvvbq, int i, ChunkBlockRenderer c_20vbkqxvz) {
      GL11.glNewList(i, 4864);
      GlStateManager.pushMatrix();
      c_20vbkqxvz.m_32thpmjwk();
      this.bufferUploader.end(c_36rbvvvbq, c_36rbvvvbq.getLimit());
      GlStateManager.popMatrix();
      GL11.glEndList();
   }

   private void m_69jdkttkb(BufferBuilder c_36rbvvvbq, VertexBuffer c_21yipcxzt) {
      this.vertexBufferUploader.setBuffer(c_21yipcxzt);
      this.vertexBufferUploader.end(c_36rbvvvbq, c_36rbvvvbq.getLimit());
   }

   public void m_13sztraxa() {
      this.f_74paoaycu.clear();
   }
}
