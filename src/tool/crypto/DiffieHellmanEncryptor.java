package tool.crypto;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;

import tool.PublicString;
import tool.common.StringProcessor;

public class DiffieHellmanEncryptor {
	private static final String LOCAL_ALGORITHM = PublicString.AES_ALGORITHM;

	public static DHPublicKey getPublicKey(BigInteger y, BigInteger p, BigInteger g) throws Exception {
		// Constructor that takes a public value y, a prime modulus p,
		// and a base generator g.
		DHPublicKeySpec keySpec = new DHPublicKeySpec(y, p, g);
		KeyFactory factory = KeyFactory.getInstance("DH");
		return (DHPublicKey) factory.generatePublic(keySpec);// 可能是不是直接改成PublicKey会好一点
	}
	
	public static String getPublicKeyString(BigInteger y, BigInteger p, BigInteger g) throws Exception {
		return StringProcessor.objectToBase64(getPublicKey(y, p, g));
	}
	
	public static String getPrivateKeyString(BigInteger x, BigInteger p, BigInteger g) throws Exception {
		return StringProcessor.objectToBase64(getPrivateKey(x, p, g));
	}

	public static DHPrivateKey getPrivateKey(BigInteger x, BigInteger p, BigInteger g) throws Exception {
		// Constructor that takes a private value x, a prime modulus p, and a
		// base generator g.
		DHPrivateKeySpec keySpec = new DHPrivateKeySpec(x, p, g);
		KeyFactory factory = KeyFactory.getInstance("DH");
		return (DHPrivateKey) factory.generatePrivate(keySpec);
	}

	public static Key getLocalKey(Key pubKey, Key prvKey) throws Exception {
		// key - the key for this phase. For example, in the case of
		// Diffie-Hellman between 2 parties, this would be the other party's
		// Diffie-Hellman public key.
		KeyAgreement agreement = KeyAgreement.getInstance(PublicString.DH_ALGORITHM);
		agreement.init((Key)prvKey);// key - the party's private information. For
								// example, in the case of the Diffie-Hellman
								// key agreement, this would be the party's own
								// Diffie-Hellman private key.
		agreement.doPhase((Key)pubKey, true);
		return agreement.generateSecret(LOCAL_ALGORITHM);
	}

}
