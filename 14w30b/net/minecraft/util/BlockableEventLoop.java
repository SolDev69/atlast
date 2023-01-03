package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;

public interface BlockableEventLoop {
   ListenableFuture submit(Runnable event);

   boolean isOnSameThread();
}
