package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ProjectileRenderer extends EntityRenderer {
   private final Item item;
   private final int metadata;
   private ItemRenderer f_90juftvaa;

   public ProjectileRenderer(EntityRenderDispatcher c_28wsgstbh, Item c_30vndvelc, int i, ItemRenderer c_60kdcdvri) {
      super(c_28wsgstbh);
      this.item = c_30vndvelc;
      this.metadata = i;
      this.f_90juftvaa = c_60kdcdvri;
   }

   public ProjectileRenderer(EntityRenderDispatcher item, Item c_30vndvelc, ItemRenderer c_60kdcdvri) {
      this(item, c_30vndvelc, 0, c_60kdcdvri);
   }

   @Override
   public void render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)dx, (float)dy, (float)dz);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      this.f_90juftvaa.renderHeldItem(new ItemStack(this.item, 1, this.metadata));
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(entity, dx, dy, dz, yaw, tickDelta);
   }

   @Override
   protected Identifier getTexture(Entity entity) {
      return SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS;
   }
}
