package main;

public class UnitTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			String origin = "memeda";
//			BigInteger p=RSA_Encryptor.genaratePrime();
//			BigInteger q=RSA_Encryptor.genaratePrime();
//			RSAPublicKey pubKey = RSA_Encryptor.getPublicKey(p, q);
//			RSAPrivateKey prvKey = RSA_Encryptor.getPrivateKey(p, q);
//			String pubKeyStr=StringProcessor.objectToBase64(pubKey);
//			String prvKeyStr=StringProcessor.objectToBase64(prvKey);
//			System.out.println("PublicKey is: "+pubKeyStr);
//			System.out.println("PrivateKey is: "+prvKeyStr);
//			String lockmsg = RSA_Encryptor.Encrypt(origin, pubKeyStr);
//			System.out.println("Encrypt message is: "+lockmsg);
//			System.out.println("Decrypt msg is: "+RSA_Encryptor.Decrypt(lockmsg, prvKeyStr));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		Layout_Main gui = new Layout_Main(0, 0, 594, 420);
		gui.setVisible(true);
		
	}

}
