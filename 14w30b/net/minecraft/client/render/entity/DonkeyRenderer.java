package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.entity.HorseModel;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DonkeyRenderer extends MobRenderer {
   private static final Map MODEL_IDENTIFIERS = Maps.newHashMap();
   private static final Identifier WHITE_TEXTURE = new Identifier("textures/entity/horse/horse_white.png");
   private static final Identifier MULE_TEXTURE = new Identifier("textures/entity/horse/mule.png");
   private static final Identifier DONKEY_TEXTURE = new Identifier("textures/entity/horse/donkey.png");
   private static final Identifier ZOMBIE_TEXTURE = new Identifier("textures/entity/horse/horse_zombie.png");
   private static final Identifier SKELETON_TEXTURE = new Identifier("textures/entity/horse/horse_skeleton.png");

   public DonkeyRenderer(EntityRenderDispatcher c_28wsgstbh, HorseModel c_93jybqltj, float f) {
      super(c_28wsgstbh, c_93jybqltj, f);
   }

   protected void scale(HorseBaseEntity c_32ekkasvh, float f) {
      float var3 = 1.0F;
      int var4 = c_32ekkasvh.getType();
      if (var4 == 1) {
         var3 *= 0.87F;
      } else if (var4 == 2) {
         var3 *= 0.92F;
      }

      GlStateManager.scalef(var3, var3, var3);
      super.scale(c_32ekkasvh, f);
   }

   protected Identifier getTexture(HorseBaseEntity c_32ekkasvh) {
      if (!c_32ekkasvh.hasArmor()) {
         switch(c_32ekkasvh.getType()) {
            case 0:
            default:
               return WHITE_TEXTURE;
            case 1:
               return DONKEY_TEXTURE;
            case 2:
               return MULE_TEXTURE;
            case 3:
               return ZOMBIE_TEXTURE;
            case 4:
               return SKELETON_TEXTURE;
         }
      } else {
         return this.getHorseIdentifier(c_32ekkasvh);
      }
   }

   private Identifier getHorseIdentifier(HorseBaseEntity horse) {
      String var2 = horse.getHorseName();
      if (!horse.m_42hzgygyd()) {
         return null;
      } else {
         Identifier var3 = (Identifier)MODEL_IDENTIFIERS.get(var2);
         if (var3 == null) {
            var3 = new Identifier(var2);
            MinecraftClient.getInstance().getTextureManager().register(var3, new LayeredTexture(horse.getHorseData()));
            MODEL_IDENTIFIERS.put(var2, var3);
         }

         return var3;
      }
   }
}
