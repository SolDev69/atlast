package net.minecraft.client.entity.living.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.render.DownloadedSkinParser;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import net.minecraft.text.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ClientPlayerEntity extends PlayerEntity {
   public static final Identifier STEVE_TEXTURE = new Identifier("textures/entity/steve.png");
   private PlayerInfo player;

   public ClientPlayerEntity(World c_54ruxjwzt, GameProfile gameProfile) {
      super(c_54ruxjwzt, gameProfile);
   }

   @Override
   public boolean isSpectator() {
      PlayerInfo var1 = MinecraftClient.getInstance().getNetworkHandler().getOnlinePlayer(this.getGameProfile().getId());
      return var1 != null && var1.getGameMode() == WorldSettings.GameMode.SPECTATOR;
   }

   public boolean hasInfo() {
      return this.getInfo() != null;
   }

   protected PlayerInfo getInfo() {
      if (this.player == null) {
         this.player = MinecraftClient.getInstance().getNetworkHandler().getOnlinePlayer(this.getUuid());
      }

      return this.player;
   }

   public boolean hasTextures() {
      PlayerInfo var1 = this.getInfo();
      return var1 != null && var1.hasSkinTexture();
   }

   public Identifier getSkinTexture() {
      PlayerInfo var1 = this.getInfo();
      return var1 == null ? STEVE_TEXTURE : var1.getSkinTexture();
   }

   public Identifier getCapeTexture() {
      PlayerInfo var1 = this.getInfo();
      return var1 == null ? null : var1.getCapeTexture();
   }

   public static PlayerSkinTexture registerSkinTexture(Identifier id, String playerName) {
      TextureManager var2 = MinecraftClient.getInstance().getTextureManager();
      Object var3 = var2.getTexture(id);
      if (var3 == null) {
         var3 = new PlayerSkinTexture(
            null,
            String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripFormatting(playerName)),
            STEVE_TEXTURE,
            new DownloadedSkinParser()
         );
         var2.register(id, (Texture)var3);
      }

      return (PlayerSkinTexture)var3;
   }

   public static Identifier getSkinTextureId(String playerName) {
      return new Identifier("skins/" + StringUtils.stripFormatting(playerName));
   }

   public String getModelType() {
      PlayerInfo var1 = this.getInfo();
      return var1 == null ? "default" : var1.getModelType();
   }

   public float getFovModifier() {
      float var1 = 1.0F;
      if (this.abilities.flying) {
         var1 *= 1.1F;
      }

      IEntityAttributeInstance var2 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
      var1 = (float)((double)var1 * ((var2.get() / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
      if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      if (this.isHoldingItem() && this.getItemInHand().getItem() == Items.BOW) {
         int var3 = this.getHeldItemCooldown();
         float var4 = (float)var3 / 20.0F;
         if (var4 > 1.0F) {
            var4 = 1.0F;
         } else {
            var4 *= var4;
         }

         var1 *= 1.0F - var4 * 0.15F;
      }

      return var1;
   }
}
