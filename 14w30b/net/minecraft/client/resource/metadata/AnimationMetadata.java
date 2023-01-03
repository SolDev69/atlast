package net.minecraft.client.resource.metadata;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationMetadata implements ResourceMetadataSection {
   private final List frames;
   private final int width;
   private final int height;
   private final int time;
   private final boolean interpolated;

   public AnimationMetadata(List frames, int width, int height, int time, boolean interpolated) {
      this.frames = frames;
      this.width = width;
      this.height = height;
      this.time = time;
      this.interpolated = interpolated;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getFrameCount() {
      return this.frames.size();
   }

   public int getTime() {
      return this.time;
   }

   public boolean isInterpolated() {
      return this.interpolated;
   }

   private AnimationFrame get(int i) {
      return (AnimationFrame)this.frames.get(i);
   }

   public int getTime(int i) {
      AnimationFrame var2 = this.get(i);
      return var2.usesDefaultFrameTime() ? this.time : var2.getTime();
   }

   public boolean usesNonDefaultFrameTime(int i) {
      return !((AnimationFrame)this.frames.get(i)).usesDefaultFrameTime();
   }

   public int getIndex(int i) {
      return ((AnimationFrame)this.frames.get(i)).getIndex();
   }

   public Set getIndices() {
      HashSet var1 = Sets.newHashSet();

      for(AnimationFrame var3 : this.frames) {
         var1.add(var3.getIndex());
      }

      return var1;
   }
}
