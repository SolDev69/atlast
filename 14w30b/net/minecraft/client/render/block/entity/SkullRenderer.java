package net.minecraft.client.render.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.render.model.block.entity.HumanoidSkullModel;
import net.minecraft.client.render.model.block.entity.SkullModel;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkullRenderer extends BlockEntityRenderer {
   private static final Identifier SKELETON = new Identifier("textures/entity/skeleton/skeleton.png");
   private static final Identifier WITHER_SKELETON = new Identifier("textures/entity/skeleton/wither_skeleton.png");
   private static final Identifier ZOMBIE = new Identifier("textures/entity/zombie/zombie.png");
   private static final Identifier CREEPER = new Identifier("textures/entity/creeper/creeper.png");
   public static SkullRenderer instance;
   private final SkullModel model = new SkullModel(0, 0, 64, 32);
   private final SkullModel humanoidModel = new HumanoidSkullModel();

   public void render(SkullBlockEntity c_23redzvjz, double d, double e, double f, float g, int i) {
      Direction var10 = Direction.byId(c_23redzvjz.getCachedMetadata() & 7);
      this.render((float)d, (float)e, (float)f, var10, (float)(c_23redzvjz.getRotation() * 360) / 16.0F, c_23redzvjz.getType(), c_23redzvjz.getProfile(), i);
   }

   @Override
   public void init(BlockEntityRenderDispatcher dispatcher) {
      super.init(dispatcher);
      instance = this;
   }

   public void render(float x, float y, float z, Direction facing, float rotation, int skullType, GameProfile profile, int blockMiningProgress) {
      SkullModel var9 = this.model;
      if (blockMiningProgress >= 0) {
         this.bindTexture(MINING_PROGRESS_TEXTURES[blockMiningProgress]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch(skullType) {
            case 0:
            default:
               this.bindTexture(SKELETON);
               break;
            case 1:
               this.bindTexture(WITHER_SKELETON);
               break;
            case 2:
               this.bindTexture(ZOMBIE);
               var9 = this.humanoidModel;
               break;
            case 3:
               var9 = this.humanoidModel;
               Identifier var10 = ClientPlayerEntity.STEVE_TEXTURE;
               if (profile != null) {
                  MinecraftClient var11 = MinecraftClient.getInstance();
                  Map var12 = var11.getSkinManager().getTextures(profile);
                  if (var12.containsKey(Type.SKIN)) {
                     var10 = var11.getSkinManager().register((MinecraftProfileTexture)var12.get(Type.SKIN), Type.SKIN);
                  }
               }

               this.bindTexture(var10);
               break;
            case 4:
               this.bindTexture(CREEPER);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (facing != Direction.UP) {
         switch(facing) {
            case NORTH:
               GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.74F);
               break;
            case SOUTH:
               GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.26F);
               rotation = 180.0F;
               break;
            case WEST:
               GlStateManager.translatef(x + 0.74F, y + 0.25F, z + 0.5F);
               rotation = 270.0F;
               break;
            case EAST:
            default:
               GlStateManager.translatef(x + 0.26F, y + 0.25F, z + 0.5F);
               rotation = 90.0F;
         }
      } else {
         GlStateManager.translatef(x + 0.5F, y, z + 0.5F);
      }

      float var13 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      var9.render(null, 0.0F, 0.0F, 0.0F, rotation, 0.0F, var13);
      GlStateManager.popMatrix();
      if (blockMiningProgress >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
