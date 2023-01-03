package net.minecraft.world.chunk.storage.io;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class FileIoThread implements Runnable {
   private static final FileIoThread INSTANCE = new FileIoThread();
   private List callbacks = Collections.synchronizedList(Lists.newArrayList());
   private volatile long registered;
   private volatile long completed;
   private volatile boolean waiting;

   private FileIoThread() {
      Thread var1 = new Thread(this, "File IO Thread");
      var1.setPriority(1);
      var1.start();
   }

   public static FileIoThread getInstance() {
      return INSTANCE;
   }

   @Override
   public void run() {
      while(true) {
         this.runCallbacks();
      }
   }

   private void runCallbacks() {
      for(int var1 = 0; var1 < this.callbacks.size(); ++var1) {
         FileIoCallback var2 = (FileIoCallback)this.callbacks.get(var1);
         boolean var3 = var2.run();
         if (!var3) {
            this.callbacks.remove(var1--);
            ++this.completed;
         }

         try {
            Thread.sleep(this.waiting ? 0L : 10L);
         } catch (InterruptedException var6) {
            var6.printStackTrace();
         }
      }

      if (this.callbacks.isEmpty()) {
         try {
            Thread.sleep(25L);
         } catch (InterruptedException var5) {
            var5.printStackTrace();
         }
      }
   }

   public void registerCallback(FileIoCallback callback) {
      if (!this.callbacks.contains(callback)) {
         ++this.registered;
         this.callbacks.add(callback);
      }
   }

   public void waitUntilFinished() {
      this.waiting = true;

      while(this.registered != this.completed) {
         Thread.sleep(10L);
      }

      this.waiting = false;
   }
}
