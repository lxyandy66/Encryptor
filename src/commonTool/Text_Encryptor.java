package commonTool;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Text_Encryptor {
	public static final boolean ENCRYPT_MODE = true;
	public static final boolean DECRYPT_MODE = false;
	public static final int DES_ENCRYPT = 0;
	public static final int DESede_ENCRYPT = 1;
	public static final int AES_ENCRYPT = 2;
	public static final int BASE64_ENCODE = 3;
	public static final int CAESAR_ENCRYPT = 4;
	public static final int CHAO_ENCRYPT = 5;
	public static final int AFFINE_ENCRYPT = 6;
	private static final BigInteger ENCRYPT_SPACE = new BigInteger("32767");

	private static void chaoDelta(BigDecimal Signal, char[] chao) {
		BigDecimal x = Signal, y;
		for (int i = 0; i < chao.length; i++) {
			y = BigDecimal.ONE.add(x.multiply(x).multiply(new BigDecimal("-2"))); // 通过logistic混沌迭代方程y=1-2x^2,产生混沌序列
			chao[i] = (char) Math.ceil((y.doubleValue() * Math.pow(10, 5)));// 混沌序列赋值作为增量数组,并且控制增量大小
			x = y;
		}
	}// 加密函数

	public static String textProcess(String msg, boolean isEncrypt, int method, String key) throws Exception {
		char[] result = msg.toCharArray();
		BigInteger keyInt;
		switch (method) {
		case AFFINE_ENCRYPT:
			throw new Exception("参数错误！");
		case CAESAR_ENCRYPT:
			keyInt = new BigInteger(key);
			for (int i = 0; i < result.length; i++)
				result[i] = (char) (isEncrypt ? result[i] + keyInt.intValue() : result[i] - keyInt.intValue());
			break;
		case CHAO_ENCRYPT:
			keyInt = new BigInteger(key);
			char[] delta = new char[msg.length()];
			BigDecimal signal = new BigDecimal(key).divide(new BigDecimal(Math.pow(10, key.toString().length())));
			chaoDelta(signal, delta);
			for (int i = 0; i < result.length; i++)
				result[i] = (char) (isEncrypt ? result[i] + delta[i] : result[i] - delta[i]);
			break;
		case DES_ENCRYPT:
			return Encryptor.encrypt_Process(msg, key, isEncrypt, PublicString.DES_ALGORITHM);
		case DESede_ENCRYPT:
			return Encryptor.encrypt_Process(msg, key, isEncrypt, PublicString.DESede_ALGORITHM);
		case AES_ENCRYPT:
			return Encryptor.encrypt_Process(msg, key, isEncrypt, PublicString.AES_ALGORITHM);
		case BASE64_ENCODE:
			if (isEncrypt)
				return Base64.encode(msg.getBytes("UTF-8"));
			else
				return new String(Base64.decode(msg), "UTF-8");
		default:
			throw new Exception("加密方法参数错误！");
		}
		return new String(result);
	}

	public static String textProcess(String msg, boolean isEncrypt, int method, String aStr, BigInteger b)
			throws Exception {
		if (method != AFFINE_ENCRYPT)
			return textProcess(msg, isEncrypt, method, aStr);
		BigInteger a = new BigInteger(aStr);
		char[] result = msg.toCharArray();
		for (int i = 0; i < result.length; i++)
			result[i] = (char) (isEncrypt ? (result[i] * a.intValue() + b.intValue())
					: ((a.modInverse(ENCRYPT_SPACE).intValue() * (result[i] - b.intValue()))//公式不能错啊
							% ENCRYPT_SPACE.intValue()));
		return new String(result);
	}

	public static boolean isLegalString(String s) {
		return s.matches("[a-zA-Z0-9]*");
	}
}
