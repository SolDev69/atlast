package net.minecraft.network.encryption;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptionUtils {
   private static final Logger LOGGER = LogManager.getLogger();

   @Environment(EnvType.CLIENT)
   public static SecretKey generateKey() {
      try {
         KeyGenerator var0 = KeyGenerator.getInstance("AES");
         var0.init(128);
         return var0.generateKey();
      } catch (NoSuchAlgorithmException var1) {
         throw new Error(var1);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
         var0.initialize(1024);
         return var0.generateKeyPair();
      } catch (NoSuchAlgorithmException var1) {
         var1.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
      try {
         return hash("SHA-1", baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
      } catch (UnsupportedEncodingException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static byte[] hash(String algorithm, byte[]... data) {
      try {
         MessageDigest var2 = MessageDigest.getInstance(algorithm);

         for(byte[] var6 : data) {
            var2.update(var6);
         }

         return var2.digest();
      } catch (NoSuchAlgorithmException var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public static PublicKey reconstitutePublicKey(byte[] data) {
      try {
         X509EncodedKeySpec var1 = new X509EncodedKeySpec(data);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         return var2.generatePublic(var1);
      } catch (NoSuchAlgorithmException var3) {
      } catch (InvalidKeySpecException var4) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) {
      return new SecretKeySpec(decrypt(privateKey, encryptedSecretKey), "AES");
   }

   @Environment(EnvType.CLIENT)
   public static byte[] encrypt(Key key, byte[] data) {
      return crypt(1, key, data);
   }

   public static byte[] decrypt(Key key, byte[] data) {
      return crypt(2, key, data);
   }

   private static byte[] crypt(int opMode, Key key, byte[] data) {
      try {
         return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
      } catch (IllegalBlockSizeException var4) {
         var4.printStackTrace();
      } catch (BadPaddingException var5) {
         var5.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   private static Cipher crypt(int opMode, String algorithm, Key key) {
      try {
         Cipher var3 = Cipher.getInstance(algorithm);
         var3.init(opMode, key);
         return var3;
      } catch (InvalidKeyException var4) {
         var4.printStackTrace();
      } catch (NoSuchAlgorithmException var5) {
         var5.printStackTrace();
      } catch (NoSuchPaddingException var6) {
         var6.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher crypt(int opMode, Key key) {
      try {
         Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
         var2.init(opMode, key, new IvParameterSpec(key.getEncoded()));
         return var2;
      } catch (GeneralSecurityException var3) {
         throw new RuntimeException(var3);
      }
   }
}
