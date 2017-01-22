package commonTool;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class HashProcessor {

	public static final String[] method={"MD5","SHA-1","SHA-256"};
	public static final int MD5=0;
	public static final int SHA1=1;
	public static final int SHA256=2;
	
	/**
	 * 计算文件的Hash值
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String getHash(File file, int methodSelect) throws Exception {
		if(!isLegalArg(methodSelect))
			throw new Exception("方法参数错误!");
		MessageDigest md = MessageDigest.getInstance(method[methodSelect]);
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int length = -1;
		while ((length = fis.read(buffer)) != -1) {
			md.update(buffer, 0, length);
		}
		fis.close();
		return StringProcessor.bytesToHex(md.digest());
	}

	/**
	 * 计算字符串的Hash值
	 * 
	 * @param str method
	 * @return
	 * @throws Exception
	 */
	public static String getHash(String str, int methodSelect) throws Exception {
		if(!isLegalArg(methodSelect))
			throw new Exception("方法参数错误!");
		byte[] bt = str.getBytes();
		MessageDigest md = MessageDigest.getInstance(method[methodSelect]);
		md.update(bt);
		return StringProcessor.bytesToHex(md.digest()); 
	}
	
	private static boolean isLegalArg(int methodSelect){
		return (methodSelect>=0&&methodSelect<=method.length-1);
			
	}

}
