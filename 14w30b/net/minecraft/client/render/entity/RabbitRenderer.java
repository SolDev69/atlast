package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.RabbitEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RabbitRenderer extends MobRenderer {
   private static final Identifier f_47hzoodvl = new Identifier("textures/entity/rabbit/brown.png");
   private static final Identifier f_70guagpgn = new Identifier("textures/entity/rabbit/white.png");
   private static final Identifier f_05vydbqmm = new Identifier("textures/entity/rabbit/black.png");
   private static final Identifier f_06vsydzlu = new Identifier("textures/entity/rabbit/gold.png");
   private static final Identifier f_29sztvtbt = new Identifier("textures/entity/rabbit/salt.png");
   private static final Identifier f_13sqncaeg = new Identifier("textures/entity/rabbit/white_splotched.png");
   private static final Identifier f_15teibnpp = new Identifier("textures/entity/rabbit/toast.png");
   private static final Identifier f_36inpouhv = new Identifier("textures/entity/rabbit/caerbannog.png");

   public RabbitRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected Identifier getTexture(RabbitEntity c_35uygbulw) {
      String var2 = Formatting.strip(c_35uygbulw.getName());
      if (var2 != null && var2.equals("Toast")) {
         return f_15teibnpp;
      } else {
         switch(c_35uygbulw.m_14jsvwagf()) {
            case 0:
            default:
               return f_47hzoodvl;
            case 1:
               return f_70guagpgn;
            case 2:
               return f_05vydbqmm;
            case 3:
               return f_13sqncaeg;
            case 4:
               return f_06vsydzlu;
            case 5:
               return f_29sztvtbt;
            case 99:
               return f_36inpouhv;
         }
      }
   }
}
