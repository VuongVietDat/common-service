package vn.com.atomi.loyalty.base.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author haidv
 * @version 1.0
 */
@Component
public class AESProvider {

  private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
  private static final String SPLIT_KEY = "";
  private static final int IV_SIZE = 128;
  private static final int IV_LENGTH = IV_SIZE / 4;
  protected final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
  private final int keySize = 256;
  private DataTypeEnum dataType = DataTypeEnum.BASE64;
  private Cipher cipher;
  private int saltLength;

  public AESProvider() {
    try {
      cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      saltLength = this.keySize / 4;
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private static byte[] generateRandom(int length) {
    var random = new SecureRandom();
    var randomBytes = new byte[length];
    random.nextBytes(randomBytes);
    return randomBytes;
  }

  private static byte[] fromBase64(String str) {
    return DatatypeConverter.parseBase64Binary(str);
  }

  private static String toBase64(byte[] ba) {
    return DatatypeConverter.printBase64Binary(ba);
  }

  private static byte[] fromHex(String str) {
    return DatatypeConverter.parseHexBinary(str);
  }

  private static String toHex(byte[] ba) {
    return DatatypeConverter.printHexBinary(ba);
  }

  public String encrypt(String salt, String iv, String passPhrase, String plainText)
      throws Exception {
    var key = generateKey(salt, passPhrase);
    var encrypted =
        doFinal(Cipher.ENCRYPT_MODE, key, iv, plainText.getBytes(StandardCharsets.UTF_8));
    String cipherText;
    if (dataType.equals(DataTypeEnum.HEX)) {
      cipherText = toHex(encrypted);
    } else {
      cipherText = toBase64(encrypted);
    }
    return cipherText;
  }

  public String encrypt(String passphrase, String plainText) throws Exception {
    var salt = toHex(generateRandom(keySize / 8));
    var iv = toHex(generateRandom(IV_SIZE / 8));
    var cipherText = encrypt(salt, iv, passphrase, plainText);
    return salt + SPLIT_KEY + iv + SPLIT_KEY + cipherText;
  }

  public String decrypt(String salt, String iv, String passPhrase, String cipherText)
      throws Exception {
    var key = generateKey(salt, passPhrase);
    byte[] encrypted;
    if (dataType.equals(DataTypeEnum.HEX)) {
      encrypted = fromHex(cipherText);
    } else {
      encrypted = fromBase64(cipherText);
    }
    var decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, encrypted);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  public String decrypt(String passPhrase, String cipherText) throws Exception {
    var salt = cipherText.substring(0, saltLength);
    var iv = cipherText.substring(saltLength, saltLength + IV_LENGTH);
    var ct = cipherText.substring(saltLength + IV_LENGTH);
    return decrypt(salt, iv, passPhrase, ct);
  }

  private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) throws Exception {
    cipher.init(encryptMode, key, new IvParameterSpec(fromHex(iv)));
    return cipher.doFinal(bytes);
  }

  private SecretKey generateKey(String salt, String passphrase) throws Exception {
    var factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
    var iterationCount = 1989;
    var spec = new PBEKeySpec(passphrase.toCharArray(), fromHex(salt), iterationCount, keySize);
    return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
  }

  public DataTypeEnum getDataType() {
    return dataType;
  }

  public void setDataType(DataTypeEnum dataType) {
    this.dataType = dataType;
  }

  public enum DataTypeEnum {
    HEX,
    BASE64
  }
}
