package com.mojang.blaze3d.shaders;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import java.io.BufferedInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.render.Effect;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.resource.Identifier;
import net.minecraft.server.ChainedJsonException;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

@Environment(EnvType.CLIENT)
public class Program {
   private final Program.Type type;
   private final String name;
   private int id;
   private int references = 0;

   private Program(Program.Type type, int id, String name) {
      this.type = type;
      this.id = id;
      this.name = name;
   }

   public void attachToEffect(Effect effect) {
      ++this.references;
      GLX.attachShader(effect.getId(), this.id);
   }

   public void close(Effect effect) {
      --this.references;
      if (this.references <= 0) {
         GLX.deleteShader(this.id);
         this.type.getPrograms().remove(this.name);
      }
   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(IResourceManager resourceManager, Program.Type type, String name) {
      Program var3 = (Program)type.getPrograms().get(name);
      if (var3 == null) {
         Identifier var4 = new Identifier("shaders/program/" + name + type.getExtension());
         BufferedInputStream var5 = new BufferedInputStream(resourceManager.getResource(var4).asStream());
         byte[] var6 = toByteArray(var5);
         ByteBuffer var7 = BufferUtils.createByteBuffer(var6.length);
         var7.put(var6);
         ((Buffer)var7).position(0);
         int var8 = GLX.createShader(type.getGlType());
         GLX.shaderSource(var8, var7);
         GLX.compileShader(var8);
         if (GLX.getShader(var8, GLX.GL_COMPILE_STATUS) == 0) {
            String var9 = StringUtils.trim(GLX.getShaderInfoLog(var8, 32768));
            ChainedJsonException var10 = new ChainedJsonException("Couldn't compile " + type.getName() + " program: " + var9);
            var10.setFileNameAndFlush(var4.getPath());
            throw var10;
         }

         var3 = new Program(type, var8, name);
         type.getPrograms().put(name, var3);
      }

      return var3;
   }

   protected static byte[] toByteArray(BufferedInputStream bis) {
      byte[] var1;
      try {
         var1 = IOUtils.toByteArray(bis);
      } finally {
         bis.close();
      }

      return var1;
   }

   @Environment(EnvType.CLIENT)
   public static enum Type {
      VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

      private final String name;
      private final String extension;
      private final int glType;
      private final Map programs = Maps.newHashMap();

      private Type(String name, String extension, int glType) {
         this.name = name;
         this.extension = extension;
         this.glType = glType;
      }

      public String getName() {
         return this.name;
      }

      protected String getExtension() {
         return this.extension;
      }

      protected int getGlType() {
         return this.glType;
      }

      protected Map getPrograms() {
         return this.programs;
      }
   }
}
