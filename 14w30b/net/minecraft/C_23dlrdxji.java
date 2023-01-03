package net.minecraft;

import java.util.Arrays;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldRegion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_23dlrdxji extends WorldRegion {
   private static final BlockState f_71ernvwzt = Blocks.AIR.defaultState();
   private final BlockPos f_80lpprgdm;
   private int[] f_09ixhpbwu;
   private BlockState[] f_11chkhrkc;

   public C_23dlrdxji(World c_54ruxjwzt, BlockPos c_76varpwca, BlockPos c_76varpwca2, int i) {
      super(c_54ruxjwzt, c_76varpwca, c_76varpwca2, i);
      this.f_80lpprgdm = c_76varpwca.subtract(new Vec3i(i, i, i));
      boolean var5 = true;
      this.f_09ixhpbwu = new int[8000];
      Arrays.fill(this.f_09ixhpbwu, -1);
      this.f_11chkhrkc = new BlockState[8000];
   }

   @Override
   public int getLightColor(BlockPos pos, int blockLight) {
      int var3 = this.m_27fajrhju(pos);
      int var4 = this.f_09ixhpbwu[var3];
      if (var4 == -1) {
         var4 = super.getLightColor(pos, blockLight);
         this.f_09ixhpbwu[var3] = var4;
      }

      return var4;
   }

   @Override
   public BlockState getBlockState(BlockPos pos) {
      int var2 = this.m_27fajrhju(pos);
      BlockState var3 = this.f_11chkhrkc[var2];
      if (var3 == null) {
         var3 = this.m_96kxegowl(pos);
         this.f_11chkhrkc[var2] = var3;
      }

      return var3;
   }

   private BlockState m_96kxegowl(BlockPos c_76varpwca) {
      if (c_76varpwca.getY() >= 0 && c_76varpwca.getY() < 256) {
         int var2 = (c_76varpwca.getX() >> 4) - this.chunkX;
         int var3 = (c_76varpwca.getZ() >> 4) - this.chunkZ;
         return this.chunks[var2][var3].getBlockState(c_76varpwca);
      } else {
         return f_71ernvwzt;
      }
   }

   private int m_27fajrhju(BlockPos c_76varpwca) {
      int var2 = c_76varpwca.getX() - this.f_80lpprgdm.getX();
      int var3 = c_76varpwca.getY() - this.f_80lpprgdm.getY();
      int var4 = c_76varpwca.getZ() - this.f_80lpprgdm.getZ();
      return var2 * 400 + var4 * 20 + var3;
   }
}
