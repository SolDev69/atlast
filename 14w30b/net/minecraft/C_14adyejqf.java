package net.minecraft;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_14adyejqf {
   private static final int f_91zahixbk = Direction.values().length;
   private final BitSet f_25jphyxme = new BitSet(f_91zahixbk * f_91zahixbk);

   public void m_98ttinzkn(Set set) {
      for(Direction var3 : set) {
         for(Direction var5 : set) {
            this.m_40ipgszsb(var3, var5, true);
         }
      }
   }

   public void m_40ipgszsb(Direction c_69garkogr, Direction c_69garkogr2, boolean bl) {
      this.f_25jphyxme.set(c_69garkogr.ordinal() + c_69garkogr2.ordinal() * f_91zahixbk, bl);
      this.f_25jphyxme.set(c_69garkogr2.ordinal() + c_69garkogr.ordinal() * f_91zahixbk, bl);
   }

   public void m_95knsiril(boolean bl) {
      this.f_25jphyxme.set(0, this.f_25jphyxme.size(), bl);
   }

   public boolean m_77sbrlvvi(Direction c_69garkogr, Direction c_69garkogr2) {
      return this.f_25jphyxme.get(c_69garkogr.ordinal() + c_69garkogr2.ordinal() * f_91zahixbk);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(' ');

      for(Direction var5 : Direction.values()) {
         var1.append(' ').append(var5.toString().toUpperCase().charAt(0));
      }

      var1.append('\n');

      for(Direction var14 : Direction.values()) {
         var1.append(var14.toString().toUpperCase().charAt(0));

         for(Direction var9 : Direction.values()) {
            if (var14 == var9) {
               var1.append("  ");
            } else {
               boolean var10 = this.m_77sbrlvvi(var14, var9);
               var1.append(' ').append((char)(var10 ? 'Y' : 'n'));
            }
         }

         var1.append('\n');
      }

      return var1.toString();
   }
}
