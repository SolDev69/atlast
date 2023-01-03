package net.minecraft;

import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_64kccbmvr {
   protected final WorldRenderer f_61ierhwig;
   protected final World f_83acveqpi;
   protected int f_84vwkcshc;
   protected int f_17zdthaza;
   protected int f_59zbrzxzu;
   public ChunkBlockRenderer[] chunkSections;

   public C_64kccbmvr(World c_54ruxjwzt, int i, WorldRenderer c_07tveanoq, C_42nteilvc c_42nteilvc) {
      this.f_61ierhwig = c_07tveanoq;
      this.f_83acveqpi = c_54ruxjwzt;
      this.m_15oifirwh(i);
      this.m_83gmcwyrx(c_42nteilvc);
   }

   protected void m_83gmcwyrx(C_42nteilvc c_42nteilvc) {
      int var2 = this.f_17zdthaza * this.f_84vwkcshc * this.f_59zbrzxzu;
      this.chunkSections = new ChunkBlockRenderer[var2];
      int var3 = 0;

      for(int var4 = 0; var4 < this.f_17zdthaza; ++var4) {
         for(int var5 = 0; var5 < this.f_84vwkcshc; ++var5) {
            for(int var6 = 0; var6 < this.f_59zbrzxzu; ++var6) {
               int var7 = (var6 * this.f_84vwkcshc + var5) * this.f_17zdthaza + var4;
               BlockPos var8 = new BlockPos(var4 * 16, var5 * 16, var6 * 16);
               this.chunkSections[var7] = c_42nteilvc.m_84vrqgyim(this.f_83acveqpi, this.f_61ierhwig, var8, var3++);
            }
         }
      }
   }

   public void m_89qbnuvdx() {
      for(ChunkBlockRenderer var4 : this.chunkSections) {
         var4.resetRenderStagesAndWorld();
      }
   }

   protected void m_15oifirwh(int i) {
      int var2 = i * 2 + 1;
      this.f_17zdthaza = var2;
      this.f_84vwkcshc = 16;
      this.f_59zbrzxzu = var2;
   }

   public void m_17tqbccfz(double x, double z) {
      int var5 = MathHelper.floor(x) - 8;
      int var6 = MathHelper.floor(z) - 8;
      int var7 = this.f_17zdthaza * 16;

      for(int var8 = 0; var8 < this.f_17zdthaza; ++var8) {
         int var9 = this.m_52cnltndo(var5, var7, var8);

         for(int var10 = 0; var10 < this.f_59zbrzxzu; ++var10) {
            int var11 = this.m_52cnltndo(var6, var7, var10);

            for(int var12 = 0; var12 < this.f_84vwkcshc; ++var12) {
               int var13 = var12 * 16;
               ChunkBlockRenderer var14 = this.chunkSections[(var10 * this.f_84vwkcshc + var12) * this.f_17zdthaza + var8];
               BlockPos var15 = new BlockPos(var9, var13, var11);
               if (!var15.equals(var14.m_97olwzrjj())) {
                  var14.m_25eilbxne(var15);
               }
            }
         }
      }
   }

   private int m_52cnltndo(int i, int j, int k) {
      int var4 = k * 16;
      int var5 = var4 - i + j / 2;
      if (var5 < 0) {
         var5 -= j - 1;
      }

      return var4 - var5 / j * j;
   }

   public void m_23nxycbeg(int i, int j, int k, int l, int m, int n) {
      int var7 = MathHelper.floorDiv(i, 16);
      int var8 = MathHelper.floorDiv(j, 16);
      int var9 = MathHelper.floorDiv(k, 16);
      int var10 = MathHelper.floorDiv(l, 16);
      int var11 = MathHelper.floorDiv(m, 16);
      int var12 = MathHelper.floorDiv(n, 16);

      for(int var13 = var7; var13 <= var10; ++var13) {
         int var14 = var13 % this.f_17zdthaza;
         if (var14 < 0) {
            var14 += this.f_17zdthaza;
         }

         for(int var15 = var8; var15 <= var11; ++var15) {
            int var16 = var15 % this.f_84vwkcshc;
            if (var16 < 0) {
               var16 += this.f_84vwkcshc;
            }

            for(int var17 = var9; var17 <= var12; ++var17) {
               int var18 = var17 % this.f_59zbrzxzu;
               if (var18 < 0) {
                  var18 += this.f_59zbrzxzu;
               }

               int var19 = (var18 * this.f_84vwkcshc + var16) * this.f_17zdthaza + var14;
               ChunkBlockRenderer var20 = this.chunkSections[var19];
               var20.m_92dylrpem(true);
            }
         }
      }
   }

   protected ChunkBlockRenderer m_73iynvurl(BlockPos c_76varpwca) {
      int var2 = MathHelper.floorDiv(c_76varpwca.getX(), 16);
      int var3 = MathHelper.floorDiv(c_76varpwca.getY(), 16);
      int var4 = MathHelper.floorDiv(c_76varpwca.getZ(), 16);
      if (var3 >= 0 && var3 < this.f_84vwkcshc) {
         var2 %= this.f_17zdthaza;
         if (var2 < 0) {
            var2 += this.f_17zdthaza;
         }

         var4 %= this.f_59zbrzxzu;
         if (var4 < 0) {
            var4 += this.f_59zbrzxzu;
         }

         int var5 = (var4 * this.f_84vwkcshc + var3) * this.f_17zdthaza + var2;
         return this.chunkSections[var5];
      } else {
         return null;
      }
   }
}
