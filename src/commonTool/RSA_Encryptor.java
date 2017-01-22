package commonTool;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class RSA_Encryptor {
	public static final BigInteger SHORT_POWER = new BigInteger("17");// 用短公开指数加速加密，就是e=17
	private static final String NOT_PRIME = "Number is not prime number";
	private static final int BIT_LENGTH = 1024;
	private static final int CERTAINTY = 200;
	// 所以要不要成员变量???

	private static SecureRandom random = new SecureRandom("Powered by L' 201301110216".getBytes());//

	public static BigInteger genaratePrime() {// 给外面的直接调用生成
		return BigInteger.probablePrime(BIT_LENGTH, random);
	}

	public static BigInteger getPublicKeyInteger(BigInteger a, BigInteger b) throws Exception {
		if (!isTwoPrime(a, b))
			throw new Exception("This two is not prime number");
		return a.multiply(b);
	}// 就是计算n,这个方法不用于公开

	public static RSAPublicKey getPublicKey(BigInteger a, BigInteger b) throws Exception {
		BigInteger publicK = getPublicKeyInteger(a, b);// 公钥模数n
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(publicK, SHORT_POWER);
		KeyFactory keyFactory = KeyFactory.getInstance(PublicString.RSA_ALGORITHM);
		return (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
	}// 就是计算n

	public static String getPublicKeyString(BigInteger a, BigInteger b) throws Exception {// 通通Base64
		// return StringProcessor.rsaPublicKeyToBase64(getPublicKey(a, b));
		return StringProcessor.objectToBase64(getPublicKey(a, b));
	}// 就是计算n

	public static RSAPublicKey getPublicKeyFromBase64(String str) throws Exception {
		return (RSAPublicKey) StringProcessor.byteToObject(Base64.decode(str));
	}

	public static BigInteger getPrivateKeyInteger(BigInteger primeA, BigInteger primeB) throws Exception {
		if (!isTwoPrime(primeA, primeB))
			throw new Exception(NOT_PRIME);
		return SHORT_POWER.modInverse(initEulerValue(primeA, primeB));// 注意这里出现过BigInteger
																		// not
																		// invertible的异常
	}// 这个不用于公开

	public static RSAPrivateKey getPrivateKey(BigInteger a, BigInteger b) throws Exception {
		RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(getPublicKeyInteger(a, b), getPrivateKeyInteger(a, b));
		KeyFactory keyFactory = KeyFactory.getInstance(PublicString.RSA_ALGORITHM);
		return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
	}

	public static String getPrivateKeyString(BigInteger a, BigInteger b) throws Exception {
		// return StringProcessor.rsaPrivateKeyToBase64(getPrivateKey(a, b));
		return StringProcessor.objectToBase64(getPrivateKey(a, b));
	}

	public static String Encrypt(String msg, String pubKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		RSAPublicKey publicKey = (RSAPublicKey) StringProcessor.base64ToObject(pubKey);// StringProcessor.base64ToRSAPublicKey(pubKey);
		return Encrypt(msg, publicKey);
	}

	public static String Encrypt(String msg, RSAPublicKey publicKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		Cipher c = Cipher.getInstance(PublicString.RSA_ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encode(c.doFinal(msg.getBytes("UTF-8")));
	}// 真正从publicKey来处理

	public static String Encrypt(String msg, RSAPrivateKey privateKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		Cipher c = Cipher.getInstance(PublicString.RSA_ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, privateKey);
		return Base64.encode(c.doFinal(msg.getBytes("UTF-8")));
	}// 仅用于签名

	public static String Encrypt(String msg, BigInteger a, BigInteger b) throws Exception {
		if (!a.isProbablePrime(CERTAINTY) || !b.isProbablePrime(CERTAINTY))
			return PublicString.ENCRYPTION_FAIL;
		return Encrypt(msg, getPublicKey(a, b));
	}

	public static BigInteger Encrypt(BigInteger msg, BigInteger primeA, BigInteger primeB) throws Exception {
		if (!isTwoPrime(primeA, primeB))
			throw new Exception(NOT_PRIME);
		BigInteger publicK = getPublicKeyInteger(primeA, primeB);
		return msg.pow(SHORT_POWER.intValue()).mod(publicK);
	}// 这个方法没问题了

	public static String Decrypt(String msg, String prvKey) throws Exception {
		return Decrypt(msg, (RSAPrivateKey) StringProcessor.base64ToObject(prvKey));
	}

	public static String Decrypt(String msg, RSAPrivateKey prvKey) throws Exception {
		Cipher c = Cipher.getInstance(PublicString.RSA_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, prvKey);
		return new String(c.doFinal(Base64.decode(msg)), "UTF-8");
	}// 真正直接从privateKey处理

	public static String Decrypt(String msg, RSAPublicKey pubKey) throws Exception {
		Cipher c = Cipher.getInstance(PublicString.RSA_ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, pubKey);
		return new String(c.doFinal(Base64.decode(msg)), "UTF-8");
	}// 仅用于验证签名

	public static String Decrypt(String msg, BigInteger primeA, BigInteger primeB) throws Exception {
		if (!primeA.isProbablePrime(CERTAINTY) || !primeB.isProbablePrime(CERTAINTY))
			return PublicString.ENCRYPTION_FAIL;
		return Decrypt(msg, getPrivateKey(primeA, primeB));
	}

	public static BigInteger Decrypt(BigInteger msg, BigInteger primeA, BigInteger primeB) throws Exception {
		BigInteger priKey = getPrivateKeyInteger(primeA, primeB);
		BigInteger pubKey = getPublicKeyInteger(primeA, primeB);
		return msg.pow(priKey.intValue()).mod(pubKey);
	}// 这个方法理论没问题了，但是需要明文密文都在限定范围，即Msg<n

	public static boolean isTwoPrime(BigInteger a, BigInteger b) {
		return a.isProbablePrime(CERTAINTY) && b.isProbablePrime(CERTAINTY);
	}

	private static BigInteger initEulerValue(BigInteger a, BigInteger b) {
		return a.add(BigInteger.ONE.negate()).multiply(b.add(BigInteger.ONE.negate()));
	}

	public static String intToBinary(BigInteger bigInt, int radix) {
		return new BigInteger(1, bigInt.toByteArray()).toString(radix);// 这里的1代表正数
	}

	public static BigInteger squareMultiply(BigInteger power, BigInteger base, BigInteger modBase) {
		BigInteger result = base;
		String powerBinString = intToBinary(power, 2);
		System.out.println(powerBinString);
		// 还有点问题
		for (int i = powerBinString.length() - 2; i >= 0; i--) {
			result = result.multiply(result);
			result = result.mod(modBase);
			if (powerBinString.charAt(i) == '1') {
				result = result.multiply(base);
				result = result.mod(modBase);
			}
		}
		return result.mod(modBase);
	}

}
