package net.minecraft.client.render.world;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.C_06pcqnnli;
import net.minecraft.C_09tthcadg;
import net.minecraft.C_23dlrdxji;
import net.minecraft.C_53fhzsins;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkBlockRenderer {
   private World world;
   private final WorldRenderer f_99imgxhcl;
   public static int currentChunkUpdates;
   private BlockPos f_16ivejsjf;
   public C_53fhzsins f_10rxmxrqu = C_53fhzsins.f_89lpuxuhd;
   private final ReentrantLock f_85qxrcrrf = new ReentrantLock();
   private final ReentrantLock f_33pfoqfaz = new ReentrantLock();
   private C_06pcqnnli f_70tykzqtf = null;
   private final int f_89efaxgqz;
   private final FloatBuffer f_64ydjxhrz = MemoryTracker.createFloatBuffer(16);
   private final VertexBuffer[] f_97slmurjv = new VertexBuffer[BlockLayer.values().length];
   public Box f_59rfoaecb;
   private int f_86ndyudce = -1;
   private boolean f_78yhgyvkc = true;

   public ChunkBlockRenderer(World world, WorldRenderer blockEntities, BlockPos x, int y) {
      this.world = world;
      this.f_99imgxhcl = blockEntities;
      this.f_89efaxgqz = y;
      if (!x.equals(this.m_97olwzrjj())) {
         this.m_25eilbxne(x);
      }

      if (GLX.useVbo()) {
         for(int var5 = 0; var5 < BlockLayer.values().length; ++var5) {
            this.f_97slmurjv[var5] = new VertexBuffer(DefaultVertexFormat.BLOCK);
         }
      }
   }

   public boolean m_03zdnqaut(int i) {
      if (this.f_86ndyudce == i) {
         return false;
      } else {
         this.f_86ndyudce = i;
         return true;
      }
   }

   public VertexBuffer m_13kjyoqhl(int i) {
      return this.f_97slmurjv[i];
   }

   public void m_25eilbxne(BlockPos c_76varpwca) {
      this.resetRenderStages();
      this.f_16ivejsjf = c_76varpwca;
      this.f_59rfoaecb = new Box(c_76varpwca, c_76varpwca.add(16, 16, 16));
      this.m_30clwllqs();
   }

   public void m_71cycciik(float f, float g, float h, C_06pcqnnli c_60jrwydru) {
      C_53fhzsins var5 = c_60jrwydru.m_44zqxragh();
      if (var5.m_57mivhtew() != null && !var5.m_66hqbsyfn(BlockLayer.TRANSLUCENT)) {
         this.m_49lufxbzs(c_60jrwydru.m_52lxfshlo().m_12wnljgwg(BlockLayer.TRANSLUCENT), this.f_16ivejsjf);
         c_60jrwydru.m_52lxfshlo().m_12wnljgwg(BlockLayer.TRANSLUCENT).setState(var5.m_57mivhtew());
         this.m_07wkzhegp(BlockLayer.TRANSLUCENT, f, g, h, c_60jrwydru.m_52lxfshlo().m_12wnljgwg(BlockLayer.TRANSLUCENT), var5);
      }
   }

   public void m_87gkfktay(float f, float g, float h, C_06pcqnnli c_60jrwydru) {
      C_53fhzsins var5 = new C_53fhzsins();
      boolean var6 = true;
      BlockPos var7 = this.f_16ivejsjf;
      BlockPos var8 = var7.add(15, 15, 15);
      c_60jrwydru.m_89robiyro().lock();

      C_23dlrdxji var9;
      try {
         if (c_60jrwydru.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.COMPILING) {
            return;
         }

         var9 = new C_23dlrdxji(this.world, var7.add(-1, -1, -1), var8.add(1, 1, 1), 1);
         c_60jrwydru.m_73swxuixb(var5);
      } finally {
         c_60jrwydru.m_89robiyro().unlock();
      }

      C_09tthcadg var10 = new C_09tthcadg();
      if (!var9.isSaved()) {
         ++currentChunkUpdates;

         for(BlockPos.Mutable var12 : BlockPos.iterateRegionMutable(var7, var8)) {
            BlockState var13 = var9.getBlockState(var12);
            Block var14 = var13.getBlock();
            if (var14.isOpaqueCube()) {
               var10.m_76wlglmxq(var12);
            }

            if (var14.hasBlockEntity()) {
               BlockEntity var15 = var9.getBlockEntity(new BlockPos(var12));
               if (var15 != null && BlockEntityRenderDispatcher.INSTANCE.hasRenderer(var15)) {
                  var5.m_93lexfuxc(var15);
               }
            }

            BlockLayer var24 = var14.getRenderLayer();
            int var16 = var24.ordinal();
            if (var14.getRenderType() != -1) {
               BufferBuilder var17 = c_60jrwydru.m_52lxfshlo().m_45etpyycn(var16);
               if (!var5.m_06fysprsm(var24)) {
                  var5.m_45hmylvpy(var24);
                  this.m_49lufxbzs(var17, var7);
               }

               if (MinecraftClient.getInstance().getBlockRenderDispatcher().render(var13, var12, var9, var17)) {
                  var5.m_55pxkkajr(var24);
               }
            }
         }

         for(BlockLayer var23 : BlockLayer.values()) {
            if (var5.m_06fysprsm(var23)) {
               this.m_07wkzhegp(var23, f, g, h, c_60jrwydru.m_52lxfshlo().m_12wnljgwg(var23), var5);
            }
         }
      }

      var5.m_36mfoqryn(var10.m_42iwfbuhe());
   }

   protected void m_85sihpnmz() {
      this.f_85qxrcrrf.lock();

      try {
         if (this.f_70tykzqtf != null && this.f_70tykzqtf.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.DONE) {
            this.f_70tykzqtf.m_58dhnfokw();
            this.f_70tykzqtf = null;
         }
      } finally {
         this.f_85qxrcrrf.unlock();
      }
   }

   public ReentrantLock m_72zxisjbx() {
      return this.f_85qxrcrrf;
   }

   public C_06pcqnnli m_02ljsooog() {
      this.f_85qxrcrrf.lock();

      C_06pcqnnli var1;
      try {
         this.m_85sihpnmz();
         this.f_70tykzqtf = new C_06pcqnnli(this, C_06pcqnnli.C_77ddrvmit.REBUILD_CHUNK);
         var1 = this.f_70tykzqtf;
      } finally {
         this.f_85qxrcrrf.unlock();
      }

      return var1;
   }

   public C_06pcqnnli m_15lwncgpq() {
      this.f_85qxrcrrf.lock();

      Object var1;
      try {
         if (this.f_70tykzqtf == null || this.f_70tykzqtf.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.PENDING) {
            if (this.f_70tykzqtf != null && this.f_70tykzqtf.m_23eezatmj() != C_06pcqnnli.C_45bvstooc.DONE) {
               this.f_70tykzqtf.m_58dhnfokw();
               this.f_70tykzqtf = null;
            }

            this.f_70tykzqtf = new C_06pcqnnli(this, C_06pcqnnli.C_77ddrvmit.RESORT_TRANSPARENCY);
            this.f_70tykzqtf.m_73swxuixb(this.f_10rxmxrqu);
            return this.f_70tykzqtf;
         }

         var1 = null;
      } finally {
         this.f_85qxrcrrf.unlock();
      }

      return (C_06pcqnnli)var1;
   }

   private void m_49lufxbzs(BufferBuilder c_36rbvvvbq, BlockPos c_76varpwca) {
      c_36rbvvvbq.start(7);
      c_36rbvvvbq.format(DefaultVertexFormat.BLOCK);
      c_36rbvvvbq.offset((double)(-c_76varpwca.getX()), (double)(-c_76varpwca.getY()), (double)(-c_76varpwca.getZ()));
   }

   private void m_07wkzhegp(BlockLayer c_26szrsafr, float f, float g, float h, BufferBuilder c_36rbvvvbq, C_53fhzsins c_53fhzsins) {
      if (c_26szrsafr == BlockLayer.TRANSLUCENT && !c_53fhzsins.m_66hqbsyfn(c_26szrsafr)) {
         c_53fhzsins.m_87tamwlry(c_36rbvvvbq.buildState(f, g, h));
      }

      c_36rbvvvbq.end();
   }

   private void m_30clwllqs() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float var1 = 1.000001F;
      GlStateManager.translatef(-8.0F, -8.0F, -8.0F);
      GlStateManager.scalef(var1, var1, var1);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      GlStateManager.getFloat(2982, this.f_64ydjxhrz);
      GlStateManager.popMatrix();
   }

   public void m_32thpmjwk() {
      GlStateManager.multMatrix(this.f_64ydjxhrz);
   }

   public C_53fhzsins m_86rgndiak() {
      return this.f_10rxmxrqu;
   }

   public void m_03gmrikwy(C_53fhzsins c_53fhzsins) {
      this.f_33pfoqfaz.lock();

      try {
         C_53fhzsins var2 = this.f_10rxmxrqu;
         HashSet var3 = Sets.newHashSet();
         HashSet var4 = Sets.newHashSet();
         var3.addAll(c_53fhzsins.m_64xgzhpzi());
         var3.removeAll(var2.m_64xgzhpzi());
         var4.addAll(var2.m_64xgzhpzi());
         var4.removeAll(c_53fhzsins.m_64xgzhpzi());
         this.f_99imgxhcl.m_89tdlskcc(var4, var3);
         this.f_10rxmxrqu = c_53fhzsins;
      } finally {
         this.f_33pfoqfaz.unlock();
      }
   }

   public void resetRenderStages() {
      this.m_85sihpnmz();
      this.f_10rxmxrqu = C_53fhzsins.f_89lpuxuhd;
   }

   public void resetRenderStagesAndWorld() {
      this.resetRenderStages();
      this.world = null;

      for(int var1 = 0; var1 < BlockLayer.values().length; ++var1) {
         if (this.f_97slmurjv[var1] != null) {
            this.f_97slmurjv[var1].delete();
         }
      }
   }

   public BlockPos m_97olwzrjj() {
      return this.f_16ivejsjf;
   }

   public boolean m_97tfqykgn() {
      this.f_85qxrcrrf.lock();

      boolean var1;
      try {
         var1 = this.f_70tykzqtf == null || this.f_70tykzqtf.m_23eezatmj() == C_06pcqnnli.C_45bvstooc.PENDING;
      } finally {
         this.f_85qxrcrrf.unlock();
      }

      return var1;
   }

   public void m_92dylrpem(boolean bl) {
      this.f_78yhgyvkc = bl;
   }

   public boolean m_68asckfgm() {
      return this.f_78yhgyvkc;
   }
}
