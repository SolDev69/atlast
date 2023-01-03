package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ModelPart {
   public float textureWidth = 64.0F;
   public float textureHeight = 32.0F;
   private int textureU;
   private int textureV;
   public float pivotX;
   public float pivotY;
   public float pivotZ;
   public float rotationX;
   public float rotationY;
   public float rotationZ;
   private boolean compiled;
   private int renderCallList;
   public boolean flipped;
   public boolean visible = true;
   public boolean invisible;
   public List boxes = Lists.newArrayList();
   public List children;
   public final String id;
   private Model model;
   public float translateX;
   public float translateY;
   public float translateZ;

   public ModelPart(Model model, String id) {
      this.model = model;
      model.parts.add(this);
      this.id = id;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public ModelPart(Model model) {
      this(model, null);
   }

   public ModelPart(Model model, int textureU, int textureV) {
      this(model);
      this.setTextureCoords(textureU, textureV);
   }

   public void addChild(ModelPart part) {
      if (this.children == null) {
         this.children = Lists.newArrayList();
      }

      this.children.add(part);
   }

   public ModelPart setTextureCoords(int textureU, int textureV) {
      this.textureU = textureU;
      this.textureV = textureV;
      return this;
   }

   public ModelPart addBox(String id, float x, float y, float z, int sizeX, int sizeY, int sizeZ) {
      id = this.id + "." + id;
      TexturePos var8 = this.model.getTexturePos(id);
      this.setTextureCoords(var8.u, var8.v);
      this.boxes.add(new Box(this, this.textureU, this.textureV, x, y, z, sizeX, sizeY, sizeZ, 0.0F).setId(id));
      return this;
   }

   public ModelPart addBox(float x, float y, float z, int sizeX, int sizeY, int sizeZ) {
      this.boxes.add(new Box(this, this.textureU, this.textureV, x, y, z, sizeX, sizeY, sizeZ, 0.0F));
      return this;
   }

   public ModelPart addBox(float x, float y, float z, int sizeX, int sizeY, int sizeZ, boolean flipped) {
      this.boxes.add(new Box(this, this.textureU, this.textureV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, flipped));
      return this;
   }

   public void addBox(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float increase) {
      this.boxes.add(new Box(this, this.textureU, this.textureV, x, y, z, sizeX, sizeY, sizeZ, increase));
   }

   public void setPivot(float x, float y, float z) {
      this.pivotX = x;
      this.pivotY = y;
      this.pivotZ = z;
   }

   public void render(float scale) {
      if (!this.invisible) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(scale);
            }

            GlStateManager.translatef(this.translateX, this.translateY, this.translateZ);
            if (this.rotationX != 0.0F || this.rotationY != 0.0F || this.rotationZ != 0.0F) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
               if (this.rotationZ != 0.0F) {
                  GlStateManager.rotatef(this.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotationY != 0.0F) {
                  GlStateManager.rotatef(this.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotationX != 0.0F) {
                  GlStateManager.rotatef(this.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.renderCallList);
               if (this.children != null) {
                  for(int var4 = 0; var4 < this.children.size(); ++var4) {
                     ((ModelPart)this.children.get(var4)).render(scale);
                  }
               }

               GlStateManager.popMatrix();
            } else if (this.pivotX == 0.0F && this.pivotY == 0.0F && this.pivotZ == 0.0F) {
               GlStateManager.callList(this.renderCallList);
               if (this.children != null) {
                  for(int var3 = 0; var3 < this.children.size(); ++var3) {
                     ((ModelPart)this.children.get(var3)).render(scale);
                  }
               }
            } else {
               GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
               GlStateManager.callList(this.renderCallList);
               if (this.children != null) {
                  for(int var2 = 0; var2 < this.children.size(); ++var2) {
                     ((ModelPart)this.children.get(var2)).render(scale);
                  }
               }

               GlStateManager.translatef(-this.pivotX * scale, -this.pivotY * scale, -this.pivotZ * scale);
            }

            GlStateManager.translatef(-this.translateX, -this.translateY, -this.translateZ);
         }
      }
   }

   public void renderRotation(float scale) {
      if (!this.invisible) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
            if (this.rotationY != 0.0F) {
               GlStateManager.rotatef(this.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotationX != 0.0F) {
               GlStateManager.rotatef(this.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotationZ != 0.0F) {
               GlStateManager.rotatef(this.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.renderCallList);
            GlStateManager.popMatrix();
         }
      }
   }

   public void translate(float scale) {
      if (!this.invisible) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(scale);
            }

            if (this.rotationX != 0.0F || this.rotationY != 0.0F || this.rotationZ != 0.0F) {
               GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
               if (this.rotationZ != 0.0F) {
                  GlStateManager.rotatef(this.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotationY != 0.0F) {
                  GlStateManager.rotatef(this.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotationX != 0.0F) {
                  GlStateManager.rotatef(this.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }
            } else if (this.pivotX != 0.0F || this.pivotY != 0.0F || this.pivotZ != 0.0F) {
               GlStateManager.translatef(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
            }
         }
      }
   }

   private void compile(float scale) {
      this.renderCallList = MemoryTracker.getLists(1);
      GL11.glNewList(this.renderCallList, 4864);
      BufferBuilder var2 = Tessellator.getInstance().getBufferBuilder();

      for(int var3 = 0; var3 < this.boxes.size(); ++var3) {
         ((Box)this.boxes.get(var3)).render(var2, scale);
      }

      GL11.glEndList();
      this.compiled = true;
   }

   public ModelPart setTextureSize(int width, int height) {
      this.textureWidth = (float)width;
      this.textureHeight = (float)height;
      return this;
   }
}
