package net.minecraft.client.render;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class PostChain {
   private RenderTarget screenTarget;
   private IResourceManager resourceManager;
   private String name;
   private final List passes = Lists.newArrayList();
   private final Map customRenderTargets = Maps.newHashMap();
   private final List fullSizedTargets = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;
   private int screenWidth;
   private int screenHeight;
   private float time;
   private float lastStamp;

   public PostChain(TextureManager textureManager, IResourceManager resourceManager, RenderTarget screenTarget, Identifier id) {
      this.resourceManager = resourceManager;
      this.screenTarget = screenTarget;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.screenWidth = screenTarget.viewWidth;
      this.screenHeight = screenTarget.viewHeight;
      this.name = id.toString();
      this.updateOrthoMatrix();
      this.load(textureManager, id);
   }

   public void load(TextureManager textureManager, Identifier id) {
      JsonParser var3 = new JsonParser();
      InputStream var4 = null;

      try {
         IResource var5 = this.resourceManager.getResource(id);
         var4 = var5.asStream();
         JsonObject var22 = var3.parse(IOUtils.toString(var4, Charsets.UTF_8)).getAsJsonObject();
         if (JsonUtils.hasJsonArray(var22, "targets")) {
            JsonArray var7 = var22.getAsJsonArray("targets");
            int var8 = 0;

            for(JsonElement var10 : var7) {
               try {
                  this.parseTargetNode(var10);
               } catch (Exception var19) {
                  ChainedJsonException var12 = ChainedJsonException.forException(var19);
                  var12.prependJsonKey("targets[" + var8 + "]");
                  throw var12;
               }

               ++var8;
            }
         }

         if (JsonUtils.hasJsonArray(var22, "passes")) {
            JsonArray var23 = var22.getAsJsonArray("passes");
            int var24 = 0;

            for(JsonElement var26 : var23) {
               try {
                  this.parsePassNode(textureManager, var26);
               } catch (Exception var18) {
                  ChainedJsonException var27 = ChainedJsonException.forException(var18);
                  var27.prependJsonKey("passes[" + var24 + "]");
                  throw var27;
               }

               ++var24;
            }
         }
      } catch (Exception var20) {
         ChainedJsonException var6 = ChainedJsonException.forException(var20);
         var6.setFileNameAndFlush(id.getPath());
         throw var6;
      } finally {
         IOUtils.closeQuietly(var4);
      }
   }

   private void parseTargetNode(JsonElement json) {
      if (JsonUtils.isString(json)) {
         this.addTempTarget(json.getAsString(), this.screenWidth, this.screenHeight);
      } else {
         JsonObject var2 = JsonUtils.asJsonObject(json, "target");
         String var3 = JsonUtils.getString(var2, "name");
         int var4 = JsonUtils.getIntegerOrDefault(var2, "width", this.screenWidth);
         int var5 = JsonUtils.getIntegerOrDefault(var2, "height", this.screenHeight);
         if (this.customRenderTargets.containsKey(var3)) {
            throw new ChainedJsonException(var3 + " is already defined");
         }

         this.addTempTarget(var3, var4, var5);
      }
   }

   private void parsePassNode(TextureManager textureManager, JsonElement json) {
      JsonObject var3 = JsonUtils.asJsonObject(json, "pass");
      String var4 = JsonUtils.getString(var3, "name");
      String var5 = JsonUtils.getString(var3, "intarget");
      String var6 = JsonUtils.getString(var3, "outtarget");
      RenderTarget var7 = this.getRenderTarget(var5);
      RenderTarget var8 = this.getRenderTarget(var6);
      if (var7 == null) {
         throw new ChainedJsonException("Input target '" + var5 + "' does not exist");
      } else if (var8 == null) {
         throw new ChainedJsonException("Output target '" + var6 + "' does not exist");
      } else {
         PostPass var9 = this.addPass(var4, var7, var8);
         JsonArray var10 = JsonUtils.getJsonArrayOrDefault(var3, "auxtargets", null);
         if (var10 != null) {
            int var11 = 0;

            for(JsonElement var13 : var10) {
               try {
                  JsonObject var14 = JsonUtils.asJsonObject(var13, "auxtarget");
                  String var30 = JsonUtils.getString(var14, "name");
                  String var16 = JsonUtils.getString(var14, "id");
                  RenderTarget var17 = this.getRenderTarget(var16);
                  if (var17 == null) {
                     Identifier var18 = new Identifier("textures/effect/" + var16 + ".png");

                     try {
                        this.resourceManager.getResource(var18);
                     } catch (FileNotFoundException var24) {
                        throw new ChainedJsonException("Render target or texture '" + var16 + "' does not exist");
                     }

                     textureManager.bind(var18);
                     Texture var19 = textureManager.getTexture(var18);
                     int var20 = JsonUtils.getInteger(var14, "width");
                     int var21 = JsonUtils.getInteger(var14, "height");
                     boolean var22 = JsonUtils.getBoolean(var14, "bilinear");
                     if (var22) {
                        GL11.glTexParameteri(3553, 10241, 9729);
                        GL11.glTexParameteri(3553, 10240, 9729);
                     } else {
                        GL11.glTexParameteri(3553, 10241, 9728);
                        GL11.glTexParameteri(3553, 10240, 9728);
                     }

                     var9.addAuxAsset(var30, var19.getGlId(), var20, var21);
                  } else {
                     var9.addAuxAsset(var30, var17, var17.width, var17.height);
                  }
               } catch (Exception var25) {
                  ChainedJsonException var15 = ChainedJsonException.forException(var25);
                  var15.prependJsonKey("auxtargets[" + var11 + "]");
                  throw var15;
               }

               ++var11;
            }
         }

         JsonArray var26 = JsonUtils.getJsonArrayOrDefault(var3, "uniforms", null);
         if (var26 != null) {
            int var27 = 0;

            for(JsonElement var29 : var26) {
               try {
                  this.parseUniformNode(var29);
               } catch (Exception var23) {
                  ChainedJsonException var31 = ChainedJsonException.forException(var23);
                  var31.prependJsonKey("uniforms[" + var27 + "]");
                  throw var31;
               }

               ++var27;
            }
         }
      }
   }

   private void parseUniformNode(JsonElement json) {
      JsonObject var2 = JsonUtils.asJsonObject(json, "uniform");
      String var3 = JsonUtils.getString(var2, "name");
      Uniform var4 = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform(var3);
      if (var4 == null) {
         throw new ChainedJsonException("Uniform '" + var3 + "' does not exist");
      } else {
         float[] var5 = new float[4];
         int var6 = 0;

         for(JsonElement var9 : JsonUtils.getJsonArray(var2, "values")) {
            try {
               var5[var6] = JsonUtils.asFloat(var9, "value");
            } catch (Exception var12) {
               ChainedJsonException var11 = ChainedJsonException.forException(var12);
               var11.prependJsonKey("values[" + var6 + "]");
               throw var11;
            }

            ++var6;
         }

         switch(var6) {
            case 0:
            default:
               break;
            case 1:
               var4.set(var5[0]);
               break;
            case 2:
               var4.set(var5[0], var5[1]);
               break;
            case 3:
               var4.set(var5[0], var5[1], var5[2]);
               break;
            case 4:
               var4.set(var5[0], var5[1], var5[2], var5[3]);
         }
      }
   }

   public RenderTarget getTempTarget(String name) {
      return (RenderTarget)this.customRenderTargets.get(name);
   }

   public void addTempTarget(String name, int width, int height) {
      RenderTarget var4 = new RenderTarget(width, height, true);
      var4.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.customRenderTargets.put(name, var4);
      if (width == this.screenWidth && height == this.screenHeight) {
         this.fullSizedTargets.add(var4);
      }
   }

   public void close() {
      for(RenderTarget var2 : this.customRenderTargets.values()) {
         var2.destroyBuffers();
      }

      for(PostPass var4 : this.passes) {
         var4.close();
      }

      this.passes.clear();
   }

   public PostPass addPass(String name, RenderTarget inTarget, RenderTarget outTarget) {
      PostPass var4 = new PostPass(this.resourceManager, name, inTarget, outTarget);
      this.passes.add(this.passes.size(), var4);
      return var4;
   }

   private void updateOrthoMatrix() {
      this.shaderOrthoMatrix = new Matrix4f();
      this.shaderOrthoMatrix.setIdentity();
      this.shaderOrthoMatrix.m00 = 2.0F / (float)this.screenTarget.width;
      this.shaderOrthoMatrix.m11 = 2.0F / (float)(-this.screenTarget.height);
      this.shaderOrthoMatrix.m22 = -0.0020001999F;
      this.shaderOrthoMatrix.m33 = 1.0F;
      this.shaderOrthoMatrix.m03 = -1.0F;
      this.shaderOrthoMatrix.m13 = 1.0F;
      this.shaderOrthoMatrix.m23 = -1.0001999F;
   }

   public void resize(int width, int height) {
      this.screenWidth = this.screenTarget.width;
      this.screenHeight = this.screenTarget.height;
      this.updateOrthoMatrix();

      for(PostPass var4 : this.passes) {
         var4.setOrthoMatrix(this.shaderOrthoMatrix);
      }

      for(RenderTarget var6 : this.fullSizedTargets) {
         var6.resize(width, height);
      }
   }

   public void process(float tickDelta) {
      if (tickDelta < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += tickDelta;
      } else {
         this.time += tickDelta - this.lastStamp;
      }

      this.lastStamp = tickDelta;

      while(this.time > 20.0F) {
         this.time -= 20.0F;
      }

      for(PostPass var3 : this.passes) {
         var3.process(this.time / 20.0F);
      }
   }

   public final String getName() {
      return this.name;
   }

   private RenderTarget getRenderTarget(String name) {
      if (name == null) {
         return null;
      } else {
         return name.equals("minecraft:main") ? this.screenTarget : (RenderTarget)this.customRenderTargets.get(name);
      }
   }
}
