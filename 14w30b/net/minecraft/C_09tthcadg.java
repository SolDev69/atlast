package net.minecraft;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_09tthcadg {
   private static final int f_02zcmpptx = (int)Math.pow(16.0, 0.0);
   private static final int f_94yeuwbkk = (int)Math.pow(16.0, 1.0);
   private static final int f_01ehklusu = (int)Math.pow(16.0, 2.0);
   private final BitSet f_01geceeyt = new BitSet(4096);
   private static final int[] f_01ewjswvy = new int[1352];
   private int f_65gqapzek = 4096;

   public void m_76wlglmxq(BlockPos c_76varpwca) {
      this.f_01geceeyt.set(m_95kxpebot(c_76varpwca), true);
      --this.f_65gqapzek;
   }

   private static int m_95kxpebot(BlockPos c_76varpwca) {
      return m_19utpmegs(c_76varpwca.getX() & 15, c_76varpwca.getY() & 15, c_76varpwca.getZ() & 15);
   }

   private static int m_19utpmegs(int i, int j, int k) {
      return i << 0 | j << 8 | k << 4;
   }

   public C_14adyejqf m_42iwfbuhe() {
      C_14adyejqf var1 = new C_14adyejqf();
      if (4096 - this.f_65gqapzek < 256) {
         var1.m_95knsiril(true);
      } else if (this.f_65gqapzek == 0) {
         var1.m_95knsiril(false);
      } else {
         for(int var5 : f_01ewjswvy) {
            if (!this.f_01geceeyt.get(var5)) {
               var1.m_98ttinzkn(this.m_18biknghe(var5));
            }
         }
      }

      return var1;
   }

   public Set m_22ngzuorx(BlockPos c_76varpwca) {
      return this.m_18biknghe(m_95kxpebot(c_76varpwca));
   }

   private Set m_18biknghe(int i) {
      EnumSet var2 = EnumSet.noneOf(Direction.class);
      LinkedList var3 = Lists.newLinkedList();
      var3.add(i);
      this.f_01geceeyt.set(i, true);

      while(!var3.isEmpty()) {
         int var4 = var3.poll();
         this.m_98tfhbrky(var4, var2);

         for(Direction var8 : Direction.values()) {
            int var9 = this.m_70rfwtbub(var4, var8);
            if (var9 >= 0 && !this.f_01geceeyt.get(var9)) {
               this.f_01geceeyt.set(var9, true);
               var3.add(var9);
            }
         }
      }

      return var2;
   }

   private void m_98tfhbrky(int i, Set set) {
      int var3 = i >> 0 & 15;
      if (var3 == 0) {
         set.add(Direction.WEST);
      } else if (var3 == 15) {
         set.add(Direction.EAST);
      }

      int var4 = i >> 8 & 15;
      if (var4 == 0) {
         set.add(Direction.DOWN);
      } else if (var4 == 15) {
         set.add(Direction.UP);
      }

      int var5 = i >> 4 & 15;
      if (var5 == 0) {
         set.add(Direction.NORTH);
      } else if (var5 == 15) {
         set.add(Direction.SOUTH);
      }
   }

   private int m_70rfwtbub(int i, Direction c_69garkogr) {
      switch(c_69garkogr) {
         case DOWN:
            if ((i >> 8 & 15) == 0) {
               return -1;
            }

            return i - f_01ehklusu;
         case UP:
            if ((i >> 8 & 15) == 15) {
               return -1;
            }

            return i + f_01ehklusu;
         case NORTH:
            if ((i >> 4 & 15) == 0) {
               return -1;
            }

            return i - f_94yeuwbkk;
         case SOUTH:
            if ((i >> 4 & 15) == 15) {
               return -1;
            }

            return i + f_94yeuwbkk;
         case WEST:
            if ((i >> 0 & 15) == 0) {
               return -1;
            }

            return i - f_02zcmpptx;
         case EAST:
            if ((i >> 0 & 15) == 15) {
               return -1;
            }

            return i + f_02zcmpptx;
         default:
            return -1;
      }
   }

   static {
      boolean var0 = false;
      boolean var1 = true;
      int var2 = 0;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               if (var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  f_01ewjswvy[var2++] = m_19utpmegs(var3, var4, var5);
               }
            }
         }
      }
   }
}
