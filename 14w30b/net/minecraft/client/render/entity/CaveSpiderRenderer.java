package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.living.mob.hostile.CaveSpiderEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CaveSpiderRenderer extends SpiderRenderer {
   private static final Identifier CAVE_SPIDER_TEXTURE = new Identifier("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize *= 0.7F;
   }

   protected void scale(CaveSpiderEntity c_73ntfgvru, float f) {
      GlStateManager.scalef(0.7F, 0.7F, 0.7F);
   }

   protected Identifier getTexture(CaveSpiderEntity c_73ntfgvru) {
      return CAVE_SPIDER_TEXTURE;
   }
}
