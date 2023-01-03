package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.util.Int2ObjectHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public abstract class AbstractPathNodeHelper {
   protected IWorld world;
   protected Int2ObjectHashMap f_56eixqmlm = new Int2ObjectHashMap();
   protected int f_87dzilfcv;
   protected int f_66qiwusrg;
   protected int f_63mfmhfjz;

   public void m_41nzanimf(IWorld world, Entity entity) {
      this.world = world;
      this.f_56eixqmlm.clear();
      this.f_87dzilfcv = MathHelper.floor(entity.width + 1.0F);
      this.f_66qiwusrg = MathHelper.floor(entity.height + 1.0F);
      this.f_63mfmhfjz = MathHelper.floor(entity.width + 1.0F);
   }

   public void m_46ezxzbdo() {
   }

   protected PathNode m_28lslnoqn(int i, int j, int k) {
      int var4 = PathNode.hash(i, j, k);
      PathNode var5 = (PathNode)this.f_56eixqmlm.get(var4);
      if (var5 == null) {
         var5 = new PathNode(i, j, k);
         this.f_56eixqmlm.put(var4, var5);
      }

      return var5;
   }

   public abstract PathNode m_17vajbguf(Entity c_47ldwddrb);

   public abstract PathNode m_97krmhugx(Entity c_47ldwddrb, double d, double e, double f);

   public abstract int m_27ozwikog(PathNode[] c_27ysujmcns, Entity c_47ldwddrb, PathNode c_27ysujmcn, PathNode c_27ysujmcn2, float f);
}
