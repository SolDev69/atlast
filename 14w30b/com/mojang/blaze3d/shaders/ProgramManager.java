package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.Effect;
import net.minecraft.server.ChainedJsonException;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ProgramManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ProgramManager instance;

   public static void createInstance() {
      instance = new ProgramManager();
   }

   public static ProgramManager getInstance() {
      return instance;
   }

   private ProgramManager() {
   }

   public void releaseProgram(Effect effect) {
      effect.getFragmentProgram().close(effect);
      effect.getVertexProgram().close(effect);
      GLX.deleteProgram(effect.getId());
   }

   public int createProgram() {
      int var1 = GLX.createProgram();
      if (var1 <= 0) {
         throw new ChainedJsonException("Could not create shader program (returned program ID " + var1 + ")");
      } else {
         return var1;
      }
   }

   public void linkProgram(Effect effect) {
      effect.getFragmentProgram().attachToEffect(effect);
      effect.getVertexProgram().attachToEffect(effect);
      GLX.linkProgram(effect.getId());
      int var2 = GLX.getProgram(effect.getId(), GLX.GL_LINK_STATUS);
      if (var2 == 0) {
         LOGGER.warn(
            "Error encountered when linking program containing VS "
               + effect.getVertexProgram().getName()
               + " and FS "
               + effect.getFragmentProgram().getName()
               + ". Log output:"
         );
         LOGGER.warn(GLX.getProgramInfoLog(effect.getId(), 32768));
      }
   }
}
