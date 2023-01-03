package net.minecraft;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_32uniyyqe {
   private final Object[] f_51pghajpf;
   private final Class f_60cymbses;
   private final ReadWriteLock f_03szoumvh = new ReentrantReadWriteLock();
   private int f_88exuzaem;
   private int f_90bidduve;

   public C_32uniyyqe(Class class_, int i) {
      this.f_60cymbses = class_;
      this.f_51pghajpf = Array.newInstance(class_, i);
   }

   public Object m_38clnhxnq(Object object) {
      this.f_03szoumvh.writeLock().lock();
      this.f_51pghajpf[this.f_90bidduve] = object;
      this.f_90bidduve = (this.f_90bidduve + 1) % this.m_78tppshlu();
      if (this.f_88exuzaem < this.m_78tppshlu()) {
         ++this.f_88exuzaem;
      }

      this.f_03szoumvh.writeLock().unlock();
      return object;
   }

   public int m_78tppshlu() {
      this.f_03szoumvh.readLock().lock();
      int var1 = this.f_51pghajpf.length;
      this.f_03szoumvh.readLock().unlock();
      return var1;
   }

   public Object[] m_06dcduyhp() {
      Object[] var1 = (Object[])Array.newInstance(this.f_60cymbses, this.f_88exuzaem);
      this.f_03szoumvh.readLock().lock();

      for(int var2 = 0; var2 < this.f_88exuzaem; ++var2) {
         int var3 = (this.f_90bidduve - this.f_88exuzaem + var2) % this.m_78tppshlu();
         if (var3 < 0) {
            var3 += this.m_78tppshlu();
         }

         var1[var2] = this.f_51pghajpf[var3];
      }

      this.f_03szoumvh.readLock().unlock();
      return var1;
   }
}
