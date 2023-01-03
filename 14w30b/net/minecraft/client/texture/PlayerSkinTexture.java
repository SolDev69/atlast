package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PlayerSkinTexture extends ResourceTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger threadIdCounter = new AtomicInteger(0);
   private final File f_31xzebiac;
   private final String url;
   private final BufferedImageSkinProvider skinProvider;
   private BufferedImage image;
   private Thread downloadThread;
   private boolean isUploaded;

   public PlayerSkinTexture(File url, String id, Identifier skinProvider, BufferedImageSkinProvider c_11jpbalew) {
      super(skinProvider);
      this.f_31xzebiac = url;
      this.url = id;
      this.skinProvider = c_11jpbalew;
   }

   private void upload() {
      if (!this.isUploaded) {
         if (this.image != null) {
            if (this.id != null) {
               this.clearGlId();
            }

            TextureUtil.uploadTexture(super.getGlId(), this.image);
            this.isUploaded = true;
         }
      }
   }

   @Override
   public int getGlId() {
      this.upload();
      return super.getGlId();
   }

   public void setImage(BufferedImage image) {
      this.image = image;
      if (this.skinProvider != null) {
         this.skinProvider.onTextureDownloaded();
      }
   }

   @Override
   public void load(IResourceManager resourceManager) {
      if (this.image == null && this.id != null) {
         super.load(resourceManager);
      }

      if (this.downloadThread == null) {
         if (this.f_31xzebiac != null && this.f_31xzebiac.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", new Object[]{this.f_31xzebiac});

            try {
               this.image = ImageIO.read(this.f_31xzebiac);
               if (this.skinProvider != null) {
                  this.setImage(this.skinProvider.process(this.image));
               }
            } catch (IOException var3) {
               LOGGER.error("Couldn't load skin " + this.f_31xzebiac, var3);
               this.m_99szfmzkj();
            }
         } else {
            this.m_99szfmzkj();
         }
      }
   }

   protected void m_99szfmzkj() {
      this.downloadThread = new Thread("Texture Downloader #" + threadIdCounter.incrementAndGet()) {
         @Override
         public void run() {
            HttpURLConnection var1 = null;
            PlayerSkinTexture.LOGGER
               .debug("Downloading http texture from {} to {}", new Object[]{PlayerSkinTexture.this.url, PlayerSkinTexture.this.f_31xzebiac});

            try {
               var1 = (HttpURLConnection)new URL(PlayerSkinTexture.this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
               var1.setDoInput(true);
               var1.setDoOutput(false);
               var1.connect();
               if (var1.getResponseCode() / 100 == 2) {
                  BufferedImage var2;
                  if (PlayerSkinTexture.this.f_31xzebiac != null) {
                     FileUtils.copyInputStreamToFile(var1.getInputStream(), PlayerSkinTexture.this.f_31xzebiac);
                     var2 = ImageIO.read(PlayerSkinTexture.this.f_31xzebiac);
                  } else {
                     var2 = TextureUtil.readImage(var1.getInputStream());
                  }

                  if (PlayerSkinTexture.this.skinProvider != null) {
                     var2 = PlayerSkinTexture.this.skinProvider.process(var2);
                  }

                  PlayerSkinTexture.this.setImage(var2);
                  return;
               }
            } catch (Exception var6) {
               PlayerSkinTexture.LOGGER.error("Couldn't download http texture", var6);
               return;
            } finally {
               if (var1 != null) {
                  var1.disconnect();
               }
            }
         }
      };
      this.downloadThread.setDaemon(true);
      this.downloadThread.start();
   }
}
