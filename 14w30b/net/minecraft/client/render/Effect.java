package net.minecraft.client.render;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.DummyUniform;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.texture.Texture;
import net.minecraft.resource.Identifier;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Effect {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DummyUniform DUMMY_UNIFORM = new DummyUniform();
   private static Effect lastAppliedEffect = null;
   private static int lastProgramId = -1;
   private static boolean f_20fxxrzjz = true;
   private final Map samplerMap = Maps.newHashMap();
   private final List samplerNames = Lists.newArrayList();
   private final List samplerLocations = Lists.newArrayList();
   private final List uniforms = Lists.newArrayList();
   private final List uniformLocations = Lists.newArrayList();
   private final Map uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private final boolean cull;
   private boolean dirty;
   private final BlendMode blend;
   private final List attributes;
   private final List attributeNames;
   private final Program vertexProgram;
   private final Program fragmentProgram;

   public Effect(IResourceManager resourceManager, String name) {
      JsonParser var3 = new JsonParser();
      Identifier var4 = new Identifier("shaders/program/" + name + ".json");
      this.name = name;
      InputStream var5 = null;

      try {
         var5 = resourceManager.getResource(var4).asStream();
         JsonObject var6 = var3.parse(IOUtils.toString(var5, Charsets.UTF_8)).getAsJsonObject();
         String var7 = JsonUtils.getString(var6, "vertex");
         String var28 = JsonUtils.getString(var6, "fragment");
         JsonArray var9 = JsonUtils.getJsonArrayOrDefault(var6, "samplers", null);
         if (var9 != null) {
            int var10 = 0;

            for(JsonElement var12 : var9) {
               try {
                  this.parseSamplerNode(var12);
               } catch (Exception var25) {
                  ChainedJsonException var14 = ChainedJsonException.forException(var25);
                  var14.prependJsonKey("samplers[" + var10 + "]");
                  throw var14;
               }

               ++var10;
            }
         }

         JsonArray var29 = JsonUtils.getJsonArrayOrDefault(var6, "attributes", null);
         if (var29 != null) {
            int var30 = 0;
            this.attributes = Lists.newArrayListWithCapacity(var29.size());
            this.attributeNames = Lists.newArrayListWithCapacity(var29.size());

            for(JsonElement var13 : var29) {
               try {
                  this.attributeNames.add(JsonUtils.asString(var13, "attribute"));
               } catch (Exception var24) {
                  ChainedJsonException var15 = ChainedJsonException.forException(var24);
                  var15.prependJsonKey("attributes[" + var30 + "]");
                  throw var15;
               }

               ++var30;
            }
         } else {
            this.attributes = null;
            this.attributeNames = null;
         }

         JsonArray var31 = JsonUtils.getJsonArrayOrDefault(var6, "uniforms", null);
         if (var31 != null) {
            int var33 = 0;

            for(JsonElement var37 : var31) {
               try {
                  this.parseUniformNode(var37);
               } catch (Exception var23) {
                  ChainedJsonException var16 = ChainedJsonException.forException(var23);
                  var16.prependJsonKey("uniforms[" + var33 + "]");
                  throw var16;
               }

               ++var33;
            }
         }

         this.blend = BlendMode.fromJson(JsonUtils.getJsonObjectOrDefault(var6, "blend", null));
         this.cull = JsonUtils.getBooleanOrDefault(var6, "cull", true);
         this.vertexProgram = Program.compileShader(resourceManager, Program.Type.VERTEX, var7);
         this.fragmentProgram = Program.compileShader(resourceManager, Program.Type.FRAGMENT, var28);
         this.programId = ProgramManager.getInstance().createProgram();
         ProgramManager.getInstance().linkProgram(this);
         this.updateLocations();
         if (this.attributeNames != null) {
            for(String var36 : this.attributeNames) {
               int var38 = GLX.getAttribLocation(this.programId, var36);
               this.attributes.add(var38);
            }
         }
      } catch (Exception var26) {
         ChainedJsonException var8 = ChainedJsonException.forException(var26);
         var8.setFileNameAndFlush(var4.getPath());
         throw var8;
      } finally {
         IOUtils.closeQuietly(var5);
      }

      this.markDirty();
   }

   public void close() {
      ProgramManager.getInstance().releaseProgram(this);
   }

   public void clear() {
      GLX.useProgram(0);
      lastProgramId = -1;
      lastAppliedEffect = null;
      f_20fxxrzjz = true;

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if (this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.bindTexture(0);
         }
      }
   }

   public void apply() {
      this.dirty = false;
      lastAppliedEffect = this;
      this.blend.apply();
      if (this.programId != lastProgramId) {
         GLX.useProgram(this.programId);
         lastProgramId = this.programId;
      }

      if (this.cull) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if (this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.enableTexture();
            Object var2 = this.samplerMap.get(this.samplerNames.get(var1));
            int var3 = -1;
            if (var2 instanceof RenderTarget) {
               var3 = ((RenderTarget)var2).colorTextureId;
            } else if (var2 instanceof Texture) {
               var3 = ((Texture)var2).getGlId();
            } else if (var2 instanceof Integer) {
               var3 = (Integer)var2;
            }

            if (var3 != -1) {
               GlStateManager.bindTexture(var3);
               GLX.uniform1i(GLX.getUniformLocation(this.programId, (CharSequence)this.samplerNames.get(var1)), var1);
            }
         }
      }

      for(Uniform var5 : this.uniforms) {
         var5.upload();
      }
   }

   public void markDirty() {
      this.dirty = true;
   }

   public Uniform getUniform(String name) {
      return this.uniformMap.containsKey(name) ? (Uniform)this.uniformMap.get(name) : null;
   }

   public Uniform safeGetUniform(String name) {
      return (Uniform)(this.uniformMap.containsKey(name) ? (Uniform)this.uniformMap.get(name) : DUMMY_UNIFORM);
   }

   private void updateLocations() {
      int var1 = 0;

      for(int var2 = 0; var1 < this.samplerNames.size(); ++var2) {
         String var3 = (String)this.samplerNames.get(var1);
         int var4 = GLX.getUniformLocation(this.programId, var3);
         if (var4 == -1) {
            LOGGER.warn("Shader " + this.name + "could not find sampler named " + var3 + " in the specified shader program.");
            this.samplerMap.remove(var3);
            this.samplerNames.remove(var2);
            --var2;
         } else {
            this.samplerLocations.add(var4);
         }

         ++var1;
      }

      for(Uniform var6 : this.uniforms) {
         String var7 = var6.getName();
         int var8 = GLX.getUniformLocation(this.programId, var7);
         if (var8 == -1) {
            LOGGER.warn("Could not find uniform named " + var7 + " in the specified" + " shader program.");
         } else {
            this.uniformLocations.add(var8);
            var6.setLocation(var8);
            this.uniformMap.put(var7, var6);
         }
      }
   }

   private void parseSamplerNode(JsonElement json) {
      JsonObject var2 = JsonUtils.asJsonObject(json, "sampler");
      String var3 = JsonUtils.getString(var2, "name");
      if (!JsonUtils.hasString(var2, "file")) {
         this.samplerMap.put(var3, null);
         this.samplerNames.add(var3);
      } else {
         this.samplerNames.add(var3);
      }
   }

   public void setSampler(String name, Object sampler) {
      if (this.samplerMap.containsKey(name)) {
         this.samplerMap.remove(name);
      }

      this.samplerMap.put(name, sampler);
      this.markDirty();
   }

   private void parseUniformNode(JsonElement json) {
      JsonObject var2 = JsonUtils.asJsonObject(json, "uniform");
      String var3 = JsonUtils.getString(var2, "name");
      int var4 = Uniform.getTypeFromString(JsonUtils.getString(var2, "type"));
      int var5 = JsonUtils.getInteger(var2, "count");
      float[] var6 = new float[Math.max(var5, 16)];
      JsonArray var7 = JsonUtils.getJsonArray(var2, "values");
      if (var7.size() != var5 && var7.size() > 1) {
         throw new ChainedJsonException("Invalid amount of values specified (expected " + var5 + ", found " + var7.size() + ")");
      } else {
         int var8 = 0;

         for(JsonElement var10 : var7) {
            try {
               var6[var8] = JsonUtils.asFloat(var10, "value");
            } catch (Exception var13) {
               ChainedJsonException var12 = ChainedJsonException.forException(var13);
               var12.prependJsonKey("values[" + var8 + "]");
               throw var12;
            }

            ++var8;
         }

         if (var5 > 1 && var7.size() == 1) {
            while(var8 < var5) {
               var6[var8] = var6[0];
               ++var8;
            }
         }

         int var14 = var5 > 1 && var5 <= 4 && var4 < 8 ? var5 - 1 : 0;
         Uniform var15 = new Uniform(var3, var4 + var14, var5, this);
         if (var4 <= 3) {
            var15.setSafe((int)var6[0], (int)var6[1], (int)var6[2], (int)var6[3]);
         } else if (var4 <= 7) {
            var15.setSafe(var6[0], var6[1], var6[2], var6[3]);
         } else {
            var15.set(var6);
         }

         this.uniforms.add(var15);
      }
   }

   public Program getVertexProgram() {
      return this.vertexProgram;
   }

   public Program getFragmentProgram() {
      return this.fragmentProgram;
   }

   public int getId() {
      return this.programId;
   }
}
