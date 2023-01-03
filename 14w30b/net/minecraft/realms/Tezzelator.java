package net.minecraft.realms;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tezzelator {
   public static Tessellator f_13kudkmyn = Tessellator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public int end() {
      return f_13kudkmyn.end();
   }

   public void vertex(double d, double e, double f) {
      f_13kudkmyn.getBufferBuilder().vertex(d, e, f);
   }

   public void color(float f, float g, float h, float i) {
      f_13kudkmyn.getBufferBuilder().color(f, g, h, i);
   }

   public void color(int i, int j, int k) {
      f_13kudkmyn.getBufferBuilder().color(i, j, k);
   }

   public void tex2(int i) {
      f_13kudkmyn.getBufferBuilder().brightness(i);
   }

   public void normal(float f, float g, float h) {
      f_13kudkmyn.getBufferBuilder().normal(f, g, h);
   }

   public void noColor() {
      f_13kudkmyn.getBufferBuilder().uncolored();
   }

   public void color(int i) {
      f_13kudkmyn.getBufferBuilder().color(i);
   }

   public void color(float f, float g, float h) {
      f_13kudkmyn.getBufferBuilder().color(f, g, h);
   }

   public BufferBuilder.State sortQuads(float f, float g, float h) {
      return f_13kudkmyn.getBufferBuilder().buildState(f, g, h);
   }

   public void restoreState(BufferBuilder.State c_16gttdmcd) {
      f_13kudkmyn.getBufferBuilder().setState(c_16gttdmcd);
   }

   public void begin(int i) {
      f_13kudkmyn.getBufferBuilder().start(i);
   }

   public void begin() {
      f_13kudkmyn.getBufferBuilder().start();
   }

   public void vertexUV(double d, double e, double f, double g, double h) {
      f_13kudkmyn.getBufferBuilder().vertex(d, e, f, g, h);
   }

   public void color(int i, int j) {
      f_13kudkmyn.getBufferBuilder().color(i, j);
   }

   public void offset(double d, double e, double f) {
      f_13kudkmyn.getBufferBuilder().offset(d, e, f);
   }

   public void color(int i, int j, int k, int l) {
      f_13kudkmyn.getBufferBuilder().color(i, j, k, l);
   }

   public void tex(double d, double e) {
      f_13kudkmyn.getBufferBuilder().texture(d, e);
   }

   public void color(byte b, byte c, byte d) {
      f_13kudkmyn.getBufferBuilder().color(b, c, d);
   }
}
