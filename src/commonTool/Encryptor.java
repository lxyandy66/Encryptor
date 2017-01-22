package commonTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Encryptor {
	public static final boolean ENCRYPT_MODE = true;
	public static final boolean DECRYPT_MODE = false;
	private static final int AES_KEYLENGTH = 16;
	private static final int DESede_KEYLENGTH = 24;
	// private Key cryptoKey;

	// 或者，可以从一组固定的原始数据（也许是由口令或者随机击键产生的）来生成一个密钥，这时可以使用如下的SecretKeyFactory

	private static SecretKey getKey(String key, String algorithm) throws Exception {
		System.out.println("Algorithm selected as: " + algorithm);
		if (algorithm.equals(PublicString.DES_ALGORITHM)) {
			SecretKeyFactory generator = SecretKeyFactory.getInstance(
					algorithm);/** AES SecretKeyFactory not available **/
			return generator.generateSecret(new DESKeySpec(processKeyToByte(algorithm, key.getBytes())));
		} else
			return new SecretKeySpec(processKeyToByte(algorithm, key.getBytes()), algorithm);// 看网上说这样就行了？？？？
		// 这两种有啥区别?????
	}// *********然而这个可以但是密钥必须8的倍数，

	private static SecretKey getKey(File file, String algorithm) throws Exception {
		System.out.println("Algorithm selected as: " + algorithm);
		if (algorithm.equals(PublicString.DES_ALGORITHM)) {
			SecretKeyFactory generator = SecretKeyFactory.getInstance(
					algorithm);/** AES SecretKeyFactory not available **/
			return generator
					.generateSecret(new DESKeySpec(processKeyToByte(algorithm, FileOperator.readFileToByte(file))));
		} else
			return new SecretKeySpec(processKeyToByte(algorithm, FileOperator.readFileToByte(file)), algorithm);// 看网上说这样就行了？？？？
	}

	// 或者，可以从一组固定的原始数据（也许是由口令或者随机击键产生的）来生成一个密钥，这时可以使用如下的SecretKeyFactory

	// private void getKey(String str_key) throws GeneralSecurityException {
	// KeyGenerator generator = KeyGenerator.getInstance("DES");
	// generator.init(new SecureRandom(str_key.getBytes()));
	// this.cryptoKey= generator.generateKey();
	// // 下面的不行怀疑是随机数的原因每次产生的key都不一样，还有待测试
	// //因为每次key生成都不一样导致解密加密用的密钥都不一样
	// }

	// 静态非静态哪种好????

	private static byte[] processKeyToByte(String algorithm, byte[] originKey) {// 用来处理输入长度和原始输入密钥长度不同的问题
		if (algorithm.equals(PublicString.AES_ALGORITHM)) {// AES use 128bit
			return getModifyByte(AES_KEYLENGTH, originKey);
		} else if (algorithm.equals(PublicString.DES_ALGORITHM)) {
			return getModifyByte(originKey.length + (8 - originKey.length % 8), originKey);
		} else if (algorithm.equals(PublicString.DESede_ALGORITHM)) {
			return getModifyByte(DESede_KEYLENGTH, originKey);
		} else
			return originKey;
	}

	private static byte[] getModifyByte(int length, byte[] origin) {
		byte[] result = new byte[length];
		if (origin.length >= length) {
			for (int i = 0; i < result.length; i++)
				result[i] = origin[i];
			return result;
		} else {
			int i = 0;
			for (; i < origin.length; i++)
				result[i] = origin[i];
			for (; i < result.length; i++)
				result[i] = 0;// 缺位补0，有没有问题?????如果不截取，用hash值会不会更好？？？
			return result;
		}
	}

	// 或者，可以从一组固定的原始数据（也许是由口令或者随机击键产生的）来生成一个密钥，这时可以使用如下的SecretKeyFactory

	// private void getKey(String str_key) throws GeneralSecurityException {
	// KeyGenerator generator = KeyGenerator.getInstance("DES");
	// generator.init(new SecureRandom(str_key.getBytes()));
	// this.cryptoKey= generator.generateKey();
	// // 下面的不行怀疑是随机数的原因每次产生的key都不一样，还有待测试
	// //因为每次key生成都不一样导致解密加密用的密钥都不一样
	// }

	// 静态非静态哪种好????

	public static boolean encrypt_Process(File in, String key, boolean isEncryption, String algorithm)
			throws Exception {
		if (in == null || !in.isFile() || !in.canRead())
			throw new Exception("File is Wrong!");// 文件异常咯
		Cipher c = Cipher.getInstance(algorithm);
		byte[] buffer = new byte[1024];
		int i = 0;
		FileInputStream fis = new FileInputStream(in);
		if (isEncryption == Encryptor.ENCRYPT_MODE) {
			c.init(Cipher.ENCRYPT_MODE, getKey(key, algorithm));// cryptoKey);这样不行看网上说不能是成员变量要是local变量
			FileOutputStream fos = new FileOutputStream(new File(in.getAbsolutePath() + ".ecy"));
			CipherInputStream cis = new CipherInputStream(fis, c);
			while ((i = cis.read(buffer)) > 0) {
				fos.write(buffer, 0, i);// ??????这里有点点不明白
			}
			cis.close();
			fos.close();
		} else if (isEncryption == Encryptor.DECRYPT_MODE) {
			c.init(Cipher.DECRYPT_MODE, getKey(key, algorithm));
			FileOutputStream fos = new FileOutputStream(new File(in.getAbsolutePath() + ".dcy"));
			CipherOutputStream cos = new CipherOutputStream(fos, c);
			while ((i = fis.read(buffer)) >= 0) {
				System.out.println();
				cos.write(buffer, 0, i);
			}
			cos.close();
			fos.close();
		} else {
			fis.close();
			throw new Exception("Arg is Wrong");
		}
		fis.close();

		return true;
	}

	public static boolean encrypt_Process(File in, File key, boolean isEncryption, String algorithm) throws Exception {
		if (in == null || !in.isFile() || !in.canRead())
			throw new Exception("File is Wrong!");// 文件异常咯
		Cipher c = Cipher.getInstance(algorithm);
		byte[] buffer = new byte[1024];
		int i = 0;
		FileInputStream fis = new FileInputStream(in);
		if (isEncryption == Encryptor.ENCRYPT_MODE) {
			c.init(Cipher.ENCRYPT_MODE, getKey(key, algorithm));// cryptoKey);这样不行看网上说不能是成员变量要是local变量
			FileOutputStream fos = new FileOutputStream(new File(in.getAbsolutePath() + ".ecy"));
			CipherInputStream cis = new CipherInputStream(fis, c);
			while ((i = cis.read(buffer)) > 0) {
				fos.write(buffer, 0, i);// ??????这里有点点不明白
			}
			cis.close();
			fos.close();
		} else if (isEncryption == Encryptor.DECRYPT_MODE) {
			c.init(Cipher.DECRYPT_MODE, getKey(key, algorithm));
			FileOutputStream fos = new FileOutputStream(new File(in.getAbsolutePath() + ".dcy"));
			CipherOutputStream cos = new CipherOutputStream(fos, c);
			while ((i = fis.read(buffer)) >= 0) {
				System.out.println();
				cos.write(buffer, 0, i);
			}
			cos.close();
			fos.close();
		} else {
			fis.close();
			throw new Exception("Arg is Wrong");
		}
		fis.close();

		return true;
	}

	// 处理字符串的加密

	public static boolean encrypt_Process(File[] file_input, File file_key, boolean isEncrypt, String cryptoAlgorithm) throws Exception{
		// TODO Auto-generated method stub
		if(file_input==null)
			throw new NullPointerException("输入的文件是空的");
		for(File temp:file_input)
			encrypt_Process(temp, file_key, isEncrypt, cryptoAlgorithm);
		return true;
	}
	public static boolean encrypt_Process(File[] file_input, String key, boolean isEncrypt, String cryptoAlgorithm) throws Exception{
		// TODO Auto-generated method stub
		if(file_input==null)
			throw new NullPointerException("输入的文件是空的");
		for(File temp:file_input)
			encrypt_Process(temp, key, isEncrypt, cryptoAlgorithm);
		return true;
	}

	public static String encrypt_Process(String in, String key, boolean isEncryption, String algorithm)
			throws Exception {
		if (in == null || in.trim().equals("") || key == null || key.trim().equals(""))
			throw new Exception("输入异常");
		Cipher c = Cipher.getInstance(algorithm);
		if (isEncryption == Encryptor.ENCRYPT_MODE) {
			c.init(Cipher.ENCRYPT_MODE, getKey(key, algorithm));
			return new String(Base64.encode(c.doFinal(in.getBytes("UTF-8"))));
		} else if (isEncryption == Encryptor.DECRYPT_MODE) {
			c.init(Cipher.DECRYPT_MODE, getKey(key, algorithm));
			return new String(c.doFinal(Base64.decode(in)), "UTF-8");
		} else
			throw new Exception("加密选项异常");

	}

	public static String encrypt_Process(String in, File key, boolean isEncryption, String algorithm) throws Exception {
		if (in == null || in.trim().equals("") || key == null || !key.isFile() || !key.canRead())
			throw new Exception("输入异常");// 默认解密时为二进制
		Cipher c = Cipher.getInstance(algorithm);
		if (isEncryption == Encryptor.ENCRYPT_MODE) {
			c.init(Cipher.ENCRYPT_MODE, getKey(key, algorithm));
			return new String(Base64.encode(c.doFinal(in.getBytes("UTF-8"))));
		} else if (isEncryption == Encryptor.DECRYPT_MODE) {
			c.init(Cipher.DECRYPT_MODE, getKey(key, algorithm));
			return new String(c.doFinal(Base64.decode(in)), "UTF-8");
		} else
			throw new Exception("加密选项异常");
	}
}
